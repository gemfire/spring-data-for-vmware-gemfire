/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created CacheWriter using GUD API types only - no native GemFire references
 */

package com.example.testcaching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.gemfire.gud.api.GudCacheWriter;
import org.springframework.data.gemfire.gud.api.GudCacheWriterException;
import org.springframework.data.gemfire.gud.api.GudEntryEvent;
import org.springframework.data.gemfire.gud.api.GudRegionEvent;

/**
 * GUD CacheWriter implementation that filters out keys starting with "S" on beforeCreate.
 * This is attached to the RegionCachingProxy region.
 * 
 * IMPORTANT: This class uses ONLY GUD API types - no org.apache.geode imports.
 */
public class SKeyFilteringCacheWriter implements GudCacheWriter<String, Object> {

    private static final Logger logger = LoggerFactory.getLogger(SKeyFilteringCacheWriter.class);

    @Override
    public void beforeCreate(GudEntryEvent<String, Object> event) throws GudCacheWriterException {
        String key = event.getKey();
        if (key != null && key.startsWith("S")) {
            logger.warn("[RegionCachingProxy] Key '{}' starts with 'S' - FILTERING OUT!", key);
            System.out.println("[RegionCachingProxy] FILTERED: Key '" + key + "' starts with 'S' - entry rejected!");
            throw new GudCacheWriterException("Keys starting with 'S' are not allowed: " + key);
        }
        logger.info("[RegionCachingProxy] beforeCreate - Key '{}' accepted", key);
        System.out.println("[RegionCachingProxy] beforeCreate - Key '" + key + "' accepted");
    }

    @Override
    public void beforeUpdate(GudEntryEvent<String, Object> event) throws GudCacheWriterException {
        String key = event.getKey();
        if (key != null && key.startsWith("S")) {
            logger.warn("[RegionCachingProxy] Key '{}' starts with 'S' - FILTERING OUT update!", key);
            throw new GudCacheWriterException("Keys starting with 'S' are not allowed: " + key);
        }
    }

    @Override
    public void beforeDestroy(GudEntryEvent<String, Object> event) throws GudCacheWriterException {
        // Allow all destroys
    }

    @Override
    public void beforeRegionDestroy(GudRegionEvent<String, Object> event) throws GudCacheWriterException {
        // Allow region destroy
    }

    @Override
    public void beforeRegionClear(GudRegionEvent<String, Object> event) throws GudCacheWriterException {
        // Allow region clear
    }

    @Override
    public void close() {
        logger.info("SKeyFilteringCacheWriter closed");
    }
}
