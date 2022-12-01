/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
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
 * @see Test
 * @see RunWith
 * @see Mock
 * @see org.mockito.Mockito
 * @see MockitoJUnitRunner
 * @see MethodInterceptor
 * @see PdxInstance
 * @see PdxInstanceMethodInterceptorFactory
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
