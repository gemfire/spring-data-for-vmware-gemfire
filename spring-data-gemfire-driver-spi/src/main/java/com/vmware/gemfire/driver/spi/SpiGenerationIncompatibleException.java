/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

public class SpiGenerationIncompatibleException extends DriverBindException {

	// transient: implementor-provided DriverMetadata is not guaranteed Serializable.
	private final transient DriverMetadata metadata;
	private final GenerationRange spiRange;

	public SpiGenerationIncompatibleException(DriverMetadata metadata, GenerationRange spiRange) {
		super("Driver Impl's supported generation range " + metadata.supportedGenerationRange()
			+ " does not intersect the current SPI generation range " + spiRange + ".");
		this.metadata = metadata;
		this.spiRange = spiRange;
	}

	public DriverMetadata metadata() {
		return metadata;
	}

	public GenerationRange spiRange() {
		return spiRange;
	}

}
