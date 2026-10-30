// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi.config;

import com.vmware.gemfire.driver.spi.DriverPdxSerializer;
import java.util.Objects;
import java.util.Optional;

/** Immutable client-cache PDX settings. All settings are optional; the defaults match an unconfigured native client cache.
 * <p>{@code persistent} and {@code diskStoreName} are accepted and read back, but the pinned client deprecates and ignores
 * them (client PDX metadata is not persisted), so they carry no persistence promise.
 */
public final class PdxConfiguration {
	private static final PdxConfiguration DEFAULTS = new PdxConfiguration(false, false, false, null, null);
	private final boolean readSerialized;
	private final boolean ignoreUnreadFields;
	private final boolean persistent;
	private final String diskStoreName;
	private final DriverPdxSerializer serializer;
	private PdxConfiguration(boolean readSerialized, boolean ignoreUnreadFields, boolean persistent, String diskStoreName, DriverPdxSerializer serializer) {
		this.readSerialized = readSerialized;
		this.ignoreUnreadFields = ignoreUnreadFields;
		this.persistent = persistent;
		this.diskStoreName = diskStoreName;
		this.serializer = serializer;
	}
	public static PdxConfiguration defaults() { return DEFAULTS; }
	public PdxConfiguration withReadSerialized(boolean value) { return new PdxConfiguration(value, ignoreUnreadFields, persistent, diskStoreName, serializer); }
	public PdxConfiguration withIgnoreUnreadFields(boolean value) { return new PdxConfiguration(readSerialized, value, persistent, diskStoreName, serializer); }
	public PdxConfiguration withPersistent(boolean value) { return new PdxConfiguration(readSerialized, ignoreUnreadFields, value, diskStoreName, serializer); }
	/** A null name clears the setting. */
	public PdxConfiguration withDiskStoreName(String value) { return new PdxConfiguration(readSerialized, ignoreUnreadFields, persistent, value, serializer); }
	/** A null serializer clears the setting. */
	public PdxConfiguration withSerializer(DriverPdxSerializer value) { return new PdxConfiguration(readSerialized, ignoreUnreadFields, persistent, diskStoreName, value); }
	public boolean readSerialized() { return readSerialized; }
	public boolean ignoreUnreadFields() { return ignoreUnreadFields; }
	public boolean persistent() { return persistent; }
	public Optional<String> diskStoreName() { return Optional.ofNullable(diskStoreName); }
	public Optional<DriverPdxSerializer> serializer() { return Optional.ofNullable(serializer); }
	@Override public boolean equals(Object other) {
		return this == other || other instanceof PdxConfiguration that && readSerialized == that.readSerialized
			&& ignoreUnreadFields == that.ignoreUnreadFields && persistent == that.persistent
			&& Objects.equals(diskStoreName, that.diskStoreName) && serializer == that.serializer;
	}
	@Override public int hashCode() { return Objects.hash(readSerialized, ignoreUnreadFields, persistent, diskStoreName, System.identityHashCode(serializer)); }
	@Override public String toString() {
		return "PdxConfiguration[readSerialized=" + readSerialized + ", ignoreUnreadFields=" + ignoreUnreadFields
			+ ", persistent=" + persistent + ", diskStoreName=" + diskStoreName + ", serializer=" + (serializer == null ? null : serializer.getClass().getName()) + "]";
	}
}
