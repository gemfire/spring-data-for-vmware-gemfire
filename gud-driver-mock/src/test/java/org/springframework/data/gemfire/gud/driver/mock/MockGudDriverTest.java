/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-17: Smoke test for MockGudDriver - verifies ServiceLoader discovery and that
 *             the provider can produce a working client cache from the mock
 */

package org.springframework.data.gemfire.gud.driver.mock;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientCacheFactory;
import org.springframework.data.gemfire.gud.api.GudClientRegionFactory;
import org.springframework.data.gemfire.gud.api.GudClientRegionShortcut;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudCacheProvider;
import org.springframework.data.gemfire.gud.core.GudDriver;
import org.springframework.data.gemfire.gud.core.GudDriverManager;

public class MockGudDriverTest {

    @Rule
    public GudResetRule gudReset = new GudResetRule();

    @Before
    public void resetIsPerformedByRule() {
        // noop - rule clears state before each test
    }

    @Test
    public void driverIsDiscoveredViaServiceLoader() {
        GudDriver driver = GudDriverManager.getDefaultDriver();
        assertThat(driver).isInstanceOf(MockGudDriver.class);
        assertThat(driver.getName()).isEqualTo("gemfire-mock");
        assertThat(driver.getSupportedVersion()).isEqualTo("10.99");
    }

    @Test
    public void mockDriverAdvertisesAllCapabilities() {
        GudDriver driver = GudDriverManager.getDefaultDriver();
        // See GudCapability.values() — every entry must be supported by the mock.
        assertThat(driver.supportsPerServerConnectionLimits()).isTrue();
        assertThat(driver.supportsDiskStoreSegments()).isTrue();
        assertThat(driver.supportsServerRegionName()).isTrue();
    }

    @Test
    public void providerProducesFreshFactoryEachCall() {
        GudClientCacheFactory f1 = GudCacheProvider.createClientCacheFactory();
        GudClientCacheFactory f2 = GudCacheProvider.createClientCacheFactory();
        assertThat(f1).isNotNull();
        assertThat(f2).isNotNull();
        assertThat(f1).isNotSameAs(f2);
    }

    @Test
    public void fluentSettersReturnSelf() {
        GudClientCacheFactory f = GudCacheProvider.createClientCacheFactory();
        GudClientCacheFactory after = f.set("a", "b")
            .setPdxReadSerialized(true)
            .setPdxPersistent(false)
            .addPoolLocator("host", 1234)
            .setPoolMinConnectionsPerServer(1); // requires PER_SERVER_CONNECTION_LIMITS
        assertThat(after).isSameAs(f);
    }

    @Test
    public void clientCacheAndRegionWorkEndToEnd() {
        GudClientCache cache = GudCacheProvider.createClientCacheFactory().create();
        assertThat(cache).isNotNull();
        assertThat(cache.isClosed()).isFalse();

        GudClientRegionFactory<String, String> regionFactory =
            cache.createClientRegionFactory(GudClientRegionShortcut.PROXY);
        GudRegion<String, String> region = regionFactory.create("Example");

        assertThat(region.getName()).isEqualTo("Example");
        region.put("k", "v");
        assertThat(region.get("k")).isEqualTo("v");
        assertThat(region.size()).isEqualTo(1);

        GudRegion<String, String> lookup = cache.getRegion("Example");
        assertThat(lookup).isSameAs(region);

        cache.close();
        assertThat(cache.isClosed()).isTrue();
    }

    @Test
    public void resetRuleIsolatesTestsAcrossRuns() {
        GudClientCache cache = GudCacheProvider.createClientCacheFactory().create();
        GudClientRegionFactory<String, String> regionFactory =
            cache.createClientRegionFactory(GudClientRegionShortcut.PROXY);
        regionFactory.create("Isolated");
        assertThat(cache.getRegion("Isolated")).isNotNull();
    }
}
