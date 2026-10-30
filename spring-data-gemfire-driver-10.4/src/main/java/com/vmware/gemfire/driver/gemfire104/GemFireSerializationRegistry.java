// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import com.vmware.gemfire.driver.spi.*;
import java.util.Optional;
import org.apache.geode.internal.InternalDataSerializer;

/** Registers driver serializers with the native registry. Native holds the registry state; nothing is kept here.
 * Registration is never distributed, because a bridge class name could not be resolved on a server.
 */
final class GemFireSerializationRegistry implements DriverSerializationRegistry {
	private final GemFireExceptionMapper exceptions;
	GemFireSerializationRegistry(GemFireExceptionMapper exceptions) { this.exceptions = exceptions; }

	@Override public void registerSerializer(DriverDataSerializer serializer) {
		// Native _register accepts a second serializer of the same class under an existing id, and bridges are all one class.
		// The check and the registration are not atomic across threads; native owns the state, so none is added here.
		invoke("registerSerializer", () -> {
			if (InternalDataSerializer.getSerializer(serializer.id()) != null) {
				throw new IllegalArgumentException("Serializer id " + serializer.id() + " is already registered");
			}
			InternalDataSerializer._register(new GemFireDataSerializerBridge(serializer), false);
			return null;
		});
	}
	@Override public void registerInstantiator(DriverInstantiator instantiator, boolean distribute) {
		registerSerializer(new GemFireInstantiatorSerializer(instantiator));
	}
	@Override public Optional<DriverDataSerializer> findSerializer(int id) {
		return invoke("findSerializer", () -> Optional.ofNullable(InternalDataSerializer.getSerializer(id))
			.filter(GemFireDataSerializerBridge.class::isInstance).map(s -> ((GemFireDataSerializerBridge) s).delegate()));
	}
	@Override public void unregisterSerializer(int id) {
		invoke("unregisterSerializer", () -> { InternalDataSerializer.unregister(id); return null; });
	}

	private <T> T invoke(String operation, java.util.function.Supplier<T> action) {
		try { return action.get(); }
		catch (RuntimeException ex) { throw exceptions.translate(operation, ex); }
	}
}
