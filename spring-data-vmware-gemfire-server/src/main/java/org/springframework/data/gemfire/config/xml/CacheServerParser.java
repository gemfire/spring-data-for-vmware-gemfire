/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.data.gemfire.server.CacheServerFactoryBean;
import org.springframework.data.gemfire.util.SpringExtensions;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/**
 * Bean definition parser for the &lt;gfe:cache-server&lt; SDG XML namespace (XSD) element.
 *
 * @author Costin Leau
 * @author John Blum
 * @see AbstractSimpleBeanDefinitionParser
 * @see CacheServerFactoryBean
 * @since 1.1.0
 */
class CacheServerParser extends AbstractSimpleBeanDefinitionParser {

	private final AtomicInteger cacheServerIdentifier = new AtomicInteger(0);

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getBeanClass(Element element) {
		return CacheServerFactoryBean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isEligibleAttribute(Attr attribute, ParserContext parserContext) {

		return super.isEligibleAttribute(attribute, parserContext)
			&& !"groups".equals(attribute.getName())
			&& !"cache-ref".equals(attribute.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void postProcess(BeanDefinitionBuilder builder, Element element) {

		String cacheRefAttribute = element.getAttribute(ParsingUtils.CACHE_REF_ATTRIBUTE_NAME);

		builder.addPropertyReference("cache", SpringExtensions.defaultIfEmpty(
			cacheRefAttribute, GemfireConstants.DEFAULT_GEMFIRE_CACHE_NAME));

		String groupsAttribute = element.getAttribute("groups");

		if (StringUtils.hasText(groupsAttribute)) {
			builder.addPropertyValue("serverGroups", StringUtils.commaDelimitedListToStringArray(groupsAttribute));
		}

		parseSubscription(element, builder);
	}

	private void parseSubscription(Element element, BeanDefinitionBuilder builder) {

		Element subscriptionConfigElement = DomUtils.getChildElementByTagName(element, "subscription-config");

		if (subscriptionConfigElement != null) {

			ParsingUtils.setPropertyValue(subscriptionConfigElement, builder, "capacity", "subscriptionCapacity");
			ParsingUtils.setPropertyValue(subscriptionConfigElement, builder, "disk-store", "subscriptionDiskStore");

			String evictionTypeAttribute = subscriptionConfigElement.getAttribute("eviction-type");

			if (StringUtils.hasText(evictionTypeAttribute)) {
				builder.addPropertyValue("subscriptionEvictionPolicy", evictionTypeAttribute);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean shouldGenerateIdAsFallback() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext)
			throws BeanDefinitionStoreException {

		String name = super.resolveId(element, definition, parserContext);

		return StringUtils.hasText(name) ? name
			: String.format("gemfireServer%d", cacheServerIdentifier.incrementAndGet());
	}
}
