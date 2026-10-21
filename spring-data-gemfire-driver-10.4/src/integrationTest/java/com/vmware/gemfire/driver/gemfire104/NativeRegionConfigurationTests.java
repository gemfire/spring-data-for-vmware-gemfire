/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.apache.geode.cache.CacheFactory;
import com.vmware.gemfire.driver.spi.*;
import com.vmware.gemfire.driver.spi.config.*;

class NativeRegionConfigurationTests {
	@Test void everyExposedAttributeMatchesNativeSettingsAndMutableViews() throws Exception {
		try (var cache = new CacheFactory().set("mcast-port", "0").set("locators", "").set("log-level", "error").create()) {
			var connection = new GemFireConnection(cache, CacheConfiguration.Mode.PEER);
			var configuration = RegionConfiguration.<String, Integer>builder()
				.scope(Scope.LOCAL).dataPolicy(DataPolicy.NORMAL).keyConstraint(String.class).valueConstraint(Integer.class)
				.initialCapacity(0).loadFactor(0.5f).concurrencyLevel(7).concurrencyChecksEnabled(false)
				.statisticsEnabled(true).ignoreJTA(true).lockGrantor(false).multicastEnabled(false)
				.cloningEnabled(true).diskSynchronous(false).indexMaintenanceSynchronous(false)
				.enableSubscriptionConflation(true).earlyAck(false).publisher(false).poolName(null).diskStoreName(null)
				.gatewaySenderIds(Set.of("sender")).asyncEventQueueIds(Set.of("queue")).build();
			var region = connection.createRegion("settings", RegionShortcut.LOCAL, configuration);
			var nativeAttributes = cache.getRegion("settings").getAttributes();
			var attributes = region.getAttributes();
			for (var method : DriverRegionAttributes.class.getMethods()) {
				if (method.getName().startsWith("getCache")) continue; // Shared suite verifies callback identity/lifecycle.
				Object expected = org.apache.geode.cache.RegionAttributes.class.getMethod(method.getName()).invoke(nativeAttributes);
				Object actual = method.invoke(attributes);
				if (actual instanceof Enum<?>) expected = expected.toString();
				assertThat(actual instanceof Enum<?> ? actual.toString() : actual).as(method.getName()).isEqualTo(expected);
			}
			assertThat(attributes.getStatisticsEnabled()).isTrue(); assertThat(attributes.getCloningEnabled()).isTrue();
			assertThat(attributes.getGatewaySenderIds()).containsExactly("sender"); assertThat(attributes.getAsyncEventQueueIds()).containsExactly("queue");
			region.put("key", 1); region.get("key"); region.get("missing");
			assertThat(region.getStatistics().getHitCount()).isEqualTo(1); assertThat(region.getStatistics().getMissCount()).isEqualTo(1);
			var child = region.createSubregion("child", RegionShortcut.LOCAL, RegionConfiguration.<String, Integer>builder().statisticsEnabled(true).build());
			child.put("child", 2); child.get("child"); assertThat(child.getStatistics().getHitCount()).isEqualTo(1);
			var mutator = region.getAttributesMutator();
			mutator.setCloningEnabled(false); assertThat(attributes.getCloningEnabled()).isFalse();
			mutator.addGatewaySenderId("otherSender"); mutator.removeGatewaySenderId("sender");
			assertThat(attributes.getGatewaySenderIds()).containsExactly("otherSender");
			mutator.addAsyncEventQueueId("otherQueue"); mutator.removeAsyncEventQueueId("queue");
			assertThat(attributes.getAsyncEventQueueIds()).containsExactly("otherQueue");
			assertThatThrownBy(() -> attributes.getGatewaySenderIds().clear()).isInstanceOf(UnsupportedOperationException.class);
		}
	}
	@Test void unsetOptionsPreserveReplicateShortcutAndExplicitOverridesReachNativeValidation() {
		try (var cache = new CacheFactory().set("mcast-port", "0").set("locators", "").set("log-level", "error").create()) {
			var connection = new GemFireConnection(cache, CacheConfiguration.Mode.PEER);
			var implicit = connection.createRegion("replicate", RegionShortcut.REPLICATE, RegionConfiguration.builder().build());
			assertThat(implicit.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.REPLICATE);
			assertThat(implicit.getAttributes().getScope()).isEqualTo(Scope.DISTRIBUTED_ACK);
			var explicit = connection.createRegion("local", RegionShortcut.REPLICATE, RegionConfiguration.builder().scope(Scope.LOCAL).dataPolicy(DataPolicy.NORMAL).build());
			assertThat(explicit.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL); assertThat(explicit.getAttributes().getScope()).isEqualTo(Scope.LOCAL);
			assertThatThrownBy(() -> connection.createRegion("bad", RegionShortcut.LOCAL, RegionConfiguration.builder().loadFactor(-1f).build()))
				.isInstanceOfSatisfying(DriverOperationException.class, e -> assertThat(e.kind()).isEqualTo(DriverFailureKind.INVALID_ARGUMENT));
			assertThat(cache.getRegion("bad")).isNull();
		}
	}
	@Test void scopePredicatesAndConfigurationNamesMatchNative() {
		for (var scope : Scope.values()) {
			var nativeScope = org.apache.geode.cache.Scope.fromString(scope.name());
			assertThat(scope.isLocal()).isEqualTo(nativeScope.isLocal()); assertThat(scope.isDistributed()).isEqualTo(nativeScope.isDistributed());
			assertThat(scope.isDistributedAck()).isEqualTo(nativeScope.isDistributedAck()); assertThat(scope.isDistributedNoAck()).isEqualTo(nativeScope.isDistributedNoAck());
			assertThat(scope.isGlobal()).isEqualTo(nativeScope.isGlobal()); assertThat(scope.isAck()).isEqualTo(nativeScope.isAck());
			assertThat(scope.toConfigTypeString()).isEqualTo(nativeScope.toConfigTypeString());
		}
	}
}
