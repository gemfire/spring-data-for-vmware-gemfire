/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.springframework.data.gemfire.util.ArrayUtils.length;
import static org.springframework.data.gemfire.util.ArrayUtils.nullSafeArray;
import static org.springframework.data.gemfire.util.RegionUtils.toRegionPath;

import org.junit.After;
import org.junit.Test;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.RegionShortcut;
import org.apache.geode.cache.Scope;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.client.Pool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.gemfire.config.annotation.test.entities.ClientRegionEntity;
import org.springframework.data.gemfire.config.annotation.test.entities.GenericRegionEntity;
import org.springframework.data.gemfire.config.annotation.test.entities.LocalRegionEntity;
import org.springframework.data.gemfire.config.annotation.test.entities.NonEntity;
import org.springframework.data.gemfire.mapping.annotation.ClientRegion;
import org.springframework.data.gemfire.mapping.annotation.LocalRegion;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;

/**
 * Unit Tests for the {@link EnableEntityDefinedRegions} annotation and {@link EntityDefinedRegionsConfiguration} class.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.Region
 * @see org.springframework.context.ConfigurableApplicationContext
 * @see org.springframework.context.annotation.AnnotationConfigApplicationContext
 * @see org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions
 * @see org.springframework.data.gemfire.config.annotation.EntityDefinedRegionsConfiguration
 * @see org.springframework.data.gemfire.mapping.annotation.ClientRegion
 * @see org.springframework.data.gemfire.mapping.annotation.LocalRegion
 * @see org.springframework.data.gemfire.tests.mock.MockObjectsSupport
 * @see org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @since 1.9.0
 */
@SuppressWarnings({ "unchecked", "unused" })
public class EnableEntityDefinedRegionsUnitTests extends SpringApplicationContextIntegrationTestsSupport {

	@After
	public void tearDown() {
		destroyAllGemFireMockObjects();
	}

	private <K, V> void assertRegion(Region<K, V> region, String name) {
		assertRegion(region, name, toRegionPath(name), null, null);
	}

	private <K, V> void assertRegion(Region<K, V> region, String name,
			Class<K> keyConstraint, Class<V> valueConstraint) {

		assertRegion(region, name, toRegionPath(name), keyConstraint, valueConstraint);
	}

	private <K, V> void assertRegion(Region<K, V> region, String name, String fullPath) {
		assertRegion(region, name, fullPath, null, null);
	}

	private <K, V> void assertRegion(Region<K, V> region, String name, String fullPath,
			Class<K> keyConstraint, Class<V> valueConstraint) {

		assertThat(region).isNotNull();
		assertThat(region.getName()).isEqualTo(name);
		assertThat(region.getFullPath()).isEqualTo(fullPath);
		assertThat(region.getAttributes()).isNotNull();
		assertThat(region.getAttributes().getKeyConstraint()).isEqualTo(keyConstraint);
		assertThat(region.getAttributes().getValueConstraint()).isEqualTo(valueConstraint);
	}

	private <K, V> void assertRegionWithAttributes(Region<K, V> region, String name, DataPolicy dataPolicy,
			String diskStoreName, Boolean diskSynchronous, Boolean ignoreJta, String poolName, Scope scope) {

		assertRegion(region, name);
		assertThat(region.getAttributes()).isNotNull();
		assertRegionAttributes(region.getAttributes(), dataPolicy, diskStoreName, diskSynchronous, ignoreJta,
			poolName, scope);
	}

	private <K, V> void assertRegionAttributes(RegionAttributes<K, V> regionAttributes, DataPolicy dataPolicy,
			String diskStoreName, Boolean diskSynchronous, Boolean ignoreJta, String poolName, Scope scope) {

		assertThat(regionAttributes).isNotNull();
		assertThat(regionAttributes.getDataPolicy()).isEqualTo(dataPolicy);
		assertThat(regionAttributes.getDiskStoreName()).isEqualTo(diskStoreName);
		assertThat(regionAttributes.isDiskSynchronous()).isEqualTo(diskSynchronous);
		assertThat(regionAttributes.getIgnoreJTA()).isEqualTo(ignoreJta);
		assertThat(regionAttributes.getPoolName()).isEqualToIgnoringCase(poolName);
		assertThat(regionAttributes.getScope()).isEqualTo(scope);
	}

	private void assertUndefinedRegions(String... regionBeanNames) {

		stream(nullSafeArray(regionBeanNames, String.class)).forEach(regionBeanName ->
			assertThat(containsBean(regionBeanName)).isFalse());

		assertThat(getBeansOfType(Region.class)).hasSize(5 - length(regionBeanNames));
	}

	@Test
	public void entityClientRegionsDefined() {

		newApplicationContext(ClientPersistentEntitiesConfiguration.class);

		Region<String, ClientRegionEntity> sessions = getBean("Sessions", Region.class);

		assertRegion(sessions, "Sessions", String.class, ClientRegionEntity.class);
		assertRegionAttributes(sessions.getAttributes(), DataPolicy.NORMAL,
			null, true, false, null, null);

		Region<Long, GenericRegionEntity> genericRegionEntity = getBean("GenericRegionEntity", Region.class);

		assertRegion(genericRegionEntity, "GenericRegionEntity", Long.class, GenericRegionEntity.class);
		assertRegionAttributes(genericRegionEntity.getAttributes(), DataPolicy.EMPTY,
			null, true, false, null, null);

		assertUndefinedRegions("ClientRegionEntity", "LocalRegionEntity", "NonEntity");
	}

