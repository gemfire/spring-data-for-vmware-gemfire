// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

/** Creates JSON documents in the storage format the factory was obtained for. */
public interface DriverJsonDocumentFactory {
	/** Parses the text. A malformed document fails with {@link DriverJsonParseException}, which never repeats the document in its message. */
	DriverJsonDocument create(String json);
}
