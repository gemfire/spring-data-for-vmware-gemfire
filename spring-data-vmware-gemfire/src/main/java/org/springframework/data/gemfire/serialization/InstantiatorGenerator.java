/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.serialization;

import org.springframework.data.gemfire.gud.api.GudDataSerializable;
import org.springframework.data.gemfire.gud.api.GudInstantiator;

/**
 * Factory that  generates {@link GudInstantiator} classes to improve instantiation of
 * custom types.
 *
 * @author Costin Leau
 */
public interface InstantiatorGenerator {

	/**
	 * Returns a (potentially new) Instantiator that optimizes the instantiation of the given types.
	 *
	 * @param clazz class produced by the instantiator
	 * @param classId instantiator class id
	 * @return an instantiator optimized for the given type.
	 */
	GudInstantiator getInstantiator(Class<? extends GudDataSerializable> clazz, int classId);
}
