/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.pdx.PdxSerializer;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.CacheFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.AbstractAnnotationConfigSupport;
import org.springframework.data.gemfire.config.support.PdxDiskStoreAwareBeanFactoryPostProcessor;
import org.springframework.data.gemfire.mapping.GemfireMappingContext;
import org.springframework.data.gemfire.mapping.MappingPdxSerializer;
import org.springframework.data.gemfire.support.NoOpBeanFactoryPostProcessor;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

/**
 * The {@link PdxConfiguration} class is a Spring {@link Configuration} class that configures PDX
 * on a {@link GemFireCache} instance.
 *
 * @author John Blum
 * @see GemFireCache
 * @see PdxSerializer
 * @see BeanFactory
 * @see BeanFactoryPostProcessor
 * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 * @see Bean
 * @see Configuration
 * @see ImportAware
 * @see AnnotationAttributes
 * @see AnnotationMetadata
 * @see CacheFactoryBean
 * @see AbstractAnnotationConfigSupport
 * @see PdxDiskStoreAwareBeanFactoryPostProcessor
 * @see GemfireMappingContext
 * @see MappingPdxSerializer
 * @see NoOpBeanFactoryPostProcessor
 * @since 2.1.0
 */
@SuppressWarnings("unused")
@Configuration("PdxConfiguration")
public class PdxConfiguration extends AbstractAnnotationConfigSupport implements ImportAware {

	public static final boolean DEFAULT_IGNORE_UNREAD_FIELDS = false;
	public static final boolean DEFAULT_PERSISTENT = false;
	public static final boolean DEFAULT_READ_SERIALIZED = false;

	public static final String DEFAULT_PDX_DISK_STORE_NAME = "";
	public static final String DEFAULT_PDX_SERIALIZER_BEAN_NAME = "";

	private Boolean ignoreUnreadFields;
	private Boolean persistent;
	private Boolean readSerialized;

	private Class<?>[] includeDomainTypes = {};

