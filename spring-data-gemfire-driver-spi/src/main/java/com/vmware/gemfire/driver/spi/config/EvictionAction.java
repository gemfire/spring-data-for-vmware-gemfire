// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi.config;

/** Action taken on eviction. {@link #toString()} yields the XML token, as the native value does. */
public enum EvictionAction {
	NONE("none"), LOCAL_DESTROY("local-destroy"), OVERFLOW_TO_DISK("overflow-to-disk");
	public static final EvictionAction DEFAULT_EVICTION_ACTION = LOCAL_DESTROY;
	private final String token;
	EvictionAction(String token) { this.token = token; }
	/** Native integer value, equal to the ordinal. */
	public int getValue() { return ordinal(); }
	public boolean isNone() { return this == NONE; }
	public boolean isLocalDestroy() { return this == LOCAL_DESTROY; }
	public boolean isOverflowToDisk() { return this == OVERFLOW_TO_DISK; }
	/** Case-sensitive token match; null, empty and unknown values yield {@link #NONE}. */
	public static EvictionAction parseAction(String token) {
		if (token != null) for (EvictionAction action : values()) if (action.token.equals(token)) return action;
		return NONE;
	}
	/** Null when the value is out of range. */
	public static EvictionAction parseValue(int value) { return value < 0 || value >= values().length ? null : values()[value]; }
	@Override public String toString() { return token; }
}
