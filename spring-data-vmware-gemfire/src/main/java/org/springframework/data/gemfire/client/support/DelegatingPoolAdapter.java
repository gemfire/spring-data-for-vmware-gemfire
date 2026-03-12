/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-12: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.client.support;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;

import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudQueryService;
import org.springframework.data.gemfire.gud.api.GudSocketFactory;

/**
 * {@link DelegatingPoolAdapter} is an abstract implementation of GemFire's {@link GudPool} interface and extension of
 * {@link FactoryDefaultsPoolAdapter} that delegates operations to the provided {@link GudPool} instance.
 *
 * However, this implementation guards against a potentially <code>null</code> {@link GudPool} reference by returning
 * default factory settings for the {@link GudPool}'s configuration properties along with default behavior for operations
 * when the {@link GudPool} reference is <code>null</code>.
 *
 * @author John Blum
 * @see InetSocketAddress
 * @see GudPool
 * @see GudSocketFactory
 * @see GudQueryService
 * @see FactoryDefaultsPoolAdapter
 * @since 1.8.0
 */
@SuppressWarnings("unused")
public abstract class DelegatingPoolAdapter extends FactoryDefaultsPoolAdapter {

	private final GudPool delegate;

	public static DelegatingPoolAdapter from(GudPool delegate) {
		return new DelegatingPoolAdapter(delegate) {};
	}

	/**
	 * Constructs an instance of {@link DelegatingPoolAdapter} initialized with the specified {@link GudPool}.
	 *
	 * @param delegate {@link GudPool} used as the delegate; can be {@literal null}.
	 * @see GudPool
	 */
	public DelegatingPoolAdapter(GudPool delegate) {
		this.delegate = delegate;
	}

	protected GudPool getDelegate() {
		return this.delegate;
	}

	@Override
	public boolean isDestroyed() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::isDestroyed)
			.orElseGet(super::isDestroyed);
	}

	@Override
	public int getFreeConnectionTimeout() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::getFreeConnectionTimeout)
			.orElseGet(super::getFreeConnectionTimeout);
	}

	@Override
	public long getIdleTimeout() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::getIdleTimeout)
			.orElseGet(super::getIdleTimeout);
	}

	@Override
	public int getLoadConditioningInterval() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::getLoadConditioningInterval)
			.orElseGet(super::getLoadConditioningInterval);
	}

	@Override
	public List<InetSocketAddress> getLocators() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::getLocators)
			.orElseGet(super::getLocators);
	}

	@Override
	public int getMaxConnections() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::getMaxConnections)
			.orElseGet(super::getMaxConnections);
	}

	@Override
	public int getMinConnections() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::getMinConnections)
			.orElseGet(super::getMinConnections);
	}

	@Override
	public int getMaxConnectionsPerServer() {

		return Optional.ofNullable(getDelegate())
				.map(GudPool::getMaxConnectionsPerServer)
				.orElseGet(super::getMaxConnectionsPerServer);
	}

	@Override
	public int getMinConnectionsPerServer() {

		return Optional.ofNullable(getDelegate())
				.map(GudPool::getMinConnectionsPerServer)
				.orElseGet(super::getMinConnectionsPerServer);
	}

	@Override
	public boolean getMultiuserAuthentication() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::getMultiuserAuthentication)
			.orElseGet(super::getMultiuserAuthentication);
	}

	@Override
	public String getName() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::getName)
			.orElseGet(super::getName);
	}

	@Override
	public List<InetSocketAddress> getOnlineLocators() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::getOnlineLocators)
			.orElseGet(super::getOnlineLocators);
	}

	@Override
	public int getPendingEventCount() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::getPendingEventCount)
			.orElse(0);
	}

	@Override
	public long getPingInterval() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::getPingInterval)
			.orElseGet(super::getPingInterval);
	}

	@Override
	public boolean getPRSingleHopEnabled() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::getPRSingleHopEnabled)
			.orElseGet(super::getPRSingleHopEnabled);
	}

	@Override
	public GudQueryService getQueryService() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::getQueryService)
			.orElseGet(super::getQueryService);
	}

	@Override
	public int getReadTimeout() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::getReadTimeout)
			.orElseGet(super::getReadTimeout);
	}

	@Override
	public int getRetryAttempts() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::getRetryAttempts)
			.orElseGet(super::getRetryAttempts);
	}

	@Override
	public int getServerConnectionTimeout() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::getServerConnectionTimeout)
			.orElseGet(super::getServerConnectionTimeout);
	}

	@Override
	public String getServerGroup() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::getServerGroup)
			.orElseGet(super::getServerGroup);
	}

	@Override
	public List<InetSocketAddress> getServers() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::getServers)
			.orElseGet(super::getServers);
	}

	@Override
	public int getSocketBufferSize() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::getSocketBufferSize)
			.orElseGet(super::getSocketBufferSize);
	}

	@Override
	public int getSocketConnectTimeout() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::getSocketConnectTimeout)
			.orElseGet(super::getSocketConnectTimeout);
	}

	@Override
	public GudSocketFactory getSocketFactory() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::getSocketFactory)
			.orElseGet(super::getSocketFactory);
	}

	@Override
	public int getStatisticInterval() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::getStatisticInterval)
			.orElseGet(super::getStatisticInterval);
	}

	@Override
	public int getSubscriptionAckInterval() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::getSubscriptionAckInterval)
			.orElseGet(super::getSubscriptionAckInterval);
	}

	@Override
	public boolean getSubscriptionEnabled() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::getSubscriptionEnabled)
			.orElseGet(super::getSubscriptionEnabled);
	}

	@Override
	public int getSubscriptionMessageTrackingTimeout() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::getSubscriptionMessageTrackingTimeout)
			.orElseGet(super::getSubscriptionMessageTrackingTimeout);
	}

	@Override
	public int getSubscriptionRedundancy() {

		return Optional.ofNullable(getDelegate())
			.map(GudPool::getSubscriptionRedundancy)
			.orElseGet(super::getSubscriptionRedundancy);
	}

	@Override
	public int getSubscriptionTimeoutMultiplier() {
		return Optional.ofNullable(getDelegate()).map(GudPool::getSubscriptionTimeoutMultiplier)
			.orElseGet(super::getSubscriptionTimeoutMultiplier);
	}

	@Override
	public void destroy() {
		Optional.ofNullable(getDelegate()).ifPresent(GudPool::destroy);
	}

	@Override
	public void destroy(boolean keepAlive) {
		Optional.ofNullable(getDelegate()).ifPresent(delegate -> delegate.destroy(keepAlive));
	}
}
