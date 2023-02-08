/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.mapping;

import org.apache.geode.pdx.PdxReader;

import org.springframework.data.mapping.model.PropertyValueProvider;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * {@link PropertyValueProvider} to read property values from {@link PdxReader}.
 *
 * @author Oliver Gierke
 * @author David Turanski
 * @author John Blum
 */
class GemfirePropertyValueProvider implements PropertyValueProvider<GemfirePersistentProperty> {

	private final PdxReader reader;

	/**
	 * Constructs a new instance of {@link GemfirePropertyValueProvider} with the given {@link PdxReader}.
	 *
	 * @param reader {@link PdxReader} used to read values from PDX serialized bytes; must not be {@literal null}.
	 * @throws IllegalArgumentException if the {@link PdxReader} is {@literal null}.
	 * @see PdxReader
	 */
	public GemfirePropertyValueProvider(@NonNull PdxReader reader) {
		Assert.notNull(reader, "PdxReader must not be null");
		this.reader = reader;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getPropertyValue(GemfirePersistentProperty property) {
		return (T) reader.readField(property.getName());
	}
}
