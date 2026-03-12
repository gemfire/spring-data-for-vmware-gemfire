/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.mapping;

import org.springframework.data.gemfire.gud.api.GudPdxReader;

import org.springframework.data.mapping.model.PropertyValueProvider;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * {@link PropertyValueProvider} to read property values from {@link GudPdxReader}.
 *
 * @author Oliver Gierke
 * @author David Turanski
 * @author John Blum
 */
class GemfirePropertyValueProvider implements PropertyValueProvider<GemfirePersistentProperty> {

	private final GudPdxReader reader;

	/**
	 * Constructs a new instance of {@link GemfirePropertyValueProvider} with the given {@link GudPdxReader}.
	 *
	 * @param reader {@link GudPdxReader} used to read values from PDX serialized bytes; must not be {@literal null}.
	 * @throws IllegalArgumentException if the {@link GudPdxReader} is {@literal null}.
	 * @see GudPdxReader
	 */
	public GemfirePropertyValueProvider(@NonNull GudPdxReader reader) {
		Assert.notNull(reader, "PdxReader must not be null");
		this.reader = reader;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getPropertyValue(GemfirePersistentProperty property) {
		return (T) reader.readField(property.getName());
	}
}
