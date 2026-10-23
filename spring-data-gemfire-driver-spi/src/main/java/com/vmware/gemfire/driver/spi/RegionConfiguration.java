/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.Objects;
import java.util.function.Consumer;

/** Immutable overrides applied after a provider shortcut. Unset values retain provider defaults.
 * Explicit null is supported for nullable constraints and named pool/disk references.
 * This configures a region; it does not create the referenced pool or disk store.
 */
public final class RegionConfiguration<K, V> {
	public record Option<T>(boolean isSet, T value) {
		public Option { if (!isSet && value != null) throw new IllegalArgumentException("Unset option has a value"); }
		public void ifSet(Consumer<? super T> consumer) { if (isSet) consumer.accept(value); }
	}
	private final Option<String> serverRegionName;
	private final Option<Class<K>> keyConstraint;
	private final Option<Class<V>> valueConstraint;
	private final Option<Integer> initialCapacity;
	private final Option<Float> loadFactor;
	private final Option<Integer> concurrencyLevel;
	private final Option<Boolean> concurrencyChecksEnabled;
	private final Option<Boolean> statisticsEnabled;
	private final Option<String> poolName;
	private final Option<Boolean> cloningEnabled;
	private final Option<String> diskStoreName;
	private final Option<Boolean> diskSynchronous;
	private final Option<RegionCallbacks<K, V>> callbacks;

	private RegionConfiguration(Builder<K, V> builder) {
		this.serverRegionName = builder.serverRegionName;
		this.keyConstraint = builder.keyConstraint;
		this.valueConstraint = builder.valueConstraint;
		this.initialCapacity = builder.initialCapacity;
		this.loadFactor = builder.loadFactor;
		this.concurrencyLevel = builder.concurrencyLevel;
		this.concurrencyChecksEnabled = builder.concurrencyChecksEnabled;
		this.statisticsEnabled = builder.statisticsEnabled;
		this.poolName = builder.poolName;
		this.cloningEnabled = builder.cloningEnabled;
		this.diskStoreName = builder.diskStoreName;
		this.diskSynchronous = builder.diskSynchronous;
		this.callbacks = builder.callbacks;
	}

	public static <K, V> Builder<K, V> builder() { return new Builder<>(); }
	public Option<String> serverRegionName() { return serverRegionName; }
	public Option<Class<K>> keyConstraint() { return keyConstraint; }
	public Option<Class<V>> valueConstraint() { return valueConstraint; }
	public Option<Integer> initialCapacity() { return initialCapacity; }
	public Option<Float> loadFactor() { return loadFactor; }
	public Option<Integer> concurrencyLevel() { return concurrencyLevel; }
	public Option<Boolean> concurrencyChecksEnabled() { return concurrencyChecksEnabled; }
	public Option<Boolean> statisticsEnabled() { return statisticsEnabled; }
	public Option<String> poolName() { return poolName; }
	public Option<Boolean> cloningEnabled() { return cloningEnabled; }
	public Option<String> diskStoreName() { return diskStoreName; }
	public Option<Boolean> diskSynchronous() { return diskSynchronous; }
	public Option<RegionCallbacks<K, V>> callbacks() { return callbacks; }

	public static final class Builder<K, V> {
		private Option<String> serverRegionName = new Option<>(false, null);
		private Option<Class<K>> keyConstraint = new Option<>(false, null);
		private Option<Class<V>> valueConstraint = new Option<>(false, null);
		private Option<Integer> initialCapacity = new Option<>(false, null);
		private Option<Float> loadFactor = new Option<>(false, null);
		private Option<Integer> concurrencyLevel = new Option<>(false, null);
		private Option<Boolean> concurrencyChecksEnabled = new Option<>(false, null);
		private Option<Boolean> statisticsEnabled = new Option<>(false, null);
		private Option<String> poolName = new Option<>(false, null);
		private Option<Boolean> cloningEnabled = new Option<>(false, null);
		private Option<String> diskStoreName = new Option<>(false, null);
		private Option<Boolean> diskSynchronous = new Option<>(false, null);
		private Option<RegionCallbacks<K, V>> callbacks = new Option<>(false, null);
		private Builder() { }
		public Builder<K, V> serverRegionName(String value) { this.serverRegionName = new Option<>(true, value); return this; }
		public Builder<K, V> keyConstraint(Class<K> value) { this.keyConstraint = new Option<>(true, value); return this; }
		public Builder<K, V> valueConstraint(Class<V> value) { this.valueConstraint = new Option<>(true, value); return this; }
		public Builder<K, V> initialCapacity(Integer value) { this.initialCapacity = new Option<>(true, Objects.requireNonNull(value, "initialCapacity")); return this; }
		public Builder<K, V> loadFactor(Float value) { this.loadFactor = new Option<>(true, Objects.requireNonNull(value, "loadFactor")); return this; }
		public Builder<K, V> concurrencyLevel(Integer value) { this.concurrencyLevel = new Option<>(true, Objects.requireNonNull(value, "concurrencyLevel")); return this; }
		public Builder<K, V> concurrencyChecksEnabled(Boolean value) { this.concurrencyChecksEnabled = new Option<>(true, Objects.requireNonNull(value, "concurrencyChecksEnabled")); return this; }
		public Builder<K, V> statisticsEnabled(Boolean value) { this.statisticsEnabled = new Option<>(true, Objects.requireNonNull(value, "statisticsEnabled")); return this; }
		public Builder<K, V> poolName(String value) { this.poolName = new Option<>(true, value); return this; }
		public Builder<K, V> cloningEnabled(Boolean value) { this.cloningEnabled = new Option<>(true, Objects.requireNonNull(value, "cloningEnabled")); return this; }
		public Builder<K, V> diskStoreName(String value) { this.diskStoreName = new Option<>(true, value); return this; }
		public Builder<K, V> diskSynchronous(Boolean value) { this.diskSynchronous = new Option<>(true, Objects.requireNonNull(value, "diskSynchronous")); return this; }
		public Builder<K, V> callbacks(RegionCallbacks<K, V> value) { this.callbacks = new Option<>(true, Objects.requireNonNull(value, "callbacks")); return this; }
		public RegionConfiguration<K, V> build() { return new RegionConfiguration<>(this); }
	}
}
