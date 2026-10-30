/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import org.junit.jupiter.api.Test;

class DriverDurableClientAttributesTests {

	@Test
	void explicitCopiesPreserveBaseIdentityAndStableCollectionMembership() {
		var original = new DriverDurableClientAttributes("client_gem_first_gem_tail", 123);
		var values = new HashSet<DriverDurableClientAttributes>();
		values.add(original);
		var replacement = original.withPoolName("second").withTimeout(-1);
		assertThat(original.baseId()).isEqualTo("client");
		assertThat(original.poolName()).isEqualTo("first_gem_tail");
		assertThat(original.id()).isEqualTo("client_gem_first_gem_tail");
		assertThat(replacement.id()).isEqualTo("client_gem_second");
		assertThat(replacement.timeout()).isEqualTo(-1);
		assertThat(original.timeout()).isEqualTo(123);
		assertThat(values).contains(new DriverDurableClientAttributes("client_gem_first_gem_tail", 123));
		assertThat(replacement.withPoolName(null).id()).isEqualTo("client");
		assertThat(replacement.withPoolName("").id()).isEqualTo("client_gem_");
		assertThat(original.toString()).isEqualTo("DriverDurableClientAttributes[details=<redacted>]");
	}

	@Test
	void nullAndEmptyIdentitiesRetainPoolMetadataWithoutInventingAnId() {
		var empty = new DriverDurableClientAttributes("_gem_pool", 0);
		assertThat(empty.baseId()).isEmpty();
		assertThat(empty.id()).isEmpty();
		assertThat(empty.poolName()).isEqualTo("pool");
		assertThat(empty).isNotEqualTo(new DriverDurableClientAttributes("", 0));
		var absent = new DriverDurableClientAttributes();
		assertThat(absent.id()).isNull();
		assertThat(absent.timeout()).isZero();
		assertThat(absent).isEqualTo(new DriverDurableClientAttributes(null, 0));
		assertThat(absent.withPoolName("pool").id()).isNull();
		assertThat(absent.withPoolName("pool").poolName()).isEqualTo("pool");
		assertThat(absent.withPoolName("pool")).isNotEqualTo(absent);
	}
}
