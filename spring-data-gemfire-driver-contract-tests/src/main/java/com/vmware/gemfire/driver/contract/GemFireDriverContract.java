/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.contract;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

import org.junit.jupiter.api.Test;

import com.vmware.gemfire.driver.core.GemFireDriverManager;
import com.vmware.gemfire.driver.spi.CacheConfiguration;
import com.vmware.gemfire.driver.spi.DriverBindException;
import com.vmware.gemfire.driver.spi.DriverCacheLoader;
import com.vmware.gemfire.driver.spi.DriverLoaderHelper;
import com.vmware.gemfire.driver.spi.DriverConfiguration;
import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverClientRuntime;
import com.vmware.gemfire.driver.spi.DriverLog;
import com.vmware.gemfire.driver.spi.DriverLogMessage;
import com.vmware.gemfire.driver.spi.DriverStatisticDescriptor;
import com.vmware.gemfire.driver.spi.DriverStatisticsTypes;
import com.vmware.gemfire.driver.spi.StatisticDescriptorConfiguration;
import com.vmware.gemfire.driver.spi.StatisticDescriptorConfiguration.ValueType;
import com.vmware.gemfire.driver.spi.DriverCancellation;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.UnsupportedFeatureException;
import com.vmware.gemfire.driver.spi.DriverRegion;
import com.vmware.gemfire.driver.spi.DriverPool;
import com.vmware.gemfire.driver.spi.PoolConfiguration;
import com.vmware.gemfire.driver.spi.PoolConfiguration.Endpoint;
import com.vmware.gemfire.driver.spi.DriverRegionLifecycleListener;
import com.vmware.gemfire.driver.spi.GemFireDriver;
import com.vmware.gemfire.driver.spi.SpiVersion;
import com.vmware.gemfire.driver.spi.UnsupportedFeatureException.Reason;
import com.vmware.gemfire.driver.spi.config.ClientRegionShortcut;
import com.vmware.gemfire.driver.contract.DefaultMethodContract.Probe;

/**
 * Extend in a driver's own test source set. Exactly one provider must be on its runtime classpath.
 * Native runs use isolated local regions; no locator/server or fixed port is required. Each driver
 * declares its unsupported methods explicitly; adding a default SPI method without a probe fails.
 */
public abstract class GemFireDriverContract extends StatisticsContract {

	protected abstract String expectedNativeVersion();

	protected abstract Map<Method, Reason> expectedUnsupportedMethods();

	protected GemFireDriver driver() { return new GemFireDriverManager().driver(); }

	protected CacheConfiguration configuration() {
		return new CacheConfiguration(Map.of("mcast-port", "0", "locators", "",
			"log-level", "error", "statistic-sampling-enabled", "false"));
	}

	@Test
	void discoversProviderAndChecksGeneratedStamp() throws Exception {
		GemFireDriver driver = driver();
		assertThat(driver.metadata().driverName()).isNotBlank();
		assertThat(driver.runtimeGemFireVersion()).isEqualTo(expectedNativeVersion());
		String path = "META-INF/gemfire-driver/" + driver.getClass().getName() + ".properties";
		try (var input = driver.getClass().getClassLoader().getResourceAsStream(path)) {
			assertThat(input).as("Generated driver stamp").isNotNull();
			Properties stamp = new Properties();
			stamp.load(input);
			assertThat(stamp.getProperty("spi.version")).isEqualTo(SpiVersion.runtime().toString());
			assertThat(stamp.getProperty("gemfire.version")).isEqualTo(expectedNativeVersion());
		}
	}

	@Test
	void everyDefaultMethodMatchesTheDriversDeclaration() throws Exception {
		new DefaultMethodContract().verify(probes(), expectedUnsupportedMethods(), expectedNativeVersion());
	}

	@Test
	void runtimeAndCacheCancellationFollowConnectionLifecycle() {
		inCache(connection -> {
			DriverClientRuntime runtime = connection.getRuntime();
			DriverCancellation cancellation = connection.getCancellation();
			assertThat(runtime.isConnected()).isTrue();
			assertThat(runtime.getMember()).isEqualTo(connection.getRuntime().getMember());
			assertThat(runtime.getMember().getUniqueId()).isNotBlank();
			Throwable cause = new IllegalStateException("private-diagnostic-token");
			assertThat(cancellation.generateCancelledException(cause)).isNull();
			cancellation.checkCancelInProgress(cause);
			connection.close();
			assertThat(runtime.isConnected()).isFalse();
			assertThat(cancellation.isCancelInProgress()).isTrue();
			assertThat(cancellation.cancelInProgress()).isNotBlank().doesNotContain("private-diagnostic-token");
			assertThat(cancellation.generateCancelledException(null)).isInstanceOf(DriverOperationException.class);
			DriverOperationException failure = (DriverOperationException) cancellation.generateCancelledException(cause);
			assertThat(failure.kind()).isEqualTo(DriverFailureKind.CANCELLED);
			assertThat(failure.getRootCause()).isSameAs(cause);
			assertThat(failure.getMessage()).doesNotContain("private-diagnostic-token");
			assertThatThrownBy(cancellation::checkCancelInProgress)
				.isInstanceOfSatisfying(DriverOperationException.class,
					ex -> assertThat(ex.kind()).isEqualTo(DriverFailureKind.CANCELLED));
			assertThatThrownBy(() -> cancellation.checkCancelInProgress(cause))
				.isInstanceOfSatisfying(DriverOperationException.class, ex -> {
					assertThat(ex.kind()).isEqualTo(DriverFailureKind.CANCELLED);
					assertThat(ex.getRootCause()).isSameAs(cause);
				});
		});
	}

