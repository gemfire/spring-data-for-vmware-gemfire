/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

/**
 * The {@link AbstractPropertyEditorConverterSupport} class is an abstract base class for Spring {@link Converter}
 * implementations that also implement the JavaBeans {@link PropertyEditor} interface.
 *
 * @author John Blum
 * @see PropertyEditor
 * @see PropertyEditorSupport
 * @see Converter
 * @since 1.6.0
 */
@SuppressWarnings("unused")
public abstract class AbstractPropertyEditorConverterSupport<T> extends PropertyEditorSupport
		implements Converter<String, T> {

	/**
	 * Asserts that the given {@link String} was converted into an instance of {@link Class type} T.
	 *
	 * @param source {@link String} to convert.
	 * @param convertedValue converted value of {@link Class type} T.
	 * @param type {@link Class type} of the converted value.
	 * @return the converted value.
	 * @throws IllegalArgumentException if the {@link String} could not be converted into
	 * an instance of {@link Class type} T.
	 */
	protected T assertConverted(String source, T convertedValue, Class<T> type) {

		Assert.notNull(convertedValue, String.format("[%1$s] is not a valid %2$s", source, type.getSimpleName()));

		return convertedValue;
	}

	/**
	 * Sets the value of this {@link PropertyEditor} to the given {@link String}
	 * converted to the appropriate {@link Class type}.
	 *
	 * @param text {@link String} to convert.
	 * @throws IllegalArgumentException if the {@link String} could not be converted into
	 * an instance of {@link Class type} T.
	 * @see #convert(Object)
	 * @see #setValue(Object)
	 */
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		setValue(convert(text));
	}
}
