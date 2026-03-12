/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Migrated to GUD API types
 */

package org.springframework.data.gemfire;

import org.springframework.data.gemfire.gud.api.GudInterestPolicy;
import org.springframework.data.gemfire.support.AbstractPropertyEditorConverterSupport;

/**
 * The InterestPolicyConverter class is a Spring Converter implementation and Java PropertyEditor handling
 * the conversion between Strings and GemFire InterestPolicy values.
 *
 * @author John Blum
 * @see AbstractPropertyEditorConverterSupport
 * @see GudInterestPolicy
 * @since 1.6.0
 */
@SuppressWarnings("unused")
public class InterestPolicyConverter extends AbstractPropertyEditorConverterSupport<GudInterestPolicy> {

	/**
	 * Converts the given String into a GemFire InterestPolicy value.
	 *
	 * @param source the String value to convert into a GemFire InterestPolicy value.
	 * @return a GemFire InterestPolicy value for the given String description of the GemFire InterestPolicy
	 * @throws IllegalArgumentException if the String is not a valid GemFire InterestPolicy.
	 * @see InterestPolicyType#getInterestPolicy(InterestPolicyType)
	 * @see InterestPolicyType#valueOfIgnoreCase(String)
	 * @see #assertConverted(String, Object, Class)
	 */
	@Override
	public GudInterestPolicy convert(final String source) {
		return assertConverted(source, InterestPolicyType.getInterestPolicy(
			InterestPolicyType.valueOfIgnoreCase(source)), GudInterestPolicy.class);
	}

}
