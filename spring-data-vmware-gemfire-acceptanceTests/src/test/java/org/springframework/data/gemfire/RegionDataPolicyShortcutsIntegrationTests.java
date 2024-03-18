/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
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
	@Qualifier("PartitionWithDataPolicy")
	private Region<?, ?> partitionWithDataPolicy;

	@Autowired
	@Qualifier("PartitionWithShortcut")
	private Region<?, ?> partitionWithShortcut;

	@Autowired
	@Qualifier("ReplicateWithDataPolicy")
	private Region<?, ?> replicateWithDataPolicy;

	@Autowired
	@Qualifier("ReplicateWithShortcut")
	private Region<?, ?> replicateWithShortcut;

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
	public void partitionRegionWithDataPolicyIsCorrect() {

		Assertions.assertThat(partitionWithDataPolicy)
			.describedAs("A reference to the 'PartitionWithDataPolicy' Region was not property configured")
			.isNotNull();

		Assertions.assertThat(partitionWithDataPolicy.getName()).isEqualTo("PartitionWithDataPolicy");
		Assertions.assertThat(partitionWithDataPolicy.getFullPath()).isEqualTo("/PartitionWithDataPolicy");
		Assertions.assertThat(partitionWithDataPolicy.getAttributes()).isNotNull();
		Assertions.assertThat(partitionWithDataPolicy.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.PARTITION);
	}

	@Test
	public void partitionRegionWithShortcutIsCorrect() {

		Assertions.assertThat(partitionWithShortcut)
			.describedAs("A reference to the 'PartitionWithShortcut' Region was not property configured")
			.isNotNull();

		Assertions.assertThat(partitionWithShortcut.getName()).isEqualTo("PartitionWithShortcut");
		Assertions.assertThat(partitionWithShortcut.getFullPath()).isEqualTo("/PartitionWithShortcut");
		Assertions.assertThat(partitionWithShortcut.getAttributes()).isNotNull();
		Assertions.assertThat(partitionWithShortcut.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.PERSISTENT_PARTITION);
	}

	@Test
	public void replicateRegionWithDataPolicyIsCorrect() {

		Assertions.assertThat(replicateWithDataPolicy)
			.describedAs("A reference to the 'ReplicateWithDataPolicy' Region was not property configured")
			.isNotNull();

		Assertions.assertThat(replicateWithDataPolicy.getName()).isEqualTo("ReplicateWithDataPolicy");
		Assertions.assertThat(replicateWithDataPolicy.getFullPath()).isEqualTo("/ReplicateWithDataPolicy");
		Assertions.assertThat(replicateWithDataPolicy.getAttributes()).isNotNull();
		Assertions.assertThat(replicateWithDataPolicy.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.REPLICATE);
	}

	@Test
	public void replicateRegionWithShortcutIsCorrect() {

		Assertions.assertThat(replicateWithShortcut)
			.describedAs("A reference to the 'ReplicateWithShortcut' Region was not property configured")
			.isNotNull();

		Assertions.assertThat(replicateWithShortcut.getName()).isEqualTo("ReplicateWithShortcut");
		Assertions.assertThat(replicateWithShortcut.getFullPath()).isEqualTo("/ReplicateWithShortcut");
		Assertions.assertThat(replicateWithShortcut.getAttributes()).isNotNull();
		Assertions.assertThat(replicateWithShortcut.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.PERSISTENT_REPLICATE);
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
		Assertions.assertThat(shortcutDefaults.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.PERSISTENT_PARTITION);
		Assertions.assertThat(shortcutDefaults.getAttributes().isDiskSynchronous()).isFalse();
		Assertions.assertThat(shortcutDefaults.getAttributes().getIgnoreJTA()).isTrue();
		Assertions.assertThat(shortcutDefaults.getAttributes().getInitialCapacity()).isEqualTo(101);
		Assertions.assertThat(shortcutDefaults.getAttributes().getLoadFactor()).isEqualTo(0.85f);
		Assertions.assertThat(shortcutDefaults.getAttributes().getKeyConstraint()).isEqualTo(Long.class);
		Assertions.assertThat(shortcutDefaults.getAttributes().getMulticastEnabled()).isFalse();
		Assertions.assertThat(shortcutDefaults.getAttributes().getValueConstraint()).isEqualTo(String.class);
		Assertions.assertThat(shortcutDefaults.getAttributes().getEvictionAttributes()).isNotNull();
		Assertions.assertThat(shortcutDefaults.getAttributes().getEvictionAttributes().getAction()).isEqualTo(EvictionAction.OVERFLOW_TO_DISK);
		Assertions.assertThat(shortcutDefaults.getAttributes().getEvictionAttributes().getAlgorithm()).isEqualTo(EvictionAlgorithm.LRU_HEAP);
		Assertions.assertThat(shortcutDefaults.getAttributes().getPartitionAttributes()).isNotNull();
		Assertions.assertThat(shortcutDefaults.getAttributes().getPartitionAttributes().getRedundantCopies()).isEqualTo(1);
		Assertions.assertThat(shortcutDefaults.getAttributes().getPartitionAttributes().getTotalNumBuckets()).isEqualTo(177);
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
		Assertions.assertThat(shortcutOverrides.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.PARTITION);
		Assertions.assertThat(shortcutOverrides.getAttributes().isDiskSynchronous()).isTrue();
		Assertions.assertThat(shortcutOverrides.getAttributes().getIgnoreJTA()).isFalse();
		Assertions.assertThat(shortcutOverrides.getAttributes().getInitialCapacity()).isEqualTo(51);
		Assertions.assertThat(shortcutOverrides.getAttributes().getLoadFactor()).isEqualTo(0.72f);
		Assertions.assertThat(shortcutOverrides.getAttributes().getKeyConstraint()).isEqualTo(String.class);
		Assertions.assertThat(shortcutOverrides.getAttributes().getValueConstraint()).isEqualTo(Object.class);
		Assertions.assertThat(shortcutOverrides.getAttributes().getEvictionAttributes()).isNotNull();
		Assertions.assertThat(shortcutOverrides.getAttributes().getEvictionAttributes().getAction()).isEqualTo(EvictionAction.LOCAL_DESTROY);
		Assertions.assertThat(shortcutOverrides.getAttributes().getEvictionAttributes().getMaximum()).isEqualTo(8192);
		Assertions.assertThat(shortcutOverrides.getAttributes().getEvictionAttributes().getAlgorithm()).isEqualTo(EvictionAlgorithm.LRU_ENTRY);
		Assertions.assertThat(shortcutOverrides.getAttributes().getPartitionAttributes()).isNotNull();
		Assertions.assertThat(shortcutOverrides.getAttributes().getPartitionAttributes().getRedundantCopies()).isEqualTo(3);
		Assertions.assertThat(shortcutOverrides.getAttributes().getPartitionAttributes().getTotalNumBuckets()).isEqualTo(111);
	}
}
