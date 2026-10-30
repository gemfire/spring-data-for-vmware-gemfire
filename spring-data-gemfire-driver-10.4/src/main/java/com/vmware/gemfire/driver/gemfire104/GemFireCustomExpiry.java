// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import com.vmware.gemfire.driver.spi.DriverCustomExpiry;
import org.apache.geode.cache.CustomExpiry;
import org.apache.geode.cache.Region;

/** Native callback delegating to the application's expiry; the application keeps ownership and closes it. */
final class GemFireCustomExpiry<K, V> implements CustomExpiry<K, V> {
	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();
	private final GemFireExpirationConversions conversions = new GemFireExpirationConversions();
	private final DriverCustomExpiry<K, V> delegate;
	GemFireCustomExpiry(DriverCustomExpiry<K, V> delegate) { this.delegate = delegate; }
	DriverCustomExpiry<K, V> delegate() { return delegate; }
	@Override public org.apache.geode.cache.ExpirationAttributes getExpiry(Region.Entry<K, V> entry) {
		var expiry = delegate.getExpiry(new GemFireRegionEntry<>(entry));
		return expiry == null ? null : conversions.toNative(expiry);
	}
	@Override public void close() {
		try { delegate.close(); }
		catch (RuntimeException failure) { throw exceptions.translate("CustomExpiry.close", failure); }
	}
}
