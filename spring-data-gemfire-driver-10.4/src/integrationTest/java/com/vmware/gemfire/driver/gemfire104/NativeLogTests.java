/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.geode.LogWriter;
import org.apache.geode.CancelCriterion;
import org.apache.geode.distributed.DistributedSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.vmware.gemfire.driver.spi.CacheConfiguration;
import com.vmware.gemfire.driver.spi.DriverLog.Level;
import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.DriverOperationException;

class NativeLogTests {

	@TempDir Path directory;

	@Test
	void suppliedJapaneseCatalogSurvivesDefaultLocaleChanges() throws Exception {
		assertLocaleProcess("ja", "JP", true);
	}

	@Test
	void shippedJapaneseLocaleKeepsRawFallbackAfterDefaultLocaleChanges() throws Exception {
		assertLocaleProcess("ja", "JP", false);
	}

	@Test
	void missingFrenchCatalogKeepsRawFallbackAfterDefaultLocaleChanges() throws Exception {
		assertLocaleProcess("fr", "FR", false);
	}

	private void assertLocaleProcess(String language, String country, boolean suppliedCatalog) throws Exception {
		var classpath = System.getProperty("nativeFixtureClasspath");
		assertThat(classpath).as("integrationTest runtime classpath").isNotBlank();
		if (suppliedCatalog) {
			// The pinned GemFire JAR omits the source-tree catalog. Supply a minimal fixture
			// only to this child process, so fallback tests retain the shipped classpath.
			var resources = directory.resolve("catalog-fixture");
			var catalog = resources.resolve("org/apache/geode/internal/i18n/StringIdResourceBundle_ja.txt");
			Files.createDirectories(catalog.getParent());
			Files.writeString(catalog, "1028 = {0}を処理しています\n");
			classpath = resources + java.io.File.pathSeparator + classpath;
		}
		var output = directory.resolve(language + "-locale-output.txt");
		var process = new ProcessBuilder(Path.of(System.getProperty("java.home"), "bin", "java").toString(),
			"--add-exports=java.base/sun.nio.ch=ALL-UNNAMED",
			"--add-opens=java.base/java.lang=ALL-UNNAMED", "--add-opens=java.base/java.nio=ALL-UNNAMED",
			"-Dfile.encoding=UTF-8", "-Duser.language=" + language, "-Duser.country=" + country,
			"-Duser.language.format=" + language, "-Duser.country.format=" + country,
			"-cp", classpath, LocaleProbe.class.getName(), language, Boolean.toString(suppliedCatalog))
			.directory(directory.toFile()).redirectErrorStream(true).redirectOutput(output.toFile()).start();
		try {
			assertThat(process.waitFor(30, TimeUnit.SECONDS)).as("locale process completed: %s", output).isTrue();
			assertThat(process.exitValue()).as("locale process output: %s", Files.readString(output)).isZero();
		} finally {
			if (process.isAlive()) {
				process.destroyForcibly();
				assertThat(process.waitFor(10, TimeUnit.SECONDS)).as("locale process terminated").isTrue();
			}
		}
	}

	/** Changes JVM-wide locale only in a disposable child process. */
	public static class LocaleProbe {
		public static void main(String[] args) {
			boolean translated = Boolean.parseBoolean(args[1]);
			var initialLocale = args[0].equals("ja") ? Locale.JAPAN : Locale.FRANCE;
			assertThat(Locale.getDefault()).isEqualTo(initialLocale);
			var catalog = LocaleProbe.class.getClassLoader().getResource(
				"org/apache/geode/internal/i18n/StringIdResourceBundle_" + args[0] + ".txt");
			if (translated) assertThat(catalog).as("supplied test catalog").isNotNull();
			else assertThat(catalog).as("no catalog on the shipped runtime classpath").isNull();
			// Initialize the native catalog before creating the adapter or changing the locale.
			var nativeMessage = new org.apache.geode.i18n.StringId(1028, "Processing {0}");
			var nativeNumber = new org.apache.geode.i18n.StringId(Integer.MAX_VALUE, "value {0,number,0.00}");
			var writer = mock(LogWriter.class);
			when(writer.warningEnabled()).thenReturn(true);
			var log = new GemFireLog(writer);
			var message = log.createMessage(1028, "Processing {0}");
			var number = log.createMessage(Integer.MAX_VALUE, "value {0,number,0.00}");
			var prefix = translated ? "msgID " + Integer.MAX_VALUE + ": " : "";
			var expected = translated ? "msgID 1028: caféを処理しています" : "Processing café";
			for (var locale : initialLocale.equals(Locale.JAPAN) ? new Locale[] {Locale.JAPAN, Locale.FRANCE}
					: new Locale[] {Locale.FRANCE, Locale.JAPAN}) {
				Locale.setDefault(locale);
				assertThat(nativeMessage.toLocalizedString("café")).isEqualTo(expected);
				assertThat(message.formatLocalized("café")).isEqualTo(expected);
				assertThat(message.rawText()).isEqualTo("Processing {0}");
				assertThat(message.format("café")).isEqualTo("Processing café");
				var numericText = locale.equals(Locale.FRANCE) ? "value 12,50" : "value 12.50";
				assertThat(nativeNumber.toLocalizedString(12.5)).isEqualTo(prefix + numericText);
				assertThat(number.formatLocalized(12.5)).isEqualTo(prefix + numericText);
				assertThat(number.format(12.5)).isEqualTo(numericText);
				var cause = new IllegalStateException("locale-cause");
				log.logLocalized(Level.WARNING, number, new Object[] {12.5}, cause);
				verify(writer).warning(prefix + numericText, cause);
			}
		}
	}

