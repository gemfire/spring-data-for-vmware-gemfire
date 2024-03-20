/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.support.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * The TestUserService class is an implementation of the UserService service interface for performing service operations
 * on Users.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.repository.sample.User
 * @see org.springframework.data.gemfire.support.sample.UserService
 * @see org.springframework.stereotype.Service
 * @since 1.4.0
 */
@Service("userService")
@SuppressWarnings("unused")
public class TestUserService implements UserService {

	@Autowired
	private UserDao userDao;

	public UserDao getUserDao() {
		Assert.state(userDao != null, "A reference to the UserDao was not properly configured");
		return userDao;
	}
}
