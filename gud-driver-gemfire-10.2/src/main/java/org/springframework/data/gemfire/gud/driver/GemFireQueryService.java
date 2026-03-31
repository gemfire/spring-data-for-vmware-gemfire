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
 * 2026-03-13: Created GemFire 10.2 QueryService adapter
 */

package org.springframework.data.gemfire.gud.driver;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.geode.cache.query.CqAttributes;
import org.apache.geode.cache.query.CqException;
import org.apache.geode.cache.query.CqExistsException;
import org.apache.geode.cache.query.CqQuery;
import org.apache.geode.cache.query.Index;
import org.apache.geode.cache.query.IndexExistsException;
import org.apache.geode.cache.query.IndexNameConflictException;
import org.apache.geode.cache.query.IndexType;
import org.apache.geode.cache.query.QueryService;
import org.apache.geode.cache.query.RegionNotFoundException;

import org.springframework.data.gemfire.gud.api.*;

/**
 * GUD API adapter for GemFire 10.2 QueryService.
 */
public class GemFireQueryService implements GudQueryService, NativeWrapper<QueryService> {

    private final QueryService nativeQueryService;

    public GemFireQueryService(QueryService nativeQueryService) {
        this.nativeQueryService = nativeQueryService;
    }

    @Override
    public QueryService getNative() {
        return nativeQueryService;
    }

    @Override
    public GudQuery newQuery(String queryString) {
        return new GemFireQuery(nativeQueryService.newQuery(queryString));
    }

    @Override
    public GudIndex createIndex(String indexName, String indexedExpression, String fromClause) {
        try {
            return new GemFireIndex(nativeQueryService.createIndex(indexName, indexedExpression, fromClause));
        } catch (IndexNameConflictException | IndexExistsException | RegionNotFoundException e) {
            throw new GudIndexInvalidException(e.getMessage(), e);
        }
    }

    @Override
    public GudIndex createIndex(String indexName, String indexedExpression, String fromClause, String imports) {
        try {
            return new GemFireIndex(nativeQueryService.createIndex(indexName, indexedExpression, fromClause, imports));
        } catch (IndexNameConflictException | IndexExistsException | RegionNotFoundException e) {
            throw new GudIndexInvalidException(e.getMessage(), e);
        }
    }

    @Override
    public GudIndex createKeyIndex(String indexName, String indexedExpression, String fromClause) {
        try {
            return new GemFireIndex(nativeQueryService.createKeyIndex(indexName, indexedExpression, fromClause));
        } catch (IndexNameConflictException | IndexExistsException | RegionNotFoundException e) {
            throw new GudIndexInvalidException(e.getMessage(), e);
        }
    }

    @Override
    public GudIndex createHashIndex(String indexName, String indexedExpression, String fromClause) {
        try {
            return new GemFireIndex(nativeQueryService.createHashIndex(indexName, indexedExpression, fromClause));
        } catch (IndexNameConflictException | IndexExistsException | RegionNotFoundException e) {
            throw new GudIndexInvalidException(e.getMessage(), e);
        }
    }

    @Override
    public GudIndex createHashIndex(String indexName, String indexedExpression, String fromClause, String imports) {
        try {
            return new GemFireIndex(nativeQueryService.createHashIndex(indexName, indexedExpression, fromClause, imports));
        } catch (IndexNameConflictException | IndexExistsException | RegionNotFoundException e) {
            throw new GudIndexInvalidException(e.getMessage(), e);
        }
    }

    @Override
    public void defineIndex(String indexName, String indexedExpression, String fromClause) {
        try {
            nativeQueryService.defineIndex(indexName, indexedExpression, fromClause);
        } catch (RegionNotFoundException e) {
            throw new GudIndexInvalidException(e.getMessage(), e);
        }
    }

    @Override
    public void defineIndex(String indexName, String indexedExpression, String fromClause, String imports) {
        try {
            nativeQueryService.defineIndex(indexName, indexedExpression, fromClause, imports);
        } catch (RegionNotFoundException e) {
            throw new GudIndexInvalidException(e.getMessage(), e);
        }
    }

    @Override
    public void defineKeyIndex(String indexName, String indexedExpression, String fromClause) {
        try {
            nativeQueryService.defineKeyIndex(indexName, indexedExpression, fromClause);
        } catch (RegionNotFoundException e) {
            throw new GudIndexInvalidException(e.getMessage(), e);
        }
    }

