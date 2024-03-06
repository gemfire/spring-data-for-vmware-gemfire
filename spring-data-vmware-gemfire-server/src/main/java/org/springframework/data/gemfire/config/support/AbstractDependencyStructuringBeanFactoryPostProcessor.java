/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.support;

import java.util.Optional;
import java.util.function.Predicate;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.Index;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.core.type.MethodMetadata;
import org.springframework.data.gemfire.GenericRegionFactoryBean;
import org.springframework.data.gemfire.LocalRegionFactoryBean;
import org.springframework.data.gemfire.PartitionedRegionFactoryBean;
import org.springframework.data.gemfire.ReplicatedRegionFactoryBean;
import org.springframework.data.gemfire.util.SpringExtensions;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * The {@link AbstractDependencyStructuringBeanFactoryPostProcessor} class is a Spring {@link BeanFactoryPostProcessor}
 * post processing the Spring {@link BeanFactory} to help ensure that the dependencies between different Apache Geode
 * or Pivotal GemFire objects (e.g. {@link Region} or an OQL {@link Index}) have been
 * properly declared in order to the lifecycle of those components are upheld according to Apache Geode
 * or Pivotal GemFire requirements/rules.
 *
 * @author John Blum
 * @see BeanFactory
 * @see BeanDefinition
 * @see BeanFactoryPostProcessor
 * @since 2.1.0
 */
@SuppressWarnings("unused")
public abstract class AbstractDependencyStructuringBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	protected BeanDefinition addDependsOn(BeanDefinition beanDefinition, String... beanNames) {
		return SpringExtensions.addDependsOn(beanDefinition, beanNames);
	}

	protected Optional<Object> getPropertyValue(BeanDefinition beanDefinition, String propertyName) {
		return SpringExtensions.getPropertyValue(beanDefinition, propertyName);
	}

	protected boolean isBeanDefinitionOfType(BeanDefinition beanDefinition, Class<?> type) {

		Assert.notNull(type, "Class type must not be null");

		return isBeanDefinitionOfType(beanDefinition, typeName -> type.getName().equals(typeName));
	}

	protected boolean isBeanDefinitionOfType(BeanDefinition beanDefinition, String typeName) {

		return isBeanDefinitionOfType(beanDefinition,
			typeNameArgument -> String.valueOf(typeName).equals(typeNameArgument));
	}

	protected boolean isBeanDefinitionOfType(BeanDefinition beanDefinition, Predicate<String> typeFilter) {

		return Optional.of(beanDefinition)
			.map(it -> beanDefinition.getBeanClassName())
			.filter(StringUtils::hasText)
			.map(typeFilter::test)
			.orElseGet(() ->
				Optional.ofNullable(beanDefinition.getFactoryMethodName())
					.filter(StringUtils::hasText)
					.filter(it -> beanDefinition instanceof AnnotatedBeanDefinition)
					.map(it -> ((AnnotatedBeanDefinition) beanDefinition).getFactoryMethodMetadata())
					.map(MethodMetadata::getReturnTypeName)
					.map(typeFilter::test)
					.orElse(false)
			);
	}

	protected Predicate<String> isRegionBeanType() {

		Predicate<String> genericRegionBeanType =
			typeName -> GenericRegionFactoryBean.class.getName().equals(typeName);

		return genericRegionBeanType.or(typeName -> LocalRegionFactoryBean.class.getName().equals(typeName))
			.or(typeName -> PartitionedRegionFactoryBean.class.getName().equals(typeName))
			.or(typeName -> ReplicatedRegionFactoryBean.class.getName().equals(typeName));
	}
}
