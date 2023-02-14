/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.mock.beans.factory.config;

import static org.mockito.Mockito.spy;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.geode.cache.Region;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * Spring {@link BeanPostProcessor} that creates spies for all managed {@link Region Regions} (beans)
 * in the Spring {@link ApplicationContext}.
 *
 * @author John Blum
 * @see Region
 * @see org.mockito.Mockito#spy(Object)
 * @see BeanPostProcessor
 * @see ApplicationContext
 * @since 0.0.22
 */
@SuppressWarnings("unused")
public class RegionSpyingBeanPostProcessor implements BeanPostProcessor {

	private final Set<String> regionBeanNames;

	public RegionSpyingBeanPostProcessor(String... regionBeanNames) {
		this(Arrays.asList(ArrayUtils.nullSafeArray(regionBeanNames, String.class)));
	}

	public RegionSpyingBeanPostProcessor(@NonNull Iterable<String> regionBeanNames) {

		this.regionBeanNames =
			StreamSupport.stream(CollectionUtils.nullSafeIterable(regionBeanNames).spliterator(), false)
				.filter(StringUtils::hasText)
				.collect(Collectors.toSet());
	}

	protected boolean isRegion(@Nullable Object target) {
		return target instanceof Region;
	}

	protected boolean isRegionBeanNameMatch(@NonNull String beanName) {

		return this.regionBeanNames.isEmpty()
			|| (StringUtils.hasText(beanName) && this.regionBeanNames.contains(beanName));
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
		return isRegion(bean) && isRegionBeanNameMatch(beanName) ? doSpy(bean) : bean;
	}

	protected @Nullable <T> T doSpy(@Nullable T target) {
		return target != null ? spy(target) : target;
	}
}
