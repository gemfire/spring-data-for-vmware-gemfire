/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.annotation.support;

import java.util.Objects;

/**
 * Abstract Data Type (ADT) and {@link FunctionalInterface} defining a contract to model the details of
 * a security {@literal authentication} request.
 *
 * This ADT is loosely modeled after Spring Security's {@code Authentication} interface.
 *
 * @author John Blum
 * @param <PRINCIPAL> {@link Class type} modeling the {@literal principal} object.
 * @param <CREDENTIALS> {@link Class type} modeling the {@literal credentials} object.
 * @see FunctionalInterface
 * @since 3.0.0
 */
@FunctionalInterface
@SuppressWarnings("unused")
public interface Authentication<PRINCIPAL, CREDENTIALS> {

	/**
	 * Determines whether the {@literal principal} has been successfully authenticated.
	 *
	 * @return a boolean value indicating whether the {@literal principal} has been successfully authenticated.
	 */
	default boolean isAuthenticated() {
		return false;
	}

	/**
	 * Determines whether {@literal authentication} was actually requested.
	 *
	 * The default implementation determines whether {@literal authentication} was requested by
	 * the presence a {@link PRINCIPAL} and {@link CREDENTIALS}. However, even if {@literal authentication}
	 * was requested, it does not necessarily mean the {@link PRINCIPAL} successfully authenticated.
	 *
	 * @return a boolean valuing indicating whether {@literal authentication} was actually requested.
	 * @see #getCredentials()
	 * @see #getPrincipal()
	 */
	default boolean isRequested() {
		return Objects.nonNull(getPrincipal()) && Objects.nonNull(getCredentials());
	}

	/**
	 * Returns an {@link Object} identifying the {@literal principal} being authenticated.
	 *
	 * @return an {@link Object} identifying the {@literal principal} being authenticated.
	 */
	PRINCIPAL getPrincipal();

	/**
	 * Returns an {@link Object} with credentials that prove the {@literal principal} is correct
	 * and is who they say they are.
	 *
	 * @return Returns an {@link Object} with credentials that prove the {@literal principal} is correct.
	 */
	default CREDENTIALS getCredentials() {
		return null;
	}
}