	@Test
	void nativeConversionsAndUnifiedSpiPreserveDestinationsThresholdsAndCauses() throws Exception {
		var file = directory.resolve("conversion.log");
		var securityFile = directory.resolve("conversion-security.log");
		var properties = new java.util.Properties();
		properties.putAll(Map.of("mcast-port", "0", "locators", "", "log-level", "warning",
			"log-file", file.toString(), "security-log-level", "error", "security-log-file", securityFile.toString()));
		var cache = new org.apache.geode.cache.client.ClientCacheFactory(properties).create();
		try {
			var system = cache.getDistributedSystem();
			var runtime = new GemFireClientRuntime(system);
			var writers = java.util.List.of(system.getLogWriter(), system.getSecurityLogWriter());
			var logs = java.util.List.of(runtime.getLog(), runtime.getSecurityLog());
			for (int channel = 0; channel < logs.size(); channel++) {
				var writer = writers.get(channel);
				var localized = writer.convertToLogWriterI18n();
				assertThat(localized).isSameAs(writer);
				assertThat(localized.convertToLogWriter()).isSameAs(writer);
				var log = logs.get(channel);
				assertThat(log.isEnabled(Level.WARNING)).isEqualTo(channel == 0);
				assertThat(localized.warningEnabled()).isEqualTo(log.isEnabled(Level.WARNING));
				var marker = "conversion-channel-" + channel;
				var cause = new IllegalStateException(marker + "-cause");
				writer.error(marker + "-native-plain", cause);
				localized.error(new org.apache.geode.i18n.StringId(Integer.MAX_VALUE, "{0}-native-localized"),
					new Object[] {marker}, cause);
				log.log(Level.ERROR, marker + "-spi-plain", cause);
				log.logLocalized(Level.ERROR, log.createMessage(Integer.MAX_VALUE, "{0}-spi-localized"),
					new Object[] {marker}, cause);
				writer.info(marker + "-filtered-native-plain");
				localized.info(new org.apache.geode.i18n.StringId(Integer.MAX_VALUE, "{0}-filtered-native-localized"),
					new Object[] {marker});
				log.log(Level.INFO, marker + "-filtered-spi-plain", null);
				log.logLocalized(Level.INFO, log.createMessage(Integer.MAX_VALUE, "{0}-filtered-spi-localized"),
					new Object[] {marker}, null);
			}
		} finally { cache.close(); }
		var files = java.util.List.of(file, securityFile);
		for (int channel = 0; channel < files.size(); channel++) {
			var marker = "conversion-channel-" + channel;
			assertThat(Files.readString(files.get(channel)))
				.contains(marker + "-native-plain", marker + "-native-localized", marker + "-spi-plain",
					marker + "-spi-localized", marker + "-cause")
				.doesNotContain(marker + "-filtered", "conversion-channel-" + (1 - channel));
		}
	}

	@Test
	void messageFormattingMatchesNativeIdentityRawTextAndLocalizedFallback() {
		var log = new GemFireLog(mock(LogWriter.class));
		for (int id : new int[] {1, Integer.MAX_VALUE}) {
			for (String pattern : new String[] {"{0}", "quoted ''{0}'' {1}", "plain café", "{0,number,#.00}"}) {
				var nativeMessage = new org.apache.geode.i18n.StringId(id, pattern);
				var message = log.createMessage(id, pattern);
				assertThat(message.id()).isEqualTo(nativeMessage.id);
				assertThat(message.rawText()).isEqualTo(nativeMessage.getRawText());
				assertThat(message.format()).isEqualTo(nativeMessage.toString());
				assertThat(message.formatLocalized()).isEqualTo(nativeMessage.toLocalizedString());
				for (Object[] parameters : new Object[][] {null, {}, {12.5, "value"}}) {
					assertThat(message.format(parameters)).isEqualTo(nativeMessage.toString(parameters));
					assertThat(message.formatLocalized(parameters)).isEqualTo(nativeMessage.toLocalizedString(parameters));
				}
			}
		}
	}

