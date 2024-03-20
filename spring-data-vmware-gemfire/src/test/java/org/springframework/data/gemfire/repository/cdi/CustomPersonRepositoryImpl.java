/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.repository.cdi;

/**
 * The SamplePersonRepositoryImpl class is an implementation of the {@link CustomPersonRepository} extension interface
 * supporting additional data access (CRUD) operations on people.
 *
 * @author John Blum
 * @author Mark Paluch
 * @see org.springframework.data.gemfire.repository.cdi.CustomPersonRepository
 * @since 1.8.0
 */
public class CustomPersonRepositoryImpl implements CustomPersonRepository {

	public int returnOne() {
		return 1;
	}

}
