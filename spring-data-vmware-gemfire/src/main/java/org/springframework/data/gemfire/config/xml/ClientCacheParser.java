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
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.config.support.CustomEditorBeanFactoryPostProcessor;
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
 * {@link BeanDefinitionParser} for the &lt;gfe:client-cache&gt; SDG XML Namespace (XSD) element.
 *
 * @author Costin Leau
 * @author David Turanski
 * @author Lyndon Adams
 * @author John Blum
 * @author Patrick Johnson
 * @see Element
 * @see BeanDefinitionBuilder
 * @see BeanDefinitionParser
 * @see ParserContext
 * @see ClientCacheFactoryBean
 */
class ClientCacheParser extends AbstractSingleBeanDefinitionParser {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getBeanClass(Element element) {
		return ClientCacheFactoryBean.class;
	}

	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder clientCacheBuilder) {

		super.doParse(element, clientCacheBuilder);

		registerGemFirePropertyEditorRegistrarWithBeanFactory(getRegistry(parserContext));

		ParsingUtils.setPropertyValue(element, clientCacheBuilder, "cache-xml-location", "cacheXml");
		ParsingUtils.setPropertyReference(element, clientCacheBuilder, "properties-ref", "properties");
		ParsingUtils.setPropertyValue(element, clientCacheBuilder, "use-bean-factory-locator");
		ParsingUtils.setPropertyValue(element, clientCacheBuilder, "close");
		ParsingUtils.setPropertyValue(element, clientCacheBuilder, "copy-on-read");
		ParsingUtils.setPropertyValue(element, clientCacheBuilder, "critical-heap-percentage");
		ParsingUtils.setPropertyValue(element, clientCacheBuilder, "critical-off-heap-percentage");
		ParsingUtils.setPropertyValue(element, clientCacheBuilder, "eviction-heap-percentage");
		ParsingUtils.setPropertyValue(element, clientCacheBuilder, "eviction-off-heap-percentage");
		ParsingUtils.setPropertyValue(element, clientCacheBuilder, "lock-lease");
		ParsingUtils.setPropertyValue(element, clientCacheBuilder, "lock-timeout");
		ParsingUtils.setPropertyValue(element, clientCacheBuilder, "message-sync-interval");
		ParsingUtils.setPropertyValue(element, clientCacheBuilder, "pdx-ignore-unread-fields");
		ParsingUtils.setPropertyValue(element, clientCacheBuilder, "pdx-read-serialized");
		ParsingUtils.setPropertyValue(element, clientCacheBuilder, "pdx-persistent");
		ParsingUtils.setPropertyReference(element, clientCacheBuilder, "pdx-serializer-ref", "pdxSerializer");
		ParsingUtils.setPropertyValue(element, clientCacheBuilder, "search-timeout");

		parsePdxDiskStore(element, parserContext, clientCacheBuilder);
		parseJndiBindings(element, parserContext, clientCacheBuilder);

		List<Element> transactionListeners =
				DomUtils.getChildElementsByTagName(element, "transaction-listener");

		if (!CollectionUtils.isEmpty(transactionListeners)) {

			ManagedList<Object> managedTransactionListeners = new ManagedList<>();

			for (Element transactionListener : transactionListeners) {
				managedTransactionListeners.add(ParsingUtils.parseRefOrNestedBeanDeclaration(
						transactionListener, parserContext, clientCacheBuilder));
			}

			clientCacheBuilder.addPropertyValue("transactionListeners", managedTransactionListeners);
		}

		Element transactionWriter = DomUtils.getChildElementByTagName(element, "transaction-writer");

		if (transactionWriter != null) {
			clientCacheBuilder.addPropertyValue("transactionWriter",
					ParsingUtils.parseRefOrNestedBeanDeclaration(transactionWriter, parserContext, clientCacheBuilder));
		}

		ParsingUtils.setPropertyValue(element, clientCacheBuilder, "durable-client-id");
		ParsingUtils.setPropertyValue(element, clientCacheBuilder, "durable-client-timeout");
		ParsingUtils.setPropertyValue(element, clientCacheBuilder, "keep-alive");
		ParsingUtils.setPropertyValue(element, clientCacheBuilder, "pool-name");
		ParsingUtils.setPropertyValue(element, clientCacheBuilder, "ready-for-events");
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
						ClientCacheFactoryBean.JndiDataSource.class);

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
