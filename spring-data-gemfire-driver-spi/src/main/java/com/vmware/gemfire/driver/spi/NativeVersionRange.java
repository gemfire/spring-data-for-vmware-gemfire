/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.Objects;

// Axis B value type (spec §5.1, §6.3): real GemFire product-version range, e.g. "10.1.0"-"10.3.x".
// A distinct value space from GenerationRange — never compared against it.
public record NativeVersionRange(String min, String max) {

	public NativeVersionRange {
		Objects.requireNonNull(min, "min must not be null");
		Objects.requireNonNull(max, "max must not be null");
		if (min.isBlank() || max.isBlank()) {
			throw new IllegalArgumentException("min/max must not be blank");
		}
	}

	public static NativeVersionRange of(String min, String max) {
		return new NativeVersionRange(min, max);
	}

}
