/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.springframework.data.gemfire.util.CollectionUtils.nullSafeSet;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.InterestRegistrationListener;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.server.CacheServer;
import org.apache.geode.cache.server.ClientSubscriptionConfig;
import org.apache.geode.cache.server.ServerLoadProbe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.server.CacheServerFactoryBean;
import org.springframework.data.gemfire.server.SubscriptionEvictionPolicy;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Spring {@link Configuration} class used to construct, configure and initialize a {@link CacheServer} instance
 * in a Spring application context.
 *
 * @author John Blum
 * @see Cache
 * @see CacheServer
 * @see Bean
 * @see Configuration
 * @see CacheServerConfigurer
 * @see PeerCacheConfiguration
 * @see CacheServerFactoryBean
 * @since 1.9.0
 */
@Configuration
@SuppressWarnings("unused")
public class CacheServerConfiguration extends PeerCacheConfiguration {

	public static final boolean DEFAULT_USE_UDP_MEMBERSHIP_MESSENGER = false;
    protected static final boolean DEFAULT_AUTO_STARTUP = true;

	protected static final String DEFAULT_NAME = "SpringBasedCacheServerApplication";

	private boolean autoStartup = DEFAULT_AUTO_STARTUP;

	private Boolean tcpNoDelay;
	private Boolean useUDPMembershipMessenger;

	private Integer maxConnections;
	private Integer maxMessageCount;
	private Integer maxThreads;
	private Integer maxTimeBetweenPings;
	private Integer messageTimeToLive;
	private Integer port;
	private Integer socketBufferSize;
	private Integer subscriptionCapacity;

	@Autowired(required = false)
	private List<CacheServerConfigurer> cacheServerConfigurers = Collections.emptyList();

	private Long loadPollInterval;

	private ServerLoadProbe serverLoadProbe;

	private Set<InterestRegistrationListener> interestRegistrationListeners;

	private String bindAddress;
	private String hostnameForClients;
	private String subscriptionDiskStoreName;

	private SubscriptionEvictionPolicy subscriptionEvictionPolicy;

	/**
	 * Bean declaration for a single, {@link CacheServer} to serve {@link ClientCache cache client} applications.
	 *
	 * @param gemfireCache peer {@link Cache} instance in which to add the {@link CacheServer}.
	 * @return a {@link CacheServerFactoryBean} used to construct, configure and initialize
	 * the {@link CacheServer} instance.
	 * @see CacheServerFactoryBean
	 * @see CacheServer
	 * @see Cache
	 */
	@Bean
	public CacheServerFactoryBean gemfireCacheServer(GemFireCache gemfireCache) {

		Assert.isInstanceOf(Cache.class, gemfireCache,
			"GemFireCache must be an instance of org.apache.geode.cache.Cache");

		CacheServerFactoryBean gemfireCacheServer = new CacheServerFactoryBean();

		gemfireCacheServer.setCache((Cache) gemfireCache);
		gemfireCacheServer.setCacheServerConfigurers(resolveCacheServerConfigurers());
		gemfireCacheServer.setAutoStartup(isAutoStartup());
		gemfireCacheServer.setBindAddress(getBindAddress());
		gemfireCacheServer.setHostNameForClients(getHostnameForClients());
		gemfireCacheServer.setListeners(getInterestRegistrationListeners());
		gemfireCacheServer.setLoadPollInterval(getLoadPollInterval());
		gemfireCacheServer.setMaxConnections(getMaxConnections());
		gemfireCacheServer.setMaxMessageCount(getMaxMessageCount());
		gemfireCacheServer.setMaxThreads(getMaxThreads());
		gemfireCacheServer.setMaxTimeBetweenPings(getMaxTimeBetweenPings());
		gemfireCacheServer.setMessageTimeToLive(getMessageTimeToLive());
		gemfireCacheServer.setPort(getPort());
		gemfireCacheServer.setServerLoadProbe(getServerLoadProbe());
		gemfireCacheServer.setSocketBufferSize(getSocketBufferSize());
		gemfireCacheServer.setSubscriptionCapacity(getSubscriptionCapacity());
		gemfireCacheServer.setSubscriptionDiskStore(getSubscriptionDiskStoreName());
		gemfireCacheServer.setSubscriptionEvictionPolicy(getSubscriptionEvictionPolicy());
		gemfireCacheServer.setTcpNoDelay(getTcpNoDelay());

		return gemfireCacheServer;
	}

	private List<CacheServerConfigurer> resolveCacheServerConfigurers() {

		return Optional.ofNullable(this.cacheServerConfigurers)
			.filter(cacheServerConfigurers -> !cacheServerConfigurers.isEmpty())
			.orElseGet(() ->
				Collections.singletonList(LazyResolvingComposableCacheServerConfigurer.create(getBeanFactory())));
	}

