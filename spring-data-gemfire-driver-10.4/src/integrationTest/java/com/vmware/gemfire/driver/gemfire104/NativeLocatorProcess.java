/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.geode.distributed.Locator;

/** Private locator-only process using the existing fixture control protocol. */
public final class NativeLocatorProcess {
	public static void main(String[] args) throws Exception {
		var properties = new Properties();
		properties.setProperty("mcast-port", "0");
		properties.setProperty("locators", "");
		properties.setProperty("log-level", "error");
		properties.setProperty("enable-cluster-configuration", "false");
		properties.setProperty("jmx-manager", "false");
		properties.setProperty("http-service-port", "0");
		properties.setProperty("disable-auto-reconnect", "true");
		var locator = Locator.startLocatorAndDS(0, new File("locator.log"), properties);
		try {
			System.out.println("CALLBACK_READY:" + locator.getPort());
			var input = new BufferedReader(new InputStreamReader(System.in));
			for (String command; (command = input.readLine()) != null;) {
				if (command.equals("STOP")) return;
				throw new IllegalArgumentException("Unknown fixture command");
			}
		} finally { locator.stop(); }
	}
}
