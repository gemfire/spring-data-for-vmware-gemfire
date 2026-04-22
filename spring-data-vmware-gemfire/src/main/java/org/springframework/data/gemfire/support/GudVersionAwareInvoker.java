/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-02: Created GudVersionAwareInvoker utility to standardise graceful handling of
 *             version-specific GUD API features across Spring FactoryBeans
 */

package org.springframework.data.gemfire.support;

import org.slf4j.Logger;

import org.springframework.data.gemfire.gud.api.GudCapability;
import org.springframework.data.gemfire.gud.api.GudUnsupportedOperationException;

/**
 * Utility class that centralises the graceful-degradation pattern for version-specific GUD API
 * features.  When a feature is not supported by the current driver a warning is logged and the
 * invocation is silently skipped, preventing the entire Spring context from failing.
 *
 * <p>Usage example:
 * <pre>{@code
 * GudVersionAwareInvoker.invokeIfSupported(
 *     () -> poolFactory.setMinConnectionsPerServer(minConnections),
 *     GudCapability.PER_SERVER_CONNECTION_LIMITS,
 *     logger,
 *     "Per-server connection limits are not supported by this GemFire version; skipping."
 * );
 * }</pre>
 */
public final class GudVersionAwareInvoker {

    private GudVersionAwareInvoker() {
    }

    /**
     * Invokes the given action.  If the action throws a {@link GudUnsupportedOperationException}
     * the exception is caught, a warning is logged using the supplied message, and execution
     * continues normally.
     *
     * @param action          the action that invokes the version-specific GUD API method
     * @param capability      the capability required by the action (used in the warning)
     * @param logger          the logger to write the warning to
     * @param warningTemplate a SLF4J-style message template; use {@code {}} placeholders
     * @param args            arguments for the warning template
     */
    public static void invokeIfSupported(
            Runnable action,
            GudCapability capability,
            Logger logger,
            String warningTemplate,
            Object... args) {
        try {
            action.run();
        } catch (GudUnsupportedOperationException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn(warningTemplate + " (requires capability: {}; minimum version: {})",
                    appendArgs(args, capability, ex.getMinimumVersion()));
            }
        }
    }

    /**
     * Invokes the given action.  If the action throws a {@link GudUnsupportedOperationException}
     * the exception is caught, a warning is logged, and execution continues normally.
     * This overload uses the exception message directly.
     *
     * @param action     the action that invokes the version-specific GUD API method
     * @param capability the capability required by the action (used in the warning)
     * @param logger     the logger to write the warning to
     */
    public static void invokeIfSupported(Runnable action, GudCapability capability, Logger logger) {
        try {
            action.run();
        } catch (GudUnsupportedOperationException ex) {
            logger.warn("Feature {} requires GemFire {}+; skipping. Details: {}",
                capability, ex.getMinimumVersion(), ex.getMessage());
        }
    }

    private static Object[] appendArgs(Object[] original, Object... extras) {
        Object[] result = new Object[original.length + extras.length];
        System.arraycopy(original, 0, result, 0, original.length);
        System.arraycopy(extras, 0, result, original.length, extras.length);
        return result;
    }
}
