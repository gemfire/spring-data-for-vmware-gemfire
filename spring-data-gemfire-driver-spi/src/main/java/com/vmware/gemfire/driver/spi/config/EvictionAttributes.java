// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi.config;

/** Immutable eviction settings. Equality ignores the sizer and hashing uses algorithm and maximum, as the native type does.
 * Heap-based attributes carry no maximum. A null action is stored as {@link EvictionAction#NONE}.
 * The lossy native configuration-model conversion is intentionally not exposed.
 */
public final class EvictionAttributes {
	public static final int DEFAULT_ENTRIES_MAXIMUM = 900;
	public static final int DEFAULT_MEMORY_MAXIMUM = 10;
	private final EvictionAlgorithm algorithm;
	private final EvictionAction action;
	private final int maximum;
	private final ObjectSizer sizer;
	private EvictionAttributes(EvictionAlgorithm algorithm, EvictionAction action, int maximum, ObjectSizer sizer) {
		this.algorithm = algorithm;
		this.action = action == null ? EvictionAction.NONE : action;
		this.maximum = maximum;
		this.sizer = sizer;
	}
	public static EvictionAttributes createLRUEntryAttributes() { return createLRUEntryAttributes(DEFAULT_ENTRIES_MAXIMUM, EvictionAction.DEFAULT_EVICTION_ACTION); }
	public static EvictionAttributes createLRUEntryAttributes(int maximumEntries) { return createLRUEntryAttributes(maximumEntries, EvictionAction.DEFAULT_EVICTION_ACTION); }
	public static EvictionAttributes createLRUEntryAttributes(EvictionAction action) { return createLRUEntryAttributes(DEFAULT_ENTRIES_MAXIMUM, action); }
	public static EvictionAttributes createLRUEntryAttributes(int maximumEntries, EvictionAction action) {
		return new EvictionAttributes(EvictionAlgorithm.LRU_ENTRY, action, maximumEntries, null);
	}
	public static EvictionAttributes createLRUHeapAttributes() { return createLRUHeapAttributes(ObjectSizer.DEFAULT, EvictionAction.DEFAULT_EVICTION_ACTION); }
	public static EvictionAttributes createLRUHeapAttributes(ObjectSizer sizer) { return createLRUHeapAttributes(sizer, EvictionAction.DEFAULT_EVICTION_ACTION); }
	public static EvictionAttributes createLRUHeapAttributes(ObjectSizer sizer, EvictionAction action) {
		return new EvictionAttributes(EvictionAlgorithm.LRU_HEAP, action, 0, sizer);
	}
	public static EvictionAttributes createLRUMemoryAttributes() { return createLRUMemoryAttributes(DEFAULT_MEMORY_MAXIMUM, ObjectSizer.DEFAULT, EvictionAction.DEFAULT_EVICTION_ACTION); }
	/** Unlike the other overloads, the single-int form carries a null sizer, as the native overload does. */
	public static EvictionAttributes createLRUMemoryAttributes(int maximumMegabytes) { return createLRUMemoryAttributes(maximumMegabytes, null, EvictionAction.DEFAULT_EVICTION_ACTION); }
	public static EvictionAttributes createLRUMemoryAttributes(int maximumMegabytes, ObjectSizer sizer) { return createLRUMemoryAttributes(maximumMegabytes, sizer, EvictionAction.DEFAULT_EVICTION_ACTION); }
	public static EvictionAttributes createLRUMemoryAttributes(int maximumMegabytes, ObjectSizer sizer, EvictionAction action) {
		return new EvictionAttributes(EvictionAlgorithm.LRU_MEMORY, action, maximumMegabytes, sizer);
	}
	public static EvictionAttributes createLRUMemoryAttributes(ObjectSizer sizer) { return createLRUMemoryAttributes(DEFAULT_MEMORY_MAXIMUM, sizer, EvictionAction.DEFAULT_EVICTION_ACTION); }
	public static EvictionAttributes createLRUMemoryAttributes(ObjectSizer sizer, EvictionAction action) { return createLRUMemoryAttributes(DEFAULT_MEMORY_MAXIMUM, sizer, action); }
	/** Retained for compatibility; the native factory is deprecated since 9.4. */
	@Deprecated public static EvictionAttributes createLIFOEntryAttributes(int maximumEntries, EvictionAction action) {
		return new EvictionAttributes(EvictionAlgorithm.LIFO_ENTRY, action, maximumEntries, null);
	}
	/** Retained for compatibility; the native factory is deprecated since 9.4. */
	@Deprecated public static EvictionAttributes createLIFOMemoryAttributes(int maximumMegabytes, EvictionAction action) {
		return new EvictionAttributes(EvictionAlgorithm.LIFO_MEMORY, action, maximumMegabytes, null);
	}
	/** Rebuilds attributes read back from a provider, whatever the algorithm. */
	public static EvictionAttributes of(EvictionAlgorithm algorithm, EvictionAction action, int maximum, ObjectSizer sizer) {
		return new EvictionAttributes(java.util.Objects.requireNonNull(algorithm, "algorithm"), action, maximum, sizer);
	}
	public ObjectSizer getObjectSizer() { return sizer; }
	public EvictionAlgorithm getAlgorithm() { return algorithm; }
	public int getMaximum() { return maximum; }
	public EvictionAction getAction() { return action; }
	public boolean isNoEviction() { return action == EvictionAction.NONE; }
	@Override public boolean equals(Object other) {
		return this == other || other instanceof EvictionAttributes that && algorithm == that.algorithm && action == that.action && maximum == that.maximum;
	}
	@Override public int hashCode() { return algorithm.hashCode() ^ maximum; }
	@Override public String toString() {
		StringBuilder text = new StringBuilder(" algorithm=").append(algorithm);
		if (!algorithm.isNone()) {
			text.append("; action=").append(action);
			if (!algorithm.isLRUHeap()) text.append("; maximum=").append(maximum);
			if (sizer != null) text.append("; sizer=").append(sizer);
		}
		return text.toString();
	}
}
