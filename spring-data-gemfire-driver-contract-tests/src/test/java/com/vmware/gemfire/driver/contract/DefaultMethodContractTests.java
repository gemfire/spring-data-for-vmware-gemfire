/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.contract;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.vmware.gemfire.driver.contract.DefaultMethodContract.Probe;
import com.vmware.gemfire.driver.spi.UnsupportedFeatureException;
import com.vmware.gemfire.driver.spi.UnsupportedFeatureException.Reason;

class DefaultMethodContractTests {

	private final DefaultMethodContract contract = new DefaultMethodContract();

	@Test
	void missingProbesCannotPassVacuously() {
		assertThatThrownBy(() -> contract.verify(Map.of(), Map.of(), "10.4.0"))
			.isInstanceOf(AssertionError.class).hasMessageContaining("Every SPI default method");
	}

	@Test
	void undeclaredUnsupportedFeatureFails() {
		Probe probe = new Probe("feature", () -> { throw UnsupportedFeatureException.notIntroduced("feature"); });
		assertThatThrownBy(() -> contract.verifyOutcome(probe, null, "10.4.0"))
			.isInstanceOf(AssertionError.class).hasMessageContaining("Unexpected unsupported");
	}

	@Test
	void supportedFeatureCannotRemainOnTheUnsupportedList() {
		assertThatThrownBy(() -> contract.verifyOutcome(new Probe("feature", () -> {}),
			Reason.NOT_INTRODUCED, "10.4.0"))
			.isInstanceOf(AssertionError.class).hasMessageContaining("Expected unsupported");
	}

	@Test
	void wrongReasonAndWrongFeatureFail() {
		Probe wrongReason = new Probe("feature", () -> { throw UnsupportedFeatureException.removed("feature", "10.4.0"); });
		assertThatThrownBy(() -> contract.verifyOutcome(wrongReason, Reason.NOT_INTRODUCED, "10.4.0"))
			.isInstanceOf(AssertionError.class);
		Probe wrongFeature = new Probe("feature", () -> { throw UnsupportedFeatureException.notIntroduced("other"); });
		assertThatThrownBy(() -> contract.verifyOutcome(wrongFeature, Reason.NOT_INTRODUCED, "10.4.0"))
			.isInstanceOf(AssertionError.class);
	}

	@Test
	void removedFeatureMustNameTheDriversNativeVersion() {
		Probe probe = new Probe("feature", () -> { throw UnsupportedFeatureException.removed("feature", "10.3.0"); });
		assertThatThrownBy(() -> contract.verifyOutcome(probe, Reason.REMOVED, "10.4.0"))
			.isInstanceOf(AssertionError.class);
	}

	@Test
	void acceptsBothDeclaredUnsupportedReasons() throws Exception {
		contract.verifyOutcome(new Probe("feature", () -> { throw UnsupportedFeatureException.notIntroduced("feature"); }),
			Reason.NOT_INTRODUCED, "10.4.0");
		contract.verifyOutcome(new Probe("feature", () -> { throw UnsupportedFeatureException.removed("feature", "10.4.0"); }),
			Reason.REMOVED, "10.4.0");
	}
}
