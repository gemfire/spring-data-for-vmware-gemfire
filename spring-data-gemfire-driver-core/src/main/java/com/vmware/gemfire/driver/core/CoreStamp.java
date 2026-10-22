/* Copyright 2026 Broadcom. SPDX-License-Identifier: Apache-2.0 */
package com.vmware.gemfire.driver.core;

import java.io.IOException;
import java.util.Properties;

import com.vmware.gemfire.driver.spi.DriverBindException;
import com.vmware.gemfire.driver.spi.SpiVersion;
import com.vmware.gemfire.driver.spi.SpiVersionIncompatibleException;

/** Core's compile requirement is independent of every provider's stamp (spec §6.1). */
record CoreStamp(SpiVersion required) {

	static CoreStamp read() {
		String resource = "/META-INF/gemfire-driver-core.properties";
		try (var input = CoreStamp.class.getResourceAsStream(resource)) {
			if (input == null) {
				throw new DriverBindException("driver-core has no SPI stamp at " + resource);
			}
			var properties = new Properties();
			properties.load(input);
			return new CoreStamp(SpiVersion.parse(properties.getProperty("spi.version")));
		}
		catch (IOException | IllegalArgumentException ex) {
			throw new DriverBindException("driver-core SPI stamp is unreadable or malformed", ex);
		}
	}

	void validate(SpiVersion runtime) {
		if (!runtime.accepts(required)) {
			throw new SpiVersionIncompatibleException("driver-core", required, runtime);
		}
	}
}
