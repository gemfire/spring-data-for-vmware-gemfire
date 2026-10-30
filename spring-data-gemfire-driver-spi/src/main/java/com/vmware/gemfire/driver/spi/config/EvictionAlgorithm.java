// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi.config;

/** Eviction policy kind. {@link #toString()} yields the XML token, as the native value does. */
public enum EvictionAlgorithm {
	NONE("none"), LRU_ENTRY("lru-entry-count"), LRU_HEAP("lru-heap-percentage"), LRU_MEMORY("lru-memory-size"),
	LIFO_ENTRY("lifo-entry-count"), LIFO_MEMORY("lifo-memory-size");
	private final String token;
	EvictionAlgorithm(String token) { this.token = token; }
	public int getValue() { return ordinal(); }
	public boolean isNone() { return this == NONE; }
	public boolean isLRUEntry() { return this == LRU_ENTRY; }
	public boolean isLRUHeap() { return this == LRU_HEAP; }
	public boolean isLRUMemory() { return this == LRU_MEMORY; }
	public boolean isLRU() { return isLRUEntry() || isLRUMemory() || isLRUHeap(); }
	/** Retained for compatibility; the native method is deprecated since 9.4. */
	@Deprecated public boolean isLIFO() { return this == LIFO_ENTRY || this == LIFO_MEMORY; }
	/** Null for an unknown token. */
	public static EvictionAlgorithm parseAction(String token) {
		if (token != null) for (EvictionAlgorithm algorithm : values()) if (algorithm.token.equals(token)) return algorithm;
		return null;
	}
	/** Null when the value is out of range. */
	public static EvictionAlgorithm parseValue(int value) { return value < 0 || value >= values().length ? null : values()[value]; }
	@Override public String toString() { return token; }
}
