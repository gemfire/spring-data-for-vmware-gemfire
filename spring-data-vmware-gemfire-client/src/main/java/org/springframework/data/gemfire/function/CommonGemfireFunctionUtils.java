/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionService;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Utility class for function logic shared between clients and servers.
 *
 * @author David Turanski
 * @author John Blum
 * @see Function
 * @see FunctionService
 * @since 1.2.0
 */
public abstract class CommonGemfireFunctionUtils {

	/**
	 * Determine the order position of an annotated method parameter
	 *
	 * @param method the {@link Method} instance
	 * @param targetAnnotationType the annotation
	 * @param requiredTypes an array of valid parameter types for the annotation
	 * @return the parameter position or -1 if the annotated parameter is not found
	 */
	public static int getAnnotationParameterPosition(Method method, Class<?> targetAnnotationType,
			Class<?>[] requiredTypes) {

		int position = -1;

		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		if (parameterAnnotations.length > 0) {

			Class<?>[] parameterTypes = method.getParameterTypes();

      for (int index = 0; index < parameterAnnotations.length; index++) {

				Annotation[] annotations = parameterAnnotations[index];

        for (Annotation annotation : annotations) {
          if (annotation.annotationType().equals(targetAnnotationType)) {

            Assert.state(position < 0, String.format(
                "Method %s signature cannot contain more than one parameter annotated with type %s",
                method.getName(), targetAnnotationType.getName()));

            boolean isRequiredType = false;

            for (Class<?> requiredType : requiredTypes) {
              if (requiredType.isAssignableFrom(parameterTypes[index])) {
                isRequiredType = true;
                break;
              }
            }

            Assert.isTrue(isRequiredType, String.format(
                "Parameter of type %s annotated with %s must be assignable from one of type %s in method %s",
                parameterTypes[index], targetAnnotationType.getName(),
                StringUtils.arrayToCommaDelimitedString(requiredTypes), method.getName()));

            position = index;
          }
        }
      }
		}

		return position;
	}
}
