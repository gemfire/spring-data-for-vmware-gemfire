/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created UserService that uses UserRepository
 */

package com.example.testcaching.service;

import com.example.testcaching.model.User;
import com.example.testcaching.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for User operations.
 * Uses UserRepository for CRUD operations.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates a new user.
     */
    public User createUser(User user) {
        logger.info("Creating user: {}", user.getId());
        return userRepository.save(user);
    }

    /**
     * Creates a new user with the given details.
     */
    public User createUser(String id, String username, String email, String firstName, String lastName) {
        User user = new User(id, username, email, firstName, lastName);
        return createUser(user);
    }

    /**
     * Finds a user by ID.
     */
    public Optional<User> findById(String id) {
        logger.info("Finding user by ID: {}", id);
        return userRepository.findById(id);
    }

    /**
     * Finds all users.
     */
    public Iterable<User> findAll() {
        logger.info("Finding all users");
        return userRepository.findAll();
    }

    /**
     * Finds users by username.
     */
    public List<User> findByUsername(String username) {
        logger.info("Finding users by username: {}", username);
        return userRepository.findByUsername(username);
    }

    /**
     * Finds a user by email.
     */
    public User findByEmail(String email) {
        logger.info("Finding user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    /**
     * Finds users by last name.
     */
    public List<User> findByLastName(String lastName) {
        logger.info("Finding users by last name: {}", lastName);
        return userRepository.findByLastName(lastName);
    }

    /**
     * Finds users by first and last name.
     */
    public List<User> findByFullName(String firstName, String lastName) {
        logger.info("Finding users by full name: {} {}", firstName, lastName);
        return userRepository.findByFirstNameAndLastName(firstName, lastName);
    }

    /**
     * Updates an existing user.
     */
    public User updateUser(User user) {
        logger.info("Updating user: {}", user.getId());
        if (!userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("User not found: " + user.getId());
        }
        return userRepository.save(user);
    }

    /**
     * Deletes a user by ID.
     */
    public void deleteUser(String id) {
        logger.info("Deleting user: {}", id);
        userRepository.deleteById(id);
    }

    /**
     * Checks if a user exists.
     */
    public boolean userExists(String id) {
        return userRepository.existsById(id);
    }

    /**
     * Counts all users.
     */
    public long countUsers() {
        return userRepository.count();
    }
}
