/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-14: Created GudEnhancedSecurityManager interface for enhanced security (10.4+ feature)
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for enhanced security manager features.
 * Provides advanced security capabilities beyond the basic SecurityManager.
 * 
 * <p>This interface is available starting with GemFire 10.4.
 * Calling methods on this interface with older drivers will throw
 * {@link GudUnsupportedOperationException}.
 * 
 * @since GUD API 1.1 (GemFire 10.4+)
 */
public interface GudEnhancedSecurityManager extends GudSecurityManager {

    /**
     * Checks if a specific permission is granted for the current user.
     *
     * @param permission the permission to check
     * @return true if the permission is granted
     */
    boolean isPermissionGranted(GudResourcePermission permission);

    /**
     * Returns detailed authentication information for the current session.
     *
     * @return authentication details
     */
    Object getAuthenticationDetails();

    /**
     * Validates the current security context.
     *
     * @return true if the security context is valid
     */
    boolean validateSecurityContext();
}
