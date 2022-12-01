/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.mapping;

import org.apache.geode.pdx.PdxReader;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;

/**
 * {@link PropertyAccessor} to read values from a {@link PdxReader}.
 *
 * @author Oliver Gierke
 */
enum PdxReaderPropertyAccessor implements PropertyAccessor {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see org.springframework.expression.PropertyAccessor#getSpecificTargetClasses()
	 */
	@Override
	public Class<?>[] getSpecificTargetClasses() {
		return new Class<?>[] { PdxReader.class };
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.expression.PropertyAccessor#canRead(org.springframework.expression.EvaluationContext, java.lang.Object, java.lang.String)
	 */
	@Override
	public boolean canRead(EvaluationContext evaluationContext, Object target, String name) {
		return ((PdxReader) target).hasField(name);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.expression.PropertyAccessor#read(org.springframework.expression.EvaluationContext, java.lang.Object, java.lang.String)
	 */
	@Override
	public TypedValue read(EvaluationContext evaluationContext, Object target, String name) {

		Object object = ((PdxReader) target).readObject(name);

		return object != null ? new TypedValue(object) : TypedValue.NULL;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.expression.PropertyAccessor#canWrite(org.springframework.expression.EvaluationContext, java.lang.Object, java.lang.String)
	 */
	@Override
	public boolean canWrite(EvaluationContext evaluationContext, Object target, String name) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.expression.PropertyAccessor#write(org.springframework.expression.EvaluationContext, java.lang.Object, java.lang.String, java.lang.Object)
	 */
	@Override
	public void write(EvaluationContext evaluationContext, Object target, String name, Object newValue) {
		throw new UnsupportedOperationException();
	}
}
