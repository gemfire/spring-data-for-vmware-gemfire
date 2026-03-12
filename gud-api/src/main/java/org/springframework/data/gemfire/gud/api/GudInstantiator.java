/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudInstantiator interface for GUD API
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire Instantiator.
 */
public abstract class GudInstantiator {

    private final Class<?> clazz;
    private final int id;
    
    public GudInstantiator(Class<?> clazz, int id) {
        this.clazz = clazz;
        this.id = id;
    }
    
    public abstract GudDataSerializable newInstance();
    
    public Class<?> getInstantiatedClass() {
        return clazz;
    }
    
    public int getId() {
        return id;
    }
}
