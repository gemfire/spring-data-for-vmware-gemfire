/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudCqAttributesFactory interface as 1:1 mapping of GemFire CqAttributesFactory
 * 2026-03-12: Added setExcludedEvents method for CQ event filtering
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Set;

/**
 * GUD API abstraction for GemFire CqAttributesFactory.
 * Factory for creating CqAttributes.
 */
public interface GudCqAttributesFactory {

    GudCqAttributesFactory addCqListener(GudCqListener cqListener);

    GudCqAttributesFactory initCqListeners(GudCqListener[] cqListeners);

    GudCqAttributesFactory setExcludedEvents(Set<GudExcludedEvent> excludedEvents);

    GudCqAttributes create();
}
