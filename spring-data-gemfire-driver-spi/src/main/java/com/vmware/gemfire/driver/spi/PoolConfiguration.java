/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.*;

/**
 * Immutable pool settings. Unset values retain provider defaults; durations are milliseconds.
 * A builder may have no endpoints for use as an overlay or default-cache configuration.
 * Named pool creation requires endpoints. Providers validate settings when creating the pool.
 * Network settings on the mock describe configuration only, never connectivity or queue behavior.
 */
public final class PoolConfiguration {
	/** Configured endpoint, not discovered topology. Construction does not resolve DNS. */
	public record Endpoint(String host, int port) {
		public Endpoint {
			if (host == null || host.isBlank()) throw new IllegalArgumentException("Endpoint host is required");
			if (port < 1 || port > 65535) throw new IllegalArgumentException("Endpoint port must be in 1..65535");
		}
	}
	private final List<Endpoint> servers;
	private final List<Endpoint> locators;
	private final Integer minimumConnections;
	private final Integer maximumConnections;
	private final Integer minimumConnectionsPerServer;
	private final Integer maximumConnectionsPerServer;
	private final Integer socketConnectTimeout;
	private final Integer freeConnectionTimeout;
	private final Integer serverConnectionTimeout;
	private final Integer loadConditioningInterval;
	private final Integer socketBufferSize;
	private final Integer readTimeout;
	private final Integer pingTimeout;
	private final Long idleTimeout;
	private final Long pingInterval;
	private final Integer statisticInterval;
	private final Integer retryAttempts;
	private final Boolean subscriptionEnabled;
	private final Boolean prSingleHopEnabled;
	private final Integer subscriptionRedundancy;
	private final Integer subscriptionMessageTrackingTimeout;
	private final Integer subscriptionAckInterval;
	private final Integer subscriptionTimeoutMultiplier;
	private final String serverGroup;
	private final String availabilityZone;
	private final Boolean multiuserAuthentication;
	private final DriverSocketFactory socketFactory;
	private final Boolean threadLocalConnections;

