/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Index kinds. {@code HASH} mirrors a deprecated native kind and is kept only for existing consumers. */
public enum DriverIndexType {
	FUNCTIONAL("RANGE"), HASH("HASH"), PRIMARY_KEY("KEY");

	private final String synonym;

	DriverIndexType(String synonym) { this.synonym = synonym; }

	/** The short provider name, for example {@code RANGE} for {@link #FUNCTIONAL}. */
	public String synonym() { return synonym; }

	/** Resolves an enum name or its synonym; unknown names are rejected. */
	public static DriverIndexType valueOfSynonym(String name) {
		java.util.Objects.requireNonNull(name, "name");
		for (DriverIndexType type : values()) {
			if (type.synonym.equals(name) || type.name().equals(name)) return type;
		}
		throw new IllegalArgumentException("Unknown index type");
	}
}
