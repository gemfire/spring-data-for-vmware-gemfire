/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client.support;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.client.SocketFactory;
import org.apache.geode.cache.query.QueryService;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.data.gemfire.util.SpringExtensions;
import org.springframework.util.Assert;

/**
 * The {@link DefaultableDelegatingPoolAdapter} class is a wrapper class around {@link Pool}
 * allowing default configuration property values to be provided in the case that the {@link Pool Pool's}
 * settings were {@literal null}.
 *
 * @author John Blum
 * @see Pool
 * @since 1.8.0
 */
@SuppressWarnings("unused")
public abstract class DefaultableDelegatingPoolAdapter {

	private final Pool delegate;

	private Preference preference = Preference.PREFER_POOL;

	public static DefaultableDelegatingPoolAdapter from(Pool delegate) {
		return new DefaultableDelegatingPoolAdapter(delegate) {};
	}

	protected DefaultableDelegatingPoolAdapter(Pool delegate) {
		Assert.notNull(delegate, "Pool delegate must not be null");
		this.delegate = delegate;
	}

	protected Pool getDelegate() {
		return this.delegate;
	}

	protected DefaultableDelegatingPoolAdapter setPreference(Preference preference) {
		this.preference = preference;
		return this;
	}

	protected Preference getPreference() {
		return this.preference;
	}

	protected <T> T defaultIfNull(T defaultValue, Supplier<T> valueProvider) {

		return prefersPool() ? SpringExtensions.defaultIfNull(valueProvider.get(), defaultValue)
			: defaultValue != null ? defaultValue
			: valueProvider.get();
	}

	protected <E, T extends Collection<E>> T defaultIfEmpty(T defaultValue, Supplier<T> valueProvider) {

		if (prefersPool()) {
			T value = valueProvider.get();
			return CollectionUtils.isEmpty(value) ? defaultValue : value;
		}
		else {
			return CollectionUtils.isEmpty(defaultValue) ? valueProvider.get() : defaultValue;
		}
	}

	public DefaultableDelegatingPoolAdapter preferDefault() {
		return setPreference(Preference.PREFER_DEFAULT);
	}

	protected boolean prefersDefault() {
		return Preference.PREFER_DEFAULT.equals(getPreference());
	}

	public DefaultableDelegatingPoolAdapter preferPool() {
		return setPreference(Preference.PREFER_POOL);
	}

	protected boolean prefersPool() {
		return Preference.PREFER_POOL.equals(getPreference());
	}

	public boolean isDestroyed() {
		return getDelegate().isDestroyed();
	}

	public int getFreeConnectionTimeout(Integer defaultFreeConnectionTimeout) {
		return defaultIfNull(defaultFreeConnectionTimeout, () -> getDelegate().getFreeConnectionTimeout());
	}

	public long getIdleTimeout(Long defaultIdleTimeout) {
		return defaultIfNull(defaultIdleTimeout, () -> getDelegate().getIdleTimeout());
	}

	public int getLoadConditioningInterval(Integer defaultLoadConditioningInterval) {
		return defaultIfNull(defaultLoadConditioningInterval, () -> getDelegate().getLoadConditioningInterval());
	}

	public List<InetSocketAddress> getLocators(List<InetSocketAddress> defaultLocators) {
		return defaultIfEmpty(defaultLocators, () -> getDelegate().getLocators());
	}

	public int getMinConnections(Integer defaultMinConnections) {
		return defaultIfNull(defaultMinConnections, () -> getDelegate().getMinConnections());
	}

	public int getMaxConnections(Integer defaultMaxConnections) {
		return defaultIfNull(defaultMaxConnections, () -> getDelegate().getMaxConnections());
	}

	public int getMinConnectionsPerServer(Integer defaultMinConnectionsPerServer) {
		return defaultIfNull(defaultMinConnectionsPerServer, () -> getDelegate().getMinConnectionsPerServer());
	}

	public int getMaxConnectionsPerServer(Integer defaultMaxConnectionsPerServer) {
		return defaultIfNull(defaultMaxConnectionsPerServer, () -> getDelegate().getMaxConnectionsPerServer());
	}

