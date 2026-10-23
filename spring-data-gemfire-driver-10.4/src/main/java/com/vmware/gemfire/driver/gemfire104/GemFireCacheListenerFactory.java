/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import com.vmware.gemfire.driver.spi.*;

final class GemFireCacheListenerFactory {
	<K, V> GemFireCacheListener<K, V> adapt(DriverCacheListener<K, V> listener) {
		return new GemFireCacheListener<>(listener);
	}
}
