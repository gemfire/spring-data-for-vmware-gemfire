// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import org.apache.geode.json.JsonDocumentFactory;
import org.apache.geode.json.JsonParseException;
import com.vmware.gemfire.driver.spi.DriverJsonDocument;
import com.vmware.gemfire.driver.spi.DriverJsonDocumentFactory;
import com.vmware.gemfire.driver.spi.DriverJsonParseException;

final class GemFireJsonDocumentFactory implements DriverJsonDocumentFactory {
	static final String PARSE_FAILURE = "JSON document could not be parsed";
	private final JsonDocumentFactory factory;
	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();
	GemFireJsonDocumentFactory(JsonDocumentFactory factory) { this.factory = factory; }
	@Override public DriverJsonDocument create(String json) {
		try { return new GemFireJsonDocument(factory.create(json)); }
		catch (JsonParseException ex) { throw new DriverJsonParseException(PARSE_FAILURE, ex); }
		catch (RuntimeException ex) { throw exceptions.translate("jsonDocumentFactory", ex); }
	}
}
