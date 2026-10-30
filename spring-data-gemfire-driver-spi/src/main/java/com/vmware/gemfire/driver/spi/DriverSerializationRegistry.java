// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

import java.util.Optional;

/** Registration of application binary serializers and instantiators for one connection.
 * Registry state belongs to the provider, never to a static in this API.
 */
public interface DriverSerializationRegistry {
	/** Registers a serializer; a conflicting id or supported class fails with {@link DriverFailureKind#INVALID_ARGUMENT}. */
	void registerSerializer(DriverDataSerializer serializer);
	/** Registers an instantiator through a serializer-backed adapter that occupies the same id space as serializers.
	 * The wire form is therefore not compatible with servers that use native instantiators, and {@code distribute}
	 * does not promise strictly local registration.
	 */
	void registerInstantiator(DriverInstantiator instantiator, boolean distribute);
	/** @return the serializer registered under the id, if any. */
	Optional<DriverDataSerializer> findSerializer(int id);
	/** Removes the serializer; an absent id is a no-op and no atomic distributed unregister is promised. */
	void unregisterSerializer(int id);
}
