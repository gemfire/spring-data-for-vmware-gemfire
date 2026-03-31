/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.springframework.data.gemfire.util.ArrayUtils.length;
import static org.springframework.data.gemfire.util.ArrayUtils.nullSafeArray;
import static org.springframework.data.gemfire.util.RegionUtils.toRegionPath;
import org.springframework.data.gemfire.gud.api.GudDataPolicy;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudRegionAttributes;
import org.springframework.data.gemfire.gud.api.GudRegionShortcut;
import org.springframework.data.gemfire.gud.api.GudScope;
import org.springframework.data.gemfire.gud.api.GudClientRegionShortcut;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.junit.After;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.config.annotation.test.entities.ClientRegionEntity;
import org.springframework.data.gemfire.config.annotation.test.entities.GenericRegionEntity;
import org.springframework.data.gemfire.config.annotation.test.entities.NonEntity;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;

/**
 * Unit Tests for the {@link EnableEntityDefinedRegions} annotation and {@link EntityDefinedRegionsConfiguration} class.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.GudRegion
 * @see org.springframework.context.ConfigurableApplicationContext
 * @see org.springframework.context.annotation.AnnotationConfigApplicationContext
 * @see org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions
 * @see org.springframework.data.gemfire.config.annotation.EntityDefinedRegionsConfiguration
 * @see org.springframework.data.gemfire.mapping.annotation.ClientRegion
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

	private <K, V> void assertRegion(GudRegion<K, V> region, String name) {
		assertRegion(region, name, toRegionPath(name), null, null);
	}

	private <K, V> void assertRegion(GudRegion<K, V> region, String name,
			Class<K> keyConstraint, Class<V> valueConstraint) {

		assertRegion(region, name, toRegionPath(name), keyConstraint, valueConstraint);
	}

	private <K, V> void assertRegion(GudRegion<K, V> region, String name, String fullPath) {
		assertRegion(region, name, fullPath, null, null);
	}

	private <K, V> void assertRegion(GudRegion<K, V> region, String name, String fullPath,
			Class<K> keyConstraint, Class<V> valueConstraint) {

		assertThat(region).isNotNull();
		assertThat(region.getName()).isEqualTo(name);
		assertThat(region.getFullPath()).isEqualTo(fullPath);
		assertThat(region.getAttributes()).isNotNull();
		assertThat(region.getAttributes().getKeyConstraint()).isEqualTo(keyConstraint);
		assertThat(region.getAttributes().getValueConstraint()).isEqualTo(valueConstraint);
	}

	private <K, V> void assertRegionWithAttributes(GudRegion<K, V> region, String name, GudDataPolicy dataPolicy,
			String diskStoreName, Boolean diskSynchronous, Boolean ignoreJta, String poolName, GudScope scope) {

		assertRegion(region, name);
		assertThat(region.getAttributes()).isNotNull();
		assertRegionAttributes(region.getAttributes(), dataPolicy, diskStoreName, diskSynchronous, ignoreJta,
			poolName, scope);
	}

	private <K, V> void assertRegionAttributes(GudRegionAttributes<K, V> regionAttributes, GudDataPolicy dataPolicy,
			String diskStoreName, Boolean diskSynchronous, Boolean ignoreJta, String poolName, GudScope scope) {

		assertThat(regionAttributes).isNotNull();
		assertThat(regionAttributes.getDataPolicy()).isEqualTo(dataPolicy);
		assertThat(regionAttributes.getDiskStoreName()).isEqualTo(diskStoreName);
		assertThat(regionAttributes.isDiskSynchronous()).isEqualTo(diskSynchronous);
		assertThat(regionAttributes.getPoolName()).isEqualToIgnoringCase(poolName);
		assertThat(regionAttributes.getScope()).isEqualTo(scope);
	}

	private void assertUndefinedRegions(String... regionBeanNames) {

		stream(nullSafeArray(regionBeanNames, String.class)).forEach(regionBeanName ->
			assertThat(containsBean(regionBeanName)).isFalse());

		assertThat(getBeansOfType(GudRegion.class)).hasSize(4 - length(regionBeanNames));
	}

	@Test
	public void entityClientRegionsDefined() {

		newApplicationContext(ClientPersistentEntitiesConfiguration.class);

		GudRegion<String, ClientRegionEntity> sessions = getBean("Sessions", GudRegion.class);

		assertRegion(sessions, "Sessions", String.class, ClientRegionEntity.class);
		assertRegionAttributes(sessions.getAttributes(), GudDataPolicy.NORMAL,
			null, true, false, null, null);

		GudRegion<Long, GenericRegionEntity> genericRegionEntity = getBean("GenericRegionEntity", GudRegion.class);

		assertRegion(genericRegionEntity, "GenericRegionEntity", Long.class, GenericRegionEntity.class);
		assertRegionAttributes(genericRegionEntity.getAttributes(), GudDataPolicy.EMPTY,
			null, true, false, null, null);

		assertUndefinedRegions("ClientRegionEntity", "NonEntity");
	}

	@Test
	public void entityClientRegionsDefinedWithCustomConfiguration() {

		newApplicationContext(ClientPersistentEntitiesWithCustomConfiguration.class);

		GudRegion<Object, Object> sessions = getBean("Sessions", GudRegion.class);

		assertRegionWithAttributes(sessions, "Sessions", GudDataPolicy.NORMAL,
			null, true, false, null, null);

		GudRegion<Object, Object> genericRegionEntity = getBean("GenericRegionEntity", GudRegion.class);

		assertRegionWithAttributes(genericRegionEntity, "GenericRegionEntity", GudDataPolicy.NORMAL,
			null, true, false, "TestPool", null);

		assertUndefinedRegions("ClientRegionEntity", "NonEntity");
	}

	@Test
	public void entityClientRegionsDefinedWithServerRegionMappingAnnotations() {

		newApplicationContext(ClientPersistentEntitiesWithServerRegionMappingAnnotationsConfiguration.class);

		GudRegion<String, ClientRegionEntity> sessions = getBean("Sessions", GudRegion.class);

		assertRegion(sessions, "Sessions", String.class, ClientRegionEntity.class);
		assertRegionAttributes(sessions.getAttributes(), GudDataPolicy.NORMAL,
			null, true, false, null, null);

		GudRegion<Long, GenericRegionEntity> genericRegionEntity = getBean("GenericRegionEntity", GudRegion.class);

		assertRegion(genericRegionEntity, "GenericRegionEntity", Long.class, GenericRegionEntity.class);
		assertRegionAttributes(genericRegionEntity.getAttributes(), GudDataPolicy.EMPTY,
			null, true, false, null, null);

		assertUndefinedRegions("ClientRegionEntity", "NonEntity");
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions(basePackageClasses = NonEntity.class, strict = true)
	static class ClientPersistentEntitiesConfiguration { }

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions(basePackageClasses = NonEntity.class, clientRegionShortcut = GudClientRegionShortcut.LOCAL,
		poolName = "TestPool")
	static class ClientPersistentEntitiesWithCustomConfiguration {

		@Bean("TestPool")
		GudPool testPool() {
			return mock(GudPool.class, "TestPool");
		}
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions(basePackageClasses = NonEntity.class, serverRegionShortcut = GudRegionShortcut.LOCAL,
		strict = true
	)
	static class ClientPersistentEntitiesWithServerRegionMappingAnnotationsConfiguration { }

}
