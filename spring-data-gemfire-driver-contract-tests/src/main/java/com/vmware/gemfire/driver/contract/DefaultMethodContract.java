/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.contract;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;

import org.junit.jupiter.api.function.Executable;

import com.vmware.gemfire.driver.spi.GemFireDriver;
import com.vmware.gemfire.driver.spi.UnsupportedFeatureException;
import com.vmware.gemfire.driver.spi.UnsupportedFeatureException.Reason;

/** Checks coverage and outcomes of every default method in the resolved SPI, including new interfaces. */
public final class DefaultMethodContract {

	public record Probe(String feature, Executable invocation) { }

	public void verify(Map<Method, Probe> probes, Map<Method, Reason> expectedUnsupported,
		String nativeVersion) throws Exception {
		Set<Method> defaults = spiDefaultMethods();
		assertThat(probes.keySet()).as("Every SPI default method needs a behavioral probe")
			.containsExactlyInAnyOrderElementsOf(defaults);
		assertThat(defaults).as("Unsupported declarations must identify real SPI default methods")
			.containsAll(expectedUnsupported.keySet());
		for (Map.Entry<Method, Probe> entry : probes.entrySet()) {
			verifyOutcome(entry.getValue(), expectedUnsupported.get(entry.getKey()), nativeVersion);
		}
	}

	void verifyOutcome(Probe probe, Reason expected, String nativeVersion) throws Exception {
		try {
			probe.invocation().execute();
		}
		catch (UnsupportedFeatureException ex) {
			assertThat(expected).as("Unexpected unsupported feature: %s", probe.feature()).isNotNull();
			assertThat(ex.feature()).isEqualTo(probe.feature());
			assertThat(ex.reason()).isEqualTo(expected);
			if (expected == Reason.REMOVED) {
				assertThat(ex.gemFireVersion()).contains(nativeVersion);
			} else {
				assertThat(ex.gemFireVersion()).isEmpty();
			}
			return;
		}
		catch (Exception ex) { throw ex; }
		catch (Error ex) { throw ex; }
		catch (Throwable ex) { throw new AssertionError("Probe failed: " + probe.feature(), ex); }
		assertThat(expected).as("Expected unsupported feature worked: %s", probe.feature()).isNull();
	}

	private Set<Method> spiDefaultMethods() throws Exception {
		String prefix = "com/vmware/gemfire/driver/spi/";
		Path location = Path.of(GemFireDriver.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		List<String> names = new ArrayList<>();
		if (Files.isDirectory(location)) {
			try (var files = Files.walk(location.resolve(prefix))) {
				files.filter(p -> p.toString().endsWith(".class"))
					.forEach(p -> names.add(location.relativize(p).toString().replace('\\', '/')));
			}
		} else {
			try (JarFile jar = new JarFile(location.toFile())) {
				jar.stream().map(e -> e.getName()).filter(n -> n.startsWith(prefix) && n.endsWith(".class"))
					.forEach(names::add);
			}
		}
		Set<Method> methods = new HashSet<>();
		for (String name : names) {
			Class<?> type = Class.forName(name.substring(0, name.length() - 6).replace('/', '.'), false,
				GemFireDriver.class.getClassLoader());
			Arrays.stream(type.getDeclaredMethods()).filter(Method::isDefault).forEach(methods::add);
		}
		return methods;
	}
}
