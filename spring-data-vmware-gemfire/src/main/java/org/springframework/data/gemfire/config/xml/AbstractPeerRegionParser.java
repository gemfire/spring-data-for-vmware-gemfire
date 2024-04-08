/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import org.apache.geode.cache.Region;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;

import org.w3c.dom.Element;

/**
 * Abstract Spring XML parser for peer {@link Region} bean definitions.
 *
 * @author John Blum
 * @see Region
 * @see BeanDefinitionBuilder
 * @see ParserContext
 * @see AbstractRegionParser
 * @see Element
 * @since 2.2.0
 */
public abstract class AbstractPeerRegionParser extends AbstractRegionParser {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doParseRegionConfiguration(Element element, ParserContext parserContext,
			BeanDefinitionBuilder regionBuilder, BeanDefinitionBuilder regionAttributesBuilder, boolean subRegion) {

		super.doParseRegionConfiguration(element, parserContext, regionBuilder, regionAttributesBuilder, subRegion);
	}
}
