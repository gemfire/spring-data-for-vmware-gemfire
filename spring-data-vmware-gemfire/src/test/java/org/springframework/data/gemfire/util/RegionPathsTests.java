/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RegionPathsTests {

	@Test
	void formatsNull() {
		assertFormatting(null, null, "/null");
	}

	@Test
	void formatsEmptyString() {
		assertFormatting("", "", "/");
	}

	@Test
	void preservesBlankString() {
		assertFormatting(" ", " ", "/ ");
	}

	@Test
	void formatsSimpleName() {
		assertFormatting("a", "a", "/a");
	}

	@Test
	void prefixesAlreadyPrefixedPath() {
		assertFormatting("/a", "a", "//a");
	}

	@Test
	void extractsNestedName() {
		assertFormatting("/a/b", "b", "//a/b");
	}

	@Test
	void preservesTrailingSeparator() {
		assertFormatting("a/", "", "/a/");
	}

	@Test
	void preservesLegacyWhitespaceOffset() {
		assertFormatting(" /a/b ", "/b ", "/ /a/b ");
	}

	private void assertFormatting(String input, String expectedName, String expectedPath) {
		assertThat(RegionPaths.toRegionName(input)).as("name for [%s]", input).isEqualTo(expectedName);
		assertThat(RegionPaths.toRegionPath(input)).as("path for [%s]", input).isEqualTo(expectedPath);
	}
}
