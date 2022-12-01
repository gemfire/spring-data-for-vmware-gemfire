/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.annotation;

import java.util.Collections;
import java.util.Set;

import org.springframework.util.ClassUtils;

/**
 * Factory class used to construct an instance of the {@link Jsr107CacheAnnotationsCacheNameResolver} if and only if
 * the JSR-107, JCache API lib is on the application classpath.
 *
 * @author John Blum
 * @see Jsr107CacheAnnotationsCacheNameResolver
 * @since 2.2.0
 */
class Jsr107CacheAnnotationsCacheNameResolverFactory {

	private final boolean jcacheApiPresent;

	Jsr107CacheAnnotationsCacheNameResolverFactory() {

		this.jcacheApiPresent =
			ClassUtils.isPresent("javax.cache.annotation.CacheResult", getClass().getClassLoader());
	}

	CachingDefinedRegionsConfiguration.CacheNameResolver create() {

		return this.jcacheApiPresent
			? new Jsr107CacheAnnotationsCacheNameResolver()
			: new NoOpCacheNameResolver();
	}

	private static class NoOpCacheNameResolver implements CachingDefinedRegionsConfiguration.CacheNameResolver {

		@Override
		public Set<String> resolveCacheNames(Class<?> type) {
			return Collections.emptySet();
		}
	}
}
