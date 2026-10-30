/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/**
 * A client log destination. Messages and causes are explicit, potentially sensitive output;
 * they are not redacted. Filtering and destinations belong to the driver logging configuration.
 * No logger identity, persistence, delivery after disconnect or dynamic reconfiguration is promised.
 * The 10.4 driver maps CONFIG to INFO and both FINER and FINEST to TRACE.
 * Localized message values expose no native logger or catalog types.
 * Plain and localized publication share this destination and its threshold. Native conversions
 * between plain and localized logger interfaces need no equivalent operation: callers use
 * {@link #log} and {@link #logLocalized} on the same handle.
 */
public interface DriverLog {

	/**
	 * Record levels, including the native extremes. ALL requires the ALL destination threshold;
	 * OFF is the highest record level and can pass even the native NONE/OFF threshold.
	 * A JUL handler set to OFF still suppresses every record before destination filtering.
	 */
	enum Level { FINEST, FINER, FINE, CONFIG, INFO, WARNING, ERROR, SEVERE, ALL, OFF }

	/** Returns a fresh, independently configurable JUL bridge; see {@link DriverLogHandler}. */
	default java.util.logging.Handler getHandler() {
		throw UnsupportedFeatureException.notIntroduced("Log.getHandler");
	}

	/** Tests the effective threshold. Level must be non-null. */
	default boolean isEnabled(Level level) {
		throw UnsupportedFeatureException.notIntroduced("Log.isEnabled");
	}

	/**
	 * Logs at the given non-null level. A null cause represents message-only output;
	 * a null message represents throwable-only output. Neither payload is transformed.
	 */
	default void log(Level level, String message, Throwable cause) {
		throw UnsupportedFeatureException.notIntroduced("Log.log");
	}

	/**
	 * Creates a message without formatting it. ID 1 and pattern "{0}" represent a literal argument.
	 * IDs refer to the selected driver's catalog; arbitrary IDs are not portable translation keys.
	 */
	default DriverLogMessage createMessage(int id, String rawText) {
		throw UnsupportedFeatureException.notIntroduced("Log.createMessage");
	}

	/**
	 * Checks the level before dereferencing the message or formatting arguments. A suppressed
	 * message may be null or invalid. Enabled runtime failures are classified with their original
	 * cause; errors propagate. Wrap an array in Object[] to pass it as one formatting argument.
	 */
	default void logLocalized(Level level, DriverLogMessage message, Object[] parameters, Throwable cause) {
		throw UnsupportedFeatureException.notIntroduced("Log.logLocalized");
	}

	/** FINER tracing with the native ENTRY class:method representation. */
	default void entering(String sourceClass, String sourceMethod) {
		throw UnsupportedFeatureException.notIntroduced("Log.entering");
	}

	/** FINER tracing with the native RETURN class:method representation. */
	default void exiting(String sourceClass, String sourceMethod) {
		throw UnsupportedFeatureException.notIntroduced("Log.exiting");
	}

	/** FINER tracing with the native THROW class:method representation and attached cause. */
	default void throwing(String sourceClass, String sourceMethod, Throwable cause) {
		throw UnsupportedFeatureException.notIntroduced("Log.throwing");
	}
}
