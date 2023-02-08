/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.xml;

import java.util.Collections;

import org.w3c.dom.Element;

import org.springframework.aop.config.AopNamespaceUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.data.gemfire.serialization.json.JSONRegionAdvice;
import org.springframework.util.StringUtils;

/**
 * Bean definition parser for the &lt;gfe-data:json-region-auto-proxy&lt; SDG XML namespace (XSD) element.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.springframework.beans.factory.xml.BeanDefinitionParser
 * @see JSONRegionAdvice
 */
class GemfireRegionAutoProxyParser implements BeanDefinitionParser {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		AopNamespaceUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(parserContext, element);

		BeanDefinitionBuilder jsonRegionAdviceBuilder = BeanDefinitionBuilder.rootBeanDefinition(
			JSONRegionAdvice.class).setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

		ParsingUtils.setPropertyValue(element, jsonRegionAdviceBuilder, "pretty-print");
		ParsingUtils.setPropertyValue(element, jsonRegionAdviceBuilder, "convert-returned-collections");

		String regionNames = element.getAttribute("included-regions");

		if (StringUtils.hasText(regionNames)) {
			String[] regions = StringUtils.commaDelimitedListToStringArray(regionNames);
			ManagedList<String> regionList = new ManagedList<String>(regions.length);
			Collections.addAll(regionList, regions);
			jsonRegionAdviceBuilder.addPropertyValue("includedRegions", regionList);
		}

		BeanDefinitionReaderUtils.registerWithGeneratedName(jsonRegionAdviceBuilder.getBeanDefinition(),
			parserContext.getRegistry());

		return jsonRegionAdviceBuilder.getBeanDefinition();
	}
}
