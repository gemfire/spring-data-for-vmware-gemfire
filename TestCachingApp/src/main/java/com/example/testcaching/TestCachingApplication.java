/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created test application for GUD driver validation - uses ONLY GUD API types
 */

package com.example.testcaching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudRegion;

/**
 * Test application to validate the GUD driver implementation.
 * 
 * IMPORTANT: This application uses ONLY GUD API types - no direct references
 * to native GemFire (org.apache.geode) classes. The driver handles all native
 * interactions internally.
 */
public class TestCachingApplication {

    private static final Logger logger = LoggerFactory.getLogger(TestCachingApplication.class);

    public static void main(String[] args) {
        logger.info("Starting TestCachingApplication using GUD API only...");
        
        try (AnnotationConfigApplicationContext context = 
                new AnnotationConfigApplicationContext(GemFireClientConfiguration.class)) {
            
            // Retrieve beans to verify they were created
            // Note: Spring FactoryBean returns the produced object, not the factory itself
            GudClientCache clientCache = context.getBean(GudClientCache.class);
            GudRegion<String, Object> regionProxy = context.getBean("RegionProxy", GudRegion.class);
            GudRegion<String, Object> regionCachingProxy = context.getBean("RegionCachingProxy", GudRegion.class);
            
            logger.info("===========================================");
            logger.info("TestCachingApplication initialized successfully!");
            logger.info("Beans created:");
            logger.info("  - ClientCache: {}", clientCache.getName());
            logger.info("  - RegionProxy: {}", regionProxy.getName());
            logger.info("  - RegionCachingProxy: {}", regionCachingProxy.getName());
            logger.info("===========================================");
            
            // Keep the application running
            logger.info("Application is running. Press Ctrl+C to exit.");
            Thread.currentThread().join();
            
        } catch (InterruptedException e) {
            logger.info("Application interrupted");
        } catch (Exception e) {
            logger.error("Failed to start application", e);
            System.exit(1);
        }
    }
}
