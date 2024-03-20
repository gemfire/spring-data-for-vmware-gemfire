/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.EvictionAttributes;
import org.apache.geode.cache.ExpirationAttributes;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.RegionService;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.compression.Compressor;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.gemfire.TestUtils;
import org.springframework.data.gemfire.util.ArrayUtils;

/**
 * Unit Tests for {@link ClientRegionFactoryBean}.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.mockito.Spy
 * @see org.mockito.junit.MockitoJUnitRunner
 * @see org.apache.geode.cache.EvictionAttributes
 * @see org.apache.geode.cache.ExpirationAttributes
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.client.ClientRegionFactory
 * @see org.apache.geode.cache.client.Pool
 * @see org.springframework.data.gemfire.client.ClientRegionFactoryBean
 */
@SuppressWarnings("rawtypes")
@RunWith(MockitoJUnitRunner.class)
public class ClientRegionFactoryBeanUnitTests {

	@Mock
	private BeanFactory mockBeanFactory;

	@Spy
	private ClientRegionFactoryBean<Object, Object> factoryBean;

	@Before
	public void setup() {

		this.factoryBean.setBeanFactory(this.mockBeanFactory);
		this.factoryBean.initializePoolResolver();
	}

	@After
	public void tearDown() throws Exception {

		this.factoryBean.destroy();
		this.factoryBean = null;
	}

	@Test
	@SuppressWarnings({ "deprecation", "unchecked" })
	public void createRegionUsingDefaultShortcut() throws Exception {

		ClientCache mockClientCache = mock(ClientCache.class);

		ClientRegionFactory mockClientRegionFactory = mock(ClientRegionFactory.class);

		Pool mockPool = mock(Pool.class);

		Region mockRegion = mock(Region.class);

		RegionAttributes mockRegionAttributes = mock(RegionAttributes.class);

		when(mockBeanFactory.getBean(eq("TestPoolTwo"), eq(Pool.class))).thenReturn(mockPool);
		when(mockClientCache.createClientRegionFactory(eq(ClientRegionShortcut.LOCAL)))
			.thenReturn(mockClientRegionFactory);
		when(mockClientRegionFactory.create(eq("TestRegion"))).thenReturn(mockRegion);
		when(mockRegionAttributes.getCloningEnabled()).thenReturn(false);
		when(mockRegionAttributes.getCompressor()).thenReturn(mock(Compressor.class));
		when(mockRegionAttributes.getConcurrencyChecksEnabled()).thenReturn(true);
		when(mockRegionAttributes.getConcurrencyLevel()).thenReturn(8);
		when(mockRegionAttributes.getCustomEntryIdleTimeout()).thenReturn(null);
		when(mockRegionAttributes.getCustomEntryTimeToLive()).thenReturn(null);
		when(mockRegionAttributes.getDiskStoreName()).thenReturn("TestDiskStoreOne");
		when(mockRegionAttributes.isDiskSynchronous()).thenReturn(false);
		when(mockRegionAttributes.getEntryIdleTimeout()).thenReturn(mock(ExpirationAttributes.class));
		when(mockRegionAttributes.getEntryTimeToLive()).thenReturn(mock(ExpirationAttributes.class));
		when(mockRegionAttributes.getEvictionAttributes()).thenReturn(mock(EvictionAttributes.class));
		when(mockRegionAttributes.getInitialCapacity()).thenReturn(101);
		when(mockRegionAttributes.getKeyConstraint()).thenReturn(Long.class);
		when(mockRegionAttributes.getLoadFactor()).thenReturn(0.75f);
		when(mockRegionAttributes.getPoolName()).thenReturn("TestPoolOne");
		when(mockRegionAttributes.getRegionIdleTimeout()).thenReturn(mock(ExpirationAttributes.class));
		when(mockRegionAttributes.getRegionTimeToLive()).thenReturn(mock(ExpirationAttributes.class));
		when(mockRegionAttributes.getStatisticsEnabled()).thenReturn(true);
		when(mockRegionAttributes.getValueConstraint()).thenReturn(Number.class);

		EvictionAttributes evictionAttributes = EvictionAttributes.createLRUEntryAttributes();

		factoryBean.setAttributes(mockRegionAttributes);
		factoryBean.setBeanFactory(mockBeanFactory);
		factoryBean.setDiskStoreName("TestDiskStoreTwo");
		factoryBean.setEvictionAttributes(evictionAttributes);
		factoryBean.setPersistent(false);
		factoryBean.setPoolName("TestPoolTwo");
		factoryBean.setShortcut(null);

		Region actualRegion = factoryBean.createRegion(mockClientCache, "TestRegion");

		assertThat(actualRegion).isEqualTo(mockRegion);

		verify(mockClientCache, times(1)).createClientRegionFactory(eq(ClientRegionShortcut.LOCAL));
		verify(mockClientRegionFactory, times(1)).setCloningEnabled(eq(false));
		verify(mockClientRegionFactory, times(1)).setCompressor(any(Compressor.class));
		verify(mockClientRegionFactory, times(1)).setConcurrencyChecksEnabled(eq(true));
		verify(mockClientRegionFactory, times(1)).setConcurrencyLevel(eq(8));
		verify(mockClientRegionFactory, times(1)).setCustomEntryIdleTimeout(null);
		verify(mockClientRegionFactory, times(1)).setCustomEntryTimeToLive(null);
		verify(mockClientRegionFactory, times(1)).setDiskStoreName(eq("TestDiskStoreOne"));
		verify(mockClientRegionFactory, times(1)).setDiskStoreName(eq("TestDiskStoreTwo"));
		verify(mockClientRegionFactory, times(1)).setDiskSynchronous(eq(false));
		verify(mockClientRegionFactory, times(1)).setEntryIdleTimeout(any(ExpirationAttributes.class));
		verify(mockClientRegionFactory, times(1)).setEntryTimeToLive(any(ExpirationAttributes.class));
		verify(mockClientRegionFactory, times(1)).setEvictionAttributes(eq(evictionAttributes));
		verify(mockClientRegionFactory, times(1)).setInitialCapacity(eq(101));
		verify(mockClientRegionFactory, times(1)).setKeyConstraint(eq(Long.class));
		verify(mockClientRegionFactory, times(1)).setLoadFactor(eq(0.75f));
		verify(mockClientRegionFactory, never()).setPoolName(eq("TestPoolOne"));
		verify(mockClientRegionFactory, times(1)).setPoolName(eq("TestPoolTwo"));
		verify(mockClientRegionFactory, times(1)).setRegionIdleTimeout(any(ExpirationAttributes.class));
		verify(mockClientRegionFactory, times(1)).setRegionTimeToLive(any(ExpirationAttributes.class));
		verify(mockClientRegionFactory, times(2)).setStatisticsEnabled(eq(true));
		verify(mockClientRegionFactory, times(1)).setValueConstraint(eq(Number.class));
		verify(mockClientRegionFactory, times(1)).create(eq("TestRegion"));
		verify(mockRegion, never()).loadSnapshot(any(InputStream.class));
	}

