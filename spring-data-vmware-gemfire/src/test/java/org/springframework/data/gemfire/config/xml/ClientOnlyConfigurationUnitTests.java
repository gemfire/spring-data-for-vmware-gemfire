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
import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.config.annotation.EnableCachingDefinedRegions;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.config.annotation.EnableGemFireProperties;
import org.springframework.data.gemfire.config.annotation.EnableSecurity;
import org.springframework.data.gemfire.config.annotation.support.CacheTypeAwareRegionFactoryBean;
import org.springframework.data.gemfire.config.support.GemfireFeature;
import org.springframework.data.gemfire.transaction.event.TransactionListenerAdapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Regression checks for the retired server configuration entry points. */
public class ClientOnlyConfigurationUnitTests {

	@Test
	public void featureCatalogRetainsClientCapabilitiesWithoutServerOperations() {

		assertThat(Arrays.stream(GemfireFeature.values()).map(Enum::name))
			.contains("CLIENT_SERVER", "CACHING", "CONTINUOUS_QUERY", "FUNCTIONS",
				"PERSISTENCE", "REGISTER_INTEREST", "TRANSACTIONS")
			.doesNotContain("BACKUP", "NETWORK_PARTITIONING", "PEER_TO_PEER", "REPLICATION");
	}

	@Test
	public void annotationsExposeClientShortcutsOnly() {

		for (Class<?> annotation : new Class<?>[] {
				EnableCachingDefinedRegions.class, EnableEntityDefinedRegions.class }) {
			assertThat(Arrays.stream(annotation.getDeclaredMethods()).map(Method::getName))
				.doesNotContain("serverRegionShortcut");
		}
	}

	@Test
	public void propertiesAnnotationRetainsClientSettingsWithoutServerAdministration() {

		assertThat(Arrays.stream(EnableGemFireProperties.class.getDeclaredMethods()).map(Method::getName))
			.contains("cacheXmlFile", "conflateEvents", "serializableObjectFilter", "validateSerializableObjects",
				"memberTimeout", "deltaPropagation", "lockMemory", "distributedSystemId", "groups", "name")
			.doesNotContain("bindAddress", "deployWorkingDirectory", "clusterConfigurationDirectory", "loadClusterConfigurationFromDirectory",
				"remoteLocators", "removeUnresponsiveClient", "userCommandPackages",
				"conserveSockets", "socketLeaseTime", "socketBufferSize", "tombstoneGcThreshold",
				"ackSevereAlertThreshold", "ackWaitThreshold", "disableTcp", "tcpPort",
				"udpFragmentSize", "udpReceiveBufferSize", "udpSendBufferSize",
				"enableNetworkPartitionDetection", "locatorWaitTimeout", "maxWaitTimeReconnect", "membershipPortRange");
	}

	@Test
	public void securityAnnotationRetainsOnlyClientAuthentication() {

		assertThat(Arrays.stream(EnableSecurity.class.getDeclaredMethods()).map(Method::getName))
			.contains("clientAuthenticationInitializer", "securityUsername", "securityPassword")
			.doesNotContain("peerAuthenticationInitializer", "shiroIniResourcePath",
				"securityManagerClass", "securityManagerClassName",
				"securityPostProcessorClass", "securityPostProcessorClassName");
	}

	@Test
	public void clientBeansDoNotExposeRetiredServerProperties() {

		assertThat(BeanUtils.getPropertyDescriptor(ClientCacheFactoryBean.class, "transactionWriter")).isNull();
		for (String property : new String[] { "serverRegionShortcut", "scope", "dataPolicy" }) {
			assertThat(BeanUtils.getPropertyDescriptor(CacheTypeAwareRegionFactoryBean.class, property)).isNull();
		}
		assertThat(Arrays.stream(TransactionListenerAdapter.class.getMethods()).map(Method::getName))
			.doesNotContain("beforeCommit");
		assertThat(Arrays.stream(GemfireUtils.class.getMethods()).map(Method::getName))
			.contains("getDistributedSystem", "isConnected", "configureDurableClient")
			.doesNotContain("getLocator");
	}

	@Test
	public void schemaRejectsPeerTemplatesAndTransactionWriters() {

		assertInvalid("<gfe:region-template id=\"peer\"/>");
		assertInvalid("<gfe:client-cache><gfe:transaction-writer ref=\"writer\"/></gfe:client-cache>");
	}

	@Test
	public void schemaRejectsFunctionHosting() {

		assertInvalid("<gfe:function-service><gfe:function ref=\"serverFunction\"/></gfe:function-service>");
	}

