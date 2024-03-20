/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.schema.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.gemfire.util.CollectionUtils.asSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.cache.Region;

import org.springframework.data.gemfire.config.schema.SchemaObjectDefiner;
import org.springframework.data.gemfire.config.schema.SchemaObjectType;
import org.springframework.data.gemfire.config.schema.definitions.RegionDefinition;

/**
 * Unit tests for {@link ComposableSchemaObjectDefiner}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see SchemaObjectDefiner
 * @see ComposableSchemaObjectDefiner
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class ComposableSchemaObjectDefinerUnitTests {

	@Mock
	private SchemaObjectDefiner mockHandlerOne;

	@Mock
	private SchemaObjectDefiner mockHandlerTwo;

	private <T> Iterable<T> emptyIterable() {
		return Collections::<T>emptyIterator;
	}

	@Test
	public void composeArrayWithNoElements() {
		assertThat(ComposableSchemaObjectDefiner.compose()).isNull();
	}

	@Test
	public void composeArrayWithOneElement() {
		assertThat(ComposableSchemaObjectDefiner.compose(this.mockHandlerOne)).isSameAs(this.mockHandlerOne);
	}

	@Test
	public void composeArrayWithTwoElements() {

		SchemaObjectDefiner handler =
			ComposableSchemaObjectDefiner.compose(this.mockHandlerOne, this.mockHandlerTwo);

		assertThat(handler).isInstanceOf(ComposableSchemaObjectDefiner.class);
		assertThat((ComposableSchemaObjectDefiner) handler)
			.containsAll(asSet(this.mockHandlerOne, this.mockHandlerTwo));
	}

	@Test
	public void composeIterableWithNoElements() {
		assertThat(ComposableSchemaObjectDefiner.compose(emptyIterable())).isNull();
	}

	@Test
	public void composeIterableWithOneElement() {
		assertThat(ComposableSchemaObjectDefiner.compose(Collections.singleton(this.mockHandlerTwo)))
			.isSameAs(this.mockHandlerTwo);
	}

	@Test
	public void composeIterableWithTwoElements() {

		SchemaObjectDefiner handler =
			ComposableSchemaObjectDefiner.compose(asSet(this.mockHandlerOne, this.mockHandlerTwo));

		assertThat(handler).isInstanceOf(ComposableSchemaObjectDefiner.class);
		assertThat((ComposableSchemaObjectDefiner) handler)
			.containsAll(asSet(this.mockHandlerOne, this.mockHandlerTwo));
	}

	@Test
	public void defineReturnsRegionDefinition() {

		Region<?, ?> mockRegion = mock(Region.class);

		when(mockRegion.getName()).thenReturn("MockRegion");

		Optional<RegionDefinition> regionDefinition = Optional.of(RegionDefinition.from(mockRegion));

		when(this.mockHandlerTwo.canDefine(any(Region.class))).thenReturn(true);
		when(this.mockHandlerTwo.define(any(Region.class))).thenAnswer(invocation -> regionDefinition);

		SchemaObjectDefiner handler =
			ComposableSchemaObjectDefiner.compose(this.mockHandlerOne, this.mockHandlerTwo);

		assertThat(handler).isNotNull();
		assertThat(handler.define(mockRegion)).isEqualTo(regionDefinition);

		verify(this.mockHandlerOne, atMost(1)).canDefine(eq(mockRegion));
		verify(this.mockHandlerOne, never()).define(any());
		verify(this.mockHandlerTwo, times(1)).canDefine(eq(mockRegion));
		verify(this.mockHandlerTwo, times(1)).define(eq(mockRegion));
	}

	@Test
	public void defineReturnsEmptyOptional() {

		Object object = new Object();

		when(this.mockHandlerOne.canDefine(any(Object.class))).thenReturn(false);
		when(this.mockHandlerTwo.canDefine(any(Object.class))).thenReturn(false);

		SchemaObjectDefiner handler =
			ComposableSchemaObjectDefiner.compose(this.mockHandlerOne, this.mockHandlerTwo);

		assertThat(handler).isNotNull();
		assertThat(handler.define(object).isPresent()).isFalse();

		verify(this.mockHandlerOne, times(1)).canDefine(eq(object));
		verify(this.mockHandlerOne, never()).define(any());
		verify(this.mockHandlerTwo, times(1)).canDefine(eq(object));
		verify(this.mockHandlerTwo, never()).define(any());
	}
}
