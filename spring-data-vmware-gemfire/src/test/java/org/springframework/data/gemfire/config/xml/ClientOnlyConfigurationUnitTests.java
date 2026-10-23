/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.config.annotation.EnableCachingDefinedRegions;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.config.annotation.support.CacheTypeAwareRegionFactoryBean;
import org.springframework.data.gemfire.transaction.event.TransactionListenerAdapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Regression checks for the retired server configuration entry points. */
public class ClientOnlyConfigurationUnitTests {

	@Test
	public void annotationsExposeClientShortcutsOnly() {

		for (Class<?> annotation : new Class<?>[] {
				EnableCachingDefinedRegions.class, EnableEntityDefinedRegions.class }) {
			assertThat(Arrays.stream(annotation.getDeclaredMethods()).map(Method::getName))
				.doesNotContain("serverRegionShortcut");
		}
	}

	@Test
	public void clientBeansDoNotExposeRetiredServerProperties() {

		assertThat(BeanUtils.getPropertyDescriptor(ClientCacheFactoryBean.class, "transactionWriter")).isNull();
		for (String property : new String[] { "serverRegionShortcut", "scope", "dataPolicy" }) {
			assertThat(BeanUtils.getPropertyDescriptor(CacheTypeAwareRegionFactoryBean.class, property)).isNull();
		}
		assertThat(Arrays.stream(TransactionListenerAdapter.class.getMethods()).map(Method::getName))
			.doesNotContain("beforeCommit");
	}

	@Test
	public void schemaRejectsPeerTemplatesAndTransactionWriters() {

		assertInvalid("<gfe:region-template id=\"peer\"/>");
		assertInvalid("<gfe:client-cache><gfe:transaction-writer ref=\"writer\"/></gfe:client-cache>");
	}

	@Test
	public void schemaRejectsPeerRegionSettings() {

		for (String attribute : new String[] {
				"scope=\"DISTRIBUTED_ACK\"", "ignore-jta=\"true\"", "is-lock-grantor=\"true\"",
				"enable-subscription-conflation=\"true\"", "publisher=\"true\"" }) {
			assertInvalid("<gfe:client-region id=\"region\" " + attribute + "/>");
		}
		for (String element : new String[] { "membership-attributes", "subscription", "partition-attributes" }) {
			assertInvalid("<gfe:client-region id=\"region\"><gfe:" + element + "/></gfe:client-region>");
		}
	}

	@Test
	public void schemaRetainsClientTemplatesCallbacksPersistenceAndSubscriptions() {

		DefaultListableBeanFactory registry = load("""
			<gfe:client-cache pdx-persistent="true">
				<gfe:transaction-listener ref="transactionListener"/>
			</gfe:client-cache>
			<gfe:pool id="clientPool" subscription-enabled="true" subscription-redundancy="1">
				<gfe:server host="localhost" port="40404"/>
			</gfe:pool>
			<gfe:client-region-template id="localTemplate" shortcut="LOCAL_PERSISTENT" statistics="true">
				<gfe:cache-listener ref="listener"/>
				<gfe:entry-ttl timeout="60" action="INVALIDATE"/>
				<gfe:cache-loader ref="loader"/>
				<gfe:cache-writer ref="writer"/>
			</gfe:client-region-template>
			<gfe:client-region id="local" template="localTemplate"/>
			""");

		assertThat(registry.getBeanDefinition("localTemplate").isAbstract()).isTrue();
		assertThat(registry.getBeanDefinition("local").getParentName()).isEqualTo("localTemplate");
		assertThat(registry.getBeanDefinition("clientPool").getPropertyValues().get("subscriptionEnabled"))
			.isEqualTo("true");
	}

	private void assertInvalid(String body) {
		assertThatThrownBy(() -> load(body)).isInstanceOf(BeanDefinitionStoreException.class)
			.hasMessageContaining("invalid");
	}

	private DefaultListableBeanFactory load(String body) {

		String document = """
			<beans xmlns="http://www.springframework.org/schema/beans"
			       xmlns:gfe="http://www.springframework.org/schema/geode"
			       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			       xsi:schemaLocation="http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd
			                           http://www.springframework.org/schema/geode https://www.springframework.org/schema/geode/spring-geode.xsd">
			""" + body + "</beans>";
		DefaultListableBeanFactory registry = new DefaultListableBeanFactory();
		new XmlBeanDefinitionReader(registry).loadBeanDefinitions(
			new ByteArrayResource(document.getBytes(StandardCharsets.UTF_8)));
		return registry;
	}
}
