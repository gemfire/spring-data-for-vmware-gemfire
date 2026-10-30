/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import java.util.List;
import java.util.UUID;
import java.util.Properties;
import java.util.ArrayList;
import java.util.StringTokenizer;
import com.vmware.gemfire.driver.spi.*;

/** Isolated mock identity, explicitly distinct from a native distributed member. */
final class MockMember implements DriverMember {
	private final String id = "mock:" + UUID.randomUUID();
	private final String name;
	private final List<String> groups;
	private final DriverDurableClientAttributes durable;

	MockMember(Properties properties) {
		name = properties.getProperty("name", "");
		var parsedGroups = new ArrayList<String>();
		for (String key : List.of("roles", "groups")) {
			var tokens = new StringTokenizer(properties.getProperty(key, ""), ",");
			while (tokens.hasMoreTokens()) {
				String group = tokens.nextToken().trim();
				if (!parsedGroups.contains(group)) parsedGroups.add(group);
			}
		}
		groups = List.copyOf(parsedGroups);
		int timeout;
		try { timeout = Integer.parseInt(properties.getProperty("durable-client-timeout", "300")); }
		catch (NumberFormatException failure) {
			throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "Invalid durable timeout", failure);
		}
		durable = new DriverDurableClientAttributes(properties.getProperty("durable-client-id", ""), timeout);
	}

	@Override public String getId() { return id; }
	@Override public String getUniqueId() { return id; }
	@Override public String getName() { return name; }
	@Override public String getHost() { return "localhost"; }
	@Override public int getProcessId() { return (int) ProcessHandle.current().pid(); }
	@Override public List<String> getGroups() { return groups; }
	@Override public DriverDurableClientAttributes getDurableClientAttributes() { return durable; }
	@Override public int compareTo(DriverMember other) { return id.compareTo(other.getId()); }
}
