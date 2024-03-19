/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import org.apache.geode.cache.Scope;

import org.springframework.util.StringUtils;

/**
 * The ScopeType enum is an enumeration of GemFire Scopes.
 *
 * @author John Blum
 * @see Scope
 * @since 1.6.0
 */
@SuppressWarnings("unused")
public enum ScopeType {

	DISTRIBUTED_ACK(Scope.DISTRIBUTED_ACK),
	DISTRIBUTED_NO_ACK(Scope.DISTRIBUTED_NO_ACK),
	GLOBAL(Scope.GLOBAL),
	LOCAL(Scope.LOCAL);

	private final Scope gemfireScope;

	/**
	 * Constructs an instance of the ScopeType initialized with a matching GemFire Scope.
	 *
	 * @param gemfireScope the GemFire Scope paired with this enumerated value.
	 * @see Scope
	 */
	ScopeType(Scope gemfireScope) {
		this.gemfireScope = gemfireScope;
	}

	/**
	 * Null-safe operation to extract the GemFire Scope from the given ScopeType enum value, or null if the provided
	 * scopeType is null.
	 *
	 * @param scopeType the ScopeType enumerated value from which to extract the GemFire Scope.
	 * @return the paired GemFire Scope from the given ScopeType or null if scopeType is null.
	 * @see Scope
	 * @see #getScope()
	 */
	public static Scope getScope(ScopeType scopeType) {
		return scopeType != null ? scopeType.getScope() : null;
	}

	/**
	 * Returns a ScopeType enumerated value for the given a GemFire Scope.
	 *
	 * @param scope the GemFire Scope used to lookup and match the appropriate ScopeType.
	 * @return a ScopeType for the given GemFire Scope or null if no match was found.
	 * @see Scope
	 * @see #getScope()
	 * @see #values()
	 */
	public static ScopeType valueOf(final Scope scope) {

		for (ScopeType scopeType : values()) {
			if (scopeType.getScope().equals(scope)) {
				return scopeType;
			}
		}

		return null;
	}

	/**
	 * Returns a ScopeType enumerated value given the case-insensitive name of the GemFire Scope.
	 *
	 * @param name a String name describing the ScopeType enum value.
	 * @return a ScopeType for the given case-insensitive, named GemFire Scope.
	 * @see String#equalsIgnoreCase(String)
	 * @see #values()
	 * @see #name()
	 * @see #transform(String)
	 */
	public static ScopeType valueOfIgnoreCase(String name) {

		name = transform(name);

		for (ScopeType scopeType : values()) {
			if (scopeType.name().equalsIgnoreCase(name)) {
				return scopeType;
			}
		}

		return null;
	}

	/**
	 * Null-safe operation that transforms a String name having hyphens and whitespace into a String with underscores
	 * and no whitespace.
	 *
	 * @param name the String to transform.
	 * @return a String value with underscores for hyphens and all leading/trailing whitespace trimmed, or null
	 * if the given String name is null.
	 */
	private static String transform(String name) {
		return StringUtils.hasText(name) ? name.trim().replaceAll("-", "_") : name;
	}

	/**
	 * Gets the matching GemFire Scope for this enumerated value.
	 *
	 * @return a GemFire Scope for this enumerated value.
	 * @see Scope
	 */
	public Scope getScope() {
		return this.gemfireScope;
	}
}
