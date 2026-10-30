/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.geode.distributed.DistributedSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.DriverOperationException;

@ResourceLock(Resources.SYSTEM_PROPERTIES)
class NativePropertyFileTests {

	@TempDir Path temporary;

	@Test
	void defaultsAndIndependentOverridesAreResolvedWithoutConnectingOrLoadingContents() throws Exception {
		try (var regular = new PropertyOverride("gemfirePropertyFile", null);
			var security = new PropertyOverride("gemfireSecurityPropertyFile", null)) {
			var driver = new GemFire104Driver();
			assertThat(driver.resolvePropertiesFile().fileName()).isEqualTo("gemfire.properties");
			assertThat(driver.resolveSecurityPropertiesFile().fileName()).isEqualTo("gfsecurity.properties");
			var file = Files.writeString(temporary.resolve("private-token.properties"), "not valid configuration: \\uZZZZ");
			System.setProperty("gemfirePropertyFile", file.toString());
			System.setProperty("gemfireSecurityPropertyFile", file.toString());
			var captured = driver.resolvePropertiesFile();
			assertThat(captured.fileName()).isEqualTo(file.toString());
			assertThat(captured.location().orElseThrow().toExternalForm())
				.isEqualTo(file.toRealPath().toFile().toURI().toURL().toExternalForm());
			assertThat(driver.resolveSecurityPropertiesFile().location().orElseThrow().toExternalForm())
				.isEqualTo(DistributedSystem.getSecurityPropertiesFileURL().toExternalForm());
			assertThat(captured.toString()).doesNotContain("private-token", temporary.toString());
			System.setProperty("gemfirePropertyFile", temporary.resolve("missing.properties").toString());
			assertThat(driver.resolvePropertiesFile().location()).isEmpty();
			assertThat(driver.resolveSecurityPropertiesFile().location()).isPresent();
			assertThat(captured.location()).isPresent();
			assertThat(captured.fileName()).isEqualTo(file.toString());
		}
	}

	@Test
	void lookupPreservesWorkingDirectoryHomeAndClasspathOrder() throws Exception {
		var workingFile = Files.createTempFile(Path.of("."), "driver-resolution-", ".properties");
		try (var regular = new PropertyOverride("gemfirePropertyFile", workingFile.getFileName().toString());
			var home = new PropertyOverride("user.home", temporary.toString())) {
			var driver = new GemFire104Driver();
			var homeFile = Files.createFile(temporary.resolve(workingFile.getFileName()));
			assertThat(driver.resolvePropertiesFile().location().orElseThrow().toExternalForm())
				.isEqualTo(workingFile.toRealPath().toFile().toURI().toURL().toExternalForm());
			Files.delete(workingFile);
			assertThat(driver.resolvePropertiesFile().location().orElseThrow().toExternalForm())
				.isEqualTo(homeFile.toRealPath().toFile().toURI().toURL().toExternalForm());
			Files.delete(homeFile);
			assertThat(driver.resolvePropertiesFile().location()).isEmpty();
			String resource = "property-resolution/driver.properties";
			System.setProperty("gemfirePropertyFile", resource);
			var expected = DistributedSystem.class.getClassLoader().getResource(resource);
			assertThat(expected).isNotNull();
			assertThat(driver.resolvePropertiesFile().location().orElseThrow().toExternalForm())
				.isEqualTo(expected.toExternalForm());
			assertThat(DistributedSystem.getPropertyFileURL().toExternalForm()).isEqualTo(expected.toExternalForm());
		} finally {
			Files.deleteIfExists(workingFile);
		}
	}

	@Test
	void resolutionFailuresAreClassifiedAndFatalErrorsPropagate() {
		var driver = new GemFire104Driver();
		var cause = new IllegalStateException("private-path-token");
		try (var nativeApi = mockStatic(DistributedSystem.class)) {
			nativeApi.when(DistributedSystem::getPropertiesFile).thenThrow(cause);
			nativeApi.when(DistributedSystem::getSecurityPropertiesFile).thenReturn("private-name");
			nativeApi.when(DistributedSystem::getSecurityPropertiesFileURL).thenThrow(cause);
			for (Runnable resolve : List.<Runnable>of(driver::resolvePropertiesFile, driver::resolveSecurityPropertiesFile)) {
				assertThatThrownBy(resolve::run).isInstanceOfSatisfying(DriverOperationException.class, failure -> {
					assertThat(failure.kind()).isEqualTo(DriverFailureKind.INVALID_STATE);
					assertThat(failure.getCause()).isSameAs(cause);
					assertThat(failure.getMessage()).doesNotContain("private-path-token", "private-name");
				});
			}
			var fatal = new LinkageError("synthetic fatal error");
			nativeApi.when(DistributedSystem::getPropertiesFile).thenThrow(fatal);
			assertThatThrownBy(driver::resolvePropertiesFile).isSameAs(fatal);
		}
	}

	/** Restore only the property this test owns, including its original absence. */
	private final class PropertyOverride implements AutoCloseable {
		private final String key;
		private final String previous;
		PropertyOverride(String key, String value) {
			this.key = key;
			this.previous = System.getProperty(key);
			if (value == null) System.clearProperty(key); else System.setProperty(key, value);
		}
		@Override public void close() {
			if (previous == null) System.clearProperty(key); else System.setProperty(key, previous);
		}
	}
}
