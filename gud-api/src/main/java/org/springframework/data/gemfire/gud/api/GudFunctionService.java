/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudFunctionService interface as 1:1 mapping of GemFire FunctionService
 * 2026-03-13: Changed to abstract class with static methods
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Map;
import java.util.Set;

/**
 * GUD API abstraction for GemFire FunctionService.
 * Entry point for function execution APIs.
 */
public abstract class GudFunctionService {

    public static GudExecution onRegion(GudRegion<?, ?> region) {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }

    public static GudExecution onServer(GudPool pool) {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }

    public static GudExecution onServer(GudRegionService regionService) {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }

    public static GudExecution onServers(GudPool pool) {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }

    public static GudExecution onServers(GudRegionService regionService) {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }

    public static GudExecution onMember(GudDistributedMember member) {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }

    public static GudExecution onMembers(Set<GudDistributedMember> members) {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }

    public static GudExecution onMembers(String... groups) {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }

    public static GudExecution onMember(String... groups) {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }

    public static GudFunction getFunction(String functionId) {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }

    public static void registerFunction(GudFunction function) {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }

    public static void unregisterFunction(String functionId) {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }

    public static boolean isRegistered(String functionId) {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }

    public static Map<String, GudFunction> getRegisteredFunctions() {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }
}
