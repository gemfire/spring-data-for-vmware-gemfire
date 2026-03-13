/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */
package org.springframework.data.gemfire.tests.mock.beans.factory.config;

import static org.mockito.Mockito.doReturn;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.data.gemfire.gud.api.GudClientCacheFactory;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudPoolFactory;
import org.springframework.data.gemfire.gud.api.GudDistributedSystem;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.client.PoolFactoryBean;
import org.springframework.data.gemfire.tests.mock.GemFireMockObjectsSupport;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * A Spring {@link BeanPostProcessor} implementation that applies mocks and spies to
 * Spring Data GemFire / Spring Data Geode (SDG) and Apache Geode / VMware GemFire
 * {@link Object objects}.
 *
 * @author John Blum
 * @see Properties
 * @see GudClientCacheFactory
 * @see GudClientCache
 * @see GudClientCacheFactory
 * @see GudPoolFactory
 * @see GudDistributedSystem
 * @see BeanPostProcessor
 * @see ClientCacheFactoryBean
 * @see ClientCacheFactoryBean
 * @see PoolFactoryBean
 * @see GemFireMockObjectsSupport
 * @since 0.0.1
 */
public class GemFireMockObjectsBeanPostProcessor implements BeanPostProcessor {

	protected static final boolean DEFAULT_USE_SINGLETON_CACHE = false;

	protected static final String GEMFIRE_PROPERTIES_BEAN_NAME = "gemfireProperties";

	private volatile boolean useSingletonCache;

	private final AtomicReference<Properties> gemfireProperties = new AtomicReference<>(new Properties());

	/**
	 * Factory method used to construct a new instance of {@link GemFireMockObjectsBeanPostProcessor} initialized to
	 * use a non-Singleton cache.
	 *
	 * @return a new instance of {@link GemFireMockObjectsBeanPostProcessor}.
	 * @see #newInstance(boolean)
	 */
	public static @NonNull GemFireMockObjectsBeanPostProcessor newInstance() {
		return newInstance(DEFAULT_USE_SINGLETON_CACHE);
	}

