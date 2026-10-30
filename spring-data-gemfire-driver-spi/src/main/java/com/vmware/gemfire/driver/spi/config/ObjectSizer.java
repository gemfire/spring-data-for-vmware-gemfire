// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi.config;

/** Entry-size estimator for memory-based eviction. The constants are provider-neutral strategy markers that the
 * provider maps to its own sizer; {@link #sizeof(Object)} on them is unsupported. Applications may supply their
 * own implementation, which the provider invokes.
 */
public interface ObjectSizer {
	ObjectSizer SIZE_CLASS_ONCE = new Strategy("SIZE_CLASS_ONCE");
	ObjectSizer REFLECTION_SIZE = new Strategy("REFLECTION_SIZE");
	ObjectSizer DEFAULT = SIZE_CLASS_ONCE;
	int sizeof(Object value);

	/** Marker for a provider-implemented sizing strategy. */
	final class Strategy implements ObjectSizer {
		private final String name;
		private Strategy(String name) { this.name = name; }
		public String name() { return name; }
		@Override public int sizeof(Object value) { throw new UnsupportedOperationException("Provider strategy " + name + " sizes inside the provider"); }
		@Override public String toString() { return name; }
	}
}
