/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.asyncqueue.AsyncEventQueue;
import org.apache.geode.cache.wan.GatewaySender;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;

import org.w3c.dom.Element;

/**
 * Abstract Spring XML parser for peer {@link Region} bean definitions.
 *
 * @author John Blum
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.asyncqueue.AsyncEventQueue
 * @see org.apache.geode.cache.wan.GatewaySender
 * @see org.springframework.beans.factory.support.BeanDefinitionBuilder
 * @see org.springframework.beans.factory.xml.ParserContext
 * @see org.springframework.data.gemfire.config.xml.AbstractRegionParser
 * @see org.w3c.dom.Element
 * @since 2.2.0
 */
public abstract class AbstractPeerRegionParser extends AbstractRegionParser {

	/**
	 * @inheritDoc
	 */
	@Override
	protected void doParseRegionConfiguration(Element element, ParserContext parserContext,
			BeanDefinitionBuilder regionBuilder, BeanDefinitionBuilder regionAttributesBuilder, boolean subRegion) {

		super.doParseRegionConfiguration(element, parserContext, regionBuilder, regionAttributesBuilder, subRegion);

		ParsingUtils.setPropertyValue(element, regionBuilder, "async-event-queue-ids");
		ParsingUtils.setPropertyValue(element, regionBuilder, "gateway-sender-ids");

		parseCollectionOfCustomSubElements(element, parserContext, regionBuilder, AsyncEventQueue.class.getName(),
			"async-event-queue", "asyncEventQueues");

		parseCollectionOfCustomSubElements(element, parserContext, regionBuilder, GatewaySender.class.getName(),
			"gateway-sender", "gatewaySenders");
	}
}
