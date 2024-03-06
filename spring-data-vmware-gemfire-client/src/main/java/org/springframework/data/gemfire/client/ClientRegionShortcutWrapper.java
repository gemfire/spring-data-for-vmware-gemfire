/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.client;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.client.ClientRegionShortcut;

import org.springframework.util.ObjectUtils;

/**
 * The ClientRegionShortcutWrapper enum is a Java enumerated type that wraps GemFire's ClientRegionShortcuts
 * with Spring Data GemFire ClientRegionShortcutWrapper enumerated values.
 *
 * @author John Blum
 * @see ClientRegionShortcut
 * @since 1.4.0
 */
@SuppressWarnings("unused")
public enum ClientRegionShortcutWrapper {

	CACHING_PROXY(ClientRegionShortcut.CACHING_PROXY, DataPolicy.NORMAL),
	CACHING_PROXY_HEAP_LRU(ClientRegionShortcut.CACHING_PROXY_HEAP_LRU, DataPolicy.NORMAL),
	CACHING_PROXY_OVERFLOW(ClientRegionShortcut.CACHING_PROXY_OVERFLOW, DataPolicy.NORMAL),
	LOCAL(ClientRegionShortcut.LOCAL, DataPolicy.NORMAL),
	LOCAL_HEAP_LRU(ClientRegionShortcut.LOCAL_HEAP_LRU, DataPolicy.NORMAL),
	LOCAL_OVERFLOW(ClientRegionShortcut.LOCAL_OVERFLOW, DataPolicy.NORMAL),
	LOCAL_PERSISTENT(ClientRegionShortcut.LOCAL_PERSISTENT, DataPolicy.PERSISTENT_REPLICATE),
	LOCAL_PERSISTENT_OVERFLOW(ClientRegionShortcut.LOCAL_PERSISTENT_OVERFLOW, DataPolicy.PERSISTENT_REPLICATE),
	PROXY(ClientRegionShortcut.PROXY, DataPolicy.EMPTY),
	UNSPECIFIED(null, null);

	private final ClientRegionShortcut clientRegionShortcut;

	private final DataPolicy dataPolicy;

	public static ClientRegionShortcutWrapper valueOf(ClientRegionShortcut clientRegionShortcut) {

		for (ClientRegionShortcutWrapper wrapper : values()) {
			if (ObjectUtils.nullSafeEquals(wrapper.getClientRegionShortcut(), clientRegionShortcut)) {
				return wrapper;
			}
		}

		return ClientRegionShortcutWrapper.UNSPECIFIED;
	}

	ClientRegionShortcutWrapper(ClientRegionShortcut clientRegionShortcut, DataPolicy dataPolicy) {
		this.clientRegionShortcut = clientRegionShortcut;
		this.dataPolicy = dataPolicy;
	}

	public ClientRegionShortcut getClientRegionShortcut() {
		return this.clientRegionShortcut;
	}

	public DataPolicy getDataPolicy() {
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