	@Test
	void runtimePropertyCopiesRetainValuesWithoutMutatingConfigurationOrLeakingDiagnostics() {
		Map<String, String> settings = new LinkedHashMap<>();
		configuration().properties().forEach((key, value) -> settings.put((String) key, (String) value));
		settings.put("security-username", "private-user");
		settings.put("security-password", "private-password");
		try (var connection = driver().connect(new CacheConfiguration(settings))) {
			var runtime = connection.getRuntime();
			var properties = runtime.getProperties();
			var security = runtime.getSecurityProperties();
			assertThat(properties.properties()).containsEntry("log-level", "error");
			assertThat(security.properties()).containsEntry("security-username", "private-user")
				.containsEntry("security-password", "private-password").doesNotContainKey("log-level");
			assertThat(properties.toString()).isEqualTo("DriverPropertySnapshot[properties=<redacted>]");
			assertThat(security.toString()).isEqualTo("DriverPropertySnapshot[properties=<redacted>]");
			properties.properties().clear();
			security.properties().setProperty("security-password", "mutated");
			assertThat(runtime.getProperties().properties()).containsEntry("log-level", "error");
			assertThat(runtime.getSecurityProperties().properties()).containsEntry("security-password", "private-password");
			connection.close();
			assertThat(properties.properties()).containsEntry("log-level", "error");
			assertThat(security.properties()).containsEntry("security-password", "private-password");
			assertThat(runtime.getProperties().properties()).containsEntry("log-level", "error");
			assertThat(runtime.getSecurityProperties().properties()).containsEntry("security-password", "private-password");
		}
	}

	@Test
	void configuredIdentityAndExplicitDurableCopiesSurviveClosure() {
		Map<String, String> settings = new LinkedHashMap<>();
		configuration().properties().forEach((key, value) -> settings.put((String) key, (String) value));
		settings.put("name", "configured-client");
		settings.put("roles", "legacy, shared");
		settings.put("groups", " shared,client,client");
		settings.put("durable-client-id", "durable-client_gem_first");
		settings.put("durable-client-timeout", "123");
		try (var connection = driver().connect(new CacheConfiguration(settings))) {
			var runtime = connection.getRuntime();
			var member = runtime.getMember();
			assertThat(runtime.getName()).isEqualTo("configured-client");
			assertThat(runtime.getLegacyId()).isZero();
			assertThat(member.getName()).isEqualTo(runtime.getName());
			assertThat(member.getGroups()).containsExactly("legacy", "shared", "client");
			var durable = member.getDurableClientAttributes();
			assertThat(durable.baseId()).isEqualTo("durable-client");
			assertThat(durable.poolName()).isEqualTo("first");
			assertThat(durable.timeout()).isEqualTo(123);
			assertThat(durable.withPoolName("second").withTimeout(456).id()).isEqualTo("durable-client_gem_second");
			assertThat(member.getDurableClientAttributes()).isEqualTo(durable);
			connection.close();
			assertThat(runtime.getName()).isEqualTo("configured-client");
			assertThat(runtime.getLegacyId()).isZero();
			assertThat(durable.id()).isEqualTo("durable-client_gem_first");
		}
	}

	@Test
	void statisticDescriptorsPreserveMetadataDefaultsAndExplicitFlags() {
		inCache(connection -> {
			var factory = connection.getRuntime().getStatisticsTypes();
			for (var type : ValueType.values()) {
				for (boolean counter : List.of(false, true)) {
					var defaults = factory.createDescriptor(new StatisticDescriptorConfiguration("alpha", null, null, type, counter));
					assertThat(defaults.getName()).isEqualTo("alpha");
					assertThat(defaults.getDescription()).isEmpty();
					assertThat(defaults.getUnit()).isEmpty();
					assertThat(defaults.getType()).isEqualTo(type);
					assertThat(defaults.isCounter()).isEqualTo(counter);
					assertThat(defaults.isLargerBetter()).isEqualTo(counter);
					assertThat(defaults.getId()).isEqualTo(-1);
					var explicit = factory.createDescriptor(new StatisticDescriptorConfiguration("beta", "elapsed", "ms", type, counter, !counter));
					assertThat(explicit.getDescription()).isEqualTo("elapsed");
					assertThat(explicit.getUnit()).isEqualTo("ms");
					assertThat(explicit.isLargerBetter()).isEqualTo(!counter);
					assertThat(defaults.compareTo(explicit)).isNegative();
					assertThat(explicit.compareTo(defaults)).isPositive();
				}
			}
		});
	}

	@Test
	void statisticTypeRegistryPreservesOrderingEqualityAndRejectsConflicts() {
		inCache(connection -> {
			var factory = connection.getRuntime().getStatisticsTypes();
			String name = "contract-" + UUID.randomUUID();
			assertThat(factory.findType(name)).isNull();
			var ratio = new StatisticDescriptorConfiguration("ratio", "ratio", "%", ValueType.DOUBLE, false);
			var count = new StatisticDescriptorConfiguration("count", "count", "operations", ValueType.LONG, true);
			var descriptors = new java.util.ArrayList<DriverStatisticDescriptor>(List.of(factory.createDescriptor(ratio), factory.createDescriptor(count)));
			var type = factory.createType(name, "description", descriptors);
			assertThat(type.getName()).isEqualTo(name);
			assertThat(type.getDescription()).isEqualTo("description");
			assertThat(type.getStatistics()).containsExactlyElementsOf(descriptors);
			assertThat(type.getStatistics()).extracting(DriverStatisticDescriptor::getId).containsExactly(1, 0);
			assertThat(type.nameToDescriptor("ratio")).isEqualTo(descriptors.get(0));
			assertThat(type.nameToId("count")).isZero();
			descriptors.clear();
			assertThat(type.getStatistics()).hasSize(2);
			assertThatThrownBy(() -> type.getStatistics().clear()).isInstanceOf(UnsupportedOperationException.class);
			var equal = factory.createType(name, "description", List.of(factory.createDescriptor(ratio), factory.createDescriptor(count)));
			assertThat(equal).isEqualTo(type).hasSameHashCodeAs(type);
			assertThat(factory.findType(name)).isEqualTo(type);
			for (Runnable invalid : List.<Runnable>of(
				() -> factory.createType(name, "changed", List.of(factory.createDescriptor(ratio), factory.createDescriptor(count))),
				() -> factory.createType(name, "description", List.of(factory.createDescriptor(count), factory.createDescriptor(ratio))),
				() -> type.nameToDescriptor("private-missing-name"), () -> type.nameToId("private-missing-name"))) {
				assertThatThrownBy(invalid::run).isInstanceOfSatisfying(DriverOperationException.class, failure -> {
					assertThat(failure.kind()).isEqualTo(DriverFailureKind.INVALID_ARGUMENT);
					assertThat(failure.getMessage()).doesNotContain(name, "private-missing-name");
				});
			}
			connection.close();
			assertThat(type.nameToId("ratio")).isEqualTo(1);
			assertThat(factory.findType(name)).isEqualTo(type);
		});
	}

