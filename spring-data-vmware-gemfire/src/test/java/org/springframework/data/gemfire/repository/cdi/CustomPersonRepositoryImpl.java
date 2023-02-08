/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.repository.cdi;

/**
 * The SamplePersonRepositoryImpl class is an implementation of the {@link CustomPersonRepository} extension interface
 * supporting additional data access (CRUD) operations on people.
 *
 * @author John Blum
 * @author Mark Paluch
 * @see CustomPersonRepository
 * @since 1.8.0
 */
public class CustomPersonRepositoryImpl implements CustomPersonRepository {

	public int returnOne() {
		return 1;
	}

}
