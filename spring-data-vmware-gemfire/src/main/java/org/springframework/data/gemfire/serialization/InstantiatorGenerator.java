// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.serialization;

import org.apache.geode.DataSerializable;
import org.apache.geode.Instantiator;

/**
 * Factory that  generates {@link Instantiator} classes to improve instantiation of
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
	Instantiator getInstantiator(Class<? extends DataSerializable> clazz, int classId);
}
