/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.listener;

import org.apache.geode.cache.query.CqEvent;

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
	 * @see org.apache.geode.cache.query.CqEvent
	 */
	void onEvent(CqEvent event);

}
