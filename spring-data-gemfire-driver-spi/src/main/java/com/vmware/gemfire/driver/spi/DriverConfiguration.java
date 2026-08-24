/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/**
 * Carries whatever a Driver Impl needs to establish a {@link DriverConnection}, which may
 * include credentials or other connection secrets. Implementations must never expose such
 * values via {@code toString()}, {@code equals()}/{@code hashCode()} diagnostics, or let them
 * appear in exception messages.
 */
public interface DriverConfiguration {
}
