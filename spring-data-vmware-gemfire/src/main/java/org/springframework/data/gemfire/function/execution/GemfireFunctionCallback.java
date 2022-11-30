// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.function.execution;

import org.apache.geode.cache.execute.Execution;

/**
 * A callback for Gemfire Function Templates
 * @author David Turanski
 *
 */
public interface GemfireFunctionCallback<T> {
	public T doInGemfire( Execution execution );
}
