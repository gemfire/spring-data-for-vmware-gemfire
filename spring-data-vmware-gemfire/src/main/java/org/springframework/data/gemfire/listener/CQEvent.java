/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.listener;

/**
 * {@link CQEvent} is an enumeration abstracting GemFire's {@link org.apache.geode.cache.query.ExcludedEvent}
 * to be used by {@link org.springframework.data.gemfire.listener.annotation.ContinuousQuery}
 */
public enum CQEvent {
    UPDATE,
    CREATE,
    INVALIDATE,
    DESTROY
}
