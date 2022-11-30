// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.config.xml;

import org.w3c.dom.Element;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.data.gemfire.function.FunctionServiceFactoryBean;
import org.springframework.data.gemfire.util.SpringExtensions;
import org.springframework.util.xml.DomUtils;

/**
 * Bean definition parser for the &lt;gfe:function-service&gt; SDG XML namespace (XSD) element.
 *
 * @author David Turanski
 * @author John Blum
 * @see AbstractSimpleBeanDefinitionParser
 * @see FunctionServiceFactoryBean
 */
class FunctionServiceParser extends AbstractSimpleBeanDefinitionParser {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getBeanClass(Element element) {
		return FunctionServiceFactoryBean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		super.doParse(element, builder);

		builder.setLazyInit(false);

		Element functionElement = DomUtils.getChildElementByTagName(element, "function");

		if (functionElement != null) {
			builder.addPropertyValue("functions", ParsingUtils.parseRefOrNestedBeanDeclaration(
				functionElement, parserContext, builder));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String resolveId(Element element, AbstractBeanDefinition beanDefinition, ParserContext parserContext)
			throws BeanDefinitionStoreException {

		String resolvedId = super.resolveId(element, beanDefinition, parserContext);

		return SpringExtensions.defaultIfEmpty(resolvedId, GemfireConstants.DEFAULT_GEMFIRE_FUNCTION_SERVICE_NAME);
	}
}
