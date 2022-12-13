/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.repository.cdi;

/**
 * The CustomPersonRepository interface is an Spring Data Repository extension type specifying additional, "custom"
 * data access operations on people.
 *
 * @author John Blum
 * @since 1.8.0
 */
public interface CustomPersonRepository {

	int returnOne();

}
