/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
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
 * @see DataPolicyConverter
 */
public class DataPolicyConverterUnitTests {

	private final DataPolicyConverter converter = new DataPolicyConverter();

	private int getDataPolicyEnumerationSize() {

		int count = 0;

		for (byte ordinal = 0; ordinal < Byte.MAX_VALUE; ordinal++) {
			try {
				if (DataPolicy.fromOrdinal(ordinal) != null) {
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

		assertThat(DataPolicyConverter.Policy.values().length - 1).isEqualTo(getDataPolicyEnumerationSize());
		assertThat(DataPolicyConverter.Policy.EMPTY.toDataPolicy()).isEqualTo(DataPolicy.EMPTY);
		assertThat(DataPolicyConverter.Policy.NORMAL.toDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		assertThat(DataPolicyConverter.Policy.PRELOADED.toDataPolicy()).isEqualTo(DataPolicy.PRELOADED);
		assertThat(DataPolicyConverter.Policy.PARTITION.toDataPolicy()).isEqualTo(DataPolicy.PARTITION);
		assertThat(DataPolicyConverter.Policy.PERSISTENT_PARTITION.toDataPolicy()).isEqualTo(DataPolicy.PERSISTENT_PARTITION);
		assertThat(DataPolicyConverter.Policy.REPLICATE.toDataPolicy()).isEqualTo(DataPolicy.REPLICATE);
		assertThat(DataPolicyConverter.Policy.PERSISTENT_REPLICATE.toDataPolicy()).isEqualTo(DataPolicy.PERSISTENT_REPLICATE);
		assertThat(DataPolicyConverter.Policy.DEFAULT.toDataPolicy()).isEqualTo(DataPolicy.DEFAULT);
	}

	@Test
	public void convertDataPolicyStrings() {

		assertThat(converter.convert("empty")).isEqualTo(DataPolicy.EMPTY);
		assertThat(converter.convert("Partition")).isEqualTo(DataPolicy.PARTITION);
		assertThat(converter.convert("PERSISTENT_REPLICATE")).isEqualTo(DataPolicy.PERSISTENT_REPLICATE);
		assertThat(converter.convert("invalid")).isNull();
		assertThat(converter.convert(null)).isNull();
	}
}
