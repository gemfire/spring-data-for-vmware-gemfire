/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import static org.assertj.core.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import com.vmware.gemfire.driver.spi.*;
import com.vmware.gemfire.driver.spi.config.*;

class MockRegionConfigurationTests {
	@Test void unavailableConfiguredEffectsFailBeforePublishingARegion() {
		try (var c = new MockConnection()) {
			List<RegionConfiguration<Object, Object>> configs = List.of(
				RegionConfiguration.builder().statisticsEnabled(true).build(), RegionConfiguration.builder().cloningEnabled(true).build(),
				RegionConfiguration.builder().poolName("pool").build(), RegionConfiguration.builder().diskStoreName("disk").build());
			for (var config : configs) {
				assertThatThrownBy(() -> c.createClientRegion("unsupported", ClientRegionShortcut.LOCAL, config))
					.isInstanceOfSatisfying(UnsupportedFeatureException.class, e -> assertThat(e.reason()).isEqualTo(UnsupportedFeatureException.Reason.NOT_INTRODUCED));
				assertThat(c.rootRegions()).isEmpty();
			}
			var r = c.createClientRegion("local", ClientRegionShortcut.LOCAL); var m = r.getAttributesMutator();
			assertThatThrownBy(() -> m.setCloningEnabled(true)).isInstanceOf(UnsupportedFeatureException.class);
			assertThat(m.getCloningEnabled()).isFalse(); }
	}
	@Test void clientRegionsReportAllSubscriptionPolicyWithoutSimulatingDelivery() {
		try (var c = new MockConnection()) {
			var region = c.createClientRegion("local", ClientRegionShortcut.LOCAL);
			assertThat(region.getAttributes().getSubscriptionAttributes()).isEqualTo(new SubscriptionAttributes(InterestPolicy.ALL));
			assertThat(region.getAttributes().getSubscriptionAttributes()).isNotEqualTo(new SubscriptionAttributes());
		}
	}
	@Test void childCleanupFailureStillClosesTheWholeLocalTree() {
		try (var c = new MockConnection()) {
			var root = c.createClientRegion("root", ClientRegionShortcut.LOCAL);
			var child = root.createClientSubregion("child", ClientRegionShortcut.LOCAL, RegionConfiguration.builder().build());
			var sibling = root.createClientSubregion("sibling", ClientRegionShortcut.LOCAL, RegionConfiguration.builder().build());
			child.setCacheLoader(new DriverCacheLoader<>() {
				@Override public Object load(DriverLoaderHelper<Object, Object> helper) { return null; }
				@Override public void close() { throw new IllegalStateException("callback failure"); }
			});
			assertThatThrownBy(root::closeRegion).isInstanceOf(DriverOperationException.class);
			assertThat(root.isDestroyed()).isTrue(); assertThat(child.isDestroyed()).isTrue(); assertThat(sibling.isDestroyed()).isTrue();
			assertThat(c.rootRegions()).isEmpty(); assertThat(c.getRegion("root/child")).isNull(); assertThat(c.getRegion("root/sibling")).isNull();
		}
	}

}
