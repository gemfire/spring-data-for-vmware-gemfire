/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client;

import org.junit.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.gemfire.TestUtils;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudPoolFactory;
import org.springframework.data.gemfire.gud.api.GudQueryService;
import org.springframework.data.gemfire.gud.api.GudSocketFactory;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.data.util.ReflectionUtils;

import java.net.InetSocketAddress;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalStateException;

/**
 * Unit Tests for {@link PoolFactoryBean}.
 *
 * @author John Blum
 * @see java.net.InetSocketAddress
 * @see org.junit.Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.client.GudClientCache
 * @see org.apache.geode.cache.client.GudPool
 * @see org.apache.geode.cache.client.GudPoolFactory
 * @see org.apache.geode.cache.client.GudSocketFactory
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.data.gemfire.client.PoolFactoryBean
 * @see org.springframework.data.gemfire.client.PoolResolver
 * @see org.springframework.data.gemfire.support.ConnectionEndpoint
 * @since 1.7.0
 */
public class PoolFactoryBeanUnitTests {

	private PoolFactoryBean newPoolFactoryBean() {
		return new PoolFactoryBean() {
			@Override
			protected GudPoolFactory createPoolFactory() {
				throw new UnsupportedOperationException("stub");
			}

			@Override
			protected boolean isClientCachePresent() {
				return false;
			}
		};
	}

	private ConnectionEndpoint newConnectionEndpoint(String host, int port) {
		return new ConnectionEndpoint(host, port);
	}

	private InetSocketAddress newSocketAddress(String host, int port) {
		return new InetSocketAddress(host, port);
	}

