/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;

import org.w3c.dom.Element;

/**
 * {@link BeanDefinitionParser} for the &lt;gfe:client-cache&gt; SDG XML Namespace (XSD) element.
 *
 * @author Costin Leau
 * @author David Turanski
 * @author Lyndon Adams
 * @author John Blum
 * @author Patrick Johnson
 * @see Element
 * @see BeanDefinitionBuilder
 * @see BeanDefinitionParser
 * @see ParserContext
 * @see ClientCacheFactoryBean
 * @see CacheParser
 */
class ClientCacheParser extends CacheParser {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getBeanClass(Element element) {
		return ClientCacheFactoryBean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder clientCacheBuilder) {

		super.doParse(element, parserContext, clientCacheBuilder);

		ParsingUtils.setPropertyValue(element, clientCacheBuilder, "durable-client-id");
		ParsingUtils.setPropertyValue(element, clientCacheBuilder, "durable-client-timeout");
		ParsingUtils.setPropertyValue(element, clientCacheBuilder, "keep-alive");
		ParsingUtils.setPropertyValue(element, clientCacheBuilder, "pool-name");
		ParsingUtils.setPropertyValue(element, clientCacheBuilder, "ready-for-events");
	}
}
