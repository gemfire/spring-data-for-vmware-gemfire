// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.function.config;

import java.lang.annotation.Annotation;

import org.springframework.core.type.filter.TypeFilter;

/**
 * Interface for Function Execution configuration sources (e.g. {@link Annotation} or XML configuration)
 * to configure classpath scanning of annotated interfaces to implement proxies that invoke Functions.
 *
 * @author David Turanski
 * @author John Blum
 */
public interface FunctionExecutionConfigurationSource {

	/**
	 * Returns the base packages the repository interfaces shall be found under.
	 *
	 * @return must not be {@literal null}.
	 */
	Iterable<String> getBasePackages();

	/**
	 * Returns configured {@link TypeFilter}s
	 * @return include filters
	 */
	Iterable<TypeFilter> getIncludeFilters();

	/**
	 * Returns configured {@link TypeFilter}s
	 * @return exclude filters
	 */
	Iterable<TypeFilter> getExcludeFilters();

	/**
	 * Returns the actual source object that the configuration originated from. Will be used by the tooling to give visual
	 * feedback on where the repository instances actually come from.
	 *
	 * @return must not be {@literal null}.
	 */
	Object getSource();

}
