/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudDataSerializerRegistry interface for DataSerializer management
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API interface for managing DataSerializer registration.
 * Provides abstraction over GemFire's InternalDataSerializer functionality.
 */
public interface GudDataSerializerRegistry {

    /**
     * Gets a registered serializer by its ID.
     *
     * @param id the serializer ID
     * @return the serializer instance, or null if not found
     */
    Object getSerializer(int id);

    /**
     * Unregisters a serializer by its ID.
     *
     * @param id the serializer ID to unregister
     */
    void unregister(int id);

    /**
     * Registers a DataSerializer class.
     *
     * @param serializerClass the DataSerializer class to register
     */
    void register(Class<?> serializerClass);
}
