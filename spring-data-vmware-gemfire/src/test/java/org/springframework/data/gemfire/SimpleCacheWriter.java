/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire;

import org.springframework.data.gemfire.gud.api.GudCacheWriter;
import org.springframework.data.gemfire.gud.api.GudCacheWriterException;
import org.springframework.data.gemfire.gud.api.GudEntryEvent;
import org.springframework.data.gemfire.gud.api.GudRegionEvent;

/**
 * @author Costin Leau
 */
@SuppressWarnings("rawtypes")
public class SimpleCacheWriter implements GudCacheWriter {

	@Override
	public void beforeCreate(GudEntryEvent event) throws GudCacheWriterException { }

	@Override
	public void beforeUpdate(GudEntryEvent event) throws GudCacheWriterException { }

	@Override
	public void beforeDestroy(GudEntryEvent event) throws GudCacheWriterException { }

	@Override
	public void beforeRegionDestroy(GudRegionEvent event) throws GudCacheWriterException { }

	@Override
	public void beforeRegionClear(GudRegionEvent event) throws GudCacheWriterException { }
}
