/*
 * Copyright 2022-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.EvictionAction;
import org.apache.geode.cache.EvictionAlgorithm;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionShortcut;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.data.gemfire.util.RegionUtils;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the use of {@link RegionShortcut} in SDG XML namespace configuration metadata.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.DataPolicy
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.RegionShortcut
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.4.0
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class RegionDataPolicyShortcutsIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("LocalWithDataPolicy")
	private Region<?, ?> localWithDataPolicy;

	@Autowired
	@Qualifier("LocalWithShortcut")
	private Region<?, ?> localWithShortcut;

	@Autowired
	@Qualifier("ShortcutDefaults")
	private Region<?, ?> shortcutDefaults;

	@Autowired
	@Qualifier("ShortcutOverrides")
	private Region<?, ?> shortcutOverrides;

	@Test
	public void localRegionWithDataPolicyIsCorrect() {

		Assertions.assertThat(localWithDataPolicy)
			.describedAs("A reference to the 'LocalWithDataPolicy' Region was not property configured")
			.isNotNull();

		Assertions.assertThat(localWithDataPolicy.getName()).isEqualTo("LocalWithDataPolicy");
		Assertions.assertThat(localWithDataPolicy.getFullPath()).isEqualTo("/LocalWithDataPolicy");
		Assertions.assertThat(localWithDataPolicy.getAttributes()).isNotNull();
		Assertions.assertThat(localWithDataPolicy.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
	}

	@Test
	public void localRegionWithShortcutIsCorrect() {

		Assertions.assertThat(localWithShortcut)
			.describedAs("A reference to the 'LocalWithShortcut' Region was not property configured")
			.isNotNull();

		Assertions.assertThat(localWithShortcut.getName()).isEqualTo("LocalWithShortcut");
		Assertions.assertThat(localWithShortcut.getFullPath()).isEqualTo("/LocalWithShortcut");
		Assertions.assertThat(localWithShortcut.getAttributes()).isNotNull();
		Assertions.assertThat(localWithShortcut.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.PERSISTENT_REPLICATE);
	}

	@Test
	public void shortcutDefaultsRegionIsCorrect() {

		Assertions.assertThat(shortcutDefaults)
			.describedAs("A reference to the 'ShortcutDefaults' Region was not properly configured")
			.isNotNull();

		Assertions.assertThat(shortcutDefaults.getName()).isEqualTo("ShortcutDefaults");
		Assertions.assertThat(shortcutDefaults.getFullPath()).isEqualTo(RegionUtils.toRegionPath("ShortcutDefaults"));
		Assertions.assertThat(shortcutDefaults.getAttributes()).isNotNull();
		Assertions.assertThat(shortcutDefaults.getAttributes().getCloningEnabled()).isFalse();
		Assertions.assertThat(shortcutDefaults.getAttributes().getConcurrencyChecksEnabled()).isTrue();
		Assertions.assertThat(shortcutDefaults.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.PERSISTENT_REPLICATE);
		Assertions.assertThat(shortcutDefaults.getAttributes().isDiskSynchronous()).isFalse();
		Assertions.assertThat(shortcutDefaults.getAttributes().getInitialCapacity()).isEqualTo(101);
		Assertions.assertThat(shortcutDefaults.getAttributes().getLoadFactor()).isEqualTo(0.85f);
		Assertions.assertThat(shortcutDefaults.getAttributes().getKeyConstraint()).isEqualTo(Long.class);
		Assertions.assertThat(shortcutDefaults.getAttributes().getValueConstraint()).isEqualTo(String.class);
		Assertions.assertThat(shortcutDefaults.getAttributes().getEvictionAttributes()).isNotNull();
		Assertions.assertThat(shortcutDefaults.getAttributes().getPartitionAttributes()).isNull();
	}

	@Test
	public void shortcutOverridesRegionIsCorrect() {

		Assertions.assertThat(shortcutOverrides)
			.describedAs("A reference to the 'ShortcutOverrides' Region was not properly configured")
			.isNotNull();

		Assertions.assertThat(shortcutOverrides.getName()).isEqualTo("ShortcutOverrides");
		Assertions.assertThat(shortcutOverrides.getFullPath()).isEqualTo(RegionUtils.toRegionPath("ShortcutOverrides"));
		Assertions.assertThat(shortcutOverrides.getAttributes()).isNotNull();
		Assertions.assertThat(shortcutOverrides.getAttributes().getCloningEnabled()).isTrue();
		Assertions.assertThat(shortcutOverrides.getAttributes().getConcurrencyChecksEnabled()).isFalse();
		Assertions.assertThat(shortcutOverrides.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		Assertions.assertThat(shortcutOverrides.getAttributes().isDiskSynchronous()).isTrue();
		Assertions.assertThat(shortcutOverrides.getAttributes().getInitialCapacity()).isEqualTo(51);
		Assertions.assertThat(shortcutOverrides.getAttributes().getLoadFactor()).isEqualTo(0.72f);
		Assertions.assertThat(shortcutOverrides.getAttributes().getKeyConstraint()).isEqualTo(String.class);
		Assertions.assertThat(shortcutOverrides.getAttributes().getValueConstraint()).isEqualTo(Object.class);
		Assertions.assertThat(shortcutOverrides.getAttributes().getEvictionAttributes()).isNotNull();
		Assertions.assertThat(shortcutOverrides.getAttributes().getEvictionAttributes().getAction()).isEqualTo(EvictionAction.LOCAL_DESTROY);
		Assertions.assertThat(shortcutOverrides.getAttributes().getEvictionAttributes().getMaximum()).isEqualTo(8192);
		Assertions.assertThat(shortcutOverrides.getAttributes().getEvictionAttributes().getAlgorithm()).isEqualTo(EvictionAlgorithm.LRU_ENTRY);
		Assertions.assertThat(shortcutOverrides.getAttributes().getPartitionAttributes()).isNull();
	}
}
