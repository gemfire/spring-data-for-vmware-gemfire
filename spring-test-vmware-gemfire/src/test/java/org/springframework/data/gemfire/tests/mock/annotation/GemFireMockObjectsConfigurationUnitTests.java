/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.mock.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.tests.mock.beans.factory.config.GemFireMockObjectsBeanPostProcessor;
import org.springframework.data.gemfire.tests.mock.context.event.DestroyGemFireMockObjectsApplicationListener;
import org.springframework.data.gemfire.tests.util.ReflectionUtils;
import org.springframework.test.context.event.AfterTestClassEvent;

/**
 * Unit Tests for {@link GemFireMockObjectsConfiguration}.
 *
 * @author John Blum
 * @see Test
 * @see org.mockito.Mockito
 * @see GemFireMockObjectsConfiguration
 * @see GemFireMockObjectsBeanPostProcessor
 * @see DestroyGemFireMockObjectsApplicationListener
 * @since 0.0.16
 */
public class GemFireMockObjectsConfigurationUnitTests {

	@Test
	public void setImportMetadataConfiguresSingletonCacheAndDestroysMockObjectsOnConfiguredEvents() {

		GemFireMockObjectsConfiguration configuration = new GemFireMockObjectsConfiguration();

		assertThat(configuration.getConfiguredDestroyEventTypes()).containsExactly(AfterTestClassEvent.class);
		assertThat(configuration.isUseSingletonCacheConfigured()).isFalse();

		Map<String, Object> enableGemFireMockObjectsAttributes = new HashMap<>();

		enableGemFireMockObjectsAttributes.put("destroyOnEvents",
			new Class[] { ContextClosedEvent.class, ContextRefreshedEvent.class });
		enableGemFireMockObjectsAttributes.put("useSingletonCache", true);

		AnnotationMetadata mockAnnotationMetadata = mock(AnnotationMetadata.class);

		doReturn(true).when(mockAnnotationMetadata)
			.hasAnnotation(eq(EnableGemFireMockObjects.class.getName()));

		doReturn(enableGemFireMockObjectsAttributes).when(mockAnnotationMetadata)
			.getAnnotationAttributes(eq(EnableGemFireMockObjects.class.getName()));

		configuration.setImportMetadata(mockAnnotationMetadata);

		assertThat(configuration.getConfiguredDestroyEventTypes())
			.containsExactly(ContextClosedEvent.class, ContextRefreshedEvent.class);
		assertThat(configuration.isUseSingletonCacheConfigured()).isTrue();

		verify(mockAnnotationMetadata, times(1))
			.hasAnnotation(eq(EnableGemFireMockObjects.class.getName()));
		verify(mockAnnotationMetadata, times(1))
			.getAnnotationAttributes(eq(EnableGemFireMockObjects.class.getName()));
	}

	@Test
	public void configuresGemFireMockObjectsBeanPostProcessor() throws NoSuchFieldException {

		GemFireMockObjectsConfiguration configuration = spy(new GemFireMockObjectsConfiguration());

		doReturn(true).when(configuration).isUseSingletonCacheConfigured();

		GemFireMockObjectsBeanPostProcessor beanPostProcessor =
			(GemFireMockObjectsBeanPostProcessor) configuration.gemfireMockObjectsBeanPostProcessor();

		assertThat(beanPostProcessor).isNotNull();
		assertThat(ReflectionUtils.<Boolean>getFieldValue(beanPostProcessor, "useSingletonCache")).isTrue();

		verify(configuration, times(1)).isUseSingletonCacheConfigured();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void configuresDestroyGemFireMockObjectsApplicationListener() throws NoSuchFieldException {

		GemFireMockObjectsConfiguration configuration = spy(new GemFireMockObjectsConfiguration());

		Class<? extends ApplicationEvent>[] destroyEventTypes =
			new Class[] { ContextRefreshedEvent.class, ContextClosedEvent.class };

		doReturn(destroyEventTypes).when(configuration).getConfiguredDestroyEventTypes();

		DestroyGemFireMockObjectsApplicationListener applicationListener =
			(DestroyGemFireMockObjectsApplicationListener) configuration.destroyGemFireMockObjectsApplicationListener();

		assertThat(applicationListener).isNotNull();
		assertThat(ReflectionUtils.<Set<Class<? extends ApplicationEvent>>>getFieldValue(applicationListener, "configuredDestroyEventTypes"))
			.containsExactlyInAnyOrder(destroyEventTypes);

		verify(configuration, times(1)).getConfiguredDestroyEventTypes();
	}
}
