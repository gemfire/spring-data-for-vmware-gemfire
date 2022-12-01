/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.xml;

import org.w3c.dom.Element;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.data.gemfire.function.config.GemfireFunctionBeanPostProcessor;

/**
 * A Spring {@link BeanDefinitionParser} to enable Spring Data GemFire Function annotation support.
 *
 * Bean definition parser for the &lt;gfe:annotation-driven&gt; SDG XML namespace (XSD) element.
 *
 * @author David Turanski
 * @author John Blum
 * @see BeanDefinitionParser
 * @see GemfireFunctionBeanPostProcessor
 */
class AnnotationDrivenParser implements BeanDefinitionParser {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		registerGemfireFunctionBeanPostProcessor(element, parserContext);
		return null;
	}

	/**
	 * Registers the {@link GemfireFunctionBeanPostProcessor} as a bean with the Spring application context.
	 *
	 * @param element {@link Element} being parsed.
	 * @param parserContext {@link ParserContext} used capture contextual information while parsing.
	 */
	private void registerGemfireFunctionBeanPostProcessor(Element element, ParserContext parserContext) {
		AbstractBeanDefinition gemfireFunctionBeanPostProcessor = BeanDefinitionBuilder
			.rootBeanDefinition(GemfireFunctionBeanPostProcessor.class)
			.setRole(BeanDefinition.ROLE_INFRASTRUCTURE)
			.getBeanDefinition();

		gemfireFunctionBeanPostProcessor.setSource(parserContext.extractSource(element));

		BeanDefinitionReaderUtils.registerWithGeneratedName(gemfireFunctionBeanPostProcessor,
			parserContext.getRegistry());
	}
}
