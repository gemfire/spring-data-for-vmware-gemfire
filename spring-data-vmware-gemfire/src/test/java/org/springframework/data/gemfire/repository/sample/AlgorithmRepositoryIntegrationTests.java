/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository.sample;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.Region;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the contract and functionality of GemFire's Repository extension when using a plain old
 * Java interface for defining the application domain object/entity type, rather than a Java class, that is the subject
 * of the persistence operations.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see Region
 * @see org.springframework.data.gemfire.repository.GemfireRepository
 * @see IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringJUnit4ClassRunner
 * @since 1.4.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class AlgorithmRepositoryIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private AlgorithmRepository algorithmRepo;

	@Autowired
	@Qualifier("Algorithms")
	private Region<?, ?> algorithmsRegion;

	@Test
	public void algorithmsRepositoryFunctionsCorrectly() {

		assertThat(algorithmRepo)
			.describedAs("A reference to the AlgorithmRepository was not properly configured")
			.isNotNull();

		assertThat(algorithmsRegion)
			.describedAs("A reference to the 'Algorithms' GemFire Cache Region was not properly configured")
			.isNotNull();

		assertThat(algorithmsRegion.getName()).isEqualTo("Algorithms");
		assertThat(algorithmsRegion.getFullPath()).isEqualTo("/Algorithms");
		assertThat(algorithmsRegion.isEmpty()).isTrue();

		algorithmRepo.save(new BinarySearch());
		algorithmRepo.save(new HeapSort());

		assertThat(algorithmsRegion.isEmpty()).isFalse();
		assertThat(algorithmsRegion.size()).isEqualTo(2);

		assertThat(algorithmsRegion.get(BinarySearch.class.getSimpleName()) instanceof BinarySearch).isTrue();
		assertThat(algorithmsRegion.get(HeapSort.class.getSimpleName()) instanceof HeapSort).isTrue();

		HeapSort heapSort = algorithmRepo.findByName(HeapSort.class.getSimpleName());

		assertThat(heapSort).isNotNull();
		assertThat(heapSort.getName()).isEqualTo(HeapSort.class.getSimpleName());

		BinarySearch binarySearch = algorithmRepo.findByName(BinarySearch.class.getSimpleName());

		assertThat(binarySearch).isNotNull();
		assertThat(binarySearch.getName()).isEqualTo(BinarySearch.class.getSimpleName());
	}

	protected static abstract class AbstractAlgorithm implements Algorithm {

		@Override
		public String getName() {
			return getClass().getSimpleName();
		}
	}

	protected static final class BinarySearch extends AbstractAlgorithm { }

	protected static final class HeapSort extends AbstractAlgorithm { }

}
