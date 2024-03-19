/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client.support;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;

import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.client.SocketFactory;
import org.apache.geode.cache.query.QueryService;

/**
 * {@link DelegatingPoolAdapter} is an abstract implementation of GemFire's {@link Pool} interface and extension of
 * {@link FactoryDefaultsPoolAdapter} that delegates operations to the provided {@link Pool} instance.
 *
 * However, this implementation guards against a potentially <code>null</code> {@link Pool} reference by returning
 * default factory settings for the {@link Pool}'s configuration properties along with default behavior for operations
 * when the {@link Pool} reference is <code>null</code>.
 *
 * @author John Blum
 * @see java.net.InetSocketAddress
 * @see org.apache.geode.cache.client.Pool
 * @see org.apache.geode.cache.client.SocketFactory
 * @see org.apache.geode.cache.query.QueryService
 * @see org.springframework.data.gemfire.client.support.FactoryDefaultsPoolAdapter
 * @since 1.8.0
 */
@SuppressWarnings("unused")
public abstract class DelegatingPoolAdapter extends FactoryDefaultsPoolAdapter {

	private final Pool delegate;

	public static DelegatingPoolAdapter from(Pool delegate) {
		return new DelegatingPoolAdapter(delegate) {};
	}

	/**
	 * Constructs an instance of {@link DelegatingPoolAdapter} initialized with the specified {@link Pool}.
	 *
	 * @param delegate {@link Pool} used as the delegate; can be {@literal null}.
	 * @see org.apache.geode.cache.client.Pool
	 */
	public DelegatingPoolAdapter(Pool delegate) {
		this.delegate = delegate;
	}

	protected Pool getDelegate() {
		return this.delegate;
	}

	@Override
	public boolean isDestroyed() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::isDestroyed)
			.orElseGet(super::isDestroyed);
	}

	@Override
	public int getFreeConnectionTimeout() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getFreeConnectionTimeout)
			.orElseGet(super::getFreeConnectionTimeout);
	}

	@Override
	public long getIdleTimeout() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getIdleTimeout)
			.orElseGet(super::getIdleTimeout);
	}

	@Override
	public int getLoadConditioningInterval() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getLoadConditioningInterval)
			.orElseGet(super::getLoadConditioningInterval);
	}

	@Override
	public List<InetSocketAddress> getLocators() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getLocators)
			.orElseGet(super::getLocators);
	}

	@Override
	public int getMaxConnections() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getMaxConnections)
			.orElseGet(super::getMaxConnections);
	}

	@Override
	public int getMinConnections() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getMinConnections)
			.orElseGet(super::getMinConnections);
	}

	@Override
	public boolean getMultiuserAuthentication() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getMultiuserAuthentication)
			.orElseGet(super::getMultiuserAuthentication);
	}

	@Override
	public String getName() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getName)
			.orElseGet(super::getName);
	}

	@Override
	public List<InetSocketAddress> getOnlineLocators() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getOnlineLocators)
			.orElseGet(super::getOnlineLocators);
	}

	@Override
	public int getPendingEventCount() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getPendingEventCount)
			.orElse(0);
	}

	@Override
	public long getPingInterval() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getPingInterval)
			.orElseGet(super::getPingInterval);
	}

	@Override
	public boolean getPRSingleHopEnabled() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getPRSingleHopEnabled)
			.orElseGet(super::getPRSingleHopEnabled);
	}

	@Override
	public QueryService getQueryService() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getQueryService)
			.orElseGet(super::getQueryService);
	}

	@Override
	public int getReadTimeout() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getReadTimeout)
			.orElseGet(super::getReadTimeout);
	}

	@Override
	public int getRetryAttempts() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getRetryAttempts)
			.orElseGet(super::getRetryAttempts);
	}

	@Override
	public int getServerConnectionTimeout() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getServerConnectionTimeout)
			.orElseGet(super::getServerConnectionTimeout);
	}

	@Override
	public String getServerGroup() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getServerGroup)
			.orElseGet(super::getServerGroup);
	}

	@Override
	public List<InetSocketAddress> getServers() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getServers)
			.orElseGet(super::getServers);
	}

	@Override
	public int getSocketBufferSize() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getSocketBufferSize)
			.orElseGet(super::getSocketBufferSize);
	}

	@Override
	public int getSocketConnectTimeout() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getSocketConnectTimeout)
			.orElseGet(super::getSocketConnectTimeout);
	}

	@Override
	public SocketFactory getSocketFactory() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getSocketFactory)
			.orElseGet(super::getSocketFactory);
	}

	@Override
	public int getStatisticInterval() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getStatisticInterval)
			.orElseGet(super::getStatisticInterval);
	}

	@Override
	public int getSubscriptionAckInterval() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getSubscriptionAckInterval)
			.orElseGet(super::getSubscriptionAckInterval);
	}

	@Override
	public boolean getSubscriptionEnabled() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getSubscriptionEnabled)
			.orElseGet(super::getSubscriptionEnabled);
	}

	@Override
	public int getSubscriptionMessageTrackingTimeout() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getSubscriptionMessageTrackingTimeout)
			.orElseGet(super::getSubscriptionMessageTrackingTimeout);
	}

	@Override
	public int getSubscriptionRedundancy() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getSubscriptionRedundancy)
			.orElseGet(super::getSubscriptionRedundancy);
	}

	@Override
	public int getSubscriptionTimeoutMultiplier() {
		return Optional.ofNullable(getDelegate()).map(Pool::getSubscriptionTimeoutMultiplier)
			.orElseGet(super::getSubscriptionTimeoutMultiplier);
	}

	@Override
	public boolean getThreadLocalConnections() {

		return Optional.ofNullable(getDelegate())
			.map(Pool::getThreadLocalConnections)
			.orElseGet(super::getThreadLocalConnections);
	}

	@Override
	public void destroy() {
		Optional.ofNullable(getDelegate()).ifPresent(Pool::destroy);
	}

	@Override
	public void destroy(boolean keepAlive) {
		Optional.ofNullable(getDelegate()).ifPresent(delegate -> delegate.destroy(keepAlive));
	}

	@Override
	public void releaseThreadLocalConnection() {
		Optional.ofNullable(getDelegate()).ifPresent(Pool::releaseThreadLocalConnection);
	}
}
