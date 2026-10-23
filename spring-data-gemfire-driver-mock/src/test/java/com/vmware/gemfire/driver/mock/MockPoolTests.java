/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.vmware.gemfire.driver.spi.PoolConfiguration;
import com.vmware.gemfire.driver.spi.PoolConfiguration.Endpoint;

class MockPoolTests {
	@Test
	void connectionsOwnIndependentRecordsAndClosureDestroysOnlyTheirPools() {
		var config = new PoolConfiguration(List.of(new Endpoint("no-dns-needed.invalid", 40404)), List.of(), null);
		try (var first = new MockConnection(); var second = new MockConnection()) {
			var firstPool = first.createPool("same", config);
			var secondPool = second.createPool("same", config);
			assertThat(firstPool.minimumConnections()).isEqualTo(1);
			first.close();
			assertThat(firstPool.isDestroyed()).isTrue();
			assertThat(secondPool.isDestroyed()).isFalse();
			assertThat(second.getPool("same")).isSameAs(secondPool);
			second.close();
			assertThat(secondPool.isDestroyed()).isTrue();
		}
	}
}
