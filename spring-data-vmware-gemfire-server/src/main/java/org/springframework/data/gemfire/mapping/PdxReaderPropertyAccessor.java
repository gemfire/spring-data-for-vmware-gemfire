/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.mapping;

import org.apache.geode.pdx.PdxReader;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;

/**
 * {@link PropertyAccessor} used to read values from a {@link PdxReader}.
 *
 * @author Oliver Gierke
 * @author John Blum
 * @see PdxReader
 * @see PropertyAccessor
 */
enum PdxReaderPropertyAccessor implements PropertyAccessor {

	INSTANCE;

	/**
	 * @inheritDoc
	 */
	@Override
	public Class<?>[] getSpecificTargetClasses() {
		return new Class<?>[] { PdxReader.class };
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean canRead(EvaluationContext evaluationContext, Object target, String name) {
		return ((PdxReader) target).hasField(name);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public TypedValue read(EvaluationContext evaluationContext, Object target, String name) {

		Object object = ((PdxReader) target).readObject(name);

		return object != null
			? new TypedValue(object)
			: TypedValue.NULL;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean canWrite(EvaluationContext evaluationContext, Object target, String name) {
		return false;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void write(EvaluationContext evaluationContext, Object target, String name, Object newValue) {
		throw new UnsupportedOperationException();
	}
}
