/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.listener;

import org.springframework.data.gemfire.gud.api.GudOperation;
import org.springframework.data.gemfire.gud.api.GudCqEvent;
import org.springframework.data.gemfire.gud.api.GudCqQuery;

/**
 * Simple GemFire-message/event-driven-pojo.
 *
 * @author Costin Leau
 */
public class GemfireMDP {

	public void handleEvent(GudCqEvent event) { }

	public void handleQuery(GudCqQuery query) { }

	public void handleOperation(GudOperation op) { }

}
