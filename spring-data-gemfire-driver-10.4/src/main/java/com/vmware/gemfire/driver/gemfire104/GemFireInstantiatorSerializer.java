// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import com.vmware.gemfire.driver.spi.DriverDataSerializable;
import com.vmware.gemfire.driver.spi.DriverDataSerializer;
import com.vmware.gemfire.driver.spi.DriverInstantiator;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/** Serializer-backed adapter for a {@link DriverInstantiator}. The native {@code Instantiator} serves only native
 * {@code DataSerializable} classes, so a driver-only class is framed as its instantiator id followed by its own data.
 * The result round-trips natively but is not wire-compatible with servers that register native instantiators.
 */
final class GemFireInstantiatorSerializer implements DriverDataSerializer {
	private final DriverInstantiator instantiator;
	GemFireInstantiatorSerializer(DriverInstantiator instantiator) { this.instantiator = instantiator; }
	@Override public int id() { return instantiator.id(); }
	@Override public Class<?>[] supportedClasses() { return new Class<?>[] { instantiator.instantiatedClass() }; }
	@Override public boolean toData(Object object, DataOutput out) throws IOException {
		if (!(object instanceof DriverDataSerializable serializable) || object.getClass() != instantiator.instantiatedClass()) return false;
		out.writeInt(instantiator.id());
		serializable.toData(out);
		return true;
	}
	@Override public Object fromData(DataInput in) throws IOException, ClassNotFoundException {
		int framed = in.readInt();
		if (framed != instantiator.id()) throw new IOException("Instantiator id " + framed + " does not match " + instantiator.id());
		DriverDataSerializable instance = instantiator.newInstance();
		instance.fromData(in);
		return instance;
	}
}
