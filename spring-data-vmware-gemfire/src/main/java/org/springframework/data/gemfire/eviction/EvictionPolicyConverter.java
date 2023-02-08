/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.eviction;

import org.springframework.data.gemfire.support.AbstractPropertyEditorConverterSupport;

/**
 * The EvictionTypeConverter class is a Spring Converter used to convert a String value into
 * a corresponding EvictionType enumerated value.
 *
 * @author John Blum
 * @see EvictionPolicyType
 * @see AbstractPropertyEditorConverterSupport
 * @since 1.6.0
 */
public class EvictionPolicyConverter extends AbstractPropertyEditorConverterSupport<EvictionPolicyType> {

	/**
	 * Converts the given String into a matching EvictionType.
	 *
	 * @param source the String value to convert into an EvictionType.
	 * @return the EvictionType matching the given String.
	 * @throws IllegalArgumentException if the String value does not represent a valid EvictionType.
	 * @see EvictionPolicyType#valueOfIgnoreCase(String)
	 * @see #assertConverted(String, Object, Class)
	 */
	@Override
	public EvictionPolicyType convert(final String source) {
		return assertConverted(source, EvictionPolicyType.valueOfIgnoreCase(source), EvictionPolicyType.class);
	}

}
