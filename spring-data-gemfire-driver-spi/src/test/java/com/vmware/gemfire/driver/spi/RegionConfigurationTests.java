/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import static org.assertj.core.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import com.vmware.gemfire.driver.spi.config.*;

class RegionConfigurationTests {
	@Test void configurationCopiesCollectionsAndPreservesUnsetNullFalseAndZero() {
		Set<String> ids = new HashSet<>(Set.of("sender"));
		var builder = RegionConfiguration.builder().gatewaySenderIds(ids);
		var first = builder.build(); ids.clear();
		var second = builder.initialCapacity(0).cloningEnabled(false).poolName(null).gatewaySenderIds(Set.of()).build();
		assertThat(first.gatewaySenderIds().value()).containsExactly("sender"); assertThat(first.initialCapacity().isSet()).isFalse();
		assertThat(first.poolName().isSet()).isFalse(); assertThat(second.poolName().isSet()).isTrue(); assertThat(second.poolName().value()).isNull();
		assertThat(second.cloningEnabled().isSet()).isTrue(); assertThat(second.cloningEnabled().value()).isFalse();
		assertThat(second.initialCapacity().value()).isZero(); assertThat(second.gatewaySenderIds().value()).isEmpty();
		assertThatThrownBy(() -> first.gatewaySenderIds().value().clear()).isInstanceOf(UnsupportedOperationException.class);
		List<String> calls = new ArrayList<>(); first.poolName().ifSet(calls::add); second.poolName().ifSet(calls::add);
		assertThat(calls).containsExactly((String) null);
	}
	@Test void addedDefaultsFailExplicitlyOnOlderProviders() {
		DriverConnection c = new DriverConnection() {};
		DriverRegion<Object, Object> r = new DriverRegion<>() {};
		var config = RegionConfiguration.builder().build();
		List<org.assertj.core.api.ThrowableAssert.ThrowingCallable> calls = List.of(
			() -> c.createRegion("r", RegionShortcut.LOCAL, config), () -> c.createClientRegion("r", ClientRegionShortcut.LOCAL, config),
			r::getAttributes, r::getAttributesMutator,
			() -> r.createSubregion("r", RegionShortcut.LOCAL, config), () -> r.createClientSubregion("r", ClientRegionShortcut.LOCAL, config));
		for (var call : calls) assertThatThrownBy(call).isInstanceOfSatisfying(UnsupportedFeatureException.class,
			e -> assertThat(e.reason()).isEqualTo(UnsupportedFeatureException.Reason.NOT_INTRODUCED));
	}
}
