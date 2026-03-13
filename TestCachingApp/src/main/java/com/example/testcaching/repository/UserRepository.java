/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created UserRepository for RegionProxy
 */

package com.example.testcaching.repository;

import com.example.testcaching.model.User;

import org.springframework.data.gemfire.repository.GemfireRepository;

import java.util.List;

/**
 * Repository interface for User entities stored in RegionProxy.
 */
public interface UserRepository extends GemfireRepository<User, String> {

    /**
     * Finds users by username.
     */
    List<User> findByUsername(String username);

    /**
     * Finds users by email.
     */
    User findByEmail(String email);

    /**
     * Finds users by last name.
     */
    List<User> findByLastName(String lastName);

    /**
     * Finds users by first name and last name.
     */
    List<User> findByFirstNameAndLastName(String firstName, String lastName);
}