	/**
	 * Configures {@link CacheServer} specific settings.
	 *
	 * @param importMetadata {@link AnnotationMetadata} containing cache server meta-data used to
	 * configure the {@link CacheServer}.
	 * @see AnnotationMetadata
	 * @see CacheServer
	 */
	@Override
	protected void configureOptional(AnnotationMetadata importMetadata) {

		super.configureCache(importMetadata);

		if (isCacheServerApplication(importMetadata)) {

			AnnotationAttributes cacheServerApplicationAttributes = getAnnotationAttributes(importMetadata);

			setAutoStartup(resolveProperty(cacheServerProperty("auto-startup"),
				Boolean.TRUE.equals(cacheServerApplicationAttributes.get("autoStartup"))));

			setBindAddress(resolveProperty(cacheServerProperty("bind-address"),
				(String) cacheServerApplicationAttributes.get("bindAddress")));

			setHostnameForClients(resolveProperty(cacheServerProperty("hostname-for-clients"),
				(String) cacheServerApplicationAttributes.get("hostnameForClients")));

			setLoadPollInterval(resolveProperty(cacheServerProperty("load-poll-interval"),
				(Long) cacheServerApplicationAttributes.get("loadPollInterval")));

			setMaxConnections(resolveProperty(cacheServerProperty("max-connections"),
				(Integer) cacheServerApplicationAttributes.get("maxConnections")));

			setMaxMessageCount(resolveProperty(cacheServerProperty("max-message-count"),
				(Integer) cacheServerApplicationAttributes.get("maxMessageCount")));

			setMaxThreads(resolveProperty(cacheServerProperty("max-threads"),
				(Integer) cacheServerApplicationAttributes.get("maxThreads")));

			setMaxTimeBetweenPings(resolveProperty(cacheServerProperty("max-time-between-pings"),
				(Integer) cacheServerApplicationAttributes.get("maxTimeBetweenPings")));

			setMessageTimeToLive(resolveProperty(cacheServerProperty("message-time-to-live"),
				(Integer) cacheServerApplicationAttributes.get("messageTimeToLive")));

			setPort(resolveProperty(cacheServerProperty("port"),
				(Integer) cacheServerApplicationAttributes.get("port")));

			setSocketBufferSize(resolveProperty(cacheServerProperty("socket-buffer-size"),
				(Integer) cacheServerApplicationAttributes.get("socketBufferSize")));

			setSubscriptionCapacity(resolveProperty(cacheServerProperty("subscription-capacity"),
				(Integer) cacheServerApplicationAttributes.get("subscriptionCapacity")));

			setSubscriptionDiskStoreName(resolveProperty(cacheServerProperty("subscription-disk-store-name"),
				(String) cacheServerApplicationAttributes.get("subscriptionDiskStoreName")));

			setSubscriptionEvictionPolicy(resolveProperty(cacheServerProperty("subscription-eviction-policy"),
				SubscriptionEvictionPolicy.class, (SubscriptionEvictionPolicy) cacheServerApplicationAttributes.get("subscriptionEvictionPolicy")));

			setTcpNoDelay(resolveProperty(cacheServerProperty("tcp-no-delay"),
				(Boolean) cacheServerApplicationAttributes.get("tcpNoDelay")));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return CacheServerApplication.class;
	}

	void setAutoStartup(boolean autoStartup) {
		this.autoStartup = autoStartup;
	}

	protected boolean isAutoStartup() {
		return this.autoStartup;
	}

	void setBindAddress(String bindAddress) {
		this.bindAddress = bindAddress;
	}

	protected String getBindAddress() {
		return Optional.ofNullable(this.bindAddress).filter(StringUtils::hasText)
			.orElse(CacheServer.DEFAULT_BIND_ADDRESS);
	}

	void setHostnameForClients(String hostnameForClients) {
		this.hostnameForClients = hostnameForClients;
	}

	protected String getHostnameForClients() {
		return Optional.ofNullable(this.hostnameForClients).filter(StringUtils::hasText)
			.orElse(CacheServer.DEFAULT_HOSTNAME_FOR_CLIENTS);
	}

	void setInterestRegistrationListeners(Set<InterestRegistrationListener> interestRegistrationListeners) {
		this.interestRegistrationListeners = interestRegistrationListeners;
	}

	protected Set<InterestRegistrationListener> getInterestRegistrationListeners() {
		return nullSafeSet(this.interestRegistrationListeners);
	}

	void setLoadPollInterval(Long loadPollInterval) {
		this.loadPollInterval = loadPollInterval;
	}

	protected Long getLoadPollInterval() {
		return Optional.ofNullable(this.loadPollInterval).orElse(CacheServer.DEFAULT_LOAD_POLL_INTERVAL);
	}

	void setMaxConnections(Integer maxConnections) {
		this.maxConnections = maxConnections;
	}

	protected Integer getMaxConnections() {
		return Optional.ofNullable(this.maxConnections).orElse(CacheServer.DEFAULT_MAX_CONNECTIONS);
	}

	void setMaxMessageCount(Integer maxMessageCount) {
		this.maxMessageCount = maxMessageCount;
	}

	protected Integer getMaxMessageCount() {
		return Optional.ofNullable(this.maxMessageCount).orElse(CacheServer.DEFAULT_MAXIMUM_MESSAGE_COUNT);
	}

	void setMaxThreads(Integer maxThreads) {
		this.maxThreads = maxThreads;
	}

	protected Integer getMaxThreads() {
		return Optional.ofNullable(this.maxThreads).orElse(CacheServer.DEFAULT_MAX_THREADS);
	}

	void setMaxTimeBetweenPings(Integer maxTimeBetweenPings) {
		this.maxTimeBetweenPings = maxTimeBetweenPings;
	}

	protected Integer getMaxTimeBetweenPings() {
		return Optional.ofNullable(this.maxTimeBetweenPings).orElse(CacheServer.DEFAULT_MAXIMUM_TIME_BETWEEN_PINGS);
	}

	void setMessageTimeToLive(Integer messageTimeToLive) {
		this.messageTimeToLive = messageTimeToLive;
	}

	protected Integer getMessageTimeToLive() {
		return Optional.ofNullable(this.messageTimeToLive).orElse(CacheServer.DEFAULT_MESSAGE_TIME_TO_LIVE);
	}

	void setPort(Integer port) {
		this.port = port;
	}

	protected Integer getPort() {
		return Optional.ofNullable(this.port).orElse(CacheServer.DEFAULT_PORT);
	}

	void setServerLoadProbe(ServerLoadProbe serverLoadProbe) {
		this.serverLoadProbe = serverLoadProbe;
	}

	protected ServerLoadProbe getServerLoadProbe() {
		return Optional.ofNullable(this.serverLoadProbe).orElse(CacheServer.DEFAULT_LOAD_PROBE);
	}

	void setSocketBufferSize(Integer socketBufferSize) {
		this.socketBufferSize = socketBufferSize;
	}

	protected Integer getSocketBufferSize() {
		return Optional.ofNullable(this.socketBufferSize).orElse(CacheServer.DEFAULT_SOCKET_BUFFER_SIZE);
	}

	void setSubscriptionCapacity(Integer subscriptionCapacity) {
		this.subscriptionCapacity = subscriptionCapacity;
	}

	protected Integer getSubscriptionCapacity() {
		return Optional.ofNullable(this.subscriptionCapacity).orElse(ClientSubscriptionConfig.DEFAULT_CAPACITY);
	}

	void setSubscriptionDiskStoreName(String subscriptionDiskStoreName) {
		this.subscriptionDiskStoreName = subscriptionDiskStoreName;
	}

	protected String getSubscriptionDiskStoreName() {
		return this.subscriptionDiskStoreName;
	}

	void setSubscriptionEvictionPolicy(SubscriptionEvictionPolicy subscriptionEvictionPolicy) {
		this.subscriptionEvictionPolicy = subscriptionEvictionPolicy;
	}

	protected SubscriptionEvictionPolicy getSubscriptionEvictionPolicy() {
		return Optional.ofNullable(this.subscriptionEvictionPolicy).orElse(SubscriptionEvictionPolicy.DEFAULT);
	}

	void setTcpNoDelay(Boolean tcpNoDelay) {
		this.tcpNoDelay = tcpNoDelay;
	}

	void setUseUdpMembershipMessenger(Boolean useUDPMembershipMessenger) {
		this.useUDPMembershipMessenger = useUDPMembershipMessenger;
	}

	protected Boolean getTcpNoDelay() {
		return Optional.ofNullable(this.tcpNoDelay).orElse(CacheServer.DEFAULT_TCP_NO_DELAY);
	}

	protected Boolean getUseUDPMembershipMessenger() {
		return Optional.ofNullable(this.useUDPMembershipMessenger).orElse(DEFAULT_USE_UDP_MEMBERSHIP_MESSENGER);
	}

	/**
	 * Returns a {@link String} containing the name of the Spring-configured Apache Geode {@link CacheServer} application
	 * and data node in the cluster.
	 *
	 * @return a {@link String} containing the name of the Spring-configured Apache Geode {@link CacheServer} application
	 * and data node in the cluster.
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return DEFAULT_NAME;
	}
}
