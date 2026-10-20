/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

/** Immutable named-pool configuration. Unspecified settings retain provider defaults. */
public final class PoolConfiguration {

	/** Configured endpoint, not a discovered server or a connectivity assertion. No DNS resolution. */
	public record Endpoint(String host, int port) {
		public Endpoint {
			if (host == null || host.isBlank()) throw new IllegalArgumentException("Endpoint host is required");
			if (port < 1 || port > 65535) throw new IllegalArgumentException("Endpoint port must be in 1..65535");
		}
	}

	private final List<Endpoint> servers;
	private final List<Endpoint> locators;
	private final Integer minimumConnections;

	/** Exactly one endpoint list must be nonempty; null minimum leaves the provider default unset. */
	public PoolConfiguration(List<Endpoint> servers, List<Endpoint> locators, Integer minimumConnections) {
		this.servers = List.copyOf(Objects.requireNonNull(servers, "servers"));
		this.locators = List.copyOf(Objects.requireNonNull(locators, "locators"));
		if (this.servers.isEmpty() == this.locators.isEmpty()) {
			throw new IllegalArgumentException("Exactly one of servers or locators must be nonempty");
		}
		if (minimumConnections != null && minimumConnections < 0) {
			throw new IllegalArgumentException("Minimum connections must be nonnegative");
		}
		this.minimumConnections = minimumConnections;
	}

	public List<Endpoint> servers() { return servers; }
	public List<Endpoint> locators() { return locators; }
	public OptionalInt minimumConnections() {
		return minimumConnections == null ? OptionalInt.empty() : OptionalInt.of(minimumConnections);
	}
}
