/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import com.vmware.gemfire.driver.spi.*;

final class GemFireCacheListenerFactory {
	/** The package-private native wrapper is exposed by RegionAttributes until membership initialization.
	 * This exact-version adaptation stays entirely inside the driver; it never exposes a native handle.
	 */
	Object applicationAdapter(Object listener) {
		if (listener != null && listener.getClass().getName().equals("org.apache.geode.internal.cache.WrappedRegionMembershipListener")) {
			try {
				var accessor = listener.getClass().getMethod("getWrappedListener");
				accessor.setAccessible(true);
				return accessor.invoke(listener);
			} catch (ReflectiveOperationException failure) {
				throw new GemFireExceptionMapper().translate("Listener.identity", failure);
			}
		}
		return listener;
	}

	<K, V> GemFireCacheListener<K, V> adapt(DriverCacheListener<K, V> listener) {
		if (listener instanceof DriverRegionMembershipListener && listener instanceof DriverRegionRoleListener) return new GemFireCacheListener.Both<>(listener);
		if (listener instanceof DriverRegionMembershipListener) return new GemFireCacheListener.Membership<>(listener);
		if (listener instanceof DriverRegionRoleListener) return new GemFireCacheListener.Roles<>(listener);
		return new GemFireCacheListener<>(listener);
	}
}
