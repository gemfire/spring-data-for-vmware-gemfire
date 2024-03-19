/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.config;

import org.w3c.dom.Element;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;

/**
 * Parser for a &lt;function-executions&gt; bean definition.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.springframework.beans.factory.config.BeanDefinition
 * @see org.springframework.beans.factory.xml.BeanDefinitionParser
 * @see org.springframework.beans.factory.xml.ParserContext
 * @see org.w3c.dom.Element
 */
public class FunctionExecutionBeanDefinitionParser implements BeanDefinitionParser {

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.xml.BeanDefinitionParser#parse(org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		new FunctionExecutionBeanDefinitionRegistrar().registerBeanDefinitions(element, parserContext);
		return null;
	}
}
