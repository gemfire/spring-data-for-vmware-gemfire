/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.geode.cache.*;

/** Separate native process: fixtures must not pretend two caches in one JVM are independent. */
public final class NativeServerProcess {
	public static void main(String[] args) throws Exception {
		try (var cache = new CacheFactory().set("mcast-port", "0").set("locators", "")
			.set("member-timeout", "1000").set("log-level", "error")
			.set("disable-auto-reconnect", "true").create()) {
			Region<String, String> region = cache.<String, String>createRegionFactory(RegionShortcut.REPLICATE).create("distributed");
			var endpoint = cache.addCacheServer(); endpoint.setPort(0); endpoint.start(); int port = endpoint.getPort();
			System.out.println("CALLBACK_READY:" + port);
			var input = new BufferedReader(new InputStreamReader(System.in));
			for (String command; (command = input.readLine()) != null;) {
				switch (command) {
					case "PUT" -> { region.put("key", "one"); region.put("key", "two"); }
					case "STOP" -> { return; }
					default -> throw new IllegalArgumentException("Unknown fixture command");
				}
				System.out.println("CALLBACK_DONE:" + command);
			}
		}
	}
}
