/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import com.vmware.gemfire.driver.spi.*;

/** Isolated mock identity, explicitly distinct from a native distributed member. */
final class MockMember implements DriverMember {
	private final String id = "mock:" + UUID.randomUUID();
	@Override public String getId() { return id; }
	@Override public String getUniqueId() { return id; }
	@Override public String getName() { return ""; }
	@Override public String getHost() { return "localhost"; }
	@Override public int getProcessId() { return (int) ProcessHandle.current().pid(); }
	@Override public List<String> getGroups() { return List.of(); }
	@Override public Set<DriverRole> getRoles() { return Set.of(); }
	@Override public DriverDurableClientAttributes getDurableClientAttributes() { return new DriverDurableClientAttributes("", 300); }
	@Override public int compareTo(DriverMember other) { return id.compareTo(other.getId()); }
}
