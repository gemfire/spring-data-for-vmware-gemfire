/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-02: Created unit tests verifying GUD API calls are translated to native GemFire 10.0 ClientCacheFactory calls
 */

package org.springframework.data.gemfire.gud.driver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.apache.geode.cache.client.ClientCacheFactory;

import org.junit.Test;

import org.springframework.data.gemfire.gud.api.GudClientCacheFactory;
import org.springframework.data.gemfire.gud.api.GudUnsupportedOperationException;

/**
 * Unit Tests verifying that {@link GemFireClientCacheFactory} (GemFire 10.0 driver) correctly
 * translates GUD API calls into native {@link ClientCacheFactory} calls.
 *
 * <p>Per-server connection limit methods are NOT supported in GemFire 10.0 and should throw
 * {@link GudUnsupportedOperationException}.
 */
public class GemFireClientCacheFactoryUnitTest {

	private ClientCacheFactory nativeFactory = mock(ClientCacheFactory.class);
	private GemFireClientCacheFactory gudFactory = new GemFireClientCacheFactory(nativeFactory);

	@Test
	public void setPropertyPropagatestoNativeFactory() {
		GudClientCacheFactory result = gudFactory.set("name", "TestCache");
		verify(nativeFactory).set("name", "TestCache");
		assertThat(result).isSameAs(gudFactory);
	}

	@Test
	public void setPdxReadSerializedPropagatestoNativeFactory() {
		gudFactory.setPdxReadSerialized(true);
		verify(nativeFactory).setPdxReadSerialized(true);
	}

	@Test
	public void setPdxDiskStorePropagatestoNativeFactory() {
		gudFactory.setPdxDiskStore("testDiskStore");
		verify(nativeFactory).setPdxDiskStore("testDiskStore");
	}

	@Test
	public void setPdxPersistentPropagatestoNativeFactory() {
		gudFactory.setPdxPersistent(true);
		verify(nativeFactory).setPdxPersistent(true);
	}

	@Test
	public void setPdxIgnoreUnreadFieldsPropagatestoNativeFactory() {
		gudFactory.setPdxIgnoreUnreadFields(true);
		verify(nativeFactory).setPdxIgnoreUnreadFields(true);
	}

	@Test
	public void addPoolLocatorPropagatestoNativeFactory() {
		gudFactory.addPoolLocator("locator-host", 10334);
		verify(nativeFactory).addPoolLocator("locator-host", 10334);
	}

	@Test
	public void addPoolServerPropagatestoNativeFactory() {
		gudFactory.addPoolServer("server-host", 40404);
		verify(nativeFactory).addPoolServer("server-host", 40404);
	}

	@Test
	public void setPoolFreeConnectionTimeoutPropagatestoNativeFactory() {
		gudFactory.setPoolFreeConnectionTimeout(5000);
		verify(nativeFactory).setPoolFreeConnectionTimeout(5000);
	}

	@Test
	public void setPoolIdleTimeoutPropagatestoNativeFactory() {
		gudFactory.setPoolIdleTimeout(60000L);
		verify(nativeFactory).setPoolIdleTimeout(60000L);
	}

	@Test
	public void setPoolLoadConditioningIntervalPropagatestoNativeFactory() {
		gudFactory.setPoolLoadConditioningInterval(300000);
		verify(nativeFactory).setPoolLoadConditioningInterval(300000);
	}

	@Test
	public void setPoolMinConnectionsPropagatestoNativeFactory() {
		gudFactory.setPoolMinConnections(5);
		verify(nativeFactory).setPoolMinConnections(5);
	}

	@Test
	public void setPoolMaxConnectionsPropagatestoNativeFactory() {
		gudFactory.setPoolMaxConnections(100);
		verify(nativeFactory).setPoolMaxConnections(100);
	}

	@Test
	public void setPoolMultiuserAuthenticationPropagatestoNativeFactory() {
		gudFactory.setPoolMultiuserAuthentication(true);
		verify(nativeFactory).setPoolMultiuserAuthentication(true);
	}

	@Test
	public void setPoolPingIntervalPropagatestoNativeFactory() {
		gudFactory.setPoolPingInterval(10000L);
		verify(nativeFactory).setPoolPingInterval(10000L);
	}

	@Test
	public void setPoolPRSingleHopEnabledPropagatestoNativeFactory() {
		gudFactory.setPoolPRSingleHopEnabled(true);
		verify(nativeFactory).setPoolPRSingleHopEnabled(true);
	}

	@Test
	public void setPoolReadTimeoutPropagatestoNativeFactory() {
		gudFactory.setPoolReadTimeout(15000);
		verify(nativeFactory).setPoolReadTimeout(15000);
	}

	@Test
	public void setPoolRetryAttemptsPropagatestoNativeFactory() {
		gudFactory.setPoolRetryAttempts(3);
		verify(nativeFactory).setPoolRetryAttempts(3);
	}

	@Test
	public void setPoolServerConnectionTimeoutPropagatestoNativeFactory() {
		gudFactory.setPoolServerConnectionTimeout(10000);
		verify(nativeFactory).setPoolServerConnectionTimeout(10000);
	}

	@Test
	public void setPoolServerGroupPropagatestoNativeFactory() {
		gudFactory.setPoolServerGroup("testGroup");
		verify(nativeFactory).setPoolServerGroup("testGroup");
	}

	@Test
	public void setPoolSocketBufferSizePropagatestoNativeFactory() {
		gudFactory.setPoolSocketBufferSize(32768);
		verify(nativeFactory).setPoolSocketBufferSize(32768);
	}

	@Test
	public void setPoolSocketConnectTimeoutPropagatestoNativeFactory() {
		gudFactory.setPoolSocketConnectTimeout(5000);
		verify(nativeFactory).setPoolSocketConnectTimeout(5000);
	}

	@Test
	public void setPoolStatisticIntervalPropagatestoNativeFactory() {
		gudFactory.setPoolStatisticInterval(1000);
		verify(nativeFactory).setPoolStatisticInterval(1000);
	}

	@Test
	public void setPoolSubscriptionAckIntervalPropagatestoNativeFactory() {
		gudFactory.setPoolSubscriptionAckInterval(100);
		verify(nativeFactory).setPoolSubscriptionAckInterval(100);
	}

	@Test
	public void setPoolSubscriptionEnabledPropagatestoNativeFactory() {
		gudFactory.setPoolSubscriptionEnabled(true);
		verify(nativeFactory).setPoolSubscriptionEnabled(true);
	}

	@Test
	public void setPoolSubscriptionMessageTrackingTimeoutPropagatestoNativeFactory() {
		gudFactory.setPoolSubscriptionMessageTrackingTimeout(900000);
		verify(nativeFactory).setPoolSubscriptionMessageTrackingTimeout(900000);
	}

	@Test
	public void setPoolSubscriptionRedundancyPropagatestoNativeFactory() {
		gudFactory.setPoolSubscriptionRedundancy(1);
		verify(nativeFactory).setPoolSubscriptionRedundancy(1);
	}

	@Test
	public void setPoolMinConnectionsPerServerThrowsUnsupportedOperationException() {
		assertThatThrownBy(() -> gudFactory.setPoolMinConnectionsPerServer(5))
			.isInstanceOf(GudUnsupportedOperationException.class)
			.hasMessageContaining("10.1");
	}

	@Test
	public void setPoolMaxConnectionsPerServerThrowsUnsupportedOperationException() {
		assertThatThrownBy(() -> gudFactory.setPoolMaxConnectionsPerServer(25))
			.isInstanceOf(GudUnsupportedOperationException.class)
			.hasMessageContaining("10.1");
	}
}
