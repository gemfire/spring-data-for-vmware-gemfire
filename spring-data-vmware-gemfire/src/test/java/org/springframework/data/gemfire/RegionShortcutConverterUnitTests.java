/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.springframework.data.gemfire.gud.api.GudRegionShortcut;

/**
 * Unit Tests for {@link RegionShortcutConverter}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.GudRegionShortcut
 * @see org.springframework.data.gemfire.RegionShortcutConverter
 * @since 1.3.4
 */
public class RegionShortcutConverterUnitTests {

	private final RegionShortcutConverter converter = new RegionShortcutConverter();

	@Test
	public void testToUpperCase() {

		assertThat(RegionShortcutConverter.toUpperCase("test")).isEqualTo("TEST");
		assertThat(RegionShortcutConverter.toUpperCase(" Test  ")).isEqualTo("TEST");
		assertThat(RegionShortcutConverter.toUpperCase("")).isEqualTo("");
		assertThat(RegionShortcutConverter.toUpperCase("  ")).isEqualTo("");
		assertThat(RegionShortcutConverter.toUpperCase("null")).isEqualTo("NULL");
		assertThat(RegionShortcutConverter.toUpperCase(null)).isEqualTo("null");
	}

	@Test
	public void testConvert() {

		for (GudRegionShortcut shortcut : GudRegionShortcut.values()) {
			assertThat(converter.convert(shortcut.name())).isEqualTo(shortcut);
		}

		assertThat(converter.convert("Replicate_Proxy")).isEqualTo(GudRegionShortcut.REPLICATE_PROXY);
		assertThat(converter.convert("replicate_overflow")).isEqualTo(GudRegionShortcut.REPLICATE_OVERFLOW);
		assertThat(converter.convert("local_Heap_LRU")).isEqualTo(GudRegionShortcut.LOCAL_HEAP_LRU);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConvertWithIllegalEnumeratedValue() {
		converter.convert("localPersistentOverflow");
	}
}
