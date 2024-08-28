/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.xml;

import org.w3c.dom.Element;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.data.gemfire.util.SpringExtensions;
import org.springframework.data.gemfire.wan.GatewayReceiverFactoryBean;

/**
 * Bean definition parser for the &lt;gfe:gateway-receiver&gt; SDG XML namespace (XSD) element.
 *
 * @author David Turanski
 * @author John Blum
 * @see AbstractSimpleBeanDefinitionParser
 * @see GatewayReceiverFactoryBean
 */
class GatewayReceiverParser extends AbstractSimpleBeanDefinitionParser {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getBeanClass(Element element) {
		return GatewayReceiverFactoryBean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		String cacheRef = element.getAttribute(ParsingUtils.CACHE_REF_ATTRIBUTE_NAME);

		builder.addConstructorArgReference(SpringExtensions.defaultIfEmpty(
			cacheRef, GemfireConstants.DEFAULT_GEMFIRE_CACHE_NAME));

		builder.setLazyInit(false);

		ParsingUtils.setPropertyValue(element, builder, "bind-address");
		ParsingUtils.setPropertyValue(element, builder, "hostname-for-senders");
		ParsingUtils.setPropertyValue(element, builder, "start-port");
		ParsingUtils.setPropertyValue(element, builder, "end-port");
		ParsingUtils.setPropertyValue(element, builder, "manual-start");
		ParsingUtils.setPropertyValue(element, builder, "maximum-time-between-pings");
		ParsingUtils.setPropertyValue(element, builder, "socket-buffer-size");
		ParsingUtils.parseTransportFilters(element, parserContext, builder);
	}
}
