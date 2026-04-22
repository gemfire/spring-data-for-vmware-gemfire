/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudObjectSizer interface as 1:1 mapping of GemFire ObjectSizer
 * 2026-04-01: Added DEFAULT constant using serialization-based sizing
 */

package org.springframework.data.gemfire.gud.api;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**
 * GUD API abstraction for GemFire ObjectSizer interface.
 * Used to determine memory size of objects for eviction.
 */
public interface GudObjectSizer {

    GudObjectSizer DEFAULT = o -> {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(o);
            oos.flush();
            return baos.size();
        } catch (Exception e) {
            return 0;
        }
    };

    int sizeof(Object o);
}
