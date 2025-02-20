/*
 * Copyright 2022-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import java.util.List;
import java.util.Map;

import org.apache.geode.internal.datasource.ConfigProperty;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the configuration of a cache JNDI DataSource using property placeholders.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.4.0
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
public class JndiBindingsWithPropertyPlaceholdersNamespaceIntegrationTests extends IntegrationTestsSupport {

	private void assertPropertyValueExists(String expectedPropertyName, String expectedPropertyValue,
			List<ConfigProperty> properties) {

		for (ConfigProperty property : properties) {
			if (expectedPropertyName.equals(property.getName())) {
				Assertions.assertThat(property.getValue()).isEqualTo(expectedPropertyValue);
				Assertions.assertThat(property.getType()).isEqualTo(String.class.getName());
				return;
			}
		}

		Assertions.fail("ConfigProperty with name [%1$s] was not found", expectedPropertyName);
	}

	@Test
	public void cacheJndiContextDataSourceConfigurationIsCorrect() {

		ClientCacheFactoryBean factory = requireApplicationContext().getBean("&gemfireCache", ClientCacheFactoryBean.class);

		List<ClientCacheFactoryBean.JndiDataSource> jndiDataSources = factory.getJndiDataSources();

		Assertions.assertThat(jndiDataSources).isNotNull();
		Assertions.assertThat(jndiDataSources.size()).isEqualTo(1);

		ClientCacheFactoryBean.JndiDataSource dataSource = jndiDataSources.get(0);

		Assertions.assertThat(dataSource).isNotNull();

		Map<String, String> attributes = dataSource.getAttributes();

		Assertions.assertThat(attributes).isNotNull();
		Assertions.assertThat(attributes.isEmpty()).isFalse();
		Assertions.assertThat(attributes.get("jndi-name")).isEqualTo("testDataSource");
		Assertions.assertThat(attributes.get("type")).isEqualTo("XAPooledDataSource");
		Assertions.assertThat(attributes.get("blocking-timeout-seconds")).isEqualTo("60");
		Assertions.assertThat(attributes.get("conn-pooled-datasource-class"))
			.isEqualTo("org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource");
		Assertions.assertThat(attributes.get("connection-url")).isEqualTo("jdbc:derby:testDataStore;create=true");
		Assertions.assertThat(attributes.get("idle-timeout-seconds")).isEqualTo("180");
		Assertions.assertThat(attributes.get("init-pool-size")).isEqualTo("10");
		Assertions.assertThat(attributes.get("jdbc-driver-class")).isEqualTo("org.apache.derby.jdbc.EmbeddedDriver");
		Assertions.assertThat(attributes.get("login-timeout-seconds")).isEqualTo("30");
		Assertions.assertThat(attributes.get("managed-connection-factory-class"))
			.isEqualTo("org.apache.derby.jdbc.NonExistingManagedConnectionFactoryClass");
		Assertions.assertThat(attributes.get("max-pool-size")).isEqualTo("50");
		Assertions.assertThat(attributes.get("password")).isEqualTo("test123");
		Assertions.assertThat(attributes.get("transaction-type")).isEqualTo("XATransaction");
		Assertions.assertThat(attributes.get("user-name")).isEqualTo("masterdba");
		Assertions.assertThat(attributes.get("xa-datasource-class")).isEqualTo("org.apache.derby.jdbc.EmbeddedXADataSource");

		List<ConfigProperty> props = dataSource.getProps();

		Assertions.assertThat(props).isNotNull();
		Assertions.assertThat(props.isEmpty()).isFalse();
		assertPropertyValueExists("schemaName", "testSchema", props);
		assertPropertyValueExists("databaseName", "testDataStore", props);
		assertPropertyValueExists("description", "test", props);
		assertPropertyValueExists("email", "masterdba@xcompany.com", props);
		assertPropertyValueExists("phone", "501-555-1234", props);
	}
}
