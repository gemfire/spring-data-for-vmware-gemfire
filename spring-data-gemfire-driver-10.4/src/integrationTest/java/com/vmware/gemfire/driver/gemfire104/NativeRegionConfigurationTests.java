/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.apache.geode.cache.client.ClientCacheFactory;
import com.vmware.gemfire.driver.spi.*;
import com.vmware.gemfire.driver.spi.config.*;

class NativeRegionConfigurationTests {
	@Test void clientAttributesMatchNativeSettingsAndMutableViews() throws Exception {
		try (var cache = new ClientCacheFactory().set("log-level", "error").create()) {
			var connection = new GemFireConnection(cache);
			var configuration = RegionConfiguration.<String, Integer>builder()
				.keyConstraint(String.class).valueConstraint(Integer.class)
				.initialCapacity(0).loadFactor(0.5f).concurrencyLevel(7).concurrencyChecksEnabled(false)
				.statisticsEnabled(true)
				.cloningEnabled(true).diskSynchronous(false)
				.poolName(null).diskStoreName(null)
				.build();
			var region = connection.createClientRegion("settings", ClientRegionShortcut.LOCAL, configuration);
			var nativeAttributes = cache.getRegion("settings").getAttributes();
			var attributes = region.getAttributes();
			for (var method : DriverRegionAttributes.class.getMethods()) {
				if (method.getName().startsWith("getCache")) continue; // Shared suite verifies callback identity/lifecycle.
					if (method.getName().equals("getSubscriptionAttributes")) continue; // Typed value; asserted below.
				Object expected = org.apache.geode.cache.RegionAttributes.class.getMethod(method.getName()).invoke(nativeAttributes);
				Object actual = method.invoke(attributes);
				if (actual instanceof Enum<?>) expected = expected.toString();
				assertThat(actual instanceof Enum<?> ? actual.toString() : actual).as(method.getName()).isEqualTo(expected);
			}
			assertThat(attributes.getStatisticsEnabled()).isTrue(); assertThat(attributes.getCloningEnabled()).isTrue();
			// Client regions install ALL, unlike the CACHE_CONTENT default of a bare SubscriptionAttributes.
			assertThat(nativeAttributes.getSubscriptionAttributes().getInterestPolicy().toString()).isEqualTo("ALL");
			assertThat(attributes.getSubscriptionAttributes()).isEqualTo(new SubscriptionAttributes(InterestPolicy.ALL));
			region.put("key", 1); region.get("key"); region.get("missing");
			assertThat(region.getStatistics().getHitCount()).isEqualTo(1); assertThat(region.getStatistics().getMissCount()).isEqualTo(1);
			var child = region.createClientSubregion("child", ClientRegionShortcut.LOCAL, RegionConfiguration.<String, Integer>builder().statisticsEnabled(true).build());
			child.put("child", 2); child.get("child"); assertThat(child.getStatistics().getHitCount()).isEqualTo(1);
			var mutator = region.getAttributesMutator();
			mutator.setCloningEnabled(false); assertThat(attributes.getCloningEnabled()).isFalse();

		}
	}
	@Test void clientLocalVariantsPreserveShortcutDefaultsAndValidateOverrides(@org.junit.jupiter.api.io.TempDir java.nio.file.Path directory) {
		try (var cache = new ClientCacheFactory().set("log-level", "error").create()) {
			var connection = new GemFireConnection(cache);
			cache.createDiskStoreFactory().setDiskDirs(new java.io.File[] {directory.toFile()}).create("client-local");
			for (var shortcut : ClientRegionShortcut.values()) {
				if (!shortcut.name().startsWith("LOCAL")) continue;
				var configuration = RegionConfiguration.builder();
				var controlFactory = cache.createClientRegionFactory(org.apache.geode.cache.client.ClientRegionShortcut.valueOf(shortcut.name()));
				if (shortcut.name().contains("PERSISTENT") || shortcut.name().contains("OVERFLOW")) {
					configuration.diskStoreName("client-local"); controlFactory.setDiskStoreName("client-local");
				}
				var region = connection.createClientRegion(shortcut.name(), shortcut, configuration.build());
				var control = controlFactory.create("control-" + shortcut.name());
				assertThat(region.getAttributes().getDataPolicy().name()).isEqualTo(control.getAttributes().getDataPolicy().toString());
				assertThat(region.getAttributes().getScope()).isEqualTo(Scope.LOCAL);
				region.put("key", "value"); assertThat(region.get("key")).isEqualTo("value");
				region.destroyRegion(null); control.destroyRegion();
			}
			assertThatThrownBy(() -> connection.createClientRegion("bad", ClientRegionShortcut.LOCAL, RegionConfiguration.builder().loadFactor(-1f).build()))
				.isInstanceOfSatisfying(DriverOperationException.class, e -> assertThat(e.kind()).isEqualTo(DriverFailureKind.INVALID_ARGUMENT));
			assertThat(cache.getRegion("bad")).isNull();
		}
	}
	@Test void supportedScopePredicatesAndConfigurationNamesMatchNative() {
		// This native version no longer supports DISTRIBUTED_NO_ACK.
		for (var nativeScope : List.of(org.apache.geode.cache.Scope.LOCAL,
				org.apache.geode.cache.Scope.DISTRIBUTED_ACK, org.apache.geode.cache.Scope.GLOBAL)) {
			var scope = Scope.valueOf(nativeScope.toString());
			assertThat(scope.isLocal()).isEqualTo(nativeScope.isLocal()); assertThat(scope.isDistributed()).isEqualTo(nativeScope.isDistributed());
			assertThat(scope.isDistributedAck()).isEqualTo(nativeScope.isDistributedAck()); assertThat(scope.isDistributedNoAck()).isFalse();
			assertThat(scope.isGlobal()).isEqualTo(nativeScope.isGlobal()); assertThat(scope.isAck()).isEqualTo(nativeScope.isAck());
			assertThat(scope.toConfigTypeString()).isEqualTo(nativeScope.toConfigTypeString());
		}
	}
}
