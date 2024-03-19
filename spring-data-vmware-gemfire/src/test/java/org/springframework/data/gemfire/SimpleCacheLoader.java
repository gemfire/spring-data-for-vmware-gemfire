/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire;

import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheLoaderException;
import org.apache.geode.cache.LoaderHelper;

/**
 * @author Costin Leau
 */
@SuppressWarnings("rawtypes")
public class SimpleCacheLoader implements CacheLoader {

	@Override
	public Object load(LoaderHelper helper) throws CacheLoaderException {
		return null;
	}

	@Override
	public void close() {
	}
}
