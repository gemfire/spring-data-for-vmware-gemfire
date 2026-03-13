/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudMembershipAttributes interface as 1:1 mapping of GemFire MembershipAttributes
 * 2026-03-13: Changed to class with constructor for instantiation
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * GUD API abstraction for GemFire MembershipAttributes.
 * Defines membership requirements for reliable regions.
 */
public class GudMembershipAttributes {

    private final Set<String> requiredRoles;
    private final GudLossAction lossAction;
    private final GudResumptionAction resumptionAction;

    public GudMembershipAttributes() {
        this.requiredRoles = new HashSet<>();
        this.lossAction = GudLossAction.NO_ACCESS;
        this.resumptionAction = GudResumptionAction.NONE;
    }

    public GudMembershipAttributes(String[] requiredRoles, GudLossAction lossAction, GudResumptionAction resumptionAction) {
        this.requiredRoles = requiredRoles != null ? new HashSet<>(Arrays.asList(requiredRoles)) : new HashSet<>();
        this.lossAction = lossAction;
        this.resumptionAction = resumptionAction;
    }

    public Set<String> getRequiredRoles() {
        return requiredRoles;
    }

    public GudLossAction getLossAction() {
        return lossAction;
    }

    public GudResumptionAction getResumptionAction() {
        return resumptionAction;
    }

    public boolean hasRequiredRoles() {
        return requiredRoles != null && !requiredRoles.isEmpty();
    }
}
