/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire;

import org.apache.geode.cache.InterestPolicy;

import org.springframework.data.gemfire.support.AbstractPropertyEditorConverterSupport;

/**
 * The InterestPolicyConverter class is a Spring Converter implementation and Java PropertyEditor handling
 * the conversion between Strings and GemFire InterestPolicy values.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.support.AbstractPropertyEditorConverterSupport
 * @see org.apache.geode.cache.InterestPolicy
 * @since 1.6.0
 */
@SuppressWarnings("unused")
public class InterestPolicyConverter extends AbstractPropertyEditorConverterSupport<InterestPolicy> {

	/**
	 * Converts the given String into a GemFire InterestPolicy value.
	 *
	 * @param source the String value to convert into a GemFire InterestPolicy value.
	 * @return a GemFire InterestPolicy value for the given String description of the GemFire InterestPolicy
	 * @throws java.lang.IllegalArgumentException if the String is not a valid GemFire InterestPolicy.
	 * @see org.springframework.data.gemfire.InterestPolicyType#getInterestPolicy(InterestPolicyType)
	 * @see org.springframework.data.gemfire.InterestPolicyType#valueOfIgnoreCase(String)
	 * @see #assertConverted(String, Object, Class)
	 */
	@Override
	public InterestPolicy convert(final String source) {
		return assertConverted(source, InterestPolicyType.getInterestPolicy(
			InterestPolicyType.valueOfIgnoreCase(source)), InterestPolicy.class);
	}

}
