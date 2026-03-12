/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire;

import java.util.function.Supplier;
import org.springframework.data.gemfire.gud.api.GudClientCache;

/**
 * Strategy interface for resolving a instance reference to a {@link GudClientCache}.
 *
 * @author John Blum
 * @param <T> {@link Class subclass} of {@link GudClientCache}.
 * @see Supplier
 * @see GudClientCache
 * @since 2.3.0
 */
@FunctionalInterface
public interface CacheResolver<T extends GudClientCache> extends Supplier<T> {

	/**
	 * Gets the resolved instance of the {@link GudClientCache}.
	 *
	 * @return the resolved instance of the {@link GudClientCache}.
	 * @see #resolve()
	 */
	@Override
	default T get() {
		return resolve();
	}

	/**
	 * Resolves the instance reference to the {@link GudClientCache} implementation.
	 *
	 * @return a instance reference to a {@link GudClientCache} implementation.
	 */
	T resolve();

}
