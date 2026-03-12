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

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;

/**
 * {@link PropertyAccessor} used to read values from a {@link GudPdxReader}.
 *
 * @author Oliver Gierke
 * @author John Blum
 * @see GudPdxReader
 * @see PropertyAccessor
 */
enum PdxReaderPropertyAccessor implements PropertyAccessor {

	INSTANCE;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?>[] getSpecificTargetClasses() {
		return new Class<?>[] { GudPdxReader.class };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canRead(EvaluationContext evaluationContext, Object target, String name) {
		return ((GudPdxReader) target).hasField(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypedValue read(EvaluationContext evaluationContext, Object target, String name) {

		Object object = ((GudPdxReader) target).readObject(name);

		return object != null
			? new TypedValue(object)
			: TypedValue.NULL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canWrite(EvaluationContext evaluationContext, Object target, String name) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(EvaluationContext evaluationContext, Object target, String name, Object newValue) {
		throw new UnsupportedOperationException();
	}
}
