/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi.config;

/**
 * Region data policy. The declaration order is not the legacy order, so the legacy ordinal is an explicit field
 * ({@link #legacyOrdinal()}) rather than {@link #ordinal()}.
 */
public enum DataPolicy {

	EMPTY(0), NORMAL(1), PRELOADED(5), PARTITION(4),
	PERSISTENT_PARTITION(6), REPLICATE(2), PERSISTENT_REPLICATE(3);

	/** The policy used when none is specified. */
	public static final DataPolicy DEFAULT = NORMAL;

	private final int legacyOrdinal;

	DataPolicy(int legacyOrdinal) { this.legacyOrdinal = legacyOrdinal; }

	/** The ordinal the legacy {@code DataPolicy} assigned to this policy. */
	public int legacyOrdinal() { return legacyOrdinal; }

	/** Looks a policy up by legacy ordinal. Unlike the legacy array lookup, an unassigned ordinal fails instead of returning null or an array error. */
	public static DataPolicy fromLegacyOrdinal(int ordinal) {
		for (DataPolicy policy : values()) {
			if (policy.legacyOrdinal == ordinal) return policy;
		}
		throw new IllegalArgumentException("No data policy has ordinal " + ordinal);
	}

	/** Case-sensitive lookup by name, failing with the unknown name as the message and a {@link NullPointerException} for null. */
	public static DataPolicy fromString(String name) {
		java.util.Objects.requireNonNull(name);
		for (DataPolicy policy : values()) {
			if (policy.name().equals(name)) return policy;
		}
		throw new IllegalArgumentException(name);
	}

	public boolean withStorage() { return this != EMPTY; }
	public boolean withReplication() { return this == REPLICATE || this == PERSISTENT_REPLICATE; }
	public boolean withPersistence() { return this == PERSISTENT_PARTITION || this == PERSISTENT_REPLICATE; }
	public boolean withPartitioning() { return this == PARTITION || this == PERSISTENT_PARTITION; }
	public boolean withPreloaded() { return this == PRELOADED; }
	public boolean isEmpty() { return this == EMPTY; }
	public boolean isNormal() { return this == NORMAL; }
	public boolean isPreloaded() { return this == PRELOADED; }
	public boolean isDefault() { return this == DEFAULT; }
	public boolean isReplicate() { return this == REPLICATE; }
	public boolean isPersistentReplicate() { return this == PERSISTENT_REPLICATE; }
	public boolean isPartition() { return this == PARTITION; }

}
