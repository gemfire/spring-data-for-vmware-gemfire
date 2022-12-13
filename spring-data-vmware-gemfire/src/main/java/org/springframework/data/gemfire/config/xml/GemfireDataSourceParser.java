/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.gemfire.client.GemfireDataSourcePostProcessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Bean definition parser for the &lt;gfe-data:datasource&gt; SDG XML namespace (XSD) element.
 *
 * @author David Turanski
 * @author John Blum
 * @see Element
 * @see BeanDefinition
 * @see AbstractBeanDefinition
 * @see BeanDefinitionBuilder
 * @see AbstractBeanDefinitionParser
 * @see ParserContext
 * @see GemfireDataSourcePostProcessor
 * @see ClientCacheParser
 * @see PoolParser
 */
class GemfireDataSourceParser extends AbstractBeanDefinitionParser {

	static final String SUBSCRIPTION_ENABLED_ATTRIBUTE_NAME = "subscription-enabled";
	static final String SUBSCRIPTION_ENABLED_PROPERTY_NAME = "subscriptionEnabled";

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {

		parseAndRegisterClientCache(element, parserContext);
		parseAndRegisterPool(element, parserContext);
		registerGemFireDataSourceBeanPostProcessor(parserContext);

		return null;
	}

	@SuppressWarnings("all")
	private void parseAndRegisterClientCache(Element element, ParserContext parserContext) {

		BeanDefinition clientCacheDefinition = new ClientCacheParser().parse(element, parserContext);

		parserContext.getRegistry()
			.registerBeanDefinition(GemfireConstants.DEFAULT_GEMFIRE_CACHE_NAME, clientCacheDefinition);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Registered GemFire ClientCache bean [%1$s] of type [%2$s]%n",
				GemfireConstants.DEFAULT_GEMFIRE_CACHE_NAME, clientCacheDefinition.getBeanClassName()));
		}
	}

	@SuppressWarnings("all")
	private void parseAndRegisterPool(Element element, ParserContext parserContext) {

		BeanDefinition poolDefinition = new PoolParser().parse(element, parserContext);

		MutablePropertyValues poolProperties = poolDefinition.getPropertyValues();

		if (!element.hasAttribute(SUBSCRIPTION_ENABLED_ATTRIBUTE_NAME)) {
			poolProperties.add(SUBSCRIPTION_ENABLED_PROPERTY_NAME, true);
		}

		parserContext.getRegistry().registerBeanDefinition(GemfireConstants.DEFAULT_GEMFIRE_POOL_NAME, poolDefinition);
	}

	private void registerGemFireDataSourceBeanPostProcessor(@NonNull ParserContext parserContext) {

		BeanFactory beanFactory = resolveBeanFactory(parserContext);

		if (beanFactory != null) {

			BeanDefinitionBuilder builder =
				BeanDefinitionBuilder.genericBeanDefinition(GemfireDataSourcePostProcessor.class);

			builder.addPropertyValue("beanFactory", beanFactory);

			BeanDefinitionReaderUtils.registerWithGeneratedName(builder.getBeanDefinition(), parserContext.getRegistry());
		}
	}

	private @Nullable BeanFactory resolveBeanFactory(@NonNull ParserContext parserContext) {

		BeanDefinitionRegistry registry = parserContext.getRegistry();

		return registry instanceof ConfigurableApplicationContext
			? ((ConfigurableApplicationContext) registry).getBeanFactory()
			: registry instanceof BeanFactory
			? (BeanFactory) registry
			: null;
	}
}
