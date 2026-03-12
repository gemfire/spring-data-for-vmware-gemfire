/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudCqAttributesMutator interface as 1:1 mapping of GemFire CqAttributesMutator
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire CqAttributesMutator interface.
 * Used to modify CQ attributes after CQ creation.
 */
public interface GudCqAttributesMutator {

    void addCqListener(GudCqListener cqListener);

    void removeCqListener(GudCqListener cqListener);

    void initCqListeners(GudCqListener[] cqListeners);
}
