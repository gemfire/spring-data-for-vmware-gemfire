/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.support;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.lucene.LuceneIndex;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.data.gemfire.search.lucene.LuceneIndexFactoryBean;
import org.springframework.util.StringUtils;

/**
 * The {@link LuceneIndexRegionBeanFactoryPostProcessor} class is a Spring {@link BeanFactoryPostProcessor} ensuring
 * that a {@link LuceneIndex} is created before the {@link Region} on which the {@link LuceneIndex} is defined.
 *
 * @author John Blum
 * @see Region
 * @see LuceneIndex
 * @see BeanDefinition
 * @see BeanFactoryPostProcessor
 * @see ConfigurableListableBeanFactory
 * @see LuceneIndexFactoryBean
 * @see AbstractDependencyStructuringBeanFactoryPostProcessor
 * @since 2.1.0
 */
@SuppressWarnings("unused")
public class LuceneIndexRegionBeanFactoryPostProcessor extends AbstractDependencyStructuringBeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

		Map<String, String> regionNameToLuceneIndexBeanName = new HashMap<>();
		Map<String, String> regionNameToRegionBeanName = new HashMap<>();

		Arrays.stream(beanFactory.getBeanDefinitionNames()).forEach(beanName -> {

			BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);

			if (isBeanDefinitionOfType(beanDefinition, LuceneIndexFactoryBean.class)) {
				resolveRegionNameFromLuceneIndex(beanDefinition)
					.ifPresent(regionName -> regionNameToLuceneIndexBeanName.put(regionName, beanName));
			}
			else if (isBeanDefinitionOfType(beanDefinition, isRegionBeanType())) {
				resolveRegionNameFromRegionBean(beanName, beanDefinition)
					.ifPresent(regionName -> regionNameToRegionBeanName.put(regionName, beanName));
			}
		});

		regionNameToRegionBeanName.keySet().stream().forEach(regionName -> {

			if (regionNameToLuceneIndexBeanName.containsKey(regionName)) {

				String regionBeanName = regionNameToRegionBeanName.get(regionName);
				String luceneIndexBeanName = regionNameToLuceneIndexBeanName.get(regionName);

				addDependsOn(beanFactory.getBeanDefinition(regionBeanName), luceneIndexBeanName);
			}
		});
	}

	private Optional<String> resolveRegionNameFromLuceneIndex(BeanDefinition beanDefinition) {

		return getPropertyValue(beanDefinition, "regionPath")
			.map(this::asFullyQualifiedRegionName);
	}

	private Optional<String> resolveRegionNameFromRegionBean(String beanName, BeanDefinition beanDefinition) {

		String regionName = asString(getPropertyValue(beanDefinition, "regionName"));
		String name = asString(getPropertyValue(beanDefinition, "name"));

		return Optional.ofNullable(StringUtils.hasText(regionName) ? regionName
			: (StringUtils.hasText(name) ? name : beanName)).filter(StringUtils::hasText);
	}

	private String asFullyQualifiedRegionName(Object regionPath) {

		return Optional.ofNullable(regionPath)
			.map(String::valueOf)
			.map(String::trim)
			.map(it -> {

				int index = it.lastIndexOf(Region.SEPARATOR);

				return index > 0 ? it : it.substring(1);
			})
			.filter(StringUtils::hasText)
			.orElse(null);
	}

	@SuppressWarnings("all")
	private String asString(Optional<Object> value) {

		return value.map(String::valueOf)
			.map(String::trim)
			.filter(StringUtils::hasText)
			.orElse(null);
	}
}
