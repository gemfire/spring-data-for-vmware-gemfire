/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GemFire 10.1 Index adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire101;

import org.apache.geode.cache.query.Index;
import org.apache.geode.cache.query.IndexType;

import org.springframework.data.gemfire.gud.api.GudIndex;
import org.springframework.data.gemfire.gud.api.GudIndexStatistics;
import org.springframework.data.gemfire.gud.api.GudIndexType;
import org.springframework.data.gemfire.gud.api.GudRegion;

/**
 * GUD API adapter for GemFire 10.1 Index.
 */
public class GemFire101Index implements GudIndex, NativeWrapper<Index> {

    private final Index nativeIndex;

    public GemFire101Index(Index nativeIndex) {
        this.nativeIndex = nativeIndex;
    }

    @Override
    public Index getNative() {
        return nativeIndex;
    }

    @Override
    public String getName() {
        return nativeIndex.getName();
    }

    @Override
    public GudIndexType getType() {
        IndexType type = nativeIndex.getType();
        switch (type) {
            case FUNCTIONAL: return GudIndexType.FUNCTIONAL;
            case PRIMARY_KEY: return GudIndexType.PRIMARY_KEY;
            case HASH: return GudIndexType.HASH;
            default: return GudIndexType.FUNCTIONAL;
        }
    }

    @Override
    public String getIndexedExpression() {
        return nativeIndex.getIndexedExpression();
    }

    @Override
    public String getFromClause() {
        return nativeIndex.getFromClause();
    }

    @Override
    public String getProjectionAttributes() {
        return nativeIndex.getProjectionAttributes();
    }

    @Override
    public String getCanonicalizedIndexedExpression() {
        return nativeIndex.getCanonicalizedIndexedExpression();
    }

    @Override
    public String getCanonicalizedFromClause() {
        return nativeIndex.getCanonicalizedFromClause();
    }

    @Override
    public String getCanonicalizedProjectionAttributes() {
        return nativeIndex.getCanonicalizedProjectionAttributes();
    }

    @Override
    public GudRegion<?, ?> getRegion() {
        return new GemFire101Region<>(nativeIndex.getRegion());
    }

    @Override
    public GudIndexStatistics getStatistics() {
        return new GemFire101IndexStatistics(nativeIndex.getStatistics());
    }
}
