/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.springframework.data.gemfire.GemfireUtils.apacheGeodeProductName;
import static org.springframework.data.gemfire.GemfireUtils.apacheGeodeVersion;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newRuntimeException;
import java.util.Optional;
import java.util.Properties;
import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.distributed.DistributedSystem;
import org.springframework.lang.NonNull;

/**
 * Abstract base class encapsulating logic to resolve or create a {@link ClientCache} instance.
 *
 * @author John Blum
 * @see Optional
 * @see Properties
 * @see ClientCache
 * @see org.apache.geode.distributed.DistributedMember
 * @see DistributedSystem
 * @see AbstractConfigurableCacheFactoryBean
 * @since 2.5.0
 */
public abstract class AbstractResolvableCacheFactoryBean extends AbstractConfigurableCacheFactoryBean {

	private volatile String cacheResolutionMessagePrefix;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ClientCache doGetObject() {
		return init();
	}

	/**
	 * Initializes a {@link ClientCache}.
	 *
	 * @return a reference to the initialized {@link ClientCache}.
	 * @see ClientCache
	 * @see #setCache(ClientCache)
	 * @see #resolveCache()
	 * @see #getCache()
	 */
	protected ClientCache init() {

		ClassLoader currentThreadContextClassLoader = Thread.currentThread().getContextClassLoader();

		try {
			// Use Spring Bean ClassLoader to load Spring configured Apache Geode classes
			Thread.currentThread().setContextClassLoader(getBeanClassLoader());

			setCache(resolveCache());

			logCacheInitialization();

			return getCache();
		}
		catch (Exception cause) {
			throw newRuntimeException(cause, "Error occurred while initializing the cache");
		}
		finally {
			Thread.currentThread().setContextClassLoader(currentThreadContextClassLoader);
		}
	}

	@SuppressWarnings("deprecation")
	private void logCacheInitialization() {

		getOptionalCache()
			.filter(cache -> isInfoLoggingEnabled())
			.ifPresent(cache -> {

				logInfo(() -> String.format("%1$s %2$s version [%3$s] Cache [%4$s]", this.cacheResolutionMessagePrefix,
					apacheGeodeProductName(), apacheGeodeVersion(), cache.getName()));

				Optional.ofNullable(cache.getDistributedSystem())
					.map(DistributedSystem::getDistributedMember)
					.ifPresent(member -> {

						String message = "Connected to Distributed System [%1$s] as Member [%2$s] in Group(s) [%3$s]"
							+ " with Role(s) [%4$s] on Host [%5$s] having PID [%6$d]";

						logInfo(() -> String.format(message,
							cache.getDistributedSystem().getName(), member.getId(), member.getGroups(),
							member.getRoles(), member.getHost(), member.getProcessId()));
					});
			});
	}

	/**
	 * Resolves a {@link ClientCache} by attempting to lookup an existing {@link ClientCache} instance in the JVM,
	 * first. If an existing {@link ClientCache} could not be found, then this method proceeds in attempting to
	 * create a new {@link ClientCache} instance.
	 *
	 * @param <T> parameterized {@link Class} type extending {@link ClientCache}.
	 * @return the resolved {@link ClientCache}.
	 * @see org.apache.geode.cache.client.ClientCache
	 * @see ClientCache
	 * @see #fetchCache()
	 * @see #resolveProperties()
	 * @see #createFactory(Properties)
	 * @see #initializeFactory(Object)
	 * @see #configureFactory(Object)
	 * @see #postProcess(Object)
	 * @see #createCache(Object)
	 * @see #postProcess(ClientCache)
	 */
	protected <T extends ClientCache> T resolveCache() {

		try {

			this.cacheResolutionMessagePrefix = "Found existing";

			T cache = fetchCache();

			cache = postProcess(cache);

			return cache;
		}
		catch (CacheClosedException cause) {

			this.cacheResolutionMessagePrefix = "Created new";

			Properties gemfireProperties = resolveProperties();

			Object factory = createFactory(gemfireProperties);

			factory = initializeFactory(factory);
			factory = configureFactory(factory);
			factory = postProcess(factory);

			T cache = createCache(factory);

			cache = postProcess(cache);

			return cache;
		}
	}

	/**
	 * Constructs a new cache factory initialized with the given Apache Geode {@link Properties}
	 * used to construct, configure and initialize a new {@link ClientCache}.
	 *
	 * @param gemfireProperties {@link Properties} used by the cache factory to configure the {@link ClientCache};
	 * must not be {@literal null}
	 * @return a new cache factory initialized with the given Apache Geode {@link Properties}.
	 * @see org.apache.geode.cache.client.ClientCacheFactory
	 * @see Properties
	 * @see #resolveProperties()
	 */
	protected abstract @NonNull Object createFactory(@NonNull Properties gemfireProperties);

	/**
	 * Configures the cache factory used to create the {@link ClientCache}.
	 *
	 * @param factory cache factory to configure; must not be {@literal null}.
	 * @return the given cache factory.
	 * @see #createFactory(Properties)
	 */
	protected @NonNull Object configureFactory(@NonNull Object factory) {
		return factory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object initializeFactory(Object factory) {
		return super.initializeFactory(factory);
	}

	/**
	 * Post process the cache factory used to create the {@link ClientCache}.
	 *
	 * @param factory cache factory to post process; must not be {@literal null}.
	 * @return the post processed cache factory.
	 * @see org.apache.geode.cache.client.ClientCacheFactory
	 * @see #createFactory(Properties)
	 */
	protected @NonNull Object postProcess(@NonNull Object factory) {
		return factory;
	}

	/**
	 * Creates a new {@link ClientCache} instance using the provided {@link Object factory}.
	 *
	 * @param <T> {@link Class Subtype} of {@link ClientCache}.
	 * @param factory factory used to create the {@link ClientCache}.
	 * @return a new instance of {@link ClientCache} created by the provided {@link Object factory}.
	 * @see org.apache.geode.cache.client.ClientCacheFactory#create()
	 * @see ClientCache
	 */
	protected abstract @NonNull <T extends ClientCache> T createCache(@NonNull Object factory);

	/**
	 * Post process the {@link ClientCache} by loading any {@literal cache.xml} file, applying custom settings
	 * specified in SDG XML configuration metadata, and registering appropriate Transaction Listeners, Writer
	 * and JVM Heap configuration.
	 *
	 * @param <T> parameterized {@link Class} type extending {@link ClientCache}.
	 * @param cache {@link ClientCache} to post process.
	 * @return the given {@link ClientCache}.
	 * @see #loadCacheXml(ClientCache)
	 * @see #configureHeapPercentages(ClientCache)
	 * @see #registerTransactionListeners(ClientCache)
	 */
	protected @NonNull <T extends ClientCache> T postProcess(@NonNull T cache) {

		loadCacheXml(cache);

		Optional.ofNullable(getCopyOnRead()).ifPresent(cache::setCopyOnRead);

		configureHeapPercentages(cache);
		registerTransactionListeners(cache);

		return cache;
	}
}