	/**
	 * Factory method used to construct a new instance of {@link GemFireMockObjectsBeanPostProcessor} initialized with
	 * the given boolean parameter to configure the use of a non/Singleton cache.
	 *
	 * @param useSingletonCache boolean value indicating whether to use a Singleton cache.
	 * @return a new instance of {@link GemFireMockObjectsBeanPostProcessor}.
	 */
	public static @NonNull GemFireMockObjectsBeanPostProcessor newInstance(boolean useSingletonCache) {

		GemFireMockObjectsBeanPostProcessor beanPostProcessor = new GemFireMockObjectsBeanPostProcessor();

		beanPostProcessor.useSingletonCache = useSingletonCache;

		return beanPostProcessor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public @Nullable Object postProcessBeforeInitialization(@Nullable Object bean, @NonNull String beanName)
			throws BeansException {

		return isGemFireProperties(bean, beanName) ? set((Properties) bean)
			: isCacheFactoryBean(bean) ? spyOnCacheFactoryBean((ClientCacheFactoryBean) bean, isUsingSingletonCache())
			: isPoolFactoryBean(bean) ? mockPoolFactoryBean((PoolFactoryBean) bean)
			: bean;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public @Nullable Object postProcessAfterInitialization(@Nullable Object bean, @NonNull String beanName)
			throws BeansException {

		if (bean instanceof GudClientCache) {

			GudClientCache gemfireCache = (GudClientCache) bean;

			GudDistributedSystem distributedSystem = gemfireCache.getDistributedSystem();

			Properties distributedSystemProperties = distributedSystem.getProperties();

			if (distributedSystemProperties != null) {
				distributedSystemProperties.putAll(getGemFireProperties());
			}
			else {
				doReturn(getGemFireProperties()).when(distributedSystem).getProperties();
			}
		}

		return bean;
	}

	private boolean isCacheFactoryBean(@Nullable Object bean) {
		return bean instanceof ClientCacheFactoryBean;
	}

	private boolean isGemFireProperties(@Nullable Object bean, @Nullable String beanName) {
		return bean instanceof Properties && GEMFIRE_PROPERTIES_BEAN_NAME.equals(beanName);
	}

	private boolean isPoolFactoryBean(@Nullable Object bean) {
		return bean instanceof PoolFactoryBean;
	}

	protected boolean isUsingSingletonCache() {
		return this.useSingletonCache;
	}

	private @Nullable Object set(@Nullable Properties gemfireProperties) {

		this.gemfireProperties.set(gemfireProperties);

		return gemfireProperties;
	}

	protected @Nullable Properties getGemFireProperties() {
		return this.gemfireProperties.get();
	}

	private @NonNull Object spyOnCacheFactoryBean(@NonNull ClientCacheFactoryBean bean, boolean useSingletonCache) {

		return bean instanceof ClientCacheFactoryBean
			? SpyingGudClientCacheFactoryInitializer.spyOn((ClientCacheFactoryBean) bean, useSingletonCache)
			: SpyingCacheFactoryInitializer.spyOn(bean, useSingletonCache);
	}

	private @NonNull Object mockPoolFactoryBean(@NonNull PoolFactoryBean bean) {
		return MockingPoolFactoryInitializer.mock(bean);
	}

	protected static class SpyingCacheFactoryInitializer
			implements ClientCacheFactoryBean.CacheFactoryInitializer<GudClientCacheFactory> {

		protected static ClientCacheFactoryBean spyOn(ClientCacheFactoryBean cacheFactoryBean, boolean useSingletonCache) {

			cacheFactoryBean.setCacheFactoryInitializer(new SpyingCacheFactoryInitializer(useSingletonCache));

			return cacheFactoryBean;
		}

		private final boolean useSingletonCache;

		protected SpyingCacheFactoryInitializer(boolean useSingletonCache) {
			this.useSingletonCache = useSingletonCache;
		}

		protected boolean isUsingSingletonCache() {
			return this.useSingletonCache;
		}

		@Override
		public GudClientCacheFactory initialize(GudClientCacheFactory cacheFactory) {
			return GemFireMockObjectsSupport.spyOn(cacheFactory, isUsingSingletonCache());
		}
	}

	protected static class SpyingGudClientCacheFactoryInitializer
			implements ClientCacheFactoryBean.CacheFactoryInitializer<GudClientCacheFactory> {

		protected static ClientCacheFactoryBean spyOn(ClientCacheFactoryBean clientCacheFactoryBean,
				boolean useSingletonCache) {

			clientCacheFactoryBean
				.setCacheFactoryInitializer(new SpyingGudClientCacheFactoryInitializer(useSingletonCache));

			return clientCacheFactoryBean;
		}

		private final boolean useSingletonCache;

		protected SpyingGudClientCacheFactoryInitializer(boolean useSingletonCache) {
			this.useSingletonCache = useSingletonCache;
		}

		protected boolean isUsingSingletonCache() {
			return this.useSingletonCache;
		}

		@Override
		public GudClientCacheFactory initialize(GudClientCacheFactory clientCacheFactory) {
			return GemFireMockObjectsSupport.spyOn(clientCacheFactory, isUsingSingletonCache());
		}
	}

	protected static class MockingPoolFactoryInitializer implements PoolFactoryBean.PoolFactoryInitializer {

		protected static PoolFactoryBean mock(PoolFactoryBean poolFactoryBean) {

			poolFactoryBean.setPoolFactoryInitializer(new MockingPoolFactoryInitializer());

			return poolFactoryBean;
		}

		@Override
		public GudPoolFactory initialize(GudPoolFactory poolFactory) {
			return GemFireMockObjectsSupport.mockGudPoolFactory();
		}
	}
}
