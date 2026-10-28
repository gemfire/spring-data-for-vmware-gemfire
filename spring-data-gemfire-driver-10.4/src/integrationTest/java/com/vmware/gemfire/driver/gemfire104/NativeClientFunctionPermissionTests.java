/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.security.ResourcePermission;
import org.apache.geode.security.ResourcePermission.Operation;
import org.apache.geode.security.ResourcePermission.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

/** Submission hook evidence only; the unsecured fixture does not test authorization enforcement. */
@Timeout(60)
class NativeClientFunctionPermissionTests {

	@TempDir Path directory;

	@Test
	void defaultPermissionsRequireDataWriteForBothOverloads() {
		var function = new PermissionFunction();
		var expected = new ResourcePermission(Resource.DATA, Operation.WRITE);
		assertThat(function.getRequiredPermissions(null)).containsExactly(expected);
		assertThat(function.getRequiredPermissions("distributed")).containsExactly(expected);
		assertThat(function.getRequiredPermissions(null, "argument")).containsExactly(expected);
		assertThat(function.getRequiredPermissions("distributed", "argument")).containsExactly(expected);
	}

	@Test
	void remoteSubmissionDelegatesToTheRegionOnlyOverride() throws Exception {
		assertTargets(false);
	}

	@Test
	void remoteSubmissionUsesTheArgumentAwareOverride() throws Exception {
		assertTargets(true);
	}

	private void assertTargets(boolean argumentAware) throws Exception {
		try (var server = new NativeServerFixture(directory);
			var cache = new ClientCacheFactory().set("log-level", "error")
				.addPoolServer("localhost", server.port).create()) {
			var region = cache.createClientRegionFactory(ClientRegionShortcut.PROXY).create("distributed");
			assertPermissions(FunctionService.onRegion(region), "/distributed", argumentAware);
			assertPermissions(FunctionService.onServer(cache), null, argumentAware);
			assertPermissions(FunctionService.onServer(cache.getDefaultPool()), null, argumentAware);
			assertPermissions(FunctionService.onServers(cache), null, argumentAware);
			assertPermissions(FunctionService.onServers(cache.getDefaultPool()), null, argumentAware);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void assertPermissions(Execution execution, String regionName, boolean argumentAware)
			throws InterruptedException {
		PermissionFunction function = argumentAware ? new ArgumentPermissions() : new RegionPermissions();
		assertThat(FunctionService.getFunction(function.getId())).isNull();
		Object results = execution.setArguments("argument").execute(function, 10, TimeUnit.SECONDS)
			.getResult(10, TimeUnit.SECONDS);
		assertThat(results).isEqualTo(List.of(List.of(
			"regionCalls=" + (argumentAware ? 0 : 1),
			"argumentCalls=" + (argumentAware ? 1 : 0),
			"region=" + regionName,
			"permissionArguments=" + (argumentAware ? "argument" : null),
			"bodyArguments=argument", "permissionRemote=true", "bodyRemote=true")));
		assertThat(function.regionCalls).isZero();
		assertThat(function.argumentCalls).isZero();
		assertThat(FunctionService.getFunction(function.getId())).isNull();
	}

	/** Present on both process classpaths; no client or server registration is performed. */
	public static class PermissionFunction implements Function<String> {
		private static final long serialVersionUID = 1L;
		private final long clientProcessId = ProcessHandle.current().pid();
		protected int regionCalls;
		protected int argumentCalls;
		private String permissionRegion;
		private Object permissionArguments;
		private boolean permissionRemote;

		@Override public boolean isHA() { return false; }

		protected Collection<ResourcePermission> permissions(String regionName, Object arguments) {
			permissionRegion = regionName;
			permissionArguments = arguments;
			permissionRemote = ProcessHandle.current().pid() != clientProcessId;
			return List.of(new ResourcePermission(Resource.DATA, Operation.READ, regionName));
		}

		@Override
		public void execute(FunctionContext<String> context) {
			context.getResultSender().lastResult(List.of(
				"regionCalls=" + regionCalls, "argumentCalls=" + argumentCalls,
				"region=" + permissionRegion, "permissionArguments=" + permissionArguments,
				"bodyArguments=" + context.getArguments(), "permissionRemote=" + permissionRemote,
				"bodyRemote=" + (ProcessHandle.current().pid() != clientProcessId)));
		}
	}

	public static class RegionPermissions extends PermissionFunction {
		private static final long serialVersionUID = 1L;

		@Override
		public Collection<ResourcePermission> getRequiredPermissions(String regionName) {
			regionCalls++;
			return permissions(regionName, null);
		}
	}

	public static final class ArgumentPermissions extends RegionPermissions {
		private static final long serialVersionUID = 1L;

		@Override
		public Collection<ResourcePermission> getRequiredPermissions(String regionName, Object arguments) {
			argumentCalls++;
			return permissions(regionName, arguments);
		}
	}
}