    @Override
    public void defineHashIndex(String indexName, String indexedExpression, String fromClause) {
        try {
            nativeQueryService.defineHashIndex(indexName, indexedExpression, fromClause);
        } catch (RegionNotFoundException e) {
            throw new GudIndexInvalidException(e.getMessage(), e);
        }
    }

    @Override
    public void defineHashIndex(String indexName, String indexedExpression, String fromClause, String imports) {
        try {
            nativeQueryService.defineHashIndex(indexName, indexedExpression, fromClause, imports);
        } catch (RegionNotFoundException e) {
            throw new GudIndexInvalidException(e.getMessage(), e);
        }
    }

    @Override
    public List<GudIndex> createDefinedIndexes() {
        try {
            return nativeQueryService.createDefinedIndexes().stream()
                .map(GemFireIndex::new)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new GudMultiIndexCreationException(e.getMessage(), e);
        }
    }

    @Override
    public GudIndex getIndex(GudRegion<?, ?> region, String indexName) {
        if (region instanceof NativeWrapper) {
            @SuppressWarnings("unchecked")
            org.apache.geode.cache.Region<?, ?> nativeRegion = ((NativeWrapper<org.apache.geode.cache.Region<?, ?>>) region).getNative();
            Index index = nativeQueryService.getIndex(nativeRegion, indexName);
            return index != null ? new GemFireIndex(index) : null;
        }
        throw new IllegalArgumentException("Region must be a native wrapper");
    }

    @Override
    public Collection<GudIndex> getIndexes() {
        return nativeQueryService.getIndexes().stream()
            .map(GemFireIndex::new)
            .collect(Collectors.toList());
    }

    @Override
    public Collection<GudIndex> getIndexes(GudRegion<?, ?> region) {
        if (region instanceof NativeWrapper) {
            @SuppressWarnings("unchecked")
            org.apache.geode.cache.Region<?, ?> nativeRegion = ((NativeWrapper<org.apache.geode.cache.Region<?, ?>>) region).getNative();
            return nativeQueryService.getIndexes(nativeRegion).stream()
                .map(GemFireIndex::new)
                .collect(Collectors.toList());
        }
        throw new IllegalArgumentException("Region must be a native wrapper");
    }

    @Override
    public Collection<GudIndex> getIndexes(GudRegion<?, ?> region, GudIndexType indexType) {
        if (region instanceof NativeWrapper) {
            @SuppressWarnings("unchecked")
            org.apache.geode.cache.Region<?, ?> nativeRegion = ((NativeWrapper<org.apache.geode.cache.Region<?, ?>>) region).getNative();
            IndexType nativeType = toNativeIndexType(indexType);
            return nativeQueryService.getIndexes(nativeRegion, nativeType).stream()
                .map(GemFireIndex::new)
                .collect(Collectors.toList());
        }
        throw new IllegalArgumentException("Region must be a native wrapper");
    }

    @Override
    public void removeIndex(GudIndex index) {
        if (index instanceof NativeWrapper) {
            @SuppressWarnings("unchecked")
            Index nativeIndex = ((NativeWrapper<Index>) index).getNative();
            nativeQueryService.removeIndex(nativeIndex);
        } else {
            throw new IllegalArgumentException("Index must be a native wrapper");
        }
    }

    @Override
    public void removeIndexes() {
        nativeQueryService.removeIndexes();
    }

    @Override
    public void removeIndexes(GudRegion<?, ?> region) {
        if (region instanceof NativeWrapper) {
            @SuppressWarnings("unchecked")
            org.apache.geode.cache.Region<?, ?> nativeRegion = ((NativeWrapper<org.apache.geode.cache.Region<?, ?>>) region).getNative();
            nativeQueryService.removeIndexes(nativeRegion);
        } else {
            throw new IllegalArgumentException("Region must be a native wrapper");
        }
    }

    @Override
    public void clearDefinedIndexes() {
        nativeQueryService.clearDefinedIndexes();
    }

    @Override
    public GudCqQuery newCq(String queryString, GudCqAttributes cqAttributes) {
        try {
            CqAttributes nativeAttrs = toNativeCqAttributes(cqAttributes);
            return new GemFireCqQuery(nativeQueryService.newCq(queryString, nativeAttrs));
        } catch (CqException e) {
            throw new GudCqException(e.getMessage(), e);
        }
    }

