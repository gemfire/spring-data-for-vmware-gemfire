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
 * 2026-03-11: Created GudQueryService interface as 1:1 mapping of GemFire QueryService
 * 2026-03-17: Removed hypothetical 10.4 features
 * 2026-03-17: Added @Deprecated annotations for deprecated hash index and IndexType methods
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * GUD API abstraction for GemFire QueryService interface.
 * Provides methods for creating and executing queries, and for managing indexes.
 */
public interface GudQueryService {

    // Query operations
    GudQuery newQuery(String queryString);

    // Index creation
    GudIndex createIndex(String indexName, String indexedExpression, String fromClause);
    GudIndex createIndex(String indexName, String indexedExpression, String fromClause, String imports);
    GudIndex createKeyIndex(String indexName, String indexedExpression, String fromClause);

    /**
     * Creates a hash index.
     *
     * @deprecated Hash indexes have been deprecated. Use {@link #createIndex(String, String, String)} instead.
     */
    @Deprecated
    GudIndex createHashIndex(String indexName, String indexedExpression, String fromClause);

    /**
     * Creates a hash index with imports.
     *
     * @deprecated Hash indexes have been deprecated. Use {@link #createIndex(String, String, String, String)} instead.
     */
    @Deprecated
    GudIndex createHashIndex(String indexName, String indexedExpression, String fromClause, String imports);

    // Index definition (deferred creation)
    void defineIndex(String indexName, String indexedExpression, String fromClause);
    void defineIndex(String indexName, String indexedExpression, String fromClause, String imports);
    void defineKeyIndex(String indexName, String indexedExpression, String fromClause);

    /**
     * Defines a hash index.
     *
     * @deprecated Hash indexes have been deprecated. Use {@link #defineIndex(String, String, String)} instead.
     */
    @Deprecated
    void defineHashIndex(String indexName, String indexedExpression, String fromClause);

    /**
     * Defines a hash index with imports.
     *
     * @deprecated Hash indexes have been deprecated. Use {@link #defineIndex(String, String, String, String)} instead.
     */
    @Deprecated
    void defineHashIndex(String indexName, String indexedExpression, String fromClause, String imports);

    List<GudIndex> createDefinedIndexes();

    // Index retrieval
    GudIndex getIndex(GudRegion<?, ?> region, String indexName);
    Collection<GudIndex> getIndexes();
    Collection<GudIndex> getIndexes(GudRegion<?, ?> region);

    /**
     * Gets indexes by type.
     *
     * @deprecated IndexType has been deprecated. Use {@link #getIndexes(GudRegion)} instead.
     */
    @Deprecated
    Collection<GudIndex> getIndexes(GudRegion<?, ?> region, GudIndexType indexType);

    // Index removal
    void removeIndex(GudIndex index);
    void removeIndexes();
    void removeIndexes(GudRegion<?, ?> region);
    void clearDefinedIndexes();

    // Continuous Query (CQ)
    GudCqQuery newCq(String queryString, GudCqAttributes cqAttributes);
    GudCqQuery newCq(String queryString, GudCqAttributes cqAttributes, boolean isDurable);
    GudCqQuery newCq(String name, String queryString, GudCqAttributes cqAttributes);
    GudCqQuery newCq(String name, String queryString, GudCqAttributes cqAttributes, boolean isDurable);

    GudCqQuery getCq(String cqName);
    GudCqQuery[] getCqs();
    GudCqQuery[] getCqs(String regionName);

    void executeCqs();
    void executeCqs(String regionName);
    void stopCqs();
    void stopCqs(String regionName);
    void closeCqs();

    // CQ service state
    List<String> getAllDurableCqsFromServer();
    GudCqServiceStatistics getCqStatistics();
}