	@Test
	void reusingStatisticDescriptorsRetainsNativeLiveIdSemantics() {
		inCache(connection -> {
			var factory = connection.getRuntime().getStatisticsTypes();
			var shared = factory.createDescriptor(new StatisticDescriptorConfiguration("shared", "", "", ValueType.DOUBLE, true));
			var first = factory.createType("first-" + UUID.randomUUID(), "", List.of(shared));
			assertThat(shared.getId()).isZero();
			var other = factory.createDescriptor(new StatisticDescriptorConfiguration("other", "", "", ValueType.LONG, true));
			factory.createType("second-" + UUID.randomUUID(), "", List.of(shared, other));
			assertThat(shared.getId()).isEqualTo(1);
			assertThat(first.nameToId("shared")).isEqualTo(1);
			assertThat(first.getStatistics().get(0).getId()).isEqualTo(1);
		});
	}

	@Test
	void statisticTypeValidationDoesNotRegisterInvalidDefinitions() {
		inCache(connection -> {
			var factory = connection.getRuntime().getStatisticsTypes();
			String name = "invalid-" + UUID.randomUUID();
			var descriptors = new java.util.ArrayList<DriverStatisticDescriptor>();
			for (int i = 0; i < 255; i++) descriptors.add(factory.createDescriptor(
				new StatisticDescriptorConfiguration("value" + i, "", "", ValueType.LONG, true)));
			for (Runnable invalid : List.<Runnable>of(
				() -> factory.createType(name, "", descriptors),
				() -> factory.createType(name, "", List.of(descriptors.get(0), descriptors.get(0))),
				() -> factory.createType(null, "", List.of()),
				() -> factory.createType(name, "", null), () -> factory.createDescriptor(null))) {
				assertThatThrownBy(invalid::run).isInstanceOfSatisfying(DriverOperationException.class,
					failure -> assertThat(failure.kind()).isEqualTo(DriverFailureKind.INVALID_ARGUMENT));
				assertThat(factory.findType(name)).isNull();
			}
			assertThat(factory.createType(name, "", descriptors.subList(0, 254)).getStatistics()).hasSize(254);
		});
	}

	@Test
	void runtimeLogUsesEffectiveLevelAliases() {
		inCache(connection -> {
			var log = connection.getRuntime().getLog();
			for (var level : DriverLog.Level.values()) {
				assertThat(log.isEnabled(level)).as("configured error threshold: %s", level)
					.isEqualTo(level == DriverLog.Level.ERROR || level == DriverLog.Level.SEVERE || level == DriverLog.Level.OFF);
			}
			assertThat(connection.getRuntime().getSecurityLog()).isNotNull();
		});
	}

	@Test
	void loggingRejectsNullLevelsWithClassifiedFailures() {
		inCache(connection -> {
			for (var log : List.of(connection.getRuntime().getLog(), connection.getRuntime().getSecurityLog())) {
				for (Runnable operation : List.<Runnable>of(() -> log.isEnabled(null),
					() -> log.log(null, "private-message", new IllegalStateException("private-cause")),
					() -> log.logLocalized(null, null, null, null))) {
					assertThatThrownBy(operation::run).isInstanceOfSatisfying(DriverOperationException.class,
						failure -> {
							assertThat(failure.kind()).isEqualTo(DriverFailureKind.INVALID_ARGUMENT);
							assertThat(failure.getMessage()).doesNotContain("private-message", "private-cause");
						});
				}
			}
		});
	}

	@Test
	void messageValuesRetainIdentityRawTextAndExplicitFormatting() {
		inCache(connection -> {
			for (var log : List.of(connection.getRuntime().getLog(), connection.getRuntime().getSecurityLog())) {
				var message = log.createMessage(Integer.MAX_VALUE, "private-pattern ''{0}'' {1}");
				assertThat(message.id()).isEqualTo(Integer.MAX_VALUE);
				assertThat(message.rawText()).isEqualTo("private-pattern ''{0}'' {1}");
				assertThat(message.format("value", 42)).isEqualTo("private-pattern 'value' 42");
				assertThat(message.format()).isEqualTo("private-pattern '{0}' {1}");
				assertThat(message.format((Object[]) null)).isEqualTo(message.format());
				assertThat(message.toString()).doesNotContain("private-pattern");
				assertThat(log.createMessage(1, "{0}").format("literal {0}")).isEqualTo("literal {0}");
				assertThat(log.createMessage(-1, null).rawText()).isNull();
				assertThatThrownBy(() -> log.createMessage(-1, null).format()).isInstanceOf(NullPointerException.class);
			}
		});
	}

	@Test
	void localizedLoggingDefersArgumentsAndClassifiesFailuresWithoutLeakingText() {
		inCache(connection -> {
			var log = connection.getRuntime().getLog();
			var fatal = new LinkageError("private-fatal");
			var explosive = new Object() { @Override public String toString() { throw fatal; } };
			log.logLocalized(DriverLog.Level.INFO, null, null, null);
			log.logLocalized(DriverLog.Level.INFO, log.createMessage(1, "{"), null, null);
			log.logLocalized(DriverLog.Level.INFO, log.createMessage(1, "{0}"), new Object[] {explosive}, null);
			assertThatThrownBy(() -> log.logLocalized(DriverLog.Level.ERROR,
				log.createMessage(1, "{0}"), new Object[] {explosive}, null)).isSameAs(fatal);
			for (DriverLogMessage message : new DriverLogMessage[] {null, log.createMessage(1, "{private-pattern")}) {
				assertThatThrownBy(() -> log.logLocalized(DriverLog.Level.ERROR, message, null, null))
					.isInstanceOfSatisfying(DriverOperationException.class, failure -> {
						assertThat(failure.kind()).isEqualTo(DriverFailureKind.INVALID_ARGUMENT);
						assertThat(failure.getCause()).isNotNull();
						assertThat(failure.getMessage()).doesNotContain("private-pattern");
					});
			}
		});
	}

