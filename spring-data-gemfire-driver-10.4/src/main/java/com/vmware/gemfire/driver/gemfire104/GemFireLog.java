/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.Objects;

import org.apache.geode.LogWriter;
import org.apache.geode.logging.internal.log4j.LogWriterLogger;

import com.vmware.gemfire.driver.spi.DriverLog;
import com.vmware.gemfire.driver.spi.DriverLogMessage;

/** Keeps native log writers and their configured destinations behind the SPI. */
final class GemFireLog implements DriverLog {

	private final LogWriter writer;
	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();

	GemFireLog(LogWriter writer) { this.writer = Objects.requireNonNull(writer, "writer"); }

	@Override public java.util.logging.Handler getHandler() {
		return new com.vmware.gemfire.driver.spi.DriverLogHandler(this);
	}

	@Override public boolean isEnabled(Level level) {
		try {
			if (level == null) throw new IllegalArgumentException("Log level is required");
			return switch (level) {
				case ALL -> ((LogWriterLogger) writer).isEnabled(org.apache.logging.log4j.Level.ALL);
				case OFF -> ((LogWriterLogger) writer).isEnabled(org.apache.logging.log4j.Level.OFF);
				case FINEST -> writer.finestEnabled();
				case FINER -> writer.finerEnabled();
				case FINE -> writer.fineEnabled();
				case CONFIG -> writer.configEnabled();
				case INFO -> writer.infoEnabled();
				case WARNING -> writer.warningEnabled();
				case ERROR -> writer.errorEnabled();
				case SEVERE -> writer.severeEnabled();
			};
		} catch (RuntimeException failure) { throw exceptions.translate("Log.isEnabled", failure); }
	}

	@Override public void log(Level level, String message, Throwable cause) {
		try {
			if (level == null) throw new IllegalArgumentException("Log level is required");
			switch (level) {
				case ALL -> ((LogWriterLogger) writer).log(Integer.MIN_VALUE, message, cause);
				case OFF -> ((LogWriterLogger) writer).log(Integer.MAX_VALUE, message, cause);
				case FINEST -> writer.finest(message, cause);
				case FINER -> writer.finer(message, cause);
				case FINE -> writer.fine(message, cause);
				case CONFIG -> writer.config(message, cause);
				case INFO -> writer.info(message, cause);
				case WARNING -> writer.warning(message, cause);
				case ERROR -> writer.error(message, cause);
				case SEVERE -> writer.severe(message, cause);
			}
		} catch (RuntimeException failure) { throw exceptions.translate("Log.log", failure); }
	}

	@Override public DriverLogMessage createMessage(int id, String rawText) {
		return new Message(id, rawText);
	}

	@Override public void logLocalized(Level level, DriverLogMessage message, Object[] parameters, Throwable cause) {
		try {
			if (isEnabled(level)) log(level, message.formatLocalized(parameters), cause);
		} catch (RuntimeException failure) { throw exceptions.translate("Log.logLocalized", failure); }
	}

	private static final class Message implements DriverLogMessage {
		private final org.apache.geode.i18n.StringId value;
		private Message(int id, String rawText) { value = new org.apache.geode.i18n.StringId(id, rawText); }
		@Override public int id() { return value.id; }
		@Override public String rawText() { return value.getRawText(); }
		@Override public String format(Object... parameters) { return value.toString(parameters); }
		@Override public String formatLocalized(Object... parameters) { return value.toLocalizedString(parameters); }
		@Override public String toString() { return "LogMessage[id=" + id() + ", text=<redacted>]"; }
	}

	@Override public void entering(String sourceClass, String sourceMethod) {
		try { writer.entering(sourceClass, sourceMethod); }
		catch (RuntimeException failure) { throw exceptions.translate("Log.entering", failure); }
	}

	@Override public void exiting(String sourceClass, String sourceMethod) {
		try { writer.exiting(sourceClass, sourceMethod); }
		catch (RuntimeException failure) { throw exceptions.translate("Log.exiting", failure); }
	}

	@Override public void throwing(String sourceClass, String sourceMethod, Throwable cause) {
		try { writer.throwing(sourceClass, sourceMethod, cause); }
		catch (RuntimeException failure) { throw exceptions.translate("Log.throwing", failure); }
	}
}
