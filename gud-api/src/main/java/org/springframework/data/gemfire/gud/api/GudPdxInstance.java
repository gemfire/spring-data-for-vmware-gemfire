/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudPdxInstance interface as 1:1 mapping of GemFire PdxInstance
 */

package org.springframework.data.gemfire.gud.api;

import java.util.List;

/**
 * GUD API abstraction for GemFire PdxInstance interface.
 * Represents a PDX serialized object without deserializing it.
 */
public interface GudPdxInstance {

    String getClassName();

    boolean isDeserializable();

    Object getObject();

    boolean hasField(String fieldName);

    List<String> getFieldNames();

    boolean isIdentityField(String fieldName);

    Object getField(String fieldName);

    GudWritablePdxInstance createWriter();

    boolean isEnum();
}
