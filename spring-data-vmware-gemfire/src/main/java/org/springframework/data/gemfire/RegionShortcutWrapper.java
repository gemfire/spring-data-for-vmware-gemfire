/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Migrated to GUD API types
 */

package org.springframework.data.gemfire;

import org.springframework.data.gemfire.gud.api.GudDataPolicy;
import org.springframework.data.gemfire.gud.api.GudRegionShortcut;
import org.springframework.util.ObjectUtils;

/**
 * The RegionShortcutWrapper enum is a Java enumerated type that wraps GemFire's RegionShortcuts
 * with Spring Data GemFire RegionShortcutWrapper enumerated values.
 *
 * @author John Blum
 * @see GudRegionShortcut
 * @since 1.4.0
 */
@SuppressWarnings("unused")
public enum RegionShortcutWrapper {

	LOCAL(GudRegionShortcut.LOCAL, GudDataPolicy.NORMAL),
	LOCAL_HEAP_LRU(GudRegionShortcut.LOCAL_HEAP_LRU, GudDataPolicy.NORMAL),
	LOCAL_OVERFLOW(GudRegionShortcut.LOCAL_OVERFLOW, GudDataPolicy.NORMAL),
	LOCAL_PERSISTENT(GudRegionShortcut.LOCAL_PERSISTENT, GudDataPolicy.NORMAL),
	LOCAL_PERSISTENT_OVERFLOW(GudRegionShortcut.LOCAL_PERSISTENT_OVERFLOW, GudDataPolicy.NORMAL),
	UNSPECIFIED(null, null);

	private final GudDataPolicy dataPolicy;

	private final GudRegionShortcut regionShortcut;

	RegionShortcutWrapper(GudRegionShortcut regionShortcut, GudDataPolicy dataPolicy) {
		this.regionShortcut = regionShortcut;
		this.dataPolicy = dataPolicy;
	}

	public static RegionShortcutWrapper valueOf(GudRegionShortcut regionShortcut) {

		for (RegionShortcutWrapper wrapper : values()) {
			if (ObjectUtils.nullSafeEquals(wrapper.getRegionShortcut(), regionShortcut)) {
				return wrapper;
			}
		}

		return RegionShortcutWrapper.UNSPECIFIED;
	}

	public GudDataPolicy getDataPolicy() {
		return this.dataPolicy;
	}

	public GudRegionShortcut getRegionShortcut() {
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
