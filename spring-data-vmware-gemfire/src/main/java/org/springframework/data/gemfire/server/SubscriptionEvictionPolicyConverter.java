// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.server;

import org.springframework.data.gemfire.support.AbstractPropertyEditorConverterSupport;

/**
 * The SubscriptionEvictionPolicyConverter class is a Spring Converter and JavaBeans PropertyEditor for converting
 * Strings into a SubscriptionEvictionPolicy enumerated value.
 *
 * @author John Blum
 * @see SubscriptionEvictionPolicy
 * @see AbstractPropertyEditorConverterSupport
 * @since 1.6.0
 */
@SuppressWarnings("unused")
public class SubscriptionEvictionPolicyConverter extends AbstractPropertyEditorConverterSupport<SubscriptionEvictionPolicy> {

	/**
	 * Converts the given String into a SubscriptionEvictionPolicy enumerated value.
	 *
	 * @param source the String to convert into a SubscriptionEvictionPolicy enum.
	 * @return a SubscriptionEvictionPolicy enumerated value for the given String.
	 * @throws IllegalArgumentException if the String is a valid SubscriptionEvictionPolicy
	 * enumerated value.
	 * @see SubscriptionEvictionPolicy#valueOfIgnoreCase(String)
	 * @see #assertConverted(String, Object, Class)
	 */
	@Override
	public SubscriptionEvictionPolicy convert(final String source) {
		return assertConverted(source, SubscriptionEvictionPolicy.valueOfIgnoreCase(source),
			SubscriptionEvictionPolicy.class);
	}

}
