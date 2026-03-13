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
 * 2026-03-11: Created GudRegionExistsException for GUD API
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire RegionExistsException.
 */
public class GudRegionExistsException extends GudCacheRuntimeException {

    private final GudRegion<?, ?> region;

    public GudRegionExistsException() {
        super();
        this.region = null;
    }

    public GudRegionExistsException(String message) {
        super(message);
        this.region = null;
    }

    public GudRegionExistsException(String message, Throwable cause) {
        super(message, cause);
        this.region = null;
    }

    public GudRegionExistsException(Throwable cause) {
        super(cause);
        this.region = null;
    }

    public GudRegionExistsException(GudRegion<?, ?> region) {
        super("Region already exists: " + (region != null ? region.getFullPath() : "null"));
        this.region = region;
    }

    public GudRegion<?, ?> getRegion() {
        return region;
    }
}
