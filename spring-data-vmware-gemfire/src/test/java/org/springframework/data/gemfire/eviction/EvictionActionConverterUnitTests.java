/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.eviction;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Test;

import org.apache.geode.cache.EvictionAction;

/**
 * Unit Tests for {@link EvictionActionConverter}.
 *
 * @author John Blum
 * @see Test
 * @see EvictionAction
 * @see EvictionActionConverter
 * @since 1.6.0
 */
public class EvictionActionConverterUnitTests {

	private final EvictionActionConverter converter = new EvictionActionConverter();

	@After
	public void tearDown() {
		converter.setValue(null);
	}

	@Test
	public void convert() {

		assertThat(converter.convert("local_destroy")).isEqualTo(EvictionAction.LOCAL_DESTROY);
		assertThat(converter.convert("None")).isEqualTo(EvictionAction.NONE);
		assertThat(converter.convert("OverFlow_TO_dIsk")).isEqualTo(EvictionAction.OVERFLOW_TO_DISK);
	}

	@Test(expected = IllegalArgumentException.class)
	public void convertIllegalValue() {

		try {
			converter.convert("invalid_value");
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("[invalid_value] is not a valid EvictionAction");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void setAsText() {

		assertThat(converter.getValue()).isNull();

		converter.setAsText("Local_Destroy");

		assertThat(converter.getValue()).isEqualTo(EvictionAction.LOCAL_DESTROY);

		converter.setAsText("overflow_to_disk");

		assertThat(converter.getValue()).isEqualTo(EvictionAction.OVERFLOW_TO_DISK);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setAsTextWithIllegalValue() {

		try {
			converter.setAsText("destroy");
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("[destroy] is not a valid EvictionAction");
			assertThat(expected).hasNoCause();

			throw expected;
		}
		finally {
			assertThat(converter.getValue()).isNull();
		}
	}
}
