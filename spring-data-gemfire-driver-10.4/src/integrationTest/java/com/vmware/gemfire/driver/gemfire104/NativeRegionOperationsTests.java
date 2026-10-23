/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

import static org.assertj.core.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.apache.geode.cache.*;
import com.vmware.gemfire.driver.spi.*;

class NativeRegionOperationsTests {
	@Test void hierarchyStatisticsAndEntryViewsDelegateToConfiguredNativeRegions() {
		try (var cache = new ClientCacheFactory().set("mcast-port", "0").set("locators", "").set("log-level", "error").create()) {
			var factory = cache.<String, String>createClientRegionFactory(ClientRegionShortcut.LOCAL).setStatisticsEnabled(true);
			var root = factory.create("root"); var child = factory.createSubregion(root, "child");
			var leaf = factory.createSubregion(child, "leaf");
			var handle = new GemFireRegion<>(root);
			assertThat(handle.getSubregion("child/leaf").getFullPath()).isEqualTo("/root/child/leaf");
			assertThat(handle.getSubregion("child").getParentRegion()).isEqualTo(handle);
			assertThat(handle.subregions(false)).extracting(DriverRegion::getFullPath).containsExactly("/root/child");
			assertThat(handle.subregions(true)).hasSize(2);
			handle.put("root", "r"); child.put("child", "c"); leaf.put("leaf", "l");
			assertThat(handle.entrySet(false)).hasSize(1); assertThat(handle.entrySet(true)).hasSize(3);
			handle.get("root"); handle.get("absent");
			var stats = handle.getStatistics();
			assertThat(stats.getHitCount()).isEqualTo(root.getStatistics().getHitCount());
			assertThat(stats.getMissCount()).isEqualTo(root.getStatistics().getMissCount());
			assertThat(stats.getHitRatio()).isEqualTo(root.getStatistics().getHitRatio());
			assertThat(stats.getLastAccessedTime()).isEqualTo(root.getStatistics().getLastAccessedTime());
			assertThat(stats.getLastModifiedTime()).isEqualTo(root.getStatistics().getLastModifiedTime());
			assertThat(handle.getEntry("root").getStatistics().getHitCount()).isEqualTo(root.getEntry("root").getStatistics().getHitCount());
			stats.resetCounts(); assertThat(stats.getHitCount()).isZero(); assertThat(stats.getMissCount()).isZero();
			var keys = handle.keySet(); var values = handle.values(); var entries = handle.entrySet();
			handle.put("extra", "e"); assertThat(keys).hasSize(2); assertThat(values).containsExactlyInAnyOrder("r", "e");
			assertThat(entries).hasSize(2); assertThat(entries.contains(entries.iterator().next())).isTrue();
			assertThatThrownBy(() -> keys.remove("root")).isInstanceOf(DriverOperationException.class);
			assertThatThrownBy(values::clear).isInstanceOf(DriverOperationException.class);
			handle.localDestroyRegion(); assertThat(child.isDestroyed()).isTrue(); assertThat(leaf.isDestroyed()).isTrue();
			assertThat(keys).isEmpty();
		}
	}
}
