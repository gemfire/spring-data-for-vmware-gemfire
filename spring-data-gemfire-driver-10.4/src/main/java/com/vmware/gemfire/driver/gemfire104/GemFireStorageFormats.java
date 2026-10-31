// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import org.apache.geode.json.StorageFormat;
import com.vmware.gemfire.driver.spi.DriverStorageFormat;

final class GemFireStorageFormats {
	private GemFireStorageFormats() {}
	static StorageFormat toNative(DriverStorageFormat format) {
		if (format == null) throw new IllegalArgumentException("format must not be null");
		return switch (format) { case PDX -> StorageFormat.PDX; case BSON -> StorageFormat.BSON; };
	}
}
