package com.vmware.gemfire.driver.spi;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UnsupportedFeatureExceptionTests {

	@Test
	void notIntroducedCarriesFeatureAndReasonButNoVersion() {
		UnsupportedFeatureException ex = UnsupportedFeatureException.notIntroduced("Pool.minConnectionsPerServer");
		assertThat(ex.feature()).isEqualTo("Pool.minConnectionsPerServer");
		assertThat(ex.reason()).isEqualTo(UnsupportedFeatureException.Reason.NOT_INTRODUCED);
		assertThat(ex.gemFireVersion()).isEmpty();
		assertThat(ex.getMessage()).contains("Pool.minConnectionsPerServer");
	}

	@Test
	void removedCarriesFeatureReasonAndGemFireVersion() {
		UnsupportedFeatureException ex = UnsupportedFeatureException.removed("Region.offHeap", "10.4.0");
		assertThat(ex.feature()).isEqualTo("Region.offHeap");
		assertThat(ex.reason()).isEqualTo(UnsupportedFeatureException.Reason.REMOVED);
		assertThat(ex.gemFireVersion()).contains("10.4.0");
		assertThat(ex.getMessage()).contains("Region.offHeap").contains("10.4.0");
	}

}
