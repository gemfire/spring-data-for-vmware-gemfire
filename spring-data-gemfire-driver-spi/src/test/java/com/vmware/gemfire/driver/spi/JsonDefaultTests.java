// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

import static org.assertj.core.api.Assertions.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import org.junit.jupiter.api.Test;

class JsonDefaultTests {
	private DriverRegionService service() {
		InvocationHandler handler = (proxy, method, args) -> InvocationHandler.invokeDefault(proxy, method, args);
		return (DriverRegionService) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { DriverRegionService.class }, handler);
	}

	@Test void providersWithoutAFormatterReportItAsNotIntroduced() {
		assertThatThrownBy(service()::getJsonFormatter).isInstanceOf(UnsupportedFeatureException.class)
			.hasMessageContaining("RegionService.getJsonFormatter");
	}

	@Test void providersWithoutADocumentFactoryReportItAsNotIntroduced() {
		assertThatThrownBy(() -> service().getJsonDocumentFactory()).isInstanceOf(UnsupportedFeatureException.class)
			.hasMessageContaining("RegionService.getJsonDocumentFactory");
		assertThatThrownBy(() -> service().getJsonDocumentFactory(DriverStorageFormat.BSON)).isInstanceOf(UnsupportedFeatureException.class)
			.hasMessageContaining("RegionService.getJsonDocumentFactory");
	}

	@Test void parseFailuresAreInvalidArgumentsAndKeepTheirCause() {
		var cause = new IllegalStateException("cause");
		var failure = new DriverJsonParseException("bad", cause);
		assertThat(failure.kind()).isEqualTo(DriverFailureKind.INVALID_ARGUMENT);
		assertThat(failure).hasCause(cause);
	}
}
