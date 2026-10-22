/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;

import com.vmware.gemfire.driver.spi.config.InterestResultPolicy;
import org.junit.jupiter.api.Test;

class NativeInterestResultPolicyTests {

	@Test
	void clientInterestPoliciesAndDefaultMatchNative() throws ReflectiveOperationException {
		for (var policy : InterestResultPolicy.values()) {
			var nativePolicy = (org.apache.geode.cache.InterestResultPolicy)
				org.apache.geode.cache.InterestResultPolicy.class.getField(policy.name()).get(null);
			assertThat(policy.isNone()).as("%s.isNone", policy).isEqualTo(nativePolicy.isNone());
			assertThat(policy.isKeys()).as("%s.isKeys", policy).isEqualTo(nativePolicy.isKeys());
			assertThat(policy.isKeysValues()).as("%s.isKeysValues", policy).isEqualTo(nativePolicy.isKeysValues());
			assertThat(policy.isDefault()).as("%s.isDefault", policy).isEqualTo(nativePolicy.isDefault());
		}
		assertThat(org.apache.geode.cache.InterestResultPolicy.class
			.getField(InterestResultPolicy.DEFAULT.name()).get(null))
			.isSameAs(org.apache.geode.cache.InterestResultPolicy.DEFAULT);
	}
}
