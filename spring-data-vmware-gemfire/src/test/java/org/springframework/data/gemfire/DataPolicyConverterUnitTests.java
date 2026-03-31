/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.springframework.data.gemfire.gud.api.GudDataPolicy;

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
				if (GudDataPolicy.fromOrdinal(ordinal) != null && !GudDataPolicy.fromOrdinal(ordinal).withPartitioning()) {
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
		assertThat(DataPolicyConverter.Policy.EMPTY.toDataPolicy()).isEqualTo(GudDataPolicy.EMPTY);
		assertThat(DataPolicyConverter.Policy.NORMAL.toDataPolicy()).isEqualTo(GudDataPolicy.NORMAL);
		assertThat(DataPolicyConverter.Policy.PRELOADED.toDataPolicy()).isEqualTo(GudDataPolicy.PRELOADED);
		assertThat(DataPolicyConverter.Policy.DEFAULT.toDataPolicy()).isEqualTo(GudDataPolicy.DEFAULT);
	}

	@Test
	public void convertDataPolicyStrings() {

		assertThat(converter.convert("empty")).isEqualTo(GudDataPolicy.EMPTY);
		assertThat(converter.convert("invalid")).isNull();
		assertThat(converter.convert(null)).isNull();
	}
}
