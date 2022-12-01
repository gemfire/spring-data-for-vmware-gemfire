/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.gemfire.function.config.one.TestFunctionExecution;

/**
 * Unit Tests for {@link FunctionExecutionComponentProvider}.
 *
 * @author David Turanski
 * @author John Blum
 * @see Test
 * @see FunctionExecutionComponentProvider
 */
public class FunctionExecutionComponentProviderUnitTests {

	@Test
	public void testDiscovery() {

		List<TypeFilter> includeFilters = new ArrayList<>();

		FunctionExecutionComponentProvider provider = new FunctionExecutionComponentProvider(includeFilters,
				AnnotationFunctionExecutionConfigurationSource.getFunctionExecutionAnnotationTypes());

		Set<BeanDefinition> candidates =
			provider.findCandidateComponents(this.getClass().getPackage().getName() + ".one");

		ScannedGenericBeanDefinition beanDefinition = null;

		for (BeanDefinition candidate : candidates) {
			if (candidate.getBeanClassName().equals(TestFunctionExecution.class.getName())) {
				beanDefinition = (ScannedGenericBeanDefinition) candidate;
			}
		}

		assertThat(beanDefinition).isNotNull();
	}

	@Test
	public void testExcludeFilter() {

		List<TypeFilter> includeFilters = new ArrayList<>();

		FunctionExecutionComponentProvider provider =
			new FunctionExecutionComponentProvider(includeFilters,
				AnnotationFunctionExecutionConfigurationSource.getFunctionExecutionAnnotationTypes());

		provider.addExcludeFilter(new AssignableTypeFilter(TestFunctionExecution.class));

		Set<BeanDefinition> candidates =
			provider.findCandidateComponents(this.getClass().getPackage().getName() + ".one");

		for (BeanDefinition candidate : candidates) {
			if (candidate.getBeanClassName().equals(TestFunctionExecution.class.getName())) {
				fail(TestFunctionExecution.class.getName() + " not excluded");
			}
		}
	}
}
