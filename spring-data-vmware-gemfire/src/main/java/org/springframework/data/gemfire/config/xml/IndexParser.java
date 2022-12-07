/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.xml;

import java.util.concurrent.atomic.AtomicBoolean;

import org.w3c.dom.Element;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.data.gemfire.IndexFactoryBean;
import org.springframework.data.gemfire.config.support.DefinedIndexesApplicationListener;

/**
 * Bean definition parser for &lt;gfe:index&gt; SDG XML namespace (XSD) element.
 *
 * @author Costin Leau
 * @author John Blum
 * @see org.springframework.data.gemfire.IndexFactoryBean
 * @see org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser
 * @since 1.1.0
 */
class IndexParser extends AbstractSimpleBeanDefinitionParser {

	private static final AtomicBoolean REGISTERED = new AtomicBoolean(false);

	/* (non-Javadoc) */
	private static void registerDefinedIndexesApplicationListener(ParserContext parserContext) {

		if (REGISTERED.compareAndSet(false, true)) {

			AbstractBeanDefinition createDefinedIndexesApplicationListener = BeanDefinitionBuilder
				.rootBeanDefinition(DefinedIndexesApplicationListener.class)
				.setRole(BeanDefinition.ROLE_INFRASTRUCTURE)
				.getBeanDefinition();

			BeanDefinitionReaderUtils.registerWithGeneratedName(createDefinedIndexesApplicationListener,
				parserContext.getRegistry());
		}

	}

	/**
	 * {@inheritDoc}
	 */
	protected Class<?> getBeanClass(Element element) {
		return IndexFactoryBean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		super.doParse(element, parserContext, builder);
		registerDefinedIndexesApplicationListener(parserContext);
		ParsingUtils.setPropertyReference(element, builder, "cache-ref", "cache");
	}

	/* (non-Javadoc) */
	@Override
	protected boolean isEligibleAttribute(String attributeName) {
		return (!"cache-ref".equals(attributeName) && super.isEligibleAttribute(attributeName));
	}
}
