/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */
package org.springframework.data.gemfire.tests.mock.beans.factory.config;

import static org.mockito.Mockito.spy;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.data.gemfire.gud.api.GudRegion;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * Spring {@link BeanPostProcessor} that creates spies for all managed {@link GudRegion GudRegions} (beans)
 * in the Spring {@link ApplicationContext}.
 *
 * @author John Blum
 * @see GudRegion
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

	protected boolean isGudRegion(@Nullable Object target) {
		return target instanceof GudRegion;
	}

	protected boolean isGudRegionBeanNameMatch(@NonNull String beanName) {

		return this.regionBeanNames.isEmpty()
			|| (StringUtils.hasText(beanName) && this.regionBeanNames.contains(beanName));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
		return isGudRegion(bean) && isGudRegionBeanNameMatch(beanName) ? doSpy(bean) : bean;
	}

	protected @Nullable <T> T doSpy(@Nullable T target) {
		return target != null ? spy(target) : target;
	}
}
