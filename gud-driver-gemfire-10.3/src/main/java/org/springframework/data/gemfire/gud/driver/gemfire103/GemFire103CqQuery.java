/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GemFire 10.3 CqQuery adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire103;

import org.apache.geode.cache.query.CqException;
import org.apache.geode.cache.query.CqQuery;
import org.apache.geode.cache.query.CqResults;
import org.apache.geode.cache.query.RegionNotFoundException;

import org.springframework.data.gemfire.gud.api.*;

/**
 * GUD API adapter for GemFire 10.3 CqQuery.
 */
public class GemFire103CqQuery implements GudCqQuery, NativeWrapper<CqQuery> {

    private final CqQuery nativeCq;

    public GemFire103CqQuery(CqQuery nativeCq) {
        this.nativeCq = nativeCq;
    }

    @Override
    public CqQuery getNative() {
        return nativeCq;
    }

    @Override
    public String getName() {
        return nativeCq.getName();
    }

    @Override
    public String getQueryString() {
        return nativeCq.getQueryString();
    }

    @Override
    public GudQuery getQuery() {
        return new GemFire103Query(nativeCq.getQuery());
    }

    @Override
    public GudCqAttributes getCqAttributes() {
        return new GemFire103CqAttributes(nativeCq.getCqAttributes());
    }

    @Override
    public GudCqAttributesMutator getCqAttributesMutator() {
        return new GemFire103CqAttributesMutator(nativeCq.getCqAttributesMutator());
    }

    @Override
    public GudCqStatistics getStatistics() {
        return new GemFire103CqStatistics(nativeCq.getStatistics());
    }

    @Override
    public GudCqState getState() {
        org.apache.geode.cache.query.CqState state = nativeCq.getState();
        if (state.isClosed()) return GudCqState.CLOSED;
        if (state.isRunning()) return GudCqState.RUNNING;
        if (state.isStopped()) return GudCqState.STOPPED;
        return GudCqState.STOPPED;
    }

    @Override
    public boolean isDurable() {
        return nativeCq.isDurable();
    }

    @Override
    public void execute() throws GudCqException {
        try {
            nativeCq.execute();
        } catch (CqException | RegionNotFoundException e) {
            throw new GudCqException(e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudCqResults<Object> executeWithInitialResults() throws GudCqException {
        try {
            CqResults<Object> results = nativeCq.executeWithInitialResults();
            return new GemFire103CqResults<>(results);
        } catch (CqException | RegionNotFoundException e) {
            throw new GudCqException(e.getMessage(), e);
        }
    }

    @Override
    public void stop() throws GudCqException {
        try {
            nativeCq.stop();
        } catch (CqException e) {
            throw new GudCqException(e.getMessage(), e);
        }
    }

    @Override
    public boolean isClosed() {
        return nativeCq.isClosed();
    }

    @Override
    public boolean isRunning() {
        return nativeCq.isRunning();
    }

    @Override
    public boolean isStopped() {
        return nativeCq.isStopped();
    }

    @Override
    public void close() throws GudCqException {
        try {
            nativeCq.close();
        } catch (CqException e) {
            throw new GudCqException(e.getMessage(), e);
        }
    }
}
