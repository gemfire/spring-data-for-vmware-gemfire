// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.function.config;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.gemfire.function.annotation.OnMember;
import org.springframework.data.gemfire.function.annotation.OnMembers;
import org.springframework.data.gemfire.function.annotation.OnRegion;
import org.springframework.data.gemfire.function.annotation.OnServer;
import org.springframework.data.gemfire.function.annotation.OnServers;
import org.springframework.data.gemfire.util.CollectionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base class and configuration source for Function Executions.
 *
 * @author David Turanski
 * @author John Blum
 * @see FunctionExecutionConfiguration
 */
public abstract class AbstractFunctionExecutionConfigurationSource implements FunctionExecutionConfigurationSource {

	private static Set<Class<? extends Annotation>> functionExecutionAnnotationTypes;

	static {

		Set<Class<? extends Annotation>> annotationTypes = new HashSet<>(5);

		annotationTypes.add(OnMember.class);
		annotationTypes.add(OnMembers.class);
		annotationTypes.add(OnRegion.class);
		annotationTypes.add(OnServer.class);
		annotationTypes.add(OnServers.class);

		functionExecutionAnnotationTypes = Collections.unmodifiableSet(annotationTypes);
	}

	public static Set<Class<? extends Annotation>> getFunctionExecutionAnnotationTypes() {
		return functionExecutionAnnotationTypes;
	}

	public static Set<String> getFunctionExecutionAnnotationTypeNames() {

		return getFunctionExecutionAnnotationTypes().stream()
			.map(Class::getName)
			.collect(Collectors.toSet());
	}

	protected Logger logger = LoggerFactory.getLogger(getClass());

	public Collection<ScannedGenericBeanDefinition> getCandidates(ResourceLoader loader) {

		ClassPathScanningCandidateComponentProvider scanner =
			new FunctionExecutionComponentProvider(getIncludeFilters(), getFunctionExecutionAnnotationTypes());

		scanner.setResourceLoader(loader);

		StreamSupport.stream(CollectionUtils.nullSafeIterable(getExcludeFilters()).spliterator(), false)
			.forEach(scanner::addExcludeFilter);

		Set<ScannedGenericBeanDefinition> result = new HashSet<>();

		for (String basePackage : getBasePackages()) {

			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Scanning Package [{}]", basePackage);
			}

			scanner.findCandidateComponents(basePackage).stream()
				.map(ScannedGenericBeanDefinition.class::cast)
				.forEach(result::add);
		}

		return result;
	}
}
