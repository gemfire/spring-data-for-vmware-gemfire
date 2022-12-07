/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.eviction;

import org.apache.geode.cache.EvictionAction;

import org.springframework.data.gemfire.support.AbstractPropertyEditorConverterSupport;

/**
 * The EvictionActionConverter class is a Spring Converter and JavaBeans PropertyEditor that converts
 * an Object value into an instance of GemFire EvictionAction.
 *
 * @author John Blum
 * @see EvictionActionType
 * @see org.springframework.data.gemfire.support.AbstractPropertyEditorConverterSupport
 * @see org.apache.geode.cache.EvictionAction
 * @since 1.6.0
 */
@SuppressWarnings("unused")
public class EvictionActionConverter extends AbstractPropertyEditorConverterSupport<EvictionAction> {

	/**
	 * Converts the given String into a GemFire EvictionAction value.
	 *
	 * @param source the String to convert.
	 * @return the GemFire EvictionAction value matching the given String.
	 * @throws java.lang.IllegalArgumentException if the String could not be converted into
	 * an instance of GemFire EvictionAction.
	 * @see org.apache.geode.cache.EvictionAction
	 */
	@Override
	public EvictionAction convert(final String source) {
		return assertConverted(source, EvictionActionType.getEvictionAction(
			EvictionActionType.valueOfIgnoreCase(source)), EvictionAction.class);
	}

}
