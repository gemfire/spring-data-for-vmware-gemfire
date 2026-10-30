// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.*;
import java.util.Set;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.junit.jupiter.api.Test;
import com.vmware.gemfire.driver.spi.*;
import com.vmware.gemfire.driver.spi.config.*;

@SuppressWarnings("deprecation")
class NativeExpirationEvictionCompressionTests {
	static Set<String> typedGetters() { return Set.of("getEntryTimeToLive", "getEntryIdleTimeout", "getRegionTimeToLive", "getRegionIdleTimeout",
		"getCustomEntryTimeToLive", "getCustomEntryIdleTimeout", "getEvictionAttributes", "getCompressor"); }

	private final DriverCustomExpiry<String, String> CUSTOM = new DriverCustomExpiry<>() {
		@Override public ExpirationAttributes getExpiry(DriverRegionEntry<String, String> entry) { return new ExpirationAttributes(30, ExpirationAction.DESTROY); }
		@Override public void close() { }
	};

	@Test void configuredSettingsAreReadBackAsProviderNeutralValues() {
		try (var cache = new ClientCacheFactory().set("log-level", "error").create()) {
			var connection = new GemFireConnection(cache);
			var sizer = new ObjectSizer() { @Override public int sizeof(Object o) { return 1; } };
			var config = RegionConfiguration.<String, String>builder()
				.statisticsEnabled(true)
				.entryTimeToLive(new ExpirationAttributes(10, ExpirationAction.DESTROY))
				.entryIdleTimeout(new ExpirationAttributes(20, ExpirationAction.INVALIDATE))
				.regionTimeToLive(new ExpirationAttributes(30, ExpirationAction.LOCAL_DESTROY))
				.regionIdleTimeout(new ExpirationAttributes(40, ExpirationAction.LOCAL_INVALIDATE))
				.customEntryTimeToLive(CUSTOM)
				.evictionAttributes(EvictionAttributes.createLRUMemoryAttributes(5, sizer, EvictionAction.LOCAL_DESTROY))
				.compressor(new SnappyCompressor())
				.build();
			var attributes = connection.<String, String>createClientRegion("expiry", ClientRegionShortcut.LOCAL, config).getAttributes();
			assertThat(attributes.getEntryTimeToLive()).isEqualTo(new ExpirationAttributes(10, ExpirationAction.DESTROY));
			assertThat(attributes.getEntryIdleTimeout()).isEqualTo(new ExpirationAttributes(20, ExpirationAction.INVALIDATE));
			assertThat(attributes.getRegionTimeToLive()).isEqualTo(new ExpirationAttributes(30, ExpirationAction.LOCAL_DESTROY));
			assertThat(attributes.getRegionIdleTimeout()).isEqualTo(new ExpirationAttributes(40, ExpirationAction.LOCAL_INVALIDATE));
			assertThat(attributes.getCustomEntryTimeToLive()).isSameAs(CUSTOM);
			assertThat(attributes.getCustomEntryIdleTimeout()).isNull();
			assertThat(attributes.getEvictionAttributes().getAlgorithm()).isEqualTo(EvictionAlgorithm.LRU_MEMORY);
			assertThat(attributes.getEvictionAttributes().getMaximum()).isEqualTo(5);
			assertThat(attributes.getEvictionAttributes().getObjectSizer()).isSameAs(sizer);
			assertThat(attributes.getCompressor()).isInstanceOf(SnappyCompressor.class);
		}
	}

	@Test void mutatorReturnsPreviousValuesAndMaximumUpdatesEviction() {
		try (var cache = new ClientCacheFactory().set("log-level", "error").create()) {
			var connection = new GemFireConnection(cache);
			var config = RegionConfiguration.<String, String>builder().statisticsEnabled(true)
				.evictionAttributes(EvictionAttributes.createLRUEntryAttributes(3, EvictionAction.LOCAL_DESTROY)).build();
			var region = connection.<String, String>createClientRegion("mutable", ClientRegionShortcut.LOCAL, config);
			var mutator = region.getAttributesMutator();
			assertThat(mutator.setEntryIdleTimeout(new ExpirationAttributes(7))).isEqualTo(ExpirationAttributes.DEFAULT);
			assertThat(mutator.setEntryIdleTimeout(new ExpirationAttributes(8))).isEqualTo(new ExpirationAttributes(7));
			assertThat(mutator.setCustomEntryIdleTimeout(CUSTOM)).isNull();
			assertThat(mutator.setCustomEntryIdleTimeout(null)).isSameAs(CUSTOM);
			mutator.setEvictionMaximum(9);
			assertThat(region.getAttributes().getEvictionAttributes().getMaximum()).isEqualTo(9);
			assertThatThrownBy(() -> mutator.setEntryIdleTimeout(null)).isInstanceOf(RuntimeException.class);
		}
	}

	@Test void nonDefaultResetPolicyIsUnsupportedOnTheFactoryPath() {
		try (var cache = new ClientCacheFactory().set("log-level", "error").create()) {
			var connection = new GemFireConnection(cache);
			var config = RegionConfiguration.<String, String>builder().statisticsEnabled(true)
				.entryTimeToLiveResetPolicy(TimeToLiveResetPolicy.NEVER).build();
			assertThatThrownBy(() -> connection.createClientRegion("reset", ClientRegionShortcut.LOCAL, config))
				.isInstanceOf(UnsupportedFeatureException.class);
		}
	}
}
