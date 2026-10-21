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
		try (var c = new MockConnection(CacheConfiguration.Mode.PEER)) {
			List<RegionConfiguration<Object, Object>> configs = List.of(
				RegionConfiguration.builder().statisticsEnabled(true).build(), RegionConfiguration.builder().cloningEnabled(true).build(),
				RegionConfiguration.builder().dataPolicy(DataPolicy.PERSISTENT_REPLICATE).build(), RegionConfiguration.builder().scope(Scope.DISTRIBUTED_ACK).build(),
				RegionConfiguration.builder().poolName("pool").build(), RegionConfiguration.builder().diskStoreName("disk").build(),
				RegionConfiguration.builder().gatewaySenderIds(Set.of("sender")).build(), RegionConfiguration.builder().asyncEventQueueIds(Set.of("queue")).build());
			for (var config : configs) {
				assertThatThrownBy(() -> c.createRegion("unsupported", RegionShortcut.LOCAL, config))
					.isInstanceOfSatisfying(UnsupportedFeatureException.class, e -> assertThat(e.reason()).isEqualTo(UnsupportedFeatureException.Reason.NOT_INTRODUCED));
				assertThat(c.rootRegions()).isEmpty();
			}
			var r = c.createRegion("local", RegionShortcut.LOCAL); var m = r.getAttributesMutator();
			assertThatThrownBy(() -> m.setCloningEnabled(true)).isInstanceOf(UnsupportedFeatureException.class);
			assertThatThrownBy(() -> m.addGatewaySenderId("sender")).isInstanceOf(UnsupportedFeatureException.class);
			assertThatThrownBy(() -> m.addAsyncEventQueueId("queue")).isInstanceOf(UnsupportedFeatureException.class);
			assertThat(m.getCloningEnabled()).isFalse(); assertThat(r.getAttributes().getGatewaySenderIds()).isEmpty(); assertThat(r.getAttributes().getAsyncEventQueueIds()).isEmpty();
		}
	}
	@Test void childCleanupFailureStillClosesTheWholeLocalTree() {
		try (var c = new MockConnection(CacheConfiguration.Mode.PEER)) {
			var root = c.createRegion("root", RegionShortcut.LOCAL);
			var child = root.createSubregion("child", RegionShortcut.LOCAL, RegionConfiguration.builder().build());
			var sibling = root.createSubregion("sibling", RegionShortcut.LOCAL, RegionConfiguration.builder().build());
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
