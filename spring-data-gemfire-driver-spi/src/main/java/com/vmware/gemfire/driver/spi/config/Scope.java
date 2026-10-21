/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi.config;

/** Region distribution scope. Native ordinals are deliberately not part of this enum's ABI. */
public enum Scope {
	LOCAL, DISTRIBUTED_NO_ACK, DISTRIBUTED_ACK, GLOBAL;
	public boolean isLocal() { return this == LOCAL; }
	public boolean isDistributed() { return this != LOCAL; }
	public boolean isDistributedNoAck() { return this == DISTRIBUTED_NO_ACK; }
	public boolean isDistributedAck() { return this == DISTRIBUTED_ACK; }
	public boolean isGlobal() { return this == GLOBAL; }
	public boolean isAck() { return this == DISTRIBUTED_ACK || this == GLOBAL; }
	public String toConfigTypeString() { return name().toLowerCase(java.util.Locale.ROOT).replace('_', '-'); }
}
