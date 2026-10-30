// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import com.vmware.gemfire.driver.spi.DriverDataSerializer;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.geode.DataSerializer;

/** Native serializer that carries a {@link DriverDataSerializer}, so the bytes are the native wire format. */
final class GemFireDataSerializerBridge extends DataSerializer {
	private final DriverDataSerializer delegate;
	GemFireDataSerializerBridge(DriverDataSerializer delegate) { this.delegate = delegate; }
	DriverDataSerializer delegate() { return delegate; }
	@Override public Class<?>[] getSupportedClasses() { return delegate.supportedClasses(); }
	@Override public boolean toData(Object object, DataOutput out) throws IOException { return delegate.toData(object, out); }
	@Override public Object fromData(DataInput in) throws IOException, ClassNotFoundException { return delegate.fromData(in); }
	@Override public int getId() { return delegate.id(); }
}
