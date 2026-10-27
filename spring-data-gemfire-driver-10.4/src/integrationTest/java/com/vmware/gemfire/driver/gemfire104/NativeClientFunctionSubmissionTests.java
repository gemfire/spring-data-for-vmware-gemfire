/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

/** Exact-version client submission evidence; this fixture does not add a public hosting API. */
@Timeout(60)
class NativeClientFunctionSubmissionTests {

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
	void clientRegionSubmitsAnUnregisteredFunctionObject() throws InterruptedException {
		try (var cache = client()) {
			var region = cache.createClientRegionFactory(ClientRegionShortcut.PROXY).create("distributed");
			assertSubmission(FunctionService.onRegion(region));
		}
	}

	@Test
	void singleServerTargetsSubmitAnUnregisteredFunctionObject() throws InterruptedException {
		try (var cache = client()) {
			assertSubmission(FunctionService.onServer(cache));
			assertSubmission(FunctionService.onServer(cache.getDefaultPool()));
		}
	}

	@Test
	void allServerTargetsSubmitAnUnregisteredFunctionObject() throws InterruptedException {
		try (var cache = client()) {
			assertSubmission(FunctionService.onServers(cache));
			assertSubmission(FunctionService.onServers(cache.getDefaultPool()));
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void assertSubmission(Execution execution) throws InterruptedException {
		var function = new SubmittedFunction("serialized-state:", ProcessHandle.current().pid());
		assertThat(FunctionService.getFunction(function.getId())).isNull();
		Object results = execution.setArguments("argument")
			.execute(function, 10, TimeUnit.SECONDS).getResult(10, TimeUnit.SECONDS);
		assertThat(results).isEqualTo(List.of("serialized-state:argument"));
		assertThat(FunctionService.getFunction(function.getId())).isNull();
	}

	/** Present on the fixture classpath, but never registered on the client or server. */
	public static final class SubmittedFunction implements Function<String> {

		private static final long serialVersionUID = 1L;

		private final String prefix;
		private final long clientProcessId;

		SubmittedFunction(String prefix, long clientProcessId) {
			this.prefix = prefix;
			this.clientProcessId = clientProcessId;
		}

		@Override
		public String getId() {
			return SubmittedFunction.class.getName();
		}

		@Override
		public void execute(FunctionContext<String> context) {
			if (ProcessHandle.current().pid() == clientProcessId) {
				throw new IllegalStateException("Function must execute in the separate server process");
			}
			if (FunctionService.getFunction(getId()) != null) {
				throw new IllegalStateException("Object submission must not require server registration");
			}
			context.getResultSender().lastResult(prefix + context.getArguments());
		}
	}
}
