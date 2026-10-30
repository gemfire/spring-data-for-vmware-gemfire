/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.Objects;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Native-free JUL bridge shared by drivers. The handler starts at ALL with a formatter whose
 * full layout contains the record timestamp, current thread name/ID, record thread/sequence IDs,
 * raw message and throwable trace. Continuation lines have two-space indentation.
 * Handler level/filter checks precede destination filtering. Publishing uses only
 * {@link java.util.logging.Formatter#formatMessage(LogRecord)}, including resource bundles and
 * parameters, and prefixes the result with {@code (tid=N msgId=N) }. The cause stays attached.
 * Supported record level values are 300, 400, 500, 700, 800, 900, 950 (ERROR), 1000,
 * Integer.MIN_VALUE (ALL) and Integer.MAX_VALUE (OFF). Numeric aliases are accepted;
 * other record levels are invalid arguments. An OFF handler threshold suppresses all records,
 * including OFF records; an OFF record reaching the destination has the highest severity.
 * Formatting/destination runtime failures go to the ErrorManager; fatal errors propagate.
 * Flush is a no-op. Close releases this handler's destination and makes further publication a
 * no-op; it does not close the client or other handlers.
 */
public final class DriverLogHandler extends Handler {

	private DriverLog destination;

	public DriverLogHandler(DriverLog destination) {
		this.destination = Objects.requireNonNull(destination, "destination");
		setFormatter(new DriverLogFormatter());
	}

	@Override public synchronized void publish(LogRecord record) {
		if (destination == null || !isLoggable(record)) return;
		DriverLog.Level level = switch (record.getLevel().intValue()) {
			case Integer.MIN_VALUE -> DriverLog.Level.ALL;
			case Integer.MAX_VALUE -> DriverLog.Level.OFF;
			case 300 -> DriverLog.Level.FINEST;
			case 400 -> DriverLog.Level.FINER;
			case 500 -> DriverLog.Level.FINE;
			case 700 -> DriverLog.Level.CONFIG;
			case 800 -> DriverLog.Level.INFO;
			case 900 -> DriverLog.Level.WARNING;
			case 950 -> DriverLog.Level.ERROR;
			case 1000 -> DriverLog.Level.SEVERE;
			default -> throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT,
				"Unsupported JUL record level", null);
		};
		String message;
		try {
			message = "(tid=" + record.getThreadID() + " msgId=" + record.getSequenceNumber() + ") "
				+ (record.getMessage() == null ? "" : getFormatter().formatMessage(record));
		} catch (RuntimeException failure) {
			reportError("Log handler formatting failed", failure, ErrorManager.FORMAT_FAILURE);
			return;
		}
		try { destination.log(level, message, record.getThrown()); }
		catch (RuntimeException failure) {
			reportError("Log handler delivery failed", failure, ErrorManager.WRITE_FAILURE);
		}
	}

	@Override public void flush() { }
	@Override public synchronized void close() { destination = null; }
}
