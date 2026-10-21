/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import org.apache.geode.cache.client.PoolFactory;
import com.vmware.gemfire.internal.version.GemFireVersion;
import com.vmware.gemfire.driver.spi.PoolConfiguration;
import com.vmware.gemfire.driver.spi.UnsupportedFeatureException;

/** Apply only explicit settings. The native factory owns defaults and cross-setting validation. */
final class GemFirePoolConfiguration {
	void apply(PoolConfiguration c, PoolFactory factory) {
		if (c.threadLocalConnections().isPresent()) {
			throw UnsupportedFeatureException.removed("Pool.threadLocalConnections", GemFireVersion.getGemFireVersion());
		}
		c.minimumConnections().ifPresent(factory::setMinConnections);
		c.maximumConnections().ifPresent(factory::setMaxConnections);
		c.minimumConnectionsPerServer().ifPresent(factory::setMinConnectionsPerServer);
		c.maximumConnectionsPerServer().ifPresent(factory::setMaxConnectionsPerServer);
		c.socketConnectTimeout().ifPresent(factory::setSocketConnectTimeout);
		c.freeConnectionTimeout().ifPresent(factory::setFreeConnectionTimeout);
		c.serverConnectionTimeout().ifPresent(factory::setServerConnectionTimeout);
		c.loadConditioningInterval().ifPresent(factory::setLoadConditioningInterval);
		c.socketBufferSize().ifPresent(factory::setSocketBufferSize);
		c.readTimeout().ifPresent(factory::setReadTimeout);
		c.pingTimeout().ifPresent(factory::setPingTimeout);
		c.idleTimeout().ifPresent(factory::setIdleTimeout);
		c.pingInterval().ifPresent(factory::setPingInterval);
		c.statisticInterval().ifPresent(factory::setStatisticInterval);
		c.retryAttempts().ifPresent(factory::setRetryAttempts);
		c.subscriptionEnabled().ifPresent(factory::setSubscriptionEnabled);
		c.prSingleHopEnabled().ifPresent(factory::setPRSingleHopEnabled);
		c.subscriptionRedundancy().ifPresent(factory::setSubscriptionRedundancy);
		c.subscriptionMessageTrackingTimeout().ifPresent(factory::setSubscriptionMessageTrackingTimeout);
		c.subscriptionAckInterval().ifPresent(factory::setSubscriptionAckInterval);
		c.subscriptionTimeoutMultiplier().ifPresent(factory::setSubscriptionTimeoutMultiplier);
		c.serverGroup().ifPresent(factory::setServerGroup);
		c.availabilityZone().ifPresent(factory::setAvailabilityZone);
		c.multiuserAuthentication().ifPresent(factory::setMultiuserAuthentication);
		c.socketFactory().ifPresent(value -> factory.setSocketFactory(new GemFireSocketFactory(value)));
		c.servers().forEach(endpoint -> factory.addServer(endpoint.host(), endpoint.port()));
		c.locators().forEach(endpoint -> factory.addLocator(endpoint.host(), endpoint.port()));
	}
}
