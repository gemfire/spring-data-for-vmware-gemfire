/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created CacheListener using GUD API types only - no native GemFire references
 */

package com.example.testcaching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.gemfire.gud.api.GudCacheListener;
import org.springframework.data.gemfire.gud.api.GudEntryEvent;
import org.springframework.data.gemfire.gud.api.GudRegionEvent;

/**
 * GUD CacheListener implementation that prints the key for every afterCreate event.
 * This is attached to the RegionProxy region.
 * 
 * IMPORTANT: This class uses ONLY GUD API types - no org.apache.geode imports.
 */
public class KeyPrintingCacheListener implements GudCacheListener<String, Object> {

    private static final Logger logger = LoggerFactory.getLogger(KeyPrintingCacheListener.class);

    @Override
    public void afterCreate(GudEntryEvent<String, Object> event) {
        String key = event.getKey();
        logger.info("=== AfterCreate Event ===");
        logger.info("Key: {}", key);
        logger.info("Region: {}", event.getRegion().getName());
        System.out.println("[RegionProxy] New entry created with key: " + key);
    }

    @Override
    public void afterUpdate(GudEntryEvent<String, Object> event) {
        logger.info("[RegionProxy] Entry updated with key: {}", event.getKey());
    }

    @Override
    public void afterInvalidate(GudEntryEvent<String, Object> event) {
        logger.info("[RegionProxy] Entry invalidated with key: {}", event.getKey());
    }

    @Override
    public void afterDestroy(GudEntryEvent<String, Object> event) {
        logger.info("[RegionProxy] Entry destroyed with key: {}", event.getKey());
    }

    @Override
    public void afterRegionInvalidate(GudRegionEvent<String, Object> event) {
        logger.info("[RegionProxy] Region invalidated: {}", event.getRegion().getName());
    }

    @Override
    public void afterRegionDestroy(GudRegionEvent<String, Object> event) {
        logger.info("[RegionProxy] Region destroyed: {}", event.getRegion().getName());
    }

    @Override
    public void afterRegionClear(GudRegionEvent<String, Object> event) {
        logger.info("[RegionProxy] Region cleared: {}", event.getRegion().getName());
    }

    @Override
    public void afterRegionCreate(GudRegionEvent<String, Object> event) {
        logger.info("[RegionProxy] Region created: {}", event.getRegion().getName());
    }

    @Override
    public void afterRegionLive(GudRegionEvent<String, Object> event) {
        logger.info("[RegionProxy] Region is live: {}", event.getRegion().getName());
    }

    @Override
    public void close() {
        logger.info("[RegionProxy] CacheListener closed");
    }
}
