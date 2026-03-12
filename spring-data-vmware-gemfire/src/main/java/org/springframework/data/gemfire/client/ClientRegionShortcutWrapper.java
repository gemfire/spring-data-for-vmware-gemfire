/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.client;

import org.springframework.data.gemfire.gud.api.GudClientRegionShortcut;
import org.springframework.data.gemfire.gud.api.GudDataPolicy;

import org.springframework.util.ObjectUtils;

/**
 * The ClientRegionShortcutWrapper enum is a Java enumerated type that wraps GemFire's ClientRegionShortcuts
 * with Spring Data GemFire ClientRegionShortcutWrapper enumerated values.
 *
 * @author John Blum
 * @see GudClientRegionShortcut
 * @since 1.4.0
 */
@SuppressWarnings("unused")
public enum ClientRegionShortcutWrapper {

	CACHING_PROXY(GudClientRegionShortcut.CACHING_PROXY, GudDataPolicy.NORMAL),
	CACHING_PROXY_HEAP_LRU(GudClientRegionShortcut.CACHING_PROXY_HEAP_LRU, GudDataPolicy.NORMAL),
	CACHING_PROXY_OVERFLOW(GudClientRegionShortcut.CACHING_PROXY_OVERFLOW, GudDataPolicy.NORMAL),
	LOCAL(GudClientRegionShortcut.LOCAL, GudDataPolicy.NORMAL),
	LOCAL_HEAP_LRU(GudClientRegionShortcut.LOCAL_HEAP_LRU, GudDataPolicy.NORMAL),
	LOCAL_OVERFLOW(GudClientRegionShortcut.LOCAL_OVERFLOW, GudDataPolicy.NORMAL),
	LOCAL_PERSISTENT(GudClientRegionShortcut.LOCAL_PERSISTENT, GudDataPolicy.PERSISTENT_REPLICATE),
	LOCAL_PERSISTENT_OVERFLOW(GudClientRegionShortcut.LOCAL_PERSISTENT_OVERFLOW, GudDataPolicy.PERSISTENT_REPLICATE),
	PROXY(GudClientRegionShortcut.PROXY, GudDataPolicy.EMPTY),
	UNSPECIFIED(null, null);

	private final GudClientRegionShortcut clientRegionShortcut;

	private final GudDataPolicy dataPolicy;

	public static ClientRegionShortcutWrapper valueOf(GudClientRegionShortcut clientRegionShortcut) {

		for (ClientRegionShortcutWrapper wrapper : values()) {
			if (ObjectUtils.nullSafeEquals(wrapper.getClientRegionShortcut(), clientRegionShortcut)) {
				return wrapper;
			}
		}

		return ClientRegionShortcutWrapper.UNSPECIFIED;
	}

	ClientRegionShortcutWrapper(GudClientRegionShortcut clientRegionShortcut, GudDataPolicy dataPolicy) {
		this.clientRegionShortcut = clientRegionShortcut;
		this.dataPolicy = dataPolicy;
	}

	public GudClientRegionShortcut getClientRegionShortcut() {
		return this.clientRegionShortcut;
	}

	public GudDataPolicy getDataPolicy() {
		return this.dataPolicy;
	}

	public boolean isCaching() {
		return name().contains("CACHING");
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
		return name().contains("PERSISTENT_OVERFLOW");
	}

	public boolean isProxy() {
		return name().contains("PROXY");
	}
}
