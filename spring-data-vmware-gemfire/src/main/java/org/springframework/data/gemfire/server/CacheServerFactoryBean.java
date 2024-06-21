/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.server;

import static java.util.stream.StreamSupport.stream;
import static org.springframework.data.gemfire.util.ArrayUtils.nullSafeArray;
import static org.springframework.data.gemfire.util.CollectionUtils.nullSafeCollection;
import static org.springframework.data.gemfire.util.CollectionUtils.nullSafeIterable;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalArgumentException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.InterestRegistrationListener;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.server.CacheServer;
import org.apache.geode.cache.server.ClientSubscriptionConfig;
import org.apache.geode.cache.server.ServerLoadProbe;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.data.gemfire.config.annotation.CacheServerConfigurer;
import org.springframework.data.gemfire.support.AbstractFactoryBeanSupport;
import org.springframework.util.StringUtils;

/**
 * Spring {@link FactoryBean} used to construct, configure and initialize a {@link CacheServer}.
 *
 * @author Costin Leau
 * @author John Blum
 * @see Cache
 * @see ClientCache
 * @see CacheServer
 * @see ClientSubscriptionConfig
 * @see ServerLoadProbe
 * @see DisposableBean
 * @see FactoryBean
 * @see InitializingBean
 * @see SmartLifecycle
 * @see CacheServerConfigurer
 * @see AbstractFactoryBeanSupport
 */