	@Test
	void localizedPublicationUsesNativeFormattingAndPreservesTheCause() {
		var writer = mock(LogWriter.class);
		when(writer.warningEnabled()).thenReturn(true);
		var log = new GemFireLog(writer);
		var cause = new IllegalStateException("localized-cause");
		var message = log.createMessage(Integer.MAX_VALUE, "localized {0}");
		log.logLocalized(Level.WARNING, message, new Object[] {"value"}, cause);
		verify(writer).warning(new org.apache.geode.i18n.StringId(Integer.MAX_VALUE,
			"localized {0}").toLocalizedString("value"), cause);
	}

	@Test
	void defaultFormatterMatchesNativeLayoutForRawMessagesAndThrowableTraces() {
		var actual = new GemFireLog(mock(LogWriter.class)).getHandler().getFormatter();
		var expected = new org.apache.geode.internal.logging.GemFireFormatter();
		var cause = new IllegalStateException("formatter-failure", new IllegalArgumentException("formatter-cause"));
		cause.addSuppressed(new IllegalStateException("formatter-suppressed"));
		String[] messages = {null, "", "bundle-key", "raw {0}", "first\nsecond\n", "first\r\nsecond",
			"first\rsecond", "tab\tvalue", "Unicode café 日本語", "long ".repeat(100)};
		var levels = new java.util.logging.Level[] {java.util.logging.Level.ALL, java.util.logging.Level.INFO,
			java.util.logging.Level.OFF, new java.util.logging.Level("CUSTOM", 850) {}};
		for (var level : levels) {
			for (String message : messages) {
				var record = new java.util.logging.LogRecord(level, message);
				record.setMillis(1234567890000L); record.setThreadID(123); record.setSequenceNumber(456);
				record.setParameters(new Object[] {"resolved"});
				record.setResourceBundle(new java.util.ListResourceBundle() {
					@Override protected Object[][] getContents() { return new Object[][] {{"bundle-key", "hello {0}"}}; }
				});
				assertThat(actual.format(record)).as("%s / %s", level, message).isEqualTo(expected.format(record));
				record.setThrown(cause);
				assertThat(actual.format(record)).as("%s / %s with throwable", level, message).isEqualTo(expected.format(record));
				if (message != null) assertThat(actual.formatMessage(record)).isEqualTo(expected.formatMessage(record));
			}
		}
	}

	@Test
	void julBridgeWritesFormattedRecordsToNativeSystemAndSecurityDestinations() throws Exception {
		var file = directory.resolve("jul.log");
		var securityFile = directory.resolve("jul-security.log");
		var config = new CacheConfiguration(Map.of("mcast-port", "0", "locators", "", "log-level", "warning",
			"log-file", file.toString(), "security-log-level", "warning", "security-log-file", securityFile.toString()));
		try (var connection = new GemFire104Driver().connect(config)) {
			var handler = connection.getRuntime().getLog().getHandler();
			var security = connection.getRuntime().getSecurityLog().getHandler();
			try {
				handler.publish(new java.util.logging.LogRecord(java.util.logging.Level.INFO, "destination-filtered-marker"));
				handler.setLevel(java.util.logging.Level.SEVERE);
				handler.publish(new java.util.logging.LogRecord(java.util.logging.Level.WARNING, "handler-filtered-marker"));
				handler.setLevel(java.util.logging.Level.ALL);
				var record = new java.util.logging.LogRecord(java.util.logging.Level.WARNING, "jul-message {0}");
				record.setParameters(new Object[] {"marker"}); record.setThreadID(7); record.setSequenceNumber(8);
				record.setThrown(new IllegalStateException("jul-cause-marker"));
				handler.publish(record); handler.flush(); handler.close();
				for (var log : java.util.List.of(connection.getRuntime().getLog(), connection.getRuntime().getSecurityLog())) {
					log.logLocalized(Level.WARNING, log.createMessage(Integer.MAX_VALUE, "localized-destination {0}"),
						new Object[] {"marker"}, new IllegalStateException("localized-destination-cause"));
				}
				handler.publish(new java.util.logging.LogRecord(java.util.logging.Level.SEVERE, "closed-handler-marker"));
				security.publish(new java.util.logging.LogRecord(java.util.logging.Level.WARNING, "jul-security-marker"));
				connection.getRuntime().getLog().log(Level.WARNING, "after-handler-close-marker", null);
			} finally { handler.close(); security.close(); }
		}
		assertThat(Files.readString(file)).contains("localized-destination marker", "localized-destination-cause", "(tid=7 msgId=8) jul-message marker", "jul-cause-marker", "after-handler-close-marker")
			.doesNotContain("destination-filtered-marker", "handler-filtered-marker", "closed-handler-marker", "jul-security-marker");
		assertThat(Files.readString(securityFile)).contains("jul-security-marker", "localized-destination marker", "localized-destination-cause").doesNotContain("jul-message marker");
	}

