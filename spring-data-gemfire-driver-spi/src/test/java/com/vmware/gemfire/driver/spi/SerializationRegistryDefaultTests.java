// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

import static org.assertj.core.api.Assertions.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import org.junit.jupiter.api.Test;

class SerializationRegistryDefaultTests {
	@Test void providersWithoutARegistryReportItAsNotIntroduced() {
		InvocationHandler handler = (proxy, method, args) -> InvocationHandler.invokeDefault(proxy, method, args);
		var connection = (DriverConnection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { DriverConnection.class }, handler);
		assertThatThrownBy(connection::getSerializationRegistry).isInstanceOf(UnsupportedFeatureException.class)
			.hasMessageContaining("Connection.getSerializationRegistry");
	}

	@Test void providersWithoutACodecReportItAsNotIntroduced() {
		InvocationHandler handler = (proxy, method, args) -> InvocationHandler.invokeDefault(proxy, method, args);
		var connection = (DriverConnection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { DriverConnection.class }, handler);
		assertThatThrownBy(connection::getDataCodec).isInstanceOf(UnsupportedFeatureException.class)
			.hasMessageContaining("Connection.getDataCodec");
	}

	@Test void providersWithoutPdxMetaDataRegistrationReportItAsNotIntroduced() {
		InvocationHandler handler = (proxy, method, args) -> InvocationHandler.invokeDefault(proxy, method, args);
		var connection = (DriverConnection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { DriverConnection.class }, handler);
		assertThatThrownBy(() -> connection.registerPdxMetaData(new Object())).isInstanceOf(UnsupportedFeatureException.class)
			.hasMessageContaining("Connection.registerPdxMetaData");
	}
}
