/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.repository.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.mapping.Regions;
import org.springframework.data.gemfire.repository.sample.PersonRepository;
import org.springframework.data.gemfire.repository.support.AbstractGemfireRepositoryFactoryIntegrationTests;
import org.springframework.test.context.ContextConfiguration;

/**
 * Integration tests for namespace usage.
 *
 * @author Oliver Gierke
 */
@ContextConfiguration("partitioned-region-repo-context.xml")
public class PartitionedRegionNamespaceRepositoryIntegrationTests
		extends AbstractGemfireRepositoryFactoryIntegrationTests {

	@Autowired
	PersonRepository repository;

	@Override
	protected PersonRepository getRepository(Regions regions) {
		return repository;
	}
}
