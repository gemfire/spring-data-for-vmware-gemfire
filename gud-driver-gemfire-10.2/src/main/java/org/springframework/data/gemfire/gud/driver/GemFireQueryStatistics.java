/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

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
 * 2026-03-13: Created GemFire 10.2 QueryStatistics adapter
 */

package org.springframework.data.gemfire.gud.driver;

import org.apache.geode.cache.query.QueryStatistics;

import org.springframework.data.gemfire.gud.api.GudQueryStatistics;

/**
 * GUD API adapter for GemFire 10.2 QueryStatistics.
 */
public class GemFireQueryStatistics implements GudQueryStatistics, NativeWrapper<QueryStatistics> {

    private final QueryStatistics nativeStatistics;

    public GemFireQueryStatistics(QueryStatistics nativeStatistics) {
        this.nativeStatistics = nativeStatistics;
    }

    @Override
    public QueryStatistics getNative() {
        return nativeStatistics;
    }

    @Override
    public long getNumExecutions() {
        return nativeStatistics.getNumExecutions();
    }

    @Override
    public long getTotalExecutionTime() {
        return nativeStatistics.getTotalExecutionTime();
    }
}
