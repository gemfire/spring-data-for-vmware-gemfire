/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository;

import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * Simple {@link Object value object} holding an entity along with the external key in which the entity will be mapped.
 *
 * @author Oliver Gierke
 * @author John Blum
 */
public class Wrapper<T, KEY> {

	private final T entity;

	private final KEY key;

	public Wrapper(@NonNull T entity, @NonNull KEY key) {

		Assert.notNull(entity, "Entity must not be null");
		Assert.notNull(key, "Key must not be null");

		this.entity = entity;
		this.key = key;
	}

	public T getEntity() {
		return this.entity;
	}

	public KEY getKey() {
		return this.key;
	}

	@Override
	public String toString() {
		return String.format("{ key: %1$s, value: %2$s }", getKey(), getEntity());
	}
}
