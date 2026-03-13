/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.client;

import org.springframework.data.gemfire.gud.api.GudInterestResultPolicy;
import org.springframework.data.gemfire.support.AbstractPropertyEditorConverterSupport;

/**
 * The InterestResultPolicyConverter class is a Spring Converter and JavaBeans PropertyEditor capable of converting
 * a String into a GemFire InterestResultPolicyConverter.
 *
 * @author John Blum
 * @see AbstractPropertyEditorConverterSupport
 * @see GudInterestResultPolicy
 * @since 1.6.0
 */
public class InterestResultPolicyConverter extends AbstractPropertyEditorConverterSupport<GudInterestResultPolicy> {

	/**
	 * Converts the given String into an instance of GemFire InterestResultPolicy.
	 *
	 * @param source the String to convert into an InterestResultPolicy value.
	 * @return a GemFire InterestResultPolicy value for the given String.
	 * @throws IllegalArgumentException if the String is not a valid GemFire InterestResultPolicy.
	 * @see InterestResultPolicyType#getInterestResultPolicy(InterestResultPolicyType)
	 * @see InterestResultPolicyType#valueOfIgnoreCase(String)
	 * @see #assertConverted(String, Object, Class)
	 * @see GudInterestResultPolicy
	 */
	@Override
	public GudInterestResultPolicy convert(final String source) {
		return assertConverted(source, InterestResultPolicyType.getInterestResultPolicy(
			InterestResultPolicyType.valueOfIgnoreCase(source)), GudInterestResultPolicy.class);
	}

}
