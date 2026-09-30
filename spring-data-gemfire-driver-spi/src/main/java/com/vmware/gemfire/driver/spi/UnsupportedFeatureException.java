/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.Optional;

/**
 * A feature the bound Driver Impl does not support (spec §5.3). Two legitimate throw sites, distinguished by
 * {@link Reason}: the SPI's {@code default} methods ({@link Reason#NOT_INTRODUCED} — the driver predates the
 * feature) and a driver's own overrides ({@link Reason#REMOVED} — its GemFire version dropped the feature).
 */
public final class UnsupportedFeatureException extends GemFireDriverException {

	public enum Reason {
		NOT_INTRODUCED, REMOVED
	}

	private final String feature;
	private final Reason reason;
	private final String gemFireVersion;

	private UnsupportedFeatureException(String message, String feature, Reason reason, String gemFireVersion) {
		super(message);
		this.feature = feature;
		this.reason = reason;
		this.gemFireVersion = gemFireVersion;
	}

	/** For an SPI {@code default} method: the bound driver predates the feature. It cannot know its own version. */
	public static UnsupportedFeatureException notIntroduced(String feature) {
		return new UnsupportedFeatureException("Feature '" + feature + "' is not supported: the bound Driver Impl"
			+ " predates it.", feature, Reason.NOT_INTRODUCED, null);
	}

	/** For a driver override: the driver's GemFire version no longer supports a feature the SPI still carries. */
	public static UnsupportedFeatureException removed(String feature, String gemFireVersion) {
		return new UnsupportedFeatureException("Feature '" + feature + "' is not supported by GemFire " + gemFireVersion
			+ ": it has been removed.", feature, Reason.REMOVED, gemFireVersion);
	}

	public String feature() {
		return feature;
	}

	public Reason reason() {
		return reason;
	}

	/** Present only for {@link Reason#REMOVED}. */
	public Optional<String> gemFireVersion() {
		return Optional.ofNullable(gemFireVersion);
	}

}
