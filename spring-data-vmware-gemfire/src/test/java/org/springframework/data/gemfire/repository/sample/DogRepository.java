/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.repository.sample;

import org.springframework.data.gemfire.mapping.annotation.Region;
import org.springframework.data.gemfire.repository.GemfireRepository;
import org.springframework.data.gemfire.repository.Query;

/**
 * @author Stuart Williams
 * @author John Blum
 */
@Region("Dogs")
public interface DogRepository extends GemfireRepository<Animal, Long> {

	Animal findByName(String name);

	@Query("SELECT * FROM /Dogs x WHERE x.name = $1")
	Animal findBy(String name);

}
