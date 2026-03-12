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

import org.springframework.data.gemfire.gud.api.GudGemFireCheckedException;
import org.springframework.data.gemfire.gud.api.GudGemFireException;
import org.springframework.data.gemfire.gud.api.GudRegion;

/**
 * Callback interface for GemFire code.
 *
 * Implementations of this interface are to be used with {@link GemfireTemplate}'s execution methods, often as anonymous
 * classes within a method implementation. A typical implementation will call Region.get/put/query to perform some
 * operations on stored objects.
 *
 * @author Costin Leau
 * @author John Blum
 * @see GudRegion
 */
@FunctionalInterface
public interface GemfireCallback<T> {

	/**
	 * This methods gets called by {@link GemfireTemplate#execute(GemfireCallback)}.
	 *
	 * The method implementation does not need to care about handling transactions or exceptions.
	 *
	 * Allows a result {@link Object} created within this callback to be returned, i.e. an application domain object
	 * or a collection of application domain objects.
	 *
	 * A custom thrown {@link RuntimeException} is treated as an application exception; the exception is propagated to
	 * the caller of the template.
	 *
	 * @param region {@link GudRegion} on which the operation of this callback will be performed.
	 * @return a result {@link Object}, or {@literal null} if no result.
	 * @throws GudGemFireCheckedException for checked {@link Exception Exceptions} occurring in GemFire.
	 * @throws GudGemFireException for {@link RuntimeException RuntimeExceptions} occurring in GemFire.
	 * @see GemfireTemplate
	 * @see GudRegion
	 */
	T doInGemfire(GudRegion<?, ?> region) throws GudGemFireCheckedException, GudGemFireException;

}
