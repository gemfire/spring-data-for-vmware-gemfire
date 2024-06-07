/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.RegionShortcut;

/**
 * Unit tests for {@link RegionShortcutToDataPolicyConverter}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.DataPolicy
 * @see org.apache.geode.cache.RegionShortcut
 * @see org.springframework.data.gemfire.RegionShortcutToDataPolicyConverter
 * @since 2.0.2
 */
public class RegionShortcutToDataPolicyConverterUnitTests {

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

	protected void assertDataPolicyReplicate(DataPolicy actual) {
		assertDataPolicy(actual, DataPolicy.REPLICATE);
	}

	protected DataPolicy convert(RegionShortcut regionShortcut) {
		return RegionShortcutToDataPolicyConverter.INSTANCE.convert(regionShortcut);
	}

	@Test
	public void nullRegionShortcutIsDataPolicyDefault() {
		assertDataPolicyDefault(convert(null));
	}

	@Test
	public void regionShortcutLocalIsDataPolicyNormal() {
		assertDataPolicyNormal(convert(RegionShortcut.LOCAL));
	}

	@Test
	public void regionShortcutLocalHeapLruIsDataPolicyNormal() {
		assertDataPolicyNormal(convert(RegionShortcut.LOCAL_HEAP_LRU));
	}

	@Test
	public void regionShortcutLocalOverflowIsDataPolicyNormal() {
		assertDataPolicyNormal(convert(RegionShortcut.LOCAL_HEAP_LRU));
	}

	@Test
	public void regionShortcutLocalPersistentIsDataPolicyPersistentReplicate() {
		assertDataPolicyPersistentReplicate(convert(RegionShortcut.LOCAL_PERSISTENT));
	}

	@Test
	public void regionShortcutLocalPersistentOverflowIsDataPolicyPersistentReplicate() {
		assertDataPolicyPersistentReplicate(convert(RegionShortcut.LOCAL_PERSISTENT_OVERFLOW));
	}

	@Test
	public void regionShortcutReplicateIsDataPolicyReplicate() {
		assertDataPolicyReplicate(convert(RegionShortcut.REPLICATE));
	}

	@Test
	public void regionShortcutReplicateHeapLruIsDataPolicyReplicate() {
		assertDataPolicyReplicate(convert(RegionShortcut.REPLICATE_HEAP_LRU));
	}

	@Test
	public void regionShortcutReplicateOverflowIsDataPolicyReplicate() {
		assertDataPolicyReplicate(convert(RegionShortcut.REPLICATE_OVERFLOW));
	}

	@Test
	public void regionShortcutReplicatePersistentIsDataPolicyPersistentReplicate() {
		assertDataPolicyPersistentReplicate(convert(RegionShortcut.REPLICATE_PERSISTENT));
	}

	@Test
	public void regionShortcutReplicatePersistentOverflowIsDataPolicyPersistentReplicate() {
		assertDataPolicyPersistentReplicate(convert(RegionShortcut.REPLICATE_PERSISTENT_OVERFLOW));
	}

	@Test
	public void regionShortcutReplicateProxyIsDataPolicyPersistentReplicate() {
		assertDataPolicyEmpty(convert(RegionShortcut.REPLICATE_PROXY));
	}
}
