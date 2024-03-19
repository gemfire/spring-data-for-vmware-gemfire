/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.schema.definitions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionShortcut;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.gemfire.config.schema.SchemaObjectType;
import org.springframework.data.gemfire.tests.util.IOUtils;

/**
 * Unit Tests for {@link RegionDefinition}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.springframework.data.gemfire.config.schema.definitions.RegionDefinition
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class RegionDefinitionUnitTests {

	@Mock
	private Region<?, ?> mockRegion;

	@Before
	public void setup() {
		assertThat(this.mockRegion).isNotNull();
		when(this.mockRegion.getName()).thenReturn("MockRegion");
	}

	@Test
	public void fromRegionCreatesRegionDefinition() {

		RegionDefinition regionDefinition = RegionDefinition.from(this.mockRegion);

		assertThat(regionDefinition).isNotNull();
		assertThat(regionDefinition.getName()).isEqualTo(this.mockRegion.getName());
		assertThat(regionDefinition.getRegion()).isSameAs(this.mockRegion);
		assertThat(regionDefinition.getRegionShortcut()).isEqualTo(RegionDefinition.DEFAULT_REGION_SHORTCUT);
		assertThat(regionDefinition.getType()).isEqualTo(SchemaObjectType.REGION);
	}

	@Test(expected = IllegalArgumentException.class)
	public void fromNullRegionThrowsIllegalArgumentException() {

		try {
			RegionDefinition.from(null);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("Region is required");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void havingRegionShortcut() {

		RegionDefinition regionDefinition = RegionDefinition.from(this.mockRegion);

		assertThat(regionDefinition).isNotNull();
		assertThat(regionDefinition.getRegion()).isSameAs(this.mockRegion);
		assertThat(regionDefinition.getRegionShortcut()).isEqualTo(RegionDefinition.DEFAULT_REGION_SHORTCUT);
		assertThat(regionDefinition.having(RegionShortcut.LOCAL)).isSameAs(regionDefinition);
		assertThat(regionDefinition.getRegionShortcut()).isEqualTo(RegionShortcut.LOCAL);
		assertThat(regionDefinition.having(null)).isSameAs(regionDefinition);
		assertThat(regionDefinition.getRegionShortcut()).isEqualTo(RegionDefinition.DEFAULT_REGION_SHORTCUT);
		assertThat(regionDefinition.having(RegionShortcut.REPLICATE)).isSameAs(regionDefinition);
		assertThat(regionDefinition.getRegionShortcut()).isEqualTo(RegionShortcut.REPLICATE);
	}

	@Test
	public void withName() {

		RegionDefinition regionDefinition = RegionDefinition.from(this.mockRegion);

		assertThat(regionDefinition).isNotNull();
		assertThat(regionDefinition.getRegion()).isSameAs(this.mockRegion);
		assertThat(regionDefinition.getName()).isEqualTo(this.mockRegion.getName());
		assertThat(regionDefinition.with("/Test")).isSameAs(regionDefinition);
		assertThat(regionDefinition.getName()).isEqualTo("/Test");
		assertThat(regionDefinition.with("  ")).isSameAs(regionDefinition);
		assertThat(regionDefinition.getName()).isEqualTo(this.mockRegion.getName());
		assertThat(regionDefinition.with("/Mock")).isSameAs(regionDefinition);
		assertThat(regionDefinition.getName()).isEqualTo("/Mock");
		assertThat(regionDefinition.with(null)).isSameAs(regionDefinition);
		assertThat(regionDefinition.getName()).isEqualTo(this.mockRegion.getName());
	}

	@Test
	public void serializeDeserializeIsSuccessful() throws IOException, ClassNotFoundException {

		RegionDefinition regionDefinition = RegionDefinition.from(this.mockRegion).having(RegionShortcut.REPLICATE);

		byte[] regionDefinitionBytes = IOUtils.serializeObject(regionDefinition);

		RegionDefinition deserializedRegionDefinition = IOUtils.deserializeObject(regionDefinitionBytes);

		assertThat(deserializedRegionDefinition).isNotNull();
		assertThat(deserializedRegionDefinition.getRegion()).isNull();
		assertThat(deserializedRegionDefinition.getName()).isEqualTo("MockRegion");
		assertThat(deserializedRegionDefinition.getRegionShortcut()).isEqualTo(RegionShortcut.REPLICATE);
	}
}
