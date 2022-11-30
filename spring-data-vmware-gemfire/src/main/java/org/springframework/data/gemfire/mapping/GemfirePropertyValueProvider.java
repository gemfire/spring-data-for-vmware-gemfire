// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.mapping;

import org.apache.geode.pdx.PdxReader;

import org.springframework.data.mapping.model.PropertyValueProvider;
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
	 * Creates a new {@link GemfirePropertyValueProvider} with the given {@link PdxReader}.
	 *
	 * @param reader must not be {@literal null}.
	 */
	public GemfirePropertyValueProvider(PdxReader reader) {
		Assert.notNull(reader, "PdxReader must not be null");
		this.reader = reader;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.convert.PropertyValueProvider#getPropertyValue(org.springframework.data.mapping.PersistentProperty)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getPropertyValue(GemfirePersistentProperty property) {
		return (T) reader.readField(property.getName());
	}
}
