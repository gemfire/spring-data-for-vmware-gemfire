// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi.config;

/**
 * Legacy region mirroring, kept for client-local storage only. It selects a {@link DataPolicy}: none is {@link DataPolicy#NORMAL},
 * keys and keys-values are both {@link DataPolicy#REPLICATE}.
 */
public enum MirrorType {
	NONE(DataPolicy.NORMAL), KEYS(DataPolicy.REPLICATE), KEYS_VALUES(DataPolicy.REPLICATE);

	private final DataPolicy dataPolicy;

	MirrorType(DataPolicy dataPolicy) { this.dataPolicy = dataPolicy; }

	/** The ordinal the legacy {@code MirrorType} assigned, which is the declaration order. */
	public int legacyOrdinal() { return ordinal(); }

	/** Looks a mirror type up by legacy ordinal, failing for an unassigned ordinal instead of the legacy array error. */
	public static MirrorType fromLegacyOrdinal(int ordinal) {
		MirrorType[] all = values();
		if (ordinal < 0 || ordinal >= all.length) throw new IllegalArgumentException("No mirror type has ordinal " + ordinal);
		return all[ordinal];
	}

	public DataPolicy getDataPolicy() { return dataPolicy; }
	public boolean isKeys() { return this == KEYS; }
	public boolean isKeysValues() { return this == KEYS_VALUES; }
	public boolean isNone() { return this == NONE; }
	public boolean isMirrored() { return this != NONE; }
}
