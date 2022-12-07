/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.repository.sample;

import org.springframework.data.gemfire.repository.GemfireRepository;
import org.springframework.data.gemfire.repository.Query;

/**
 * The PlantRepository class is a Repository extension for accessing and storing Plants.
 * Note, this Spring GemFire Repository extension incorrectly maps Plants to the Plants Region on purpose
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.repository.GemfireRepository
 * @see org.springframework.data.gemfire.repository.Query
 * @since 1.4.0
 */
@SuppressWarnings("unused")
public interface PlantRepository extends GemfireRepository<Plant, String> {

	Animal findByName(String name);

	@Query("SELECT * FROM /Placeholder x WHERE x.name = $1")
	Animal findBy(String name);

}