	protected Map<Method, Probe> probes() throws Exception {
		Map<Method, Probe> probes = new LinkedHashMap<>();
		probes.putAll(statisticsProbes());
		probes.put(DriverLog.class.getMethod("createMessage", int.class, String.class),
			new Probe("Log.createMessage", () -> inCache(c ->
				assertThat(c.getRuntime().getLog().createMessage(1, "{0}").format("probe")).isEqualTo("probe"))));
		probes.put(DriverLog.class.getMethod("logLocalized", DriverLog.Level.class, DriverLogMessage.class, Object[].class, Throwable.class),
			new Probe("Log.logLocalized", () -> inCache(c -> c.getRuntime().getLog()
				.logLocalized(DriverLog.Level.INFO, null, null, null))));
		probes.put(DriverLog.class.getMethod("getHandler"),
			new Probe("Log.getHandler", () -> inCache(c -> {
				var log = c.getRuntime().getLog();
				var first = log.getHandler(); var second = log.getHandler();
				try {
					assertThat(first).isNotSameAs(second);
					first.setLevel(java.util.logging.Level.OFF);
					assertThat(second.getLevel()).isEqualTo(java.util.logging.Level.ALL);
					second.publish(new java.util.logging.LogRecord(java.util.logging.Level.INFO, "filtered probe"));
				} finally { first.close(); second.close(); }
			})));
		probes.put(DriverClientRuntime.class.getMethod("getLog"),
			new Probe("ClientRuntime.getLog", () -> inCache(c -> assertThat(c.getRuntime().getLog()).isNotNull())));
		probes.put(DriverClientRuntime.class.getMethod("getSecurityLog"),
			new Probe("ClientRuntime.getSecurityLog", () -> inCache(c -> assertThat(c.getRuntime().getSecurityLog()).isNotNull())));
		probes.put(DriverLog.class.getMethod("isEnabled", DriverLog.Level.class),
			new Probe("Log.isEnabled", () -> inCache(c -> assertThat(c.getRuntime().getLog().isEnabled(DriverLog.Level.INFO)).isFalse())));
		probes.put(DriverLog.class.getMethod("log", DriverLog.Level.class, String.class, Throwable.class),
			new Probe("Log.log", () -> inCache(c -> c.getRuntime().getLog().log(DriverLog.Level.INFO, "probe", null))));
		probes.put(DriverLog.class.getMethod("entering", String.class, String.class),
			new Probe("Log.entering", () -> inCache(c -> c.getRuntime().getLog().entering("Probe", "method"))));
		probes.put(DriverLog.class.getMethod("exiting", String.class, String.class),
			new Probe("Log.exiting", () -> inCache(c -> c.getRuntime().getLog().exiting("Probe", "method"))));
		probes.put(DriverLog.class.getMethod("throwing", String.class, String.class, Throwable.class),
			new Probe("Log.throwing", () -> inCache(c -> c.getRuntime().getLog().throwing("Probe", "method", null))));
		probes.put(DriverClientRuntime.class.getMethod("getStatisticsTypes"),
			new Probe("ClientRuntime.getStatisticsTypes", () -> inCache(c -> assertThat(c.getRuntime().getStatisticsTypes()).isNotNull())));
		probes.put(DriverStatisticsTypes.class.getMethod("createDescriptor", StatisticDescriptorConfiguration.class),
			new Probe("StatisticsTypes.createDescriptor", () -> inCache(c -> assertThat(c.getRuntime().getStatisticsTypes().createDescriptor(
				new StatisticDescriptorConfiguration("value", "", "", ValueType.LONG, true)).getId()).isEqualTo(-1))));
		probes.put(DriverStatisticsTypes.class.getMethod("createType", String.class, String.class, List.class),
			new Probe("StatisticsTypes.createType", () -> inCache(c -> assertThat(c.getRuntime().getStatisticsTypes().createType(
				"probe-" + UUID.randomUUID(), "", List.of()).getStatistics()).isEmpty())));
		probes.put(DriverStatisticsTypes.class.getMethod("findType", String.class),
			new Probe("StatisticsTypes.findType", () -> inCache(c -> assertThat(c.getRuntime().getStatisticsTypes().findType(
				"absent-" + UUID.randomUUID())).isNull())));
		probes.put(GemFireDriver.class.getMethod("resolvePropertiesFile"),
			new Probe("Driver.resolvePropertiesFile", () -> {
				var file = driver().resolvePropertiesFile();
				assertThat(file.fileName()).isNotNull();
				assertThat(file.location()).isNotNull();
				assertThat(file.toString()).isEqualTo("DriverPropertyFile[details=<redacted>]");
			}));
		probes.put(GemFireDriver.class.getMethod("resolveSecurityPropertiesFile"),
			new Probe("Driver.resolveSecurityPropertiesFile", () -> {
				var file = driver().resolveSecurityPropertiesFile();
				assertThat(file.fileName()).isNotNull();
				assertThat(file.location()).isNotNull();
				assertThat(file.toString()).isEqualTo("DriverPropertyFile[details=<redacted>]");
			}));
		probes.put(DriverClientRuntime.class.getMethod("getName"),
			new Probe("ClientRuntime.getName", () -> inCache(c -> assertThat(c.getRuntime().getName()).isNotNull())));
		probes.put(DriverClientRuntime.class.getMethod("getLegacyId"),
			new Probe("ClientRuntime.getLegacyId", () -> inCache(c -> assertThat(c.getRuntime().getLegacyId()).isZero())));
		probes.put(DriverClientRuntime.class.getMethod("getProperties"),
			new Probe("ClientRuntime.getProperties", () -> inCache(c ->
				assertThat(c.getRuntime().getProperties().properties()).containsEntry("log-level", "error"))));
		probes.put(DriverClientRuntime.class.getMethod("getSecurityProperties"),
			new Probe("ClientRuntime.getSecurityProperties", () -> inCache(c ->
				assertThat(c.getRuntime().getSecurityProperties().properties()).isNotNull())));
		probes.put(DriverClientRuntime.class.getMethod("noCancellation"),
			new Probe("ClientRuntime.noCancellation", () -> inCache(c -> {
				var cancellation = c.getRuntime().noCancellation();
				assertCancellationClear(cancellation);
				c.close();
				assertCancellationClear(cancellation);
			})));
		probes.put(DriverClientRuntime.class.getMethod("createCancellation", BooleanSupplier.class),
			new Probe("ClientRuntime.createCancellation", () -> inCache(c -> {
				var runtime = c.getRuntime();
				assertThatThrownBy(() -> runtime.createCancellation(null)).isInstanceOf(NullPointerException.class);
				var cancelled = new AtomicBoolean();
				var cancellation = runtime.createCancellation(cancelled::get);
				var independent = runtime.createCancellation(() -> false);
				assertCancellationClear(cancellation);
				c.close();
				assertCancellationClear(cancellation);
				cancelled.set(true);
				assertThat(cancellation.isCancelInProgress()).isTrue();
				assertThat(cancellation.cancelInProgress()).isNotBlank();
				Throwable cause = new IllegalStateException("private-cause");
				var failure = (DriverOperationException) cancellation.generateCancelledException(cause);
				assertThat(failure.kind()).isEqualTo(DriverFailureKind.CANCELLED);
				assertThat(failure.getRootCause()).isSameAs(cause);
				assertThat(failure.getMessage()).doesNotContain("private-cause");
				assertThatThrownBy(() -> cancellation.checkCancelInProgress(cause))
					.isInstanceOfSatisfying(DriverOperationException.class, ex -> {
						assertThat(ex.kind()).isEqualTo(DriverFailureKind.CANCELLED);
						assertThat(ex.getRootCause()).isSameAs(cause);
					});
				assertThatThrownBy(cancellation::checkCancelInProgress)
					.isInstanceOfSatisfying(DriverOperationException.class,
						ex -> assertThat(ex.kind()).isEqualTo(DriverFailureKind.CANCELLED));
				assertCancellationClear(independent);
			})));
		probes.put(DriverClientRuntime.class.getMethod("getCancellation"),
			new Probe("ClientRuntime.getCancellation", () -> inCache(c -> {
				DriverCancellation systemCancellation = c.getRuntime().getCancellation();
				assertThat(systemCancellation).isNotSameAs(c.getCancellation());
				assertThat(systemCancellation.cancelInProgress()).isNull();
				assertThat(systemCancellation.isCancelInProgress()).isFalse();
				systemCancellation.checkCancelInProgress();
				systemCancellation.checkCancelInProgress(new IllegalStateException("private-diagnostic-token"));
				assertThat(systemCancellation.generateCancelledException(null)).isNull();
			})));
		probes.put(DriverConnection.class.getMethod("getRuntime"),
			new Probe("Connection.getRuntime", () -> inCache(c -> assertThat(c.getRuntime()).isNotNull())));
		probes.put(DriverConnection.class.getMethod("getCancellation"),
			new Probe("Connection.getCancellation", () -> inCache(c -> assertThat(c.getCancellation()).isNotNull())));
		probes.put(DriverClientRuntime.class.getMethod("isConnected"),
			new Probe("ClientRuntime.isConnected", () -> inCache(c -> assertThat(c.getRuntime().isConnected()).isTrue())));
		probes.put(DriverClientRuntime.class.getMethod("getMember"),
			new Probe("ClientRuntime.getMember", () -> inCache(c -> assertThat(c.getRuntime().getMember().getId()).isNotBlank())));
		probes.put(DriverCancellation.class.getMethod("cancelInProgress"),
			new Probe("Cancellation.cancelInProgress", () -> inCache(c -> assertThat(c.getCancellation().cancelInProgress()).isNull())));
		probes.put(DriverCancellation.class.getMethod("isCancelInProgress"),
			new Probe("Cancellation.isCancelInProgress", () -> inCache(c -> assertThat(c.getCancellation().isCancelInProgress()).isFalse())));
		probes.put(DriverCancellation.class.getMethod("checkCancelInProgress"),
			new Probe("Cancellation.checkCancelInProgress", () -> inCache(c -> c.getCancellation().checkCancelInProgress())));
		probes.put(DriverCancellation.class.getMethod("checkCancelInProgress", Throwable.class),
			new Probe("Cancellation.checkCancelInProgressWithCause", () -> inCache(c -> c.getCancellation().checkCancelInProgress(new IllegalStateException("diagnostic")))));
		probes.put(DriverCancellation.class.getMethod("generateCancelledException", Throwable.class),
			new Probe("Cancellation.generateCancelledException", () -> inCache(c -> assertThat(c.getCancellation().generateCancelledException(null)).isNull())));
		probes.put(DriverConnection.class.getMethod("rootRegions"),
			new Probe("Connection.rootRegions", () -> inCache(c -> {
				assertThat(c.rootRegions()).isEmpty();
				c.createClientRegion("created", ClientRegionShortcut.LOCAL);
				assertThat(c.rootRegions()).extracting(DriverRegion::name).containsExactly("created");
			})));
		probes.put(DriverRegion.class.getMethod("isDestroyed"), new Probe("Region.isDestroyed", () ->
			inCache(c -> {
				DriverRegion<Object, Object> region = c.createClientRegion("created", ClientRegionShortcut.LOCAL);
				assertThat(region.isDestroyed()).isFalse();
				c.close();
				assertThat(region.isDestroyed()).isTrue();
			})));
		probes.put(DriverRegion.class.getMethod("clear"), new Probe("Region.clear", () ->
			inRegion(r -> {
				r.put("key", "value");
				r.clear();
				assertThat(r.size()).isZero();
				assertThat(r.get("key")).isNull();
			})));
		probes.put(DriverRegion.class.getMethod("putIfAbsent", Object.class, Object.class),
			new Probe("Region.putIfAbsent", () -> inRegion(r -> {
				assertThat(r.putIfAbsent("key", "first")).isNull();
				assertThat(r.putIfAbsent("key", "second")).isEqualTo("first");
				assertThat(r.get("key")).isEqualTo("first");
			})));
		probes.put(DriverConnection.class.getMethod("createClientRegion", String.class, ClientRegionShortcut.class),
			new Probe("Connection.createClientRegion", () -> inCache(c ->
				assertThat(c.createClientRegion("created", ClientRegionShortcut.LOCAL).name()).isEqualTo("created"))));
		probes.put(DriverConnection.class.getMethod("getRegion", String.class),
			new Probe("Connection.getRegion", () -> inCache(c -> {
				assertThat(c.getRegion("absent")).isNull();
				c.createClientRegion("created", ClientRegionShortcut.LOCAL);
				assertThat(c.getRegion("/created").name()).isEqualTo("created");
			})));
		probes.put(DriverConnection.class.getMethod("isClosed"),
			new Probe("Connection.isClosed", () -> inCache(c -> assertThat(c.isClosed()).isFalse())));
		probes.put(DriverConnection.class.getMethod("close"),
			new Probe("Connection.close", () -> inCache(c -> {
				c.close();
				assertThat(c.isClosed()).isTrue();
			})));
		probes.put(DriverRegion.class.getMethod("name"), new Probe("Region.name", () ->
			inRegion(r -> assertThat(r.name()).isEqualTo("entries"))));
		probes.put(DriverRegion.class.getMethod("get", Object.class), new Probe("Region.get", () ->
			inRegion(r -> { r.put("key", "value"); assertThat(r.get("key")).isEqualTo("value"); })));
		probes.put(DriverRegion.class.getMethod("put", Object.class, Object.class), new Probe("Region.put", () ->
			inRegion(r -> {
				assertThat(r.put("key", "first")).isNull();
				assertThat(r.put("key", "second")).isEqualTo("first");
			})));
		probes.put(DriverRegion.class.getMethod("remove", Object.class), new Probe("Region.remove", () ->
			inRegion(r -> { r.put("key", "value"); assertThat(r.remove("key")).isEqualTo("value"); })));
		probes.put(DriverRegion.class.getMethod("containsKey", Object.class), new Probe("Region.containsKey", () ->
			inRegion(r -> { r.put("key", "value"); assertThat(r.containsKey("key")).isTrue(); })));
		probes.put(DriverRegion.class.getMethod("size"), new Probe("Region.size", () ->
			inRegion(r -> { r.put("key", "value"); assertThat(r.size()).isEqualTo(1); })));
		probes.put(DriverRegion.class.getMethod("setCacheLoader", DriverCacheLoader.class),
			new Probe("Region.setCacheLoader", () -> inRegion(r -> {
				r.setCacheLoader(new RecordingLoader(h -> "loaded"));
				assertThat(r.get("key")).isEqualTo("loaded");
			})));
		probes.put(DriverRegion.class.getMethod("get", Object.class, Object.class),
			new Probe("Region.getWithArgument", () -> inRegion(r -> {
				r.setCacheLoader(new RecordingLoader(h -> (String) h.getArgument()));
				assertThat(r.get("key", "argument")).isEqualTo("argument");
			})));
		probes.put(DriverRegion.class.getMethod("addLifecycleListener", DriverRegionLifecycleListener.class),
			new Probe("Region.addLifecycleListener", () -> inRegion(r -> r.addLifecycleListener(new RecordingLifecycle()))));
		probes.put(DriverConnection.class.getMethod("createPool", String.class, PoolConfiguration.class),
			new Probe("Connection.createPool", () -> inPool(pool -> assertThat(pool.name()).isNotBlank())));
		probes.put(DriverConnection.class.getMethod("getPool", String.class),
			new Probe("Connection.getPool", () -> inCache(c -> {
				String name = UUID.randomUUID().toString();
				assertThat(c.getPool(name)).isNull();
				DriverPool pool = c.createPool(name, poolConfiguration(false));
				try { assertThat(c.getPool(name).name()).isEqualTo(name); }
				finally { pool.destroy(); }
			})));
		probes.put(DriverConnection.class.getMethod("getCurrentServers"), new Probe("Connection.getCurrentServers", () ->
			inCache(connection -> assertThat(connection.getCurrentServers()).isEmpty())));
		probes.put(DriverPool.class.getMethod("onlineLocators"), new Probe("Pool.onlineLocators", () ->
			inPool(pool -> assertThat(pool.onlineLocators()).isEmpty())));
		probes.put(DriverPool.class.getMethod("name"), new Probe("Pool.name", () ->
			inPool(pool -> assertThat(pool.name()).isNotBlank())));
		probes.put(DriverPool.class.getMethod("servers"), new Probe("Pool.servers", () ->
			inPool(pool -> assertThat(pool.servers()).extracting(Endpoint::port).containsExactly(40404))));
		probes.put(DriverPool.class.getMethod("locators"), new Probe("Pool.locators", () ->
			inPool(pool -> assertThat(pool.locators()).isEmpty())));
		probes.put(DriverPool.class.getMethod("minimumConnections"), new Probe("Pool.minimumConnections", () ->
			inPool(pool -> assertThat(pool.minimumConnections()).isZero())));
		probes.put(DriverPool.class.getMethod("isDestroyed"), new Probe("Pool.isDestroyed", () ->
			inPool(pool -> assertThat(pool.isDestroyed()).isFalse())));
		probes.put(DriverPool.class.getMethod("destroy"), new Probe("Pool.destroy", () ->
			inPool(pool -> { pool.destroy(); assertThat(pool.isDestroyed()).isTrue(); })));
		probes.put(DriverPool.class.getMethod("configuration"), new Probe("Pool.configuration", () ->
			inPool(pool -> { assertThat(pool.configuration().minimumConnections()).hasValue(0);
				assertThat(pool.configuration().servers()).isEqualTo(pool.servers()); })));
		probes.put(com.vmware.gemfire.driver.spi.DriverRegionService.class.getMethod("createPdxInstanceFactory", String.class),
			new Probe("RegionService.createPdxInstanceFactory", () -> inCache(c ->
				assertThat(c.createPdxInstanceFactory("Probe").writeInt("id", 1).create().getField("id")).isEqualTo(1))));
		probes.put(com.vmware.gemfire.driver.spi.DriverRegionService.class.getMethod("createPdxEnum", String.class, String.class, int.class),
			new Probe("RegionService.createPdxEnum", () -> inCache(c ->
				assertThat(c.createPdxEnum("ProbeEnum", "ONE", 0).getField("name")).isEqualTo("ONE"))));
		probes.putAll(transactionProbes());
		probes.putAll(callbackProbes());
		probes.putAll(regionOperationProbes());
		probes.putAll(regionConfigurationProbes());
		return probes;
	}

