/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudCqAttributes interface as 1:1 mapping of GemFire CqAttributes
 * 2026-03-31: Added getCqListener() and getExcludedEvents() default methods
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Collections;
import java.util.Set;

/**
 * GUD API abstraction for GemFire CqAttributes interface.
 * Defines the attributes of a continuous query.
 */
public interface GudCqAttributes {

    GudCqListener[] getCqListeners();

    default GudCqListener getCqListener() {
        GudCqListener[] listeners = getCqListeners();
        return (listeners != null && listeners.length > 0) ? listeners[0] : null;
    }

    default Set<GudExcludedEvent> getExcludedEvents() {
        return Collections.emptySet();
    }
}
