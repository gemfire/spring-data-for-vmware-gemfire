/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.client.ClientRegionShortcut;

/**
 * Unit tests for {@link ClientRegionShortcutToDataPolicyConverter}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see DataPolicy
 * @see ClientRegionShortcut
 * @see ClientRegionShortcutToDataPolicyConverter
 * @since 2.0.2
 */
public class ClientRegionShortcutToDataPolicyConverterUnitTests {

	protected void assertDataPolicy(DataPolicy actual, DataPolicy expected) {
		assertThat(actual).isEqualTo(expected);
	}

	protected void assertDataPolicyDefault(DataPolicy actual) {
		assertDataPolicy(actual, DataPolicy.DEFAULT);
	}

	protected void assertDataPolicyEmpty(DataPolicy actual) {
		assertDataPolicy(actual, DataPolicy.EMPTY);
	}

	protected void assertDataPolicyNormal(DataPolicy actual) {
		assertDataPolicy(actual, DataPolicy.NORMAL);
	}

	protected void assertDataPolicyPersistentReplicate(DataPolicy actual) {
		assertDataPolicy(actual, DataPolicy.PERSISTENT_REPLICATE);
	}

	protected DataPolicy convert(ClientRegionShortcut clientRegionShortcut) {
		return ClientRegionShortcutToDataPolicyConverter.INSTANCE.convert(clientRegionShortcut);
	}

	@Test
	public void clientRegionShortcutCachingProxyIsDataPolicyNormal() {
		assertDataPolicyNormal(convert(ClientRegionShortcut.CACHING_PROXY));
	}

	@Test
	public void clientRegionShortcutCachingProxyHeapLruIsDataPolicyNormal() {
		assertDataPolicyNormal(convert(ClientRegionShortcut.CACHING_PROXY_HEAP_LRU));
	}

	@Test
	public void clientRegionShortcutCachingProxyOverflowIsDataPolicyNormal() {
		assertDataPolicyNormal(convert(ClientRegionShortcut.CACHING_PROXY_OVERFLOW));
	}

	@Test
	public void clientRegionShortcutLocalIsDataPolicyNormal() {
		assertDataPolicyNormal(convert(ClientRegionShortcut.LOCAL));
	}

	@Test
	public void clientRegionShortcutLocalHeapLruIsDataPolicyNormal() {
		assertDataPolicyNormal(convert(ClientRegionShortcut.LOCAL_HEAP_LRU));
	}

	@Test
	public void clientRegionShortcutLocalOverflowIsDataPolicyNormal() {
		assertDataPolicyNormal(convert(ClientRegionShortcut.LOCAL_OVERFLOW));
	}

	@Test
	public void clientRegionShortcutLocalPersistentIsDataPolicyPersistentReplicate() {
		assertDataPolicyPersistentReplicate(convert(ClientRegionShortcut.LOCAL_PERSISTENT));
	}

	@Test
	public void clientRegionShortcutLocalPersistentOverflowIsDataPolicyPersistentReplicate() {
		assertDataPolicyPersistentReplicate(convert(ClientRegionShortcut.LOCAL_PERSISTENT_OVERFLOW));
	}

	@Test
	public void clientRegionShortcutLocalProxyIsDataPolicyEmpty() {
		assertDataPolicyEmpty(convert(ClientRegionShortcut.PROXY));
	}

	@Test
	public void nullClientRegionShortcutIsDataPolicyDefault() {
		assertDataPolicyDefault(convert(null));
	}
}
