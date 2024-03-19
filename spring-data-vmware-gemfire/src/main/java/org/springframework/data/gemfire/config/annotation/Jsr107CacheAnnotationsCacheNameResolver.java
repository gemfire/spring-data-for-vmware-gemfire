/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.springframework.data.gemfire.util.ArrayUtils.asArray;

import java.lang.annotation.Annotation;

import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CachePut;
import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheResult;

/**
 * The {@link Jsr107CacheAnnotationsCacheNameResolver} class is a {@link CachingDefinedRegionsConfiguration.CacheNameResolver}
 * implementation that can resolve JSR-107, JCache API cache annotations from a given {@link Class class type}.
 *
 * @author John Blum
 * @see java.lang.annotation.Annotation
 * @see org.springframework.data.gemfire.config.annotation.CachingDefinedRegionsConfiguration.AbstractCacheNameResolver
 * @since 2.2.0
 */
class Jsr107CacheAnnotationsCacheNameResolver extends CachingDefinedRegionsConfiguration.AbstractCacheNameResolver {

	@Override
	@SuppressWarnings("unchecked")
	protected Class<? extends Annotation>[] getClassCacheAnnotationTypes() {
		return append(getMethodCacheAnnotationTypes(), CacheDefaults.class);
	}

	@Override
	protected Class<? extends Annotation>[] getMethodCacheAnnotationTypes() {

		return asArray(
			CachePut.class,
			CacheRemove.class,
			CacheRemoveAll.class,
			CacheResult.class
		);
	}
}
