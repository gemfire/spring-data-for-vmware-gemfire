/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-14: Created GudUnsupportedOperationException for capability-aware error handling
 */

package org.springframework.data.gemfire.gud.api;

/**
 * Exception thrown when an operation is not supported by the current driver.
 * This is typically thrown when calling a method that requires a newer GemFire
 * version than the current driver supports.
 */
public class GudUnsupportedOperationException extends GudException {

    private final String requiredCapability;
    private final String minimumVersion;

    /**
     * Creates a new GudUnsupportedOperationException with a message.
     *
     * @param message the detail message
     */
    public GudUnsupportedOperationException(String message) {
        super(message);
        this.requiredCapability = null;
        this.minimumVersion = null;
    }

    /**
     * Creates a new GudUnsupportedOperationException with capability information.
     *
     * @param message the detail message
     * @param requiredCapability the capability that is required but not supported
     * @param minimumVersion the minimum GemFire version required
     */
    public GudUnsupportedOperationException(String message, String requiredCapability, String minimumVersion) {
        super(message);
        this.requiredCapability = requiredCapability;
        this.minimumVersion = minimumVersion;
    }

    /**
     * Creates a new GudUnsupportedOperationException with a cause.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public GudUnsupportedOperationException(String message, Throwable cause) {
        super(message, cause);
        this.requiredCapability = null;
        this.minimumVersion = null;
    }

    /**
     * Gets the capability that is required but not supported.
     *
     * @return the required capability name, or null if not specified
     */
    public String getRequiredCapability() {
        return requiredCapability;
    }

    /**
     * Gets the minimum GemFire version required for this operation.
     *
     * @return the minimum version string, or null if not specified
     */
    public String getMinimumVersion() {
        return minimumVersion;
    }
}