    @Override
    public GudCqQuery newCq(String queryString, GudCqAttributes cqAttributes, boolean isDurable) {
        try {
            CqAttributes nativeAttrs = toNativeCqAttributes(cqAttributes);
            return new GemFireCqQuery(nativeQueryService.newCq(queryString, nativeAttrs, isDurable));
        } catch (CqException e) {
            throw new GudCqException(e.getMessage(), e);
        }
    }

    @Override
    public GudCqQuery newCq(String name, String queryString, GudCqAttributes cqAttributes) {
        try {
            CqAttributes nativeAttrs = toNativeCqAttributes(cqAttributes);
            return new GemFireCqQuery(nativeQueryService.newCq(name, queryString, nativeAttrs));
        } catch (CqException | CqExistsException e) {
            throw new GudCqException(e.getMessage(), e);
        }
    }

    @Override
    public GudCqQuery newCq(String name, String queryString, GudCqAttributes cqAttributes, boolean isDurable) {
        try {
            CqAttributes nativeAttrs = toNativeCqAttributes(cqAttributes);
            return new GemFireCqQuery(nativeQueryService.newCq(name, queryString, nativeAttrs, isDurable));
        } catch (CqException | CqExistsException e) {
            throw new GudCqException(e.getMessage(), e);
        }
    }

    @Override
    public GudCqQuery getCq(String cqName) {
        CqQuery cq = nativeQueryService.getCq(cqName);
        return cq != null ? new GemFireCqQuery(cq) : null;
    }

    @Override
    public GudCqQuery[] getCqs() {
        CqQuery[] cqs = nativeQueryService.getCqs();
        if (cqs == null) return new GudCqQuery[0];
        GudCqQuery[] result = new GudCqQuery[cqs.length];
        for (int i = 0; i < cqs.length; i++) {
            result[i] = new GemFireCqQuery(cqs[i]);
        }
        return result;
    }

    @Override
    public GudCqQuery[] getCqs(String regionName) {
        try {
            CqQuery[] cqs = nativeQueryService.getCqs(regionName);
            if (cqs == null) return new GudCqQuery[0];
            GudCqQuery[] result = new GudCqQuery[cqs.length];
            for (int i = 0; i < cqs.length; i++) {
                result[i] = new GemFireCqQuery(cqs[i]);
            }
            return result;
        } catch (CqException e) {
            throw new GudCqException(e.getMessage(), e);
        }
    }

    @Override
    public void executeCqs() {
        try {
            nativeQueryService.executeCqs();
        } catch (CqException e) {
            throw new GudCqException(e.getMessage(), e);
        }
    }

    @Override
    public void executeCqs(String regionName) {
        try {
            nativeQueryService.executeCqs(regionName);
        } catch (CqException e) {
            throw new GudCqException(e.getMessage(), e);
        }
    }

    @Override
    public void stopCqs() {
        try {
            nativeQueryService.stopCqs();
        } catch (CqException e) {
            throw new GudCqException(e.getMessage(), e);
        }
    }

    @Override
    public void stopCqs(String regionName) {
        try {
            nativeQueryService.stopCqs(regionName);
        } catch (CqException e) {
            throw new GudCqException(e.getMessage(), e);
        }
    }

    @Override
    public void closeCqs() {
        nativeQueryService.closeCqs();
    }

    @Override
    public List<String> getAllDurableCqsFromServer() {
        try {
            return nativeQueryService.getAllDurableCqsFromServer();
        } catch (CqException e) {
            throw new GudCqException(e.getMessage(), e);
        }
    }

    @Override
    public GudCqServiceStatistics getCqStatistics() {
        return new GemFireCqServiceStatistics(nativeQueryService.getCqStatistics());
    }

    private IndexType toNativeIndexType(GudIndexType type) {
        switch (type) {
            case FUNCTIONAL: return IndexType.FUNCTIONAL;
            case PRIMARY_KEY: return IndexType.PRIMARY_KEY;
            case HASH: return IndexType.HASH;
            default: return IndexType.FUNCTIONAL;
        }
    }

    private CqAttributes toNativeCqAttributes(GudCqAttributes attrs) {
        if (attrs instanceof NativeWrapper) {
            @SuppressWarnings("unchecked")
            CqAttributes nativeAttrs = ((NativeWrapper<CqAttributes>) attrs).getNative();
            return nativeAttrs;
        }
        throw new IllegalArgumentException("CqAttributes must be created through the driver");
    }
}
