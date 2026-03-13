/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GudAttributesFactoryImpl for GUD API
 */

package org.springframework.data.gemfire.gud.api;

/**
 * Abstract implementation of GudAttributesFactory that provides a factory method.
 * Drivers should provide their own implementation.
 */
public abstract class GudAttributesFactoryImpl<K, V> implements GudAttributesFactory<K, V> {

    @SuppressWarnings("unchecked")
    public static <K, V> GudAttributesFactory<K, V> create() {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }
}
