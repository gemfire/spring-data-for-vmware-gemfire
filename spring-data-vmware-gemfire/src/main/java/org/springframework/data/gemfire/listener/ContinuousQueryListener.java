/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.listener;

import org.springframework.data.gemfire.gud.api.GudCqEvent;

/**
 * Continuous Query (CQ) listener listening for events and notifications by a GemFire Continuous Query (CQ).
 *
 * @author Costin Leau
 * @author John Blum
 */
public interface ContinuousQueryListener {

	/**
	 * Action performed by the listener when notified of a CQ event.
	 *
	 * @param event the event from the CQ.
	 * @see GudCqEvent
	 */
	void onEvent(GudCqEvent event);

}
