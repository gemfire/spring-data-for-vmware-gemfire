// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import org.apache.geode.cache.client.ClientCacheFactory;
import com.vmware.gemfire.internal.version.GemFireVersion;
import com.vmware.gemfire.driver.spi.PoolConfiguration;
import com.vmware.gemfire.driver.spi.UnsupportedFeatureException;

/** Apply only explicit settings to the cache factory's default-pool setters; the native factory owns defaults. */
final class GemFireDefaultPoolConfiguration {
	void apply(PoolConfiguration c, ClientCacheFactory factory) {
		if (c.threadLocalConnections().isPresent()) {
			throw UnsupportedFeatureException.removed("Pool.threadLocalConnections", GemFireVersion.getGemFireVersion());
		}
		c.minimumConnections().ifPresent(factory::setPoolMinConnections);
		c.maximumConnections().ifPresent(factory::setPoolMaxConnections);
		c.minimumConnectionsPerServer().ifPresent(factory::setPoolMinConnectionsPerServer);
		c.maximumConnectionsPerServer().ifPresent(factory::setPoolMaxConnectionsPerServer);
		c.socketConnectTimeout().ifPresent(factory::setPoolSocketConnectTimeout);
		c.freeConnectionTimeout().ifPresent(factory::setPoolFreeConnectionTimeout);
		c.serverConnectionTimeout().ifPresent(factory::setPoolServerConnectionTimeout);
		c.loadConditioningInterval().ifPresent(factory::setPoolLoadConditioningInterval);
		c.socketBufferSize().ifPresent(factory::setPoolSocketBufferSize);
		c.readTimeout().ifPresent(factory::setPoolReadTimeout);
		c.pingTimeout().ifPresent(factory::setPoolPingTimeout);
		c.idleTimeout().ifPresent(factory::setPoolIdleTimeout);
		c.pingInterval().ifPresent(factory::setPoolPingInterval);
		c.statisticInterval().ifPresent(factory::setPoolStatisticInterval);
		c.retryAttempts().ifPresent(factory::setPoolRetryAttempts);
		c.subscriptionEnabled().ifPresent(factory::setPoolSubscriptionEnabled);
		c.prSingleHopEnabled().ifPresent(factory::setPoolPRSingleHopEnabled);
		c.subscriptionRedundancy().ifPresent(factory::setPoolSubscriptionRedundancy);
		c.subscriptionMessageTrackingTimeout().ifPresent(factory::setPoolSubscriptionMessageTrackingTimeout);
		c.subscriptionAckInterval().ifPresent(factory::setPoolSubscriptionAckInterval);
		c.subscriptionTimeoutMultiplier().ifPresent(factory::setPoolSubscriptionTimeoutMultiplier);
		c.serverGroup().ifPresent(factory::setPoolServerGroup);
		c.availabilityZone().ifPresent(factory::setPoolAvailabilityZone);
		c.multiuserAuthentication().ifPresent(factory::setPoolMultiuserAuthentication);
		c.socketFactory().ifPresent(value -> factory.setPoolSocketFactory(new GemFireSocketFactory(value)));
		c.servers().forEach(endpoint -> factory.addPoolServer(endpoint.host(), endpoint.port()));
		c.locators().forEach(endpoint -> factory.addPoolLocator(endpoint.host(), endpoint.port()));
	}
}
