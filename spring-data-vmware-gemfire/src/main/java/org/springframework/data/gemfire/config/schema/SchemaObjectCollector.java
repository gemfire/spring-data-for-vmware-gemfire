/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.schema;

import java.util.Collections;
import java.util.Set;
import org.apache.geode.cache.client.ClientCache;
import org.springframework.context.ApplicationContext;

/**
 * The {@link SchemaObjectCollector} interface defines a contract for implementing objects to search for
 * and find all schema objects of a particular type in a given context.
 *
 * Implementations of this interface know how to inspect the given context and find all references
 * to the schema object instances of a particular type.
 *
 * @author John Blum
 * @see ClientCache
 * @see ApplicationContext
 * @since 2.0.0
 */
public interface SchemaObjectCollector<T> {

	/**
	 * Collects all schema objects of type {@link T} declared in the given {@link ApplicationContext}.
	 *
	 * @param applicationContext Spring {@link ApplicationContext} from which to collect schema objects
	 * of type {@link T}.
	 * @return a {@link Set} of all schema objects of type {@link T} declared in the {@link ApplicationContext};
	 * returns an empty {@link Set} if no schema object of type {@link T} could be found.
	 * @see ApplicationContext
	 * @see Iterable
	 */
	default Iterable<T> collectFrom(ApplicationContext applicationContext) {
		return Collections.emptySet();
	}

	/**
	 * Collects all schema objects of type {@link T} defined in the {@link ClientCache}.
	 *
	 * @param gemfireCache {@link ClientCache} from which to collect schema objects of type {@link T}.
	 * @return a {@link Set} of all schema objects of type {@link T} defined in the {@link ClientCache};
	 * returns an empty {@link Set} if no schema object of type {@link T} could be found.
	 * @see ClientCache
	 * @see Iterable
	 */
	default Iterable<T> collectFrom(ClientCache gemfireCache) {
		return Collections.emptySet();
	}
}
