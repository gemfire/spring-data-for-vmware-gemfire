/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.fixtures.security;

/** Native-free configuration shared by the authentication client and private server fixture. */
public final class SecurityFixtureCredentials {

    public static final String USERNAME = "testUser";
    public static final String PASSWORD = "t35t9@55w0rd!";
    public static final String MANAGER_CLASS_NAME =
        "org.springframework.data.gemfire.config.annotation.TestSecurityManager";

    private SecurityFixtureCredentials() {
    }
}
