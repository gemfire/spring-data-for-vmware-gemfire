/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.EvictionAction;
import org.apache.geode.cache.EvictionAlgorithm;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionShortcut;
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

		assertThat(localWithDataPolicy)
			.describedAs("A reference to the 'LocalWithDataPolicy' Region was not property configured")
			.isNotNull();

		assertThat(localWithDataPolicy.getName()).isEqualTo("LocalWithDataPolicy");
		assertThat(localWithDataPolicy.getFullPath()).isEqualTo("/LocalWithDataPolicy");
		assertThat(localWithDataPolicy.getAttributes()).isNotNull();
		assertThat(localWithDataPolicy.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
	}

	@Test
	public void localRegionWithShortcutIsCorrect() {

		assertThat(localWithShortcut)
			.describedAs("A reference to the 'LocalWithShortcut' Region was not property configured")
			.isNotNull();

		assertThat(localWithShortcut.getName()).isEqualTo("LocalWithShortcut");
		assertThat(localWithShortcut.getFullPath()).isEqualTo("/LocalWithShortcut");
		assertThat(localWithShortcut.getAttributes()).isNotNull();
		assertThat(localWithShortcut.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.PERSISTENT_REPLICATE);
	}

	@Test
	public void shortcutDefaultsRegionIsCorrect() {

		assertThat(shortcutDefaults)
			.describedAs("A reference to the 'ShortcutDefaults' Region was not properly configured")
			.isNotNull();

		assertThat(shortcutDefaults.getName()).isEqualTo("ShortcutDefaults");
		assertThat(shortcutDefaults.getFullPath()).isEqualTo(RegionUtils.toRegionPath("ShortcutDefaults"));
		assertThat(shortcutDefaults.getAttributes()).isNotNull();
		assertThat(shortcutDefaults.getAttributes().getCloningEnabled()).isFalse();
		assertThat(shortcutDefaults.getAttributes().getConcurrencyChecksEnabled()).isTrue();
		assertThat(shortcutDefaults.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.PERSISTENT_REPLICATE);
		assertThat(shortcutDefaults.getAttributes().isDiskSynchronous()).isFalse();
		assertThat(shortcutDefaults.getAttributes().getInitialCapacity()).isEqualTo(101);
		assertThat(shortcutDefaults.getAttributes().getLoadFactor()).isEqualTo(0.85f);
		assertThat(shortcutDefaults.getAttributes().getKeyConstraint()).isEqualTo(Long.class);
		assertThat(shortcutDefaults.getAttributes().getValueConstraint()).isEqualTo(String.class);
		assertThat(shortcutDefaults.getAttributes().getEvictionAttributes()).isNotNull();
		assertThat(shortcutDefaults.getAttributes().getPartitionAttributes()).isNull();
	}

	@Test
	public void shortcutOverridesRegionIsCorrect() {

		assertThat(shortcutOverrides)
			.describedAs("A reference to the 'ShortcutOverrides' Region was not properly configured")
			.isNotNull();

		assertThat(shortcutOverrides.getName()).isEqualTo("ShortcutOverrides");
		assertThat(shortcutOverrides.getFullPath()).isEqualTo(RegionUtils.toRegionPath("ShortcutOverrides"));
		assertThat(shortcutOverrides.getAttributes()).isNotNull();
		assertThat(shortcutOverrides.getAttributes().getCloningEnabled()).isTrue();
		assertThat(shortcutOverrides.getAttributes().getConcurrencyChecksEnabled()).isFalse();
		assertThat(shortcutOverrides.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		assertThat(shortcutOverrides.getAttributes().isDiskSynchronous()).isTrue();
		assertThat(shortcutOverrides.getAttributes().getInitialCapacity()).isEqualTo(51);
		assertThat(shortcutOverrides.getAttributes().getLoadFactor()).isEqualTo(0.72f);
		assertThat(shortcutOverrides.getAttributes().getKeyConstraint()).isEqualTo(String.class);
		assertThat(shortcutOverrides.getAttributes().getValueConstraint()).isEqualTo(Object.class);
		assertThat(shortcutOverrides.getAttributes().getEvictionAttributes()).isNotNull();
		assertThat(shortcutOverrides.getAttributes().getEvictionAttributes().getAction()).isEqualTo(EvictionAction.LOCAL_DESTROY);
		assertThat(shortcutOverrides.getAttributes().getEvictionAttributes().getMaximum()).isEqualTo(8192);
		assertThat(shortcutOverrides.getAttributes().getEvictionAttributes().getAlgorithm()).isEqualTo(EvictionAlgorithm.LRU_ENTRY);
		assertThat(shortcutOverrides.getAttributes().getPartitionAttributes()).isNull();
	}
}
