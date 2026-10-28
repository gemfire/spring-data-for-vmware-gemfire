/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.client.ServerOperationException;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.FunctionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

/** Native client registry evidence; no public SPI hosting or registration contract is added. */
@Timeout(60)
class NativeClientFunctionRegistryTests {

	@TempDir Path directory;
	private NativeServerFixture server;

	@BeforeEach
	void startServer() throws Exception {
		server = new NativeServerFixture(directory);
	}

	@AfterEach
	void stopServer() throws Exception {
		if (server != null) server.close();
	}

	private ClientCache client() {
		return new ClientCacheFactory().set("log-level", "error")
			.addPoolServer("localhost", server.port).create();
	}

	@Test
	void regionUsesLocalMetadataWithoutTransmittingTheRegisteredBody() throws InterruptedException {
		try (var cache = client()) {
			var region = cache.createClientRegionFactory(ClientRegionShortcut.PROXY).create("distributed");
			assertRegistryBehavior(FunctionService.onRegion(region), FunctionException.class);
		}
	}

	@Test
	void singleServerCacheTargetUsesLocalMetadata() throws InterruptedException {
		try (var cache = client()) {
			assertRegistryBehavior(FunctionService.onServer(cache), ServerOperationException.class);
		}
	}

	@Test
	void singleServerPoolTargetUsesLocalMetadata() throws InterruptedException {
		try (var cache = client()) {
			assertRegistryBehavior(FunctionService.onServer(cache.getDefaultPool()), ServerOperationException.class);
		}
	}

	@Test
	void allServersCacheTargetUsesLocalMetadata() throws InterruptedException {
		try (var cache = client()) {
			assertRegistryBehavior(FunctionService.onServers(cache), ServerOperationException.class);
		}
	}

	@Test
	void allServersPoolTargetUsesLocalMetadata() throws InterruptedException {
		try (var cache = client()) {
			assertRegistryBehavior(FunctionService.onServers(cache.getDefaultPool()), ServerOperationException.class);
		}
	}

