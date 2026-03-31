/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Test;

import org.springframework.data.gemfire.gud.api.GudScope;

/**
 * Unit Tests for {@link ScopeConverter}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.data.gemfire.ScopeConverter
 * @see org.apache.geode.cache.GudScope
 * @since 1.6.0
 */
public class ScopeConverterUnitTests {

	private final ScopeConverter converter = new ScopeConverter();

	@After
	public void tearDown() {
		converter.setValue(null);
	}

	@Test
	public void convert() {

		assertThat(converter.convert("distributed-ACK")).isEqualTo(GudScope.DISTRIBUTED_ACK);
		assertThat(converter.convert(" Distributed_NO-aCK")).isEqualTo(GudScope.DISTRIBUTED_NO_ACK);
		assertThat(converter.convert("loCAL  ")).isEqualTo(GudScope.LOCAL);
		assertThat(converter.convert(" GLOBal  ")).isEqualTo(GudScope.GLOBAL);
	}

	@Test(expected = IllegalArgumentException.class)
	public void convertIllegalValue() {

		try {
			converter.convert("illegal-value");
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("[illegal-value] is not a valid GudScope");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void setAsText() {

		assertThat(converter.getValue()).isNull();

		converter.setAsText("DisTributeD-nO_Ack");

		assertThat(converter.getValue()).isEqualTo(GudScope.DISTRIBUTED_NO_ACK);

		converter.setAsText("distributed-ack");

		assertThat(converter.getValue()).isEqualTo(GudScope.DISTRIBUTED_ACK);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setAsTextWithIllegalValue() {

		try {
			converter.setAsText("d!5tr!but3d-n0_@ck");
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("[d!5tr!but3d-n0_@ck] is not a valid GudScope");
			assertThat(expected).hasNoCause();

			throw expected;
		}
		finally {
			assertThat(converter.getValue()).isNull();
		}
	}
}
