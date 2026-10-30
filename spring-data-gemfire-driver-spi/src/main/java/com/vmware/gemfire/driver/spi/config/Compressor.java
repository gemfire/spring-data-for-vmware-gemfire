// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi.config;

/** Region value compressor. Implementations report failure with {@link CompressionException}. */
public interface Compressor {
	byte[] compress(byte[] input);
	byte[] decompress(byte[] input);
}
