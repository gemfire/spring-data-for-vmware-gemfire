/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.repository.sample;

import org.springframework.data.gemfire.mapping.annotation.Region;
import org.springframework.data.gemfire.repository.GemfireRepository;
import org.springframework.data.gemfire.repository.Query;

/**
 * The RabbitRepository class is a Spring Data GemFire Repository extension for accessing and persistent Rabbits
 * from/to an underlying data store (GemFire).
 *
 * @author John Blum
 * @see Region
 * @see org.springframework.data.gemfire.repository.GemfireRepository
 * @see org.springframework.data.gemfire.repository.Query
 * @since 1.4.0
 */
@Region("Rabbits")
@SuppressWarnings("unused")
public interface RabbitRepository extends GemfireRepository<Animal, Long> {

	Animal findByName(String name);

	@Query("SELECT * FROM /Placeholder x WHERE x.name = $1")
	Animal findBy(String name);

}