	@Test
	void extremeRecordsMatchNativeHandlersForSystemAndSecurityDestinations() throws Exception {
		for (var threshold : java.util.List.of("all", "info", "none")) {
			var file = directory.resolve(threshold + ".log");
			var securityFile = directory.resolve(threshold + "-security.log");
			var properties = new java.util.Properties();
			properties.putAll(Map.of("mcast-port", "0", "locators", "", "log-level", threshold,
				"log-file", file.toString(), "security-log-level", threshold, "security-log-file", securityFile.toString()));
			var cache = new org.apache.geode.cache.client.ClientCacheFactory(properties).create();
			try {
				var system = cache.getDistributedSystem();
				var runtime = new GemFireClientRuntime(system);
				var writers = java.util.List.of(system.getLogWriter(), system.getSecurityLogWriter());
				var logs = java.util.List.of(runtime.getLog(), runtime.getSecurityLog());
				for (int channel = 0; channel < logs.size(); channel++) {
					var log = logs.get(channel);
					assertThat(log.isEnabled(Level.ALL)).as("%s channel %s", threshold, channel).isEqualTo(threshold.equals("all"));
					assertThat(log.isEnabled(Level.OFF)).as("%s channel %s", threshold, channel).isTrue();
					var handlers = java.util.List.of(writers.get(channel).getHandler(), log.getHandler());
					try {
						for (int bridge = 0; bridge < handlers.size(); bridge++) {
							var handler = handlers.get(bridge);
							var marker = "channel-" + channel + "-bridge-" + bridge;
							handler.publish(new java.util.logging.LogRecord(java.util.logging.Level.ALL, marker + "-all-marker"));
							var off = new java.util.logging.LogRecord(java.util.logging.Level.OFF, marker + "-off-marker");
							off.setThrown(new IllegalStateException(marker + "-cause-marker"));
							handler.publish(off);
							handler.setLevel(java.util.logging.Level.OFF);
							handler.publish(new java.util.logging.LogRecord(java.util.logging.Level.OFF, marker + "-filtered-marker"));
						}
					} finally { handlers.forEach(java.util.logging.Handler::close); }
				}
			} finally { cache.close(); }
			var files = java.util.List.of(file, securityFile);
			for (int channel = 0; channel < files.size(); channel++) {
				var contents = Files.readString(files.get(channel));
				for (int bridge = 0; bridge < 2; bridge++) {
					var marker = "channel-" + channel + "-bridge-" + bridge;
					assertThat(contents).as("%s channel %s", threshold, channel)
						.contains(marker + "-off-marker", marker + "-cause-marker")
						.doesNotContain(marker + "-filtered-marker", "channel-" + (1 - channel) + "-bridge-");
					if (threshold.equals("all")) assertThat(contents).contains(marker + "-all-marker");
					else assertThat(contents).doesNotContain(marker + "-all-marker");
				}
			}
		}
	}

	@Test
	void forwardsOrdinaryLevelsAndNullablePayloadWithoutRewriting() throws Exception {
		var writer = mock(LogWriter.class);
		var log = new GemFireLog(writer);
		var cause = new IllegalStateException("private-cause");
		String[] names = {"finest", "finer", "fine", "config", "info", "warning", "error", "severe"};
		for (int i = 0; i < names.length; i++) {
			var level = Level.values()[i];
			var enabled = LogWriter.class.getMethod(names[i] + "Enabled");
			when((Boolean) enabled.invoke(writer)).thenReturn(true);
			assertThat(log.isEnabled(level)).isTrue();
			when((Boolean) enabled.invoke(writer)).thenReturn(false);
			assertThat(log.isEnabled(level)).isFalse();
			var emit = LogWriter.class.getMethod(names[i], String.class, Throwable.class);
			log.log(level, "private-message", cause);
			emit.invoke(verify(writer), "private-message", cause);
			log.log(level, null, cause);
			emit.invoke(verify(writer), null, cause);
			log.log(level, "message-only", null);
			emit.invoke(verify(writer), "message-only", null);
		}
		log.entering("Owner", "method"); log.exiting("Owner", "method"); log.throwing("Owner", "method", cause);
		verify(writer).entering("Owner", "method"); verify(writer).exiting("Owner", "method");
		verify(writer).throwing("Owner", "method", cause);
	}

