/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.*;
import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import org.apache.geode.cache.*;
import org.apache.geode.cache.client.*;
import org.apache.geode.distributed.Locator;
import com.vmware.gemfire.driver.spi.*;

@Timeout(120)
class NativeDistributedCallbackTests {
	@TempDir Path directory;

	@Test
	void realRemoteMembershipRoleChangesAndSerializedEntryEventsReachInjectedCallbacks() throws Exception {
		try (var locator = new Peer("locator"); var cache = new CacheFactory().set("mcast-port", "0")
			.set("locators", "localhost[" + locator.port + "]").set("member-timeout", "1000")
			.set("disable-auto-reconnect", "true").set("log-level", "error").create()) {
			var creates = new LinkedBlockingQueue<String>(); var departures = new LinkedBlockingQueue<String>();
			var crashes = new LinkedBlockingQueue<String>(); var gains = new LinkedBlockingQueue<Set<String>>();
			var losses = new LinkedBlockingQueue<Set<String>>(); var values = new LinkedBlockingQueue<String>();
			var errors = new LinkedBlockingQueue<Throwable>(); var initial = new ArrayList<Integer>();
			var listener = new DriverRegionRoleListenerAdapter<String, String>() {
				@Override public void initialMembers(DriverRegion<String, String> r, DriverMember[] members) { initial.add(members.length); }
				@Override public void afterRemoteRegionCreate(DriverRegionEvent<String, String> e) { creates.add(e.getDistributedMember().getId()); }
				@Override public void afterRemoteRegionDeparture(DriverRegionEvent<String, String> e) { departures.add(e.getDistributedMember().getId()); }
				@Override public void afterRemoteRegionCrash(DriverRegionEvent<String, String> e) { crashes.add(e.getDistributedMember().getId()); }
				@Override public void afterRoleGain(DriverRoleEvent<String, String> e) { gains.add(Set.copyOf(e.getRequiredRoles())); }
				@Override public void afterRoleLoss(DriverRoleEvent<String, String> e) { losses.add(Set.copyOf(e.getRequiredRoles())); }
				@Override public void afterCreate(DriverEntryEvent<String, String> e) { capture(e); }
				@Override public void afterUpdate(DriverEntryEvent<String, String> e) { capture(e); }
				private void capture(DriverEntryEvent<String, String> e) {
					try {
						assertThat(e.isOriginRemote()).isTrue();
						assertThat(e.getDistributedMember().getRoles()).extracting(DriverRole::getName).contains("worker");
						var serialized = e.getSerializedNewValue();
						assertThat(serialized).isNotNull();
						assertThat(serialized.getSerializedValue()).isNotEmpty();
						assertThat(serialized.getDeserializedValue()).isEqualTo(e.getNewValue());
						values.add(e.getKey() + ":" + e.getOldValue() + ":" + e.getNewValue());
					} catch (Throwable failure) { errors.add(failure); }
				}
			};
			cache.<String, String>createRegionFactory(RegionShortcut.REPLICATE)
				.setMembershipAttributes(new MembershipAttributes(new String[] { "worker" }, LossAction.FULL_ACCESS, ResumptionAction.NONE))
				.addCacheListener(new GemFireCacheListenerFactory().adapt(listener)).create("distributed");
			assertThat(initial).containsExactly(0);
			try (var peer = new Peer("localhost[" + locator.port + "]")) {
				String identity = creates.poll(20, TimeUnit.SECONDS); assertThat(identity).isNotNull();
				assertThat(gains.poll(20, TimeUnit.SECONDS)).contains("worker");
				peer.command("PUT");
				String first = values.poll(10, TimeUnit.SECONDS);
				assertThat(errors).isEmpty();
				assertThat(first).isEqualTo("key:null:one");
				assertThat(values.poll(10, TimeUnit.SECONDS)).isEqualTo("key:one:two");
				peer.command("CLOSE_REGION");
				assertThat(departures.poll(20, TimeUnit.SECONDS)).isEqualTo(identity);
				assertThat(losses.poll(20, TimeUnit.SECONDS)).contains("worker");
				peer.command("CREATE_REGION");
				assertThat(creates.poll(20, TimeUnit.SECONDS)).isEqualTo(identity);
				peer.crash();
				assertThat(crashes.poll(30, TimeUnit.SECONDS)).isEqualTo(identity);
			}
		}
	}