	@Test
	void clientDisconnectClearsLocalRegistrationButPreservesServerRegistration() throws InterruptedException {
		var local = new RegisteredFunction("client:", ProcessHandle.current().pid());
		String id = local.getId();
		assertThat(FunctionService.getFunction(id)).isNull();
		try {
			try (var cache = client()) {
				FunctionService.registerFunction(local);
				assertThat(FunctionService.getFunction(id)).isSameAs(local);
				assertThat(FunctionService.onServer(cache)
					.execute(new InstallServerFunction(local.clientProcessId), 10, TimeUnit.SECONDS)
					.getResult(10, TimeUnit.SECONDS)).isEqualTo(List.of("installed"));
			}

			assertThat(FunctionService.getFunction(id)).isNull();
			assertThat(FunctionService.isRegistered(id)).isFalse();
			assertThat(FunctionService.getRegisteredFunctions()).doesNotContainKey(id);
			try (var cache = client()) {
				assertThat(FunctionService.onServer(cache).setArguments("after-reconnect")
					.execute(id, 10, TimeUnit.SECONDS).getResult(10, TimeUnit.SECONDS))
					.isEqualTo(List.of("server:after-reconnect"));
				assertThat(FunctionService.getFunction(id)).isNull();
			}
		} finally {
			FunctionService.unregisterFunction(id);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void assertRegistryBehavior(Execution target, Class<? extends RuntimeException> missingFunctionFailure)
			throws InterruptedException {
		var local = new RegisteredFunction("client:", ProcessHandle.current().pid());
		String id = local.getId();
		assertThat(FunctionService.getFunction(id)).isNull();
		try {
			FunctionService.registerFunction(local);
			assertThat(FunctionService.getFunction(id)).isSameAs(local);
			assertThat(FunctionService.isRegistered(id)).isTrue();
			assertThat(FunctionService.getRegisteredFunctions()).containsEntry(id, local);

			Execution execution = target.setArguments("argument");
			// Local registration supplies metadata, but cannot install an execution body remotely.
			assertThatThrownBy(() -> execution.execute(id, 10, TimeUnit.SECONDS)
				.getResult(10, TimeUnit.SECONDS)).isInstanceOf(missingFunctionFailure).hasMessageContaining(id);

			// Install a different instance in the separate server process using object submission.
			assertThat(execution.execute(new InstallServerFunction(local.clientProcessId), 10, TimeUnit.SECONDS)
				.getResult(10, TimeUnit.SECONDS)).isEqualTo(List.of("installed"));
			local.resultMetadataReads = 0;
			local.haMetadataReads = 0;
			local.writeMetadataReads = 0;
			assertThat(execution.execute(id, 10, TimeUnit.SECONDS).getResult(10, TimeUnit.SECONDS))
				.isEqualTo(List.of("server:argument"));
			assertThat(local.resultMetadataReads).isPositive();
			assertThat(local.haMetadataReads).isPositive();
			assertThat(local.writeMetadataReads).isPositive();
			assertThat(execution.execute(local, 10, TimeUnit.SECONDS).getResult(10, TimeUnit.SECONDS))
				.isEqualTo(List.of("client:argument"));

			var snapshot = FunctionService.getRegisteredFunctions();
			var replacement = new RegisteredFunction("replacement:", local.clientProcessId);
			FunctionService.registerFunction(replacement);
			assertThat(FunctionService.getFunction(id)).isSameAs(replacement);
			assertThat(FunctionService.getRegisteredFunctions()).containsEntry(id, replacement);
			assertThat(snapshot).containsEntry(id, local);
			snapshot.remove(id);
			assertThat(FunctionService.getFunction(id)).isSameAs(replacement);

			local.resultMetadataReads = 0;
			local.haMetadataReads = 0;
			local.writeMetadataReads = 0;
			replacement.resultMetadataReads = 0;
			replacement.haMetadataReads = 0;
			replacement.writeMetadataReads = 0;
			// Reusing the executor must consult the replacement, but still execute the server's body.
			assertThat(execution.execute(id, 10, TimeUnit.SECONDS).getResult(10, TimeUnit.SECONDS))
				.isEqualTo(List.of("server:argument"));
			assertThat(replacement.resultMetadataReads).isPositive();
			assertThat(replacement.haMetadataReads).isPositive();
			assertThat(replacement.writeMetadataReads).isPositive();
			assertThat(local.resultMetadataReads).isZero();
			assertThat(local.haMetadataReads).isZero();
			assertThat(local.writeMetadataReads).isZero();
			assertThat(execution.execute(replacement, 10, TimeUnit.SECONDS).getResult(10, TimeUnit.SECONDS))
				.isEqualTo(List.of("replacement:argument"));

			FunctionService.unregisterFunction(id);
			assertThat(FunctionService.getFunction(id)).isNull();
			assertThat(FunctionService.isRegistered(id)).isFalse();
			assertThat(FunctionService.getRegisteredFunctions()).doesNotContainKey(id);
			// With no local metadata, ID invocation fetches attributes and executes the server's body.
			assertThat(execution.execute(id, 10, TimeUnit.SECONDS).getResult(10, TimeUnit.SECONDS))
				.isEqualTo(List.of("server:argument"));
			assertThat(FunctionService.getFunction(id)).isNull();
		} finally {
			FunctionService.unregisterFunction(id);
		}
	}

	public static final class RegisteredFunction implements Function<String> {

		private static final long serialVersionUID = 1L;
		private final String prefix;
		private final long clientProcessId;
		private transient int resultMetadataReads;
		private transient int haMetadataReads;
		private transient int writeMetadataReads;

		RegisteredFunction(String prefix, long clientProcessId) {
			this.prefix = prefix;
			this.clientProcessId = clientProcessId;
		}

		@Override public String getId() { return RegisteredFunction.class.getName(); }
		@Override public boolean hasResult() { resultMetadataReads++; return true; }
		@Override public boolean isHA() { haMetadataReads++; return true; }
		@Override public boolean optimizeForWrite() { writeMetadataReads++; return false; }

		@Override
		public void execute(FunctionContext<String> context) {
			if (ProcessHandle.current().pid() == clientProcessId) {
				throw new IllegalStateException("Function must execute in the separate server process");
			}
			context.getResultSender().lastResult(prefix + context.getArguments());
		}
	}

	public static final class InstallServerFunction implements Function<String> {

		private static final long serialVersionUID = 1L;
		private final long clientProcessId;

		InstallServerFunction(long clientProcessId) {
			this.clientProcessId = clientProcessId;
		}

		@Override public String getId() { return InstallServerFunction.class.getName(); }

		@Override
		public void execute(FunctionContext<String> context) {
			if (ProcessHandle.current().pid() == clientProcessId) {
				throw new IllegalStateException("Registration must occur in the separate server process");
			}
			FunctionService.registerFunction(new RegisteredFunction("server:", clientProcessId));
			context.getResultSender().lastResult("installed");
		}
	}
}
