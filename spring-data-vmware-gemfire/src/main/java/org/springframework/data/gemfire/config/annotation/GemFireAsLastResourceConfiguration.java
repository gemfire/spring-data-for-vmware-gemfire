// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.config.annotation;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalArgumentException;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalStateException;

import java.util.Map;
import java.util.Optional;

import org.apache.geode.cache.GemFireCache;

import org.aspectj.lang.annotation.Aspect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.config.annotation.support.GemFireAsLastResourceConnectionAcquiringAspect;
import org.springframework.data.gemfire.config.annotation.support.GemFireAsLastResourceConnectionClosingAspect;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * The {@link GemFireAsLastResourceConfiguration} class is a Spring {@link Configuration @Configuration}
 * annotated class used to configure the GemFire "Last Resource" Spring Data GemFire {@link Aspect Aspects}.
 *
 * @author John Blum
 * @see GemFireCache
 * @see Aspect
 * @see Bean
 * @see Configuration
 * @see ImportAware
 * @see AnnotationAttributes
 * @see AnnotationMetadata
 * @see EnableGemFireAsLastResource
 * @see GemFireAsLastResourceConnectionAcquiringAspect
 * @see GemFireAsLastResourceConnectionClosingAspect
 * @see EnableTransactionManagement
 * @since 1.0.0
 */
@Configuration
@SuppressWarnings("unused")
public class GemFireAsLastResourceConfiguration implements ImportAware {

	private Integer enableTransactionManagementOrder;

	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {
		this.enableTransactionManagementOrder = resolveEnableTransactionManagementOrder(importMetadata);
	}

	protected int resolveEnableTransactionManagementOrder(AnnotationMetadata importMetadata) {

		AnnotationAttributes enableTransactionManagementAttributes =
			resolveEnableTransactionManagementAttributes(importMetadata);

		Integer order = enableTransactionManagementAttributes.getNumber("order");

		return Optional.ofNullable(order)
			.filter(it -> !(it == Integer.MAX_VALUE || it == Integer.MIN_VALUE))
			.orElseThrow(() -> newIllegalArgumentException(
				"The @%1$s(order) attribute value [%2$s] must be explicitly set to a value"
					+ " other than Integer.MAX_VALUE or Integer.MIN_VALUE",
				EnableTransactionManagement.class.getSimpleName(), String.valueOf(order)));
	}

	protected AnnotationAttributes resolveEnableTransactionManagementAttributes(
			AnnotationMetadata importMetadata) {

		Map<String, Object> enableTransactionManagementAttributes =
			importMetadata.getAnnotationAttributes(EnableTransactionManagement.class.getName());

		return Optional.ofNullable(enableTransactionManagementAttributes)
			.map(AnnotationAttributes::fromMap)
			.orElseThrow(() -> newIllegalStateException(
				"The @%1$s annotation may only be used on a Spring application @%2$s class"
					+ " that is also annotated with @%3$s having an explicit [order] set",
				EnableGemFireAsLastResource.class.getSimpleName(), Configuration.class.getSimpleName(),
				EnableTransactionManagement.class.getSimpleName()));
	}

	protected Integer getEnableTransactionManagementOrder() {

		return Optional.ofNullable(this.enableTransactionManagementOrder)
			.orElseThrow(() -> newIllegalStateException(
				"The @%1$s(order) attribute was not properly set [%2$s]; Also, please make your"
					+ " Spring application @%3$s annotated class is annotated with both @%4$s and @%1$s",
				EnableTransactionManagement.class.getSimpleName(), String.valueOf(this.enableTransactionManagementOrder),
				Configuration.class.getSimpleName(), EnableGemFireAsLastResource.class.getSimpleName()));
	}

	@Bean
	public Object gemfireCachePostProcessor(@Autowired(required = false) GemFireCache gemfireCache) {

		Optional.ofNullable(gemfireCache)
			.ifPresent(cache -> cache.setCopyOnRead(true));

		return null;
	}

	@Bean
	public GemFireAsLastResourceConnectionAcquiringAspect gemfireJcaConnectionAcquiringAspect() {

		GemFireAsLastResourceConnectionAcquiringAspect connectionAcquiringAspect =
			new GemFireAsLastResourceConnectionAcquiringAspect();

		int order = (getEnableTransactionManagementOrder() + 1);

		connectionAcquiringAspect.setOrder(order);

		return connectionAcquiringAspect;
	}

	@Bean
	public GemFireAsLastResourceConnectionClosingAspect gemfireJcaConnectionClosingAspect() {

		GemFireAsLastResourceConnectionClosingAspect connectionClosingAspect =
			new GemFireAsLastResourceConnectionClosingAspect();

		int order = (getEnableTransactionManagementOrder() - 1);

		connectionClosingAspect.setOrder(order);

		return connectionClosingAspect;
	}
}