	@Test
	void subscribedClientReceivesRealRegionLiveMarker() throws Exception {
		try (var server = new Peer("server")) {
			var live = new CompletableFuture<DriverOperation>();
			try (var cache = new ClientCacheFactory().set("log-level", "error").set("durable-client-id", "callback-" + UUID.randomUUID())
				.addPoolServer("localhost", server.port).setPoolSubscriptionEnabled(true).create()) {
				var connection = new GemFireConnection(cache, CacheConfiguration.Mode.CLIENT);
				var listener = new DriverCacheListenerAdapter<String, String>() {
					@Override public void afterRegionLive(DriverRegionEvent<String, String> event) { live.complete(event.getOperation()); }
				};
				connection.createClientRegion("distributed", com.vmware.gemfire.driver.spi.config.ClientRegionShortcut.CACHING_PROXY,
					new RegionCallbacks<>(List.of(listener), null, null));
				cache.getRegion("distributed").registerInterestForAllKeys(InterestResultPolicy.KEYS_VALUES, true);
				cache.readyForEvents();
				assertThat(live.get(30, TimeUnit.SECONDS)).isEqualTo(DriverOperation.MARKER);
			}
		}
	}

	private class Peer implements AutoCloseable {
		private final Process process;
		private final ExecutorService reader = Executors.newSingleThreadExecutor();
		private final BlockingQueue<String> messages = new LinkedBlockingQueue<>();
		private final BufferedWriter input;
		final int port;
		Peer(String mode) throws Exception {
			process = new ProcessBuilder(Path.of(System.getProperty("java.home"), "bin", "java").toString(),
				"--add-exports=java.base/sun.nio.ch=ALL-UNNAMED", "--add-opens=java.base/java.lang=ALL-UNNAMED",
				"--add-opens=java.base/java.nio=ALL-UNNAMED", "-cp", System.getProperty("nativeFixtureClasspath"),
				CallbackPeerProcess.class.getName(), mode).directory(java.nio.file.Files.createTempDirectory(directory, "peer-").toFile()).redirectErrorStream(true).start();
			input = process.outputWriter();
			reader.submit(() -> {
				try (var output = process.inputReader()) {
					for (String line; (line = output.readLine()) != null;) if (line.startsWith("CALLBACK_")) messages.add(line);
				} catch (IOException failure) { messages.add("CALLBACK_FAILURE:" + failure); }
			});
			String ready = messages.poll(30, TimeUnit.SECONDS);
			if (ready == null || !ready.startsWith("CALLBACK_READY:")) {
				close(); throw new AssertionError("Peer did not become ready: " + ready);
			}
			port = Integer.parseInt(ready.substring("CALLBACK_READY:".length()));
		}
		void command(String command) throws Exception {
			input.write(command); input.newLine(); input.flush();
			assertThat(messages.poll(30, TimeUnit.SECONDS)).isEqualTo("CALLBACK_DONE:" + command);
		}
		void crash() throws Exception { process.destroyForcibly(); assertThat(process.waitFor(10, TimeUnit.SECONDS)).isTrue(); }
		@Override public void close() throws Exception {
			try {
				if (process.isAlive()) {
					input.write("STOP"); input.newLine(); input.flush();
					if (!process.waitFor(10, TimeUnit.SECONDS)) crash();
				}
			} finally { process.destroyForcibly(); reader.shutdownNow(); }
		}
	}
}
