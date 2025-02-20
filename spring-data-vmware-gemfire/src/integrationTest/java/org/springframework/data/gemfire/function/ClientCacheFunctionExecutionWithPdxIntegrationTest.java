/*
 * Copyright 2022-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.PdxInstanceFactory;
import org.apache.geode.pdx.PdxSerializer;
import org.apache.geode.pdx.internal.PdxInstanceEnum;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.function.sample.ApplicationDomainFunctionExecutions;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.utility.MountableFile;

import com.vmware.gemfire.testcontainers.GemFireCluster;

/**
 * Integration Tests for SDG's Function annotation support and interaction between an Apache Geode client
 * and server cache when PDX is configured and read-serialized is set to {@literal true}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.pdx.PdxInstance
 * @see org.apache.geode.pdx.PdxInstanceFactory
 * @see org.apache.geode.pdx.PdxReader
 * @see org.apache.geode.pdx.PdxSerializer
 * @see org.apache.geode.pdx.PdxWriter
 * @see org.apache.geode.pdx.internal.PdxInstanceEnum
 * @see org.springframework.data.gemfire.function.sample.ApplicationDomainFunctionExecutions
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.5.2
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class ClientCacheFunctionExecutionWithPdxIntegrationTest {

	private static GemFireCluster gemFireCluster;

	@Autowired
	private ClientCache gemfireClientCache;

	@Autowired
	private ApplicationDomainFunctionExecutions functionExecutions;

	@BeforeClass
	public static void startGemFireServer() throws Exception {
		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 1)
				.withCacheXml(GemFireCluster.ALL_GLOB, "/client-cache-function-execution-with-pdx-cache.xml")
				.withClasspath(GemFireCluster.ALL_GLOB, System.getProperty("TEST_JAR_PATH"))
				.withPdx("org\\.springframework\\.data\\.gemfire\\.function\\..*", true)
				.withPreStart(GemFireCluster.ALL_GLOB, container -> container.copyFileToContainer(MountableFile.forHostPath(System.getProperty("TEST_JAR_PATH")), "/testJar.jar"))
				.withGfsh(false, "deploy --jar=/testJar.jar");

		gemFireCluster.acceptLicense().start();

		System.setProperty("spring.data.gemfire.cache.server.port", String.valueOf(gemFireCluster.getServerPorts().get(0)));
	}

	private PdxInstance toPdxInstance(Map<String, Object> pdxData) {

		PdxInstanceFactory pdxInstanceFactory =
			this.gemfireClientCache.createPdxInstanceFactory(pdxData.get("@type").toString());

		for (Map.Entry<String, Object> entry : pdxData.entrySet()) {
			pdxInstanceFactory.writeObject(entry.getKey(), entry.getValue());
		}

		return pdxInstanceFactory.create();
	}

	@Test
	public void unconvertedFunctionArgumentTypes() {

		Class<?>[] argumentTypes = this.functionExecutions
			.captureUnconvertedArgumentTypes("test", 2, Boolean.FALSE, new Person("Jane", "Doe"),
				Gender.FEMALE);

		Assertions.assertThat(argumentTypes).isNotNull();
		Assertions.assertThat(argumentTypes.length).isEqualTo(5);
		Assertions.assertThat(argumentTypes[0]).isEqualTo(String.class);
		Assertions.assertThat(argumentTypes[1]).isEqualTo(Integer.class);
		Assertions.assertThat(argumentTypes[2]).isEqualTo(Boolean.class);
		Assertions.assertThat(PdxInstance.class).isAssignableFrom(argumentTypes[3]);
		Assertions.assertThat(argumentTypes[4]).isEqualTo(PdxInstanceEnum.class);
	}

	@Test
	public void getAddressFieldValue() {

		Address address = new Address("100 Main St.", "Portland", "OR", "97205");

		Assertions.assertThat(this.functionExecutions.getAddressField(address, "city")).isEqualTo("Portland");
	}

	@Test
	public void pdxDataFieldValue() {

		Map<String, Object> pdxData = new HashMap<>(3);

		pdxData.put("@type", "x.y.z.domain.MyApplicationDomainType");
		pdxData.put("booleanField", Boolean.TRUE);
		pdxData.put("integerField", 123);
		pdxData.put("stringField", "test");

		Integer value = this.functionExecutions.getDataField(toPdxInstance(pdxData), "integerField");

		Assertions.assertThat(value).isEqualTo(pdxData.get("integerField"));
	}

	public static class ComposablePdxSerializerFactoryBean implements FactoryBean<PdxSerializer>, InitializingBean {

		private List<PdxSerializer> pdxSerializers = Collections.emptyList();

		private PdxSerializer pdxSerializer;

		public void setPdxSerializers(List<PdxSerializer> pdxSerializers) {
			this.pdxSerializers = pdxSerializers;
		}

		@Override
		public void afterPropertiesSet() {
			this.pdxSerializer = ComposablePdxSerializer.compose(this.pdxSerializers.toArray(new PdxSerializer[0]));
		}

		@Override
		public PdxSerializer getObject() {
			return this.pdxSerializer;
		}

		@Override
		public Class<?> getObjectType() {
			return this.pdxSerializer != null ? this.pdxSerializer.getClass() : PdxSerializer.class;
		}

		@Override
		public boolean isSingleton() {
			return true;
		}
	}
}
