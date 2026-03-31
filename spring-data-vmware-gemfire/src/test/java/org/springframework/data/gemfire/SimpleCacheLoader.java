/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire;

import org.springframework.data.gemfire.gud.api.GudCacheLoader;
import org.springframework.data.gemfire.gud.api.GudCacheLoaderException;
import org.springframework.data.gemfire.gud.api.GudLoaderHelper;

/**
 * @author Costin Leau
 */
@SuppressWarnings("rawtypes")
public class SimpleCacheLoader implements GudCacheLoader {

	@Override
	public Object load(GudLoaderHelper helper) throws GudCacheLoaderException {
		return null;
	}

	@Override
	public void close() {
	}
}
