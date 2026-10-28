/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

/** Client-local callback evidence, without a server process or server endpoints. */
@Timeout(60)
class NativeClientLocalFunctionTests {

	@TempDir Path directory;

	private ClientCache client() {
		return new ClientCacheFactory().set("log-level", "error").set("name", "local-functions").create();
	}

	private Region<String, String> region(ClientCache cache, ClientRegionShortcut shortcut) {
		if (cache.findDiskStore("functions") == null) {
			cache.createDiskStoreFactory().setDiskDirs(new java.io.File[] { directory.toFile() })
				.setMaxOplogSize(1).create("functions");
		}
		var factory = cache.<String, String>createClientRegionFactory(shortcut);
		if (shortcut != ClientRegionShortcut.LOCAL && shortcut != ClientRegionShortcut.LOCAL_HEAP_LRU) {
			factory.setDiskStoreName("functions");
		}
		return factory.create(shortcut.name());
	}

	@Test
	void persistentLocalRegionExecutesObjectAndRegisteredIdWithClientContext() throws InterruptedException {
		assertLocalExecution(ClientRegionShortcut.LOCAL_PERSISTENT);
	}

	@Test
	void persistentOverflowLocalRegionExecutesObjectAndRegisteredIdWithClientContext() throws InterruptedException {
		assertLocalExecution(ClientRegionShortcut.LOCAL_PERSISTENT_OVERFLOW);
	}

	private void assertLocalExecution(ClientRegionShortcut shortcut) throws InterruptedException {
		try (var cache = client()) {
			var region = region(cache, shortcut);
			region.put("key", "value");
			var function = new RecordingFunction(Terminal.RESULT);
			var execution = FunctionService.onRegion(region).setArguments("argument").withFilter(Set.of("key"));
			assertThat(FunctionService.isRegistered(function.getId())).isFalse();
			assertThat(execution.execute(function).getResult(10, TimeUnit.SECONDS))
				.isEqualTo(List.of("state:argument", "value"));
			assertThat(FunctionService.isRegistered(function.getId())).isFalse();
			assertContext(function, cache, region);

			FunctionService.registerFunction(function);
			try {
				function.observation.set(null);
				assertThat(execution.execute(function.getId()).getResult(10, TimeUnit.SECONDS))
					.isEqualTo(List.of("state:argument", "value"));
				assertContext(function, cache, region);
				assertThat(function.invocations).hasValue(2);
			}
			finally {
				FunctionService.unregisterFunction(function.getId());
			}
		}
	}

	private void assertContext(RecordingFunction function, ClientCache cache, Region<?, ?> region) {
		var observed = function.observation.get();
		assertThat(observed).isNotNull();
		assertThat(observed.cache()).isSameAs(cache);
		assertThat(observed.region()).isSameAs(region);
		assertThat(observed.filter()).isEqualTo(Set.of("key"));
		assertThat(observed.arguments()).isEqualTo("argument");
		assertThat(observed.functionId()).isEqualTo(function.getId());
		assertThat(observed.memberName()).isEqualTo("local-functions");
		assertThat(observed.principal()).isNull();
		assertThat(observed.possibleDuplicate()).isFalse();
		assertThat(observed.processId()).isEqualTo(ProcessHandle.current().pid());
	}

	@Test
	void nonPersistentLocalShortcutsRejectObjectAndRegisteredIdBeforeCallback() {
		try (var cache = client()) {
			var function = new RecordingFunction(Terminal.RESULT);
			FunctionService.registerFunction(function);
			try {
				for (var shortcut : List.of(ClientRegionShortcut.LOCAL, ClientRegionShortcut.LOCAL_HEAP_LRU,
						ClientRegionShortcut.LOCAL_OVERFLOW)) {
					var execution = FunctionService.onRegion(region(cache, shortcut));
					assertThatThrownBy(() -> execution.execute(function))
						.isInstanceOf(FunctionException.class).hasMessageContaining("DataPolicy.NORMAL");
					assertThatThrownBy(() -> execution.execute(function.getId()))
						.isInstanceOf(FunctionException.class).hasMessageContaining("DataPolicy.NORMAL");
				}
				assertThat(function.invocations).hasValue(0);
			}
			finally {
				FunctionService.unregisterFunction(function.getId());
			}
		}
	}

	@Test
	void localSendExceptionCompletesWithTheExceptionAsAResult() throws InterruptedException {
		try (var cache = client()) {
			var function = new RecordingFunction(Terminal.EXCEPTION);
			Object results = FunctionService.onRegion(region(cache, ClientRegionShortcut.LOCAL_PERSISTENT))
				.execute(function).getResult(10, TimeUnit.SECONDS);
			assertThat(results).isEqualTo(List.of(function.failure));
			assertThat(function.invocations).hasValue(1);
		}
	}

	@Test
	void localCallbackWithoutLastResultFailsCollection() {
		try (var cache = client()) {
			var function = new RecordingFunction(Terminal.MISSING);
			var execution = FunctionService.onRegion(region(cache, ClientRegionShortcut.LOCAL_PERSISTENT));
			assertThatThrownBy(() -> execution.execute(function).getResult(10, TimeUnit.SECONDS))
				.isInstanceOf(FunctionException.class).hasMessageContaining("did not send last result");
			assertThat(function.invocations).hasValue(1);
		}
	}

	private enum Terminal { RESULT, EXCEPTION, MISSING }

	private record Observation(Object cache, Object region, Set<?> filter, Object arguments, String functionId,
		String memberName, Object principal, boolean possibleDuplicate, long processId) { }

	private static final class RecordingFunction implements Function<String> {
		private static final long serialVersionUID = 1L;
		private final Terminal terminal;
		private final String prefix = "state:";
		private final AtomicInteger invocations = new AtomicInteger();
		private final AtomicReference<Observation> observation = new AtomicReference<>();
		private final IllegalStateException failure = new IllegalStateException("local function failure");

		private RecordingFunction(Terminal terminal) {
			this.terminal = terminal;
		}

		@Override public String getId() { return RecordingFunction.class.getName(); }
		@Override public boolean isHA() { return false; }

		@Override
		public void execute(FunctionContext<String> context) {
			invocations.incrementAndGet();
			var regionContext = (RegionFunctionContext<String>) context;
			observation.set(new Observation(context.getCache(), regionContext.getDataSet(),
				Set.copyOf(regionContext.getFilter()), context.getArguments(), context.getFunctionId(),
				context.getMemberName(), context.getPrincipal(), context.isPossibleDuplicate(), ProcessHandle.current().pid()));
			switch (terminal) {
				case RESULT -> {
					context.getResultSender().sendResult(prefix + context.getArguments());
					context.getResultSender().lastResult(regionContext.getDataSet().get("key"));
				}
				case EXCEPTION -> context.getResultSender().sendException(failure);
				case MISSING -> { }
			}
		}
	}
}
