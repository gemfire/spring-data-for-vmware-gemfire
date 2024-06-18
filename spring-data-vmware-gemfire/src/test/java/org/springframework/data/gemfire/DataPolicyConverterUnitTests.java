/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.apache.geode.cache.DataPolicy;

/**
 * Unit Tests for {@link DataPolicyConverter}.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.data.gemfire.DataPolicyConverter
 */
public class DataPolicyConverterUnitTests {

	private final DataPolicyConverter converter = new DataPolicyConverter();

	private int getDataPolicyEnumerationSize() {

		int count = 0;

		for (byte ordinal = 0; ordinal < Byte.MAX_VALUE; ordinal++) {
			try {
				if (DataPolicy.fromOrdinal(ordinal) != null && !DataPolicy.fromOrdinal(ordinal).withPartitioning()) {
					count++;
				}
			}
			catch (ArrayIndexOutOfBoundsException ignore) {
				break;
			}
			catch (Throwable ignore) {
			}
		}

		return count;
	}

	@Test
	public void policyToDataPolicyConversion() {

		assertThat(DataPolicyConverter.Policy.values().length).isEqualTo(getDataPolicyEnumerationSize() - 1);
		assertThat(DataPolicyConverter.Policy.EMPTY.toDataPolicy()).isEqualTo(DataPolicy.EMPTY);
		assertThat(DataPolicyConverter.Policy.NORMAL.toDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		assertThat(DataPolicyConverter.Policy.PRELOADED.toDataPolicy()).isEqualTo(DataPolicy.PRELOADED);
		assertThat(DataPolicyConverter.Policy.DEFAULT.toDataPolicy()).isEqualTo(DataPolicy.DEFAULT);
	}

	@Test
	public void convertDataPolicyStrings() {

		assertThat(converter.convert("empty")).isEqualTo(DataPolicy.EMPTY);
		assertThat(converter.convert("invalid")).isNull();
		assertThat(converter.convert(null)).isNull();
	}
}
