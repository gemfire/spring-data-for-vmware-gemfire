/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.Objects;

/**
 * Immutable durable identity snapshot. Pool qualification is explicit and never consults
 * thread-local state. Copy methods do not change a connection, pool or remote durable queue.
 * Equality/hash code use base ID, timeout and pool name, including null IDs; unlike native
 * mutable attributes they are stable. Explicit accessors may reveal application identifiers.
 */
public final class DriverDurableClientAttributes {

	private final String baseId;
	private final int timeout;
	private final String poolName;

	/** Unconfigured identity, matching the native no-argument field values. */
	public DriverDurableClientAttributes() { this(null, 0, null); }

	/**
	 * Captures an ID, splitting at the first native {@code _gem_} pool separator.
	 * Null/empty IDs and arbitrary timeouts remain representable; connection configuration
	 * validation is separate. This preserves the original two-argument SPI constructor.
	 */
	public DriverDurableClientAttributes(String id, int timeout) {
		int separator = id == null ? -1 : id.indexOf("_gem_");
		this.baseId = separator < 0 ? id : id.substring(0, separator);
		this.poolName = separator < 0 ? null : id.substring(separator + 5);
		this.timeout = timeout;
	}

	private DriverDurableClientAttributes(String baseId, int timeout, String poolName) {
		this.baseId = baseId;
		this.timeout = timeout;
		this.poolName = poolName;
	}

	/** Qualified ID; null/empty base IDs remain null/empty even when a pool is recorded. */
	public String id() {
		return baseId == null || baseId.isEmpty() || poolName == null ? baseId : baseId + "_gem_" + poolName;
	}
	public String baseId() { return baseId; }
	public int timeout() { return timeout; }
	/** Null means unqualified; an empty pool name is distinct from null. */
	public String poolName() { return poolName; }
	public DriverDurableClientAttributes withPoolName(String poolName) {
		return new DriverDurableClientAttributes(baseId, timeout, poolName);
	}
	public DriverDurableClientAttributes withTimeout(int timeout) {
		return new DriverDurableClientAttributes(baseId, timeout, poolName);
	}
	@Override public boolean equals(Object other) {
		return other instanceof DriverDurableClientAttributes that && timeout == that.timeout
			&& Objects.equals(baseId, that.baseId) && Objects.equals(poolName, that.poolName);
	}
	@Override public int hashCode() { return Objects.hash(baseId, timeout, poolName); }
	@Override public String toString() { return "DriverDurableClientAttributes[details=<redacted>]"; }
}
