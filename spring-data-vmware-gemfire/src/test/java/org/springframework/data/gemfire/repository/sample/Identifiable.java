/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository.sample;

/**
 * Defines a contract for Abstract Data Types (ADT) that can be identified.
 *
 * @author John Blum
 * @since 2.4.0
 */
public interface Identifiable<T> {

	T getId();

}
