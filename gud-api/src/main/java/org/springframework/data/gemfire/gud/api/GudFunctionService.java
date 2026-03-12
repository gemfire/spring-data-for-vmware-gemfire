/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudFunctionService interface as 1:1 mapping of GemFire FunctionService
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Map;
import java.util.Set;

/**
 * GUD API abstraction for GemFire FunctionService.
 * Entry point for function execution APIs.
 */
public interface GudFunctionService {

    GudExecution<?, ?, ?> onRegion(GudRegion<?, ?> region);

    GudExecution<?, ?, ?> onServer(GudPool pool);

    GudExecution<?, ?, ?> onServer(GudRegionService regionService);

    GudExecution<?, ?, ?> onServers(GudPool pool);

    GudExecution<?, ?, ?> onServers(GudRegionService regionService);

    GudExecution<?, ?, ?> onMember(GudDistributedMember member);

    GudExecution<?, ?, ?> onMembers(Set<GudDistributedMember> members);

    GudExecution<?, ?, ?> onMembers(String... groups);

    GudExecution<?, ?, ?> onMember(String... groups);

    GudFunction getFunction(String functionId);

    void registerFunction(GudFunction function);

    void unregisterFunction(String functionId);

    boolean isRegistered(String functionId);

    Map<String, GudFunction> getRegisteredFunctions();
}
