/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudCqQuery interface as 1:1 mapping of GemFire CqQuery
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire CqQuery interface.
 * Represents a continuous query.
 */
public interface GudCqQuery {

    String getName();

    String getQueryString();

    GudQuery getQuery();

    GudCqAttributes getCqAttributes();

    GudCqAttributesMutator getCqAttributesMutator();

    GudCqStatistics getStatistics();

    GudCqState getState();

    boolean isDurable();

    void execute() throws GudCqException;

    GudCqResults<Object> executeWithInitialResults() throws GudCqException;

    void stop() throws GudCqException;

    boolean isClosed();

    boolean isRunning();

    boolean isStopped();

    void close() throws GudCqException;
}
