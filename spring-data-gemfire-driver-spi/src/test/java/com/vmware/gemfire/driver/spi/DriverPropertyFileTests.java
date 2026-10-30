/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URI;

import org.junit.jupiter.api.Test;

class DriverPropertyFileTests {

	@Test
	void explicitAccessRetainsLocationWhileDiagnosticsRedactBothFields() throws Exception {
		var location = URI.create("file:/private-token/gfsecurity.properties").toURL();
		var result = new DriverPropertyFile("private-token", location);
		assertThat(result.fileName()).isEqualTo("private-token");
		assertThat(result.location().orElseThrow()).isSameAs(location);
		assertThat(result.toString()).isEqualTo("DriverPropertyFile[details=<redacted>]");
		assertThat(new DriverPropertyFile("", null).location()).isEmpty();
		assertThatThrownBy(() -> new DriverPropertyFile(null, location)).isInstanceOf(NullPointerException.class);
	}
}
