/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.fork;

import java.io.IOException;
import java.util.Scanner;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionFactory;
import org.apache.geode.cache.RegionShortcut;
import org.apache.geode.cache.server.CacheServer;

import org.springframework.data.gemfire.util.SpringExtensions;

/**
 * @author Costin Leau
 * @author John Blum
 */
public class CqCacheServerProcess {

	private static final int DEFAULT_CACHE_SERVER_PORT = CacheServer.DEFAULT_PORT;

	private static Region<String, Integer> testCqRegion;

	private static final String CACHE_SERVER_PORT_PROPERTY = "spring.data.gemfire.cache.server.port";
	private static final String GEMFIRE_LOG_LEVEL = "error";
	private static final String GEMFIRE_NAME = "CqServer";

	public static void main(final String[] args) throws Exception {
		waitForShutdown(registerShutdownHook(startCacheServer(addRegion(
			newGemFireCache(GEMFIRE_NAME, GEMFIRE_LOG_LEVEL), "test-cq"))));
	}

	private static Cache newGemFireCache(String name, String logLevel) {

		return new CacheFactory()
			.set("name", name)
			.set("log-level", logLevel)
			.create();
	}

	private static Cache addRegion(Cache gemfireCache, String name) {

		RegionFactory<String, Integer> regionFactory = gemfireCache.createRegionFactory(RegionShortcut.REPLICATE);

		testCqRegion = regionFactory.create(name);

		return gemfireCache;
	}

	private static Cache startCacheServer(Cache gemfireCache) throws IOException {

		CacheServer cacheServer = gemfireCache.addCacheServer();

		cacheServer.setPort(getCacheServerPort(DEFAULT_CACHE_SERVER_PORT));
		cacheServer.start();

		return gemfireCache;
	}

	private static int getCacheServerPort(int defaultPort) {
		return Integer.getInteger(CACHE_SERVER_PORT_PROPERTY, defaultPort);
	}

	private static Cache registerShutdownHook(Cache gemfireCache) {

		Runtime.getRuntime().addShutdownHook(new Thread(() -> SpringExtensions.safeDoOperation(() -> gemfireCache.close())));

		return gemfireCache;
	}

	@SuppressWarnings({ "unused" })
	private static void waitForShutdown(Cache gemfireCache) {

		Scanner scanner = new Scanner(System.in);

		scanner.nextLine();

		testCqRegion.put("one", 1);
		testCqRegion.put("two", 2);
		testCqRegion.put("three", 3);

		scanner.nextLine();
	}
}
