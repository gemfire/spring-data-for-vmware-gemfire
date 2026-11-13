/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/**
 * A detached snapshot of query results. It is unmodifiable, so {@link #isModifiable()} is always false,
 * and elements are plain values or {@link DriverStruct} rows.
 */
public interface DriverSelectResults<E> extends java.util.Collection<E> {
	boolean isModifiable();
	int occurrences(E element);
	java.util.Set<E> asSet();
	java.util.List<E> asList();
	DriverCollectionType collectionType();
}