	/** Legacy named-pool constructor: exactly one endpoint list must be nonempty. */
	public PoolConfiguration(List<Endpoint> servers, List<Endpoint> locators, Integer minimumConnections) {
		this(legacyBuilder(servers, locators, minimumConnections));
	}
	private static Builder legacyBuilder(List<Endpoint> servers, List<Endpoint> locators, Integer minimum) {
		Objects.requireNonNull(servers, "servers"); Objects.requireNonNull(locators, "locators");
		if (servers.isEmpty() == locators.isEmpty()) throw new IllegalArgumentException("Exactly one endpoint kind is required");
		if (minimum != null && minimum < 0) throw new IllegalArgumentException("Minimum connections must be nonnegative");
		Builder builder = builder().servers(servers).locators(locators);
		if (minimum != null) builder.minimumConnections(minimum);
		return builder;
	}
	private PoolConfiguration(Builder b) {
		servers = List.copyOf(b.servers); locators = List.copyOf(b.locators);
		if (!servers.isEmpty() && !locators.isEmpty()) throw new IllegalArgumentException("Cannot mix servers and locators");
		minimumConnections = b.minimumConnections;
		maximumConnections = b.maximumConnections;
		minimumConnectionsPerServer = b.minimumConnectionsPerServer;
		maximumConnectionsPerServer = b.maximumConnectionsPerServer;
		socketConnectTimeout = b.socketConnectTimeout;
		freeConnectionTimeout = b.freeConnectionTimeout;
		serverConnectionTimeout = b.serverConnectionTimeout;
		loadConditioningInterval = b.loadConditioningInterval;
		socketBufferSize = b.socketBufferSize;
		readTimeout = b.readTimeout;
		pingTimeout = b.pingTimeout;
		idleTimeout = b.idleTimeout;
		pingInterval = b.pingInterval;
		statisticInterval = b.statisticInterval;
		retryAttempts = b.retryAttempts;
		subscriptionEnabled = b.subscriptionEnabled;
		prSingleHopEnabled = b.prSingleHopEnabled;
		subscriptionRedundancy = b.subscriptionRedundancy;
		subscriptionMessageTrackingTimeout = b.subscriptionMessageTrackingTimeout;
		subscriptionAckInterval = b.subscriptionAckInterval;
		subscriptionTimeoutMultiplier = b.subscriptionTimeoutMultiplier;
		serverGroup = b.serverGroup;
		availabilityZone = b.availabilityZone;
		multiuserAuthentication = b.multiuserAuthentication;
		socketFactory = b.socketFactory;
		threadLocalConnections = b.threadLocalConnections;
	}
	public static Builder builder() { return new Builder(); }
	public List<Endpoint> servers() { return servers; }
	public List<Endpoint> locators() { return locators; }
	public OptionalInt minimumConnections() { return minimumConnections == null ? OptionalInt.empty() : OptionalInt.of(minimumConnections); }
	public OptionalInt maximumConnections() { return maximumConnections == null ? OptionalInt.empty() : OptionalInt.of(maximumConnections); }
	public OptionalInt minimumConnectionsPerServer() { return minimumConnectionsPerServer == null ? OptionalInt.empty() : OptionalInt.of(minimumConnectionsPerServer); }
	public OptionalInt maximumConnectionsPerServer() { return maximumConnectionsPerServer == null ? OptionalInt.empty() : OptionalInt.of(maximumConnectionsPerServer); }
	public OptionalInt socketConnectTimeout() { return socketConnectTimeout == null ? OptionalInt.empty() : OptionalInt.of(socketConnectTimeout); }
	public OptionalInt freeConnectionTimeout() { return freeConnectionTimeout == null ? OptionalInt.empty() : OptionalInt.of(freeConnectionTimeout); }
	public OptionalInt serverConnectionTimeout() { return serverConnectionTimeout == null ? OptionalInt.empty() : OptionalInt.of(serverConnectionTimeout); }
	public OptionalInt loadConditioningInterval() { return loadConditioningInterval == null ? OptionalInt.empty() : OptionalInt.of(loadConditioningInterval); }
	public OptionalInt socketBufferSize() { return socketBufferSize == null ? OptionalInt.empty() : OptionalInt.of(socketBufferSize); }
	public OptionalInt readTimeout() { return readTimeout == null ? OptionalInt.empty() : OptionalInt.of(readTimeout); }
	public OptionalInt pingTimeout() { return pingTimeout == null ? OptionalInt.empty() : OptionalInt.of(pingTimeout); }
	public OptionalLong idleTimeout() { return idleTimeout == null ? OptionalLong.empty() : OptionalLong.of(idleTimeout); }
	public OptionalLong pingInterval() { return pingInterval == null ? OptionalLong.empty() : OptionalLong.of(pingInterval); }
	public OptionalInt statisticInterval() { return statisticInterval == null ? OptionalInt.empty() : OptionalInt.of(statisticInterval); }
	public OptionalInt retryAttempts() { return retryAttempts == null ? OptionalInt.empty() : OptionalInt.of(retryAttempts); }
	public Optional<Boolean> subscriptionEnabled() { return Optional.ofNullable(subscriptionEnabled); }
	public Optional<Boolean> prSingleHopEnabled() { return Optional.ofNullable(prSingleHopEnabled); }
	public OptionalInt subscriptionRedundancy() { return subscriptionRedundancy == null ? OptionalInt.empty() : OptionalInt.of(subscriptionRedundancy); }
	public OptionalInt subscriptionMessageTrackingTimeout() { return subscriptionMessageTrackingTimeout == null ? OptionalInt.empty() : OptionalInt.of(subscriptionMessageTrackingTimeout); }
	public OptionalInt subscriptionAckInterval() { return subscriptionAckInterval == null ? OptionalInt.empty() : OptionalInt.of(subscriptionAckInterval); }
	public OptionalInt subscriptionTimeoutMultiplier() { return subscriptionTimeoutMultiplier == null ? OptionalInt.empty() : OptionalInt.of(subscriptionTimeoutMultiplier); }
	public Optional<String> serverGroup() { return Optional.ofNullable(serverGroup); }
	public Optional<String> availabilityZone() { return Optional.ofNullable(availabilityZone); }
	public Optional<Boolean> multiuserAuthentication() { return Optional.ofNullable(multiuserAuthentication); }
	public Optional<DriverSocketFactory> socketFactory() { return Optional.ofNullable(socketFactory); }
	public Optional<Boolean> threadLocalConnections() { return Optional.ofNullable(threadLocalConnections); }

