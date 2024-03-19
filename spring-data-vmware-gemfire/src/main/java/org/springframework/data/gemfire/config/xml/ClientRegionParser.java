/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import java.util.List;

import org.w3c.dom.Element;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.data.gemfire.RegionAttributesFactoryBean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.client.KeyInterest;
import org.springframework.data.gemfire.client.RegexInterest;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;

/**
 * Bean definition parser for the &lt;gfe:client-region&gt; SDG XML namespace (XSD) element.
 *
 * To avoid eager evaluations, Region interests are declared as nested bean definitions.
 *
 * @author Costin Leau
 * @author David Turanski
 * @author John Blum
 * @see ClientRegionFactoryBean
 * @see AbstractRegionParser
 */
class ClientRegionParser extends AbstractRegionParser {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getRegionFactoryClass() {
		return ClientRegionFactoryBean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doParseRegion(Element element, ParserContext parserContext, BeanDefinitionBuilder regionBuilder,
			boolean subRegion) {

		validateDataPolicyShortcutAttributesMutualExclusion(element, parserContext);

		String resolvedCacheRef = ParsingUtils.resolveCacheReference(element.getAttribute("cache-ref"));

		if (!subRegion) {

			regionBuilder.addPropertyReference("cache", resolvedCacheRef);

			ParsingUtils.setPropertyValue(element, regionBuilder, "close");
			ParsingUtils.setPropertyValue(element, regionBuilder, "destroy");
		}

		ParsingUtils.setPropertyValue(element, regionBuilder, "name");
		ParsingUtils.setPropertyValue(element, regionBuilder, "data-policy");
		ParsingUtils.setPropertyValue(element, regionBuilder, "ignore-if-exists", "lookupEnabled");
		ParsingUtils.setPropertyValue(element, regionBuilder, "persistent");
		ParsingUtils.setPropertyValue(element, regionBuilder, "pool-name");
		ParsingUtils.setPropertyValue(element, regionBuilder, "shortcut");

		parseDiskStoreAttribute(element, regionBuilder);

		// Client RegionAttributes for Compression, Eviction, Expiration and Statistics
		BeanDefinitionBuilder regionAttributesBuilder =
			BeanDefinitionBuilder.genericBeanDefinition(RegionAttributesFactoryBean.class);

		mergeRegionTemplateAttributes(element, parserContext, regionBuilder, regionAttributesBuilder);

		ParsingUtils.parseOptionalRegionAttributes(element, parserContext, regionAttributesBuilder);
		ParsingUtils.parseStatistics(element, regionAttributesBuilder);
		ParsingUtils.parseExpiration(element, parserContext, regionAttributesBuilder);
		ParsingUtils.parseEviction(element, parserContext, regionAttributesBuilder);
		ParsingUtils.parseCompressor(element, parserContext, regionAttributesBuilder);

		regionBuilder.addPropertyValue("attributes", regionAttributesBuilder.getBeanDefinition());

		List<Element> subElements = DomUtils.getChildElements(element);

		ManagedList<Object> interests = new ManagedList<>();

		for (Element subElement : subElements) {

			String subElementLocalName = subElement.getLocalName();

			if ("cache-listener".equals(subElementLocalName)) {
				regionBuilder.addPropertyValue("cacheListeners", ParsingUtils.parseRefOrNestedBeanDeclaration(
					subElement, parserContext, regionBuilder));
			}
			else if ("cache-loader".equals(subElementLocalName)) {
				regionBuilder.addPropertyValue("cacheLoader", ParsingUtils.parseRefOrNestedBeanDeclaration(
					subElement, parserContext, regionBuilder));
			}
			else if ("cache-writer".equals(subElementLocalName)) {
				regionBuilder.addPropertyValue("cacheWriter", ParsingUtils.parseRefOrNestedBeanDeclaration(
					subElement, parserContext, regionBuilder));
			}
			else if ("key-interest".equals(subElementLocalName)) {
				interests.add(parseKeyInterest(subElement, parserContext));
			}
			else if ("regex-interest".equals(subElementLocalName)) {
				interests.add(parseRegexInterest(subElement));
			}
		}

		if (!interests.isEmpty()) {
			regionBuilder.addPropertyValue("interests", interests);
		}

		if (!subRegion) {
			parseSubRegions(element, parserContext, resolvedCacheRef);
		}
	}

	private void parseDiskStoreAttribute(Element element, BeanDefinitionBuilder builder) {

		String diskStoreRefAttribute = element.getAttribute("disk-store-ref");

		if (StringUtils.hasText(diskStoreRefAttribute)) {
			builder.addPropertyValue("diskStoreName", diskStoreRefAttribute);
			builder.addDependsOn(diskStoreRefAttribute);
		}
	}

	private void parseCommonInterestAttributes(Element element, BeanDefinitionBuilder builder) {

		ParsingUtils.setPropertyValue(element, builder, "durable", "durable");
		ParsingUtils.setPropertyValue(element, builder, "receive-values", "receiveValues");
		ParsingUtils.setPropertyValue(element, builder, "result-policy", "policy");
	}

	private Object parseKeyInterest(Element keyInterestElement, ParserContext parserContext) {

		BeanDefinitionBuilder keyInterestBuilder = BeanDefinitionBuilder.genericBeanDefinition(KeyInterest.class);

		keyInterestBuilder.addConstructorArgValue(
			ParsingUtils.parseRefOrNestedBeanDeclaration(keyInterestElement, parserContext, keyInterestBuilder,
				"key-ref"));

		parseCommonInterestAttributes(keyInterestElement, keyInterestBuilder);

		return keyInterestBuilder.getBeanDefinition();
	}

	private Object parseRegexInterest(Element regexInterestElement) {

		BeanDefinitionBuilder regexInterestBuilder = BeanDefinitionBuilder.genericBeanDefinition(RegexInterest.class);

		regexInterestBuilder.addConstructorArgValue(regexInterestElement.getAttribute("pattern"));

		parseCommonInterestAttributes(regexInterestElement, regexInterestBuilder);

		return regexInterestBuilder.getBeanDefinition();
	}
}
