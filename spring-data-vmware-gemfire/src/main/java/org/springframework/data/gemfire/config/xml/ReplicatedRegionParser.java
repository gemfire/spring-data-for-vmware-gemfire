/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import org.w3c.dom.Element;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.data.gemfire.RegionAttributesFactoryBean;
import org.springframework.data.gemfire.ReplicatedRegionFactoryBean;

/**
 * Bean definition parser for the &lt;gfe:replicated-region&gt; SDG XML namespace (XSD) element.
 *
 * @author Costin Leau
 * @author David Turanski
 * @author John Blum
 * @see BeanDefinitionBuilder
 * @see ParserContext
 * @see ReplicatedRegionFactoryBean
 * @see AbstractPeerRegionParser
 * @see Element
 */
class ReplicatedRegionParser extends AbstractPeerRegionParser {

	/**
	 * {{@inheritDoc}}
	 */
	@Override
	protected Class<?> getRegionFactoryClass() {
		return ReplicatedRegionFactoryBean.class;
	}

	/**
	 * {{@inheritDoc}}
	 */
	@Override
	protected void doParseRegion(Element element, ParserContext parserContext, BeanDefinitionBuilder builder,
			boolean subRegion) {

		validateDataPolicyShortcutAttributesMutualExclusion(element, parserContext);

		ParsingUtils.parseScope(element, builder);

		BeanDefinitionBuilder regionAttributesBuilder =
			BeanDefinitionBuilder.genericBeanDefinition(RegionAttributesFactoryBean.class);

		doParseRegionConfiguration(element, parserContext, builder, regionAttributesBuilder, subRegion);

		builder.addPropertyValue("attributes", regionAttributesBuilder.getBeanDefinition());
	}
}
