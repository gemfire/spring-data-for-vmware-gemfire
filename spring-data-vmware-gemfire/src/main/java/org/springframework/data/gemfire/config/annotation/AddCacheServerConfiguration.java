/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.config.annotation.support.AbstractAnnotationConfigSupport;
import org.springframework.data.gemfire.config.xml.GemfireConstants;
import org.springframework.data.gemfire.server.CacheServerFactoryBean;
import org.springframework.data.gemfire.server.SubscriptionEvictionPolicy;
import org.springframework.util.StringUtils;

/**
 * The {@link AddCacheServerConfiguration} class is a Spring {@link ImportBeanDefinitionRegistrar} that registers
 * a {@link CacheServerFactoryBean} definition for the {@link org.apache.geode.cache.server.CacheServer}
 * configuration meta-data defined in {@link EnableCacheServer} annotation.
 *
 * @author John Blum
 * @see org.apache.geode.cache.server.CacheServer
 * @see org.springframework.beans.factory.BeanFactory
 * @see BeanDefinition
 * @see BeanDefinitionBuilder
 * @see BeanDefinitionRegistry
 * @see ImportBeanDefinitionRegistrar
 * @see AnnotationMetadata
 * @see CacheServerApplication
 * @see CacheServerConfiguration
 * @see CacheServerConfigurer
 * @see EnableCacheServer
 * @see AbstractAnnotationConfigSupport
 * @see CacheServerFactoryBean
 * @since 1.9.0
 */
