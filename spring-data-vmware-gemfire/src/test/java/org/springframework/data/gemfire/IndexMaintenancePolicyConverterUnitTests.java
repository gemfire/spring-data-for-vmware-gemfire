/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Test;

/**
 * Unit Tests for {@link IndexMaintenancePolicyConverter}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.data.gemfire.IndexMaintenancePolicyConverter
 * @see org.springframework.data.gemfire.IndexMaintenancePolicyType
 * @since 1.6.0
 */
public class IndexMaintenancePolicyConverterUnitTests {

	private final IndexMaintenancePolicyConverter converter = new IndexMaintenancePolicyConverter();

	@After
	public void tearDown() {
		converter.setValue(null);
	}

	@Test
	public void convert() {

		assertThat(converter.convert("asynchronous")).isEqualTo(IndexMaintenancePolicyType.ASYNCHRONOUS);
		assertThat(converter.convert("Synchronous")).isEqualTo(IndexMaintenancePolicyType.SYNCHRONOUS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void convertIllegalValue() {

		try {
			converter.convert("sync");
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("[sync] is not a valid IndexMaintenancePolicyType");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void setAsText() {

		assertThat(converter.getValue()).isNull();

		converter.setAsText("aSynchronous");

		assertThat(converter.getValue()).isEqualTo(IndexMaintenancePolicyType.ASYNCHRONOUS);

		converter.setAsText("synchrONoUS");

		assertThat(converter.getValue()).isEqualTo(IndexMaintenancePolicyType.SYNCHRONOUS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setAsTextWithIllegalValue() {

		try {
			converter.setAsText("async");
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("[async] is not a valid IndexMaintenancePolicyType");
			assertThat(expected).hasNoCause();

			throw expected;
		}
		finally {
			assertThat(converter.getValue()).isNull();
		}
	}
}
