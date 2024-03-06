/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Spring {@link org.springframework.beans.factory.xml.NamespaceHandler} for Spring GemFire
 * XML namespace (XSD) bean definitions.
 *
 * @author Costin Leau
 * @author David Turanski
 * @author John Blum
 * @see NamespaceHandlerSupport
 */
@SuppressWarnings("unused")
class GemfireNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {

		registerBeanDefinitionParser("auto-region-lookup", new AutoRegionLookupParser());
		registerBeanDefinitionParser("cache", new CacheParser());
		registerBeanDefinitionParser("client-cache", new ClientCacheParser());
		registerBeanDefinitionParser("client-region", new ClientRegionParser());
		registerBeanDefinitionParser("client-region-template", new ClientRegionParser());
		registerBeanDefinitionParser("cq-listener-container", new GemfireListenerContainerParser());
		registerBeanDefinitionParser("function-service", new FunctionServiceParser());
		registerBeanDefinitionParser("index", new IndexParser());
		registerBeanDefinitionParser("lookup-region", new LookupRegionParser());
		registerBeanDefinitionParser("pool", new PoolParser());
		registerBeanDefinitionParser("transaction-manager", new TransactionManagerParser());
	}
}