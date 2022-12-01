/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire;

import java.io.Serializable;

import org.apache.geode.cache.EntryOperation;
import org.apache.geode.cache.PartitionResolver;

/**
 * @author Costin Leau
 */
@SuppressWarnings("rawtypes")
public class SimplePartitionResolver implements PartitionResolver {

	@Override
	public String getName() {
		return getClass().getName();
	}

	@Override
	public Serializable getRoutingObject(EntryOperation opDetails) {
		return getName();
	}

	@Override
	public void close() {
	}
}
