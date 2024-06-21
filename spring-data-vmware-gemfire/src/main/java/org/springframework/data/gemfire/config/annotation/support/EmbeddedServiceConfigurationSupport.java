/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation.support;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalStateException;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.CacheFactoryBean;
import org.springframework.data.gemfire.LocatorFactoryBean;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.config.annotation.AbstractCacheConfiguration;
import org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer;
import org.springframework.data.gemfire.config.annotation.LocatorConfigurer;
import org.springframework.data.gemfire.config.annotation.PeerCacheConfigurer;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * An abstract base class supporting the configuration of Apache Geode embedded services.
 *
 * @author John Blum
 * @see Map
 * @see Properties
 * @see BeanFactory
 * @see AutowireCapableBeanFactory
 * @see BeanDefinitionHolder
 * @see BeanPostProcessor
 * @see NamedBeanHolder
 * @see BeanDefinitionBuilder
 * @see BeanDefinitionRegistry
 * @see ImportBeanDefinitionRegistrar
 * @see AnnotationAttributes
 * @see AnnotationMetadata
 * @see AbstractCacheConfiguration
 * @see AbstractAnnotationConfigSupport
 * @since 1.9.0
 */
@SuppressWarnings("unused")
public abstract class EmbeddedServiceConfigurationSupport extends AbstractAnnotationConfigSupport
		implements ImportBeanDefinitionRegistrar {

	public static final Integer DEFAULT_PORT = 0;
	public static final String DEFAULT_HOST = "localhost";

	@Autowired(required = false)
	private AbstractCacheConfiguration cacheConfiguration;

	/**
	 * Returns a reference to an instance of the {@link AbstractCacheConfiguration} class used to configure
	 * a GemFire (Singleton, client or peer) cache instance along with it's associated, embedded services.
	 *
	 * @param <T> {@link Class} type extension of {@link AbstractCacheConfiguration}.
	 * @return a reference to a single {@link AbstractCacheConfiguration} instance.
	 * @throws IllegalStateException if the {@link AbstractCacheConfiguration} reference was not configured.
	 * @see AbstractCacheConfiguration
	 */
	@SuppressWarnings("unchecked")
	protected @NonNull <T extends AbstractCacheConfiguration> T getCacheConfiguration() {

		return Optional.ofNullable((T) this.cacheConfiguration)
			.orElseThrow(() -> newIllegalStateException("AbstractCacheConfiguration is required"));
	}

	@Override
	public final void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata,
			@NonNull BeanDefinitionRegistry registry) {

		if (isAnnotationPresent(importingClassMetadata)) {

			AnnotationAttributes annotationAttributes = getAnnotationAttributes(importingClassMetadata);

			registerBeanDefinitions(importingClassMetadata, annotationAttributes, registry);
			setGemFireProperties(importingClassMetadata, annotationAttributes, registry);
		}
	}

	protected void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata,
			@NonNull AnnotationAttributes annotationAttributes, @NonNull BeanDefinitionRegistry registry) {

		registerBeanDefinitions(importingClassMetadata, (Map<String, Object>) annotationAttributes, registry);
	}

	protected void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata,
			@NonNull Map<String, Object> annotationAttributes, @NonNull BeanDefinitionRegistry registry) {

	}

	protected void setGemFireProperties(@NonNull AnnotationMetadata importingClassMetadata,
			@NonNull AnnotationAttributes annotationAttributes, @NonNull BeanDefinitionRegistry registry) {

		Properties gemfireProperties = toGemFireProperties(annotationAttributes);

		if (hasProperties(gemfireProperties)) {

			try {
				getCacheConfiguration().add(gemfireProperties);
			}
			catch (Exception ignore) {
				//registerGemFirePropertiesBeanPostProcessor(registry, gemfireProperties);
				registerGemFirePropertiesConfigurer(registry, gemfireProperties);
			}
		}
	}

	protected abstract @Nullable Properties toGemFireProperties(@NonNull Map<String, Object> annotationAttributes);

	protected boolean hasProperties(@Nullable Properties properties) {
		return !CollectionUtils.isEmpty(properties);
	}

	protected void registerGemFirePropertiesBeanPostProcessor(@NonNull BeanDefinitionRegistry registry,
			Properties gemFireProperties) {

		registerBeanDefinition(registry, GemFirePropertiesBeanPostProcessor.class, gemFireProperties);
	}

	protected void registerGemFirePropertiesConfigurer(@NonNull BeanDefinitionRegistry registry,
			Properties gemfireProperties) {

		registerClientGemFirePropertiesConfigurer(registry, gemfireProperties);
		registerLocatorGemFirePropertiesConfigurer(registry, gemfireProperties);
		registerPeerGemFirePropertiesConfigurer(registry, gemfireProperties);
	}

	private void registerBeanDefinition(@NonNull BeanDefinitionRegistry registry, @NonNull Class<?> beanType,
			@NonNull Properties gemfireProperties) {

		BeanDefinitionBuilder builder =
			BeanDefinitionBuilder.genericBeanDefinition(beanType);

		builder.addConstructorArgValue(gemfireProperties);

		BeanDefinitionReaderUtils.registerBeanDefinition(newBeanDefinitionHolder(builder), registry);
	}

	protected void registerClientGemFirePropertiesConfigurer(BeanDefinitionRegistry registry,
			Properties gemfireProperties) {

		registerBeanDefinition(registry, ClientGemFirePropertiesConfigurer.class, gemfireProperties);
	}

	protected void registerLocatorGemFirePropertiesConfigurer(BeanDefinitionRegistry registry,
		Properties gemfireProperties) {

		registerBeanDefinition(registry, LocatorGemFirePropertiesConfigurer.class, gemfireProperties);
	}

	protected void registerPeerGemFirePropertiesConfigurer(BeanDefinitionRegistry registry,
			Properties gemfireProperties) {

		registerBeanDefinition(registry, PeerGemFirePropertiesConfigurer.class, gemfireProperties);
	}

	protected BeanDefinitionHolder newBeanDefinitionHolder(BeanDefinitionBuilder builder) {

		String beanName = generateBeanName(builder.getRawBeanDefinition().getBeanClass().getSimpleName());

		return new BeanDefinitionHolder(builder.getBeanDefinition(), beanName);
	}

	protected String generateBeanName() {
		return generateBeanName(getAnnotationType());
	}

	protected String generateBeanName(Class<?> typeQualifier) {
		return generateBeanName(typeQualifier.getSimpleName());
	}

	protected String generateBeanName(String nameQualifier) {
		return String.format("%1$s.%2$s", getClass().getName(), nameQualifier);
	}

	/**
	 * Resolves a Spring managed bean with the given {@link Class} type from the Spring {@link BeanFactory}.
	 *
	 * It is assumed that the given typed bean is the only bean of this {@link Class} type.  If more than 1 bean
	 * of the given {@link Class} type is found, then the Spring {@link BeanFactory} will throw
	 * a {@link org.springframework.beans.factory.NoUniqueBeanDefinitionException}.
	 *
	 * If the {@link BeanFactory} is an instance of {@link AutowireCapableBeanFactory}, then the returned bean
	 * will also be configured.
	 *
	 * @param <T> {@link Class} type of the registered Spring managed bean.
	 * @param beanType required {@link Class} type of the registered Spring managed bean.
	 * @return a Spring managed bean instance for the given, required {@link Class} type, or {@literal null}
	 * if no bean instance of the given, required {@link Class} type could be found.
	 * @throws BeansException if the Spring manage bean of the required {@link Class} type could not be resolved.
	 * @see #getBeanFactory()
	 */
	@SuppressWarnings("unchecked")
	protected <T> T resolveBean(Class<T> beanType) {

		BeanFactory beanFactory = getBeanFactory();

		if (beanFactory instanceof AutowireCapableBeanFactory autowiringBeanFactory) {

			NamedBeanHolder<T> beanHolder = autowiringBeanFactory.resolveNamedBean(beanType);

			return (T) autowiringBeanFactory.configureBean(beanHolder.getBeanInstance(), beanHolder.getBeanName());
		}
		else {
			return beanFactory.getBean(beanType);
		}
	}

	protected @Nullable String resolveHost(@Nullable String hostname) {
		return resolveHost(hostname, DEFAULT_HOST);
	}

	protected @Nullable String resolveHost(@Nullable String hostname, @Nullable String defaultHostname) {
		return StringUtils.hasText(hostname) ? hostname : defaultHostname;
	}

	protected @Nullable Integer resolvePort(@Nullable Integer port) {
		return resolvePort(port, DEFAULT_PORT);
	}

	protected @Nullable Integer resolvePort(@Nullable Integer port, @Nullable Integer defaultPort) {
		return port != null ? port : defaultPort;
	}

	protected static class AbstractGemFirePropertiesConfigurer {

		private final Properties gemfireProperties;

		protected AbstractGemFirePropertiesConfigurer(@NonNull Properties gemfireProperties) {

			Assert.notEmpty(gemfireProperties, "GemFire Properties must not be null");

			this.gemfireProperties = gemfireProperties;
		}

		protected void configureGemFireProperties(@NonNull CacheFactoryBean bean) {
			bean.getProperties().putAll(this.gemfireProperties);
		}
	}

	protected static class ClientGemFirePropertiesConfigurer extends AbstractGemFirePropertiesConfigurer
			implements ClientCacheConfigurer {

		protected ClientGemFirePropertiesConfigurer(@NonNull Properties gemfireProperties) {
			super(gemfireProperties);
		}

		@Override
		public void configure(@Nullable String beanName, @NonNull ClientCacheFactoryBean bean) {
			configureGemFireProperties(bean);
		}
	}

	protected static class LocatorGemFirePropertiesConfigurer implements LocatorConfigurer {

		private final Properties gemfireProperties;

		public LocatorGemFirePropertiesConfigurer(@NonNull Properties gemfireProperties) {

			Assert.notEmpty(gemfireProperties, "GemFire Properties must not be null");

			this.gemfireProperties = gemfireProperties;
		}

		@Override
		public void configure(@Nullable String beanName, @NonNull LocatorFactoryBean bean) {

			Properties gemfireProperties = bean.getGemFireProperties();

			gemfireProperties.putAll(this.gemfireProperties);

			bean.setGemFireProperties(gemfireProperties);
		}
	}

	protected static class PeerGemFirePropertiesConfigurer extends AbstractGemFirePropertiesConfigurer
			implements PeerCacheConfigurer {

		protected PeerGemFirePropertiesConfigurer(@NonNull Properties gemfireProperties) {
			super(gemfireProperties);
		}

		@Override
		public void configure(@Nullable String beanName, @NonNull CacheFactoryBean bean) {
			configureGemFireProperties(bean);
		}
	}

	/**
	 * Spring {@link BeanPostProcessor} used to process before initialization Pivotal GemFire or Apache Geode
	 * {@link Properties} defined as a bean in the Spring application context.
	 *
	 * @see BeanPostProcessor
	 */
	protected static class GemFirePropertiesBeanPostProcessor implements BeanPostProcessor {

		protected static final String GEMFIRE_PROPERTIES_BEAN_NAME = "gemfireProperties";

		private final Properties gemfireProperties;

		/**
		 * Constructs a new instance of the {@link GemFirePropertiesBeanPostProcessor} initialized with
		 * the given GemFire/Geode {@link Properties}.
		 *
		 * @param gemfireProperties {@link Properties} used to configure Pivotal GemFire or Apache Geode.
		 * @throws IllegalArgumentException if {@link Properties} are {@literal null} or empty.
		 * @see Properties
		 */
		protected GemFirePropertiesBeanPostProcessor(@NonNull Properties gemfireProperties) {

			Assert.notEmpty(gemfireProperties, "GemFire Properties must not be null");

			this.gemfireProperties = gemfireProperties;
		}

		/**
		 * {{@inheritDoc}}
		 */
		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

			if (bean instanceof Properties gemfirePropertiesBean && GEMFIRE_PROPERTIES_BEAN_NAME.equals(beanName)) {
				gemfirePropertiesBean.putAll(this.gemfireProperties);
			}

			return bean;
		}
	}
}
