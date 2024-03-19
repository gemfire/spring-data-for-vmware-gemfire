/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;

import org.springframework.context.ApplicationContext;
import org.springframework.data.gemfire.mapping.GemfireMappingContext;
import org.springframework.data.gemfire.repository.sample.PersonRepository;

/**
 * Unit Tests for {@link GemfireRepositoryFactoryBean}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.springframework.data.gemfire.repository.support.GemfireRepositoryFactoryBean
 * @since 1.6.3
 */
@SuppressWarnings("rawtypes")
public class GemfireRepositoryFactoryBeanUnitTests {

	private GemfireRepositoryFactoryBean repositoryFactoryBean;

	@Before
	public void setup() {
		repositoryFactoryBean = new GemfireRepositoryFactoryBean<>(PersonRepository.class);
	}

	@Test(expected = IllegalStateException.class)
	public void rejectsMappingContextNotSet() {

		try {
			repositoryFactoryBean.afterPropertiesSet();
		}
		catch (IllegalStateException expected) {

			assertThat(expected).hasMessage("GemfireMappingContext must not be null");

			throw expected;
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void initializesWithMappingContext() {

		RegionAttributes<?, ?> mockRegionAttributes = mock(RegionAttributes.class);

		doReturn(Long.class).when(mockRegionAttributes).getKeyConstraint();

		Region<?, ?> mockRegion = mock(Region.class);

		doReturn("simple").when(mockRegion).getName();
		doReturn(mockRegionAttributes).when(mockRegion).getAttributes();

		ApplicationContext mockApplicationContext = mock(ApplicationContext.class);

		doReturn(Collections.singletonMap("simple", mockRegion))
			.when(mockApplicationContext).getBeansOfType(Region.class);

		repositoryFactoryBean.setApplicationContext(mockApplicationContext);
		repositoryFactoryBean.setGemfireMappingContext(new GemfireMappingContext());
		repositoryFactoryBean.afterPropertiesSet();

		assertThat(repositoryFactoryBean.getObject()).isNotNull();
	}
}