	@Test
	void localRegionCrudUsesSharedDataAcrossHandles() {
		inCache(c -> {
			DriverRegion<String, String> region = c.createClientRegion("entries", ClientRegionShortcut.LOCAL);
			assertThat(region.get("missing")).isNull();
			assertThat(region.containsKey("missing")).isFalse();
			assertThat(region.put("key", "first")).isNull();
			assertThat(c.<String, String>getRegion("entries").get("key")).isEqualTo("first");
			assertThat(region.put("key", "second")).isEqualTo("first");
			assertThat(region.size()).isEqualTo(1);
			assertThat(region.remove("key")).isEqualTo("second");
			assertThat(region.remove("key")).isNull();
			assertThat(region.size()).isZero();
		});
	}

	@Test
	void clientLocalRegionSupportsCrud() {
		inCache(c -> {
			DriverRegion<String, String> region = c.createClientRegion("entries", ClientRegionShortcut.LOCAL);
			region.put("key", "value");
			assertThat(region.get("key")).isEqualTo("value");
			assertThat(c.getRegion("entries").size()).isEqualTo(1);
		});
	}

	@Test
	void closedCacheAndExistingRegionHandleFailAtTheBoundary() {
		inCache(c -> {
			DriverRegion<String, String> region = c.createClientRegion("entries", ClientRegionShortcut.LOCAL);
			c.close();
			assertThat(c.isClosed()).isTrue();
			assertThatThrownBy(() -> c.getRegion("entries")).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> region.get("key")).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(c::rootRegions).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(region::clear).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> region.putIfAbsent("key", "value")).isInstanceOf(DriverOperationException.class);
		});
	}

	@Test
	void invalidOperationsUseSpiExceptions() {
		inCache(c -> {
			DriverRegion<String, String> region = c.createClientRegion("entries", ClientRegionShortcut.LOCAL);
			assertThatThrownBy(() -> c.createClientRegion("entries", ClientRegionShortcut.LOCAL))
				.isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> c.getRegion(null)).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> c.getRegion("")).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> c.createClientRegion("invalid", null)).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> region.get(null)).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> region.put("key", null)).isInstanceOf(DriverOperationException.class);
		});
	}

	@Test
	void rejectsUnknownConfigurationWithoutPrintingIt() {
		DriverConfiguration config = new DriverConfiguration() {
			@Override public String toString() { return "credential-secret"; }
		};
		assertThatThrownBy(() -> driver().connect(config)).isInstanceOf(DriverBindException.class)
			.hasMessageNotContaining("credential-secret");
	}

	@Test
	void loadersReceiveContextAndCacheOnlyNonNullResults() {
		inCache(c -> {
			DriverRegion<String, String> region = localRegion(c);
			RecordingLoader loader = new RecordingLoader(helper -> {
				assertThat(helper.getRegion().name()).isEqualTo("entries");
				helper.getRegion().put("side-effect", "visible");
				return "absent".equals(helper.getKey()) ? null : helper.getKey() + ":" + helper.getArgument();
			});
			region.setCacheLoader(loader);
			assertThat(region.get("key", "argument")).isEqualTo("key:argument");
			assertThat(region.get("key", "ignored")).isEqualTo("key:argument");
			assertThat(region.get("side-effect")).isEqualTo("visible");
			assertThat(loader.loads.get()).isEqualTo(1);
			assertThat(region.get("absent")).isNull();
			assertThat(region.get("absent")).isNull();
			assertThat(region.containsKey("absent")).isFalse();
			assertThat(loader.loads.get()).isEqualTo(3);
		});
	}

	@Test
	void loaderReplacementRemovalAndShutdownFollowLifecycle() {
		inCache(c -> {
			DriverRegion<String, String> region = localRegion(c);
			RecordingLoader first = new RecordingLoader(h -> "first");
			RecordingLoader second = new RecordingLoader(h -> "second");
			region.setCacheLoader(first);
			c.<String, String>getRegion("entries").setCacheLoader(first);
			assertThat(first.closes.get()).isZero();
			region.setCacheLoader(second);
			assertThat(first.closes.get()).isEqualTo(1);
			region.setCacheLoader(null);
			assertThat(second.closes.get()).isEqualTo(1);
			assertThat(region.get("missing")).isNull();
			RecordingLoader last = new RecordingLoader(h -> "last");
			region.setCacheLoader(last);
			c.close();
			c.close();
			assertThat(last.closes.get()).isEqualTo(1);
			assertThatThrownBy(() -> region.setCacheLoader(last)).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> region.get("key", "argument")).isInstanceOf(DriverOperationException.class);
		});
	}

	@Test
	void loaderFailuresKeepTheirCauseAndDoNotInsertValues() {
		inRegion(region -> {
			RuntimeException failure = new IllegalStateException("callback-secret");
			RecordingLoader loader = new RecordingLoader(h -> { throw failure; });
			region.setCacheLoader(loader);
			assertThatThrownBy(() -> region.get("key", "argument"))
				.isInstanceOf(DriverOperationException.class).hasRootCause(failure)
				.hasMessageNotContaining("callback-secret")
				.satisfies(ex -> assertThat(((DriverOperationException) ex).kind()).isEqualTo(DriverFailureKind.LOADER_FAILURE));
			assertThat(region.containsKey("key")).isFalse();
			assertThatThrownBy(() -> region.get(null, "argument")).isInstanceOf(DriverOperationException.class);
			assertThat(loader.loads.get()).isEqualTo(1);
		});
	}

	@Test
	void loaderUnsupportedAndTypedVetoAreNotReclassifiedOrTreatedAsMisses() {
		inRegion(region -> {
			for (RuntimeException failure : new RuntimeException[] {
				UnsupportedFeatureException.notIntroduced("loader.remote"),
				UnsupportedFeatureException.removed("loader.remote", expectedNativeVersion()),
				new DriverOperationException(DriverFailureKind.WRITER_VETO, "Application veto", null) }) {
				region.setCacheLoader(new RecordingLoader(helper -> { throw failure; }));
				assertThatThrownBy(() -> region.get("key")).isSameAs(failure);
				assertThat(region.containsKey("key")).isFalse();
			}
		});
	}

	@Test
	void duplicateRegionAndInvalidArgumentHaveDistinctSemantics() {
		inCache(connection -> {
			var region = connection.createClientRegion("entries", ClientRegionShortcut.LOCAL);
			assertThatThrownBy(() -> connection.createClientRegion("entries", ClientRegionShortcut.LOCAL))
				.isInstanceOfSatisfying(DriverOperationException.class,
					failure -> assertThat(failure.kind()).isEqualTo(DriverFailureKind.REGION_EXISTS));
			assertThatThrownBy(() -> region.get(null))
				.isInstanceOfSatisfying(DriverOperationException.class,
					failure -> assertThat(failure.kind()).isEqualTo(DriverFailureKind.INVALID_ARGUMENT));
		});
	}

	@Test
	void localLifecycleRegistrationIdentityAndCleanup() {
		inCache(c -> {
			DriverRegion<String, String> region = localRegion(c);
			RecordingLifecycle first = new RecordingLifecycle();
			RecordingLifecycle second = new RecordingLifecycle();
			region.addLifecycleListener(first);
			c.getRegion("entries").addLifecycleListener(first);
			region.addLifecycleListener(second);
			assertThat(first.events).isEmpty();
			assertThatThrownBy(() -> region.addLifecycleListener(null)).isInstanceOf(DriverOperationException.class);
			c.close();
			c.close();
			assertThat(first.events).containsExactly("destroy:entries", "close");
			assertThat(second.events).containsExactly("destroy:entries", "close");
			assertThatThrownBy(() -> region.addLifecycleListener(first)).isInstanceOf(DriverOperationException.class);
		});
	}

	@Test
	void callbackRuntimeFailuresDoNotSkipRemainingLifecycleCallbacks() {
		inCache(c -> {
			DriverRegion<String, String> region = localRegion(c);
			region.addLifecycleListener(new DriverRegionLifecycleListener() {
				@Override public void afterRegionDestroy(DriverRegion<?, ?> destroyed) {
					throw new IllegalStateException("expected callback failure");
				}
				@Override public void close() { throw new IllegalStateException("expected cleanup failure"); }
			});
			RecordingLifecycle listener = new RecordingLifecycle();
			region.addLifecycleListener(listener);
			c.close();
			assertThat(listener.events).containsExactly("destroy:entries", "close");
		});
	}

	@Test
	void olderRegionImplementationsRejectLifecycleRegistrationWithTypedFailure() {
		DriverRegion<Object, Object> older = new DriverRegion<>() { };
		assertThatThrownBy(() -> older.addLifecycleListener(new RecordingLifecycle()))
			.isInstanceOfSatisfying(com.vmware.gemfire.driver.spi.UnsupportedFeatureException.class, failure -> {
				assertThat(failure.feature()).isEqualTo("Region.addLifecycleListener");
				assertThat(failure.reason()).isEqualTo(Reason.NOT_INTRODUCED);
			});
	}

	private static class RecordingLifecycle implements DriverRegionLifecycleListener {
		private final java.util.List<String> events = new java.util.ArrayList<>();
		@Override public void afterRegionDestroy(DriverRegion<?, ?> region) { events.add("destroy:" + region.name()); }
		@Override public void close() { events.add("close"); }
		// Equal but distinct callbacks remain independent registrations.
		@Override public boolean equals(Object other) { return other instanceof RecordingLifecycle; }
		@Override public int hashCode() { return 1; }
	}

	private DriverRegion<String, String> localRegion(DriverConnection connection) {
		return connection.createClientRegion("entries", ClientRegionShortcut.LOCAL);
	}

	private static final class RecordingLoader implements DriverCacheLoader<String, String> {
		private final Function<DriverLoaderHelper<String, String>, String> action;
		private final AtomicInteger loads = new AtomicInteger();
		private final AtomicInteger closes = new AtomicInteger();
		RecordingLoader(Function<DriverLoaderHelper<String, String>, String> action) { this.action = action; }
		@Override public String load(DriverLoaderHelper<String, String> helper) {
			loads.incrementAndGet();
			return action.apply(helper);
		}
		@Override public void close() { closes.incrementAndGet(); }
	}

	@Test
	void namedPoolMetadataDuplicateRejectionAndRecreation() {
		for (boolean locators : new boolean[] { false, true }) {
			inCache(connection -> {
				String name = UUID.randomUUID().toString();
				PoolConfiguration config = poolConfiguration(locators);
				DriverPool first = connection.createPool(name, config);
				try {
					assertThat(first.name()).isEqualTo(name);
					assertThat(first.minimumConnections()).isZero();
					assertThat(locators ? first.locators() : first.servers())
						.extracting(Endpoint::port).containsExactly(locators ? 10334 : 40404);
					assertThat(locators ? first.servers() : first.locators()).isEmpty();
					assertThatThrownBy(() -> connection.createPool(name, config))
						.isInstanceOf(DriverOperationException.class);
					assertThat(first.isDestroyed()).isFalse();
					assertThat(connection.getPool(name).minimumConnections()).isZero();
				}
				finally { first.destroy(); }
				assertThat(first.isDestroyed()).isTrue();
				assertThat(connection.getPool(name)).isNull();
				DriverPool replacement = connection.createPool(name, config);
				try {
					first.destroy(); // A stale handle must not remove the new pool.
					assertThat(connection.getPool(name).isDestroyed()).isFalse();
				}
				finally { replacement.destroy(); }
			});
		}
	}

	@Test
	void poolAccessRejectsClosedConnectionAndInvalidArguments() {
		inCache(connection -> {
			PoolConfiguration config = poolConfiguration(false);
				for (String name : new String[] { null, "", "  " }) {
					assertThatThrownBy(() -> connection.createPool(name, config)).isInstanceOf(DriverOperationException.class);
					assertThatThrownBy(() -> connection.getPool(name)).isInstanceOf(DriverOperationException.class);
				}
				assertThatThrownBy(() -> connection.createPool("null", null)).isInstanceOf(DriverOperationException.class);
			connection.close();
			assertThatThrownBy(() -> connection.createPool("closed", config)).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(() -> connection.getPool("closed")).isInstanceOf(DriverOperationException.class);
		});
	}

	private PoolConfiguration poolConfiguration(boolean locators) {
		return new PoolConfiguration(locators ? List.of() : List.of(new Endpoint("127.0.0.1", 40404)),
			locators ? List.of(new Endpoint("127.0.0.1", 10334)) : List.of(), 0);
	}

	private void inPool(Consumer<DriverPool> action) {
		inCache(connection -> {
			DriverPool pool = connection.createPool(UUID.randomUUID().toString(), poolConfiguration(false));
			try { action.accept(pool); }
			finally { pool.destroy(); }
		});
	}

	private static void assertCancellationClear(DriverCancellation cancellation) {
		assertThat(cancellation.cancelInProgress()).isNull();
		assertThat(cancellation.isCancelInProgress()).isFalse();
		cancellation.checkCancelInProgress();
		Throwable cause = new IllegalStateException("private-cause");
		cancellation.checkCancelInProgress(cause);
		assertThat(cancellation.generateCancelledException(cause)).isNull();
	}

	private void inCache(Consumer<DriverConnection> action) {
		try (DriverConnection connection = connect()) {
			action.accept(connection);
		}
	}

	private void inRegion(Consumer<DriverRegion<String, String>> action) {
		inCache(c -> action.accept(c.createClientRegion("entries", ClientRegionShortcut.LOCAL)));
	}
}
