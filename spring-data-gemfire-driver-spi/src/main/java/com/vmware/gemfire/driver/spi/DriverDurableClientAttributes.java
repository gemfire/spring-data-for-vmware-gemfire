/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Durable identity carried by member metadata. */
public record DriverDurableClientAttributes(String id, int timeout) { }
