/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Properties;

import org.junit.jupiter.api.Test;

class DriverPropertySnapshotTests {

	@Test
	void flattensStringDefaultsAndDetachesEveryCopyWithoutExposingObjects() {
		Properties defaults = new Properties();
		defaults.setProperty("inherited", "original");
		Properties source = new Properties(defaults);
		source.setProperty("security-password", "private-token");
		source.put("native-object", new Object());
		source.put(42, "non-string-key");
		var snapshot = new DriverPropertySnapshot(source);
		source.setProperty("security-password", "changed");
		defaults.setProperty("inherited", "changed");
		assertThat(snapshot.properties()).hasSize(2)
			.containsEntry("inherited", "original").containsEntry("security-password", "private-token");
		snapshot.properties().clear();
		assertThat(snapshot.properties()).hasSize(2);
		assertThat(snapshot.toString()).isEqualTo("DriverPropertySnapshot[properties=<redacted>]");
	}

	@Test
	void rejectsNullProperties() {
		assertThatThrownBy(() -> new DriverPropertySnapshot(null)).isInstanceOf(NullPointerException.class);
	}
}
