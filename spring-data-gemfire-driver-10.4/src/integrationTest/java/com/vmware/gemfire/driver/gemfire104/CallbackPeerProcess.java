/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.geode.cache.*;

/** Separate native process: fixtures must not pretend two caches in one JVM are independent. */
public final class CallbackPeerProcess {
	public static void main(String[] args) throws Exception {
		if (args[0].equals("locator")) {
			var properties = new java.util.Properties();
			properties.setProperty("mcast-port", "0"); properties.setProperty("locators", "");
			properties.setProperty("enable-cluster-configuration", "false"); properties.setProperty("log-level", "error");
			properties.setProperty("member-timeout", "1000"); properties.setProperty("disable-auto-reconnect", "true");
			var locator = org.apache.geode.distributed.Locator.startLocatorAndDS(0, new java.io.File("locator.log"), properties);
			try {
				System.out.println("CALLBACK_READY:" + locator.getPort());
				new BufferedReader(new InputStreamReader(System.in)).readLine();
			} finally { locator.stop(); }
			return;
		}
		boolean server = args[0].equals("server");
		try (var cache = new CacheFactory().set("mcast-port", "0").set("locators", server ? "" : args[0])
			.set("roles", "worker").set("member-timeout", "1000").set("log-level", "error")
			.set("disable-auto-reconnect", "true").create()) {
			Region<String, String> region = cache.<String, String>createRegionFactory(RegionShortcut.REPLICATE).create("distributed");
			int port = 0;
			if (server) { var endpoint = cache.addCacheServer(); endpoint.setPort(0); endpoint.start(); port = endpoint.getPort(); }
			System.out.println("CALLBACK_READY:" + port);
			var input = new BufferedReader(new InputStreamReader(System.in));
			for (String command; (command = input.readLine()) != null;) {
				switch (command) {
					case "PUT" -> { region.put("key", "one"); region.put("key", "two"); }
					case "CLOSE_REGION" -> region.close();
					case "CREATE_REGION" -> region = cache.<String, String>createRegionFactory(RegionShortcut.REPLICATE).create("distributed");
					case "STOP" -> { return; }
					default -> throw new IllegalArgumentException("Unknown fixture command");
				}
				System.out.println("CALLBACK_DONE:" + command);
			}
		}
	}
}
