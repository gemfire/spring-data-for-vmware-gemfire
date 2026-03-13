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
 * 2026-03-11: Created GudOperation enum as 1:1 mapping of GemFire Operation
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire Operation.
 * Defines the type of operation that triggered a cache event.
 */
public enum GudOperation {

    CREATE,
    LOCAL_CREATE,
    PUT_IF_ABSENT,
    UPDATE,
    LOCAL_UPDATE,
    REPLACE,
    INVALIDATE,
    LOCAL_INVALIDATE,
    DESTROY,
    LOCAL_DESTROY,
    REMOVE,
    EVICT_DESTROY,
    LOCAL_EVICT_DESTROY,
    EXPIRE_INVALIDATE,
    EXPIRE_LOCAL_INVALIDATE,
    EXPIRE_DESTROY,
    EXPIRE_LOCAL_DESTROY,
    REGION_CREATE,
    REGION_CLOSE,
    REGION_DESTROY,
    LOCAL_REGION_DESTROY,
    REGION_INVALIDATE,
    LOCAL_REGION_INVALIDATE,
    REGION_CLEAR,
    LOCAL_REGION_CLEAR,
    REGION_EXPIRE_INVALIDATE,
    REGION_EXPIRE_LOCAL_INVALIDATE,
    REGION_EXPIRE_DESTROY,
    REGION_EXPIRE_LOCAL_DESTROY,
    REGION_LOAD_CREATE,
    LOCAL_LOAD_CREATE,
    NET_LOAD_CREATE,
    REGION_LOAD_UPDATE,
    LOCAL_LOAD_UPDATE,
    NET_LOAD_UPDATE,
    SEARCH_CREATE,
    SEARCH_UPDATE,
    PUTALL_CREATE,
    PUTALL_UPDATE,
    REMOVEALL_DESTROY,
    GET_FOR_REGISTER_INTEREST,
    UPDATE_VERSION_STAMP,
    GET_ENTRY,
    GET,
    CONTAINS_KEY,
    CONTAINS_VALUE,
    CONTAINS_VALUE_FOR_KEY,
    FUNCTION_EXECUTION,
    MARKER,
    UNKNOWN;

    public boolean isCreate() {
        return name().contains("CREATE");
    }

    public boolean isUpdate() {
        return name().contains("UPDATE") && !name().contains("VERSION");
    }

    public boolean isDestroy() {
        return name().contains("DESTROY");
    }

    public boolean isInvalidate() {
        return name().contains("INVALIDATE");
    }

    public boolean isLocal() {
        return name().startsWith("LOCAL");
    }

    public boolean isDistributed() {
        return !isLocal();
    }

    public boolean isExpiration() {
        return name().contains("EXPIRE");
    }

    public boolean isEviction() {
        return name().contains("EVICT");
    }

    public boolean isLoad() {
        return name().contains("LOAD");
    }

    public boolean isRegion() {
        return name().startsWith("REGION") || name().contains("_REGION_");
    }

    public boolean isEntry() {
        return !isRegion() && !isMarker();
    }

    public boolean isMarker() {
        return this == MARKER;
    }

    public boolean isPutAll() {
        return name().startsWith("PUTALL");
    }

    public boolean isRemoveAll() {
        return name().startsWith("REMOVEALL");
    }

    public boolean isClear() {
        return name().contains("CLEAR");
    }

    public boolean isClose() {
        return name().contains("CLOSE");
    }
}
