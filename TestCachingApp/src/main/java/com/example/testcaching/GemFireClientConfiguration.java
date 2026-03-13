/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created Spring configuration class with @Bean methods for GemFire client setup
 * 2026-03-13: Refactored to use Spring Data GemFire annotations (@ClientCacheApplication, @EnableDiskStore)
 */

package com.example.testcaching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableDiskStore;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;

import org.springframework.data.gemfire.gud.api.GudCacheListener;
import org.springframework.data.gemfire.gud.api.GudCacheWriter;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientRegionShortcut;

/**
 * Spring Configuration class that creates GemFire client components using Spring Data GemFire annotations.
 * 
 * This configuration demonstrates the proper Spring Data GemFire approach:
 * - @ClientCacheApplication creates the client cache via GudCacheProvider
 * - @EnableDiskStore creates disk stores via GudClientCache
 * - ClientRegionFactoryBean creates regions with proper Spring lifecycle integration
 * 
 * All underlying GemFire interactions go through the GUD API and driver layer.
 */
@Configuration
@ComponentScan(basePackageClasses = GemFireClientConfiguration.class)
@ClientCacheApplication(
    name = "TestCachingApp",
    locators = @ClientCacheApplication.Locator(host = "localhost", port = 23232),
    subscriptionEnabled = true,
    readyForEvents = true
)
@EnableDiskStore(
    name = "CachingProxyDiskStore",
    autoCompact = true,
    diskDirectories = @EnableDiskStore.DiskDirectory(location = "./data/diskstore")
)
@EnableGemfireRepositories(basePackages = "com.example.testcaching.repository")
public class GemFireClientConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(GemFireClientConfiguration.class);

    private static final String DISK_STORE_NAME = "CachingProxyDiskStore";

    /**
     * Creates a CacheListener that prints the key for every afterCreate event.
     */
    @Bean
    public GudCacheListener<String, Object> keyPrintingCacheListener() {
        return new KeyPrintingCacheListener();
    }

    /**
     * Creates a CacheWriter that filters out keys starting with "S".
     */
    @Bean
    public GudCacheWriter<String, Object> sKeyFilteringCacheWriter() {
        return new SKeyFilteringCacheWriter();
    }

    /**
     * Creates the RegionProxy using ClientRegionFactoryBean for proper Spring lifecycle integration.
     * The FactoryBean returns a GudRegion when getObject() is called.
     */
    @Bean("RegionProxy")
    public ClientRegionFactoryBean<String, Object> regionProxy(GudClientCache clientCache, GudCacheListener<String, Object> keyPrintingCacheListener) {
        logger.info("Configuring ClientRegionFactoryBean 'RegionProxy' (PROXY) with CacheListener");
        
        ClientRegionFactoryBean<String, Object> factoryBean = new ClientRegionFactoryBean<>();
        factoryBean.setCache(clientCache);
        factoryBean.setShortcut(GudClientRegionShortcut.PROXY);
        factoryBean.setCacheListeners(new GudCacheListener[] { keyPrintingCacheListener });
        
        return factoryBean;
    }

    /**
     * Creates the RegionCachingProxy using ClientRegionFactoryBean for proper Spring lifecycle integration.
     * The FactoryBean returns a GudRegion when getObject() is called.
     */
    @Bean("RegionCachingProxy")
    @DependsOn("CachingProxyDiskStore")
    public ClientRegionFactoryBean<String, Object> regionCachingProxy(GudClientCache clientCache, GudCacheWriter<String, Object> sKeyFilteringCacheWriter) {
        logger.info("Configuring ClientRegionFactoryBean 'RegionCachingProxy' (CACHING_PROXY_OVERFLOW) with DiskStore and CacheWriter");
        
        ClientRegionFactoryBean<String, Object> factoryBean = new ClientRegionFactoryBean<>();
        factoryBean.setCache(clientCache);
        factoryBean.setShortcut(GudClientRegionShortcut.CACHING_PROXY_OVERFLOW);
        factoryBean.setDiskStoreName(DISK_STORE_NAME);
        factoryBean.setDiskSynchronous(true);
        factoryBean.setCacheWriter(sKeyFilteringCacheWriter);
        
        return factoryBean;
    }
}
