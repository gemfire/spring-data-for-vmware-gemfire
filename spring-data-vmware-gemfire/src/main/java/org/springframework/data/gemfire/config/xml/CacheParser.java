/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import java.util.List;
import java.util.Optional;

import org.apache.geode.internal.datasource.ConfigProperty;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.gemfire.CacheFactoryBean;
import org.springframework.data.gemfire.config.support.CustomEditorBeanFactoryPostProcessor;
import org.springframework.data.gemfire.config.support.GemfireFeature;
import org.springframework.data.gemfire.config.support.PdxDiskStoreAwareBeanFactoryPostProcessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * Spring {@link BeanDefinitionParser} for the &lt;gfe:cache&gt; SDG XML namespace element.
 *
 * @author Costin Leau
 * @author Oliver Gierke
 * @author David Turanski
 * @author John Blum
 * @author Patrick Johnson
 * @see AbstractBeanDefinition
 * @see BeanDefinitionBuilder
 * @see BeanDefinitionRegistry
 * @see AbstractSingleBeanDefinitionParser
 * @see BeanDefinitionParser
 * @see ParserContext
 * @see CacheFactoryBean
 * @see Element
 */
class CacheParser extends AbstractSingleBeanDefinitionParser {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getBeanClass(Element element) {
		return CacheFactoryBean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder cacheBuilder) {

		super.doParse(element, cacheBuilder);

		registerGemFirePropertyEditorRegistrarWithBeanFactory(getRegistry(parserContext));

		ParsingUtils.setPropertyValue(element, cacheBuilder, "cache-xml-location", "cacheXml");
		ParsingUtils.setPropertyReference(element, cacheBuilder, "properties-ref", "properties");
		ParsingUtils.setPropertyValue(element, cacheBuilder, "use-bean-factory-locator");
		ParsingUtils.setPropertyValue(element, cacheBuilder, "close");
		ParsingUtils.setPropertyValue(element, cacheBuilder, "copy-on-read");
		ParsingUtils.setPropertyValue(element, cacheBuilder, "critical-heap-percentage");
		ParsingUtils.setPropertyValue(element, cacheBuilder, "eviction-heap-percentage");
		ParsingUtils.setPropertyValue(element, cacheBuilder, "enable-auto-reconnect");
		ParsingUtils.setPropertyValue(element, cacheBuilder, "lock-lease");
		ParsingUtils.setPropertyValue(element, cacheBuilder, "lock-timeout");
		ParsingUtils.setPropertyValue(element, cacheBuilder, "message-sync-interval");
		ParsingUtils.setPropertyValue(element, cacheBuilder, "pdx-ignore-unread-fields");
		ParsingUtils.setPropertyValue(element, cacheBuilder, "pdx-read-serialized");
		ParsingUtils.setPropertyValue(element, cacheBuilder, "pdx-persistent");
		ParsingUtils.setPropertyReference(element, cacheBuilder, "pdx-serializer-ref", "pdxSerializer");
		ParsingUtils.setPropertyValue(element, cacheBuilder, "search-timeout");
		ParsingUtils.setPropertyValue(element, cacheBuilder, "use-cluster-configuration");

		parsePdxDiskStore(element, parserContext, cacheBuilder);
		parseJndiBindings(element, parserContext, cacheBuilder);

		List<Element> transactionListeners =
			DomUtils.getChildElementsByTagName(element, "transaction-listener");

		if (!CollectionUtils.isEmpty(transactionListeners)) {

			ManagedList<Object> managedTransactionListeners = new ManagedList<>();

			for (Element transactionListener : transactionListeners) {
				managedTransactionListeners.add(ParsingUtils.parseRefOrNestedBeanDeclaration(
					transactionListener, parserContext, cacheBuilder));
			}

			cacheBuilder.addPropertyValue("transactionListeners", managedTransactionListeners);
		}

		Element transactionWriter = DomUtils.getChildElementByTagName(element, "transaction-writer");

		if (transactionWriter != null) {
			cacheBuilder.addPropertyValue("transactionWriter",
				ParsingUtils.parseRefOrNestedBeanDeclaration(transactionWriter, parserContext, cacheBuilder));
		}
	}

	protected @NonNull BeanDefinitionRegistry getRegistry(@NonNull ParserContext parserContext) {
		return parserContext.getRegistry();
	}

	protected @Nullable BeanFactory resolveBeanFactory(@Nullable BeanDefinitionRegistry registry) {

		return registry instanceof ConfigurableApplicationContext ? ((ConfigurableApplicationContext) registry).getBeanFactory()
			: registry instanceof ApplicationContext ? ((ApplicationContext) registry).getAutowireCapableBeanFactory()
			: registry instanceof ConfigurableListableBeanFactory ? (ConfigurableListableBeanFactory) registry
			: registry instanceof BeanFactory ? (BeanFactory) registry
			: null;
	}

