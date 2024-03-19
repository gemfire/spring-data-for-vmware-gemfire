/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.config;

import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.data.gemfire.util.SpringExtensions;
import org.springframework.util.Assert;

/**
 * Function execution configuration used by bean definition builders
 *
 * @author David Turanski
 * @author John Blum
 */
class FunctionExecutionConfiguration  {

	private Class<?> functionExecutionInterface;

	private final AnnotationAttributes annotationAttributes;

	private final String annotationType;

	/* Constructor used for testing purposes only! */
	FunctionExecutionConfiguration() {
		this.annotationType = null;
		this.annotationAttributes = null;
	}

	FunctionExecutionConfiguration(ScannedGenericBeanDefinition beanDefinition, String annotationType) {

		try {

			this.annotationType = annotationType;

			this.annotationAttributes = AnnotationAttributes.fromMap(beanDefinition.getMetadata()
				.getAnnotationAttributes(annotationType, true));

			this.functionExecutionInterface =
				beanDefinition.resolveBeanClass(beanDefinition.getClass().getClassLoader());

			assertFunctionExecutionInterfaceIsValid(annotationType);
		}
		catch (ClassNotFoundException cause) {
			throw new RuntimeException(cause);
		}
	}

	private void assertFunctionExecutionInterfaceIsValid(String annotationType) {

		boolean valid = this.functionExecutionInterface != null && this.functionExecutionInterface.isInterface();

		Assert.isTrue(valid, String.format("The annotation [%1$s] only applies to an interface; It is not valid for type [%2$s]",
			annotationType, SpringExtensions.nullSafeName(this.functionExecutionInterface)));
	}

	String getAnnotationType() {
		return this.annotationType;
	}

	Object getAttribute(String name) {
		return this.annotationAttributes.get(name);
	}

	AnnotationAttributes getAttributes() {
		return this.annotationAttributes;
	}

	Class<?> getFunctionExecutionInterface() {
		return this.functionExecutionInterface;
	}
}
