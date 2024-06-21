/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.xml;

import static org.springframework.data.gemfire.util.CollectionUtils.nullSafeList;

import org.w3c.dom.Element;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.data.gemfire.snapshot.SnapshotServiceFactoryBean;
import org.springframework.util.xml.DomUtils;

/**
 * Bean definition parser for the &lt;gfe-data:snapshot-service&gt; SDG XML namespace (XSD) element.
 *
 * @author John Blum
 * @see AbstractSingleBeanDefinitionParser
 * @see SnapshotServiceFactoryBean
 * @since 1.7.0
 */
class SnapshotServiceParser extends AbstractSingleBeanDefinitionParser {

	/**
	 * {{@inheritDoc}}
	 */
	@Override
	protected Class<?> getBeanClass(final Element element) {
		return SnapshotServiceFactoryBean.class;
	}

	/**
	 * {{@inheritDoc}}
	 */
	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		super.doParse(element, parserContext, builder);

		ParsingUtils.setCacheReference(element, builder);
		ParsingUtils.setRegionReference(element, builder);
		ParsingUtils.setPropertyValue(element, builder, "suppress-import-on-init");
		builder.addPropertyValue("exports", parseExports(element, parserContext));
		builder.addPropertyValue("imports", parseImports(element, parserContext));
	}

	/* (non-Javadoc) */
	private ManagedList<BeanDefinition> parseExports(Element element, ParserContext parserContext) {
		return parseSnapshots(element, parserContext, "snapshot-export");
	}

	/* (non-Javadoc) */
	private ManagedList<BeanDefinition> parseImports(Element element, ParserContext parserContext) {
		return parseSnapshots(element, parserContext, "snapshot-import");
	}

	/* (non-Javadoc) */
	private ManagedList<BeanDefinition> parseSnapshots(Element element, ParserContext parserContext,
			String childTagName) {

		ManagedList<BeanDefinition> snapshotBeans = new ManagedList<>();

		nullSafeList(DomUtils.getChildElementsByTagName(element, childTagName)).forEach(childElement ->
			snapshotBeans.add(parseSnapshotMetadata(childElement, parserContext)));

		return snapshotBeans;
	}

	/* (non-Javadoc) */
	private BeanDefinition parseSnapshotMetadata(Element snapshotMetadataElement, ParserContext parserContext) {

		BeanDefinitionBuilder snapshotMetadataBuilder =
			BeanDefinitionBuilder.genericBeanDefinition(SnapshotServiceFactoryBean.SnapshotMetadata.class);

		snapshotMetadataBuilder.addConstructorArgValue(snapshotMetadataElement.getAttribute("location"));

		snapshotMetadataBuilder.addConstructorArgValue(snapshotMetadataElement.getAttribute("format"));

		if (isSnapshotFilterSpecified(snapshotMetadataElement)) {
			snapshotMetadataBuilder.addConstructorArgValue(ParsingUtils.parseRefOrNestedBeanDeclaration(
				snapshotMetadataElement, parserContext, snapshotMetadataBuilder, "filter-ref",
					true));
		}

		ParsingUtils.setPropertyValue(snapshotMetadataElement, snapshotMetadataBuilder, "invokeCallbacks");
		ParsingUtils.setPropertyValue(snapshotMetadataElement, snapshotMetadataBuilder, "parallel");

		return snapshotMetadataBuilder.getBeanDefinition();
	}

	/* (non-Javadoc) */
	private boolean isSnapshotFilterSpecified(final Element snapshotMetadataElement) {
		return (snapshotMetadataElement.hasAttribute("filter-ref") || snapshotMetadataElement.hasChildNodes());
	}
}
