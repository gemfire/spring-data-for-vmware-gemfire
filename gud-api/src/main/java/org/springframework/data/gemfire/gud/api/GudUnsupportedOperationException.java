/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-14: Created GudUnsupportedOperationException for capability-aware error handling
 * 2026-04-02: Changed requiredCapability field from String to GudCapability enum for type safety
 */

package org.springframework.data.gemfire.gud.api;

/**
 * Exception thrown when an operation is not supported by the current driver.
 * This is typically thrown by the {@code default} method implementations in GUD API interfaces
 * when calling a method that requires a newer GemFire version than the current driver supports.
 *
 * <p>Callers may inspect {@link #getRequiredCapability()} and {@link #getMinimumVersion()} to
 * produce actionable diagnostic messages or to branch on capability.
 */
public class GudUnsupportedOperationException extends GudException {

    private final GudCapability requiredCapability;
    private final String minimumVersion;

    /**
     * Creates a new exception with a message only.
     *
     * @param message the detail message
     */
    public GudUnsupportedOperationException(String message) {
        super(message);
        this.requiredCapability = null;
        this.minimumVersion = null;
    }

    /**
     * Creates a new exception with capability information, for use in interface default methods.
     *
     * @param message the detail message
     * @param requiredCapability the {@link GudCapability} that is required but not available
     * @param minimumVersion the minimum GemFire version that provides the capability
     */
    public GudUnsupportedOperationException(String message, GudCapability requiredCapability, String minimumVersion) {
        super(message);
        this.requiredCapability = requiredCapability;
        this.minimumVersion = minimumVersion;
    }

    /**
     * Creates a new exception with a cause.
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
     * Gets the capability that is required but not supported by the current driver.
     *
     * @return the required capability, or {@code null} if not specified
     */
    public GudCapability getRequiredCapability() {
        return requiredCapability;
    }

    /**
     * Gets the minimum GemFire version required for this operation.
     *
     * @return the minimum version string (e.g. {@code "10.1"}), or {@code null} if not specified
     */
    public String getMinimumVersion() {
        return minimumVersion;
    }
}
