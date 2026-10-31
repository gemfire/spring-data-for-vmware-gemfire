// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.mock;

import com.vmware.gemfire.driver.spi.*;
import java.util.function.BooleanSupplier;

/** Creates in-memory documents. Storage-format selection is a provider concern, so the mock only offers the default (PDX-like) form. */
final class MockJsonDocumentFactory implements DriverJsonDocumentFactory {
	private final BooleanSupplier closed;

	MockJsonDocumentFactory(BooleanSupplier closed) { this.closed = closed; }

	@Override public DriverJsonDocument create(String json) {
		if (closed.getAsBoolean()) throw new DriverOperationException(DriverFailureKind.INVALID_STATE, "Cache is closed", null);
		return new MockJsonDocument(MockJson.parseObject(json));
	}
}
