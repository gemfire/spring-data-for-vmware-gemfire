/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.xml;

import org.w3c.dom.Element;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.data.gemfire.search.lucene.LuceneServiceFactoryBean;

/**
 * Spring XML {@link AbstractSingleBeanDefinitionParser parser} for a {@link LuceneServiceFactoryBean} bean definition.
 *
 * @author John Blum
 * @see BeanDefinitionBuilder
 * @see AbstractSingleBeanDefinitionParser
 * @see LuceneServiceFactoryBean
 * @since 1.1.0
 */
class LuceneServiceParser extends AbstractSingleBeanDefinitionParser {

	/**
	 * @inheritDoc
	 */
	@Override
	protected Class<?> getBeanClass(Element element) {
		return LuceneServiceFactoryBean.class;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	protected void doParse(Element element, BeanDefinitionBuilder builder) {
		super.doParse(element, builder);
		ParsingUtils.setCacheReference(element, builder);
	}
}
