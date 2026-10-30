/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import com.vmware.gemfire.driver.spi.DriverLog.Level;

class MockLogTests {

	@Test
	void plainAndLocalizedPublicationShareThresholdDestinationAndCause() {
		var log = new MockLog("warning");
		var cause = new IllegalStateException("mixed-cause");
		log.log(Level.INFO, "filtered-plain", cause);
		log.logLocalized(Level.INFO, log.createMessage(42, "filtered {0}"), new Object[] {"localized"}, cause);
		log.log(Level.WARNING, "accepted plain", cause);
		log.logLocalized(Level.WARNING, log.createMessage(42, "accepted {0}"), new Object[] {"localized"}, cause);
		assertThat(log.entries()).extracting(MockLog.Entry::message)
			.containsExactly("accepted plain", "accepted localized");
		assertThat(log.entries()).allSatisfy(entry -> {
			assertThat(entry.level()).isEqualTo(Level.WARNING);
			assertThat(entry.cause()).isSameAs(cause);
		});
	}

	@Test
	void localizedMessagesUseRawFallbackAndRetainArgumentShapeAndCauses() {
		var log = new MockLog("warning");
		var cause = new IllegalStateException("localized-cause");
		var message = log.createMessage(42, "raw {0}");
		assertThat(message.formatLocalized("value")).isEqualTo("raw value");
		var array = new String[] {"first", "second"};
		log.logLocalized(Level.WARNING, message, new Object[] {array}, cause);
		assertThat(log.entries()).hasSize(1);
		assertThat(log.entries().get(0).message()).isEqualTo("raw " + array);
		assertThat(log.entries().get(0).cause()).isSameAs(cause);
		assertThat(message.toString()).doesNotContain("raw {0}");
	}

	@Test
	void handlerAndDestinationThresholdsBothApplyAndHandlersCloseIndependently() {
		var log = new MockLog("warning");
		var first = log.getHandler(); var second = log.getHandler();
		first.publish(new java.util.logging.LogRecord(java.util.logging.Level.INFO, "destination-filtered"));
		first.setLevel(java.util.logging.Level.SEVERE);
		first.publish(new java.util.logging.LogRecord(java.util.logging.Level.WARNING, "handler-filtered"));
		first.close();
		first.publish(new java.util.logging.LogRecord(java.util.logging.Level.SEVERE, "closed"));
		var cause = new IllegalStateException("retained");
		var record = new java.util.logging.LogRecord(java.util.logging.Level.WARNING, "accepted {0}");
		record.setParameters(new Object[] {"value"}); record.setThrown(cause);
		record.setThreadID(7); record.setSequenceNumber(8);
		assertThat(second.getFormatter().format(record)).contains("(msgTID=7 msgSN=8) accepted {0}", "retained");
		second.publish(record); second.flush(); second.close();
		assertThat(log.entries()).hasSize(1);
		assertThat(log.entries().get(0).level()).isEqualTo(Level.WARNING);
		assertThat(log.entries().get(0).message()).isEqualTo("(tid=7 msgId=8) accepted value");
		assertThat(log.entries().get(0).cause()).isSameAs(cause);
	}

	@Test
	void recordsExactMessagesCausesAndTracesInDetachedSnapshots() {
		var log = new MockLog("all");
		var cause = new IllegalStateException("private-cause");
		for (var level : Level.values()) {
			log.log(level, "private-message", cause);
			log.log(level, null, cause);
			log.log(level, "message-only", null);
		}
		assertThat(log.entries()).hasSize(30);
		assertThat(log.entries().get(0).message()).isEqualTo("private-message");
		assertThat(log.entries().get(0).cause()).isSameAs(cause);
		assertThat(log.entries().get(1).message()).isNull();
		assertThat(log.entries().get(2).cause()).isNull();
		assertThat(log.entries().get(0).toString()).doesNotContain("private-message", "private-cause");
		var snapshot = log.entries();
		assertThatThrownBy(snapshot::clear).isInstanceOf(UnsupportedOperationException.class);
		log.entering("Owner", "method"); log.exiting("Owner", "method"); log.throwing("Owner", "method", cause);
		assertThat(snapshot).hasSize(30);
		assertThat(log.entries().subList(30, 33)).extracting(MockLog.Entry::message)
			.containsExactly("ENTRY Owner:method", "RETURN Owner:method", "THROW Owner:method");
		assertThat(log.entries().get(32).cause()).isSameAs(cause);
	}

	@Test
	void thresholdsPreserveAliasesAndFilterRecordedOutput() {
		String[] settings = {"all", "finest", "finer", "fine", "config", "info", "warning", "error", "severe", "none"};
		int[] counts = {10, 9, 9, 7, 6, 6, 4, 3, 2, 1};
		for (int i = 0; i < settings.length; i++) {
			var log = new MockLog(settings[i]);
			for (var level : Level.values()) log.log(level, "message", null);
			assertThat(log.entries()).as(settings[i]).hasSize(counts[i]);
			assertThat(log.isEnabled(Level.FINER)).isEqualTo(log.isEnabled(Level.FINEST));
			assertThat(log.isEnabled(Level.CONFIG)).isEqualTo(log.isEnabled(Level.INFO));
		}
		var log = new MockLog("info");
		log.entering("Owner", "method"); log.exiting("Owner", "method"); log.throwing("Owner", "method", null);
		assertThat(log.entries()).isEmpty();
	}

	@Test
	void extremeRecordsRespectDestinationThresholdsAndHandlerOffSuppressesAllRecords() {
		for (var threshold : java.util.List.of("all", "info", "none")) {
			var log = new MockLog(threshold);
			assertThat(log.isEnabled(Level.ALL)).as(threshold).isEqualTo(threshold.equals("all"));
			assertThat(log.isEnabled(Level.OFF)).as(threshold).isTrue();
			var handler = log.getHandler();
			try {
				handler.publish(new java.util.logging.LogRecord(java.util.logging.Level.ALL, "all-record"));
				handler.publish(new java.util.logging.LogRecord(java.util.logging.Level.OFF, "off-record"));
				handler.setLevel(java.util.logging.Level.OFF);
				handler.publish(new java.util.logging.LogRecord(java.util.logging.Level.OFF, "filtered"));
			} finally { handler.close(); }
			assertThat(log.entries()).extracting(MockLog.Entry::level)
				.containsExactly(threshold.equals("all") ? new Level[] {Level.ALL, Level.OFF} : new Level[] {Level.OFF});
		}
	}

	@Test
	void runtimeChannelsAndConnectionsHaveIndependentRecorders() {
		var settings = new Properties();
		settings.setProperty("log-level", "error");
		settings.setProperty("security-log-level", "fine");
		try (var first = new MockConnection(settings); var second = new MockConnection()) {
			var log = (MockLog) first.getRuntime().getLog();
			var security = (MockLog) first.getRuntime().getSecurityLog();
			log.log(Level.INFO, "filtered", null);
			security.log(Level.FINE, "security", null);
			assertThat(log.entries()).isEmpty();
			assertThat(security.entries()).extracting(MockLog.Entry::message).containsExactly("security");
			assertThat((MockLog) first.getRuntime().getSecurityLog()).isSameAs(security);
			assertThat(((MockLog) second.getRuntime().getSecurityLog()).entries()).isEmpty();
			assertThat(second.getRuntime().getLog().isEnabled(Level.CONFIG)).isTrue();
			assertThat(second.getRuntime().getLog().isEnabled(Level.FINE)).isFalse();
		}
	}
}
