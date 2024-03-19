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
import static org.springframework.data.gemfire.util.CollectionUtils.nullSafeList;
import static org.springframework.data.gemfire.util.RegionUtils.toRegionPath;

import java.util.List;

import org.junit.After;
import org.junit.Test;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.DiskStore;
import org.apache.geode.cache.FixedPartitionAttributes;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.PartitionAttributes;
import org.apache.geode.cache.PartitionResolver;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.RegionExistsException;
import org.apache.geode.cache.RegionShortcut;
import org.apache.geode.cache.Scope;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.client.Pool;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.gemfire.PartitionedRegionFactoryBean;
import org.springframework.data.gemfire.ReplicatedRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.test.entities.ClientRegionEntity;
import org.springframework.data.gemfire.config.annotation.test.entities.CollocatedPartitionRegionEntity;
import org.springframework.data.gemfire.config.annotation.test.entities.GenericRegionEntity;
import org.springframework.data.gemfire.config.annotation.test.entities.LocalRegionEntity;
import org.springframework.data.gemfire.config.annotation.test.entities.NonEntity;
import org.springframework.data.gemfire.config.annotation.test.entities.PartitionRegionEntity;
import org.springframework.data.gemfire.config.annotation.test.entities.ReplicateRegionEntity;
import org.springframework.data.gemfire.mapping.annotation.ClientRegion;
import org.springframework.data.gemfire.mapping.annotation.LocalRegion;
import org.springframework.data.gemfire.mapping.annotation.PartitionRegion;
import org.springframework.data.gemfire.mapping.annotation.ReplicateRegion;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.MockObjectsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;

