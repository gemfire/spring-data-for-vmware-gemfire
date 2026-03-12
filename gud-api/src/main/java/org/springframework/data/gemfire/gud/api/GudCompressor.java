/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudCompressor interface as 1:1 mapping of GemFire Compressor
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire Compressor interface.
 * Used to compress region entry values.
 */
public interface GudCompressor {

    byte[] compress(byte[] input);

    byte[] decompress(byte[] input);
}
