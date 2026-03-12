/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire;

import static org.springframework.data.gemfire.GemfireUtils.apacheGeodeProductName;
import static org.springframework.data.gemfire.GemfireUtils.apacheGeodeVersion;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newRuntimeException;
import java.util.Optional;
import java.util.Properties;
import org.springframework.data.gemfire.gud.api.GudCacheClosedException;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudDistributedSystem;
import org.springframework.lang.NonNull;

/**
 * Abstract base class encapsulating logic to resolve or create a {@link GudClientCache} instance.
 *
 * @author John Blum
 * @see Optional
 * @see Properties
 * @see GudClientCache
 * @see GudDistributedSystem
 * @see AbstractConfigurableCacheFactoryBean
 * @since 2.5.0
 */
public abstract class AbstractResolvableCacheFactoryBean extends AbstractConfigurableCacheFactoryBean {

	private volatile String cacheResolutionMessagePrefix;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected GudClientCache doGetObject() {
		return init();
	}

	/**
	 * Initializes a {@link GudClientCache}.
	 *
	 * @return a reference to the initialized {@link GudClientCache}.
	 * @see GudClientCache
	 * @see #setCache(GudClientCache)
	 * @see #resolveCache()
	 * @see #getCache()
	 */
	protected GudClientCache init() {

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
					.map(GudDistributedSystem::getDistributedMember)
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
	 * Resolves a {@link GudClientCache} by attempting to lookup an existing {@link GudClientCache} instance in the JVM,
	 * first. If an existing {@link GudClientCache} could not be found, then this method proceeds in attempting to
	 * create a new {@link GudClientCache} instance.
	 *
	 * @param <T> parameterized {@link Class} type extending {@link GudClientCache}.
	 * @return the resolved {@link GudClientCache}.
	 * @see GudClientCache
	 * @see #fetchCache()
	 * @see #resolveProperties()
	 * @see #createFactory(Properties)
	 * @see #initializeFactory(Object)
	 * @see #configureFactory(Object)
	 * @see #postProcess(Object)
	 * @see #createCache(Object)
	 * @see #postProcess(GudClientCache)
	 */
	protected <T extends GudClientCache> T resolveCache() {

		try {

			this.cacheResolutionMessagePrefix = "Found existing";

			T cache = fetchCache();

			cache = postProcess(cache);

			return cache;
		}
		catch (GudCacheClosedException cause) {

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
	 * used to construct, configure and initialize a new {@link GudClientCache}.
	 *
	 * @param gemfireProperties {@link Properties} used by the cache factory to configure the {@link GudClientCache};
	 * must not be {@literal null}
	 * @return a new cache factory initialized with the given Apache Geode {@link Properties}.
	 * @see GudClientCache
	 * @see Properties
	 * @see #resolveProperties()
	 */
	protected abstract @NonNull Object createFactory(@NonNull Properties gemfireProperties);

	/**
	 * Configures the cache factory used to create the {@link GudClientCache}.
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
	 * Post process the cache factory used to create the {@link GudClientCache}.
	 *
	 * @param factory cache factory to post process; must not be {@literal null}.
	 * @return the post processed cache factory.
	 * @see GudClientCache
	 * @see #createFactory(Properties)
	 */
	protected @NonNull Object postProcess(@NonNull Object factory) {
		return factory;
	}

	/**
	 * Creates a new {@link GudClientCache} instance using the provided {@link Object factory}.
	 *
	 * @param <T> {@link Class Subtype} of {@link GudClientCache}.
	 * @param factory factory used to create the {@link GudClientCache}.
	 * @return a new instance of {@link GudClientCache} created by the provided {@link Object factory}.
	 * @see GudClientCache
	 */
	protected abstract @NonNull <T extends GudClientCache> T createCache(@NonNull Object factory);

	/**
	 * Post process the {@link GudClientCache} by loading any {@literal cache.xml} file, applying custom settings
	 * specified in SDG XML configuration metadata, and registering appropriate Transaction Listeners, Writer
	 * and JVM Heap configuration.
	 *
	 * @param <T> parameterized {@link Class} type extending {@link GudClientCache}.
	 * @param cache {@link GudClientCache} to post process.
	 * @return the given {@link GudClientCache}.
	 * @see #loadCacheXml(GudClientCache)
	 * @see #configureHeapPercentages(GudClientCache)
	 * @see #registerTransactionListeners(GudClientCache)
	 */
	protected @NonNull <T extends GudClientCache> T postProcess(@NonNull T cache) {

		loadCacheXml(cache);

		Optional.ofNullable(getCopyOnRead()).ifPresent(cache::setCopyOnRead);

		configureHeapPercentages(cache);
		registerTransactionListeners(cache);

		return cache;
	}
}
