/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.config;

import static org.springframework.data.gemfire.util.ArrayUtils.nullSafeArray;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalArgumentException;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;

import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Annotation based configuration source for {@link Function} {@link Execution Executions}.
 *
 * @author David Turanski
 * @author John Blum
 * @see Annotation
 * @see Execution
 * @see Function
 * @see EnableGemfireFunctionExecutions
 */
public class AnnotationFunctionExecutionConfigurationSource extends AbstractFunctionExecutionConfigurationSource {

	private static final String BASE_PACKAGES = "basePackages";
	private static final String BASE_PACKAGE_CLASSES = "basePackageClasses";
	private static final String VALUE = "value";

	private final AnnotationMetadata metadata;
	private final AnnotationAttributes attributes;

	/**
	 * Constructs a new instance of {@link AnnotationFunctionExecutionConfigurationSource}
	 * from the given {@link AnnotationMetadata} and {@link EnableGemfireFunctionExecutions} annotation.
	 *
	 * @param metadata {@link AnnotationMetadata} for the {@link EnableGemfireFunctionExecutions} annotation;
	 * must not be {@literal null}.
	 * @see AnnotationMetadata
	 */
	 public AnnotationFunctionExecutionConfigurationSource(@NonNull AnnotationMetadata metadata) {

		Assert.notNull(metadata, "AnnotationMetadata must not be null");

		this.attributes = AnnotationAttributes
			.fromMap(metadata.getAnnotationAttributes(EnableGemfireFunctionExecutions.class.getName()));

		this.metadata = metadata;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getSource() {
		return this.metadata;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterable<String> getBasePackages() {

		String[] value = this.attributes.getStringArray(VALUE);
		String[] basePackages = this.attributes.getStringArray(BASE_PACKAGES);

		Class<?>[] basePackageClasses = this.attributes.getClassArray(BASE_PACKAGE_CLASSES);

		// Default configuration - return package of annotated class
		if (areAllEmpty(value, basePackages, basePackageClasses)) {

			String className = this.metadata.getClassName();

			return Collections.singleton(className.substring(0, className.lastIndexOf('.')));
		}

		Set<String> packages = new HashSet<>();

		packages.addAll(Arrays.asList(value));
		packages.addAll(Arrays.asList(basePackages));

		Arrays.stream(nullSafeArray(basePackageClasses, Class.class))
			.map(ClassUtils::getPackageName)
			.forEach(packages::add);

		return packages;
	}

	private boolean areAllEmpty(@Nullable Object[]... arrays) {

		for (Object[] array : ArrayUtils.nullSafeArray(arrays, Object[].class)) {
			if (!ArrayUtils.isEmpty(array)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterable<TypeFilter> getIncludeFilters() {
		return parseFilters("includeFilters");
	}

	/**
	 * {@inheritDoc}
	 */
 	@Override
	public Iterable<TypeFilter> getExcludeFilters() {
		return parseFilters("excludeFilters");
	}

	private Set<TypeFilter> parseFilters(String attributeName) {

		Set<TypeFilter> result = new HashSet<>();

		AnnotationAttributes[] filters = this.attributes.getAnnotationArray(attributeName);

		Arrays.stream(ArrayUtils.nullSafeArray(filters, AnnotationAttributes.class))
			.map(this::typeFiltersFor)
			.forEach(result::addAll);

		return result;
	}

	/**
	 * Copy of {@code ComponentScanAnnotationParser#typeFiltersFor}.
	 *
	 * @param filterAttributes {@link AnnotationAttributes} for the {@literal include} and {@literal exclude} filters.
	 * @return a {@link List} of {@link TypeFilter TypeFilters} based on the configuration of the {@literal include}
	 * and {@literal exclude} attributes.
	 * @see AnnotationAttributes
	 */
	@SuppressWarnings("unchecked")
	private List<TypeFilter> typeFiltersFor(AnnotationAttributes filterAttributes) {

		List<TypeFilter> typeFilters = new ArrayList<>();

		FilterType filterType = filterAttributes.getEnum("type");

		for (Class<?> filterClass : filterAttributes.getClassArray("value")) {
			switch (filterType) {
			case ANNOTATION:
				String message = "An error occured when processing a @ComponentScan ANNOTATION type filter: ";
				Assert.isAssignable(Annotation.class, filterClass, message);
				Class<Annotation> annoClass = (Class<Annotation>) filterClass;
				typeFilters.add(new AnnotationTypeFilter(annoClass));
				break;
			case ASSIGNABLE_TYPE:
				typeFilters.add(new AssignableTypeFilter(filterClass));
				break;
			case CUSTOM:
				message = "An error occurred when processing a @ComponentScan CUSTOM type filter: ";
				Assert.isAssignable(TypeFilter.class, filterClass, message);
				typeFilters.add(BeanUtils.instantiateClass(filterClass, TypeFilter.class));
				break;
			default:
				throw newIllegalArgumentException("Unknown filter type [%s]", filterType);
			}
		}

		return typeFilters;
	}
}
