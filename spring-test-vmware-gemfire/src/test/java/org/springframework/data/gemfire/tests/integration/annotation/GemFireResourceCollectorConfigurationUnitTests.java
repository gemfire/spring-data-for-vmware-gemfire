/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.integration.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.tests.integration.context.event.GemFireResourceCollectorApplicationListener;
import org.springframework.data.gemfire.tests.support.MapBuilder;
import org.springframework.data.gemfire.tests.util.ReflectionUtils;
import org.springframework.test.context.event.AfterTestClassEvent;
import org.springframework.test.context.event.AfterTestExecutionEvent;
import org.springframework.test.context.event.AfterTestMethodEvent;

/**
 * Unit Tests for {@link GemFireResourceCollectorConfiguration}.
 *
 * @author John Blum
 * @see Test
 * @see org.mockito.Mockito
 * @see GemFireResourceCollectorConfiguration
 * @since 0.0.17
 */
public class GemFireResourceCollectorConfigurationUnitTests {

	@Test
	public void setImportMetadataParsesConfiguration() {

		Map<String, Object> annotationAttributes = MapBuilder.<String, Object>newMapBuilder()
			.put("collectOnEvents", new Class[] { AfterTestMethodEvent.class, AfterTestExecutionEvent.class })
			.put("tryCleanDiskStoreFiles", true)
			.build();

		AnnotationMetadata mockAnnotationMetadata = mock(AnnotationMetadata.class);

		doReturn(true).when(mockAnnotationMetadata)
			.hasAnnotation(EnableGemFireResourceCollector.class.getName());
		doReturn(annotationAttributes).when(mockAnnotationMetadata)
			.getAnnotationAttributes(EnableGemFireResourceCollector.class.getName());

		GemFireResourceCollectorConfiguration configuration = new GemFireResourceCollectorConfiguration();

		assertThat(configuration.getConfiguredCollectorEventTypes()).containsExactly(AfterTestClassEvent.class);
		assertThat(configuration.isTryCleanDiskStoreFiles()).isFalse();

		configuration.setImportMetadata(mockAnnotationMetadata);

		assertThat(configuration.getConfiguredCollectorEventTypes())
			.containsExactly(AfterTestMethodEvent.class, AfterTestExecutionEvent.class);
		assertThat(configuration.isTryCleanDiskStoreFiles()).isTrue();

		verify(mockAnnotationMetadata, times(1))
			.hasAnnotation(eq(EnableGemFireResourceCollector.class.getName()));
		verify(mockAnnotationMetadata, times(1))
			.getAnnotationAttributes(eq(EnableGemFireResourceCollector.class.getName()));
	}

	@Test
	public void createsGemFireResourceCollectorApplicationListenerWithDefaultConfiguration()
			throws NoSuchFieldException {

		GemFireResourceCollectorConfiguration configuration = new GemFireResourceCollectorConfiguration();

		assertThat(configuration.getConfiguredCollectorEventTypes()).containsExactly(AfterTestClassEvent.class);
		assertThat(configuration.isTryCleanDiskStoreFiles()).isFalse();

		GemFireResourceCollectorApplicationListener listener =
			(GemFireResourceCollectorApplicationListener) configuration.gemfireResourceCollectorApplicationListener();

		assertThat(ReflectionUtils.<Set<Class<?>>>getFieldValue(listener, "gemfireResourceCollectorEventTypes"))
			.containsExactly(AfterTestClassEvent.class);

		assertThat(ReflectionUtils.<Boolean>getFieldValue(listener, "tryCleanDiskStoreFilesEnabled"))
			.isFalse();
	}

	@Test
	public void createsGemFireResourceCollectorApplicationListenerWithCustomConfiguration()
			throws NoSuchFieldException {

		GemFireResourceCollectorConfiguration configuration = spy(new GemFireResourceCollectorConfiguration());

		doReturn(new Class<?>[] { AfterTestMethodEvent.class, AfterTestExecutionEvent.class })
			.when(configuration).getConfiguredCollectorEventTypes();
		doReturn(true).when(configuration).isTryCleanDiskStoreFiles();

		assertThat(configuration.getConfiguredCollectorEventTypes())
			.containsExactly(AfterTestMethodEvent.class, AfterTestExecutionEvent.class);
		assertThat(configuration.isTryCleanDiskStoreFiles()).isTrue();

		GemFireResourceCollectorApplicationListener listener =
			(GemFireResourceCollectorApplicationListener) configuration.gemfireResourceCollectorApplicationListener();

		assertThat(ReflectionUtils.<Set<Class<?>>>getFieldValue(listener, "gemfireResourceCollectorEventTypes"))
			.containsExactlyInAnyOrder(AfterTestMethodEvent.class, AfterTestExecutionEvent.class);

		assertThat(ReflectionUtils.<Boolean>getFieldValue(listener, "tryCleanDiskStoreFilesEnabled"))
			.isTrue();
	}
}
