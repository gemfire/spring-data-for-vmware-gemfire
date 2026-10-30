// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import com.vmware.gemfire.driver.spi.*;
import java.lang.reflect.Constructor;
import org.apache.geode.pdx.PdxReader;
import org.apache.geode.pdx.PdxSerializer;
import org.apache.geode.pdx.PdxWriter;

/** Native serializer installed on the client cache. It serves {@link DriverPdxSerializable} classes itself and
 * otherwise delegates to the configured {@link DriverPdxSerializer}; a decline passes through unchanged.
 */
final class GemFirePdxSerializerBridge implements PdxSerializer {
	private final DriverPdxSerializer delegate;
	GemFirePdxSerializerBridge(DriverPdxSerializer delegate) { this.delegate = delegate; }
	DriverPdxSerializer delegate() { return delegate; }
	@Override public boolean toData(Object object, PdxWriter out) {
		if (object instanceof DriverPdxSerializable serializable) { serializable.toData(new GemFirePdxWriter(out)); return true; }
		return delegate != null && delegate.toData(object, new GemFirePdxWriter(out));
	}
	@Override public Object fromData(Class<?> type, PdxReader in) {
		if (DriverPdxSerializable.class.isAssignableFrom(type)) {
			try {
				Constructor<?> constructor = type.getDeclaredConstructor();
				constructor.setAccessible(true);
				DriverPdxSerializable instance = (DriverPdxSerializable) constructor.newInstance();
				instance.fromData(new GemFirePdxReader(in));
				return instance;
			} catch (ReflectiveOperationException ex) {
				throw new IllegalStateException("Cannot instantiate " + type.getName() + " through its no-argument constructor", ex);
			}
		}
		return delegate == null ? null : delegate.fromData(type, new GemFirePdxReader(in));
	}
}
