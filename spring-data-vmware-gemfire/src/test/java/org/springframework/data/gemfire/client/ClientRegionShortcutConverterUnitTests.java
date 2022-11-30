// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.apache.geode.cache.client.ClientRegionShortcut;

/**
 * Unit Tests for {@link ClientRegionShortcutConverter}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientRegionShortcut
 * @see org.springframework.data.gemfire.client.ClientRegionShortcutConverter
 * @since 1.3.4
 */
public class ClientRegionShortcutConverterUnitTests {

	private final ClientRegionShortcutConverter converter = new ClientRegionShortcutConverter();

	@Test
	public void testToUpperCase() {

		assertThat(ClientRegionShortcutConverter.toUpperCase("test")).isEqualTo("TEST");
		assertThat(ClientRegionShortcutConverter.toUpperCase(" Test  ")).isEqualTo("TEST");
		assertThat(ClientRegionShortcutConverter.toUpperCase("")).isEqualTo("");
		assertThat(ClientRegionShortcutConverter.toUpperCase("  ")).isEqualTo("");
		assertThat(ClientRegionShortcutConverter.toUpperCase("null")).isEqualTo("NULL");
		assertThat(ClientRegionShortcutConverter.toUpperCase(null)).isEqualTo("null");
	}

	@Test
	public void testConvert() {

		for (ClientRegionShortcut shortcut : ClientRegionShortcut.values()) {
			assertThat(converter.convert(shortcut.name())).isEqualTo(shortcut);
		}

		assertThat(converter.convert("Proxy")).isEqualTo(ClientRegionShortcut.PROXY);
		assertThat(converter.convert("caching_proxy")).isEqualTo(ClientRegionShortcut.CACHING_PROXY);
		assertThat(converter.convert("local_Heap_LRU")).isEqualTo(ClientRegionShortcut.LOCAL_HEAP_LRU);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConvertWithIllegalEnumeratedValue() {
		converter.convert("LOCAL Persistent OverFlow");
	}
}
