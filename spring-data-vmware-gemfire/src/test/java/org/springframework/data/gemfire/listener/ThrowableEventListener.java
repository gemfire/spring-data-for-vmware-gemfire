/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.listener;

import org.springframework.data.gemfire.gud.api.GudCqEvent;


/**
 *
 * @author Costin Leau
 */
public class ThrowableEventListener implements ContinuousQueryListener {

	public void onEvent(GudCqEvent event) {
		throw new IllegalStateException("throwing exception for event " + event);
	}
}
