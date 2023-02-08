/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.schema.support;

import static java.util.stream.StreamSupport.stream;
import static org.springframework.data.gemfire.util.ArrayUtils.nullSafeArray;
import static org.springframework.data.gemfire.util.CollectionUtils.nullSafeIterable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.geode.cache.GemFireCache;

import org.springframework.context.ApplicationContext;
import org.springframework.data.gemfire.config.schema.SchemaObjectCollector;
import org.springframework.lang.Nullable;

/**
 * The {@link ComposableSchemaObjectCollector} class is a {@link SchemaObjectCollector} implementation composed of
 * multiple {@link SchemaObjectCollector} objects wrapped in a facade and treated like a single
 * {@link SchemaObjectCollector} using the Composite Software Design Pattern.
 *
 * @author John Blum
 * @see SchemaObjectCollector
 * @since 2.0.0
 */
public final class ComposableSchemaObjectCollector
		implements SchemaObjectCollector<Object>, Iterable<SchemaObjectCollector<?>> {

	private final List<SchemaObjectCollector<?>> schemaObjectCollectors;

	@Nullable
	@SuppressWarnings("unchecked")
	public static SchemaObjectCollector<?> compose(SchemaObjectCollector<?>... schemaObjectCollectors) {
		return compose(Arrays.asList(nullSafeArray(schemaObjectCollectors, SchemaObjectCollector.class)));
	}

	@Nullable
	public static SchemaObjectCollector<?> compose(Iterable<SchemaObjectCollector<?>> schemaObjectCollectors) {

		List<SchemaObjectCollector<?>> schemaObjectCollectorList =
			stream(nullSafeIterable(schemaObjectCollectors).spliterator(), false)
				.filter(Objects::nonNull).collect(Collectors.toList());

		return (schemaObjectCollectorList.isEmpty() ? null
			: (schemaObjectCollectorList.size() == 1 ? schemaObjectCollectorList.iterator().next()
				: new ComposableSchemaObjectCollector(schemaObjectCollectorList)));
	}

	private ComposableSchemaObjectCollector(List<SchemaObjectCollector<?>> schemaObjectCollectors) {
		this.schemaObjectCollectors = Collections.unmodifiableList(schemaObjectCollectors);
	}

	@Override
	public Iterable<Object> collectFrom(ApplicationContext applicationContext) {
		return collectFrom(collector -> collector.collectFrom(applicationContext));
	}

	@Override
	public Iterable<Object> collectFrom(GemFireCache gemfireCache) {
		return collectFrom(collector -> collector.collectFrom(gemfireCache));
	}

	private Iterable<Object> collectFrom(Function<SchemaObjectCollector<?>, Iterable<?>> schemaObjectSource) {

		return this.schemaObjectCollectors.stream()
			.flatMap(collector -> stream(schemaObjectSource.apply(collector).spliterator(), false))
			.collect(Collectors.toList());
	}

	@Override
	public Iterator<SchemaObjectCollector<?>> iterator() {
		return Collections.unmodifiableList(this.schemaObjectCollectors).iterator();
	}
}
