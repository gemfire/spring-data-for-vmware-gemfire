/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import com.vmware.gemfire.driver.spi.DriverWritablePdxInstance;
import org.apache.geode.pdx.WritablePdxInstance;

final class GemFireWritablePdxInstance extends GemFirePdxInstance implements DriverWritablePdxInstance {
	GemFireWritablePdxInstance(WritablePdxInstance value) { super(value); }
	@Override public void setField(String name, Object value) {
		invoke(() -> { ((WritablePdxInstance) this.value).setField(name, new GemFirePdxValues().toNative(value)); return null; });
	}
}
