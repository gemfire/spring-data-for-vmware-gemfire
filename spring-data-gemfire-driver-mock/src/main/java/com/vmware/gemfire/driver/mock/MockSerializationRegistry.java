// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.mock;

import com.vmware.gemfire.driver.spi.*;
import java.util.*;

/** Per-connection serializer registry that models id and supported-class collisions. No wire format is modelled. */
final class MockSerializationRegistry implements DriverSerializationRegistry {
	private final Map<Integer, DriverDataSerializer> byId = new HashMap<>();
	private final Map<Class<?>, Integer> byClass = new HashMap<>();

	@Override public synchronized void registerSerializer(DriverDataSerializer serializer) {
		Objects.requireNonNull(serializer, "serializer");
		if (byId.containsKey(serializer.id())) throw invalid("Serializer id " + serializer.id() + " is already registered");
		for (Class<?> type : serializer.supportedClasses()) {
			if (byClass.containsKey(type)) throw invalid(type.getName() + " already has serializer " + byClass.get(type));
		}
		byId.put(serializer.id(), serializer);
		for (Class<?> type : serializer.supportedClasses()) byClass.put(type, serializer.id());
	}
	@Override public void registerInstantiator(DriverInstantiator instantiator, boolean distribute) {
		Objects.requireNonNull(instantiator, "instantiator");
		registerSerializer(new InstantiatorSerializer(instantiator));
	}
	@Override public synchronized Optional<DriverDataSerializer> findSerializer(int id) { return Optional.ofNullable(byId.get(id)); }
	@Override public synchronized void unregisterSerializer(int id) {
		DriverDataSerializer removed = byId.remove(id);
		if (removed != null) for (Class<?> type : removed.supportedClasses()) byClass.remove(type, id);
	}

	private static DriverOperationException invalid(String message) {
		return new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, message, null);
	}

	private record InstantiatorSerializer(DriverInstantiator instantiator) implements DriverDataSerializer {
		@Override public int id() { return instantiator.id(); }
		@Override public Class<?>[] supportedClasses() { return new Class<?>[] { instantiator.instantiatedClass() }; }
		@Override public boolean toData(Object object, java.io.DataOutput out) { throw UnsupportedFeatureException.notIntroduced("Mock.serializationWireFormat"); }
		@Override public Object fromData(java.io.DataInput in) { throw UnsupportedFeatureException.notIntroduced("Mock.serializationWireFormat"); }
	}
}
