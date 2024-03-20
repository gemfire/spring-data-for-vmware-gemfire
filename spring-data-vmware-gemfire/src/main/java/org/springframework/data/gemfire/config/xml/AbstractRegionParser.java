/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.geode.cache.Region;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedArray;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.data.gemfire.PeerRegionFactoryBean;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * Abstract base class encapsulating functionality common to all Region parsers.
 *
 * @author David Turanski
 * @author John Blum
 * @see AbstractSingleBeanDefinitionParser
 * @see PeerRegionFactoryBean
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

	protected void doParseRegionConfiguration(Element element, ParserContext parserContext,
			BeanDefinitionBuilder regionBuilder, BeanDefinitionBuilder regionAttributesBuilder, boolean subRegion) {

		mergeRegionTemplateAttributes(element, parserContext, regionBuilder, regionAttributesBuilder);

		String resolvedCacheReference = ParsingUtils.resolveCacheReference(element.getAttribute("cache-ref"));

		if (!subRegion) {

			regionBuilder.addPropertyReference("cache", resolvedCacheReference);

			ParsingUtils.setPropertyValue(element, regionBuilder, "close");
			ParsingUtils.setPropertyValue(element, regionBuilder, "destroy");
		}

		ParsingUtils.setPropertyValue(element, regionBuilder, "name");
		ParsingUtils.setPropertyValue(element, regionBuilder, "data-policy");
		ParsingUtils.setPropertyValue(element, regionBuilder, "ignore-if-exists", "lookupEnabled");
		ParsingUtils.setPropertyValue(element, regionBuilder, "persistent");
		ParsingUtils.setPropertyValue(element, regionBuilder, "shortcut");

		if (StringUtils.hasText(element.getAttribute("disk-store-ref"))) {
			ParsingUtils.setPropertyValue(element, regionBuilder, "disk-store-ref", "diskStoreName");
			regionBuilder.addDependsOn(element.getAttribute("disk-store-ref"));
		}

		ParsingUtils.parseOptionalRegionAttributes(element, parserContext, regionAttributesBuilder);
		ParsingUtils.parseSubscription(element, parserContext, regionAttributesBuilder);
		ParsingUtils.parseStatistics(element, regionAttributesBuilder);
		ParsingUtils.parseMembershipAttributes(element, parserContext, regionAttributesBuilder);
		ParsingUtils.parseExpiration(element, parserContext, regionAttributesBuilder);
		ParsingUtils.parseEviction(element, parserContext, regionAttributesBuilder);
		ParsingUtils.parseCompressor(element, parserContext, regionAttributesBuilder);

		List<Element> subElements = DomUtils.getChildElements(element);

		for (Element subElement : subElements) {
			if (subElement.getLocalName().equals("cache-listener")) {
				regionBuilder.addPropertyValue("cacheListeners", ParsingUtils.parseRefOrNestedBeanDeclaration(
					subElement, parserContext, regionBuilder));
			}
			else if (subElement.getLocalName().equals("cache-loader")) {
				regionBuilder.addPropertyValue("cacheLoader", ParsingUtils.parseRefOrSingleNestedBeanDeclaration(
					subElement, parserContext, regionBuilder));
			}
			else if (subElement.getLocalName().equals("cache-writer")) {
				regionBuilder.addPropertyValue("cacheWriter", ParsingUtils.parseRefOrSingleNestedBeanDeclaration(
					subElement, parserContext, regionBuilder));
			}
		}

		if (!subRegion) {
			parseSubRegions(element, parserContext, resolvedCacheReference);
		}
	}

	void mergeRegionTemplateAttributes(Element element, ParserContext parserContext,
			BeanDefinitionBuilder regionBuilder, BeanDefinitionBuilder regionAttributesBuilder) {

		String regionTemplateName = getParentName(element);

		if (StringUtils.hasText(regionTemplateName)) {
			if (parserContext.getRegistry().containsBeanDefinition(regionTemplateName)) {

				BeanDefinition templateRegion = parserContext.getRegistry().getBeanDefinition(regionTemplateName);

				BeanDefinition templateRegionAttributes = getRegionAttributesBeanDefinition(templateRegion);

				if (templateRegionAttributes != null) {
					// NOTE we only need to merge the parent's RegionAttributes with this since the parent
					// will have already merged its parent's RegionAttributes and so on...
					regionAttributesBuilder.getRawBeanDefinition().overrideFrom(templateRegionAttributes);
				}
			}
			else {
				parserContext.getReaderContext().error(String.format(
					"The Region template [%1$s] must be defined before the Region [%2$s] referring to the template",
						regionTemplateName, resolveId(element, regionBuilder.getRawBeanDefinition(), parserContext)),
							element);
			}
		}
	}

	BeanDefinition getRegionAttributesBeanDefinition(BeanDefinition region) {

		Assert.notNull(region, "BeanDefinition must not be null");

		Object regionAttributes = null;

		if (region.getPropertyValues().contains("attributes")) {
			regionAttributes =
				Optional.ofNullable(region.getPropertyValues().getPropertyValue("attributes"))
					.map(PropertyValue::getValue)
					.orElse(null);
		}

		return regionAttributes instanceof BeanDefinition ? (BeanDefinition) regionAttributes : null;
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
