/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.config;

import static java.util.Arrays.stream;
import static org.springframework.data.gemfire.util.ArrayUtils.nullSafeArray;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.geode.cache.execute.Function;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.gemfire.function.GemfireFunctionUtils;
import org.springframework.data.gemfire.function.annotation.GemfireFunction;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * Spring {@link BeanPostProcessor} that discovers bean components configured as {@link Function} implementations,
 * i.e. beans containing {@link Method methods} annotated with {@link GemfireFunction}.
 *
 * @author David Turanski
 * @author John Blum
 * @see Annotation
 * @see Method
 * @see Function
 * @see BeanPostProcessor
 * @see GemfireFunction
 */
public class GemfireFunctionBeanPostProcessor implements BeanPostProcessor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

		registerAnyDeclaredGemfireFunctionAnnotatedMethods(bean);

		return bean;
	}

	private void registerAnyDeclaredGemfireFunctionAnnotatedMethods(Object bean) {

		stream(nullSafeArray(ReflectionUtils.getAllDeclaredMethods(bean.getClass()), Method.class)).forEach(method -> {

			GemfireFunction gemfireFunctionAnnotation = AnnotationUtils.getAnnotation(method, GemfireFunction.class);

			if (gemfireFunctionAnnotation != null) {

				Assert.isTrue(Modifier.isPublic(method.getModifiers()),
					String.format("The bean [%s] method [%s] annotated with [%s] must be public",
						bean.getClass().getName(), method.getName(), GemfireFunction.class.getName()));

				AnnotationAttributes gemfireFunctionAttributes = resolveAnnotationAttributes(gemfireFunctionAnnotation);

				GemfireFunctionUtils.registerFunctionForPojoMethod(bean, method,
					gemfireFunctionAttributes, false);
			}
		});
	}

	private AnnotationAttributes resolveAnnotationAttributes(Annotation annotation) {

		return AnnotationAttributes.fromMap(AnnotationUtils.getAnnotationAttributes(annotation,
			false, true));
	}
}
