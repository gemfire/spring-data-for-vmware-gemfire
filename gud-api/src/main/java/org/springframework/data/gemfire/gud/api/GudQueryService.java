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
 * 2026-03-14: Added default methods for API evolution support (10.4+ features)
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
    GudIndex createHashIndex(String indexName, String indexedExpression, String fromClause);
    GudIndex createHashIndex(String indexName, String indexedExpression, String fromClause, String imports);

    // Index definition (deferred creation)
    void defineIndex(String indexName, String indexedExpression, String fromClause);
    void defineIndex(String indexName, String indexedExpression, String fromClause, String imports);
    void defineKeyIndex(String indexName, String indexedExpression, String fromClause);
    void defineHashIndex(String indexName, String indexedExpression, String fromClause);
    void defineHashIndex(String indexName, String indexedExpression, String fromClause, String imports);
    List<GudIndex> createDefinedIndexes();

    // Index retrieval
    GudIndex getIndex(GudRegion<?, ?> region, String indexName);
    Collection<GudIndex> getIndexes();
    Collection<GudIndex> getIndexes(GudRegion<?, ?> region);
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

    // ===== API Evolution: 10.4+ Features =====
    // These default methods throw UnsupportedOperationException when called on older drivers.

    /**
     * Creates a spatial index for geospatial queries.
     * This feature requires GemFire 10.4 or later.
     *
     * @param indexName the name of the index
     * @param indexedExpression the expression to index
     * @param fromClause the from clause specifying the region
     * @return the created index
     * @throws GudUnsupportedOperationException if the driver doesn't support this feature
     * @since GUD API 1.1 (GemFire 10.4+)
     */
    default GudIndex createSpatialIndex(String indexName, String indexedExpression, String fromClause) {
        throw new GudUnsupportedOperationException(
            "createSpatialIndex() requires a GemFire 10.4+ driver. " +
            "Check driver.supportsCapability(GudCapability.NEW_INDEX_TYPES) before calling.",
            "NEW_INDEX_TYPES", "10.4");
    }

    /**
     * Defines a spatial index for deferred creation.
     * This feature requires GemFire 10.4 or later.
     *
     * @param indexName the name of the index
     * @param indexedExpression the expression to index
     * @param fromClause the from clause specifying the region
     * @throws GudUnsupportedOperationException if the driver doesn't support this feature
     * @since GUD API 1.1 (GemFire 10.4+)
     */
    default void defineSpatialIndex(String indexName, String indexedExpression, String fromClause) {
        throw new GudUnsupportedOperationException(
            "defineSpatialIndex() requires a GemFire 10.4+ driver. " +
            "Check driver.supportsCapability(GudCapability.NEW_INDEX_TYPES) before calling.",
            "NEW_INDEX_TYPES", "10.4");
    }
}
