/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ListResourceBundle;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.jupiter.api.Test;

class DriverLogFormatterTests {

	@Test
	void fullLayoutPreservesRawMessageAndDistinguishesCurrentAndRecordThreads() {
		var formatter = new DriverLogHandler(new DriverLog() {}).getFormatter();
		var record = new LogRecord(Level.WARNING, "raw {0}");
		record.setMillis(0); record.setThreadID(123); record.setSequenceNumber(456);
		record.setParameters(new Object[] {"resolved"});
		var thread = Thread.currentThread();
		assertThat(formatter.format(record)).startsWith(System.lineSeparator() + "[WARNING ")
			.contains(" " + thread.getName() + " tid=0x" + Long.toHexString(thread.getId()) + "] ")
			.endsWith("(msgTID=123 msgSN=456) raw {0}" + System.lineSeparator());
		assertThat(formatter.formatMessage(record)).isEqualTo("raw resolved");
	}

	@Test
	void multilineNullAndEmptyMessagesHaveNativeLineEndings() {
		var formatter = new DriverLogHandler(new DriverLog() {}).getFormatter();
		String newline = System.lineSeparator();
		var record = new LogRecord(Level.INFO, "first\r\nsecond\n");
		assertThat(formatter.format(record)).endsWith("first" + newline + "  second" + newline + "  " + newline);
		record.setMessage(null);
		assertThat(formatter.format(record)).endsWith(") " + newline);
		record.setMessage("");
		assertThat(formatter.format(record)).endsWith(") " + newline);
	}

	@Test
	void fullFormatRetainsBundleKeyAndThrowableWhileMessageFormatResolvesParameters() {
		var formatter = new DriverLogHandler(new DriverLog() {}).getFormatter();
		var record = new LogRecord(Level.SEVERE, "bundle-key");
		record.setResourceBundle(new ListResourceBundle() {
			@Override protected Object[][] getContents() { return new Object[][] {{"bundle-key", "hello {0}"}}; }
		});
		record.setParameters(new Object[] {"world"});
		var cause = new IllegalStateException("failure-marker", new IllegalArgumentException("cause-marker"));
		cause.addSuppressed(new IllegalStateException("suppressed-marker"));
		record.setThrown(cause);
		assertThat(formatter.format(record)).contains(") bundle-key" + System.lineSeparator(),
			"failure-marker", "Caused by: java.lang.IllegalArgumentException: cause-marker",
			"Suppressed: java.lang.IllegalStateException: suppressed-marker").doesNotContain("hello world");
		assertThat(formatter.formatMessage(record)).isEqualTo("hello world");
	}
}
