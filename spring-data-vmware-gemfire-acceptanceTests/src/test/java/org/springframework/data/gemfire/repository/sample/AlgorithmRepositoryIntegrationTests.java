/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository.sample;

import org.apache.geode.cache.Region;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
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
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.repository.GemfireRepository
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
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

		Assertions.assertThat(algorithmRepo)
			.describedAs("A reference to the AlgorithmRepository was not properly configured")
			.isNotNull();

		Assertions.assertThat(algorithmsRegion)
			.describedAs("A reference to the 'Algorithms' GemFire Cache Region was not properly configured")
			.isNotNull();

		Assertions.assertThat(algorithmsRegion.getName()).isEqualTo("Algorithms");
		Assertions.assertThat(algorithmsRegion.getFullPath()).isEqualTo("/Algorithms");
		Assertions.assertThat(algorithmsRegion.isEmpty()).isTrue();

		algorithmRepo.save(new BinarySearch());
		algorithmRepo.save(new HeapSort());

		Assertions.assertThat(algorithmsRegion.isEmpty()).isFalse();
		Assertions.assertThat(algorithmsRegion.size()).isEqualTo(2);

		Assertions.assertThat(algorithmsRegion.get(BinarySearch.class.getSimpleName()) instanceof BinarySearch).isTrue();
		Assertions.assertThat(algorithmsRegion.get(HeapSort.class.getSimpleName()) instanceof HeapSort).isTrue();

		HeapSort heapSort = algorithmRepo.findByName(HeapSort.class.getSimpleName());

		Assertions.assertThat(heapSort).isNotNull();
		Assertions.assertThat(heapSort.getName()).isEqualTo(HeapSort.class.getSimpleName());

		BinarySearch binarySearch = algorithmRepo.findByName(BinarySearch.class.getSimpleName());

		Assertions.assertThat(binarySearch).isNotNull();
		Assertions.assertThat(binarySearch.getName()).isEqualTo(BinarySearch.class.getSimpleName());
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
