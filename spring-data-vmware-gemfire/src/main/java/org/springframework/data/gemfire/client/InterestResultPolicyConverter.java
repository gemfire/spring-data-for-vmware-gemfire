/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.client;

import org.apache.geode.cache.InterestResultPolicy;

import org.springframework.data.gemfire.support.AbstractPropertyEditorConverterSupport;

/**
 * The InterestResultPolicyConverter class is a Spring Converter and JavaBeans PropertyEditor capable of converting
 * a String into a GemFire InterestResultPolicyConverter.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.support.AbstractPropertyEditorConverterSupport
 * @see org.apache.geode.cache.InterestResultPolicy
 * @since 1.6.0
 */
public class InterestResultPolicyConverter extends AbstractPropertyEditorConverterSupport<InterestResultPolicy> {

	/**
	 * Converts the given String into an instance of GemFire InterestResultPolicy.
	 *
	 * @param source the String to convert into an InterestResultPolicy value.
	 * @return a GemFire InterestResultPolicy value for the given String.
	 * @throws java.lang.IllegalArgumentException if the String is not a valid GemFire InterestResultPolicy.
	 * @see org.springframework.data.gemfire.client.InterestResultPolicyType#getInterestResultPolicy(InterestResultPolicyType)
	 * @see org.springframework.data.gemfire.client.InterestResultPolicyType#valueOfIgnoreCase(String)
	 * @see #assertConverted(String, Object, Class)
	 * @see org.apache.geode.cache.InterestResultPolicy
	 */
	@Override
	public InterestResultPolicy convert(final String source) {
		return assertConverted(source, InterestResultPolicyType.getInterestResultPolicy(
			InterestResultPolicyType.valueOfIgnoreCase(source)), InterestResultPolicy.class);
	}

}
