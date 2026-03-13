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

package org.springframework.data.gemfire.eviction;

import org.springframework.data.gemfire.gud.api.GudEvictionAction;
import org.springframework.data.gemfire.support.AbstractPropertyEditorConverterSupport;

/**
 * The EvictionActionConverter class is a Spring Converter and JavaBeans PropertyEditor that converts
 * an Object value into an instance of GemFire EvictionAction.
 *
 * @author John Blum
 * @see EvictionActionType
 * @see AbstractPropertyEditorConverterSupport
 * @see GudEvictionAction
 * @since 1.6.0
 */
@SuppressWarnings("unused")
public class EvictionActionConverter extends AbstractPropertyEditorConverterSupport<GudEvictionAction> {

	/**
	 * Converts the given String into a GemFire EvictionAction value.
	 *
	 * @param source the String to convert.
	 * @return the GemFire EvictionAction value matching the given String.
	 * @throws IllegalArgumentException if the String could not be converted into
	 * an instance of GemFire EvictionAction.
	 * @see GudEvictionAction
	 */
	@Override
	public GudEvictionAction convert(final String source) {
		return assertConverted(source, EvictionActionType.getEvictionAction(
			EvictionActionType.valueOfIgnoreCase(source)), GudEvictionAction.class);
	}

}
