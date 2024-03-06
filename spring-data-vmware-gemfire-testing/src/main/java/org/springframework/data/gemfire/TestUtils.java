/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire;

import java.lang.reflect.Field;

/**
 * Utility class containing common functionality used when writing tests.
 *
 * @author Costin Leau
 * @author John Blum
 */
public abstract class TestUtils {

	@SuppressWarnings("unchecked")
	public static <T> T readField(String name, Object target) throws Exception {
		Field field = findField(name, target);

		if (field == null) {
			throw new IllegalArgumentException(String.format("Cannot find field [%1$s] in class [%2$s]",
				name, target.getClass().getName()));
		}

		field.setAccessible(true);

		return (T) field.get(target);
	}

	/* (non-Javadoc) */
	private static Field findField(String fieldName, Object target) {
		return findField(fieldName, target.getClass());
	}

	/* (non-Javadoc) */
	private static Field findField(String fieldName, Class<?> type) {
		Field field = null;

		while (field == null && !type.equals(Object.class)) {
			try {
				field = type.getDeclaredField(fieldName);
			}
			catch (Throwable ignore) {
			}
			finally {
				type = type.getSuperclass();
			}
		}

		return field;
	}
}
