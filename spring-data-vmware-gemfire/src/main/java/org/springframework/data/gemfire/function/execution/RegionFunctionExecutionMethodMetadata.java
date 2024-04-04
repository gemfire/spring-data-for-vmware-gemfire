/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.execution;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.data.gemfire.function.annotation.Filter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 *
 * @author David Turanski
 * @author John Blum
 * @see FunctionExecutionMethodMetadata
 */
class RegionFunctionExecutionMethodMetadata extends FunctionExecutionMethodMetadata<RegionMethodMetadata>  {

	public RegionFunctionExecutionMethodMetadata(Class<?> serviceInterface) {
		super(serviceInterface);
	}

	@Override
	protected RegionMethodMetadata newMetadataInstance(Method method) {
		return new RegionMethodMetadata(method);
	}

}

class RegionMethodMetadata extends MethodMetadata {

	private final int filterArgPosition;

	public RegionMethodMetadata(Method method) {

		super(method);

		this.filterArgPosition = getAnnotationParameterPosition(method, Filter.class,
			new Class<?>[] { Set.class });
	}

	public int getFilterArgPosition() {
		return this.filterArgPosition;
	}

	/**
	 * Determine the order position of a an annotated method parameter
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

			List<Class<?>> requiredTypesList = Arrays.asList(requiredTypes);

			for (int index = 0; index < parameterAnnotations.length; index++) {

				Annotation[] annotations = parameterAnnotations[index];

				if (annotations.length > 0) {
					for (Annotation annotation : annotations) {
						if (annotation.annotationType().equals(targetAnnotationType)) {

							Assert.state(position < 0, String.format(
									"Method %s signature cannot contain more than one parameter annotated with type %s",
									method.getName(), targetAnnotationType.getName()));

							boolean isRequiredType = false;

							for (Class<?> requiredType : requiredTypesList) {
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
		}

		return position;
	}
}