	public boolean getMultiuserAuthentication(Boolean defaultMultiUserAuthentication) {
		return defaultIfNull(defaultMultiUserAuthentication, () -> getDelegate().getMultiuserAuthentication());
	}

	public String getName() {
		return getDelegate().getName();
	}

	public int getPendingEventCount() {
		return getDelegate().getPendingEventCount();
	}

	public long getPingInterval(Long defaultPingInterval) {
		return defaultIfNull(defaultPingInterval, () -> getDelegate().getPingInterval());
	}

	public boolean getPRSingleHopEnabled(Boolean defaultPrSingleHopEnabled) {
		return defaultIfNull(defaultPrSingleHopEnabled, () -> getDelegate().getPRSingleHopEnabled());
	}

	public QueryService getQueryService(QueryService defaultQueryService) {
		return defaultIfNull(defaultQueryService, () -> getDelegate().getQueryService());
	}

	public int getReadTimeout(Integer defaultReadTimeout) {
		return defaultIfNull(defaultReadTimeout, () -> getDelegate().getReadTimeout());
	}

	public int getRetryAttempts(Integer defaultRetryAttempts) {
		return defaultIfNull(defaultRetryAttempts, () -> getDelegate().getRetryAttempts());
	}

	public int getServerConnectionTimeout(Integer defaultServerConnectionTimeout) {
		return defaultIfNull(defaultServerConnectionTimeout, () -> getDelegate().getServerConnectionTimeout());
	}

	public String getServerGroup(String defaultServerGroup) {
		return defaultIfNull(defaultServerGroup, () -> getDelegate().getServerGroup());
	}

	public List<InetSocketAddress> getServers(List<InetSocketAddress> defaultServers) {
		return defaultIfEmpty(defaultServers, () -> getDelegate().getServers());
	}

	public int getSocketBufferSize(Integer defaultSocketBufferSize) {
		return defaultIfNull(defaultSocketBufferSize, () -> getDelegate().getSocketBufferSize());
	}

	public int getSocketConnectTimeout(Integer defaultSocketConnectTimeout) {
		return defaultIfNull(defaultSocketConnectTimeout, () -> getDelegate().getSocketConnectTimeout());
	}

	public SocketFactory getSocketFactory(SocketFactory defaultSocketFactory) {
		return defaultIfNull(defaultSocketFactory, () -> getDelegate().getSocketFactory());
	}

	public int getStatisticInterval(Integer defaultStatisticInterval) {
		return defaultIfNull(defaultStatisticInterval, () -> getDelegate().getStatisticInterval());
	}

	public int getSubscriptionAckInterval(Integer defaultSubscriptionAckInterval) {
		return defaultIfNull(defaultSubscriptionAckInterval, () -> getDelegate().getSubscriptionAckInterval());
	}

	public boolean getSubscriptionEnabled(Boolean defaultSubscriptionEnabled) {
		return defaultIfNull(defaultSubscriptionEnabled, () -> getDelegate().getSubscriptionEnabled());
	}

	public int getSubscriptionMessageTrackingTimeout(Integer defaultSubscriptionMessageTrackingTimeout) {
		return defaultIfNull(defaultSubscriptionMessageTrackingTimeout,
			() -> getDelegate().getSubscriptionMessageTrackingTimeout());
	}

	public int getSubscriptionRedundancy(Integer defaultSubscriptionRedundancy) {
		return defaultIfNull(defaultSubscriptionRedundancy, () -> getDelegate().getSubscriptionRedundancy());
	}

	public int getSubscriptionTimeoutMultiplier(Integer defaultSubscriptionTimeoutMultiplier) {
		return defaultIfNull(defaultSubscriptionTimeoutMultiplier,
			() -> getDelegate().getSubscriptionTimeoutMultiplier());
	}

	public void destroy() {
		getDelegate().destroy();
	}

	public void destroy(boolean keepAlive) {
		getDelegate().destroy(keepAlive);
	}

	enum Preference {

		PREFER_DEFAULT,
		PREFER_POOL

	}

	interface ValueProvider<T> {
		T getValue();
	}
}
