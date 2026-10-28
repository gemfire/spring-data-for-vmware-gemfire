/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.vmware.gemfire.driver.fixture.submission.SubmissionProbe;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ServerOperationException;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

/** Deployment negative control: ID invocation must not depend on submitted-body classes. */
@Timeout(60)
class NativeClientFunctionDeploymentTests {

	@TempDir Path directory;

	@Test
	void missingEnvelopeAndApplicationClassesRejectObjectButPreserveIdInvocation() throws Exception {
		var body = new SubmissionProbe();
		var envelope = new NativeClientFunctionEnvelopeTests.Envelope(body);
		try (var server = new NativeServerFixture(directory, isolatedServerClasspath());
			var cache = new ClientCacheFactory().set("log-level", "error")
				.addPoolServer("localhost", server.port).create()) {
			var execution = FunctionService.onServer(cache);
			// This native-only fixture checks class absence remotely and installs an independent body
			// under the same ID. A mistaken object-to-ID fallback would therefore succeed.
			assertThat(execution.execute(new InstallIndependentBody(body.id(), envelope.getClass().getName()),
				10, TimeUnit.SECONDS).getResult(10, TimeUnit.SECONDS)).isEqualTo(List.of("installed"));
			assertThat(FunctionService.getFunction(body.id())).isNull();
			assertThat(execution.setArguments("before").execute(body.id(), 10, TimeUnit.SECONDS)
				.getResult(10, TimeUnit.SECONDS)).isEqualTo(List.of("independent:before"));

			assertThatThrownBy(() -> execution.setArguments("object").execute(envelope, 10, TimeUnit.SECONDS)
				.getResult(10, TimeUnit.SECONDS))
				.isInstanceOf(ServerOperationException.class)
				.hasRootCauseInstanceOf(ClassNotFoundException.class)
				.hasStackTraceContaining(envelope.getClass().getName());

			assertThat(execution.setArguments("after").execute(body.id(), 10, TimeUnit.SECONDS)
				.getResult(10, TimeUnit.SECONDS)).isEqualTo(List.of("independent:after"));
			assertThat(body.permissionCalls()).isZero();
			assertThat(body.bodyCalls()).isZero();
			assertThat(FunctionService.getFunction(body.id())).isNull();
		}
	}

	private String isolatedServerClasspath() throws Exception {
		Path integrationClasses = Path.of(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
		Path applicationClasses = Path.of(SubmissionProbe.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		assertThat(integrationClasses).isDirectory().isNotEqualTo(applicationClasses);
		Path isolatedClasses = Files.createDirectory(directory.resolve("isolated-classes"));
		String envelopePrefix = NativeClientFunctionEnvelopeTests.class.getName().replace('.', '/');
		try (var files = Files.walk(integrationClasses)) {
			for (Path source : files.filter(Files::isRegularFile).toList()) {
				Path relative = integrationClasses.relativize(source);
				String name = relative.toString().replace(File.separatorChar, '/');
				if (name.equals(envelopePrefix + ".class") || name.startsWith(envelopePrefix + "$")) continue;
				Path destination = isolatedClasses.resolve(relative);
				Files.createDirectories(destination.getParent());
				Files.copy(source, destination);
			}
		}
		var classpath = new ArrayList<String>();
		boolean replaced = false;
		boolean removed = false;
		for (String entry : System.getProperty("nativeFixtureClasspath").split(Pattern.quote(File.pathSeparator))) {
			Path path = Path.of(entry).toAbsolutePath().normalize();
			if (path.equals(applicationClasses.toAbsolutePath().normalize())) { removed = true; continue; }
			if (path.equals(integrationClasses.toAbsolutePath().normalize())) {
				classpath.add(isolatedClasses.toString());
				replaced = true;
			} else {
				classpath.add(entry);
			}
		}
		assertThat(replaced).as("replace integration output containing envelope classes").isTrue();
		assertThat(removed).as("remove application callback output").isTrue();
		return String.join(File.pathSeparator, classpath);
	}

	public static final class InstallIndependentBody implements Function<Object> {
		private static final long serialVersionUID = 1L;
		private final String applicationClass;
		private final String envelopeClass;
		InstallIndependentBody(String applicationClass, String envelopeClass) {
			this.applicationClass = applicationClass;
			this.envelopeClass = envelopeClass;
		}
		@Override public String getId() { return getClass().getName(); }
		@Override public boolean isHA() { return false; }
		@Override public void execute(FunctionContext<Object> context) {
			for (String name : List.of(applicationClass, envelopeClass)) {
				try {
					Class.forName(name);
					throw new IllegalStateException("Class must be absent on server: " + name);
				} catch (ClassNotFoundException expected) {
					// Missing classes are the precondition for this deployment control.
				}
			}
			FunctionService.registerFunction(new IndependentBody(applicationClass));
			context.getResultSender().lastResult("installed");
		}
	}

	public static final class IndependentBody implements Function<Object> {
		private static final long serialVersionUID = 1L;
		private final String id;
		IndependentBody(String id) { this.id = id; }
		@Override public String getId() { return id; }
		@Override public boolean isHA() { return false; }
		@Override public void execute(FunctionContext<Object> context) {
			context.getResultSender().lastResult("independent:" + context.getArguments());
		}
	}
}
