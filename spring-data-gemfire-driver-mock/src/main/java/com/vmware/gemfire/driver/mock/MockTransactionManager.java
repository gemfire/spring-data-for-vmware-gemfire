/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import com.vmware.gemfire.driver.spi.DriverTransactionManager;

/** No staged region writes or transaction guarantees. Every operation uses its typed unsupported default. */
final class MockTransactionManager implements DriverTransactionManager { }
