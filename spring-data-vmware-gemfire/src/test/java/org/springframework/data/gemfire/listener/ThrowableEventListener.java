/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.listener;

import org.apache.geode.cache.query.CqEvent;


/**
 *
 * @author Costin Leau
 */
public class ThrowableEventListener implements ContinuousQueryListener {

	public void onEvent(CqEvent event) {
		throw new IllegalStateException("throwing exception for event " + event);
	}
}
