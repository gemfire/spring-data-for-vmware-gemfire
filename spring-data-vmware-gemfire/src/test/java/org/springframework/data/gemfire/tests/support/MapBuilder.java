/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.tests.support;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link MapBuilder} class employs the Builder Software Design Pattern to build a {@link Map}.
 *
 * @author John Blum
 * @see Map
 * @since 0.0.1
 */
public class MapBuilder<KEY, VALUE> {

	public static <KEY, VALUE> MapBuilder<KEY, VALUE> newMapBuilder() {
		return new MapBuilder<>();
	}

	private final Map<KEY, VALUE> map = new HashMap<>();

	public MapBuilder<KEY, VALUE> put(KEY key, VALUE value) {
		this.map.put(key, value);
		return this;
	}

	public MapBuilder<KEY, VALUE> remove(KEY key) {
		this.map.remove(key);
		return this;
	}

	public Map<KEY, VALUE> build() {
		return this.map;
	}
}
