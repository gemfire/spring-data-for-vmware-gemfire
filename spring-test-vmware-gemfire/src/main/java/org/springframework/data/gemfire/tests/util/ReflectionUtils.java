/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Utility {@link Class} for performing reflective and introspective Java {@link Object} operations.
 *
 * @author John Blum
 * @see Class
 * @see Object
 * @see Constructor
 * @see Field
 * @see Method
 * @see org.springframework.util.ReflectionUtils
 * @since 0.0.10
 */
@SuppressWarnings("unused")
public abstract class ReflectionUtils extends org.springframework.util.ReflectionUtils {

	@SuppressWarnings("unchecked")
	public static <T> T getFieldValue(Object target, String fieldName) throws NoSuchFieldException {

		Assert.notNull(target, "Target object must not be null");
		Assert.hasText(fieldName, String.format("Field name [%s] must be specified", fieldName));

		Field field = findField(target.getClass(), fieldName);

		return Optional.ofNullable(field)
			.map(ReflectionUtils::makeAccessibleReturnField)
			.map(it -> (T) getField(it, target))
			.orElseThrow(() ->
				new NoSuchFieldException(String.format("Field with name [%s] was not found on Object of type [%s]",
					fieldName, target.getClass().getName())));
	}

	@SuppressWarnings("rawtypes")
	public static Constructor makeAccessibleReturnConstructor(Constructor constructor) {
		makeAccessible(constructor);
		return constructor;
	}

	public static Field makeAccessibleReturnField(Field field) {
		makeAccessible(field);
		return field;
	}

	public static Method makeAccessibleReturnMethod(Method method) {
		makeAccessible(method);
		return method;
	}

	public static <T> T setField(T target, String fieldName, Object value) throws NoSuchFieldException {

		Assert.notNull(target, "Target object must not be null");
		Assert.hasText(fieldName, String.format("Field name [%s] must be specified", fieldName));

		Field field = findField(target.getClass(), fieldName);

		if (field != null) {

			Class<?> fieldType = field.getType();

			Assert.isTrue(value == null || fieldType.isInstance(value),
				String.format("The value type [%1$s] is not assignment compatible with the field type [%2$s]",
					ObjectUtils.nullSafeClassName(value), fieldType.getName()));

			makeAccessibleReturnField(field);
			setField(field, target, value);

			return target;
		}
		else {
			throw new NoSuchFieldException(String.format("Field [%s] was not found on Object of type [%s]",
				fieldName, target.getClass().getName()));
		}
	}
}
