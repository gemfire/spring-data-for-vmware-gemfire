/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GemFire 10.3 Query adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire103;

import org.apache.geode.cache.query.Query;
import org.apache.geode.cache.query.QueryException;

import org.springframework.data.gemfire.gud.api.GudQuery;
import org.springframework.data.gemfire.gud.api.GudQueryException;
import org.springframework.data.gemfire.gud.api.GudQueryStatistics;

/**
 * GUD API adapter for GemFire 10.3 Query.
 */
public class GemFire103Query implements GudQuery, NativeWrapper<Query> {

    private final Query nativeQuery;

    public GemFire103Query(Query nativeQuery) {
        this.nativeQuery = nativeQuery;
    }

    @Override
    public Query getNative() {
        return nativeQuery;
    }

    @Override
    public String getQueryString() {
        return nativeQuery.getQueryString();
    }

    @Override
    public Object execute() throws GudQueryException {
        try {
            Object result = nativeQuery.execute();
            return wrapResult(result);
        } catch (QueryException e) {
            throw new GudQueryException(e.getMessage(), e);
        }
    }

    @Override
    public Object execute(Object[] params) throws GudQueryException {
        try {
            Object result = nativeQuery.execute(params);
            return wrapResult(result);
        } catch (QueryException e) {
            throw new GudQueryException(e.getMessage(), e);
        }
    }

    @Override
    public GudQueryStatistics getStatistics() {
        return new GemFire103QueryStatistics(nativeQuery.getStatistics());
    }

    @Override
    public void compile() {
        // GemFire queries are compiled on first execution
    }

    @Override
    public boolean isCompiled() {
        return nativeQuery.isCompiled();
    }

    private Object wrapResult(Object result) {
        if (result instanceof org.apache.geode.cache.query.SelectResults) {
            return new GemFire103SelectResults<>((org.apache.geode.cache.query.SelectResults<?>) result);
        }
        return result;
    }
}
