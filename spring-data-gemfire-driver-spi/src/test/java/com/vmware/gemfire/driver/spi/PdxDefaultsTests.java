/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

class PdxDefaultsTests {
	@Test void olderProvidersFailExplicitlyForBothPdxCreationMethods() {
		DriverConnection old = new DriverConnection() {};
		assertThatThrownBy(() -> old.createPdxInstanceFactory("Type")).isInstanceOfSatisfying(UnsupportedFeatureException.class,
			ex -> { assertThat(ex.feature()).isEqualTo("RegionService.createPdxInstanceFactory"); assertThat(ex.reason()).isEqualTo(UnsupportedFeatureException.Reason.NOT_INTRODUCED); });
		assertThatThrownBy(() -> old.createPdxEnum("Type", "VALUE", 0)).isInstanceOfSatisfying(UnsupportedFeatureException.class,
			ex -> { assertThat(ex.feature()).isEqualTo("RegionService.createPdxEnum"); assertThat(ex.reason()).isEqualTo(UnsupportedFeatureException.Reason.NOT_INTRODUCED); });
	}
}
