/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.springframework.data.gemfire.gud.api.GudDataPolicy;
import org.springframework.data.gemfire.gud.api.GudRegionShortcut;

/**
 * Unit tests for {@link RegionShortcutToDataPolicyConverter}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.GudDataPolicy
 * @see org.apache.geode.cache.GudRegionShortcut
 * @see org.springframework.data.gemfire.RegionShortcutToDataPolicyConverter
 * @since 2.0.2
 */
public class RegionShortcutToDataPolicyConverterUnitTests {

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

	protected GudDataPolicy convert(GudRegionShortcut regionShortcut) {
		return RegionShortcutToDataPolicyConverter.INSTANCE.convert(regionShortcut);
	}

	@Test
	public void nullRegionShortcutIsDataPolicyDefault() {
		assertDataPolicyDefault(convert(null));
	}

	@Test
	public void regionShortcutLocalIsDataPolicyNormal() {
		assertDataPolicyNormal(convert(GudRegionShortcut.LOCAL));
	}

	@Test
	public void regionShortcutLocalHeapLruIsDataPolicyNormal() {
		assertDataPolicyNormal(convert(GudRegionShortcut.LOCAL_HEAP_LRU));
	}

	@Test
	public void regionShortcutLocalOverflowIsDataPolicyNormal() {
		assertDataPolicyNormal(convert(GudRegionShortcut.LOCAL_HEAP_LRU));
	}
}
