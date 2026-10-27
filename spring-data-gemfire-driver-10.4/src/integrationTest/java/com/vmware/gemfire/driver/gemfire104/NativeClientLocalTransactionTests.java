/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverEntryEvent;
import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.DriverRegion;
import com.vmware.gemfire.driver.spi.DriverTransactionEvent;
import com.vmware.gemfire.driver.spi.DriverTransactionId;
import com.vmware.gemfire.driver.spi.DriverTransactionListenerAdapter;
import com.vmware.gemfire.driver.spi.config.ClientRegionShortcut;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/** Client-local transaction delivery is distinct from the server-backed client stub path. */
@Timeout(60)
class NativeClientLocalTransactionTests {

	private DriverConnection connection() {
		return new GemFireConnection(new ClientCacheFactory().set("log-level", "error").create());
	}

	@Test
	void localCommitDeliversTransactionIdentityCacheAndEntryPayload() {
		try (var connection = connection()) {
			DriverRegion<String, String> region = connection.createClientRegion("local", ClientRegionShortcut.LOCAL);
			var manager = connection.getTransactionManager();
			var listener = new RecordingListener();
			manager.addListener(listener);

			manager.begin();
			var id = manager.getTransactionId();
			region.put("key", "committed");
			manager.commit();

			assertThat(manager.exists()).isFalse();
			assertThat(region.get("key")).isEqualTo("committed");
			listener.assertOutcome("commit", id, List.of(new Entry("key", null, "committed")));
		}
	}

	@Test
	void localRollbackDeliversTheDiscardedEntryPayload() {
		try (var connection = connection()) {
			DriverRegion<String, String> region = connection.createClientRegion("local", ClientRegionShortcut.LOCAL);
			var manager = connection.getTransactionManager();
			var listener = new RecordingListener();
			manager.addListener(listener);
			region.put("key", "original");

			manager.begin();
			var id = manager.getTransactionId();
			region.put("key", "discarded");
			manager.rollback();

			assertThat(manager.exists()).isFalse();
			assertThat(region.get("key")).isEqualTo("original");
			listener.assertOutcome("rollback", id, List.of(new Entry("key", "original", "discarded")));
		}
	}

	@Test
	void emptyClientTransactionsDeliverCommitAndRollbackWithEmptyEvents() {
		try (var connection = connection()) {
			var manager = connection.getTransactionManager();
			var listener = new RecordingListener();
			manager.addListener(listener);

			manager.begin();
			var committed = manager.getTransactionId();
			manager.commit();
			manager.begin();
			var rolledBack = manager.getTransactionId();
			manager.rollback();

			assertThat(manager.exists()).isFalse();
			assertThat(listener.failures).isEmpty();
			assertThat(listener.outcomes).containsExactly(
				new Outcome("commit", committed, List.of()), new Outcome("rollback", rolledBack, List.of()));
		}
	}

	@Test
	void localWriteConflictDeliversFailedCommitAndPreservesTheConcurrentValue() throws Exception {
		try (var connection = connection()) {
			DriverRegion<String, String> region = connection.createClientRegion("local", ClientRegionShortcut.LOCAL);
			var manager = connection.getTransactionManager();
			var listener = new RecordingListener();
			manager.addListener(listener);
			region.put("key", "original");

			manager.begin();
			var id = manager.getTransactionId();
			region.put("key", "staged");
			assertThat(manager.suspend()).isEqualTo(id);
			var worker = Executors.newSingleThreadExecutor();
			try {
				worker.submit(() -> region.put("key", "concurrent")).get(10, TimeUnit.SECONDS);
			}
			finally {
				worker.shutdownNow();
				assertThat(worker.awaitTermination(10, TimeUnit.SECONDS)).isTrue();
			}
			manager.resume(id);

			assertThatThrownBy(manager::commit).isInstanceOfSatisfying(DriverOperationException.class,
				failure -> assertThat(failure.kind()).isEqualTo(DriverFailureKind.COMMIT_CONFLICT));
			assertThat(manager.exists()).isFalse();
			assertThat(manager.exists(id)).isFalse();
			assertThat(region.get("key")).isEqualTo("concurrent");
			listener.assertOutcome("failed", id, List.of(new Entry("key", "original", "staged")));
		}
	}

	@Test
	void clientLocalListenerFailureDoesNotUndoCommitOrSkipLaterListenersAndCleanup() {
		var closed = new AtomicInteger();
		try (var connection = connection()) {
			DriverRegion<String, String> region = connection.createClientRegion("local", ClientRegionShortcut.LOCAL);
			var manager = connection.getTransactionManager();
			var listener = new RecordingListener();
			manager.addListener(new DriverTransactionListenerAdapter() {
				@Override public void afterCommit(DriverTransactionEvent event) {
					throw new IllegalStateException("deliberate listener failure");
				}
				@Override public void close() { closed.incrementAndGet(); }
			});
			manager.addListener(listener);

			manager.begin();
			var id = manager.getTransactionId();
			region.put("key", "committed");
			manager.commit();

			assertThat(manager.exists()).isFalse();
			assertThat(region.get("key")).isEqualTo("committed");
			listener.assertOutcome("commit", id, List.of(new Entry("key", null, "committed")));
		}
		assertThat(closed).hasValue(1);
	}

	private record Entry(Object key, Object oldValue, Object newValue) { }
	private record Outcome(String kind, DriverTransactionId id, List<Entry> entries) { }

	private class RecordingListener extends DriverTransactionListenerAdapter {
		private final List<Outcome> outcomes = new ArrayList<>();
		private final List<Throwable> failures = new ArrayList<>();

		@Override public void afterCommit(DriverTransactionEvent event) { record("commit", event); }
		@Override public void afterFailedCommit(DriverTransactionEvent event) { record("failed", event); }
		@Override public void afterRollback(DriverTransactionEvent event) { record("rollback", event); }

		private void record(String kind, DriverTransactionEvent event) {
			// Native dispatch suppresses listener failures. Surface all assertions on the test thread.
			try {
				assertThat(event.getCache().isClosed()).isFalse();
				assertThat(event.getCache().getTransactionManager().exists()).isFalse();
				var id = event.getTransactionId();
				assertThat(id).isNotNull();
				assertThat(id.getMemberId().getId()).isNotBlank();
				List<Entry> entries = new ArrayList<>();
				for (var cacheEvent : event.getEvents()) {
					assertThat(cacheEvent).isInstanceOf(DriverEntryEvent.class);
					var entry = (DriverEntryEvent<?, ?>) cacheEvent;
					assertThat(entry.getTransactionId()).isEqualTo(id);
					assertThat(entry.getRegion().name()).isEqualTo("local");
					assertThat(event.getCache().getRegion("local").name()).isEqualTo("local");
					assertThat(entry.getDistributedMember()).isEqualTo(id.getMemberId());
					entries.add(new Entry(entry.getKey(), entry.getOldValue(), entry.getNewValue()));
				}
				outcomes.add(new Outcome(kind, id, List.copyOf(entries)));
			}
			catch (Throwable failure) { failures.add(failure); }
		}

		private void assertOutcome(String kind, DriverTransactionId id, List<Entry> entries) {
			assertThat(failures).isEmpty();
			assertThat(outcomes).containsExactly(new Outcome(kind, id, entries));
		}
	}
}
