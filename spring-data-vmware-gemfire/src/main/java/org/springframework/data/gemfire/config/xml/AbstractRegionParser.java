/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.geode.cache.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedArray;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Abstract base class encapsulating functionality common to all Region parsers.
 *
 * @author David Turanski
 * @author John Blum
 * @see AbstractSingleBeanDefinitionParser
 */
abstract class AbstractRegionParser extends AbstractSingleBeanDefinitionParser {

	protected static final String REGION_DEFINITION_SUFFIX = "region";
	protected static final String REGION_TEMPLATE_SUFFIX = "-template";
	protected static final String TEMPLATE_ATTRIBUTE = "template";

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getBeanClass(Element element) {
		return getRegionFactoryClass();
	}

	/**
	 * Return the {@link Class type} of the {@link Region} {@link FactoryBean}.
	 *
	 * @return the {@link Class type} of the {@link Region} {@link FactoryBean}.
	 * @see FactoryBean
	 * @see Class
	 */
	protected abstract Class<?> getRegionFactoryClass();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getParentName(Element element) {

		String regionTemplate = element.getAttribute(TEMPLATE_ATTRIBUTE);

		return StringUtils.hasText(regionTemplate) ? regionTemplate : super.getParentName(element);
	}

	/**
	 * Determines whether the given SDG XML namespace configuration {@link Element} defines a {@link Region} template
	 * used as the base configuration for one or more {@link Region Regions}.
	 *
	 * @param element SDG XML namespace {@link Element}.
	 * @return a boolean value indicating whether the given SDG XML namespace configuration {@link Element}
	 * defines a {@link Region} template.
	 * @see Element
	 */
	protected boolean isRegionTemplate(@NonNull Element element) {

		String localName = element.getLocalName();

		return localName != null && localName.endsWith(REGION_TEMPLATE_SUFFIX);
	}

	/**
	 * Determines whether the current SDG XML namespace {@link Region} {@link Element} is a {@link Region Sub-Region}
	 * definition.
	 *
	 * @param element SDG XML namespace {@link Region} {@link Element} to evaluate as a {@link Region Sub-Region}.
	 * @return a boolean value indicating whether the current SDG XML namespace {@link Region} {@link Element}
	 * is a {@link Region Sub-Region} definition.
	 * @see Element
	 */
	protected boolean isSubRegion(@NonNull Element element) {

		String localName = element.getParentNode().getLocalName();

		return localName != null && localName.endsWith(REGION_DEFINITION_SUFFIX);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

		super.doParse(element, parserContext, builder);

		builder.setAbstract(isRegionTemplate(element));

        doParseRegion(element, parserContext, builder, isSubRegion(element));
	}

	protected abstract void doParseRegion(Element element, ParserContext parserContext,
			BeanDefinitionBuilder builder, boolean subRegion);

	void validateRegionTemplate(Element element, ParserContext parserContext,
			BeanDefinitionBuilder regionBuilder) {

		String regionTemplateName = getParentName(element);

		// Client settings inherit through the parent bean definition, including chained templates.
		if (StringUtils.hasText(regionTemplateName)
				&& !parserContext.getRegistry().containsBeanDefinition(regionTemplateName)) {
			parserContext.getReaderContext().error(String.format(
				"The Region template [%1$s] must be defined before the Region [%2$s] referring to the template",
					regionTemplateName, resolveId(element, regionBuilder.getRawBeanDefinition(), parserContext)),
						element);
		}
	}

	protected void parseCollectionOfCustomSubElements(Element element, ParserContext parserContext,
			BeanDefinitionBuilder builder, String className, String subElementName, String propertyName) {

		List<Element> subElements =
			DomUtils.getChildElementsByTagName(element, subElementName, subElementName + "-ref");

		if (!CollectionUtils.isEmpty(subElements)) {

			ManagedArray array = new ManagedArray(className, subElements.size());

			for (Element subElement : subElements) {
				array.add(ParsingUtils.parseRefOrNestedCustomElement(subElement, parserContext, builder));
			}

			builder.addPropertyValue(propertyName, array);
		}
	}

	protected void parseSubRegions(Element element, ParserContext parserContext, String resolvedCacheRef) {

		Map<String, Element> allSubRegionElements = new HashMap<>();

		findSubRegionElements(element, getRegionNameFromElement(element), allSubRegionElements);

		if (!CollectionUtils.isEmpty(allSubRegionElements)) {
			for (Map.Entry<String, Element> entry : allSubRegionElements.entrySet()) {
				parseSubRegion(entry.getValue(), parserContext, entry.getKey(), resolvedCacheRef);
			}
		}
	}

	private void findSubRegionElements(Element parent, String parentPath, Map<String, Element> allSubRegionElements) {

		for (Element element : DomUtils.getChildElements(parent)) {
			if (element.getLocalName().endsWith("region")) {

				String subRegionName = getRegionNameFromElement(element);
				String subRegionPath = buildSubRegionPath(parentPath, subRegionName);

				allSubRegionElements.put(subRegionPath, element);

				findSubRegionElements(element, subRegionPath, allSubRegionElements);
			}
		}
	}

	private String getRegionNameFromElement(Element element) {

		String name = element.getAttribute(NAME_ATTRIBUTE);

		return StringUtils.hasText(name)
			? name
			: element.getAttribute(ID_ATTRIBUTE);
	}

	private String buildSubRegionPath(String parentName, String regionName) {

		String regionPath = StringUtils.arrayToDelimitedString(new String[] { parentName, regionName }, "/");

		if (!regionPath.startsWith(Region.SEPARATOR)) {
			regionPath = Region.SEPARATOR + regionPath;
		}

		return regionPath;
	}

	@SuppressWarnings("all")
	private BeanDefinition parseSubRegion(Element element, ParserContext parserContext, String subRegionPath,
			String cacheRef) {

		String parentBeanName = getParentRegionPathFrom(subRegionPath);
		String regionName = getRegionNameFromElement(element); // do before 'renaming' the element below

		element.setAttribute("id", subRegionPath);
		element.setAttribute("name", subRegionPath);

		BeanDefinition beanDefinition = parserContext.getDelegate().parseCustomElement(element);

		MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();

		propertyValues.add("cache", new RuntimeBeanReference(cacheRef));
		propertyValues.add("parent", new RuntimeBeanReference(parentBeanName));
		propertyValues.add("regionName", regionName);

		return beanDefinition;
	}

	private String getParentRegionPathFrom(String regionPath) {

		int index = regionPath.lastIndexOf(Region.SEPARATOR);

		String parentPath = regionPath.substring(0, index);

		if (parentPath.lastIndexOf(Region.SEPARATOR) == 0) {
			parentPath = parentPath.substring(1);
		}

		return parentPath;
	}

	protected void validateDataPolicyShortcutAttributesMutualExclusion(Element element, ParserContext parserContext) {

		if (element.hasAttribute("data-policy") && element.hasAttribute("shortcut")) {

			String message = String.format("Only one of [data-policy, shortcut] may be specified with element [%s]",
				element.getTagName());

			parserContext.getReaderContext().error(message, element);
		}
	}
}
