/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static java.util.Arrays.stream;
import static org.springframework.data.gemfire.util.ArrayUtils.nullSafeArray;
import static org.springframework.data.gemfire.util.CollectionUtils.asSet;
import static org.springframework.data.gemfire.util.CollectionUtils.nullSafeIterable;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.geode.cache.Region;
import org.apache.geode.compression.Compressor;
import org.apache.geode.compression.SnappyCompressor;

import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.ResolvableRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.AbstractAnnotationConfigSupport;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.data.gemfire.util.SpringExtensions;
import org.springframework.util.StringUtils;

/**
 * The {@link CompressionConfiguration} class is a Spring {@link ImportAware} implementation capable of
 * enabling Pivotal GemFire/Apache Geode cache {@link Region Regions} data compression.
 *
 * @author John Blum
 * @see Region
 * @see BeanFactoryPostProcessor
 * @see Bean
 * @see Configuration
 * @see ImportAware
 * @see EnableCompression
 * @see AbstractAnnotationConfigSupport
 * @since 2.0.2
 */
@Configuration
@SuppressWarnings("unused")
public class CompressionConfiguration extends AbstractAnnotationConfigSupport implements ImportAware {

	protected static final String SNAPPY_COMPRESSOR_BEAN_NAME = "SnappyCompressor";

	private String compressorBeanName = SNAPPY_COMPRESSOR_BEAN_NAME;

	private Set<String> regionNames = new HashSet<>();

	/**
	 * Returns the {@link EnableCompression} {@link Annotation} {@link Class} type.
	 *
	 * @return the {@link EnableCompression} {@link Annotation} {@link Class} type.
	 * @see EnableOffHeap
	 * @see Annotation
	 */
	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return EnableCompression.class;
	}

	public void setCompressorBeanName(String compressorBeanName) {
		this.compressorBeanName = compressorBeanName;
	}

	protected String resolveCompressorBeanName() {

		return Optional.ofNullable(this.compressorBeanName)
			.filter(StringUtils::hasText)
			.orElse(SNAPPY_COMPRESSOR_BEAN_NAME);
	}

	public void setRegionNames(String[] regionNames) {
		setRegionNames(asSet(nullSafeArray(regionNames, String.class)));
	}

	public void setRegionNames(Iterable<String> regionNames) {
		this.regionNames = CollectionUtils.addAll(this.regionNames, nullSafeIterable(regionNames));
	}

	protected Set<String> resolveRegionNames() {
		return Collections.unmodifiableSet(this.regionNames);
	}

	@Override
	public void setImportMetadata(AnnotationMetadata importingClassMetadata) {

		if (isAnnotationPresent(importingClassMetadata)) {

			AnnotationAttributes enableCompressionAttributes = getAnnotationAttributes(importingClassMetadata);

			setCompressorBeanName(resolveProperty(cacheCompressionProperty("compressor-bean-name"),
				enableCompressionAttributes.getString("compressorBeanName")));

			setRegionNames(resolveProperty(cacheCompressionProperty("region-names"),
				String[].class, enableCompressionAttributes.getStringArray("regionNames")));
		}
	}

	@Bean(SNAPPY_COMPRESSOR_BEAN_NAME)
	Compressor snappyCompressor() {
		return new SnappyCompressor();
	}

	@Bean
	BeanFactoryPostProcessor regionCompressionBeanFactoryPostProcessor() {

		String resolvedCompressorBeanName = resolveCompressorBeanName();

		return beanFactory ->
			stream(nullSafeArray(beanFactory.getBeanDefinitionNames(), String.class)).forEach(beanName ->
				Optional.of(beanFactory.getBeanDefinition(beanName))
					.filter(beanDefinition -> isTargetedRegionBean(beanName, beanDefinition, beanFactory))
					.ifPresent(beanDefinition -> SpringExtensions.setPropertyReference(
						beanDefinition, "compressor", resolvedCompressorBeanName)));
	}

	private boolean isTargetedRegionBean(String beanName, BeanDefinition beanDefinition,
			ConfigurableListableBeanFactory beanFactory) {

		return isNamedRegion(beanName, beanDefinition, beanFactory) && isRegionBean(beanDefinition, beanFactory);
	}

	private boolean isRegionBean(BeanDefinition beanDefinition, ConfigurableListableBeanFactory beanFactory) {

		return Optional.ofNullable(beanDefinition)
			.flatMap(it -> resolveBeanClass(it, beanFactory.getBeanClassLoader()))
			.filter(beanClass -> ResolvableRegionFactoryBean.class.isAssignableFrom(beanClass))
			.isPresent();
	}

	private boolean isNamedRegion(String beanName, BeanDefinition beanDefinition,
			ConfigurableListableBeanFactory beanFactory) {

		Set<String> resolvedRegionNames = resolveRegionNames();

		return CollectionUtils.isEmpty(resolvedRegionNames)
			|| CollectionUtils.containsAny(resolvedRegionNames, resolveBeanNames(beanName, beanDefinition, beanFactory));
	}

	private Collection<String> resolveBeanNames(String beanName, BeanDefinition beanDefinition,
			ConfigurableListableBeanFactory beanFactory) {

		Collection<String> beanNames = new HashSet<>();

		beanNames.add(beanName);

		Collections.addAll(beanNames, beanFactory.getAliases(beanName));

		PropertyValue regionName = beanDefinition.getPropertyValues().getPropertyValue("regionName");

		if (regionName != null) {

			Object regionNameValue = regionName.getValue();

			if (regionNameValue != null) {
				beanNames.add(regionNameValue.toString());
			}
		}

		return beanNames;
	}
}
