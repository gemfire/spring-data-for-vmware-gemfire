/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudSnappyCompressor class for GUD API
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire SnappyCompressor.
 */
public class GudSnappyCompressor implements GudCompressor {

    private static final GudSnappyCompressor INSTANCE = new GudSnappyCompressor();
    
    public static GudSnappyCompressor getDefaultInstance() {
        return INSTANCE;
    }

    @Override
    public byte[] compress(byte[] input) {
        throw new UnsupportedOperationException("Compression handled by driver");
    }

    @Override
    public byte[] decompress(byte[] input) {
        throw new UnsupportedOperationException("Decompression handled by driver");
    }
}
