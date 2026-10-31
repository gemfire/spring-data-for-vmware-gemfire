/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi.config;

/**
 * Region distribution scope. {@link #ordinal()} is the declaration order; the legacy ordinal is {@link #legacyOrdinal()}.
 * {@link #DISTRIBUTED_NO_ACK} was removed from the legacy API, so it has no legacy ordinal.
 */
public enum Scope {
	LOCAL(0), DISTRIBUTED_NO_ACK(-1), DISTRIBUTED_ACK(2), GLOBAL(3);

	private final int legacyOrdinal;

	Scope(int legacyOrdinal) { this.legacyOrdinal = legacyOrdinal; }

	/** The ordinal the legacy {@code Scope} assigned to this scope. Fails for the removed no-ack scope. */
	public int legacyOrdinal() {
		if (legacyOrdinal < 0) throw new IllegalStateException("The distributed-no-ack scope has been removed and has no legacy ordinal");
		return legacyOrdinal;
	}

	/** Looks a scope up by legacy ordinal. The legacy slot 1 held the removed no-ack scope and resolves to distributed-ack, as it did there. */
	public static Scope fromLegacyOrdinal(int ordinal) {
		return switch (ordinal) {
			case 0 -> LOCAL;
			case 1, 2 -> DISTRIBUTED_ACK;
			case 3 -> GLOBAL;
			default -> throw new IllegalArgumentException("No scope has ordinal " + ordinal);
		};
	}

	/** Case-sensitive lookup by name. The removed no-ack scope is rejected with a pointer to distributed-ack. */
	public static Scope fromString(String name) {
		if ("DISTRIBUTED_NO_ACK".equals(name)) throw new IllegalArgumentException("The distributed-no-ack scope has been removed. Use distributed-ack.");
		for (Scope scope : values()) {
			if (scope != DISTRIBUTED_NO_ACK && scope.name().equals(name)) return scope;
		}
		throw new IllegalArgumentException(String.format("%s is not a valid string representation of %s.", name, Scope.class.getName()));
	}

	public boolean isLocal() { return this == LOCAL; }
	public boolean isDistributed() { return this != LOCAL; }
	public boolean isDistributedNoAck() { return this == DISTRIBUTED_NO_ACK; }
	public boolean isDistributedAck() { return this == DISTRIBUTED_ACK; }
	public boolean isGlobal() { return this == GLOBAL; }
	public boolean isAck() { return this == DISTRIBUTED_ACK || this == GLOBAL; }
	public String toConfigTypeString() { return name().toLowerCase(java.util.Locale.ROOT).replace('_', '-'); }
}
