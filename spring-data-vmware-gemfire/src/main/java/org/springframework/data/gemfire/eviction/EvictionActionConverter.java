/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
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
 * @see AbstractPropertyEditorConverterSupport
 * @see EvictionAction
 * @since 1.6.0
 */
@SuppressWarnings("unused")
public class EvictionActionConverter extends AbstractPropertyEditorConverterSupport<EvictionAction> {

	/**
	 * Converts the given String into a GemFire EvictionAction value.
	 *
	 * @param source the String to convert.
	 * @return the GemFire EvictionAction value matching the given String.
	 * @throws IllegalArgumentException if the String could not be converted into
	 * an instance of GemFire EvictionAction.
	 * @see EvictionAction
	 */
	@Override
	public EvictionAction convert(final String source) {
		return assertConverted(source, EvictionActionType.getEvictionAction(
			EvictionActionType.valueOfIgnoreCase(source)), EvictionAction.class);
	}

}
