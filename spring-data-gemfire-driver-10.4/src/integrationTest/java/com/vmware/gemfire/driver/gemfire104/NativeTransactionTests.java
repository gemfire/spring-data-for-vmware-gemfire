/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.*;
import com.vmware.gemfire.driver.spi.*;
import org.apache.geode.cache.*;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import java.io.*;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Timeout(60)
class NativeTransactionTests {
	@TempDir Path directory;
	private NativeServerFixture server;
	@BeforeEach void startServer() throws Exception { server = new NativeServerFixture(directory); }
	@AfterEach void stopServer() throws Exception { if (server != null) server.close(); }
	private ClientCache cache() { return new ClientCacheFactory().set("log-level", "error").addPoolServer("localhost", server.port).create(); }
	private DriverConnection connection(ClientCache cache) { return new GemFireConnection(cache); }
	private DriverRegion<String, String> region(DriverConnection connection) {
		return connection.createClientRegion("entries", com.vmware.gemfire.driver.spi.config.ClientRegionShortcut.CACHING_PROXY,
			RegionConfiguration.<String, String>builder().serverRegionName("distributed").build());
	}

	private void capture(List<Throwable> errors, Runnable assertions) {
		try { assertions.run(); } catch (Throwable failure) { errors.add(failure); }
	}

