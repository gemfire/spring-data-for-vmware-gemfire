/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.extensions.spring.context.annotation;

import java.lang.annotation.Annotation;
import java.util.Optional;

import org.apache.shiro.util.StringUtils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.gemfire.tests.util.SpringUtils;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Spring {@link BeanFactoryPostProcessor} implementation used to post process {@link BeanDefinition BeanDefinitions}
 * annotated with {@link DependencyOf} annotations.
 *
 * @author John Blum
 * @see BeanDefinition
 * @see BeanFactoryPostProcessor
 * @see ConfigurableListableBeanFactory
 * @see DependencyOf
 * @since 0.0.23
 */
public class DependencyOfBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	protected static final Class<? extends Annotation> DEPENDENCY_OF_TYPE = DependencyOf.class;

	protected static final String VALUE_ATTRIBUTE_NAME = "value";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {

		String[] dependencyOfAnnotatedBeanNames =
			ArrayUtils.nullSafeArray(beanFactory.getBeanNamesForAnnotation(DEPENDENCY_OF_TYPE), String.class);

		for (String beanName : dependencyOfAnnotatedBeanNames) {

			Annotation dependencyOf = beanFactory.findAnnotationOnBean(beanName, DEPENDENCY_OF_TYPE);

			Optional.ofNullable(dependencyOf)
				.map(this::getAnnotationAttributes)
				.map(this::getValueAttribute)
				.ifPresent(dependentBeanNames -> {
					for (String dependentBeanName : dependentBeanNames) {
						Optional.ofNullable(dependentBeanName)
							.filter(StringUtils::hasText)
							.map(beanFactory::getBeanDefinition)
							.ifPresent(dependentBeanDefinition ->
								SpringUtils.addDependsOn(dependentBeanDefinition, beanName));
					}
				});
		}
	}

	private @Nullable AnnotationAttributes getAnnotationAttributes(@NonNull Annotation annotation) {

		return annotation != null
			? AnnotationAttributes.fromMap(AnnotationUtils.getAnnotationAttributes(annotation))
			: null;
	}

	private @Nullable String[] getValueAttribute(@NonNull AnnotationAttributes annotationAttributes) {

		return annotationAttributes != null
			? annotationAttributes.getStringArray(VALUE_ATTRIBUTE_NAME)
			: null;
	}
}
