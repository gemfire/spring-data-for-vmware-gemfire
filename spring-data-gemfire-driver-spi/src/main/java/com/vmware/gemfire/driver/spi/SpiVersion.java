/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * The SPI's {@code major.minor} — the ABI (binary-compatibility) unit (spec §6.1). Minor bumps are additive
 * only; a major bump is the only point at which anything is removed or changed.
 */
public record SpiVersion(int major, int minor) implements Comparable<SpiVersion>, Serializable {

	private static final String VERSION_RESOURCE = "spi-version.properties";
	private static final String VERSION_KEY = "spi.version";

	public SpiVersion {
		if (major < 0 || minor < 0) {
			throw new IllegalArgumentException("SPI version components must be >= 0: " + major + "." + minor);
		}
	}

	public static SpiVersion of(int major, int minor) {
		return new SpiVersion(major, minor);
	}

	/** Parses {@code major.minor}; any further segments (patch, qualifier) are ignored. */
	public static SpiVersion parse(String text) {
		if (text == null) {
			throw new IllegalArgumentException("SPI version must not be null");
		}
		String[] parts = text.trim().split("\\.");
		if (parts.length < 2) {
			throw new IllegalArgumentException("SPI version must be 'major.minor': '" + text + "'");
		}
		try {
			return new SpiVersion(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
		}
		catch (NumberFormatException ex) {
			throw new IllegalArgumentException("SPI version must be 'major.minor': '" + text + "'", ex);
		}
	}

	/**
	 * The version of the SPI jar actually on the classpath, read from a resource inside that jar — never from a
	 * compile-time constant, which javac would inline into the compiling module and so report the version that
	 * module was built against rather than the one at runtime (§6.1). Stateless: reads and caches nothing.
	 */
	public static SpiVersion runtime() {
		try (InputStream in = SpiVersion.class.getResourceAsStream(VERSION_RESOURCE)) {
			if (in == null) {
				throw new DriverBindException("The SPI jar is missing its version resource " + VERSION_RESOURCE + ".");
			}
			Properties properties = new Properties();
			properties.load(in);
			return parse(properties.getProperty(VERSION_KEY));
		}
		catch (IOException ex) {
			throw new UncheckedIOException("Unable to read the SPI version resource " + VERSION_RESOURCE, ex);
		}
		catch (IllegalArgumentException ex) {
			throw new DriverBindException("The SPI version resource " + VERSION_RESOURCE + " is malformed.", ex);
		}
	}

	/**
	 * True iff a driver built against {@code builtAgainst} can run on this SPI version: same major, and this minor
	 * is at least the one the driver was built against (§6.2).
	 */
	public boolean accepts(SpiVersion builtAgainst) {
		return this.major == builtAgainst.major && this.minor >= builtAgainst.minor;
	}

	@Override
	public int compareTo(SpiVersion other) {
		int byMajor = Integer.compare(this.major, other.major);
		return byMajor != 0 ? byMajor : Integer.compare(this.minor, other.minor);
	}

	@Override
	public String toString() {
		return major + "." + minor;
	}

}
