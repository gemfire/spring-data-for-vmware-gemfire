/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-12: Migrated from org.apache.geode imports to GUD API types
 * 2026-03-14: Added graceful handling for unsupported per-server connection settings
 */

package org.springframework.data.gemfire.client;

import static org.springframework.data.gemfire.util.CollectionUtils.nullSafeCollection;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.gemfire.AbstractResolvableCacheFactoryBean;
import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.data.gemfire.JndiDataSourceType;
import org.springframework.data.gemfire.client.support.DefaultableDelegatingPoolAdapter;
import org.springframework.data.gemfire.client.support.DelegatingPoolAdapter;
import org.springframework.data.gemfire.client.support.PoolManagerPoolResolver;
import org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer;
import org.springframework.data.gemfire.config.xml.GemfireConstants;
import org.springframework.data.gemfire.gud.api.GudCacheClosedException;
import org.springframework.data.gemfire.gud.api.GudCacheProvider;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientCacheFactory;
import org.springframework.data.gemfire.gud.api.GudConfigProperty;
import org.springframework.data.gemfire.gud.api.GudUnsupportedOperationException;
import org.springframework.data.gemfire.gud.api.GudDistributedSystem;
import org.springframework.data.gemfire.gud.api.GudJndiBinding;
import org.springframework.data.gemfire.gud.api.GudPdxSerializer;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudSocketFactory;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.data.gemfire.support.ConnectionEndpointList;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.data.gemfire.util.PropertiesBuilder;
import org.springframework.data.gemfire.util.SpringExtensions;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Spring {@link FactoryBean} used to construct, configure and initialize a {@link GudClientCache}.
 *
 * @author Costin Leau
 * @author Lyndon Adams
 * @author John Blum
 * @see InetSocketAddress
 * @see Properties
 * @see GudClientCache
 * @see GudClientCacheFactory
 * @see GudPool
 * @see GudSocketFactory
 * @see GudDistributedSystem
 * @see GudPdxSerializer
 * @see FactoryBean
 * @see ApplicationContext
 * @see ApplicationListener
 * @see ApplicationContextEvent
 * @see ContextRefreshedEvent
 * @see ClientCacheFactoryBean
 * @see PoolManagerPoolResolver
 * @see ClientCacheConfigurer
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class ClientCacheFactoryBean extends AbstractResolvableCacheFactoryBean implements ApplicationListener<ContextRefreshedEvent> {

	protected static final PoolResolver DEFAULT_POOL_RESOLVER = new PoolManagerPoolResolver();

	private Boolean keepAlive = false;
	private Boolean multiUserAuthentication;
	private Boolean prSingleHopEnabled;
	private Boolean readyForEvents;
	private Boolean subscriptionEnabled;
	private Boolean threadLocalConnections;

	private final ConnectionEndpointList locators = new ConnectionEndpointList();
	private final ConnectionEndpointList servers = new ConnectionEndpointList();

	private Integer durableClientTimeout;
	private Integer freeConnectionTimeout;
	private Integer loadConditioningInterval;
	private Integer minConnections;
	private Integer maxConnections;
	private Integer minConnectionsPerServer;
	private Integer maxConnectionsPerServer;
	private Integer readTimeout;
	private Integer retryAttempts;
	private Integer serverConnectionTimeout;
	private Integer socketBufferSize;
	private Integer socketConnectTimeout;
	private Integer statisticsInterval;
	private Integer subscriptionAckInterval;
	private Integer subscriptionMessageTrackingTimeout;
	private Integer subscriptionRedundancy;

	private List<ClientCacheConfigurer> clientCacheConfigurers = Collections.emptyList();
	private List<JndiDataSource> jndiDataSources;

	private Long idleTimeout;
	private Long pingInterval;

	private GudPool pool;

	private PoolResolver poolResolver = DEFAULT_POOL_RESOLVER;

	private GudSocketFactory socketFactory;

	private String durableClientId;
	private String poolName;
	private String serverGroup;

	private final ClientCacheConfigurer compositeClientCacheConfigurer = (beanName, bean) ->
		nullSafeCollection(this.clientCacheConfigurers).forEach(clientCacheConfigurer ->
			clientCacheConfigurer.configure(beanName, bean));

	/**
	 * Applies the composite {@link ClientCacheConfigurer ClientCacheConfigurers} to this {@link ClientCacheFactoryBean}
	 * before the {@link GudClientCache} is created.
	 *
	 * @see #getCompositeClientCacheConfigurer()
	 * @see #applyClientCacheConfigurers(ClientCacheConfigurer...)
	 */
	@Override
	protected void applyCacheConfigurers() {
		applyClientCacheConfigurers(getCompositeClientCacheConfigurer());
	}

	/**
	 * Applies the array of {@link ClientCacheConfigurer ClientCacheConfigurers} to this {@link ClientCacheFactoryBean}
	 * before the {@link GudClientCache} is created.
	 *
	 * @param clientCacheConfigurers array of {@link ClientCacheConfigurer ClientCacheConfigurers}
	 * applied to this {@link ClientCacheFactoryBean}.
	 * @see ClientCacheConfigurer
	 * @see #applyClientCacheConfigurers(Iterable)
	 */
	protected void applyClientCacheConfigurers(ClientCacheConfigurer... clientCacheConfigurers) {
		applyClientCacheConfigurers(Arrays.asList(ArrayUtils.nullSafeArray(clientCacheConfigurers,
			ClientCacheConfigurer.class)));
	}

	/**
	 * Apples the {@link Iterable} of {@link ClientCacheConfigurer ClientCacheConfigurers}
	 * to this {@link ClientCacheFactoryBean} before the {@link GudClientCache} is created.
	 *
	 * @param clientCacheConfigurers {@link Iterable} of {@link ClientCacheConfigurer ClientCacheConfigurers}
	 * applied to this {@link ClientCacheFactoryBean}.
	 * @see ClientCacheConfigurer
	 * @see Iterable
	 */
	protected void applyClientCacheConfigurers(Iterable<ClientCacheConfigurer> clientCacheConfigurers) {
		StreamSupport.stream(CollectionUtils.nullSafeIterable(clientCacheConfigurers).spliterator(), false)
			.forEach(clientCacheConfigurer -> clientCacheConfigurer.configure(getBeanName(), this));
	}

	/**
	 * Fetches an existing {@link GudClientCache} instance from the {@link GudClientCacheFactory}.
	 *
	 * @param <T> parameterized {@link Class} type extension of {@link GudClientCache}.
	 * @return an existing {@link GudClientCache} instance if available.
	 * @throws GudCacheClosedException if an existing {@link GudClientCache} instance does not exist.
	 * @see GudClientCacheFactory#getAnyInstance()
	 * @see GudClientCache
	 * @see #getCache()
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected <T extends GudClientCache> T doFetchCache() {
		return (T) getClientCacheFactory().getAnyInstance();
	}

	/**
	 * Returns the {@link Class type} of {@link GudClientCache} constructed by this {@link ClientCacheFactoryBean}.
	 *
	 * Returns {@link GudClientCache} {@link Class}.
	 *
	 * @return the {@link Class type} of {@link GudClientCache} constructed by this {@link ClientCacheFactoryBean}.
	 * @see FactoryBean#getObjectType()
	 */
	@Override
	protected Class<? extends GudClientCache> doGetObjectType() {
		return GudClientCache.class;
	}

	/**
	 * Resolves the Apache Geode {@link Properties} used to configure the {@link GudClientCache}.
	 *
	 * @return the resolved Apache Geode {@link Properties} used to configure the {@link GudClientCache}.
	 * @see GudDistributedSystem#getProperties()
	 */
	@Override
	protected @NonNull Properties resolveProperties() {
		return resolveProperties(GemfireUtils::getDistributedSystem);
	}

	@NonNull Properties resolveProperties(@NonNull Supplier<GudDistributedSystem> distributedSystemSupplier) {

		Properties gemfireProperties = super.resolveProperties();

		GudDistributedSystem distributedSystem = distributedSystemSupplier.get();

		if (GemfireUtils.isConnected(distributedSystem)) {
			gemfireProperties = PropertiesBuilder.from(distributedSystem.getProperties())
				.add(gemfireProperties)
				.build();
		}

		GemfireUtils.configureDurableClient(gemfireProperties, getDurableClientId(), getDurableClientTimeout());

		return gemfireProperties;
	}

	/**
	 * Returns the {@link GudClientCacheFactory} to use for creating {@link GudClientCache} instances.
	 * Uses {@link GudCacheProvider} to obtain the factory from the driver discovered via ServiceLoader.
	 *
	 * @return the {@link GudClientCacheFactory} to use.
	 * @see GudCacheProvider#createClientCacheFactory()
	 * @see GudClientCacheFactory
	 */
	protected GudClientCacheFactory getClientCacheFactory() {
		return GudCacheProvider.createClientCacheFactory();
	}

	/**
	 * Constructs a new instance of {@link GudClientCacheFactory} initialized with the given Apache Geode {@link Properties}
	 * used to construct, configure and initialize a new {@link GudClientCache} instance.
	 *
	 * @param gemfireProperties {@link Properties} used by the {@link GudClientCacheFactory}
	 * to configure the {@link GudClientCache}.
	 * @return a new instance of {@link GudClientCacheFactory} initialized with the given Apache Geode {@link Properties}.
	 * @see GudClientCacheFactory
	 * @see Properties
	 */
	@Override
	protected @NonNull Object createFactory(@NonNull Properties gemfireProperties) {
		GudClientCacheFactory factory = getClientCacheFactory();
		
		// Apply properties to the factory
		for (String propertyName : gemfireProperties.stringPropertyNames()) {
			factory.set(propertyName, gemfireProperties.getProperty(propertyName));
		}
		
		return factory;
	}

	/**
	 * Configures the {@link GudClientCacheFactory} used to create the {@link GudClientCache}.
	 *
	 * @param factory {@link GudClientCacheFactory} used to create the {@link GudClientCache}.
	 * @return the configured {@link GudClientCacheFactory}.
	 * @see GudClientCacheFactory
	 * @see #configurePool(GudClientCacheFactory)
	 * @see #configurePdx(PdxConfigurer)
	 */
	@Override
	protected @NonNull Object configureFactory(@NonNull Object factory) {
		return configurePool(configurePdx((GudClientCacheFactory) factory));
	}

	/**
	 * Configures the {@link GudClientCache} to use PDX serialization.
	 *
	 * @param clientCacheFactory {@link GudClientCacheFactory} to configure with PDX.
	 * @return the given {@link GudClientCacheFactory}.
	 * @see ClientCacheFactoryToPdxConfigurerAdapter
	 * @see GudClientCacheFactory
	 * @see #configurePdx(PdxConfigurer)
	 */
	protected @NonNull GudClientCacheFactory configurePdx(@NonNull GudClientCacheFactory clientCacheFactory) {

		PdxConfigurer<GudClientCacheFactory> pdxConfigurer =
			ClientCacheFactoryToPdxConfigurerAdapter.from(clientCacheFactory);

		return configurePdx(pdxConfigurer);
	}

	/**
	 * Configure the {@literal DEFAULT} {@link GudPool} of the {@link GudClientCacheFactory} using a given {@link GudPool}
	 * instance or a named {@link GudPool} instance.
	 *
	 * @param clientCacheFactory {@link GudClientCacheFactory} used to configure the {@literal DEFAULT} {@link GudPool}.
	 * @see GudClientCacheFactory
	 * @see GudPool
	 */
	protected @NonNull GudClientCacheFactory configurePool(@NonNull GudClientCacheFactory clientCacheFactory) {

		DefaultableDelegatingPoolAdapter pool =
			DefaultableDelegatingPoolAdapter.from(DelegatingPoolAdapter.from(resolvePool())).preferDefault();

		clientCacheFactory.setPoolFreeConnectionTimeout(pool.getFreeConnectionTimeout(getFreeConnectionTimeout()));
		clientCacheFactory.setPoolIdleTimeout(pool.getIdleTimeout(getIdleTimeout()));
		clientCacheFactory.setPoolLoadConditioningInterval(pool.getLoadConditioningInterval(getLoadConditioningInterval()));
		clientCacheFactory.setPoolMinConnections(pool.getMinConnections(getMinConnections()));
		clientCacheFactory.setPoolMaxConnections(pool.getMaxConnections(getMaxConnections()));
		configurePerServerConnectionLimits(clientCacheFactory, pool);
		clientCacheFactory.setPoolMultiuserAuthentication(pool.getMultiuserAuthentication(getMultiUserAuthentication()));
		clientCacheFactory.setPoolPingInterval(pool.getPingInterval(getPingInterval()));
		clientCacheFactory.setPoolPRSingleHopEnabled(pool.getPRSingleHopEnabled(getPrSingleHopEnabled()));
		clientCacheFactory.setPoolReadTimeout(pool.getReadTimeout(getReadTimeout()));
		clientCacheFactory.setPoolRetryAttempts(pool.getRetryAttempts(getRetryAttempts()));
		clientCacheFactory.setPoolServerConnectionTimeout(pool.getServerConnectionTimeout(getServerConnectionTimeout()));
		clientCacheFactory.setPoolServerGroup(pool.getServerGroup(getServerGroup()));
		clientCacheFactory.setPoolSocketBufferSize(pool.getSocketBufferSize(getSocketBufferSize()));
		clientCacheFactory.setPoolSocketConnectTimeout(pool.getSocketConnectTimeout(getSocketConnectTimeout()));
		clientCacheFactory.setPoolSocketFactory(pool.getSocketFactory(getSocketFactory()));
		clientCacheFactory.setPoolStatisticInterval(pool.getStatisticInterval(getStatisticsInterval()));
		clientCacheFactory.setPoolSubscriptionAckInterval(pool.getSubscriptionAckInterval(getSubscriptionAckInterval()));
		clientCacheFactory.setPoolSubscriptionEnabled(pool.getSubscriptionEnabled(getSubscriptionEnabled()));
		clientCacheFactory.setPoolSubscriptionMessageTrackingTimeout(pool.getSubscriptionMessageTrackingTimeout(getSubscriptionMessageTrackingTimeout()));
		clientCacheFactory.setPoolSubscriptionRedundancy(pool.getSubscriptionRedundancy(getSubscriptionRedundancy()));

		AtomicBoolean noServers = new AtomicBoolean(getServers().isEmpty());

		boolean noLocators = getLocators().isEmpty();
		boolean hasLocators = !noLocators;
		boolean hasServers = !noServers.get();

		if (hasServers || noLocators) {

			Iterable<InetSocketAddress> servers = pool.getServers(getServers().toInetSocketAddresses());

			StreamSupport.stream(servers.spliterator(), false).forEach(server -> {
				clientCacheFactory.addPoolServer(server.getHostName(), server.getPort());
				noServers.set(false);
			});
		}

		if (hasLocators || noServers.get()) {

			Iterable<InetSocketAddress> locators = pool.getLocators(getLocators().toInetSocketAddresses());

			StreamSupport.stream(locators.spliterator(), false).forEach(locator ->
				clientCacheFactory.addPoolLocator(locator.getHostName(), locator.getPort()));
		}

		return clientCacheFactory;
	}

	/**
	 * Configures per-server connection limits on the client cache factory.
	 * These settings are only available in GemFire 10.3+. For older versions,
	 * a warning is logged and the settings are skipped.
	 */
	private void configurePerServerConnectionLimits(GudClientCacheFactory clientCacheFactory,
			DefaultableDelegatingPoolAdapter pool) {

		Integer minConnectionsPerServer = pool.getMinConnectionsPerServer(getMinConnectionsPerServer());
		Integer maxConnectionsPerServer = pool.getMaxConnectionsPerServer(getMaxConnectionsPerServer());

		try {
			if (minConnectionsPerServer != null) {
				clientCacheFactory.setPoolMinConnectionsPerServer(minConnectionsPerServer);
			}
			if (maxConnectionsPerServer != null) {
				clientCacheFactory.setPoolMaxConnectionsPerServer(maxConnectionsPerServer);
			}
		} catch (GudUnsupportedOperationException ex) {
			getLogger().warn("Per-server connection limits (minConnectionsPerServer={}, maxConnectionsPerServer={}) " +
				"are not supported by this GemFire version. {}",
				minConnectionsPerServer, maxConnectionsPerServer, ex.getMessage());
		}
	}

	/**
	 * Resolves the {@link GudPool} used to configure the {@link GudClientCache}, {@literal DEFAULT} {@link GudPool}.
	 *
	 * @return the resolved {@link GudPool} used to configure the {@link GudClientCache}, {@literal DEFAULT} {@link GudPool}.
	 * @see GudPool
	 * @see #getPoolName()
	 * @see #getPool()
	 * @see #findPool(String)
	 * @see #isPoolNameResolvable(String)
	 */
	protected @Nullable GudPool resolvePool() {

		GudPool pool = getPool();

		if (pool == null) {

			String poolName = resolvePoolName();

			pool = findPool(poolName);

			if (pool == null && isPoolNameResolvable(poolName)) {

				String dereferencedPoolName = SpringExtensions.dereferenceBean(poolName);

				PoolFactoryBean poolFactoryBean =
					getBeanFactory().getBean(dereferencedPoolName, PoolFactoryBean.class);

				return poolFactoryBean.getPool();
			}
		}

		return pool;
	}

	private boolean isPoolNameResolvable(@Nullable String poolName) {

		return Optional.ofNullable(poolName)
			.filter(StringUtils::hasText)
			.filter(getBeanFactory()::containsBean)
			.isPresent();
	}

	@NonNull String resolvePoolName() {

		return Optional.ofNullable(getPoolName())
			.filter(StringUtils::hasText)
			.orElseGet(this::getDefaultPoolName);
	}

	@NonNull String getDefaultPoolName() {
		return GemfireConstants.DEFAULT_GEMFIRE_POOL_NAME;
	}

	GudPool findPool(String name) {
		return getPoolResolver().resolve(name);
	}

	/**
	 * Returns the {@link GudJndiBinding} used for JNDI data source mapping.
	 * Uses {@link GudCacheProvider} to obtain the binding from the driver discovered via ServiceLoader.
	 *
	 * @return the {@link GudJndiBinding} to use.
	 * @see GudCacheProvider#getJndiBinding()
	 * @see GudJndiBinding
	 */
	protected GudJndiBinding getJndiBinding() {
		return GudCacheProvider.getJndiBinding();
	}

	@Override
	protected @NonNull <T extends GudClientCache> T postProcess(@NonNull T cache) {

		super.postProcess(cache);

		registerJndiDataSources(cache);

		return cache;
	}

	private GudClientCache registerJndiDataSources(GudClientCache cache) {

		CollectionUtils.nullSafeCollection(getJndiDataSources()).forEach(jndiDataSource -> {

			String type = jndiDataSource.getAttributes().get("type");

			JndiDataSourceType jndiDataSourceType = JndiDataSourceType.valueOfIgnoreCase(type);

			Assert.notNull(jndiDataSourceType,
					String.format("'jndi-binding' 'type' [%1$s] is invalid; 'type' must be one of %2$s",
							type, Arrays.toString(JndiDataSourceType.values())));

			jndiDataSource.getAttributes().put("type", jndiDataSourceType.getName());

			SpringExtensions.safeRunOperation(() ->
					getJndiBinding().mapDatasource(jndiDataSource.getAttributes(), jndiDataSource.getProps()));
		});

		return cache;
	}

	/**
	 * @param jndiDataSources the list of configured JndiDataSources to use with this Cache.
	 */
	public void setJndiDataSources(List<JndiDataSource> jndiDataSources) {
		this.jndiDataSources = jndiDataSources;
	}

	/**
	 * @return the list of configured JndiDataSources.
	 */
	public List<JndiDataSource> getJndiDataSources() {
		return this.jndiDataSources;
	}

	/**
	 * Creates a new {@link GudClientCache} instance using the provided {@link GudClientCacheFactory factory}.
	 *
	 * @param <T> parameterized {@link Class} type extending {@link GudClientCache}.
	 * @param factory instance of {@link GudClientCacheFactory}.
	 * @return a new instance of {@link GudClientCache} created by the provided factory.
	 * @see GudClientCacheFactory#create()
	 * @see GudClientCache
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected @NonNull <T extends GudClientCache> T createCache(@NonNull Object factory) {
		return (T) ((GudClientCacheFactory) factory).create();
	}

	/**
	 * Inform the Apache Geode cluster of servers that this {@link GudClientCache} is ready to receive events and updates
	 * iff the client is durable.
	 *
	 * @param event {@link ApplicationContextEvent} fired when the {@link ApplicationContext} is refreshed.
	 * @see GudClientCache#readyForEvents()
	 * @see #isReadyForEvents()
	 * @see #fetchCache()
	 */
	@Override
	public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {

		if (isReadyForEvents()) {
			try {
				this.<GudClientCache>fetchCache().readyForEvents();
			}
			catch (IllegalStateException | GudCacheClosedException ignore) {
				// Exceptions are thrown when GudClientCache.readyForEvents() is called on a non-durable client
				// or when the GudClientCache is closing.
			}
		}
	}

	/**
	 * Null-safe method used to {@link GudClientCache#close()} the {@link GudClientCache} and preserve durability.
	 *
	 * @param cache {@link GudClientCache} to close.
	 * @see GudClientCache#close(boolean)
	 * @see #isKeepAlive()
	 */
	@Override
	protected void close(@NonNull GudClientCache cache) {
		((GudClientCache) cache).close(isKeepAlive());
	}

	public void addLocators(ConnectionEndpoint... locators) {
		this.locators.add(locators);
	}

	public void addLocators(Iterable<ConnectionEndpoint> locators) {
		this.locators.add(locators);
	}

	public void addServers(ConnectionEndpoint... servers) {
		this.servers.add(servers);
	}

	public void addServers(Iterable<ConnectionEndpoint> servers) {
		this.servers.add(servers);
	}

	/**
	 * Null-safe operation to set an array of {@link ClientCacheConfigurer ClientCacheConfigurers} used to apply
	 * additional configuration to this {@link ClientCacheFactoryBean} when using Annotation-based configuration.
	 *
	 * @param clientCacheConfigurers array of {@link ClientCacheConfigurer ClientCacheConfigurers} used to
	 * apply additional configuration to this {@link ClientCacheFactoryBean}.
	 * @see ClientCacheConfigurer
	 * @see #setClientCacheConfigurers(List)
	 */
	public void setClientCacheConfigurers(ClientCacheConfigurer... clientCacheConfigurers) {
		setClientCacheConfigurers(Arrays.asList(ArrayUtils.nullSafeArray(clientCacheConfigurers, ClientCacheConfigurer.class)));
	}

	/**
	 * Null-safe operation to set an {@link List} of {@link ClientCacheConfigurer ClientCacheConfigurers} to apply
	 * additional configuration to this {@link ClientCacheFactoryBean} when using Annotation-based configuration.
	 *
	 * @param clientCacheConfigurers {@link List} of {@link ClientCacheConfigurer ClientCacheConfigurers} used to
	 * apply additional configuration to this {@link ClientCacheFactoryBean}.
	 * @see ClientCacheConfigurer
	 * @see #setClientCacheConfigurers(ClientCacheConfigurer...)
	 */
	public void setClientCacheConfigurers(List<ClientCacheConfigurer> clientCacheConfigurers) {
		this.clientCacheConfigurers = clientCacheConfigurers != null ? clientCacheConfigurers : Collections.emptyList();
	}

	/**
	 * Returns a reference to the {@literal Composite} {@link ClientCacheConfigurer} used to apply additional
	 * configuration to this {@link ClientCacheFactoryBean} on Spring container initialization.
	 *
	 * @return the {@literal Composite} {@link ClientCacheConfigurer}.
	 * @see ClientCacheConfigurer
	 */
	public @NonNull ClientCacheConfigurer getCompositeClientCacheConfigurer() {
		return this.compositeClientCacheConfigurer;
	}

	/**
	 * Set the GemFire System property 'durable-client-id' to indicate to the server that this client is durable.
	 *
	 * @param durableClientId a String value indicating the durable client id.
	 */
	public void setDurableClientId(String durableClientId) {
		this.durableClientId = durableClientId;
	}

	/**
	 * Gets the value of the GemFire System property 'durable-client-id' indicating to the server whether
	 * this client is durable.
	 *
	 * @return a String value indicating the durable client id.
	 */
	public String getDurableClientId() {
		return this.durableClientId;
	}

	/**
	 * Set the GemFire System property 'durable-client-timeout' indicating to the server how long to track events
	 * for the durable client when disconnected.
	 *
	 * @param durableClientTimeout an Integer value indicating the timeout in seconds for the server to keep
	 * the durable client's queue around.
	 */
	public void setDurableClientTimeout(Integer durableClientTimeout) {
		this.durableClientTimeout = durableClientTimeout;
	}

	/**
	 * Get the value of the GemFire System property 'durable-client-timeout' indicating to the server how long
	 * to track events for the durable client when disconnected.
	 *
	 * @return an Integer value indicating the timeout in seconds for the server to keep
	 * the durable client's queue around.
	 */
	public Integer getDurableClientTimeout() {
		return this.durableClientTimeout;
	}

	public void setFreeConnectionTimeout(Integer freeConnectionTimeout) {
		this.freeConnectionTimeout = freeConnectionTimeout;
	}

	public Integer getFreeConnectionTimeout() {
		return this.freeConnectionTimeout;
	}

	public void setIdleTimeout(Long idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	public Long getIdleTimeout() {
		return this.idleTimeout;
	}

	/**
	 * Sets whether the server(s) should keep the durable client's queue alive for the duration of the timeout
	 * when the client voluntarily disconnects.
	 *
	 * @param keepAlive a boolean value indicating to the server to keep the durable client's queues alive.
	 */
	public void setKeepAlive(Boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	/**
	 * Gets the user specified value for whether the server(s) should keep the durable client's queue alive
	 * for the duration of the timeout when the client voluntarily disconnects.
	 *
	 * @return a boolean value indicating whether the server should keep the durable client's queues alive.
	 */
	public Boolean getKeepAlive() {
		return this.keepAlive;
	}

	/**
	 * Determines whether the server(s) should keep the durable client's queue alive for the duration of the timeout
	 * when the client voluntarily disconnects.
	 *
	 * @return a boolean value indicating whether the server should keep the durable client's queues alive.
	 */
	public boolean isKeepAlive() {
		return Boolean.TRUE.equals(getKeepAlive());
	}

	public void setLoadConditioningInterval(Integer loadConditioningInterval) {
		this.loadConditioningInterval = loadConditioningInterval;
	}

	public Integer getLoadConditioningInterval() {
		return this.loadConditioningInterval;
	}

	public void setLocators(ConnectionEndpoint[] locators) {
		setLocators(ConnectionEndpointList.from(locators));
	}

	public void setLocators(Iterable<ConnectionEndpoint> locators) {
		getLocators().clear();
		addLocators(locators);
	}

	protected ConnectionEndpointList getLocators() {
		return this.locators;
	}

	public void setMinConnections(Integer minConnections) {
		this.minConnections = minConnections;
	}

	public Integer getMinConnections() {
		return this.minConnections;
	}

	public void setMaxConnections(Integer maxConnections) {
		this.maxConnections = maxConnections;
	}

	public Integer getMaxConnections() {
		return this.maxConnections;
	}

	public void setMinConnectionsPerServer(Integer minConnectionsPerServer) {
		this.minConnectionsPerServer = minConnectionsPerServer;
	}

	public Integer getMinConnectionsPerServer() {
		return this.minConnectionsPerServer;
	}

	public void setMaxConnectionsPerServer(Integer maxConnectionsPerServer) {
		this.maxConnectionsPerServer = maxConnectionsPerServer;
	}

	public Integer getMaxConnectionsPerServer() {
		return this.maxConnectionsPerServer;
	}

	public void setMultiUserAuthentication(Boolean multiUserAuthentication) {
		this.multiUserAuthentication = multiUserAuthentication;
	}

	public Boolean getMultiUserAuthentication() {
		return this.multiUserAuthentication;
	}

	/**
	 * Sets the {@link GudPool} used by this {@link GudClientCache} to obtain connections to the Apache Geode cluster.
	 *
	 * @param pool {@link GudPool} used by this {@link GudClientCache} to obtain connections to the Apache Geode cluster.
	 * @see GudPool
	 */
	public void setPool(@Nullable GudPool pool) {
		this.pool = pool;
	}

	/**
	 * Gets the {@link GudPool} used by this {@link GudClientCache} to obtain connections to the Apache Geode cluster.
	 *
	 * @return {@link GudPool} used by this {@link GudClientCache} to obtain connections to the Apache Geode cluster.
	 * @see GudPool
	 */
	public @Nullable GudPool getPool() {
		return this.pool;
	}

	/**
	 * Sets the {@link String name} of the {@link GudPool} used by this {@link GudClientCache} to obtain connections to
	 * the Apache Geode cluster.
	 *
	 * @param poolName {@link String name} of the {@link GudPool} used by this {@link GudClientCache} to obtain connections to
	 * the Apache Geode cluster.
	 */
	public void setPoolName(@Nullable String poolName) {
		this.poolName = poolName;
	}

	/**
	 * Gets the {@link String name} of the {@link GudPool} used by this {@link GudClientCache} to obtain connections to
	 * the Apache Geode cluster.
	 *
	 * @return {@link String name} of the {@link GudPool} used by this {@link GudClientCache} to obtain connections to
	 * the Apache Geode cluster.
	 */
	public @Nullable String getPoolName() {
		return this.poolName;
	}

	/**
	 * Sets (configures) the {@link PoolResolver} used by this {@link GudClientCache} to resolve {@link GudPool} objects.
	 *
	 * The {@link GudPool} objects may be managed or un-managed depending on the {@link PoolResolver} implementation.
	 *
	 * @param poolResolver {@link PoolResolver} used to resolve the configured {@link GudPool}.
	 * @see PoolResolver
	 */
	public void setPoolResolver(@Nullable PoolResolver poolResolver) {
		this.poolResolver = poolResolver;
	}

	/**
	 * Gets the configured {@link PoolResolver} used by this {@link GudClientCache} to resolve {@link GudPool} objects.
	 *
	 * @return the configured {@link PoolResolver}.  If no {@link PoolResolver} was configured, then return the default,
	 * {@link PoolManagerPoolResolver}.
	 * @see PoolResolver
	 * @see PoolManagerPoolResolver
	 */
	public @NonNull PoolResolver getPoolResolver() {

		PoolResolver poolResolver = this.poolResolver;

		return poolResolver != null ? poolResolver : DEFAULT_POOL_RESOLVER;
	}

	public void setPingInterval(Long pingInterval) {
		this.pingInterval = pingInterval;
	}

	public Long getPingInterval() {
		return this.pingInterval;
	}

	public void setPrSingleHopEnabled(Boolean prSingleHopEnabled) {
		this.prSingleHopEnabled = prSingleHopEnabled;
	}

	public Boolean getPrSingleHopEnabled() {
		return this.prSingleHopEnabled;
	}

	public void setReadTimeout(Integer readTimeout) {
		this.readTimeout = readTimeout;
	}

	public Integer getReadTimeout() {
		return this.readTimeout;
	}

	/**
	 * Sets the readyForEvents property to indicate whether the cache client should notify the server
	 * that it is ready to receive updates.
	 *
	 * @param readyForEvents sets a boolean flag to notify the server that this durable client
	 * is ready to receive updates.
	 * @see #getReadyForEvents()
	 */
	public void setReadyForEvents(Boolean readyForEvents){
		this.readyForEvents = readyForEvents;
	}

	/**
	 * Gets the user-configured value for deciding that this client is ready to receive events from the server(s).
	 *
	 * @return a {@link Boolean} indicating whether this client is ready to receive events from the server(s).
	 */
	public Boolean getReadyForEvents(){
		return this.readyForEvents;
	}

	/**
	 * Determines whether this GemFire cache client is ready for events.  If 'readyForEvents' was explicitly set,
	 * then it takes precedence over all other considerations (e.g. durability).
	 *
	 * @return a boolean value indicating whether this GemFire cache client is ready for events.
	 * @see GemfireUtils#isDurable(GudClientCache)
	 * @see #getReadyForEvents()
	 */
	public boolean isReadyForEvents() {

		Boolean readyForEvents = getReadyForEvents();

		return readyForEvents != null ? Boolean.TRUE.equals(readyForEvents)
			: SpringExtensions.safeGetValue(() -> GemfireUtils.isDurable(fetchCache()), false);
	}

	public void setRetryAttempts(Integer retryAttempts) {
		this.retryAttempts = retryAttempts;
	}

	public Integer getRetryAttempts() {
		return this.retryAttempts;
	}

	public void setServerConnectionTimeout(Integer serverConnectionTimeout) {
		this.serverConnectionTimeout = serverConnectionTimeout;
	}

	public Integer getServerConnectionTimeout() {
		return this.serverConnectionTimeout;
	}

	public void setServerGroup(String serverGroup) {
		this.serverGroup = serverGroup;
	}

	public String getServerGroup() {
		return this.serverGroup;
	}

	public void setServers(ConnectionEndpoint[] servers) {
		setServers(ConnectionEndpointList.from(servers));
	}

	public void setServers(Iterable<ConnectionEndpoint> servers) {
		getServers().clear();
		addServers(servers);
	}

	protected ConnectionEndpointList getServers() {
		return this.servers;
	}

	public void setSocketBufferSize(Integer socketBufferSize) {
		this.socketBufferSize = socketBufferSize;
	}

	public Integer getSocketBufferSize() {
		return this.socketBufferSize;
	}

	public void setSocketConnectTimeout(Integer socketConnectTimeout) {
		this.socketConnectTimeout = socketConnectTimeout;
	}

	public Integer getSocketConnectTimeout() {
		return this.socketConnectTimeout;
	}

	public void setSocketFactory(@Nullable GudSocketFactory socketFactory) {
		this.socketFactory = socketFactory;
	}

	public @NonNull GudSocketFactory getSocketFactory() {
		return this.socketFactory;
	}

	public void setStatisticsInterval(Integer statisticsInterval) {
		this.statisticsInterval = statisticsInterval;
	}

	public Integer getStatisticsInterval() {
		return this.statisticsInterval;
	}

	public void setSubscriptionAckInterval(Integer subscriptionAckInterval) {
		this.subscriptionAckInterval = subscriptionAckInterval;
	}

	public Integer getSubscriptionAckInterval() {
		return this.subscriptionAckInterval;
	}

	public void setSubscriptionEnabled(Boolean subscriptionEnabled) {
		this.subscriptionEnabled = subscriptionEnabled;
	}

	public Boolean getSubscriptionEnabled() {
		return this.subscriptionEnabled;
	}

	public void setSubscriptionMessageTrackingTimeout(Integer subscriptionMessageTrackingTimeout) {
		this.subscriptionMessageTrackingTimeout = subscriptionMessageTrackingTimeout;
	}

	public Integer getSubscriptionMessageTrackingTimeout() {
		return this.subscriptionMessageTrackingTimeout;
	}

	public void setSubscriptionRedundancy(Integer subscriptionRedundancy) {
		this.subscriptionRedundancy = subscriptionRedundancy;
	}

	public Integer getSubscriptionRedundancy() {
		return this.subscriptionRedundancy;
	}

	public void setThreadLocalConnections(Boolean threadLocalConnections) {
		this.threadLocalConnections = threadLocalConnections;
	}

	public Boolean getThreadLocalConnections() {
		return this.threadLocalConnections;
	}

	public static class ClientCacheFactoryToPdxConfigurerAdapter implements PdxConfigurer<GudClientCacheFactory> {

		public static ClientCacheFactoryToPdxConfigurerAdapter from(@NonNull GudClientCacheFactory clientCacheFactory) {
			return new ClientCacheFactoryToPdxConfigurerAdapter(clientCacheFactory);
		}

		private final GudClientCacheFactory cacheFactory;

		protected ClientCacheFactoryToPdxConfigurerAdapter(@NonNull GudClientCacheFactory cacheFactory) {
			Assert.notNull(cacheFactory, "GudClientCacheFactory must not be null");
			this.cacheFactory = cacheFactory;
		}

		@Override
		public @NonNull GudClientCacheFactory getTarget() {
			return this.cacheFactory;
		}

		@Override
		public @NonNull PdxConfigurer<GudClientCacheFactory> setDiskStoreName(String diskStoreName) {
			getTarget().setPdxDiskStore(diskStoreName);
			return this;
		}

		@Override
		public @NonNull PdxConfigurer<GudClientCacheFactory> setIgnoreUnreadFields(Boolean ignoreUnreadFields) {
			getTarget().setPdxIgnoreUnreadFields(ignoreUnreadFields);
			return this;
		}

		@Override
		public @NonNull PdxConfigurer<GudClientCacheFactory> setPersistent(Boolean persistent) {
			getTarget().setPdxPersistent(persistent);
			return this;
		}

		@Override
		public @NonNull PdxConfigurer<GudClientCacheFactory> setReadSerialized(Boolean readSerialized) {
			getTarget().setPdxReadSerialized(readSerialized);
			return this;
		}

		@Override
		public @NonNull PdxConfigurer<GudClientCacheFactory> setSerializer(GudPdxSerializer pdxSerializer) {
			getTarget().setPdxSerializer(pdxSerializer);
			return this;
		}
	}

	public static class JndiDataSource {

		private List<GudConfigProperty> configProperties;

		private Map<String, String> attributes;

		public Map<String, String> getAttributes() {
			return this.attributes;
		}

		public void setAttributes(Map<String, String> attributes) {
			this.attributes = attributes;
		}

		public List<GudConfigProperty> getProps() {
			return this.configProperties;
		}

		public void setProps(List<GudConfigProperty> props) {
			this.configProperties = props;
		}
	}
}
