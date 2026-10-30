// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi.config;

/** Marker for the provider's Snappy compressor, which the provider maps to its own implementation. Equality and
 * hashing are by class, and each {@link #getDefaultInstance()} call returns a new instance. The native class is deprecated
 * but retained. Direct {@code compress} and {@code decompress} are unsupported; the provider performs them.
 */
@Deprecated
public final class SnappyCompressor implements Compressor {
	public SnappyCompressor() { }
	public static SnappyCompressor getDefaultInstance() { return new SnappyCompressor(); }
	@Override public byte[] compress(byte[] input) { throw new UnsupportedOperationException("Snappy compression runs inside the provider"); }
	@Override public byte[] decompress(byte[] input) { throw new UnsupportedOperationException("Snappy decompression runs inside the provider"); }
	@Override public boolean equals(Object other) { return other != null && getClass().getName().equals(other.getClass().getName()); }
	@Override public int hashCode() { return getClass().getName().hashCode(); }
}
