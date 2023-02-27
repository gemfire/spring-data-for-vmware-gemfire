/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.xml;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import org.w3c.dom.Element;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.data.gemfire.search.lucene.LuceneIndexFactoryBean;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;

/**
 * Spring XML {@link AbstractSingleBeanDefinitionParser parser} for the {@link LuceneIndexFactoryBean} bean definition.
 *
 * @author John Blum
 * @see BeanDefinitionBuilder
 * @see AbstractSingleBeanDefinitionParser
 * @see LuceneIndexFactoryBean
 * @since 1.1.0
 * @deprecated To be removed in GemFire 10 integration
 */
class LuceneIndexParser extends AbstractSingleBeanDefinitionParser {

	/**
	 * @inheritDoc
	 */
	@Override
	protected Class<?> getBeanClass(Element element) {
		return LuceneIndexFactoryBean.class;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

		super.doParse(element, parserContext, builder);

		ParsingUtils.setCacheReference(element, builder);
		ParsingUtils.setPropertyValue(element, builder, "destroy");
		ParsingUtils.setPropertyReference(element, builder, "lucene-service-ref", "luceneService");
		ParsingUtils.setPropertyValue(element, builder, "name", "indexName");
		ParsingUtils.setPropertyReference(element, builder, "region-ref", "region");
		ParsingUtils.setPropertyValue(element, builder, "region-path");

		Optional.ofNullable(element.getAttribute("fields"))
			.filter(StringUtils::hasText)
			.ifPresent(fields -> builder.addPropertyValue("fields",
				Arrays.stream(fields.split(",")).map(String::trim).collect(Collectors.toList())));

		Optional.ofNullable(DomUtils.getChildElementByTagName(element, "field-analyzers"))
			.ifPresent(fieldAnalyzersElement -> builder.addPropertyValue("fieldAnalyzers",
				ParsingUtils.parseRefOrSingleNestedBeanDeclaration(fieldAnalyzersElement, parserContext, builder)));

		Optional.ofNullable(DomUtils.getChildElementByTagName(element, "lucene-serializer"))
			.ifPresent(luceneSerializerElement -> builder.addPropertyValue("luceneSerializer",
				ParsingUtils.parseRefOrSingleNestedBeanDeclaration(luceneSerializerElement, parserContext, builder)));
	}
}
