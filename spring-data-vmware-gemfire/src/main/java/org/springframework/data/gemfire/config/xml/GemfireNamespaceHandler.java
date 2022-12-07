/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
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
 * @see org.springframework.beans.factory.xml.NamespaceHandlerSupport
 */
@SuppressWarnings("unused")
class GemfireNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {

		registerBeanDefinitionParser("annotation-driven", new AnnotationDrivenParser());
		registerBeanDefinitionParser("async-event-queue", new AsyncEventQueueParser());
		registerBeanDefinitionParser("auto-region-lookup", new AutoRegionLookupParser());
		registerBeanDefinitionParser("cache", new CacheParser());
		registerBeanDefinitionParser("cache-server", new CacheServerParser());
		registerBeanDefinitionParser("client-cache", new ClientCacheParser());
		registerBeanDefinitionParser("client-region", new ClientRegionParser());
		registerBeanDefinitionParser("client-region-template", new ClientRegionParser());
		registerBeanDefinitionParser("cq-listener-container", new GemfireListenerContainerParser());
		registerBeanDefinitionParser("disk-store", new DiskStoreParser());
		registerBeanDefinitionParser("function-service", new FunctionServiceParser());
		registerBeanDefinitionParser("gateway-receiver", new GatewayReceiverParser());
		registerBeanDefinitionParser("gateway-sender", new GatewaySenderParser());
		registerBeanDefinitionParser("index", new IndexParser());
		registerBeanDefinitionParser("local-region", new LocalRegionParser());
		registerBeanDefinitionParser("local-region-template", new LocalRegionParser());
		registerBeanDefinitionParser("lookup-region", new LookupRegionParser());
		registerBeanDefinitionParser("lucene-index", new LuceneIndexParser());
		registerBeanDefinitionParser("lucene-service", new LuceneServiceParser());
		registerBeanDefinitionParser("partitioned-region", new PartitionedRegionParser());
		registerBeanDefinitionParser("partitioned-region-template", new PartitionedRegionParser());
		registerBeanDefinitionParser("pool", new PoolParser());
		registerBeanDefinitionParser("region-template", new TemplateRegionParser());
		registerBeanDefinitionParser("replicated-region", new ReplicatedRegionParser());
		registerBeanDefinitionParser("replicated-region-template", new ReplicatedRegionParser());
		registerBeanDefinitionParser("transaction-manager", new TransactionManagerParser());
	}
}