/**
 * Unit Tests for the {@link EnableEntityDefinedRegions} annotation and {@link EntityDefinedRegionsConfiguration} class.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see GemFireCache
 * @see Region
 * @see org.springframework.context.ConfigurableApplicationContext
 * @see org.springframework.context.annotation.AnnotationConfigApplicationContext
 * @see EnableEntityDefinedRegions
 * @see EntityDefinedRegionsConfiguration
 * @see ClientRegion
 * @see LocalRegion
 * @see PartitionRegion
 * @see ReplicateRegion
 * @see ReplicateRegion
 * @see MockObjectsSupport
 * @see SpringApplicationContextIntegrationTestsSupport
 * @see EnableGemFireMockObjects
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

	private <K, V> void assertPartitionAttributes(PartitionAttributes<K, V> partitionAttributes,
			String collocatedWith, PartitionResolver<?, ?> partitionResolver, Integer redundantCopies) {

		assertThat(partitionAttributes).isNotNull();
		assertThat(partitionAttributes.getColocatedWith()).isEqualTo(collocatedWith);
		assertThat(partitionAttributes.getPartitionResolver()).isEqualTo(partitionResolver);
		assertThat(partitionAttributes.getRedundantCopies()).isEqualTo(redundantCopies);
	}

	private void assertFixedPartitionAttributes(FixedPartitionAttributes fixedPartitionAttributes,
			String partitionName, boolean primary, int numBuckets) {

		assertThat(fixedPartitionAttributes).isNotNull();
		assertThat(fixedPartitionAttributes.getPartitionName()).isEqualTo(partitionName);
		assertThat(fixedPartitionAttributes.isPrimary()).isEqualTo(primary);
		assertThat(fixedPartitionAttributes.getNumBuckets()).isEqualTo(numBuckets);
	}

	private void assertUndefinedRegions(String... regionBeanNames) {

		stream(nullSafeArray(regionBeanNames, String.class)).forEach(regionBeanName ->
			assertThat(containsBean(regionBeanName)).isFalse());

		assertThat(getBeansOfType(Region.class)).hasSize(11 - length(regionBeanNames));
	}

	private FixedPartitionAttributes findFixedPartitionAttributes(PartitionAttributes<?, ?> partitionAttributes,
			String partitionName) {

		assertThat(partitionAttributes).isNotNull();

		List<FixedPartitionAttributes> fixedPartitionAttributes =
			nullSafeList(partitionAttributes.getFixedPartitionAttributes());

		for (FixedPartitionAttributes attributes : fixedPartitionAttributes) {
			if (attributes.getPartitionName().equals(partitionName)) {
				return attributes;
			}
		}

		return null;
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

		assertUndefinedRegions("ClientRegionEntity", "CollocatedPartitionRegionEntity",
			"ContactEvents", "LocalRegionEntity", "NonEntity", "PartitionRegionEntity", "Customers",
			"ReplicateRegionEntity", "Accounts");
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

		assertUndefinedRegions("ClientRegionEntity", "CollocatedPartitionRegionEntity",
			"ContactEvents", "LocalRegionEntity", "NonEntity", "PartitionRegionEntity", "Customers",
			"ReplicateRegionEntity", "Accounts");
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

		Region<Long, PartitionRegionEntity> customers = getBean("Customers", Region.class);

		assertRegion(customers, "Customers", Long.class, PartitionRegionEntity.class);
		assertRegionAttributes(customers.getAttributes(), DataPolicy.EMPTY,
			null, true, false, null, null);

		Region<Object, ReplicateRegionEntity> accounts = getBean("Accounts", Region.class);

		assertRegion(accounts, "Accounts", Object.class, ReplicateRegionEntity.class);
		assertRegionAttributes(accounts.getAttributes(), DataPolicy.EMPTY,
			null, true, false, null, null);

		assertUndefinedRegions("ClientRegionEntity", "CollocatedPartitionRegionEntity",
			"ContactEvents", "NonEntity", "PartitionRegionEntity", "ReplicateRegionEntity");
	}

	@Test
	public void entityPeerPartitionRegionsDefined() {

		newApplicationContext(PeerPartitionRegionPersistentEntitiesConfiguration.class);

		Region<Object, Object> customers = getBean("Customers", Region.class);

		assertRegionWithAttributes(customers, "Customers", DataPolicy.PERSISTENT_PARTITION, null,
			true, false, null, Scope.DISTRIBUTED_NO_ACK);
		assertPartitionAttributes(customers.getAttributes().getPartitionAttributes(), null,
			null, 1);
		assertFixedPartitionAttributes(findFixedPartitionAttributes(customers.getAttributes().getPartitionAttributes(),
			"one"), "one", true, 16);
		assertFixedPartitionAttributes(findFixedPartitionAttributes(customers.getAttributes().getPartitionAttributes(),
			"two"), "two", false, 21);

		Region<Object, Object> contactEvents = getBean("ContactEvents", Region.class);

		assertRegionWithAttributes(contactEvents, "ContactEvents", DataPolicy.PERSISTENT_PARTITION,
			"mockDiskStore", false, true, null, Scope.DISTRIBUTED_NO_ACK);
		assertPartitionAttributes(contactEvents.getAttributes().getPartitionAttributes(), "Customers",
			getBean("mockPartitionResolver", PartitionResolver.class), 2);

		assertUndefinedRegions("ClientRegionEntity", "Sessions", "CollocatedPartitionRegionEntity",
			"GenericRegionEntity", "LocalRegionEntity", "NonEntity", "PartitionRegionEntity", "ReplicateRegionEntity",
			"Accounts");
	}

	@Test(expected = RegionExistsException.class)
	public void entityPartitionRegionAlreadyDefinedThrowsRegionExistsException() {

		try {
			newApplicationContext(ExistingPartitionRegionPersistentEntitiesConfiguration.class);
		}
		catch (BeanCreationException expected) {

			assertThat(expected).hasCauseInstanceOf(RegionExistsException.class);
			assertThat(expected.getCause()).hasMessage("/Customers");

			throw (RegionExistsException) expected.getCause();
		}
	}

	@Test
	public void entityReplicateRegionAlreadyDefinedIgnoresEntityDefinedRegionDefinition() {

		newApplicationContext(ExistingReplicateRegionPersistentEntitiesConfiguration.class);

		Region<Object, Object> accounts = getBean("Accounts", Region.class);

		assertRegionWithAttributes(accounts, "Accounts", DataPolicy.REPLICATE,
			null, true, false, null, Scope.DISTRIBUTED_NO_ACK);
	}

	@Test
	public void entityServerRegionsDefined() {

		newApplicationContext(ServerPersistentEntitiesConfiguration.class);

		Region<Object, Object> accounts = getBean("Accounts", Region.class);

		assertRegionWithAttributes(accounts, "Accounts", DataPolicy.REPLICATE,
			null, true, false, null, Scope.DISTRIBUTED_ACK);

		Region<Object, Object> customers = getBean("Customers", Region.class);

		assertRegionWithAttributes(customers, "Customers", DataPolicy.PERSISTENT_PARTITION,
			null, true, false, null, Scope.DISTRIBUTED_NO_ACK);
		assertPartitionAttributes(customers.getAttributes().getPartitionAttributes(), null,
			null, 1);

		Region<Object, Object> localRegionEntity = getBean("LocalRegionEntity", Region.class);

		assertRegionWithAttributes(localRegionEntity, "LocalRegionEntity", DataPolicy.NORMAL,
			null, true, false, null, Scope.LOCAL);

		Region<Object, Object> genericRegionEntity = getBean("GenericRegionEntity", Region.class);

		assertRegionWithAttributes(genericRegionEntity, "GenericRegionEntity", DataPolicy.PARTITION,
			null, true, false, null, Scope.DISTRIBUTED_NO_ACK);

		assertUndefinedRegions("ClientRegionEntity", "Sessions", "CollocatedPartitionRegionEntity",
			"ContactEvents", "NonEntity", "PartitionRegionEntity", "ReplicateRegionEntity");
	}

	@Test
	public void entityServerRegionsDefinedWithCustomConfiguration() {

		newApplicationContext(ServerPersistentEntitiesWithCustomConfiguration.class);

		Region<Object, Object> accounts = getBean("Sessions", Region.class);

		assertRegionWithAttributes(accounts, "Sessions", DataPolicy.REPLICATE,
			null, true, false, null, Scope.DISTRIBUTED_ACK);

		Region<Object, Object> genericRegionEntity = getBean("GenericRegionEntity", Region.class);

		assertRegionWithAttributes(genericRegionEntity, "GenericRegionEntity", DataPolicy.REPLICATE,
			null, true, false, null, Scope.DISTRIBUTED_ACK);

		Region<Object, Object> localRegionEntity = getBean("LocalRegionEntity", Region.class);

		assertRegionWithAttributes(localRegionEntity, "LocalRegionEntity", DataPolicy.NORMAL,
			null, true, false, null, Scope.LOCAL);

		Region<Object, Object> customers = getBean("Customers", Region.class);

		assertRegionWithAttributes(customers, "Customers", DataPolicy.PERSISTENT_PARTITION,
			null, true, false, null, Scope.DISTRIBUTED_NO_ACK);

		assertUndefinedRegions("ClientRegionEntity", "CollocatedPartitionRegionEntity",
			"ContactEvents", "NonEntity", "PartitionRegionEntity", "ReplicateRegionEntity", "Accounts");
	}

	@Test
	public void entityServerRegionsDefinedWithClientRegionMappingAnnotations() {

		newApplicationContext(ServerPersistentEntitiesWithClientRegionMappingAnnotationsConfiguration.class);

		Region<Object, Object> sessions = getBean("Sessions", Region.class);

		assertRegionWithAttributes(sessions, "Sessions", DataPolicy.PARTITION,
			null, true, false, null, Scope.DISTRIBUTED_NO_ACK);

		Region<Object, Object> genericRegionEntity = getBean("GenericRegionEntity", Region.class);

		assertRegionWithAttributes(genericRegionEntity, "GenericRegionEntity", DataPolicy.PARTITION,
			null, true, false, null, Scope.DISTRIBUTED_NO_ACK);

		Region<Object, Object> customers = getBean("Customers", Region.class);

		assertRegionWithAttributes(customers, "Customers", DataPolicy.PERSISTENT_PARTITION,
			null, true, false, null, Scope.DISTRIBUTED_NO_ACK);

		assertUndefinedRegions("ClientRegionEntity", "CollocatedPartitionRegionEntity",
			"ContactEvents", "LocalRegionEntity", "NonEntity", "PartitionRegionEntity", "ReplicateRegionEntity",
			"Accounts");
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions(basePackageClasses = NonEntity.class, strict = true, excludeFilters =
		@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {
			LocalRegion.class, PartitionRegion.class, ReplicateRegion.class
		})
	)
	static class ClientPersistentEntitiesConfiguration { }

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions(basePackageClasses = NonEntity.class, clientRegionShortcut = ClientRegionShortcut.LOCAL,
		poolName = "TestPool", excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION,
			classes = { LocalRegion.class, PartitionRegion.class, ReplicateRegion.class })
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
		strict = true, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
			classes = CollocatedPartitionRegionEntity.class)
	)
	static class ClientPersistentEntitiesWithServerRegionMappingAnnotationsConfiguration { }

	@PeerCacheApplication
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions(basePackageClasses = NonEntity.class, excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {
			ClientRegion.class, LocalRegion.class, ReplicateRegion.class
		}),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = GenericRegionEntity.class)
	})
	static class PeerPartitionRegionPersistentEntitiesConfiguration {

		@Bean @Lazy
		DiskStore mockDiskStore() {
			return mock(DiskStore.class, MockObjectsSupport.mockObjectIdentifier("MockDiskStore"));
		}

		@Bean @Lazy
		PartitionResolver<?, ?> mockPartitionResolver() {
			return mock(PartitionResolver.class,
				MockObjectsSupport.mockObjectIdentifier("MockPartitionResolver"));
		}
	}

	@PeerCacheApplication
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions(basePackageClasses = NonEntity.class, excludeFilters = {
			@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = ClientRegion.class),
			@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CollocatedPartitionRegionEntity.class)
	})
	static class ServerPersistentEntitiesConfiguration { }

	@PeerCacheApplication
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions(basePackageClasses = NonEntity.class, serverRegionShortcut = RegionShortcut.REPLICATE,
		excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
			CollocatedPartitionRegionEntity.class, ReplicateRegionEntity.class
		})
	)
	static class ServerPersistentEntitiesWithCustomConfiguration { }

	@PeerCacheApplication
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions(basePackageClasses = NonEntity.class, clientRegionShortcut = ClientRegionShortcut.LOCAL,
		poolName = "TestPool", excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
			CollocatedPartitionRegionEntity.class, LocalRegionEntity.class, ReplicateRegionEntity.class
		})
	)
	static class ServerPersistentEntitiesWithClientRegionMappingAnnotationsConfiguration { }

	@PeerCacheApplication
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions(basePackageClasses = NonEntity.class, excludeFilters =
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
			ClientRegionEntity.class, CollocatedPartitionRegionEntity.class, GenericRegionEntity.class,
			LocalRegionEntity.class, ReplicateRegionEntity.class
		})
	)
	static class ExistingPartitionRegionPersistentEntitiesConfiguration {

		@Bean
		PartitionedRegionFactoryBean<Long, PartitionRegionEntity> customersRegion(GemFireCache gemfireCache) {

			PartitionedRegionFactoryBean<Long, PartitionRegionEntity> customers = new PartitionedRegionFactoryBean<>();

			customers.setCache(gemfireCache);
			customers.setClose(false);
			customers.setPersistent(false);
			customers.setRegionName("Customers");

			return customers;
		}
	}

	@PeerCacheApplication
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions(basePackageClasses = NonEntity.class, excludeFilters =
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
			ClientRegionEntity.class, CollocatedPartitionRegionEntity.class, GenericRegionEntity.class,
			LocalRegionEntity.class, PartitionRegionEntity.class
		})
	)
	static class ExistingReplicateRegionPersistentEntitiesConfiguration {

		@Bean
		ReplicatedRegionFactoryBean<Long, ReplicateRegionEntity> accountsRegion(GemFireCache gemfireCache) {

			ReplicatedRegionFactoryBean<Long, ReplicateRegionEntity> accounts = new ReplicatedRegionFactoryBean<>();

			accounts.setCache(gemfireCache);
			accounts.setClose(false);
			accounts.setLookupEnabled(true);
			accounts.setPersistent(false);
			accounts.setRegionName("Accounts");
			accounts.setScope(Scope.DISTRIBUTED_NO_ACK);

			return accounts;
		}
	}
}
