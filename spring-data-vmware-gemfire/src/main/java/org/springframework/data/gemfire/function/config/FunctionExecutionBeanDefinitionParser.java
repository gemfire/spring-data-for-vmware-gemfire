/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;

import org.w3c.dom.Element;

/**
 * Parser for a &lt;function-executions&gt; bean definition.
 *
 * @author David Turanski
 * @author John Blum
 * @see BeanDefinition
 * @see BeanDefinitionParser
 * @see ParserContext
 * @see Element
 */
public class FunctionExecutionBeanDefinitionParser implements BeanDefinitionParser {

	/**
	 * @inheritDoc
	 */
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		new FunctionExecutionBeanDefinitionRegistrar().registerBeanDefinitions(element, parserContext);
		return null;
	}
}
