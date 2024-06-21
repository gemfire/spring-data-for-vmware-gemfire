/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.data.gemfire.function.config.FunctionExecutionBeanDefinitionParser;
import org.springframework.data.gemfire.repository.config.GemfireRepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryBeanDefinitionParser;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

/**
 * Spring {@link org.springframework.beans.factory.xml.NamespaceHandler} for Spring Data GemFire
 * XML namespace (XSD) bean definitions.
 *
 * @author Costin Leau
 * @author David Turanski
 * @author Oliver Gierke
 * @author John Blum
 */
@SuppressWarnings("unused")
class GemfireDataNamespaceHandler extends NamespaceHandlerSupport {

	/**
	 * {{@inheritDoc}}
	 */
	@Override
	public void init() {

		RepositoryConfigurationExtension extension = new GemfireRepositoryConfigurationExtension();

		registerBeanDefinitionParser("datasource", new GemfireDataSourceParser());
		registerBeanDefinitionParser("function-executions", new FunctionExecutionBeanDefinitionParser());
		registerBeanDefinitionParser("json-region-autoproxy", new GemfireRegionAutoProxyParser());
		registerBeanDefinitionParser("repositories", new RepositoryBeanDefinitionParser(extension));
		registerBeanDefinitionParser("snapshot-service", new SnapshotServiceParser());
	}
}
