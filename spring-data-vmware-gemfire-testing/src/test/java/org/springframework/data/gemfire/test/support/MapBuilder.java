/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.test.support;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link MapBuilder} class employs the Builder Software Design Pattern to build a {@link Map}.
 *
 * @author John Blum
 * @see java.util.Map
 * @since 2.0.0
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