	@Test void clientCommitAndRollbackPreserveEntryIdsWithoutTransactionListenerNotifications() {
		try (var cache = cache()) {
			var connection = connection(cache); var region = region(connection); var manager = connection.getTransactionManager();
			List<String> outcomes = new ArrayList<>(); List<DriverTransactionId> entryIds = new ArrayList<>();
			region.addCacheListener(new DriverCacheListenerAdapter<>() {
				@Override public void afterCreate(DriverEntryEvent<String, String> event) { entryIds.add(event.getTransactionId()); }
			});
			manager.addListener(new DriverTransactionListenerAdapter() {
				@Override public void afterCommit(DriverTransactionEvent event) { outcomes.add("commit"); }
				@Override public void afterRollback(DriverTransactionEvent event) { outcomes.add("rollback"); }
			});

			manager.begin(); var committed = manager.getTransactionId(); region.put("commit", "value"); manager.commit();
			assertThat(region.get("commit")).isEqualTo("value"); assertThat(manager.exists()).isFalse();
			manager.begin(); var rolledBack = manager.getTransactionId(); region.put("rollback", "value"); manager.rollback();
			assertThat(region.get("rollback")).isNull(); assertThat(outcomes).isEmpty();
			assertThat(entryIds).containsExactly(committed); assertThat(rolledBack).isNotEqualTo(committed);
			assertThat(committed.getMemberId().getId()).isEqualTo(cache.getDistributedSystem().getDistributedMember().getId());
		}
	}
	@Test void realConcurrentWriteConflictReportsFailureWithoutApplyingTheStagedValue() throws Exception {
		try (var cache = cache()) {
			var connection = connection(cache); var region = region(connection); var manager = connection.getTransactionManager();
			List<Throwable> callbackErrors = new ArrayList<>();
			AtomicInteger failures = new AtomicInteger();
			manager.addListener(new DriverTransactionListenerAdapter() {
				@Override public void afterFailedCommit(DriverTransactionEvent event) { failures.incrementAndGet(); capture(callbackErrors, () -> assertThat(event.getEvents()).hasSize(1)); }
			});
			region.put("key", "original"); manager.begin(); region.put("key", "staged"); var id = manager.suspend();
			ExecutorService executor = Executors.newSingleThreadExecutor();
			try { executor.submit(() -> region.put("key", "concurrent")).get(10, TimeUnit.SECONDS); }
			finally { executor.shutdownNow(); assertThat(executor.awaitTermination(10, TimeUnit.SECONDS)).isTrue(); }
			manager.resume(id);
			assertThatThrownBy(manager::commit).isInstanceOfSatisfying(DriverOperationException.class, ex -> assertThat(ex.kind()).isEqualTo(DriverFailureKind.COMMIT_CONFLICT));
			assertThat(manager.exists()).isFalse(); assertThat(manager.exists(id)).isFalse();
			assertThat(callbackErrors).isEmpty(); assertThat(failures).hasValue(0); assertThat(region.get("key")).isEqualTo("concurrent");
		}
	}
	@Test void suspensionMovesThreadAssociationAndTimedResumeWaitsForRelease() throws Exception {
		try (var cache = cache()) {
			var connection = connection(cache); var region = region(connection); var manager = connection.getTransactionManager();
			manager.begin(); region.put("key", "first"); var id = manager.suspend();
			assertThat(manager.isSuspended(id)).isTrue(); assertThat(manager.exists(id)).isTrue(); assertThat(manager.exists()).isFalse();
			CountDownLatch associated = new CountDownLatch(1), release = new CountDownLatch(1), waiting = new CountDownLatch(1);
			ExecutorService workers = Executors.newFixedThreadPool(2);
			try {
				Future<?> owner = workers.submit(() -> {
					manager.resume(id); associated.countDown();
					try { assertThat(release.await(10, TimeUnit.SECONDS)).isTrue(); }
					catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new AssertionError(e); }
					region.put("key", "second"); assertThat(manager.suspend()).isEqualTo(id);
				});
				assertThat(associated.await(10, TimeUnit.SECONDS)).isTrue();
				assertThat(manager.tryResume(id)).isFalse(); assertThat(manager.tryResume(id, 0, TimeUnit.NANOSECONDS)).isFalse();
				Future<?> waiter = workers.submit(() -> { waiting.countDown(); assertThat(manager.tryResume(id, 10, TimeUnit.SECONDS)).isTrue(); manager.commit(); });
				assertThat(waiting.await(10, TimeUnit.SECONDS)).isTrue(); release.countDown();
				owner.get(15, TimeUnit.SECONDS); waiter.get(15, TimeUnit.SECONDS);
				assertThat(region.get("key")).isEqualTo("second"); assertThat(manager.exists(id)).isFalse(); assertThat(manager.tryResume(id)).isFalse();
			} finally { release.countDown(); workers.shutdownNow(); assertThat(workers.awaitTermination(10, TimeUnit.SECONDS)).isTrue(); }
		}
	}
	@Test void externalizedIdentityRoundTripsThroughTheExactNativeFormatAndCanResume() throws Exception {
		try (var cache = cache()) {
			var manager = connection(cache).getTransactionManager(); manager.begin(); var id = manager.suspend();
			var bytes = new ByteArrayOutputStream(); try (var output = new ObjectOutputStream(bytes)) { manager.writeTransactionId(id, output); }
			DriverTransactionId restored; try (var input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) { restored = manager.readTransactionId(input); }
			assertThat(restored).isEqualTo(id).hasSameHashCodeAs(id); assertThat(restored.getMemberId()).isEqualTo(id.getMemberId());
			manager.resume(restored); manager.rollback();
			var nativeId = new org.apache.geode.internal.cache.TXId();
			try (var input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) { nativeId.readExternal(input); }
			assertThat(nativeId).isEqualTo(((GemFireTransactionId) id).value);
			var bad = new ByteArrayOutputStream(); new ObjectOutputStream(bad).close();
			try (var input = new ObjectInputStream(new ByteArrayInputStream(bad.toByteArray()))) { assertThatThrownBy(() -> manager.readTransactionId(input)).isInstanceOf(IOException.class); }
			DriverTransactionId foreign = () -> null;
			assertThatThrownBy(() -> manager.resume(foreign)).isInstanceOfSatisfying(DriverOperationException.class, ex -> assertThat(ex.kind()).isEqualTo(DriverFailureKind.INVALID_ARGUMENT));
		}
	}
	@Test void listenerRegistrationIdentityDuplicatesReplacementAndCleanupFollowTheNativeManager() {
		try (var cache = cache()) {
			var manager = connection(cache).getTransactionManager(); AtomicInteger firstClosed = new AtomicInteger(), secondClosed = new AtomicInteger();
			var first = new DriverTransactionListenerAdapter() { @Override public void close() { firstClosed.incrementAndGet(); } };
			var second = new DriverTransactionListenerAdapter() { @Override public void close() { secondClosed.incrementAndGet(); } };
			manager.addListener(first); manager.addListener(first); assertThat(manager.getListeners()).containsExactly(first);
			assertThat(manager.setListener(second)).isSameAs(first); assertThat(firstClosed).hasValue(1);
			manager.addListener(first); assertThatThrownBy(manager::getListener).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> manager.setListener(null)).isInstanceOf(DriverOperationException.class);
			manager.initListeners(new DriverTransactionListener[] {first, first, second});
			assertThat(manager.getListeners()).containsExactly(first, first, second);
			manager.removeListener(first); assertThat(manager.getListeners()).containsExactly(first, second);
			manager.initListeners(null); assertThat(manager.getListeners()).isEmpty();
			assertThat(firstClosed).hasValue(4); assertThat(secondClosed).hasValue(2);
			assertThatThrownBy(() -> manager.addListener(null)).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> manager.initListeners(new DriverTransactionListener[] {first, null})).isInstanceOf(DriverOperationException.class);
		}
	}
	@Test void listenerInitializationHooksAreAdaptedWithoutExtraLifecycleCalls() {
		try (var cache = cache()) {
			var connection = connection(cache); var manager = connection.getTransactionManager(); List<String> calls = new ArrayList<>(); Properties properties = new Properties();
			var listener = new DriverTransactionListenerAdapter() {
				@Override public void init(Properties p) { assertThat(p).isSameAs(properties); calls.add("listener.init"); }
				@Override public void initialize(DriverConnection c, Properties p) { assertThat(c.getTransactionManager()).isNotNull(); calls.add("listener.initialize"); }
				@Override public void close() { calls.add("listener.close"); }
			};
			manager.addListener(listener); assertThat(calls).isEmpty();
			var callback = cache.getCacheTransactionManager().getListener();
			callback.init(properties); callback.initialize((Cache) cache, properties);
			manager.removeListener(listener);
			assertThat(calls).containsExactly("listener.init", "listener.initialize", "listener.close");
		}
	}
	@Test void listenersAlreadyInstalledInTheClientRoundTripWithoutLosingIdentity() throws Exception {
		try (var cache = cache()) {
			var connection = connection(cache); var manager = connection.getTransactionManager(); List<String> calls = new ArrayList<>();
			var nativeListener = new org.apache.geode.cache.util.TransactionListenerAdapter() {
				@Override public void afterCommit(TransactionEvent event) { calls.add("commit"); }
				@Override public void afterFailedCommit(TransactionEvent event) { calls.add("failed"); }
				@Override public void afterRollback(TransactionEvent event) { calls.add("rollback"); }
				@Override public void init(Properties properties) { calls.add("init"); }
				@Override public void initialize(Cache actual, Properties properties) { assertThat(actual).isSameAs(cache); calls.add("initialize"); }
				@Override public void close() { calls.add("close"); }
			};
			cache.getCacheTransactionManager().addListener(nativeListener);
			var listener = manager.getListener();
			assertThat(listener).isEqualTo(manager.getListener()).hasSameHashCodeAs(manager.getListener());
			manager.addListener(listener);
			assertThat(cache.getCacheTransactionManager().getListeners()).containsExactly(nativeListener);
			manager.begin(); var nativeId = cache.getCacheTransactionManager().getTransactionId();
			TransactionEvent nativeEvent = new TransactionEvent() {
				@Override public TransactionId getTransactionId() { return nativeId; }
				@Override public Cache getCache() { return (Cache) cache; }
				@Override public List<CacheEvent<?, ?>> getEvents() { return List.of(); }
			};
			var event = new GemFireTransactionEvent(nativeEvent);
			listener.afterCommit(event); listener.afterFailedCommit(event); listener.afterRollback(event);
			listener.init(new Properties()); listener.initialize(connection, new Properties());
			manager.removeListener(listener); manager.rollback();
			assertThat(calls).containsExactly("commit", "failed", "rollback", "init", "initialize", "close");
		}
	}

	@Test void registeredClientTransactionListenerIsSilentAndClosedOnCacheClose() {
		AtomicInteger closed = new AtomicInteger(), notified = new AtomicInteger();
		try (var cache = cache()) {
			var connection = connection(cache); var region = region(connection); var manager = connection.getTransactionManager();
			manager.addListener(new DriverTransactionListenerAdapter() {
				@Override public void afterCommit(DriverTransactionEvent event) { notified.incrementAndGet(); }
				@Override public void close() { closed.incrementAndGet(); }
			});
			manager.begin(); region.put("key", "committed"); manager.commit(); assertThat(region.get("key")).isEqualTo("committed");
		}
		assertThat(closed).hasValue(1); assertThat(notified).hasValue(0);
	}
	@Test void clientStateErrorsAndUnsupportedNativeTransactionModesAreExplicit() {
		try (var cache = cache()) {
			var connection = connection(cache); var region = region(connection);
			var manager = connection.getTransactionManager(); assertThat(manager.getTransactionId()).isNull(); assertThat(manager.suspend()).isNull();
			assertThatThrownBy(manager::commit).isInstanceOf(DriverOperationException.class); assertThatThrownBy(manager::rollback).isInstanceOf(DriverOperationException.class);
			manager.begin(); assertThatThrownBy(manager::begin).isInstanceOf(DriverOperationException.class); manager.rollback();
			// A successful native flag setter is not evidence of a supported client transaction mode.
			var nativeManager = cache.getCacheTransactionManager();
			nativeManager.setDistributed(true); nativeManager.begin(); region.put("distributed-mode", "value");
			assertThatThrownBy(nativeManager::commit).isInstanceOf(UnsupportedOperationInTransactionException.class);
			if (nativeManager.exists()) nativeManager.rollback();
			nativeManager.setDistributed(false);
		}
		try (var cache = new ClientCacheFactory().set("log-level", "error").create()) {
			assertThatThrownBy(() -> cache.getCacheTransactionManager().setWriter(null))
				.isInstanceOf(IllegalStateException.class).hasMessageContaining("cannot be registered on a client");
		}
	}
	@Test void incompleteSynchronizationAndGeneralFailuresRetainTheirDistinctClassifications() {
		List<RuntimeException> failures = List.of(new CommitIncompleteException("private"), new FailedSynchronizationException("private"),
			new SynchronizationCommitConflictException("private"), new TransactionException("private"));
		List<DriverFailureKind> kinds = List.of(DriverFailureKind.COMMIT_INCOMPLETE, DriverFailureKind.LOCK_FAILURE, DriverFailureKind.LOCK_FAILURE, DriverFailureKind.TRANSACTION_FAILURE);
		for (int i = 0; i < failures.size(); i++) {
			RuntimeException failure = failures.get(i); DriverFailureKind kind = kinds.get(i);
			var nativeManager = (CacheTransactionManager) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {CacheTransactionManager.class},
				(proxy, method, arguments) -> { throw failure; });
			var manager = new GemFireTransactionManager(nativeManager);
			assertThatThrownBy(manager::commit).isInstanceOfSatisfying(DriverOperationException.class, ex -> {
				assertThat(ex.kind()).isEqualTo(kind); assertThat(ex.getCause()).isSameAs(failure); assertThat(ex.getMessage()).doesNotContain("private");
			});
		}
	}
}