	/**
	 * Explicit scalar settings win over fallback settings, including false, zero and empty strings.
	 * A nonempty endpoint list replaces both fallback endpoint lists; empty lists inherit them.
	 * This does not resolve any provider defaults or create a pool.
	 */
	public PoolConfiguration withFallback(PoolConfiguration fallback) {
		Objects.requireNonNull(fallback, "fallback");
		Builder b = new Builder();
		boolean ownEndpoints = !servers.isEmpty() || !locators.isEmpty();
		b.servers = ownEndpoints ? servers : fallback.servers;
		b.locators = ownEndpoints ? locators : fallback.locators;
		b.minimumConnections = minimumConnections != null ? minimumConnections : fallback.minimumConnections;
		b.maximumConnections = maximumConnections != null ? maximumConnections : fallback.maximumConnections;
		b.minimumConnectionsPerServer = minimumConnectionsPerServer != null ? minimumConnectionsPerServer : fallback.minimumConnectionsPerServer;
		b.maximumConnectionsPerServer = maximumConnectionsPerServer != null ? maximumConnectionsPerServer : fallback.maximumConnectionsPerServer;
		b.socketConnectTimeout = socketConnectTimeout != null ? socketConnectTimeout : fallback.socketConnectTimeout;
		b.freeConnectionTimeout = freeConnectionTimeout != null ? freeConnectionTimeout : fallback.freeConnectionTimeout;
		b.serverConnectionTimeout = serverConnectionTimeout != null ? serverConnectionTimeout : fallback.serverConnectionTimeout;
		b.loadConditioningInterval = loadConditioningInterval != null ? loadConditioningInterval : fallback.loadConditioningInterval;
		b.socketBufferSize = socketBufferSize != null ? socketBufferSize : fallback.socketBufferSize;
		b.readTimeout = readTimeout != null ? readTimeout : fallback.readTimeout;
		b.pingTimeout = pingTimeout != null ? pingTimeout : fallback.pingTimeout;
		b.idleTimeout = idleTimeout != null ? idleTimeout : fallback.idleTimeout;
		b.pingInterval = pingInterval != null ? pingInterval : fallback.pingInterval;
		b.statisticInterval = statisticInterval != null ? statisticInterval : fallback.statisticInterval;
		b.retryAttempts = retryAttempts != null ? retryAttempts : fallback.retryAttempts;
		b.subscriptionEnabled = subscriptionEnabled != null ? subscriptionEnabled : fallback.subscriptionEnabled;
		b.prSingleHopEnabled = prSingleHopEnabled != null ? prSingleHopEnabled : fallback.prSingleHopEnabled;
		b.subscriptionRedundancy = subscriptionRedundancy != null ? subscriptionRedundancy : fallback.subscriptionRedundancy;
		b.subscriptionMessageTrackingTimeout = subscriptionMessageTrackingTimeout != null ? subscriptionMessageTrackingTimeout : fallback.subscriptionMessageTrackingTimeout;
		b.subscriptionAckInterval = subscriptionAckInterval != null ? subscriptionAckInterval : fallback.subscriptionAckInterval;
		b.subscriptionTimeoutMultiplier = subscriptionTimeoutMultiplier != null ? subscriptionTimeoutMultiplier : fallback.subscriptionTimeoutMultiplier;
		b.serverGroup = serverGroup != null ? serverGroup : fallback.serverGroup;
		b.availabilityZone = availabilityZone != null ? availabilityZone : fallback.availabilityZone;
		b.multiuserAuthentication = multiuserAuthentication != null ? multiuserAuthentication : fallback.multiuserAuthentication;
		b.socketFactory = socketFactory != null ? socketFactory : fallback.socketFactory;
		b.threadLocalConnections = threadLocalConnections != null ? threadLocalConnections : fallback.threadLocalConnections;
		return b.build();
	}

