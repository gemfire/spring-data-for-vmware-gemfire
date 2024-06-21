/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import org.w3c.dom.Element;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.data.gemfire.PeerRegionFactoryBean;
import org.springframework.data.gemfire.RegionAttributesFactoryBean;

/**
 * Bean definition parser for &lt;gfe:*-region-template&gt; SDG XML namespace (XSD) elements.
 *
 * @author John Blum
 * @see BeanDefinitionBuilder
 * @see ParserContext
 * @see PeerRegionFactoryBean
 * @see RegionAttributesFactoryBean
 * @see AbstractRegionParser
 * @see Element
 * @since 1.5.0
 */
class TemplateRegionParser extends AbstractPeerRegionParser {

	/**
	 * {{@inheritDoc}}
	 */
	@Override
	protected Class<?> getRegionFactoryClass() {
		return PeerRegionFactoryBean.class;
	}

	/**
	 * {{@inheritDoc}}
	 */
	@Override
	protected void doParseRegion(Element element, ParserContext parserContext, BeanDefinitionBuilder builder,
			boolean subRegion) {

		BeanDefinitionBuilder regionAttributesBuilder =
			BeanDefinitionBuilder.genericBeanDefinition(RegionAttributesFactoryBean.class);

		doParseRegionConfiguration(element, parserContext, builder, regionAttributesBuilder, subRegion);

		builder.addPropertyValue("attributes", regionAttributesBuilder.getBeanDefinition());
	}
}