@SuppressWarnings("unused")
public class CacheServerFactoryBean extends AbstractFactoryBeanSupport<CacheServer>
		implements DisposableBean, InitializingBean, SmartLifecycle {

	private boolean autoStartup = true;
	private boolean notifyBySubscription = CacheServer.DEFAULT_NOTIFY_BY_SUBSCRIPTION;
	private boolean tcpNoDelay = CacheServer.DEFAULT_TCP_NO_DELAY;

	private int maxConnections = CacheServer.DEFAULT_MAX_CONNECTIONS;
	private int maxMessageCount = CacheServer.DEFAULT_MAXIMUM_MESSAGE_COUNT;
	private int maxThreads = CacheServer.DEFAULT_MAX_THREADS;
	private int maxTimeBetweenPings = CacheServer.DEFAULT_MAXIMUM_TIME_BETWEEN_PINGS;
	private int messageTimeToLive = CacheServer.DEFAULT_MESSAGE_TIME_TO_LIVE;
	private int port = CacheServer.DEFAULT_PORT;
	private int socketBufferSize = CacheServer.DEFAULT_SOCKET_BUFFER_SIZE;
	private int subscriptionCapacity = ClientSubscriptionConfig.DEFAULT_CAPACITY;

	private long loadPollInterval = CacheServer.DEFAULT_LOAD_POLL_INTERVAL;

	private Cache cache;

	private CacheServer cacheServer;

	private List<CacheServerConfigurer> cacheServerConfigurers = Collections.emptyList();

	private final CacheServerConfigurer compositeCacheServerConfigurer = (beanName, bean) ->
		nullSafeCollection(cacheServerConfigurers).forEach(cacheServerConfigurer ->
			cacheServerConfigurer.configure(beanName, bean));

	private ServerLoadProbe serverLoadProbe = CacheServer.DEFAULT_LOAD_PROBE;

	private Set<InterestRegistrationListener> listeners = Collections.emptySet();

	private String bindAddress = CacheServer.DEFAULT_BIND_ADDRESS;
	private String hostNameForClients = CacheServer.DEFAULT_HOSTNAME_FOR_CLIENTS;
	private String subscriptionDiskStore;

	private String[] serverGroups = {};

	private SubscriptionEvictionPolicy subscriptionEvictionPolicy = SubscriptionEvictionPolicy.DEFAULT;

	/**
	 * {{@inheritDoc}}
	 */
	@Override
	public void afterPropertiesSet() throws IOException {

		applyCacheServerConfigurers();

		Cache cache = resolveCache();

		this.cacheServer = postProcess(configure(addCacheServer(cache)));
	}

	/* (non-Javadoc) */
	private void applyCacheServerConfigurers() {
		applyCacheServerConfigurers(getCompositeCacheServerConfigurer());
	}

	/**
	 * Null-safe operation to apply the given array of {@link CacheServerConfigurer CacheServerConfigurers}
	 * to this {@link CacheServerFactoryBean}.
	 *
	 * @param cacheServerConfigurers array of {@link CacheServerConfigurer CacheServerConfigurers} applied to
	 * this {@link CacheServerFactoryBean}.
	 * @see CacheServerConfigurer
	 * @see #applyCacheServerConfigurers(Iterable)
	 */
	protected void applyCacheServerConfigurers(CacheServerConfigurer... cacheServerConfigurers) {
		applyCacheServerConfigurers(Arrays.asList(nullSafeArray(cacheServerConfigurers, CacheServerConfigurer.class)));
	}

	/**
	 * Null-safe operation to apply the given {@link Iterable} of {@link CacheServerConfigurer CacheServerConfigurers}
	 * to this {@link CacheServerFactoryBean}.
	 *
	 * @param cacheServerConfigurers {@link Iterable} of {@link CacheServerConfigurer CacheServerConfigurers} applied to
	 * this {@link CacheServerFactoryBean}.
	 * @see CacheServerConfigurer
	 */
	protected void applyCacheServerConfigurers(Iterable<CacheServerConfigurer> cacheServerConfigurers) {
		stream(nullSafeIterable(cacheServerConfigurers).spliterator(), false)
			.forEach(cacheServerConfigurer -> cacheServerConfigurer.configure(getBeanName(), this));
	}

	/* (non-Javadoc) */
	private Cache resolveCache() {
		return Optional.ofNullable(this.cache)
			.orElseThrow(() -> newIllegalArgumentException("Cache is required"));
	}

	/**
	 * Adds a {@link CacheServer} to the given {@link Cache} for server {@link ClientCache cache clients}.
	 *
	 * @param cache {@link Cache} used to add a {@link CacheServer}.
	 * @return the newly added {@link CacheServer}.
	 * @see Cache#addCacheServer()
	 * @see CacheServer
	 */
	protected CacheServer addCacheServer(Cache cache) {
		return cache.addCacheServer();
	}

	/**
	 * Configures the provided {@link CacheServer} with any custom, application-specific configuration.
	 *
	 * @param cacheServer {@link CacheServer} to configure.
	 * @return the given {@link CacheServer}.
	 * @see CacheServer
	 */
	protected CacheServer configure(CacheServer cacheServer) {

		cacheServer.setBindAddress(this.bindAddress);
		cacheServer.setGroups(this.serverGroups);
		cacheServer.setHostnameForClients(this.hostNameForClients);
		cacheServer.setLoadPollInterval(this.loadPollInterval);
		cacheServer.setLoadProbe(this.serverLoadProbe);
		cacheServer.setMaxConnections(this.maxConnections);
		cacheServer.setMaximumMessageCount(this.maxMessageCount);
		cacheServer.setMaximumTimeBetweenPings(this.maxTimeBetweenPings);
		cacheServer.setMaxThreads(this.maxThreads);
		cacheServer.setMessageTimeToLive(this.messageTimeToLive);
		cacheServer.setNotifyBySubscription(this.notifyBySubscription);
		cacheServer.setPort(this.port);
		cacheServer.setSocketBufferSize(this.socketBufferSize);
		cacheServer.setTcpNoDelay(this.tcpNoDelay);

		nullSafeCollection(this.listeners).forEach(cacheServer::registerInterestRegistrationListener);

		ClientSubscriptionConfig config = cacheServer.getClientSubscriptionConfig();

		config.setCapacity(this.subscriptionCapacity);
		getSubscriptionEvictionPolicy().setEvictionPolicy(config);

		Optional.ofNullable(this.subscriptionDiskStore).filter(StringUtils::hasText)
			.ifPresent(config::setDiskStoreName);

		return cacheServer;
	}

	/**
	 * Post-process the {@link CacheServer} with any necessary follow-up actions.
	 *
	 * @param cacheServer {@link CacheServer} to process.
	 * @return the given {@link CacheServer}.
	 * @see CacheServer
	 */
	protected CacheServer postProcess(CacheServer cacheServer) {
		return cacheServer;
	}

	/**
	 * Returns a reference to the Composite {@link CacheServerConfigurer} used to apply additional configuration
	 * to this {@link CacheServerFactoryBean} on Spring container initialization.
	 *
	 * @return the Composite {@link CacheServerConfigurer}.
	 * @see CacheServerConfigurer
	 */
	protected CacheServerConfigurer getCompositeCacheServerConfigurer() {
		return this.compositeCacheServerConfigurer;
	}

	/**
	 * {{@inheritDoc}}
	 */
	@Override
	public CacheServer getObject() {
		return this.cacheServer;
	}

	/**
	 * {{@inheritDoc}}
	 */
	@Override
	public Class<?> getObjectType() {
		return this.cacheServer != null ? this.cacheServer.getClass() : CacheServer.class;
	}

	public boolean isAutoStartup() {
		return this.autoStartup;
	}

	public boolean isRunning() {
		return this.cacheServer != null && this.cacheServer.isRunning();
	}

	/**
	 * Start at the latest possible moment.
	 */
	public int getPhase() {
		return Integer.MAX_VALUE;
	}

	public void destroy() {
		stop();
		this.cacheServer = null;
	}

	@Override
	public void start() {

		try {
			cacheServer.start();
		}
		catch (IOException e) {
			throw new BeanInitializationException("Cannot start cache server", e);
		}
	}

	public void stop() {
		Optional.ofNullable(this.cacheServer).ifPresent(CacheServer::stop);
	}

	@Override
	public void stop(Runnable callback) {
		stop();
		callback.run();
	}

	public void setAutoStartup(boolean autoStartup) {
		this.autoStartup = autoStartup;
	}

	public void setBindAddress(String bindAddress) {
		this.bindAddress = bindAddress;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	void setCacheServer(CacheServer cacheServer) {
		this.cacheServer = cacheServer;
	}

	/**
	 * Null-safe operation to set an array of {@link CacheServerConfigurer CacheServerConfigurers} used to apply
	 * additional configuration to this {@link CacheServerFactoryBean} when using Annotation-based configuration.
	 *
	 * @param cacheServerConfigurers array of {@link CacheServerConfigurer CacheServerConfigurers} used to apply
	 * additional configuration to this {@link CacheServerFactoryBean}.
	 * @see org.springframework.data.gemfire.config.annotation.PeerCacheConfigurer
	 * @see #setCacheServerConfigurers(List)
	 */
	public void setCacheServerConfigurers(CacheServerConfigurer... cacheServerConfigurers) {
		setCacheServerConfigurers(Arrays.asList(nullSafeArray(cacheServerConfigurers, CacheServerConfigurer.class)));
	}

	/**
	 * Null-safe operation to set an {@link Iterable} of {@link CacheServerConfigurer CacheServerConfigurers}
	 * used to apply additional configuration to this {@link CacheServerFactoryBean} when using
	 * Annotation-based configuration.
	 *
	 * @param cacheServerConfigurers {@literal Iterable} of {@link CacheServerConfigurer CacheServerConfigurers}
	 * used to apply additional configuration to this {@link CacheServerFactoryBean}.
	 * @see org.springframework.data.gemfire.config.annotation.PeerCacheConfigurer
	 */
	public void setCacheServerConfigurers(List<CacheServerConfigurer> cacheServerConfigurers) {
		this.cacheServerConfigurers = Optional.ofNullable(cacheServerConfigurers).orElseGet(Collections::emptyList);
	}

	public void setHostNameForClients(String hostNameForClients) {
		this.hostNameForClients = hostNameForClients;
	}

	public void setListeners(Set<InterestRegistrationListener> listeners) {
		this.listeners = listeners;
	}

	public void setLoadPollInterval(long loadPollInterval) {
		this.loadPollInterval = loadPollInterval;
	}

	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	public void setMaxMessageCount(int maxMessageCount) {
		this.maxMessageCount = maxMessageCount;
	}

	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}

	public void setMaxTimeBetweenPings(int maxTimeBetweenPings) {
		this.maxTimeBetweenPings = maxTimeBetweenPings;
	}

	public void setMessageTimeToLive(int messageTimeToLive) {
		this.messageTimeToLive = messageTimeToLive;
	}

	public void setNotifyBySubscription(boolean notifyBySubscription) {
		this.notifyBySubscription = notifyBySubscription;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setServerGroups(String[] serverGroups) {
		this.serverGroups = serverGroups;
	}

	public void setServerLoadProbe(ServerLoadProbe serverLoadProbe) {
		this.serverLoadProbe = serverLoadProbe;
	}

	public void setSocketBufferSize(int socketBufferSize) {
		this.socketBufferSize = socketBufferSize;
	}

	public void setSubscriptionCapacity(int subscriptionCapacity) {
		this.subscriptionCapacity = subscriptionCapacity;
	}

	public void setSubscriptionDiskStore(String diskStoreName) {
		this.subscriptionDiskStore = diskStoreName;
	}

	SubscriptionEvictionPolicy getSubscriptionEvictionPolicy() {
		return Optional.ofNullable(this.subscriptionEvictionPolicy).orElse(SubscriptionEvictionPolicy.DEFAULT);
	}

	public void setSubscriptionEvictionPolicy(SubscriptionEvictionPolicy evictionPolicy) {
		this.subscriptionEvictionPolicy = evictionPolicy;
	}

	public void setTcpNoDelay(boolean tcpNoDelay) {
		this.tcpNoDelay = tcpNoDelay;
	}
}
