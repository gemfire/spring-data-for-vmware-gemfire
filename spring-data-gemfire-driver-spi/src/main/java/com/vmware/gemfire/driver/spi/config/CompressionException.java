// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi.config;

/** A compressor could not compress or decompress its input, for example corrupt compressed bytes. */
public class CompressionException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public CompressionException(String message, Throwable cause) { super(message, cause); }
}
