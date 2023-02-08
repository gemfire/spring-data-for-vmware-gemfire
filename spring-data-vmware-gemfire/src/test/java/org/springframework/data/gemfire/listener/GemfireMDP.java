/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.listener;

import org.apache.geode.cache.Operation;
import org.apache.geode.cache.query.CqEvent;
import org.apache.geode.cache.query.CqQuery;

/**
 * Simple GemFire-message/event-driven-pojo.
 *
 * @author Costin Leau
 */
public class GemfireMDP {

	public void handleEvent(CqEvent event) { }

	public void handleQuery(CqQuery query) { }

	public void handleOperation(Operation op) { }

}
