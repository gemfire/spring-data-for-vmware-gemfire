// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi.config;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/** Builds {@link LegacyEvictionSettings} the way the legacy {@code EvictionAttributes.generate} did. */
public final class LegacyEvictionSettingsFactory {

	private static final String CLASS_NAME = "([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)*[\\p{L}_$][\\p{L}\\p{N}_$]*";

	private LegacyEvictionSettingsFactory() { }

	/**
	 * Builds the settings the way the legacy generator did.
	 *
	 * @param action the action token, parsed by {@link LegacyEvictionSettings#parseAction(String)}
	 * @param maxMemory the memory maximum, selecting the memory branch when set
	 * @param maxEntryCount the entry maximum, selecting the entry branch when no memory maximum is set
	 * @param sizerClassName the sizer class, or {@code null}; a non-blank value must be a valid class name
	 * @param sizerParameters the sizer parameters, ignored when there is no sizer class name
	 * @return the settings, or {@code null} when every argument is {@code null}
	 */
	public static LegacyEvictionSettings generate(String action, Integer maxMemory, Integer maxEntryCount, String sizerClassName,
			Map<String, String> sizerParameters) {
		if (action == null && maxMemory == null && maxEntryCount == null && sizerClassName == null) return null;
		String sizer = null;
		Map<String, String> parameters = Collections.emptyMap();
		if (sizerClassName != null) {
			if (sizerClassName.isBlank()) {
				sizer = "";
			} else {
				if (!sizerClassName.matches(CLASS_NAME)) throw new IllegalArgumentException("Invalid className");
				sizer = sizerClassName;
				if (sizerParameters != null) parameters = Collections.unmodifiableMap(new TreeMap<>(sizerParameters));
			}
		}
		EvictionAction parsed = LegacyEvictionSettings.parseAction(action);
		if (maxMemory == null && maxEntryCount == null) return new LegacyEvictionSettings(LegacyEvictionSettings.Branch.LRU_HEAP_PERCENTAGE, parsed, null, sizer, parameters);
		if (maxMemory != null) return new LegacyEvictionSettings(LegacyEvictionSettings.Branch.LRU_MEMORY_SIZE, parsed, maxMemory.toString(), sizer, parameters);
		return new LegacyEvictionSettings(LegacyEvictionSettings.Branch.LRU_ENTRY_COUNT, parsed, maxEntryCount.toString(), null, Collections.emptyMap());
	}
}
