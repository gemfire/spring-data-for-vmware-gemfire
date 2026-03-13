/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created Function Execution interface for TestFunction on RegionProxy
 */

package com.example.testcaching.function;

import com.example.testcaching.model.User;

import org.springframework.data.gemfire.function.annotation.FunctionId;
import org.springframework.data.gemfire.function.annotation.OnRegion;

/**
 * Function Execution interface that executes functions on RegionProxy.
 * 
 * The @OnRegion annotation binds this interface to the "regionProxy" bean,
 * and methods annotated with @FunctionId will execute the corresponding
 * server-side function.
 */
@OnRegion(region = "regionProxy")
public interface UserFunctionExecution {

    /**
     * Executes the "TestFunction" on the RegionProxy region.
     * 
     * @param userId the user ID to look up
     * @return the User object returned by the function
     */
    @FunctionId("TestFunction")
    User executeTestFunction(String userId);

    /**
     * Executes the "TestFunction" with multiple arguments.
     * 
     * @param userId the user ID
     * @param includeDetails whether to include additional details
     * @return the User object returned by the function
     */
    @FunctionId("TestFunction")
    User executeTestFunctionWithDetails(String userId, boolean includeDetails);

    /**
     * Executes a function to find users by criteria.
     * 
     * @param criteria the search criteria
     * @return list of matching users
     */
    @FunctionId("FindUsersByCriteria")
    Iterable<User> findUsersByCriteria(String criteria);
}
