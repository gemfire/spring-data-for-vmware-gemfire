/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.vmware.gemfire.driver.fixture.submission.SubmissionProbe;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.security.ResourcePermission;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

/** Private transport experiment; no public API or secured authorization claim. */
@Timeout(60)
class NativeClientFunctionEnvelopeTests {

	@TempDir Path directory;

	@Test
	void regionTransportsNeutralStateAndCallbacks() throws Exception {
		assertTargets("region");
	}

	@Test
	void singleServerTransportsNeutralStateAndCallbacks() throws Exception {
		assertTargets("server");
	}

	@Test
	void allServersTransportsNeutralStateAndCallbacks() throws Exception {
		assertTargets("servers");
	}

	private void assertTargets(String target) throws Exception {
		try (var server = new NativeServerFixture(directory);
			var cache = new ClientCacheFactory().set("log-level", "error")
				.addPoolServer("localhost", server.port).create()) {
			switch (target) {
				case "region" -> {
					var region = cache.createClientRegionFactory(ClientRegionShortcut.PROXY).create("distributed");
					assertSubmission(FunctionService.onRegion(region).withFilter(Set.of("key")), "/distributed");
				}
				case "server" -> {
					assertSubmission(FunctionService.onServer(cache), null);
					assertSubmission(FunctionService.onServer(cache.getDefaultPool()), null);
				}
				case "servers" -> {
					assertSubmission(FunctionService.onServers(cache), null);
					assertSubmission(FunctionService.onServers(cache.getDefaultPool()), null);
				}
				default -> throw new IllegalArgumentException(target);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void assertSubmission(Execution execution, String regionName) throws InterruptedException {
		var body = new SubmissionProbe();
		var envelope = new Envelope(body);
		assertThat(FunctionService.getFunction(body.id())).isNull();
		FunctionService.registerFunction(envelope);
		try {
			for (String prefix : List.of("first:", "changed:")) {
				body.prefix(prefix);
				Object result = execution.setArguments("argument").execute(envelope, 10, TimeUnit.SECONDS)
					.getResult(10, TimeUnit.SECONDS);
				assertThat((List<Object>) result).containsExactlyInAnyOrderElementsOf(List.of(
					prefix + "argument", List.of("permissions=1", "bodies=1",
						"permissionRegion=" + regionName, "permissionArguments=argument",
						"function=" + body.id(), "region=" + regionName,
						"filter=" + (regionName == null ? "null" : "[key]"), "duplicate=false")));
				assertThat(body.permissionCalls()).isZero();
				assertThat(body.bodyCalls()).isZero();
				assertThat(FunctionService.getFunction(body.id())).isSameAs(envelope);
				assertThat(((Envelope) FunctionService.getFunction(body.id())).body).isSameAs(body);
			}
		}
		finally {
			FunctionService.unregisterFunction(body.id());
		}
	}

	/** Driver-side envelope; its class and the neutral body are explicitly on both fixture classpaths. */
	public static final class Envelope implements Function<Object> {
		private static final long serialVersionUID = 1L;
		private final SubmissionProbe body;

		Envelope(SubmissionProbe body) { this.body = body; }
		@Override public String getId() { return body.id(); }
		@Override public boolean hasResult() { return body.hasResult(); }
		@Override public boolean isHA() { return body.highAvailability(); }
		@Override public boolean optimizeForWrite() { return body.optimizeForWrite(); }

		@Override
		public Collection<ResourcePermission> getRequiredPermissions(String region, Object arguments) {
			return body.permissions(region, arguments).stream().map(permission -> new ResourcePermission(
				permission.resource(), permission.operation(), permission.target(), permission.key())).toList();
		}

		@Override
		public void execute(FunctionContext<Object> context) {
			if (FunctionService.getFunction(getId()) != null) {
				throw new IllegalStateException("Local registration must not install the envelope on the server");
			}
			RegionFunctionContext<?> regionContext = context instanceof RegionFunctionContext<?> value ? value : null;
			var invocation = new SubmissionProbe.Context(context.getArguments(), context.getFunctionId(),
				regionContext == null ? null : regionContext.getDataSet().getFullPath(),
				regionContext == null ? null : regionContext.getFilter(), context.isPossibleDuplicate());
			body.execute(invocation, new SubmissionProbe.Sender() {
				@Override public void sendResult(Object result) { context.getResultSender().sendResult(result); }
				@Override public void lastResult(Object result) { context.getResultSender().lastResult(result); }
			});
		}
	}
}
