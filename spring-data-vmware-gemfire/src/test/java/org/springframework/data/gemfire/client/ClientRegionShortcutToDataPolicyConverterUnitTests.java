/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.springframework.data.gemfire.gud.api.GudDataPolicy;
import org.springframework.data.gemfire.gud.api.GudClientRegionShortcut;

/**
 * Unit tests for {@link ClientRegionShortcutToDataPolicyConverter}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.GudDataPolicy
 * @see org.apache.geode.cache.client.GudClientRegionShortcut
 * @see org.springframework.data.gemfire.client.ClientRegionShortcutToDataPolicyConverter
 * @since 2.0.2
 */
public class ClientRegionShortcutToDataPolicyConverterUnitTests {

	protected void assertDataPolicy(GudDataPolicy actual, GudDataPolicy expected) {
		assertThat(actual).isEqualTo(expected);
	}

	protected void assertDataPolicyDefault(GudDataPolicy actual) {
		assertDataPolicy(actual, GudDataPolicy.DEFAULT);
	}

	protected void assertDataPolicyEmpty(GudDataPolicy actual) {
		assertDataPolicy(actual, GudDataPolicy.EMPTY);
	}

	protected void assertDataPolicyNormal(GudDataPolicy actual) {
		assertDataPolicy(actual, GudDataPolicy.NORMAL);
	}

	protected void assertDataPolicyPersistentReplicate(GudDataPolicy actual) {
		assertDataPolicy(actual, GudDataPolicy.PERSISTENT_REPLICATE);
	}

	protected GudDataPolicy convert(GudClientRegionShortcut clientRegionShortcut) {
		return ClientRegionShortcutToDataPolicyConverter.INSTANCE.convert(clientRegionShortcut);
	}

	@Test
	public void clientRegionShortcutCachingProxyIsDataPolicyNormal() {
		assertDataPolicyNormal(convert(GudClientRegionShortcut.CACHING_PROXY));
	}

	@Test
	public void clientRegionShortcutCachingProxyHeapLruIsDataPolicyNormal() {
		assertDataPolicyNormal(convert(GudClientRegionShortcut.CACHING_PROXY_HEAP_LRU));
	}

	@Test
	public void clientRegionShortcutCachingProxyOverflowIsDataPolicyNormal() {
		assertDataPolicyNormal(convert(GudClientRegionShortcut.CACHING_PROXY_OVERFLOW));
	}

	@Test
	public void clientRegionShortcutLocalIsDataPolicyNormal() {
		assertDataPolicyNormal(convert(GudClientRegionShortcut.LOCAL));
	}

	@Test
	public void clientRegionShortcutLocalHeapLruIsDataPolicyNormal() {
		assertDataPolicyNormal(convert(GudClientRegionShortcut.LOCAL_HEAP_LRU));
	}

	@Test
	public void clientRegionShortcutLocalOverflowIsDataPolicyNormal() {
		assertDataPolicyNormal(convert(GudClientRegionShortcut.LOCAL_OVERFLOW));
	}

	@Test
	public void clientRegionShortcutLocalPersistentIsDataPolicyPersistentReplicate() {
		assertDataPolicyPersistentReplicate(convert(GudClientRegionShortcut.LOCAL_PERSISTENT));
	}

	@Test
	public void clientRegionShortcutLocalPersistentOverflowIsDataPolicyPersistentReplicate() {
		assertDataPolicyPersistentReplicate(convert(GudClientRegionShortcut.LOCAL_PERSISTENT_OVERFLOW));
	}

	@Test
	public void clientRegionShortcutLocalProxyIsDataPolicyEmpty() {
		assertDataPolicyEmpty(convert(GudClientRegionShortcut.PROXY));
	}

	@Test
	public void nullClientRegionShortcutIsDataPolicyDefault() {
		assertDataPolicyDefault(convert(null));
	}
}
