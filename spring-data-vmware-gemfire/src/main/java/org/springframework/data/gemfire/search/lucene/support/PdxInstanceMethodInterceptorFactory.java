/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.search.lucene.support;

import static org.springframework.data.gemfire.search.lucene.support.PdxInstanceMethodInterceptor.newPdxInstanceMethodInterceptor;

import org.apache.geode.pdx.PdxInstance;

import org.aopalliance.intercept.MethodInterceptor;

import org.springframework.data.projection.MethodInterceptorFactory;

/**
 * The {@link PdxInstanceMethodInterceptorFactory} class is a Spring Data {@link MethodInterceptorFactory} used to
 * identify {@link PdxInstance} types and instantiates an instance of the {@link PdxInstanceMethodInterceptor}
 * in order to intercept and handle invocations on the {@link PdxInstance} for the proxied projection.
 *
 * @author John Blum
 * @see org.aopalliance.intercept.MethodInterceptor
 * @see org.apache.geode.pdx.PdxInstance
 * @see org.springframework.data.gemfire.search.lucene.support.PdxInstanceMethodInterceptor
 * @see org.springframework.data.projection.MethodInterceptorFactory
 * @since 1.0.0
 */
public enum PdxInstanceMethodInterceptorFactory implements MethodInterceptorFactory {

	INSTANCE;

	/**
	 * @inheritDoc
	 */
	@Override
	public MethodInterceptor createMethodInterceptor(Object source, Class<?> targetType) {
		return newPdxInstanceMethodInterceptor(source);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean supports(Object source, Class<?> targetType) {
		return PdxInstance.class.isInstance(source);
	}
}
