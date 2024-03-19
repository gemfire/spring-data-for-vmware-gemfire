/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.apache.geode.cache.RegionShortcut;

/**
 * Unit Tests for {@link RegionShortcutConverter}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see RegionShortcut
 * @see RegionShortcutConverter
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

		for (RegionShortcut shortcut : RegionShortcut.values()) {
			assertThat(converter.convert(shortcut.name())).isEqualTo(shortcut);
		}

		assertThat(converter.convert("Partition_Proxy")).isEqualTo(RegionShortcut.PARTITION_PROXY);
		assertThat(converter.convert("replicate_overflow")).isEqualTo(RegionShortcut.REPLICATE_OVERFLOW);
		assertThat(converter.convert("local_Heap_LRU")).isEqualTo(RegionShortcut.LOCAL_HEAP_LRU);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConvertWithIllegalEnumeratedValue() {
		converter.convert("localPersistentOverflow");
	}
}
