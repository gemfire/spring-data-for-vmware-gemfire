/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.support.sample;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * The TestUserDao class is an implementation of the UserDao Data Access Object (DAO) interface for performing
 * data access and persistence operations on Users.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.repository.sample.User
 * @see UserDao
 * @see Repository
 * @since 1.4.0
 */
@Repository("userDao")
@SuppressWarnings("unused")
public class TestUserDao implements UserDao {

	@Autowired
	private DataSource userDataSource;

	public DataSource getDataSource() {
		Assert.state(userDataSource != null, "A reference to the Users DataSource was not properly configured");
		return userDataSource;
	}
}
