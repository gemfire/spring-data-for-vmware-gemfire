// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.mock;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.vmware.gemfire.driver.spi.*;
import com.vmware.gemfire.driver.spi.config.*;

class MockExpirationEvictionCompressionTests {
	@Test void disabledSettingsAreReportedAndNothingIsSimulated() {
		try (var c = new MockConnection()) {
			var attributes = c.createClientRegion("local", ClientRegionShortcut.LOCAL).getAttributes();
			assertThat(attributes.getEntryTimeToLive()).isEqualTo(ExpirationAttributes.DEFAULT);
			assertThat(attributes.getCustomEntryTimeToLive()).isNull();
			assertThat(attributes.getEvictionAttributes().isNoEviction()).isTrue();
			assertThat(attributes.getCompressor()).isNull();
		}
	}

	@Test void behaviorTheMockCannotProvideFailsExplicitlyBeforePublishingARegion() {
		try (var c = new MockConnection()) {
			var configs = java.util.List.<RegionConfiguration<Object, Object>>of(
				RegionConfiguration.builder().entryTimeToLive(new ExpirationAttributes(5)).build(),
				RegionConfiguration.builder().evictionAttributes(EvictionAttributes.createLRUEntryAttributes(5)).build(),
				RegionConfiguration.builder().compressor(new SnappyCompressor()).build());
			for (var config : configs) {
				assertThatThrownBy(() -> c.createClientRegion("unsupported", ClientRegionShortcut.LOCAL, config))
					.isInstanceOf(UnsupportedFeatureException.class);
				assertThat(c.rootRegions()).isEmpty();
			}
		}
	}

	@Test void mutatorAcceptsOnlyDisabledValuesAndReturnsThePreviousOne() {
		try (var c = new MockConnection()) {
			var mutator = c.createClientRegion("local", ClientRegionShortcut.LOCAL).getAttributesMutator();
			assertThat(mutator.setEntryIdleTimeout(new ExpirationAttributes(0))).isEqualTo(ExpirationAttributes.DEFAULT);
			assertThat(mutator.setCustomEntryTimeToLive(null)).isNull();
			assertThatThrownBy(() -> mutator.setRegionTimeToLive(new ExpirationAttributes(5))).isInstanceOf(UnsupportedFeatureException.class);
			assertThatThrownBy(() -> mutator.setEvictionMaximum(5)).isInstanceOf(UnsupportedFeatureException.class);
			assertThatThrownBy(() -> mutator.setEntryIdleTimeout(null)).isInstanceOf(NullPointerException.class);
		}
	}
}
