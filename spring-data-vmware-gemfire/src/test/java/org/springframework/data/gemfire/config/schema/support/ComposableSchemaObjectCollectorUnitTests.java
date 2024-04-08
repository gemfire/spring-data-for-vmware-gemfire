/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.schema.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.gemfire.util.CollectionUtils.asSet;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.cache.GemFireCache;

import org.springframework.context.ApplicationContext;
import org.springframework.data.gemfire.config.schema.SchemaObjectCollector;
import org.springframework.data.gemfire.config.schema.SchemaObjectType;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Unit tests for {@link ComposableSchemaObjectCollector}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.springframework.data.gemfire.config.schema.support.ComposableSchemaObjectCollector
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class ComposableSchemaObjectCollectorUnitTests {

	@Mock
	private ApplicationContext mockApplicationContext;

	@Mock
	private GemFireCache mockCache;

	@Mock
	private SchemaObjectCollector<Object> mockSchemaObjectCollectorOne;

	@Mock
	private SchemaObjectCollector<Object> mockSchemaObjectCollectorTwo;

	private <T> Iterable<T> emptyIterable() {
		return Collections::emptyIterator;
	}

	@Test
	public void composeArrayWithNoElements() {
		assertThat(ComposableSchemaObjectCollector.compose()).isNull();
	}

	@Test
	public void composeArrayWithOneElement() {
		assertThat(ComposableSchemaObjectCollector.compose(this.mockSchemaObjectCollectorOne))
			.isSameAs(this.mockSchemaObjectCollectorOne);
	}

	@Test
	public void composeArrayWithTwoElements() {

		SchemaObjectCollector<?> composedSchemaObjectCollector = ComposableSchemaObjectCollector.compose(
			this.mockSchemaObjectCollectorOne, this.mockSchemaObjectCollectorTwo);

		assertThat(composedSchemaObjectCollector).isInstanceOf(ComposableSchemaObjectCollector.class);
		assertThat((ComposableSchemaObjectCollector) composedSchemaObjectCollector).hasSize(2);
		assertThat((ComposableSchemaObjectCollector) composedSchemaObjectCollector)
			.containsAll(asSet(this.mockSchemaObjectCollectorOne, this.mockSchemaObjectCollectorTwo));

	}

	@Test
	public void composeIterableWithNoElements() {
		assertThat(ComposableSchemaObjectCollector.compose(emptyIterable())).isNull();
	}

	@Test
	public void composableIterableWithOneElement() {
		assertThat(ComposableSchemaObjectCollector.compose(Collections.singleton(this.mockSchemaObjectCollectorTwo)))
			.isSameAs(this.mockSchemaObjectCollectorTwo);
	}

	@Test
	public void composeIterableWithTwoElements() {

		SchemaObjectCollector<?> composedSchemaObjectCollector = ComposableSchemaObjectCollector.compose(
			asSet(this.mockSchemaObjectCollectorOne, this.mockSchemaObjectCollectorTwo));

		assertThat(composedSchemaObjectCollector).isInstanceOf(ComposableSchemaObjectCollector.class);
		assertThat((ComposableSchemaObjectCollector) composedSchemaObjectCollector)
			.containsAll(asSet(this.mockSchemaObjectCollectorOne, this.mockSchemaObjectCollectorTwo));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void collectFromApplicationContext() {

		SchemaObject region = SchemaObject.of(SchemaObjectType.REGION);
		SchemaObject index = SchemaObject.of(SchemaObjectType.INDEX);
		SchemaObject diskStore = SchemaObject.of(SchemaObjectType.DISK_STORE);

		when(this.mockSchemaObjectCollectorOne.collectFrom(any(ApplicationContext.class)))
			.thenReturn(asSet(region, index));

		when(this.mockSchemaObjectCollectorTwo.collectFrom(any(ApplicationContext.class)))
			.thenReturn(asSet(diskStore));

		SchemaObjectCollector composedSchemaObjectCollector = ComposableSchemaObjectCollector.compose(
			asSet(this.mockSchemaObjectCollectorOne, this.mockSchemaObjectCollectorTwo));

		assertThat(composedSchemaObjectCollector).isNotNull();

		Iterable<Object> schemaObjects = composedSchemaObjectCollector.collectFrom(this.mockApplicationContext);

		assertThat(schemaObjects).isNotNull();
		assertThat(schemaObjects).hasSize(3);
		assertThat(schemaObjects).contains(region, index, diskStore);

		verify(this.mockSchemaObjectCollectorOne, times(1))
			.collectFrom(eq(this.mockApplicationContext));

		verify(this.mockSchemaObjectCollectorTwo, times(1))
			.collectFrom(eq(this.mockApplicationContext));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void collectFromGemFireCache() {

		SchemaObject region = SchemaObject.of(SchemaObjectType.REGION);
		SchemaObject index = SchemaObject.of(SchemaObjectType.INDEX);
		SchemaObject diskStore = SchemaObject.of(SchemaObjectType.DISK_STORE);

		when(this.mockSchemaObjectCollectorOne.collectFrom(any(GemFireCache.class)))
			.thenReturn(asSet(index, diskStore));

		when(this.mockSchemaObjectCollectorTwo.collectFrom(any(GemFireCache.class)))
			.thenReturn(asSet(region));

		SchemaObjectCollector composedSchemaObjectCollector = ComposableSchemaObjectCollector.compose(
			asSet(this.mockSchemaObjectCollectorOne, this.mockSchemaObjectCollectorTwo));

		assertThat(composedSchemaObjectCollector).isNotNull();

		Iterable<Object> schemaObjects = composedSchemaObjectCollector.collectFrom(this.mockCache);

		assertThat(schemaObjects).isNotNull();
		assertThat(schemaObjects).hasSize(3);

		verify(this.mockSchemaObjectCollectorOne, times(1)).collectFrom(eq(this.mockCache));

		verify(this.mockSchemaObjectCollectorTwo, times(1)).collectFrom(eq(this.mockCache));
	}

	@RequiredArgsConstructor(staticName = "of")
	static class SchemaObject {
		@NonNull SchemaObjectType type;
	}
}
