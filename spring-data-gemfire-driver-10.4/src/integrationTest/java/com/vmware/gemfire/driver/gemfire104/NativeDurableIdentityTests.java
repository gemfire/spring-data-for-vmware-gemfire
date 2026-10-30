/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.DurableClientAttributes;
import org.apache.geode.internal.cache.tier.sockets.ClientProxyMembershipID;
import org.junit.jupiter.api.Test;

import com.vmware.gemfire.driver.spi.DriverDurableClientAttributes;

class NativeDurableIdentityTests {

	@Test
	void capturePreservesNativePoolContextButSnapshotsAndExplicitCopiesAreStable() {
		String previous = ClientProxyMembershipID.getPoolName();
		try {
			ClientProxyMembershipID.setPoolName(null);
			var source = new DurableClientAttributes("client_gem_first_gem_tail", 123);
			var nativeMember = mock(DistributedMember.class);
			when(nativeMember.getDurableClientAttributes()).thenReturn(source);
			var member = new GemFireEventMapper().member(nativeMember);
			var captured = member.getDurableClientAttributes();
			assertThat(captured.id()).isEqualTo(source.getId());
			assertThat(captured.poolName()).isEqualTo(source.getPoolName()).isEqualTo("first_gem_tail");
			int hash = captured.hashCode();
			ClientProxyMembershipID.setPoolName("ambient");
			source.updateTimeout(-1);
			assertThat(member.getDurableClientAttributes().id()).isEqualTo("client_gem_ambient");
			assertThat(member.getDurableClientAttributes().timeout()).isEqualTo(-1);
			assertThat(captured.id()).isEqualTo("client_gem_first_gem_tail");
			assertThat(captured.timeout()).isEqualTo(123);
			assertThat(captured.hashCode()).isEqualTo(hash);
			var explicit = captured.withPoolName("explicit").withTimeout(456);
			assertThat(explicit.id()).isEqualTo("client_gem_explicit");
			assertThat(source.getPoolName()).isEqualTo("ambient");
			assertThat(source.getTimeout()).isEqualTo(-1);
			ClientProxyMembershipID.setPoolName(null);
			source.setPoolName("explicit");
			source.updateTimeout(456);
			assertThat(explicit).isEqualTo(member.getDurableClientAttributes());
		} finally {
			ClientProxyMembershipID.setPoolName(previous);
		}
	}

	@Test
	void nullAndEmptyNativeIdsKeepExplicitPoolFieldsAndUseDeliberateValueEquality() {
		for (DurableClientAttributes source : java.util.List.of(
				new DurableClientAttributes(), new DurableClientAttributes("", 0))) {
			source.setPoolName("pool");
			var nativeMember = mock(DistributedMember.class);
			when(nativeMember.getDurableClientAttributes()).thenReturn(source);
			var captured = new GemFireEventMapper().member(nativeMember).getDurableClientAttributes();
			assertThat(captured.id()).isEqualTo(source.getId());
			assertThat(captured.poolName()).isEqualTo("pool");
			assertThat(captured).isEqualTo(new DriverDurableClientAttributes(source.getId(), 0).withPoolName("pool"));
			assertThat(captured.toString()).isEqualTo("DriverDurableClientAttributes[details=<redacted>]");
		}
		// Native null-ID objects are deliberately not value-equal; SPI snapshots are.
		assertThat(new DurableClientAttributes()).isNotEqualTo(new DurableClientAttributes());
		assertThat(new DriverDurableClientAttributes()).isEqualTo(new DriverDurableClientAttributes());
	}
}
