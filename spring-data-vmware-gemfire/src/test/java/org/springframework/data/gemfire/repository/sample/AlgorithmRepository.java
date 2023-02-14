/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.repository.sample;

import org.springframework.data.gemfire.repository.GemfireRepository;

/**
 * The AlgorithmRepository class is a Data Access Object (DAO) for accessing and persistent data/state about Algorithms
 * to a GemFire Cache/Region.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.repository.GemfireRepository
 * @since 1.4.0
 */
@SuppressWarnings("unused")
public interface AlgorithmRepository extends GemfireRepository<Algorithm, String> {

	<T extends Algorithm> T findByName(String name);

}
