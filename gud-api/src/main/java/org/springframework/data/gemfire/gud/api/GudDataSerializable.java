/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudDataSerializable interface for GUD API
 */

package org.springframework.data.gemfire.gud.api;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

/**
 * GUD API abstraction for GemFire DataSerializable.
 */
public interface GudDataSerializable extends Serializable {

    void toData(DataOutput out) throws IOException;
    
    void fromData(DataInput in) throws IOException, ClassNotFoundException;
}
