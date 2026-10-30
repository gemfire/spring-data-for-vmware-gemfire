// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.mock;

import com.vmware.gemfire.driver.spi.*;
import java.util.*;

import com.vmware.gemfire.driver.spi.config.PdxConfiguration;

/** Local serialize/deserialize round trip through the configured PDX callbacks. It encodes no PDX bytes. */
final class MockPdxCodec {
	private final PdxConfiguration configuration;
	MockPdxCodec(PdxConfiguration configuration) { this.configuration = configuration; }
	MockPdxInstance serialize(Object value) {
		MockPdxWriter writer = new MockPdxWriter(value.getClass().getName());
		boolean handled = value instanceof DriverPdxSerializable;
		try {
			if (handled) ((DriverPdxSerializable) value).toData(writer);
			else handled = configuration.serializer().map(s -> s.toData(value, writer)).orElse(false);
		} catch (GemFireDriverException ex) { throw ex; }
		catch (RuntimeException ex) { throw new DriverOperationException(DriverFailureKind.SYSTEM_FAILURE, "PDX toData failed for " + value.getClass().getName(), ex); }
		if (!handled) throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, value.getClass().getName() + " is not PDX serializable", null);
		return writer.create();
	}
	Object deserialize(Class<?> type, MockPdxInstance instance) {
		MockPdxReader reader = new MockPdxReader(instance, configuration.ignoreUnreadFields());
		Object result;
		try {
			if (DriverPdxSerializable.class.isAssignableFrom(type)) {
				var constructor = type.getDeclaredConstructor();
				constructor.setAccessible(true);
				DriverPdxSerializable value = (DriverPdxSerializable) constructor.newInstance();
				value.fromData(reader);
				result = value;
			} else result = configuration.serializer().map(s -> s.fromData(type, reader)).orElse(null);
		} catch (GemFireDriverException ex) { throw ex; }
		catch (ReflectiveOperationException | RuntimeException ex) { throw new DriverOperationException(DriverFailureKind.SYSTEM_FAILURE, "PDX fromData failed for " + type.getName(), ex); }
		if (result == null) throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, type.getName() + " could not be deserialized by any PDX serializer", null);
		return result;
	}
}
