// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.config.xml;

import org.apache.geode.cache.asyncqueue.AsyncEventQueue;
import org.apache.geode.cache.wan.GatewaySender;

import org.w3c.dom.Element;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.data.gemfire.LookupRegionFactoryBean;
import org.springframework.util.xml.DomUtils;

/**
 * Bean definition parser for the &lt;gfe:lookup-region&gt; SDG XML namespace (XSD) element.
 *
 * @author Costin Leau
 * @author David Turanski
 * @author John Blum
 * @see AsyncEventQueue
 * @see GatewaySender
 * @see BeanDefinitionBuilder
 * @see ParserContext
 * @see LookupRegionFactoryBean
 * @see AbstractRegionParser
 * @see Element
 */
class LookupRegionParser extends AbstractRegionParser {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getRegionFactoryClass() {
		return LookupRegionFactoryBean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doParseRegion(Element element, ParserContext parserContext, BeanDefinitionBuilder builder,
			boolean subRegion) {

		super.doParse(element, builder);

		String resolvedCacheRef =
			ParsingUtils.resolveCacheReference(element.getAttribute(ParsingUtils.CACHE_REF_ATTRIBUTE_NAME));

		builder.addPropertyReference("cache", resolvedCacheRef);

		ParsingUtils.setPropertyValue(element, builder, "async-event-queue-ids");
		ParsingUtils.setPropertyValue(element, builder, "cloning-enabled");
		ParsingUtils.setPropertyValue(element, builder, "eviction-maximum");
		ParsingUtils.setPropertyValue(element, builder, "gateway-sender-ids");
		ParsingUtils.setPropertyValue(element, builder, "name");

		ParsingUtils.parseExpiration(element, parserContext, builder);

		parseCollectionOfCustomSubElements(element, parserContext, builder, AsyncEventQueue.class.getName(),
			"async-event-queue", "asyncEventQueues");

		parseCollectionOfCustomSubElements(element, parserContext, builder, GatewaySender.class.getName(),
			"gateway-sender", "gatewaySenders");

		Element cacheListenerElement = DomUtils.getChildElementByTagName(element, "cache-listener");

		if (cacheListenerElement != null) {
			builder.addPropertyValue("cacheListeners",
				ParsingUtils.parseRefOrNestedBeanDeclaration(cacheListenerElement, parserContext, builder));
		}

		Element cacheLoaderElement = DomUtils.getChildElementByTagName(element, "cache-loader");

		if (cacheLoaderElement != null) {
			builder.addPropertyValue("cacheLoader",
				ParsingUtils.parseRefOrSingleNestedBeanDeclaration(cacheLoaderElement, parserContext, builder));
		}

		Element cacheWriterElement = DomUtils.getChildElementByTagName(element, "cache-writer");

		if (cacheWriterElement != null) {
			builder.addPropertyValue("cacheWriter",
				ParsingUtils.parseRefOrSingleNestedBeanDeclaration(cacheWriterElement, parserContext, builder));
		}

		if (!subRegion) {
			parseSubRegions(element, parserContext, resolvedCacheRef);
		}
	}
}
