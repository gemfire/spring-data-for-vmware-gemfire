/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.jupiter.api.Test;

class DriverLogHandlerTests {

	@Test
	void mapsNumericAliasesAndFormatsBundlesParametersIdsAndCause() {
		var levels = new ArrayList<DriverLog.Level>();
		var messages = new ArrayList<String>();
		var causes = new ArrayList<Throwable>();
		var handler = new DriverLogHandler(new DriverLog() {
			@Override public void log(DriverLog.Level level, String message, Throwable cause) {
				levels.add(level); messages.add(message); causes.add(cause);
			}
		});
		var cause = new IllegalStateException("record-cause");
		for (int value : new int[] {300, 400, 500, 700, 800, 900, 950, 1000, Integer.MIN_VALUE, Integer.MAX_VALUE}) {
			var record = new LogRecord(new Level("alias", value) {}, "key");
			record.setResourceBundle(new ListResourceBundle() {
				@Override protected Object[][] getContents() { return new Object[][] {{"key", "hello {0}"}}; }
			});
			record.setParameters(new Object[] {"world"}); record.setThrown(cause);
			record.setThreadID(12); record.setSequenceNumber(34);
			handler.publish(record);
		}
		assertThat(levels).containsExactly(DriverLog.Level.values());
		assertThat(messages).hasSize(10).allMatch(message -> message.equals("(tid=12 msgId=34) hello world"));
		assertThat(causes).hasSize(10).allMatch(value -> value == cause);
		var empty = new LogRecord(Level.INFO, null);
		empty.setThreadID(12); empty.setSequenceNumber(35);
		handler.publish(empty);
		assertThat(messages.get(10)).isEqualTo("(tid=12 msgId=35) ");
		assertThat(causes.get(10)).isNull();
	}

	@Test
	void filtersBeforeFormattingAndCloseDoesNotAffectTheDestination() {
		var messages = new ArrayList<String>();
		var destination = new DriverLog() {
			@Override public void log(DriverLog.Level level, String message, Throwable cause) { messages.add(message); }
		};
		var handler = new DriverLogHandler(destination);
		assertThat(handler.getLevel()).isEqualTo(Level.ALL);
		handler.setFormatter(new Formatter() {
			@Override public String format(LogRecord record) { throw new AssertionError("format must not be called"); }
			@Override public String formatMessage(LogRecord record) { return "formatted"; }
		});
		handler.setLevel(Level.WARNING);
		handler.publish(null); handler.publish(new LogRecord(Level.INFO, "filtered"));
		handler.setFilter(record -> false);
		handler.publish(new LogRecord(Level.SEVERE, "filtered"));
		handler.setFilter(null);
		handler.publish(new LogRecord(Level.WARNING, "accepted"));
		handler.flush();
		assertThat(messages).hasSize(1);
		assertThat(messages.get(0)).endsWith(") formatted");
		handler.setLevel(Level.OFF);
		handler.publish(new LogRecord(Level.SEVERE, "off"));
		handler.publish(new LogRecord(Level.OFF, "off-record"));
		handler.close(); handler.close(); handler.setLevel(Level.ALL);
		handler.publish(new LogRecord(Level.SEVERE, "closed"));
		destination.log(DriverLog.Level.INFO, "direct", null);
		assertThat(messages).hasSize(2).last().isEqualTo("direct");
	}

	@Test
	void rejectsUnsupportedRecordLevelsWithoutLeakingPayloads() {
		var handler = new DriverLogHandler(new DriverLog() {});
		for (var level : List.of(new Level("private-level", Integer.MIN_VALUE + 1) {},
			new Level("private-level", Integer.MAX_VALUE - 1) {}, new Level("private-level", 850) {})) {
			assertThatThrownBy(() -> handler.publish(new LogRecord(level, "private-message")))
				.isInstanceOfSatisfying(DriverOperationException.class, failure -> {
					assertThat(failure.kind()).isEqualTo(DriverFailureKind.INVALID_ARGUMENT);
					assertThat(failure.getMessage()).doesNotContain("private-level", "private-message");
				});
		}
	}

	@Test
	void reportsFormattingAndDeliveryFailuresButPropagatesFatalErrors() {
		var failures = new ArrayList<Exception>();
		var codes = new ArrayList<Integer>();
		var deliveryFailure = new DriverOperationException(DriverFailureKind.INVALID_STATE, "delivery", null);
		var handler = new DriverLogHandler(new DriverLog() {
			@Override public void log(DriverLog.Level level, String message, Throwable cause) { throw deliveryFailure; }
		});
		handler.setErrorManager(new ErrorManager() {
			@Override public void error(String message, Exception failure, int code) { failures.add(failure); codes.add(code); }
		});
		var record = new LogRecord(Level.INFO, "message");
		handler.publish(record);
		var formatFailure = new IllegalArgumentException("format");
		handler.setFormatter(new Formatter() {
			@Override public String format(LogRecord value) { return "unused"; }
			@Override public String formatMessage(LogRecord value) { throw formatFailure; }
		});
		handler.publish(record);
		assertThat(codes).containsExactly(ErrorManager.WRITE_FAILURE, ErrorManager.FORMAT_FAILURE);
		assertThat(failures).containsExactly(deliveryFailure, formatFailure);
		var fatal = new LinkageError("fatal");
		var fatalHandler = new DriverLogHandler(new DriverLog() {
			@Override public void log(DriverLog.Level level, String message, Throwable cause) { throw fatal; }
		});
		assertThatThrownBy(() -> fatalHandler.publish(record)).isSameAs(fatal);
	}
}
