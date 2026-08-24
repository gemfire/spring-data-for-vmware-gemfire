/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/**
 * The runtime connection a bound {@link GemFireDriver} establishes via
 * {@link GemFireDriver#connect(DriverConfiguration)}. Shape is intentionally left to
 * each Driver Impl — the SPI adds no behavior beyond the type itself (§5.6, thin-delegate stance).
 */
public interface DriverConnection {
}
