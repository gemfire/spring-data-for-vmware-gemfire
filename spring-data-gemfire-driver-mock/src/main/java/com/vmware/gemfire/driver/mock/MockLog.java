/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.vmware.gemfire.driver.spi.DriverLog;
import com.vmware.gemfire.driver.spi.DriverLogMessage;
import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.DriverOperationException;

/**
 * Thread-safe connection-local recorder; no files, rotation, global logger or asynchronous delivery.
 * Supports named level thresholds (including all/none); native numeric/custom levels are not modeled.
 * System and security records stay separate even when native destinations would share a file.
 * Explicitly extracted records retain messages and original causes; diagnostic output is redacted.
 */
public final class MockLog implements DriverLog {

	public record Entry(Level level, String message, Throwable cause) {
		@Override public String toString() { return "LogEntry[level=" + level + ", details=<redacted>]"; }
	}

	private final int threshold;
	private final List<Entry> entries = new ArrayList<>();

	MockLog(String level) {
		threshold = switch (level.toLowerCase(Locale.ROOT)) {
			case "all" -> 0;
			case "finest", "finer", "trace" -> 1;
			case "fine", "debug" -> 2;
			case "config", "info" -> 3;
			case "warning", "warn" -> 4;
			case "error" -> 5;
			case "severe", "fatal" -> 6;
			case "none", "off" -> 7;
			default -> throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT,
				"Unsupported mock log threshold", null);
		};
	}

	/** Immutable snapshot; message/cause access is intended for explicit test assertions. */
	public synchronized List<Entry> entries() { return List.copyOf(entries); }

	@Override public java.util.logging.Handler getHandler() {
		return new com.vmware.gemfire.driver.spi.DriverLogHandler(this);
	}

	@Override public boolean isEnabled(Level level) {
		if (level == null) throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT,
			"Log level is required", null);
		int severity = switch (level) {
			case ALL -> 0;
			case OFF -> 7;
			case FINEST, FINER -> 1;
			case FINE -> 2;
			case CONFIG, INFO -> 3;
			case WARNING -> 4;
			case ERROR -> 5;
			case SEVERE -> 6;
		};
		return severity >= threshold;
	}

	@Override public synchronized void log(Level level, String message, Throwable cause) {
		if (isEnabled(level)) entries.add(new Entry(level, message, cause));
	}
	@Override public DriverLogMessage createMessage(int id, String rawText) {
		return new Message(id, rawText);
	}

	@Override public void logLocalized(Level level, DriverLogMessage message, Object[] parameters, Throwable cause) {
		try {
			if (isEnabled(level)) log(level, message.formatLocalized(parameters), cause);
		} catch (com.vmware.gemfire.driver.spi.GemFireDriverException failure) {
			throw failure;
		} catch (RuntimeException failure) {
			var kind = failure instanceof IllegalArgumentException || failure instanceof NullPointerException
				? DriverFailureKind.INVALID_ARGUMENT : failure instanceof IllegalStateException
				? DriverFailureKind.INVALID_STATE : DriverFailureKind.UNKNOWN;
			throw new DriverOperationException(kind, "Mock operation failed: Log.logLocalized", failure);
		}
	}

	private record Message(int id, String rawText) implements DriverLogMessage {
		@Override public String format(Object... parameters) { return java.text.MessageFormat.format(rawText, parameters); }
		@Override public String formatLocalized(Object... parameters) { return format(parameters); }
		@Override public String toString() { return "LogMessage[id=" + id + ", text=<redacted>]"; }
	}

	@Override public void entering(String sourceClass, String sourceMethod) {
		log(Level.FINER, "ENTRY " + sourceClass + ":" + sourceMethod, null);
	}
	@Override public void exiting(String sourceClass, String sourceMethod) {
		log(Level.FINER, "RETURN " + sourceClass + ":" + sourceMethod, null);
	}
	@Override public void throwing(String sourceClass, String sourceMethod, Throwable cause) {
		log(Level.FINER, "THROW " + sourceClass + ":" + sourceMethod, cause);
	}
}
