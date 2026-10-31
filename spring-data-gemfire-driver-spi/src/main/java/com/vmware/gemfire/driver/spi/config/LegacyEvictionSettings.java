// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi.config;

import java.util.Map;
import java.util.Objects;

/**
 * Immutable legacy eviction settings, replacing the legacy configuration-model eviction DTOs. {@link LegacyEvictionSettingsFactory#generate} selects
 * the heap, memory or entry branch exactly as the legacy {@code EvictionAttributes.generate} did, and
 * {@link #parseAction(String)} reproduces its action parsing, so the {@code none} action fails with the legacy message.
 * Only the memory and heap branches carry a sizer; the entry branch ignores it, as the legacy code did. For
 * algorithm-based eviction use {@link EvictionAttributes}.
 */
public final class LegacyEvictionSettings {

	/** The branch of the legacy eviction configuration that was selected. */
	public enum Branch { LRU_ENTRY_COUNT, LRU_HEAP_PERCENTAGE, LRU_MEMORY_SIZE }

	private final Branch branch;
	private final EvictionAction action;
	private final String maximum;
	private final String sizerClassName;
	private final Map<String, String> sizerParameters;

	LegacyEvictionSettings(Branch branch, EvictionAction action, String maximum, String sizerClassName, Map<String, String> sizerParameters) {
		this.branch = branch;
		this.action = action;
		this.maximum = maximum;
		this.sizerClassName = sizerClassName;
		this.sizerParameters = sizerParameters;
	}

	/**
	 * Parses an action token. A blank token means {@link EvictionAction#LOCAL_DESTROY}; anything other than
	 * {@code local-destroy} or {@code overflow-to-disk}, including {@code none}, is rejected.
	 */
	public static EvictionAction parseAction(String action) {
		if (EvictionAction.LOCAL_DESTROY.toString().equals(action)) return EvictionAction.LOCAL_DESTROY;
		if (EvictionAction.OVERFLOW_TO_DISK.toString().equals(action)) return EvictionAction.OVERFLOW_TO_DISK;
		if (action == null || action.isBlank()) return EvictionAction.LOCAL_DESTROY;
		throw new IllegalArgumentException("invalid eviction action: " + action);
	}

	public Branch getBranch() { return branch; }

	/** Either {@link EvictionAction#LOCAL_DESTROY} or {@link EvictionAction#OVERFLOW_TO_DISK}. */
	public EvictionAction getAction() { return action; }

	/** The maximum as text, or {@code null} for the heap branch. */
	public String getMaximum() { return maximum; }

	/** The sizer class name, empty when a blank name was given, or {@code null} when there is no sizer. */
	public String getSizerClassName() { return sizerClassName; }

	/** The sizer parameters ordered by name; never {@code null}. */
	public Map<String, String> getSizerParameters() { return sizerParameters; }

	@Override public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof LegacyEvictionSettings that)) return false;
		return branch == that.branch && action == that.action && Objects.equals(maximum, that.maximum)
				&& Objects.equals(sizerClassName, that.sizerClassName) && sizerParameters.equals(that.sizerParameters);
	}

	@Override public int hashCode() { return Objects.hash(branch, action, maximum, sizerClassName, sizerParameters); }

	@Override public String toString() {
		return "LegacyEvictionSettings[" + branch + ", action=" + action + ", maximum=" + maximum + ", sizer=" + sizerClassName + sizerParameters + "]";
	}
}
