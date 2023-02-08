/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.expiration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Test;

import org.apache.geode.cache.ExpirationAction;

/**
 * Unit Tests for {@link ExpirationActionConverter}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.EvictionAction
 * @see org.springframework.data.gemfire.expiration.ExpirationActionConverter
 * @since 1.6.0
 */
public class ExpirationActionConverterUnitTests {

	private final ExpirationActionConverter converter = new ExpirationActionConverter();

	@After
	public void tearDown() {
		converter.setValue(null);
	}

	@Test
	public void convert() {
		assertThat(converter.convert("destroy")).isEqualTo(ExpirationAction.DESTROY);
		assertThat(converter.convert("inValidAte")).isEqualTo(ExpirationAction.INVALIDATE);
		assertThat(converter.convert("LOCAL_dEsTrOy")).isEqualTo(ExpirationAction.LOCAL_DESTROY);
		assertThat(converter.convert("Local_Invalidate")).isEqualTo(ExpirationAction.LOCAL_INVALIDATE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void convertIllegalValue() {

		try {
			converter.convert("illegal_value");
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("[illegal_value] is not a valid ExpirationAction");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void setAsText() {

		assertThat(converter.getValue()).isNull();

		converter.setAsText("InValidAte");

		assertThat(converter.getValue()).isEqualTo(ExpirationAction.INVALIDATE);

		converter.setAsText("Local_Destroy");

		assertThat(converter.getValue()).isEqualTo(ExpirationAction.LOCAL_DESTROY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setAsTextWithIllegalValue() {

		try {
			converter.setAsText("destruction");
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("[destruction] is not a valid ExpirationAction");
			assertThat(expected).hasNoCause();

			throw expected;
		}
		finally {
			assertThat(converter.getValue()).isNull();
		}
	}
}
