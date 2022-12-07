/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.expiration;

import org.apache.geode.cache.ExpirationAction;

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
public class ExpirationActionConverter extends AbstractPropertyEditorConverterSupport<ExpirationAction> {

	/**
	 * Converts the given String into an appropriate GemFire ExpirationAction.
	 *
	 * @param source the String to convert into an GemFire ExpirationAction.
	 * @return an GemFire ExpirationAction value for the given String.
	 * @throws java.lang.IllegalArgumentException if the String is not a valid GemFire ExpirationAction.
	 * @see ExpirationActionType#valueOfIgnoreCase(String)
	 * @see org.apache.geode.cache.ExpirationAction
	 */
	@Override
	public ExpirationAction convert(final String source) {
		return assertConverted(source, ExpirationActionType.getExpirationAction(
			ExpirationActionType.valueOfIgnoreCase(source)), ExpirationAction.class);
	}

}