	@Test
	public void namespaceRejectsFunctionHostingWithoutSchemaValidation() {

		assertThatThrownBy(() -> load("<gfe:function-service/>", false))
			.isInstanceOf(BeanDefinitionStoreException.class)
			.hasMessageContaining("Cannot locate BeanDefinitionParser for element [function-service]");
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
			<gfe:pool id="clientPool" subscription-enabled="true" subscription-redundancy="1"
			          idle-timeout="120000" min-connections="2" max-connections="8"
			          read-timeout="30000" socket-connect-timeout="5000" subscription-ack-interval="500"
			          retry-attempts="3" ping-interval="15000">
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
		assertThat(registry.getBeanDefinition("clientPool").getPropertyValues().get("idleTimeout"))
			.isEqualTo("120000");
		assertThat(registry.getBeanDefinition("clientPool").getPropertyValues().get("minConnections"))
			.isEqualTo("2");
		assertThat(registry.getBeanDefinition("clientPool").getPropertyValues().get("maxConnections"))
			.isEqualTo("8");
		assertThat(registry.getBeanDefinition("clientPool").getPropertyValues().get("readTimeout"))
			.isEqualTo("30000");
		assertThat(registry.getBeanDefinition("clientPool").getPropertyValues().get("socketConnectTimeout"))
			.isEqualTo("5000");
		assertThat(registry.getBeanDefinition("clientPool").getPropertyValues().get("subscriptionAckInterval"))
			.isEqualTo("500");
		assertThat(registry.getBeanDefinition("clientPool").getPropertyValues().get("retryAttempts"))
			.isEqualTo("3");
		assertThat(registry.getBeanDefinition("clientPool").getPropertyValues().get("pingInterval"))
			.isEqualTo("15000");
		assertThat(registry.getBeanDefinition("clientPool").getPropertyValues().get("subscriptionRedundancy"))
			.isEqualTo("1");
	}

	@Test
	public void clientTemplateSettingsInheritAndOverrideWithoutGenericAttributes() {

		DefaultListableBeanFactory registry = load("""
			<gfe:client-region-template id="base" initial-capacity="51" disk-synchronous="false"
			                          cloning-enabled="true" statistics="true">
				<gfe:entry-ttl timeout="60" action="INVALIDATE"/>
			</gfe:client-region-template>
			<gfe:client-region-template id="middle" template="base" load-factor="0.85"/>
			<gfe:client-region id="child" template="middle" cloning-enabled="false" initial-capacity="17"/>
			<gfe:client-region id="defaults" shortcut="LOCAL_HEAP_LRU"/>
			""");

		var settings = registry.getMergedBeanDefinition("child").getPropertyValues();
		assertThat(settings.get("initialCapacity")).isEqualTo("17");
		assertThat(settings.get("diskSynchronous")).isEqualTo("false");
		assertThat(settings.get("cloningEnabled")).isEqualTo("false");
		assertThat(settings.get("loadFactor")).isEqualTo("0.85");
		assertThat(settings.get("statisticsEnabled")).isEqualTo(Boolean.TRUE);
		assertThat(settings.get("entryTimeToLive"))
			.isSameAs(registry.getBeanDefinition("base").getPropertyValues().get("entryTimeToLive"));
		assertThat(settings.contains("attributes")).isFalse();

		var defaults = registry.getMergedBeanDefinition("defaults").getPropertyValues();
		for (String property : new String[] { "attributes", "evictionAttributes", "entryTimeToLive",
				"initialCapacity", "diskSynchronous", "cloningEnabled", "concurrencyChecksEnabled" }) {
			assertThat(defaults.contains(property)).as(property).isFalse();
		}
	}

	@Test
	public void clientTemplatesMustStillBeDefinedBeforeUse() {

		assertThatThrownBy(() -> load("""
			<gfe:client-region id="child" template="later"/>
			<gfe:client-region-template id="later"/>
			"""))
			.isInstanceOf(BeanDefinitionStoreException.class)
			.hasMessageContaining("The Region template [later] must be defined before the Region [child]");
	}

	private void assertInvalid(String body) {
		assertThatThrownBy(() -> load(body)).isInstanceOf(BeanDefinitionStoreException.class)
			.hasMessageContaining("invalid");
	}

	private DefaultListableBeanFactory load(String body) {
		return load(body, true);
	}

	private DefaultListableBeanFactory load(String body, boolean validate) {

		String document = """
			<beans xmlns="http://www.springframework.org/schema/beans"
			       xmlns:gfe="http://www.springframework.org/schema/geode"
			       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			       xsi:schemaLocation="http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd
			                           http://www.springframework.org/schema/geode https://www.springframework.org/schema/geode/spring-geode.xsd">
			""" + body + "</beans>";
		DefaultListableBeanFactory registry = new DefaultListableBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(registry);
		reader.setValidating(validate);
		reader.setNamespaceAware(true);
		reader.loadBeanDefinitions(
			new ByteArrayResource(document.getBytes(StandardCharsets.UTF_8)));
		return registry;
	}
}
