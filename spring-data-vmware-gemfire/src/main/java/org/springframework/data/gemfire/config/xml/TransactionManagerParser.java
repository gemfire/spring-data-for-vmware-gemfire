/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.xml;

import org.w3c.dom.Element;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.data.gemfire.transaction.GemfireTransactionManager;
import org.springframework.util.StringUtils;

/**
 * Bean definition parser for the &lt;gfe:transaction-manager&gt; SDG XML namespace (XSD) element.
 *
 * @author Costin Leau
 * @author John Blum
 * @see AbstractSingleBeanDefinitionParser
 * @see GemfireTransactionManager
 */
class TransactionManagerParser extends AbstractSingleBeanDefinitionParser {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getBeanClass(Element element) {
		return GemfireTransactionManager.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doParse(Element element, BeanDefinitionBuilder builder) {
		super.doParse(element, builder);

		ParsingUtils.setPropertyValue(element, builder, "copy-on-read", "copyOnRead");
		builder.addPropertyReference("cache", ParsingUtils.resolveCacheReference(element));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext)
			throws BeanDefinitionStoreException {

		String name = super.resolveId(element, definition, parserContext);

		if (!StringUtils.hasText(name)) {
			name = GemfireConstants.DEFAULT_GEMFIRE_TRANSACTION_MANAGER_NAME;
			//For backward compatibility
			parserContext.getRegistry().registerAlias(GemfireConstants.DEFAULT_GEMFIRE_TRANSACTION_MANAGER_NAME,
				"gemfire-transaction-manager");
		}

		return name;
	}
}
