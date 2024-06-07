/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.RegionShortcut;

import org.springframework.util.ObjectUtils;

/**
 * The RegionShortcutWrapper enum is a Java enumerated type that wraps GemFire's RegionShortcuts
 * with Spring Data GemFire RegionShortcutWrapper enumerated values.
 *
 * @author John Blum
 * @see RegionShortcut
 * @since 1.4.0
 */
@SuppressWarnings("unused")
public enum RegionShortcutWrapper {

	LOCAL(RegionShortcut.LOCAL, DataPolicy.NORMAL),
	LOCAL_HEAP_LRU(RegionShortcut.LOCAL_HEAP_LRU, DataPolicy.NORMAL),
	LOCAL_OVERFLOW(RegionShortcut.LOCAL_OVERFLOW, DataPolicy.NORMAL),
	LOCAL_PERSISTENT(RegionShortcut.LOCAL_PERSISTENT, DataPolicy.PERSISTENT_REPLICATE),
	LOCAL_PERSISTENT_OVERFLOW(RegionShortcut.LOCAL_PERSISTENT_OVERFLOW, DataPolicy.PERSISTENT_REPLICATE),
	REPLICATE(RegionShortcut.REPLICATE, DataPolicy.REPLICATE),
	REPLICATE_HEAP_LRU(RegionShortcut.REPLICATE_HEAP_LRU, DataPolicy.REPLICATE),
	REPLICATE_OVERFLOW(RegionShortcut.REPLICATE_OVERFLOW, DataPolicy.REPLICATE),
	REPLICATE_PERSISTENT(RegionShortcut.REPLICATE_PERSISTENT, DataPolicy.PERSISTENT_REPLICATE),
	REPLICATE_PERSISTENT_OVERFLOW(RegionShortcut.REPLICATE_PERSISTENT_OVERFLOW, DataPolicy.PERSISTENT_REPLICATE),
	REPLICATE_PROXY(RegionShortcut.REPLICATE_PROXY, DataPolicy.EMPTY),
	UNSPECIFIED(null, null);

	private final DataPolicy dataPolicy;

	private final RegionShortcut regionShortcut;

	RegionShortcutWrapper(RegionShortcut regionShortcut, DataPolicy dataPolicy) {
		this.regionShortcut = regionShortcut;
		this.dataPolicy = dataPolicy;
	}

	public static RegionShortcutWrapper valueOf(RegionShortcut regionShortcut) {

		for (RegionShortcutWrapper wrapper : values()) {
			if (ObjectUtils.nullSafeEquals(wrapper.getRegionShortcut(), regionShortcut)) {
				return wrapper;
			}
		}

		return RegionShortcutWrapper.UNSPECIFIED;
	}

	public DataPolicy getDataPolicy() {
		return this.dataPolicy;
	}

	public RegionShortcut getRegionShortcut() {
		return this.regionShortcut;
	}

	public boolean isHeapLru() {
		return name().contains("HEAP_LRU");
	}

	public boolean isLocal() {
		return name().contains("LOCAL");
	}

	public boolean isOverflow() {
		return name().contains("OVERFLOW");
	}

	public boolean isPersistent() {
		return name().contains("PERSISTENT");
	}

	public boolean isPersistentOverflow() {
		return (isOverflow() && isPersistent());
	}

	public boolean isProxy() {
		return name().contains("PROXY");
	}

	public boolean isRedundant() {
		return name().contains("REDUNDANT");
	}

	public boolean isReplicate() {
		return name().contains("REPLICATE");
	}
}