	private void registerGemFirePropertyEditorRegistrarWithBeanFactory(BeanDefinitionRegistry registry) {

		Optional.ofNullable(registry)
			.map(this::resolveBeanFactory)
			.filter(ConfigurableListableBeanFactory.class::isInstance)
			.map(ConfigurableListableBeanFactory.class::cast)
			.ifPresent(beanFactory -> beanFactory.addPropertyEditorRegistrar(new CustomEditorBeanFactoryPostProcessor
				.CustomEditorPropertyEditorRegistrar()));
	}

	private void parsePdxDiskStore(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

		ParsingUtils.setPropertyValue(element, builder, "pdx-disk-store", "pdxDiskStoreName");

		String pdxDiskStoreName = element.getAttribute("pdx-disk-store");

		if (StringUtils.hasText(pdxDiskStoreName)) {
			registerPdxDiskStoreAwareBeanFactoryPostProcessor(getRegistry(parserContext), pdxDiskStoreName);
		}
	}

	private void registerPdxDiskStoreAwareBeanFactoryPostProcessor(BeanDefinitionRegistry registry,
			String pdxDiskStoreName) {

		BeanDefinitionReaderUtils.registerWithGeneratedName(
			createPdxDiskStoreAwareBeanFactoryPostProcessorBeanDefinition(pdxDiskStoreName), registry);
	}

	private AbstractBeanDefinition createPdxDiskStoreAwareBeanFactoryPostProcessorBeanDefinition(
			String pdxDiskStoreName) {

		BeanDefinitionBuilder builder =
			BeanDefinitionBuilder.genericBeanDefinition(PdxDiskStoreAwareBeanFactoryPostProcessor.class);

		builder.addConstructorArgValue(pdxDiskStoreName);

		return builder.getBeanDefinition();
	}

	@SuppressWarnings("unused")
	private void parseJndiBindings(Element element, ParserContext parserContext, BeanDefinitionBuilder cacheBuilder) {

		List<Element> jndiBindings = DomUtils.getChildElementsByTagName(element, "jndi-binding");

		if (!CollectionUtils.isEmpty(jndiBindings)) {

			ManagedList<Object> jndiDataSources = new ManagedList<>(jndiBindings.size());

			for (Element jndiBinding : jndiBindings) {

				BeanDefinitionBuilder jndiDataSource = BeanDefinitionBuilder.genericBeanDefinition(
					CacheFactoryBean.JndiDataSource.class);

				// NOTE 'jndi-name' and 'type' are required by the XSD so we should have at least 2 attributes.
				NamedNodeMap attributes = jndiBinding.getAttributes();

				ManagedMap<String, String> jndiAttributes = new ManagedMap<>(attributes.getLength());

				for (int index = 0, length = attributes.getLength(); index < length; index++) {
					Attr attribute = (Attr) attributes.item(index);
					jndiAttributes.put(attribute.getLocalName(), attribute.getValue());
				}

				jndiDataSource.addPropertyValue("attributes", jndiAttributes);

				List<Element> jndiProps = DomUtils.getChildElementsByTagName(jndiBinding, "jndi-prop");

				if (!CollectionUtils.isEmpty(jndiProps)) {

					ManagedList<Object> props = new ManagedList<>(jndiProps.size());

					for (Element jndiProp : jndiProps) {

						String key = jndiProp.getAttribute("key");
						String type = jndiProp.getAttribute("type");
						String value = jndiProp.getTextContent();

						type = (StringUtils.hasText(type) ? type : String.class.getName());

						props.add(BeanDefinitionBuilder.genericBeanDefinition(ConfigProperty.class)
							.addConstructorArgValue(key)
							.addConstructorArgValue(value)
							.addConstructorArgValue(type)
							.getBeanDefinition());
					}

					jndiDataSource.addPropertyValue("props", props);
				}

				jndiDataSources.add(jndiDataSource.getBeanDefinition());
			}

			cacheBuilder.addPropertyValue("jndiDataSources", jndiDataSources);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext)
			throws BeanDefinitionStoreException {

		String name = Optional.of(super.resolveId(element, definition, parserContext))
			.filter(StringUtils::hasText)
			.map(StringUtils::trimWhitespace)
			.orElse(GemfireConstants.DEFAULT_GEMFIRE_CACHE_NAME);

		if (!"gemfire-cache".equals(name)) {
			parserContext.getRegistry().registerAlias(name, "gemfire-cache");
		}

		return name;
	}
}
