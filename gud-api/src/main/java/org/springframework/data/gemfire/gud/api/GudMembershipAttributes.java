/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudMembershipAttributes interface as 1:1 mapping of GemFire MembershipAttributes
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Set;

/**
 * GUD API abstraction for GemFire MembershipAttributes interface.
 * Defines membership requirements for reliable regions.
 */
public interface GudMembershipAttributes {

    Set<String> getRequiredRoles();

    GudLossAction getLossAction();

    GudResumptionAction getResumptionAction();

    boolean hasRequiredRoles();
}