public class AddCacheServerConfiguration extends AbstractAnnotationConfigSupport
		implements ImportBeanDefinitionRegistrar {

	@Autowired(required = false)
	private List<CacheServerConfigurer> cacheServerConfigurers = Collections.emptyList();

	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return EnableCacheServer.class;
	}

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

		if (isAnnotationPresent(importingClassMetadata)) {

			AnnotationAttributes enableCacheServerAttributes = getAnnotationAttributes(importingClassMetadata);

			registerCacheServerFactoryBeanDefinition(enableCacheServerAttributes, registry);
		}
	}

	/**
	 * Registers a {@link CacheServerFactoryBean} bean definition for the given {@link EnableCacheServer} annotation
	 * configuration meta-data.
	 *
	 * @param enableCacheServerAttributes attributes for the {@link EnableCacheServer} annotation.
	 * @param registry {@link BeanDefinitionRegistry} used to register the {@link CacheServerFactoryBean}
	 * bean definition.
	 * @see BeanDefinitionBuilder
	 * @see BeanDefinitionRegistry
	 * @see CacheServerFactoryBean
	 */
	protected void registerCacheServerFactoryBeanDefinition(AnnotationAttributes enableCacheServerAttributes,
			BeanDefinitionRegistry registry) {

		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(CacheServerFactoryBean.class);

		String beanName = registerCacheServerFactoryBeanDefinition(builder.getBeanDefinition(),
			enableCacheServerAttributes.getString("name"), registry);

		builder.addPropertyReference("cache", GemfireConstants.DEFAULT_GEMFIRE_CACHE_NAME);
		builder.addPropertyValue("cacheServerConfigurers", resolveCacheServerConfigurers());

		builder.addPropertyValue("autoStartup",
			resolveProperty(namedCacheServerProperty(beanName, "auto-startup"),
				resolveProperty(cacheServerProperty("auto-startup"),
					enableCacheServerAttributes.getBoolean("autoStartup"))));

		builder.addPropertyValue("bindAddress",
			resolveProperty(namedCacheServerProperty(beanName, "bind-address"),
				resolveProperty(cacheServerProperty("bind-address"),
					enableCacheServerAttributes.getString("bindAddress"))));

		builder.addPropertyValue("hostNameForClients",
			resolveProperty(namedCacheServerProperty(beanName, "hostname-for-clients"),
				resolveProperty(cacheServerProperty("hostname-for-clients"),
					enableCacheServerAttributes.getString("hostnameForClients"))));

		builder.addPropertyValue("loadPollInterval",
			resolveProperty(namedCacheServerProperty(beanName, "load-poll-interval"),
				resolveProperty(cacheServerProperty("load-poll-interval"),
					enableCacheServerAttributes.<Long>getNumber("loadPollInterval"))));

		builder.addPropertyValue("maxConnections",
			resolveProperty(namedCacheServerProperty(beanName, "max-connections"),
				resolveProperty(cacheServerProperty("max-connections"),
					enableCacheServerAttributes.<Integer>getNumber("maxConnections"))));

		builder.addPropertyValue("maxMessageCount",
			resolveProperty(namedCacheServerProperty(beanName, "max-message-count"),
				resolveProperty(cacheServerProperty("max-message-count"),
					enableCacheServerAttributes.<Integer>getNumber("maxMessageCount"))));

		builder.addPropertyValue("maxThreads",
			resolveProperty(namedCacheServerProperty(beanName, "max-threads"),
				resolveProperty(cacheServerProperty("max-threads"),
					enableCacheServerAttributes.<Integer>getNumber("maxThreads"))));

		builder.addPropertyValue("maxTimeBetweenPings",
			resolveProperty(namedCacheServerProperty(beanName, "max-time-between-pings"),
				resolveProperty(cacheServerProperty("max-time-between-pings"),
					enableCacheServerAttributes.<Integer>getNumber("maxTimeBetweenPings"))));

		builder.addPropertyValue("messageTimeToLive",
			resolveProperty(namedCacheServerProperty(beanName, "message-time-to-live"),
				resolveProperty(cacheServerProperty("message-time-to-live"),
					enableCacheServerAttributes.<Integer>getNumber("messageTimeToLive"))));

		builder.addPropertyValue("port",
			resolveProperty(namedCacheServerProperty(beanName, "port"),
				resolveProperty(cacheServerProperty("port"),
					enableCacheServerAttributes.<Integer>getNumber("port"))));

		builder.addPropertyValue("socketBufferSize",
			resolveProperty(namedCacheServerProperty(beanName, "socket-buffer-size"),
				resolveProperty(cacheServerProperty("socket-buffer-size"),
					enableCacheServerAttributes.<Integer>getNumber("socketBufferSize"))));

		builder.addPropertyValue("subscriptionCapacity",
			resolveProperty(namedCacheServerProperty(beanName, "subscription-capacity"),
				resolveProperty(cacheServerProperty("subscription-capacity"),
					enableCacheServerAttributes.<Integer>getNumber("subscriptionCapacity"))));

		builder.addPropertyValue("subscriptionDiskStore",
			resolveProperty(namedCacheServerProperty(beanName, "subscription-disk-store-name"),
				resolveProperty(cacheServerProperty("subscription-disk-store-name"),
					enableCacheServerAttributes.getString("subscriptionDiskStoreName"))));

		builder.addPropertyValue("subscriptionEvictionPolicy",
			resolveProperty(namedCacheServerProperty(beanName, "subscription-eviction-policy"),
				SubscriptionEvictionPolicy.class, resolveProperty(cacheServerProperty("subscription-eviction-policy"),
					SubscriptionEvictionPolicy.class, enableCacheServerAttributes.getEnum("subscriptionEvictionPolicy"))));

		builder.addPropertyValue("tcpNoDelay",
			resolveProperty(namedCacheServerProperty(beanName, "tcp-no-delay"),
				resolveProperty(cacheServerProperty("tcp-no-delay"),
					enableCacheServerAttributes.getBoolean("tcpNoDelay"))));
	}

	private List<CacheServerConfigurer> resolveCacheServerConfigurers() {

		return Optional.ofNullable(this.cacheServerConfigurers)
			.filter(cacheServerConfigurers -> !cacheServerConfigurers.isEmpty())
			.orElseGet(() ->
				Collections.singletonList(LazyResolvingComposableCacheServerConfigurer.create(getBeanFactory())));

	}

	protected String registerCacheServerFactoryBeanDefinition(AbstractBeanDefinition beanDefinition, String beanName,
			BeanDefinitionRegistry registry) {

		if (StringUtils.hasText(beanName)) {

			BeanDefinitionHolder beanDefinitionHolder = newBeanDefinitionHolder(beanDefinition, beanName);

			BeanDefinitionReaderUtils.registerBeanDefinition(beanDefinitionHolder, registry);

			return beanName;
		}
		else {
			return BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition, registry);
		}
	}

	protected BeanDefinitionHolder newBeanDefinitionHolder(BeanDefinition beanDefinition, String beanName) {
		return new BeanDefinitionHolder(beanDefinition, beanName);
	}
}
