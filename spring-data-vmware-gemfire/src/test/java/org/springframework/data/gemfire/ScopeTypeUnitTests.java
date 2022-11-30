// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.apache.geode.cache.Scope;

/**
 * Unit Tests for {@link ScopeType} enum.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Scope
 * @see org.springframework.data.gemfire.ScopeType
 * @since 1.6.0
 */
public class ScopeTypeUnitTests {

	@Test
	public void testStaticGetScope() {

		assertThat(ScopeType.getScope(ScopeType.GLOBAL)).isEqualTo(Scope.GLOBAL);
		assertThat(ScopeType.getScope(ScopeType.LOCAL)).isEqualTo(Scope.LOCAL);
	}

	@Test
	public void testStaticGetScopeWithNull() {
		assertThat(ScopeType.getScope(null)).isNull();
	}

	@Test
	public void testGetScope() {

		assertThat(ScopeType.DISTRIBUTED_ACK.getScope()).isEqualTo(Scope.DISTRIBUTED_ACK);
		assertThat(ScopeType.DISTRIBUTED_NO_ACK.getScope()).isEqualTo(Scope.DISTRIBUTED_NO_ACK);
		assertThat(ScopeType.LOCAL.getScope()).isEqualTo(Scope.LOCAL);
		assertThat(ScopeType.GLOBAL.getScope()).isEqualTo(Scope.GLOBAL);
	}

	@Test
	public void testValueOf() {

		try {
			for (int ordinal = 0; ordinal < Integer.MAX_VALUE; ordinal++) {

				Scope expectedScope = Scope.fromOrdinal(ordinal);
				ScopeType scopeType = ScopeType.valueOf(expectedScope);

				assertThat(scopeType).isNotNull();
				assertThat(scopeType.getScope()).isSameAs(expectedScope);
			}
		}
		catch (ArrayIndexOutOfBoundsException ignore) {
		}
	}

	@Test
	public void testValueOfWithNull() {
		assertThat(ScopeType.valueOf((Scope) null)).isNull();
	}

	@Test
	public void testValueOfIgnoreCase() {

		assertThat(ScopeType.valueOfIgnoreCase("distributed_ack")).isEqualTo(ScopeType.DISTRIBUTED_ACK);
		assertThat(ScopeType.valueOfIgnoreCase("Distributed_No_Ack")).isEqualTo(ScopeType.DISTRIBUTED_NO_ACK);
		assertThat(ScopeType.valueOfIgnoreCase("LOCal ")).isEqualTo(ScopeType.LOCAL);
		assertThat(ScopeType.valueOfIgnoreCase("GLOBAL")).isEqualTo(ScopeType.GLOBAL);
		assertThat(ScopeType.valueOfIgnoreCase(" global  ")).isEqualTo(ScopeType.GLOBAL);
		assertThat(ScopeType.valueOfIgnoreCase("DisTRIBUTEd-acK")).isEqualTo(ScopeType.DISTRIBUTED_ACK);
	}

	@Test
	public void testValueOfIgnoreCaseWithInvalidValues() {

		assertThat(ScopeType.valueOfIgnoreCase(" distributed ack ")).isNull();
		assertThat(ScopeType.valueOfIgnoreCase("D!str!but3d_N0_@ck")).isNull();
		assertThat(ScopeType.valueOfIgnoreCase("DISTRIBUTED_CANT_ACK")).isNull();
		assertThat(ScopeType.valueOfIgnoreCase("Dist_ACK")).isNull();
		assertThat(ScopeType.valueOfIgnoreCase("NOT-Distributed-ACK")).isNull();
		assertThat(ScopeType.valueOfIgnoreCase("LOCALE")).isNull();
		assertThat(ScopeType.valueOfIgnoreCase("GLO-BAL")).isNull();
		assertThat(ScopeType.valueOfIgnoreCase("  ")).isNull();
		assertThat(ScopeType.valueOfIgnoreCase("")).isNull();
		assertThat(ScopeType.valueOfIgnoreCase(null)).isNull();
	}
}
