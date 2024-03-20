/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Test;

/**
 * Unit Tests for {@link IndexTypeConverter}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see IndexType
 * @see IndexTypeConverter
 * @since 1.5.2
 */
public class IndexTypeConverterUnitTests {

	private final IndexTypeConverter converter = new IndexTypeConverter();

	@After
	public void tearDown() {
		converter.setValue(null);
	}

	@Test
	public void convert() {

		assertThat(converter.convert("FUNCTIONAL")).isEqualTo(IndexType.FUNCTIONAL);
		assertThat(converter.convert("hASh")).isEqualTo(IndexType.HASH);
		assertThat(converter.convert("hASH")).isEqualTo(IndexType.HASH);
		assertThat(converter.convert("Key")).isEqualTo(IndexType.KEY);
		assertThat(converter.convert("primary_KEY")).isEqualTo(IndexType.PRIMARY_KEY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void convertWithIllegalValue() {

		try {
			converter.convert("function");
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("[function] is not a valid IndexType");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void setAsText() {

		assertThat(converter.getValue()).isNull();

		converter.setAsText("HasH");

		assertThat(converter.getValue()).isEqualTo(IndexType.HASH);

		converter.setAsText("key");

		assertThat(converter.getValue()).isEqualTo(IndexType.KEY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setAsTextWithIllegalValue() {

		try {
			converter.setAsText("invalid");
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("[invalid] is not a valid IndexType");
			assertThat(expected).hasNoCause();

			throw expected;
		}
		finally {
			assertThat(converter.getValue()).isNull();
		}
	}
}