	@Test
	void nativeDestinationsReceiveMessagesAndTracesWithIndependentThresholds() throws Exception {
		var systemFile = directory.resolve("system.log");
		var securityFile = directory.resolve("security.log");
		var config = new CacheConfiguration(Map.of("mcast-port", "0", "locators", "", "log-level", "finest",
			"log-file", systemFile.toString(), "security-log-level", "warning", "security-log-file", securityFile.toString()));
		try (var connection = new GemFire104Driver().connect(config)) {
			var log = connection.getRuntime().getLog();
			var security = connection.getRuntime().getSecurityLog();
			for (var level : Level.values()) assertThat(log.isEnabled(level)).as(level.name()).isEqualTo(level != Level.ALL);
			assertThat(security.isEnabled(Level.INFO)).isFalse();
			assertThat(security.isEnabled(Level.WARNING)).isTrue();
			log.log(Level.CONFIG, "system-log-marker", null);
			log.entering("TraceOwner", "method"); log.exiting("TraceOwner", "method");
			log.throwing("TraceOwner", "method", new IllegalStateException("trace-cause-marker"));
			security.log(Level.INFO, "filtered-security-marker", null);
			security.log(Level.WARNING, "security-log-marker", null);
		}
		assertThat(Files.readString(systemFile)).contains("system-log-marker", "ENTRY TraceOwner:method",
			"RETURN TraceOwner:method", "THROW TraceOwner:method", "trace-cause-marker");
		assertThat(Files.readString(securityFile)).contains("security-log-marker")
			.doesNotContain("filtered-security-marker", "system-log-marker");
	}

	@Test
	void configUsesInfoThresholdAndSecurityCanShareTheSystemDestination() throws Exception {
		var file = directory.resolve("shared.log");
		var config = new CacheConfiguration(Map.of("mcast-port", "0", "locators", "", "log-level", "config",
			"log-file", file.toString(), "security-log-level", "warning"));
		try (var connection = new GemFire104Driver().connect(config)) {
			var log = connection.getRuntime().getLog();
			assertThat(log.isEnabled(Level.CONFIG)).isTrue();
			assertThat(log.isEnabled(Level.INFO)).isTrue();
			assertThat(log.isEnabled(Level.FINE)).isFalse();
			log.log(Level.INFO, "shared-system-marker", null);
			connection.getRuntime().getSecurityLog().log(Level.WARNING, "shared-security-marker", null);
		}
		assertThat(Files.readString(file)).contains("shared-system-marker", "shared-security-marker");
	}

	@Test
	void runtimeAccessAndLoggingFailuresAreSanitizedWhileFatalErrorsPropagate() {
		var system = mock(DistributedSystem.class);
		when(system.getCancelCriterion()).thenReturn(CancelCriterion.NONE);
		var writer = mock(LogWriter.class);
		when(system.getLogWriter()).thenReturn(writer);
		when(system.getSecurityLogWriter()).thenReturn(writer);
		var runtime = new GemFireClientRuntime(system);
		var cause = new IllegalStateException("private-cause");
		doThrow(cause).when(writer).info("private-message", null);
		for (var log : java.util.List.of(runtime.getLog(), runtime.getSecurityLog())) {
			assertThatThrownBy(() -> log.log(Level.INFO, "private-message", null))
				.isInstanceOfSatisfying(DriverOperationException.class, failure -> {
					assertThat(failure.kind()).isEqualTo(DriverFailureKind.INVALID_STATE);
					assertThat(failure.getCause()).isSameAs(cause);
					assertThat(failure.getMessage()).doesNotContain("private-message", "private-cause");
				});
		}
		when(system.getLogWriter()).thenThrow(cause);
		when(system.getSecurityLogWriter()).thenThrow(cause);
		assertThatThrownBy(runtime::getLog).isInstanceOf(DriverOperationException.class).hasCause(cause);
		assertThatThrownBy(runtime::getSecurityLog).isInstanceOf(DriverOperationException.class).hasCause(cause);
		var fatal = new LinkageError("fatal");
		doThrow(fatal).when(writer).fineEnabled();
		assertThatThrownBy(() -> new GemFireLog(writer).isEnabled(Level.FINE)).isSameAs(fatal);
	}
}
