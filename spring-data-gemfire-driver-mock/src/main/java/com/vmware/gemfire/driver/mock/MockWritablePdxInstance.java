/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import com.vmware.gemfire.driver.spi.*;
import java.util.Map;

final class MockWritablePdxInstance extends MockPdxInstance implements DriverWritablePdxInstance {
	MockWritablePdxInstance(String className, Map<String, MockPdxField> fields, boolean deserializable) { super(className, fields, deserializable, false); }
	@Override public void setField(String name, Object value) {
		if (!fields.containsKey(name)) throw new DriverOperationException(DriverFailureKind.SYSTEM_FAILURE, "Unknown PDX field", null);
		fields.put(name, fields.get(name).withValue(value));
	}
}