	private String diskStoreName;
	private String serializerBeanName;

	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return EnablePdx.class;
	}

	@Override
	protected BeanFactory getBeanFactory() {
		return super.getBeanFactory();
	}

	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {

		if (isAnnotationPresent(importMetadata)) {

			AnnotationAttributes enablePdxAttributes = getAnnotationAttributes(importMetadata);

			setDiskStoreName(resolveProperty(pdxProperty("disk-store-name"),
				enablePdxAttributes.containsKey("diskStoreName")
					? enablePdxAttributes.getString("diskStoreName")
					: null));

			setIgnoreUnreadFields(resolveProperty(pdxProperty("ignore-unread-fields"),
				enablePdxAttributes.containsKey("ignoreUnreadFields")
					? enablePdxAttributes.getBoolean("ignoreUnreadFields")
					: DEFAULT_IGNORE_UNREAD_FIELDS));

			setIncludeDomainTypes(enablePdxAttributes.getClassArray("includeDomainTypes"));

			setPersistent(resolveProperty(pdxProperty("persistent"),
				enablePdxAttributes.containsKey("persistent")
					? enablePdxAttributes.getBoolean("persistent")
					: null));

			setReadSerialized(resolveProperty(pdxProperty("read-serialized"),
				enablePdxAttributes.containsKey("readSerialized")
					? enablePdxAttributes.getBoolean("readSerialized")
					: null));

			setSerializerBeanName(resolveProperty(pdxProperty("serializer-bean-name"),
				enablePdxAttributes.containsKey("serializerBeanName")
					? enablePdxAttributes.getString("serializerBeanName")
					: null));
		}
	}

	void setDiskStoreName(String diskStoreName) {
		this.diskStoreName = diskStoreName;
	}

	protected Optional<String> getDiskStoreName() {
		return Optional.ofNullable(this.diskStoreName).filter(StringUtils::hasText);
	}

	void setIgnoreUnreadFields(Boolean ignoreUnreadFields) {
		this.ignoreUnreadFields = ignoreUnreadFields;
	}

	protected boolean isIgnoreUnreadFields() {
		return Boolean.TRUE.equals(this.ignoreUnreadFields);
	}

	void setIncludeDomainTypes(Class<?>[] includeDomainTypes) {
		this.includeDomainTypes = includeDomainTypes;
	}

	protected Class<?>[] getIncludeDomainTypes() {
		return ArrayUtils.nullSafeArray(this.includeDomainTypes, Class.class);
	}

	void setPersistent(Boolean persistent) {
		this.persistent = persistent;
	}

	protected boolean isPersistent() {
		return Boolean.TRUE.equals(this.persistent);
	}

	void setReadSerialized(Boolean readSerialized) {
		this.readSerialized = readSerialized;
	}

	protected boolean isReadSerialized() {
		return Boolean.TRUE.equals(this.readSerialized);
	}

	void setSerializerBeanName(String serializerBeanName) {
		this.serializerBeanName = serializerBeanName;
	}

	protected Optional<String> getSerializerBeanName() {
		return Optional.ofNullable(this.serializerBeanName).filter(StringUtils::hasText);
	}

	@Bean
	BeanFactoryPostProcessor pdxDiskStoreAwareBeanFactoryPostProcessor() {

		return getDiskStoreName()
			.<BeanFactoryPostProcessor>map(PdxDiskStoreAwareBeanFactoryPostProcessor::new)
			.orElse(NoOpBeanFactoryPostProcessor.INSTANCE);
	}

	@Bean
	ClientCacheConfigurer clientCachePdxConfigurer() {
		return (beanName, clientCacheFactoryBean) -> configurePdx(clientCacheFactoryBean);
	}

	/**
	 * Configures Pivotal GemFire/Apache Geode cache PDX Serialization.
	 *
	 * @param cacheFactoryBean {@link CacheFactoryBean} instance on which to configure PDX.
	 * with PDX de/serialization capabilities.
	 * @see CacheFactoryBean
	 * @see <a href="https://geode.apache.org/docs/guide/113/developing/data_serialization/gemfire_pdx_serialization.html">Geode PDX Serialization</a>
	 */
	protected void configurePdx(@NonNull CacheFactoryBean cacheFactoryBean) {

		getDiskStoreName().ifPresent(cacheFactoryBean::setPdxDiskStoreName);

		cacheFactoryBean.setPdxIgnoreUnreadFields(isIgnoreUnreadFields());
		cacheFactoryBean.setPdxPersistent(isPersistent());
		cacheFactoryBean.setPdxReadSerialized(isReadSerialized());
		cacheFactoryBean.setPdxSerializer(resolvePdxSerializer());
	}

	/**
	 * Resolves the configured {@link ConversionService} from the {@link BeanFactory}.
	 *
	 * @return an {@link Optional optionally} configured {@link ConversionService}.
	 * @see ConversionService
	 * @see Optional
	 * @see #getBeanFactory()
	 */
	protected Optional<ConversionService> resolveConversionService() {

		return Optional.of(getBeanFactory())
			.filter(it -> it instanceof ConfigurableBeanFactory)
			.map(it -> ((ConfigurableBeanFactory) it).getConversionService());
	}

	/**
	 * Returns any configured {@link GemfireMappingContext} registered in the {@link BeanFactory}.
	 *
	 * @return an {@link Optional optionally} configured {@link GemfireMappingContext}.
	 * @see GemfireMappingContext
	 * @see Optional
	 * @see #getBeanFactory()
	 */
	protected Optional<GemfireMappingContext> resolveMappingContext() {

		try {
			return Optional.of(getBeanFactory().getBean(GemfireMappingContext.class));
		}
		catch (Throwable ignore) {
			return Optional.empty();
		}
	}

	/**
	 * Resolves the {@link PdxSerializer} used to configure the cache for PDX Serialization.
	 *
	 * implementing the {@link PdxSerializer} interface.
	 * @return the resolved {@link PdxSerializer} from configuration.
	 * @see PdxSerializer
	 * @see #getBeanFactory()
	 */
	protected @NonNull PdxSerializer resolvePdxSerializer() {

		BeanFactory beanFactory = getBeanFactory();

		PdxSerializer serializer = getSerializerBeanName()
			.filter(beanFactory::containsBean)
			.map(beanName -> beanFactory.getBean(beanName, PdxSerializer.class))
			.orElseGet(this::newPdxSerializer);

		if (serializer instanceof MappingPdxSerializer mappingSerializer) {
			mappingSerializer.setIncludeTypeFilters(buildIncludeTypeFilters());
		}

		return serializer;
	}

	/**
	 * Constructs a new instance of {@link PdxSerializer}.
	 *
	 * @param <T> {@link Class} type of the {@link PdxSerializer}.
	 * @return a new instance of {@link PdxSerializer}.
	 * @see PdxSerializer
	 */
	@SuppressWarnings("unchecked")
	protected @NonNull <T extends PdxSerializer> T newPdxSerializer() {

		return (T) MappingPdxSerializer.create(resolveMappingContext().orElse(null),
			resolveConversionService().orElse(null));
	}

	private @NonNull Predicate<Class<?>> buildIncludeTypeFilters() {

		Predicate<Class<?>> includeTypeFilter = type -> false;

		for (Class<?> domainType : getIncludeDomainTypes()) {
			if (Objects.nonNull(domainType)) {
				includeTypeFilter = includeTypeFilter.or(type -> domainType.isAssignableFrom(type));
			}
		}

		return includeTypeFilter;
	}
}