	@Test
	public void entityClientRegionsDefinedWithCustomConfiguration() {

		newApplicationContext(ClientPersistentEntitiesWithCustomConfiguration.class);

		Region<Object, Object> sessions = getBean("Sessions", Region.class);

		assertRegionWithAttributes(sessions, "Sessions", DataPolicy.NORMAL,
			null, true, false, null, null);

		Region<Object, Object> genericRegionEntity = getBean("GenericRegionEntity", Region.class);

		assertRegionWithAttributes(genericRegionEntity, "GenericRegionEntity", DataPolicy.NORMAL,
			null, true, false, "TestPool", null);

		assertUndefinedRegions("ClientRegionEntity", "LocalRegionEntity", "NonEntity");
	}

	@Test
	public void entityClientRegionsDefinedWithServerRegionMappingAnnotations() {

		newApplicationContext(ClientPersistentEntitiesWithServerRegionMappingAnnotationsConfiguration.class);

		Region<String, ClientRegionEntity> sessions = getBean("Sessions", Region.class);

		assertRegion(sessions, "Sessions", String.class, ClientRegionEntity.class);
		assertRegionAttributes(sessions.getAttributes(), DataPolicy.NORMAL,
			null, true, false, null, null);

		Region<Long, GenericRegionEntity> genericRegionEntity = getBean("GenericRegionEntity", Region.class);

		assertRegion(genericRegionEntity, "GenericRegionEntity", Long.class, GenericRegionEntity.class);
		assertRegionAttributes(genericRegionEntity.getAttributes(), DataPolicy.EMPTY,
			null, true, false, null, null);

		Region<String, LocalRegionEntity> localRegionEntity = getBean("LocalRegionEntity", Region.class);

		assertRegion(localRegionEntity, "LocalRegionEntity", String.class, LocalRegionEntity.class);
		assertRegionAttributes(localRegionEntity.getAttributes(), DataPolicy.EMPTY,
			null, true, false, null, null);

		assertUndefinedRegions("ClientRegionEntity", "NonEntity");
	}

	@Test
	public void entityServerRegionsDefined() {

		newApplicationContext(ServerPersistentEntitiesConfiguration.class);

		Region<Object, Object> localRegionEntity = getBean("LocalRegionEntity", Region.class);

		assertRegionWithAttributes(localRegionEntity, "LocalRegionEntity", DataPolicy.NORMAL,
			null, true, false, null, Scope.LOCAL);

		Region<Object, Object> genericRegionEntity = getBean("GenericRegionEntity", Region.class);

		assertRegionWithAttributes(genericRegionEntity, "GenericRegionEntity", DataPolicy.NORMAL,
			null, true, false, null, Scope.DISTRIBUTED_NO_ACK);

		assertUndefinedRegions("ClientRegionEntity", "Sessions", "NonEntity");
	}

	@Test
	public void entityServerRegionsDefinedWithClientRegionMappingAnnotations() {

		newApplicationContext(ServerPersistentEntitiesWithClientRegionMappingAnnotationsConfiguration.class);

		Region<Object, Object> sessions = getBean("Sessions", Region.class);

		assertRegionWithAttributes(sessions, "Sessions", DataPolicy.NORMAL,
			null, true, false, null, Scope.DISTRIBUTED_NO_ACK);

		Region<Object, Object> genericRegionEntity = getBean("GenericRegionEntity", Region.class);

		assertRegionWithAttributes(genericRegionEntity, "GenericRegionEntity", DataPolicy.NORMAL,
			null, true, false, null, Scope.DISTRIBUTED_NO_ACK);

		assertUndefinedRegions("ClientRegionEntity", "LocalRegionEntity", "NonEntity");
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions(basePackageClasses = NonEntity.class, strict = true, excludeFilters =
		@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {
			LocalRegion.class
		})
	)
	static class ClientPersistentEntitiesConfiguration { }

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions(basePackageClasses = NonEntity.class, clientRegionShortcut = ClientRegionShortcut.LOCAL,
		poolName = "TestPool", excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION,
			classes = { LocalRegion.class })
	)
	static class ClientPersistentEntitiesWithCustomConfiguration {

		@Bean("TestPool")
		Pool testPool() {
			return mock(Pool.class, "TestPool");
		}
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions(basePackageClasses = NonEntity.class, serverRegionShortcut = RegionShortcut.LOCAL,
		strict = true
	)
	static class ClientPersistentEntitiesWithServerRegionMappingAnnotationsConfiguration { }

	@PeerCacheApplication
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions(basePackageClasses = NonEntity.class, excludeFilters = {
			@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = ClientRegion.class),
	})
	static class ServerPersistentEntitiesConfiguration { }

	@PeerCacheApplication
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions(basePackageClasses = NonEntity.class, clientRegionShortcut = ClientRegionShortcut.LOCAL,
		poolName = "TestPool", excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
			LocalRegionEntity.class
		})
	)
	static class ServerPersistentEntitiesWithClientRegionMappingAnnotationsConfiguration { }

}
