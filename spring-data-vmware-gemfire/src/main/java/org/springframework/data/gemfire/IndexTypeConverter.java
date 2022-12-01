/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire;

import org.springframework.data.gemfire.support.AbstractPropertyEditorConverterSupport;
import org.springframework.util.StringUtils;

/**
 * The IndexTypeConverter class is a Spring Converter implementation as well as a JavaBeans PropertyEditor
 * that converts a given String value into a proper IndexType.
 *
 * @author John Blum
 * @see IndexType
 * @see AbstractPropertyEditorConverterSupport
 * @since 1.5.2
 */
@SuppressWarnings("unused")
public class IndexTypeConverter extends AbstractPropertyEditorConverterSupport<IndexType> {

	/**
	 * Converts the given String value into an appropriate IndexType.
	 *
	 * @param value the String to convert into an appropriate IndexType enumerated value.
	 * @return an IndexType converted from the given String.
	 * @throws IllegalArgumentException if the given String could not be converted into
	 * an appropriate IndexType enumerated value.
	 * @see #assertConverted(String, Object, Class)
	 * @see IndexType#valueOfIgnoreCase(String)
	 * @see StringUtils#trimWhitespace(String)
	 */
	@Override
	public IndexType convert(final String value) {
		return assertConverted(value, IndexType.valueOfIgnoreCase(StringUtils.trimWhitespace(value)), IndexType.class);
	}

}