	@Test
	public void afterPropertiesSetCreatesPool() throws Exception {

		BeanFactory mockBeanFactory = mock(BeanFactory.class);

		GudPool mockPool = mock(GudPool.class);

		GudPoolFactory mockPoolFactory = mock(GudPoolFactory.class);

		PoolResolver mockPoolResolver = mock(PoolResolver.class);

		GudSocketFactory mockSocketFactory = mock(GudSocketFactory.class);

		doReturn(mockPool).when(mockPoolFactory).create(eq("GemFirePool"));
		doReturn(null).when(mockPoolResolver).resolve(anyString());

		PoolFactoryBean poolFactoryBean = spy(newPoolFactoryBean());

		doReturn(mockPoolFactory).when(poolFactoryBean).createPoolFactory();
		doReturn(false).when(poolFactoryBean).isClientCachePresent();

		poolFactoryBean.setBeanFactory(mockBeanFactory);
		poolFactoryBean.setBeanName("GemFirePool");
		poolFactoryBean.setName(null);
		poolFactoryBean.setFreeConnectionTimeout(60000);
		poolFactoryBean.setIdleTimeout(120000L);
		poolFactoryBean.setKeepAlive(false);
		poolFactoryBean.setLoadConditioningInterval(15000);
		poolFactoryBean.setLocators(Collections.singletonList(newConnectionEndpoint("localhost", 54321)));
		poolFactoryBean.setMaxConnections(50);
		poolFactoryBean.setMinConnections(5);
		poolFactoryBean.setMaxConnectionsPerServer(10);
		poolFactoryBean.setMinConnectionsPerServer(1);
		poolFactoryBean.setMultiUserAuthentication(false);
		poolFactoryBean.setPingInterval(5000L);
		poolFactoryBean.setPoolResolver(mockPoolResolver);
		poolFactoryBean.setPrSingleHopEnabled(true);
		poolFactoryBean.setReadTimeout(30000);
		poolFactoryBean.setRetryAttempts(10);
		poolFactoryBean.setServerConnectionTimeout(10000);
		poolFactoryBean.setServerGroup("TestServerGroup");
		poolFactoryBean.setServers(Collections.singletonList(newConnectionEndpoint("localhost", 12345)));
		poolFactoryBean.setSocketBufferSize(32768);
		poolFactoryBean.setSocketConnectTimeout(5000);
		poolFactoryBean.setSocketFactory(mockSocketFactory);
		poolFactoryBean.setStatisticInterval(1000);
		poolFactoryBean.setSubscriptionAckInterval(500);
		poolFactoryBean.setSubscriptionEnabled(true);
		poolFactoryBean.setSubscriptionMessageTrackingTimeout(20000);
		poolFactoryBean.setSubscriptionRedundancy(2);
		poolFactoryBean.afterPropertiesSet();

		assertThat(poolFactoryBean.getBeanFactory()).isEqualTo(mockBeanFactory);
		assertThat(poolFactoryBean.getObject()).isSameAs(mockPool);
		assertThat(poolFactoryBean.getPoolResolver()).isSameAs(mockPoolResolver);

		verify(mockBeanFactory, times(1)).getBean(eq(GudClientCache.class));
		verify(mockPoolFactory, times(1)).setFreeConnectionTimeout(eq(60000));
		verify(mockPoolFactory, times(1)).setIdleTimeout(eq(120000L));
		verify(mockPoolFactory, times(1)).setLoadConditioningInterval(eq(15000));
		verify(mockPoolFactory, times(1)).setMaxConnections(eq(50));
		verify(mockPoolFactory, times(1)).setMinConnections(eq(5));
		verify(mockPoolFactory, times(1)).setMaxConnectionsPerServer(eq(10));
		verify(mockPoolFactory, times(1)).setMinConnectionsPerServer(eq(1));
		verify(mockPoolFactory, times(1)).setMultiuserAuthentication(eq(false));
		verify(mockPoolFactory, times(1)).setPingInterval(eq(5000L));
		verify(mockPoolFactory, times(1)).setPRSingleHopEnabled(eq(true));
		verify(mockPoolFactory, times(1)).setReadTimeout(eq(30000));
		verify(mockPoolFactory, times(1)).setRetryAttempts(eq(10));
		verify(mockPoolFactory, times(1)).setServerConnectionTimeout(eq(10000));
		verify(mockPoolFactory, times(1)).setServerGroup(eq("TestServerGroup"));
		verify(mockPoolFactory, times(1)).setSocketBufferSize(eq(32768));
		verify(mockPoolFactory, times(1)).setSocketConnectTimeout(eq(5000));
		verify(mockPoolFactory, times(1)).setSocketFactory(eq(mockSocketFactory));
		verify(mockPoolFactory, times(1)).setStatisticInterval(eq(1000));
		verify(mockPoolFactory, times(1)).setSubscriptionAckInterval(eq(500));
		verify(mockPoolFactory, times(1)).setSubscriptionEnabled(eq(true));
		verify(mockPoolFactory, times(1)).setSubscriptionMessageTrackingTimeout(eq(20000));
		verify(mockPoolFactory, times(1)).setSubscriptionRedundancy(eq(2));
		verify(mockPoolFactory, times(1)).addLocator(eq("localhost"), eq(54321));
		verify(mockPoolFactory, times(1)).addServer(eq("localhost"), eq(12345));
		verify(mockPoolFactory, times(1)).create(eq("GemFirePool"));
		verify(mockPoolResolver, times(2)).resolve(eq("GemFirePool"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void afterPropertiesSetWithUnspecifiedName() throws Exception {

		PoolFactoryBean poolFactoryBean = newPoolFactoryBean();

		poolFactoryBean.setBeanName(null);
		poolFactoryBean.setName(null);

		assertThat(poolFactoryBean.getBeanName()).isNull();
		assertThat(poolFactoryBean.getName()).isNull();

		try {
			poolFactoryBean.afterPropertiesSet();
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("Pool name is required");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void afterPropertiesSetUsesName() throws Exception {

		PoolFactoryBean poolFactoryBean = newPoolFactoryBean();

		poolFactoryBean.setBeanName("gemfirePool");
		poolFactoryBean.setName("TestPool");

		assertThat(poolFactoryBean.getBeanName()).isEqualTo("gemfirePool");
		assertThat(poolFactoryBean.getName()).isEqualTo("TestPool");

		poolFactoryBean.afterPropertiesSet();

		assertThat(poolFactoryBean.getName()).isEqualTo("TestPool");
	}

	@Test
	public void afterPropertiesSetDefaultsToBeanName() throws Exception {

		PoolFactoryBean poolFactoryBean = newPoolFactoryBean();

		poolFactoryBean.setBeanName("swimPool");

		assertThat(poolFactoryBean.getBeanName()).isEqualTo("swimPool");
		assertThat(poolFactoryBean.getName()).isNull();

		poolFactoryBean.afterPropertiesSet();

		assertThat(poolFactoryBean.getName()).isEqualTo("swimPool");
	}

	@Test
	public void destroyDestroysPool() throws Exception {

		GudPool mockPool = mock(GudPool.class);

		doReturn(false).when(mockPool).isDestroyed();

		PoolFactoryBean poolFactoryBean = newPoolFactoryBean();

		poolFactoryBean.setPool(mockPool);
		poolFactoryBean.destroy();

		assertThat(TestUtils.<GudPool>readField("pool", poolFactoryBean)).isNull();

		InOrder order = inOrder(mockPool);

		order.verify(mockPool, times(1)).isDestroyed();
		order.verify(mockPool, times(1)).destroy(eq(false));
		order.verify(mockPool, times(1)).getName();

		verifyNoMoreInteractions(mockPool);
	}

	@Test
	public void destroyNonSpringManagedPool() throws Exception {

		GudPool mockPool = mock(GudPool.class);

		PoolFactoryBean poolFactoryBean = newPoolFactoryBean();

		ReflectionUtils.setField(PoolFactoryBean.class.getDeclaredField("springManagedPool"), poolFactoryBean, false);
		poolFactoryBean.setPool(mockPool);
		poolFactoryBean.destroy();

		verifyNoInteractions(mockPool);
	}

	@Test
	public void destroyUninitializedPool() {

		PoolFactoryBean poolFactoryBean = newPoolFactoryBean();

		poolFactoryBean.setPool(null);
		poolFactoryBean.destroy();
	}

	@Test
	public void getObjectTypeEqualsPoolClass() {
		assertThat(newPoolFactoryBean().getObjectType()).isEqualTo(GudPool.class);
	}

	@Test
	public void getObjectTypeEqualsPoolInstanceType() {

		GudPool mockPool = mock(GudPool.class);

		PoolFactoryBean poolFactoryBean = newPoolFactoryBean();

		poolFactoryBean.setPool(mockPool);

		assertThat(poolFactoryBean.getObjectType()).isEqualTo(mockPool.getClass());
	}

	@Test
	public void isSingleton() {
		assertThat(newPoolFactoryBean().isSingleton()).isTrue();
	}

	@Test
	public void addGetAndSetLocators() {

		PoolFactoryBean poolFactoryBean = newPoolFactoryBean();

		assertThat(poolFactoryBean.getLocators()).isNotNull();
		assertThat(poolFactoryBean.getLocators().isEmpty()).isTrue();

		ConnectionEndpoint localhost = newConnectionEndpoint("localhost", 21668);

		poolFactoryBean.addLocators(localhost);

		assertThat(poolFactoryBean.getLocators().size()).isEqualTo(1);
		assertThat(poolFactoryBean.getLocators().findOne("localhost")).isEqualTo(localhost);

		ConnectionEndpoint skullbox = newConnectionEndpoint("skullbox", 10334);
		ConnectionEndpoint boombox = newConnectionEndpoint("boombox", 10334);

		poolFactoryBean.addLocators(skullbox, boombox);

		assertThat(poolFactoryBean.getLocators().size()).isEqualTo(3);
		assertThat(poolFactoryBean.getLocators().findOne("localhost")).isEqualTo(localhost);
		assertThat(poolFactoryBean.getLocators().findOne("skullbox")).isEqualTo(skullbox);
		assertThat(poolFactoryBean.getLocators().findOne("boombox")).isEqualTo(boombox);

		poolFactoryBean.setLocators(ArrayUtils.asArray(localhost));

		assertThat(poolFactoryBean.getLocators().size()).isEqualTo(1);
		assertThat(poolFactoryBean.getLocators().findOne("localhost")).isEqualTo(localhost);

		poolFactoryBean.setLocators(Collections.emptyList());

		assertThat(poolFactoryBean.getLocators()).isNotNull();
		assertThat(poolFactoryBean.getLocators().isEmpty()).isTrue();
	}

	@Test
	public void addGetAndSetServers() {

		PoolFactoryBean poolFactoryBean = newPoolFactoryBean();

		assertThat(poolFactoryBean.getServers()).isNotNull();
		assertThat(poolFactoryBean.getServers().isEmpty()).isTrue();

		ConnectionEndpoint localhost = newConnectionEndpoint("localhost", 21668);

		poolFactoryBean.addServers(localhost);

		assertThat(poolFactoryBean.getServers().size()).isEqualTo(1);
		assertThat(poolFactoryBean.getServers().findOne("localhost")).isEqualTo(localhost);

		ConnectionEndpoint skullbox = newConnectionEndpoint("skullbox", 10334);
		ConnectionEndpoint boombox = newConnectionEndpoint("boombox", 10334);

		poolFactoryBean.addServers(skullbox, boombox);

		assertThat(poolFactoryBean.getServers().size()).isEqualTo(3);
		assertThat(poolFactoryBean.getServers().findOne("localhost")).isEqualTo(localhost);
		assertThat(poolFactoryBean.getServers().findOne("skullbox")).isEqualTo(skullbox);
		assertThat(poolFactoryBean.getServers().findOne("boombox")).isEqualTo(boombox);

		poolFactoryBean.setServers(ArrayUtils.asArray(localhost));

		assertThat(poolFactoryBean.getServers().size()).isEqualTo(1);
		assertThat(poolFactoryBean.getServers().findOne("localhost")).isEqualTo(localhost);

		poolFactoryBean.setServers(Collections.emptyList());

		assertThat(poolFactoryBean.getServers()).isNotNull();
		assertThat(poolFactoryBean.getServers().isEmpty()).isTrue();
	}

	@Test
	public void getPoolWhenPoolIsSet() {

		GudPool mockPool = mock(GudPool.class);

		PoolFactoryBean poolFactoryBean = newPoolFactoryBean();

		poolFactoryBean.setPool(mockPool);

		assertThat(poolFactoryBean.getPool()).isSameAs(mockPool);
	}

	@Test
	public void getPoolWhenPoolIsUnset() {

		GudSocketFactory mockSocketFactory = mock(GudSocketFactory.class);

		PoolFactoryBean poolFactoryBean = newPoolFactoryBean();

		poolFactoryBean.setFreeConnectionTimeout(5000);
		poolFactoryBean.setIdleTimeout(120000L);
		poolFactoryBean.setLoadConditioningInterval(300000);
		poolFactoryBean.setLocators(ArrayUtils.asArray(newConnectionEndpoint("skullbox", 11235)));
		poolFactoryBean.setMaxConnections(500);
		poolFactoryBean.setMinConnections(50);
		poolFactoryBean.setMaxConnectionsPerServer(50);
		poolFactoryBean.setMinConnectionsPerServer(5);
		poolFactoryBean.setMultiUserAuthentication(true);
		poolFactoryBean.setPingInterval(15000L);
		poolFactoryBean.setPrSingleHopEnabled(true);
		poolFactoryBean.setReadTimeout(30000);
		poolFactoryBean.setRetryAttempts(1);
		poolFactoryBean.setServerConnectionTimeout(10000);
		poolFactoryBean.setServerGroup("TestGroup");
		poolFactoryBean.setServers(ArrayUtils.asArray(newConnectionEndpoint("boombox", 12480)));
		poolFactoryBean.setSocketBufferSize(16384);
		poolFactoryBean.setSocketConnectTimeout(5000);
		poolFactoryBean.setSocketFactory(mockSocketFactory);
		poolFactoryBean.setStatisticInterval(500);
		poolFactoryBean.setSubscriptionAckInterval(200);
		poolFactoryBean.setSubscriptionEnabled(true);
		poolFactoryBean.setSubscriptionMessageTrackingTimeout(20000);
		poolFactoryBean.setSubscriptionRedundancy(2);
		poolFactoryBean.setSubscriptionTimeoutMultiplier(4);

		GudPool pool = poolFactoryBean.getPool();

		assertThat(pool).isInstanceOf(PoolAdapter.class);
		assertThat(pool.isDestroyed()).isFalse();
		assertThat(pool.getFreeConnectionTimeout()).isEqualTo(5000);
		assertThat(pool.getIdleTimeout()).isEqualTo(120000L);
		assertThat(pool.getLoadConditioningInterval()).isEqualTo(300000);
		assertThat(pool.getLocators()).isEqualTo(Collections.singletonList(newSocketAddress("skullbox", 11235)));
		assertThat(pool.getMaxConnections()).isEqualTo(500);
		assertThat(pool.getMinConnections()).isEqualTo(50);
		assertThat(pool.getMaxConnectionsPerServer()).isEqualTo(50);
		assertThat(pool.getMinConnectionsPerServer()).isEqualTo(5);
		assertThat(pool.getMultiuserAuthentication()).isTrue();
		assertThat(pool.getName()).isNull();
		assertThat(pool.getPingInterval()).isEqualTo(15000L);
		assertThat(pool.getPRSingleHopEnabled()).isTrue();
		assertThat(pool.getReadTimeout()).isEqualTo(30000);
		assertThat(pool.getRetryAttempts()).isEqualTo(1);
		assertThat(pool.getServerConnectionTimeout()).isEqualTo(10000);
		assertThat(pool.getServerGroup()).isEqualTo("TestGroup");
		assertThat(pool.getServers()).isEqualTo(Collections.singletonList(newSocketAddress("boombox", 12480)));
		assertThat(pool.getSocketBufferSize()).isEqualTo(16384);
		assertThat(pool.getSocketConnectTimeout()).isEqualTo(5000);
		assertThat(pool.getSocketFactory()).isEqualTo(mockSocketFactory);
		assertThat(pool.getStatisticInterval()).isEqualTo(500);
		assertThat(pool.getSubscriptionAckInterval()).isEqualTo(200);
		assertThat(pool.getSubscriptionEnabled()).isTrue();
		assertThat(pool.getSubscriptionMessageTrackingTimeout()).isEqualTo(20000);
		assertThat(pool.getSubscriptionRedundancy()).isEqualTo(2);
		assertThat(pool.getSubscriptionTimeoutMultiplier()).isEqualTo(4);
	}

	@Test
	public void getPoolNameWhenBeanNameSet() {

		PoolFactoryBean poolFactoryBean = newPoolFactoryBean();

		poolFactoryBean.setBeanName("PoolBean");
		poolFactoryBean.setName(null);

		assertThat(poolFactoryBean.getPool().getName()).isEqualTo("PoolBean");
	}

	@Test
	public void getPoolNameWhenBeanNameAndNameSet() {

		PoolFactoryBean poolFactoryBean = newPoolFactoryBean();

		poolFactoryBean.setBeanName("PoolBean");
		poolFactoryBean.setName("TestPool");

		assertThat(poolFactoryBean.getPool().getName()).isEqualTo("TestPool");
	}

	@Test
	public void getPoolPendingEventCountWithPool() {

		GudPool mockPool = mock(GudPool.class);

		when(mockPool.getPendingEventCount()).thenReturn(2);

		PoolFactoryBean poolFactoryBean = newPoolFactoryBean();

		GudPool pool = poolFactoryBean.getPool();

		assertThat(pool).isNotSameAs(mockPool);
		assertThat(pool).isInstanceOf(PoolAdapter.class);

		poolFactoryBean.setPool(mockPool);

		assertThat(pool.getPendingEventCount()).isEqualTo(2);

		verify(mockPool, times(1)).getPendingEventCount();
	}

	@Test(expected = IllegalStateException.class)
	public void getPoolPendingEventCountWithoutPoolThrowsIllegalStateException() {

		try {
			newPoolFactoryBean().getPool().getPendingEventCount();
		}
		catch (IllegalStateException expected) {

			assertThat(expected).hasMessage("Pool [null] has not been initialized");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void getPoolQueryServiceWithPool() {

		GudPool mockPool = mock(GudPool.class);

		GudQueryService mockQueryService = mock(GudQueryService.class);

		when(mockPool.getQueryService()).thenReturn(mockQueryService);

		PoolFactoryBean poolFactoryBean = newPoolFactoryBean();
		GudPool pool = poolFactoryBean.getPool();

		assertThat(pool).isNotSameAs(mockPool);
		assertThat(pool).isInstanceOf(PoolAdapter.class);

		poolFactoryBean.setPool(mockPool);

		assertThat(pool.getQueryService()).isEqualTo(mockQueryService);

		verify(mockPool, times(1)).getQueryService();
	}

	@Test(expected = IllegalStateException.class)
	public void getPoolQueryServiceWithoutPoolThrowsIllegalStateException() {

		try {
			newPoolFactoryBean().getPool().getQueryService();
		}
		catch (IllegalStateException expected) {

			assertThat(expected).hasMessage("Pool [null] has not been initialized");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void getPoolAndDestroyWithPool() {

		GudPool mockPool = mock(GudPool.class);

		PoolFactoryBean poolFactoryBean = spy(newPoolFactoryBean());

		doThrow(newIllegalStateException("test")).when(poolFactoryBean).destroy();

		GudPool pool = poolFactoryBean.getPool();

		assertThat(pool).isNotSameAs(mockPool);
		assertThat(pool).isInstanceOf(PoolAdapter.class);

		poolFactoryBean.setPool(mockPool);
		pool.destroy();
		pool.destroy(true);

		verify(mockPool, times(1)).destroy(eq(false));
		verify(mockPool, times(1)).destroy(eq(true));
	}

	@Test
	public void getPoolAndDestroyWithoutPool() {

		PoolFactoryBean poolFactoryBean = spy(newPoolFactoryBean());

		doThrow(newIllegalStateException("test")).when(poolFactoryBean).destroy();

		GudPool pool = poolFactoryBean.getPool();

		assertThat(pool).isInstanceOf(PoolAdapter.class);

		pool.destroy();
		pool.destroy(true);

		verify(poolFactoryBean, times(2)).destroy();
	}

	@Test
	public void setAndGetPoolResolver() {

		PoolResolver mockPoolResolver = mock(PoolResolver.class);

		PoolFactoryBean poolFactoryBean = newPoolFactoryBean();

		assertThat(poolFactoryBean.getPoolResolver()).isEqualTo(PoolFactoryBean.DEFAULT_POOL_RESOLVER);

		poolFactoryBean.setPoolResolver(mockPoolResolver);

		assertThat(poolFactoryBean.getPoolResolver()).isSameAs(mockPoolResolver);

		poolFactoryBean.setPoolResolver(null);

		assertThat(poolFactoryBean.getPoolResolver()).isEqualTo(PoolFactoryBean.DEFAULT_POOL_RESOLVER);
	}

	@Test
	public void setAndGetSocketFactory() {

		GudSocketFactory mockSocketFactory = mock(GudSocketFactory.class);

		PoolFactoryBean poolFactoryBean = newPoolFactoryBean();

		assertThat(poolFactoryBean.getSocketFactory()).isEqualTo(GudPoolFactory.DEFAULT_SOCKET_FACTORY);

		poolFactoryBean.setSocketFactory(mockSocketFactory);

		assertThat(poolFactoryBean.getSocketFactory()).isEqualTo(mockSocketFactory);

		poolFactoryBean.setSocketFactory(null);

		assertThat(poolFactoryBean.getSocketFactory()).isEqualTo(GudPoolFactory.DEFAULT_SOCKET_FACTORY);

		verifyNoInteractions(mockSocketFactory);
	}
}
