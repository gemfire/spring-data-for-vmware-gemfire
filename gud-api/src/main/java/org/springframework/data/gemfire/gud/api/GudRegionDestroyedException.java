/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudRegionDestroyedException as 1:1 mapping of GemFire RegionDestroyedException
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API exception indicating the region has been destroyed.
 */
public class GudRegionDestroyedException extends GudException {

    private final String regionFullPath;

    public GudRegionDestroyedException(String regionFullPath) {
        super("Region " + regionFullPath + " has been destroyed");
        this.regionFullPath = regionFullPath;
    }

    public GudRegionDestroyedException(String regionFullPath, String message) {
        super(message);
        this.regionFullPath = regionFullPath;
    }

    public GudRegionDestroyedException(String regionFullPath, String message, Throwable cause) {
        super(message, cause);
        this.regionFullPath = regionFullPath;
    }

    public String getRegionFullPath() {
        return regionFullPath;
    }
}