	@Test
	@SuppressWarnings({ "deprecation", "unchecked" })
	public void createRegionUsingDefaultPersistentShortcut() throws Exception {

		ClientCache mockClientCache = mock(ClientCache.class);

		ClientRegionFactory<Object, Object> mockClientRegionFactory = mock(ClientRegionFactory.class);

		Pool mockPool = mock(Pool.class);

		Region<Object, Object> mockRegion = mock(Region.class);

		RegionAttributes<Object, Object> mockRegionAttributes = mock(RegionAttributes.class);

		when(mockBeanFactory.getBean(anyString(), eq(Pool.class))).thenReturn(mockPool);
		when(mockClientCache.createClientRegionFactory(eq(ClientRegionShortcut.LOCAL_PERSISTENT)))
			.thenReturn(mockClientRegionFactory);
		when(mockClientRegionFactory.create(eq("TestRegion"))).thenReturn(mockRegion);
		when(mockRegionAttributes.getPoolName()).thenReturn("TestPool");

		factoryBean.setAttributes(mockRegionAttributes);
		factoryBean.setBeanFactory(mockBeanFactory);
		factoryBean.setPersistent(true);

		Region<Object, Object> actualRegion = factoryBean.createRegion(mockClientCache, "TestRegion");

		assertThat(actualRegion).isEqualTo(mockRegion);

		verify(mockBeanFactory, times(1)).getBean(eq("TestPool"), eq(Pool.class));
		verify(mockClientCache, times(1)).createClientRegionFactory(eq(ClientRegionShortcut.LOCAL_PERSISTENT));
		verify(mockClientRegionFactory, times(1)).setPoolName(eq("TestPool"));
		verify(mockClientRegionFactory, times(1)).create(eq("TestRegion"));
		verify(mockRegionAttributes, times(1)).getPoolName();
		verify(mockRegion, never()).loadSnapshot(any(InputStream.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void createRegionWithSpecifiedShortcut() {

		ClientCache mockClientCache = mock(ClientCache.class);

		ClientRegionFactory<Object, Object> mockClientRegionFactory = mock(ClientRegionFactory.class);

		Region<Object, Object> mockRegion = mock(Region.class);

		when(mockClientCache.createClientRegionFactory(eq(ClientRegionShortcut.CACHING_PROXY)))
			.thenReturn(mockClientRegionFactory);
		when(mockClientRegionFactory.create(eq("TestRegion"))).thenReturn(mockRegion);

		factoryBean.setBeanFactory(mockBeanFactory);
		factoryBean.setShortcut(ClientRegionShortcut.CACHING_PROXY);

		Region<Object, Object> actualRegion = factoryBean.createRegion(mockClientCache, "TestRegion");

		assertThat(actualRegion).isEqualTo(mockRegion);

		verifyNoInteractions(mockBeanFactory);

		verify(mockClientCache, times(1))
			.createClientRegionFactory(eq(ClientRegionShortcut.CACHING_PROXY));

		verify(mockClientRegionFactory, times(1)).create(eq("TestRegion"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void createRegionAsSubRegion() {

		ClientCache mockClientCache = mock(ClientCache.class);

		ClientRegionFactory<Object, Object> mockClientRegionFactory = mock(ClientRegionFactory.class);

		Region<Object, Object> mockRegion = mock(Region.class, "RootRegion");
		Region<Object, Object> mockSubRegion = mock(Region.class, "SubRegion");

		when(mockClientCache.createClientRegionFactory(eq(ClientRegionShortcut.PROXY))).thenReturn(mockClientRegionFactory);
		when(mockClientRegionFactory.createSubregion(eq(mockRegion), eq("TestSubRegion"))).thenReturn(mockSubRegion);
		when(mockRegion.getName()).thenReturn("Parent");

		factoryBean.setBeanFactory(mockBeanFactory);
		factoryBean.setParent(mockRegion);
		factoryBean.setShortcut(ClientRegionShortcut.PROXY);

		Region<Object, Object> actualRegion = factoryBean.createRegion(mockClientCache, "TestSubRegion");

		assertThat(actualRegion).isEqualTo(mockSubRegion);

		verifyNoInteractions(mockBeanFactory);

		verify(mockClientCache, times(1))
			.createClientRegionFactory(eq(ClientRegionShortcut.PROXY));

		verify(mockClientRegionFactory, times(1))
			.createSubregion(eq(mockRegion), eq("TestSubRegion"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void createClientRegionFactoryFromClientCache() {

		ClientCache mockClientCache = mock(ClientCache.class);

		ClientRegionFactory mockClientRegionFactory = mock(ClientRegionFactory.class);

		when(mockClientCache.createClientRegionFactory(any(ClientRegionShortcut.class)))
			.thenReturn(mockClientRegionFactory);

		assertThat(factoryBean.createClientRegionFactory(mockClientCache, ClientRegionShortcut.CACHING_PROXY))
			.isEqualTo(mockClientRegionFactory);

		verify(mockClientCache, times(1))
			.createClientRegionFactory(eq(ClientRegionShortcut.CACHING_PROXY));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void configurePoolFromClientRegionFactoryBeanAndEagerlyInitializePool() {

		ClientRegionFactory<Object, Object> mockClientRegionFactory = mock(ClientRegionFactory.class);

		Pool mockPool = mock(Pool.class);

		when(mockBeanFactory.getBean(eq("MockPool"), eq(Pool.class))).thenReturn(mockPool);

		factoryBean.setBeanFactory(mockBeanFactory);
		factoryBean.setPoolName("MockPool");
		factoryBean.configure(mockClientRegionFactory);

		verify(mockBeanFactory, times(1)).getBean(eq("MockPool"), eq(Pool.class));
		verify(mockClientRegionFactory, times(1)).setPoolName(eq("MockPool"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void configurePoolFromClientRegionFactoryBeanEvenWhenRegionAttributesPoolNameIsSet() {

		ClientRegionFactory<Object, Object> mockClientRegionFactory = mock(ClientRegionFactory.class);

		Pool mockPool = mock(Pool.class);

		RegionAttributes<Object, Object> mockRegionAttributes = mock(RegionAttributes.class);

		when(mockBeanFactory.getBean(anyString(), eq(Pool.class))).thenReturn(mockPool);
		when(mockRegionAttributes.getPoolName()).thenReturn("TestPool");

		factoryBean.setAttributes(mockRegionAttributes);
		factoryBean.setBeanFactory(mockBeanFactory);
		factoryBean.setPoolName("MockPool");
		factoryBean.configure(mockClientRegionFactory);

		verify(mockBeanFactory, times(1)).getBean(eq("MockPool"), eq(Pool.class));
		verify(mockBeanFactory, never()).getBean(eq("TestPool"), eq(Pool.class));
		verify(mockClientRegionFactory, times(1)).setPoolName(eq("MockPool"));
		verify(mockRegionAttributes, times(1)).getPoolName();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void configurePoolFromRegionAttributesAndEagerlyInitializePool() {

		ClientRegionFactory<Object, Object> mockClientRegionFactory = mock(ClientRegionFactory.class);

		Pool mockPool = mock(Pool.class);

		RegionAttributes<Object, Object> mockRegionAttributes = mock(RegionAttributes.class);

		when(mockBeanFactory.getBean(eq("TestPool"), eq(Pool.class))).thenReturn(mockPool);
		when(mockRegionAttributes.getPoolName()).thenReturn("TestPool");

		factoryBean.setAttributes(mockRegionAttributes);
		factoryBean.setBeanFactory(mockBeanFactory);
		factoryBean.configure(mockClientRegionFactory);

		verify(mockBeanFactory, times(1)).getBean(eq("TestPool"), eq(Pool.class));
		verify(mockClientRegionFactory, times(1)).setPoolName(eq("TestPool"));
		verify(mockRegionAttributes, times(1)).getPoolName();
	}

	@Test(expected = IllegalArgumentException.class)
	@SuppressWarnings("unchecked")
	public void configurePoolThrowsExceptionWhileEagerlyInitializingPool() {

		ClientRegionFactory<Object, Object> mockClientRegionFactory = mock(ClientRegionFactory.class);

		when(mockBeanFactory.getBean(anyString(), eq(Pool.class))).thenThrow(new BeanCreationException("test"));

		factoryBean.setBeanFactory(mockBeanFactory);
		factoryBean.setPoolName("MockPool");

		try {
			factoryBean.configure(mockClientRegionFactory);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("[MockPool] is not resolvable as a Pool in the application context");
			assertThat(expected).hasNoCause();

			throw expected;
		}
		finally {
			verify(mockBeanFactory, times(1)).getBean(eq("MockPool"), eq(Pool.class));
			verify(mockClientRegionFactory, never()).setPoolName(eq("MockPool"));
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void doesNotConfigurePoolWhenClientRegionFactoryBeanPoolIsDefaultPool() {

		ClientRegionFactory<Object, Object> mockClientRegionFactory = mock(ClientRegionFactory.class);

		factoryBean.setBeanFactory(mockBeanFactory);
		factoryBean.setPoolName(ClientRegionFactoryBean.DEFAULT_POOL_NAME);
		factoryBean.configure(mockClientRegionFactory);

		assertThat(factoryBean.getPoolName().orElse(null)).isEqualTo(ClientRegionFactoryBean.DEFAULT_POOL_NAME);

		verify(mockBeanFactory, never()).getBean(anyString(), eq(Pool.class));
		verify(mockClientRegionFactory, never()).setPoolName(anyString());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void doesNotConfigurePoolWhenRegionAttributesPoolIsDefaultPool() {

		ClientRegionFactory<Object, Object> mockClientRegionFactory = mock(ClientRegionFactory.class);

		RegionAttributes<Object, Object> mockRegionAttributes = mock(RegionAttributes.class);

		when(mockRegionAttributes.getPoolName()).thenReturn(ClientRegionFactoryBean.DEFAULT_POOL_NAME);

		factoryBean.setAttributes(mockRegionAttributes);
		factoryBean.setBeanFactory(mockBeanFactory);
		factoryBean.configure(mockClientRegionFactory);

		assertThat(factoryBean.getPoolName().orElse(null)).isNull();

		verify(mockBeanFactory, never()).getBean(anyString(), eq(Pool.class));
		verify(mockClientRegionFactory, never()).setPoolName(anyString());
		verify(mockRegionAttributes, times(1)).getPoolName();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void doesNotConfigurePoolWhenDeclaredPoolIsEmpty() {

		ClientRegionFactory<Object, Object> mockClientRegionFactory = mock(ClientRegionFactory.class);

		RegionAttributes<Object, Object> mockRegionAttributes = mock(RegionAttributes.class);

		when(mockRegionAttributes.getPoolName()).thenReturn("  ");

		factoryBean.setAttributes(mockRegionAttributes);
		factoryBean.setBeanFactory(mockBeanFactory);
		factoryBean.setPoolName("");
		factoryBean.configure(mockClientRegionFactory);

		assertThat(factoryBean.getPoolName().orElse(null)).isEqualTo("");

		verify(mockBeanFactory, never()).getBean(anyString(), eq(Pool.class));
		verify(mockClientRegionFactory, never()).setPoolName(anyString());
		verify(mockRegionAttributes, times(1)).getPoolName();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void doesNotConfigurePoolWhenDeclaredPoolIsNull() {

		ClientRegionFactory<Object, Object> mockClientRegionFactory = mock(ClientRegionFactory.class);

		RegionAttributes<Object, Object> mockRegionAttributes = mock(RegionAttributes.class);

		when(mockRegionAttributes.getPoolName()).thenReturn(null);

		factoryBean.setAttributes(mockRegionAttributes);
		factoryBean.setBeanFactory(mockBeanFactory);
		factoryBean.setPoolName(null);
		factoryBean.configure(mockClientRegionFactory);

		assertThat(factoryBean.getPoolName().orElse(null)).isNull();

		verify(mockBeanFactory, never()).getBean(anyString(), eq(Pool.class));
		verify(mockClientRegionFactory, never()).setPoolName(anyString());
		verify(mockRegionAttributes, times(1)).getPoolName();
	}

	@Test
	public void isPersistentIsCorrect() {

		assertThat(factoryBean.isPersistent()).isFalse();

		factoryBean.setPersistent(false);

		assertThat(factoryBean.isPersistent()).isFalse();

		factoryBean.setPersistent(true);

		assertThat(factoryBean.isPersistent()).isTrue();
	}

	@Test
	public void isNotPersistentIsCorrect() {

		assertThat(factoryBean.isNotPersistent()).isFalse();

		factoryBean.setPersistent(true);

		assertThat(factoryBean.isNotPersistent()).isFalse();

		factoryBean.setPersistent(false);

		assertThat(factoryBean.isNotPersistent()).isTrue();
	}

	@Test
	public void testCloseDestroySettings() {

		ClientRegionFactoryBean<Object, Object> factory = new ClientRegionFactoryBean<>();

		assertThat(factory).isNotNull();
		assertThat(factory.isClose()).isFalse();
		assertThat(factory.isDestroy()).isFalse();

		factory.setClose(false);

		assertThat(factory.isClose()).isFalse();
		assertThat(factory.isDestroy()).isFalse(); // when destroy is false it remains false even when setClose(false) is called

		factory.setClose(true);

		assertThat(factory.isClose()).isTrue(); // calling setClose(true) should set close to true
		assertThat(factory.isDestroy()).isFalse();

		factory.setDestroy(false);

		assertThat(factory.isClose()).isTrue(); // calling setDestroy(false) should have no affect on close
		assertThat(factory.isDestroy()).isFalse();

		factory.setDestroy(true);

		assertThat(factory.isClose()).isFalse(); // setting destroy to true should set close to false
		assertThat(factory.isDestroy()).isTrue(); // calling setDestroy(true) should set destroy to true

		factory.setClose(false);

		assertThat(factory.isClose()).isFalse();
		assertThat(factory.isDestroy()).isTrue(); // calling setClose(false) should have no affect on destroy

		factory.setDestroy(false);

		assertThat(factory.isClose()).isFalse(); // setting destroy back to false should have no affect on close
		assertThat(factory.isDestroy()).isFalse();

		factory.setDestroy(true);

		assertThat(factory.isClose()).isFalse();
		assertThat(factory.isDestroy()).isTrue();

		factory.setClose(true);

		assertThat(factory.isClose()).isTrue();
		assertThat(factory.isDestroy()).isFalse(); // setting close to true should set destroy to false
	}

	@Test
	public void testResolveClientRegionShortcut() throws Exception {

		assertThat(TestUtils.<Object>readField("dataPolicy", factoryBean)).isNull();
		assertThat(TestUtils.<Object>readField("persistent", factoryBean)).isNull();
		assertThat(TestUtils.<Object>readField("shortcut", factoryBean)).isNull();
		assertThat(factoryBean.resolveClientRegionShortcut()).isEqualTo(ClientRegionShortcut.LOCAL);
	}

	@Test
	public void testResolveClientRegionShortcutWhenNotPersistent() throws Exception {

		factoryBean.setPersistent(false);

		assertThat(TestUtils.<Object>readField("dataPolicy", factoryBean)).isNull();
		assertThat(factoryBean.isNotPersistent()).isTrue();
		assertThat(TestUtils.<Object>readField("shortcut", factoryBean)).isNull();
		assertThat(factoryBean.resolveClientRegionShortcut()).isEqualTo(ClientRegionShortcut.LOCAL);
	}

	@Test
	public void testResolveClientRegionShortcutWhenPersistent() throws Exception {

		factoryBean.setPersistent(true);

		assertThat(TestUtils.<Object>readField("dataPolicy", factoryBean)).isNull();
		assertThat(factoryBean.isPersistent()).isTrue();
		assertThat(TestUtils.<Object>readField("shortcut", factoryBean)).isNull();
		assertThat(factoryBean.resolveClientRegionShortcut()).isEqualTo(ClientRegionShortcut.LOCAL_PERSISTENT);
	}

	@Test
	public void testResolveClientRegionShortcutUsingShortcut() throws Exception {

		factoryBean.setShortcut(ClientRegionShortcut.CACHING_PROXY_OVERFLOW);

		assertThat(TestUtils.<Object>readField("dataPolicy", factoryBean)).isNull();
		assertThat(TestUtils.<Object>readField("persistent", factoryBean)).isNull();
		assertThat(factoryBean.resolveClientRegionShortcut()).isEqualTo(ClientRegionShortcut.CACHING_PROXY_OVERFLOW);
	}

	@Test
	public void testResolveClientRegionShortcutUsingShortcutWhenNotPersistent() throws Exception {

		factoryBean.setPersistent(false);
		factoryBean.setShortcut(ClientRegionShortcut.CACHING_PROXY_HEAP_LRU);

		assertThat(TestUtils.<Object>readField("dataPolicy", factoryBean)).isNull();
		assertThat(factoryBean.isNotPersistent()).isTrue();
		assertThat(factoryBean.resolveClientRegionShortcut()).isEqualTo(ClientRegionShortcut.CACHING_PROXY_HEAP_LRU);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveClientRegionShortcutUsingShortcutWhenPersistent() throws Exception {

		try {
			factoryBean.setPersistent(true);
			factoryBean.setShortcut(ClientRegionShortcut.CACHING_PROXY);

			assertThat(TestUtils.<Object>readField("dataPolicy", factoryBean)).isNull();
			assertThat(factoryBean.isPersistent()).isTrue();

			factoryBean.resolveClientRegionShortcut();
		}
		catch (IllegalArgumentException expected) {
			assertThat(expected.getMessage())
				.isEqualTo("Client Region Shortcut [CACHING_PROXY] is not valid when persistent is true");
			throw expected;
		}
	}

	@Test
	public void testResolveClientRegionShortcutUsingPersistentShortcut() throws Exception {

		factoryBean.setShortcut(ClientRegionShortcut.LOCAL_PERSISTENT);

		assertThat(TestUtils.<Object>readField("dataPolicy", factoryBean)).isNull();
		assertThat(TestUtils.<Object>readField("persistent", factoryBean)).isNull();
		assertThat(factoryBean.resolveClientRegionShortcut()).isEqualTo(ClientRegionShortcut.LOCAL_PERSISTENT);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveClientRegionShortcutUsingPersistentShortcutWhenNotPersistent() throws Exception {

		try {
			factoryBean.setPersistent(false);
			factoryBean.setShortcut(ClientRegionShortcut.LOCAL_PERSISTENT);

			assertThat(TestUtils.<Object>readField("dataPolicy", factoryBean)).isNull();
			assertThat(factoryBean.isNotPersistent()).isTrue();

			factoryBean.resolveClientRegionShortcut();
		}
		catch (IllegalArgumentException expected) {
			assertThat(expected.getMessage())
				.isEqualTo("Client Region Shortcut [LOCAL_PERSISTENT] is not valid when persistent is false");
			throw expected;
		}
	}

	@Test
	public void testResolveClientRegionShortcutUsingPersistentShortcutWhenPersistent() throws Exception {

		factoryBean.setPersistent(true);
		factoryBean.setShortcut(ClientRegionShortcut.LOCAL_PERSISTENT_OVERFLOW);

		assertThat(TestUtils.<Object>readField("dataPolicy", factoryBean)).isNull();
		assertThat(factoryBean.isPersistent()).isTrue();
		assertThat(factoryBean.resolveClientRegionShortcut()).isEqualTo(ClientRegionShortcut.LOCAL_PERSISTENT_OVERFLOW);
	}

	@Test
	public void testResolveClientRegionShortcutUsingEmptyDataPolicy() throws Exception {

		factoryBean.setDataPolicy(DataPolicy.EMPTY);

		assertThat(TestUtils.<Object>readField("persistent", factoryBean)).isNull();
		assertThat(TestUtils.<Object>readField("shortcut", factoryBean)).isNull();
		assertThat(factoryBean.resolveClientRegionShortcut()).isEqualTo(ClientRegionShortcut.PROXY);
	}

	@Test
	public void testResolveClientRegionShortcutUsingNormalDataPolicyWhenNotPersistent() throws Exception {

		factoryBean.setDataPolicy(DataPolicy.NORMAL);
		factoryBean.setPersistent(false);

		assertThat(factoryBean.isNotPersistent()).isTrue();
		assertThat(TestUtils.<Object>readField("shortcut", factoryBean)).isNull();
		assertThat(factoryBean.resolveClientRegionShortcut()).isEqualTo(ClientRegionShortcut.CACHING_PROXY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveClientRegionShortcutUsingNormalDataPolicyWhenPersistent() throws Exception {

		try {
			factoryBean.setDataPolicy(DataPolicy.NORMAL);
			factoryBean.setPersistent(true);

			assertThat(factoryBean.isPersistent()).isTrue();
			assertThat(TestUtils.<Object>readField("shortcut", factoryBean)).isNull();

			factoryBean.resolveClientRegionShortcut();
		}
		catch (IllegalArgumentException expected) {
			assertThat(expected.getMessage()).isEqualTo("Data Policy [NORMAL] is not valid when persistent is true");
			throw expected;
		}
	}

	@Test
	public void testResolveClientRegionShortcutUsingPersistentReplicateDataPolicy() throws Exception {

		factoryBean.setDataPolicy(DataPolicy.PERSISTENT_REPLICATE);

		assertThat(TestUtils.<Object>readField("persistent", factoryBean)).isNull();
		assertThat(TestUtils.<Object>readField("shortcut", factoryBean)).isNull();
		assertThat(factoryBean.resolveClientRegionShortcut()).isEqualTo(ClientRegionShortcut.LOCAL_PERSISTENT);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveClientRegionShortcutUsingPersistentReplicateDataPolicyWhenNotPersistent() throws Exception {

		try {
			factoryBean.setDataPolicy(DataPolicy.PERSISTENT_REPLICATE);
			factoryBean.setPersistent(false);

			assertThat(factoryBean.isNotPersistent()).isTrue();
			assertThat(TestUtils.<Object>readField("shortcut", factoryBean)).isNull();

			factoryBean.resolveClientRegionShortcut();
		}
		catch (IllegalArgumentException expected) {
			assertThat(expected.getMessage())
				.isEqualTo("Data Policy [PERSISTENT_REPLICATE] is not valid when persistent is false");
			throw expected;
		}
	}

	@Test
	public void testResolveClientRegionShortcutUsingPersistentReplicateDataPolicyWhenPersistent() throws Exception {

		factoryBean.setDataPolicy(DataPolicy.PERSISTENT_REPLICATE);
		factoryBean.setPersistent(true);

		assertThat(TestUtils.<Object>readField("shortcut", factoryBean)).isNull();
		assertThat(factoryBean.isPersistent()).isTrue();
		assertThat(factoryBean.resolveClientRegionShortcut()).isEqualTo(ClientRegionShortcut.LOCAL_PERSISTENT);
	}

	private <K> Interest<K> newInterest(K key) {
		return new Interest<>(key);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void destroyCallsRegionClose() throws Exception {

		Region mockRegion = mock(Region.class, "MockRegion");

		RegionService mockRegionService = mock(RegionService.class, "MockRegionService");

		when(mockRegion.getRegionService()).thenReturn(mockRegionService);
		when(mockRegionService.isClosed()).thenReturn(false);

		doReturn(mockRegion).when(factoryBean).getObject();

		factoryBean.setClose(true);
		factoryBean.setInterests(ArrayUtils.asArray(newInterest("test")));

		assertThat(factoryBean.isClose()).isTrue();
		assertThat(factoryBean.isDestroy()).isFalse();
		assertThat(factoryBean.getInterests()).isNotNull();
		assertThat(factoryBean.getInterests()).hasSize(1);

		factoryBean.destroy();

		verify(factoryBean, times(1)).getObject();
		verify(mockRegion, times(1)).getRegionService();
		verify(mockRegionService, times(1)).isClosed();
		verify(mockRegion, times(1)).close();
		verify(mockRegion, never()).destroyRegion();
		verify(mockRegion, never()).unregisterInterest(any());
		verify(mockRegion, never()).unregisterInterestRegex(anyString());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void destroyCallsRegionDestroy() throws Exception {

		Region mockRegion = mock(Region.class, withSettings().lenient());

		RegionService mockRegionService = mock(RegionService.class);

		when(mockRegion.getRegionService()).thenReturn(mockRegionService);

		doReturn(mockRegion).when(factoryBean).getObject();

		factoryBean.setClose(false);
		factoryBean.setDestroy(true);
		factoryBean.setInterests(ArrayUtils.asArray(newInterest("test")));

		assertThat(factoryBean.isClose()).isFalse();
		assertThat(factoryBean.isDestroy()).isTrue();
		assertThat(factoryBean.getInterests()).isNotNull();
		assertThat(factoryBean.getInterests()).hasSize(1);

		factoryBean.destroy();

		verify(factoryBean, times(1)).getObject();
		verify(mockRegion, never()).getRegionService();
		verify(mockRegionService, never()).isClosed();
		verify(mockRegion, never()).close();
		verify(mockRegion, times(1)).destroyRegion();
		verify(mockRegion, never()).unregisterInterest(any());
		verify(mockRegion, never()).unregisterInterestRegex(anyString());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void destroyDoesNothingWhenClientRegionFactoryBeanCloseIsTrueButRegionServiceIsClosed() throws Exception {

		Region mockRegion = mock(Region.class, "MockRegion");

		RegionService mockRegionService = mock(RegionService.class, "MockRegionService");

		when(mockRegion.getRegionService()).thenReturn(mockRegionService);
		when(mockRegionService.isClosed()).thenReturn(true);

		doReturn(mockRegion).when(factoryBean).getObject();

		factoryBean.setClose(true);
		factoryBean.setInterests(ArrayUtils.asArray(newInterest("test")));

		assertThat(factoryBean.isClose()).isTrue();
		assertThat(factoryBean.isDestroy()).isFalse();
		assertThat(factoryBean.getInterests()).isNotNull();
		assertThat(factoryBean.getInterests()).hasSize(1);

		factoryBean.destroy();

		verify(factoryBean, times(1)).getObject();
		verify(mockRegion, times(1)).getRegionService();
		verify(mockRegionService, times(1)).isClosed();
		verify(mockRegion, never()).close();
		verify(mockRegion, never()).destroyRegion();
		verify(mockRegion, never()).unregisterInterest(any());
		verify(mockRegion, never()).unregisterInterestRegex(anyString());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void destroyDoesNothingWhenClientRegionFactoryBeanCloseAndDestroyAreFalse() throws Exception {

		Region mockRegion = mock(Region.class, "MockRegion");

		doReturn(mockRegion).when(factoryBean).getObject();

		factoryBean.destroy();

		verify(factoryBean, times(1)).getObject();
		verify(mockRegion, never()).getRegionService();
		verify(mockRegion, never()).close();
		verify(mockRegion, never()).destroyRegion();
		verify(mockRegion, never()).unregisterInterest(any());
		verify(mockRegion, never()).unregisterInterestRegex(anyString());
	}

	@Test
	public void destroyDoesNothingWhenRegionIsNull() throws Exception {

		doReturn(null).when(factoryBean).getObject();

		factoryBean.destroy();

		verify(factoryBean, times(1)).getObject();
	}
}
