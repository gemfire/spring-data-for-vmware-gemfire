/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

// Axis A value type (spec §5.1, §6.1/§6.2): SPI-capability-generation range.
public record GenerationRange(int min, int max) {

	public GenerationRange {
		if (min > max) {
			throw new IllegalArgumentException("min (" + min + ") must be <= max (" + max + ")");
		}
	}

	public static GenerationRange of(int min, int max) {
		return new GenerationRange(min, max);
	}

	public boolean intersects(GenerationRange other) {
		return this.max >= other.min && this.min <= other.max;
	}

	// Effective intersected range — diagnostics only (§6.2); not itself load-bearing for bind decisions.
	public GenerationRange intersectWith(GenerationRange other) {
		if (!intersects(other)) {
			throw new IllegalArgumentException("Ranges " + this + " and " + other + " do not intersect");
		}
		return new GenerationRange(Math.max(this.min, other.min), Math.min(this.max, other.max));
	}

}
