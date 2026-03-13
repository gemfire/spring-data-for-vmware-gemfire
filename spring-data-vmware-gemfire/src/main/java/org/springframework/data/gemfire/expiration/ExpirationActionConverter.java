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

package org.springframework.data.gemfire.expiration;

import org.springframework.data.gemfire.gud.api.GudExpirationAction;
import org.springframework.data.gemfire.support.AbstractPropertyEditorConverterSupport;

/**
 * The ExpirationActionTypeConverter class is a Spring Converter used to convert a String value into
 * a corresponding ExpirationActionType enumerated value.
 *
 * @author John Blum
 * @see java.beans.PropertyEditorSupport
 * @see org.springframework.core.convert.converter.Converter
 * @see ExpirationActionType
 * @since 1.6.0
 */
public class ExpirationActionConverter extends AbstractPropertyEditorConverterSupport<GudExpirationAction> {

	/**
	 * Converts the given String into an appropriate GemFire ExpirationAction.
	 *
	 * @param source the String to convert into an GemFire ExpirationAction.
	 * @return an GemFire ExpirationAction value for the given String.
	 * @throws IllegalArgumentException if the String is not a valid GemFire ExpirationAction.
	 * @see ExpirationActionType#valueOfIgnoreCase(String)
	 * @see GudExpirationAction
	 */
	@Override
	public GudExpirationAction convert(final String source) {
		return assertConverted(source, ExpirationActionType.getExpirationAction(
			ExpirationActionType.valueOfIgnoreCase(source)), GudExpirationAction.class);
	}

}