	public static final class Builder {
		private List<Endpoint> servers = List.of();
		private List<Endpoint> locators = List.of();
		private Integer minimumConnections;
		private Integer maximumConnections;
		private Integer minimumConnectionsPerServer;
		private Integer maximumConnectionsPerServer;
		private Integer socketConnectTimeout;
		private Integer freeConnectionTimeout;
		private Integer serverConnectionTimeout;
		private Integer loadConditioningInterval;
		private Integer socketBufferSize;
		private Integer readTimeout;
		private Integer pingTimeout;
		private Long idleTimeout;
		private Long pingInterval;
		private Integer statisticInterval;
		private Integer retryAttempts;
		private Boolean subscriptionEnabled;
		private Boolean prSingleHopEnabled;
		private Integer subscriptionRedundancy;
		private Integer subscriptionMessageTrackingTimeout;
		private Integer subscriptionAckInterval;
		private Integer subscriptionTimeoutMultiplier;
		private String serverGroup;
		private String availabilityZone;
		private Boolean multiuserAuthentication;
		private DriverSocketFactory socketFactory;
		private Boolean threadLocalConnections;
		private Builder() {}
		public Builder servers(List<Endpoint> values) { servers = List.copyOf(values); return this; }
		public Builder locators(List<Endpoint> values) { locators = List.copyOf(values); return this; }
		public Builder minimumConnections(int value) { minimumConnections = value; return this; }
		public Builder maximumConnections(int value) { maximumConnections = value; return this; }
		public Builder minimumConnectionsPerServer(int value) { minimumConnectionsPerServer = value; return this; }
		public Builder maximumConnectionsPerServer(int value) { maximumConnectionsPerServer = value; return this; }
		public Builder socketConnectTimeout(int value) { socketConnectTimeout = value; return this; }
		public Builder freeConnectionTimeout(int value) { freeConnectionTimeout = value; return this; }
		public Builder serverConnectionTimeout(int value) { serverConnectionTimeout = value; return this; }
		public Builder loadConditioningInterval(int value) { loadConditioningInterval = value; return this; }
		public Builder socketBufferSize(int value) { socketBufferSize = value; return this; }
		public Builder readTimeout(int value) { readTimeout = value; return this; }
		public Builder pingTimeout(int value) { pingTimeout = value; return this; }
		public Builder idleTimeout(long value) { idleTimeout = value; return this; }
		public Builder pingInterval(long value) { pingInterval = value; return this; }
		public Builder statisticInterval(int value) { statisticInterval = value; return this; }
		public Builder retryAttempts(int value) { retryAttempts = value; return this; }
		public Builder subscriptionEnabled(boolean value) { subscriptionEnabled = value; return this; }
		public Builder prSingleHopEnabled(boolean value) { prSingleHopEnabled = value; return this; }
		public Builder subscriptionRedundancy(int value) { subscriptionRedundancy = value; return this; }
		public Builder subscriptionMessageTrackingTimeout(int value) { subscriptionMessageTrackingTimeout = value; return this; }
		public Builder subscriptionAckInterval(int value) { subscriptionAckInterval = value; return this; }
		public Builder subscriptionTimeoutMultiplier(int value) { subscriptionTimeoutMultiplier = value; return this; }
		public Builder serverGroup(String value) { serverGroup = value == null ? "" : value; return this; }
		public Builder availabilityZone(String value) { availabilityZone = value == null ? "" : value; return this; }
		public Builder multiuserAuthentication(boolean value) { multiuserAuthentication = value; return this; }
		public Builder socketFactory(DriverSocketFactory value) { socketFactory = Objects.requireNonNull(value, "socketFactory"); return this; }
		public Builder threadLocalConnections(boolean value) { threadLocalConnections = value; return this; }
		public PoolConfiguration build() { return new PoolConfiguration(this); }
	}
}
