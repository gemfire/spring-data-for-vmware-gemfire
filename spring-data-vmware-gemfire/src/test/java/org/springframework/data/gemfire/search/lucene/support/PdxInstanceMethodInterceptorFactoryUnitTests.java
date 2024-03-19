/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.search.lucene.support;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.pdx.PdxInstance;

import org.aopalliance.intercept.MethodInterceptor;

/**
 * Unit tests for {@link PdxInstanceMethodInterceptorFactory}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.junit.runner.RunWith
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.mockito.junit.MockitoJUnitRunner
 * @see org.aopalliance.intercept.MethodInterceptor
 * @see org.apache.geode.pdx.PdxInstance
 * @see org.springframework.data.gemfire.search.lucene.support.PdxInstanceMethodInterceptorFactory
 * @since 1.1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class PdxInstanceMethodInterceptorFactoryUnitTests {

	@Mock
	private PdxInstance mockSource;

	@Test
	public void createMethodInterceptorIsSuccessful() {
		MethodInterceptor methodInterceptor =
			PdxInstanceMethodInterceptorFactory.INSTANCE.createMethodInterceptor(mockSource, Object.class);

		assertThat(methodInterceptor).isInstanceOf(PdxInstanceMethodInterceptor.class);
		assertThat(((PdxInstanceMethodInterceptor) methodInterceptor).getSource()).isSameAs(mockSource);
	}

	@Test
	public void supportsPdxInstances() {
		assertThat(PdxInstanceMethodInterceptorFactory.INSTANCE.supports(mockSource, Object.class)).isTrue();
	}

	@Test
	public void doesNotSupportNonPdxInstances() {
		assertThat(PdxInstanceMethodInterceptorFactory.INSTANCE.supports(new Object(), Object.class)).isFalse();
		assertThat(PdxInstanceMethodInterceptorFactory.INSTANCE.supports(Boolean.TRUE, Object.class)).isFalse();
		assertThat(PdxInstanceMethodInterceptorFactory.INSTANCE.supports('X', Object.class)).isFalse();
		assertThat(PdxInstanceMethodInterceptorFactory.INSTANCE.supports(2, Object.class)).isFalse();
		assertThat(PdxInstanceMethodInterceptorFactory.INSTANCE.supports(Math.PI, Object.class)).isFalse();
		assertThat(PdxInstanceMethodInterceptorFactory.INSTANCE.supports("test", Object.class)).isFalse();
	}
}
