/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.cache.Region;

import org.springframework.data.gemfire.GemfireOperations;
import org.springframework.data.gemfire.GemfireTemplate;

/**
 * Unit tests for {@link GemfireDaoSupport}.
 *
 * @author Costin Leau
 * @author John Blum
 * @see org.junit.Rule
 * @see org.junit.Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.mockito.junit.MockitoJUnitRunner
 */
@RunWith(MockitoJUnitRunner.class)
public class GemfireDaoSupportUnitTests {

	@Mock
	public Region<?, ?> mockRegion;

	@Test
	public void setAndGetGemfireTemplate() {
		GemfireTemplate expectedGemfireTemplate = new GemfireTemplate(mockRegion);
		GemfireDaoSupport dao = new TestGemfireDaoSupport();

		assertThat(dao.getGemfireTemplate()).isNull();

		dao.setGemfireTemplate(expectedGemfireTemplate);

		assertThat(dao.getGemfireTemplate()).isSameAs(expectedGemfireTemplate);

		dao.setGemfireTemplate(null);

		assertThat(dao.getGemfireTemplate()).isNull();
	}

	@Test
	public void setRegion() {
		GemfireDaoSupport dao = new TestGemfireDaoSupport();

		dao.setRegion(mockRegion);

		GemfireOperations gemfireTemplate = dao.getGemfireTemplate();

		assertThat(gemfireTemplate).isNotNull();
		assertThat(gemfireTemplate).isInstanceOf(GemfireTemplate.class);
		assertThat(((GemfireTemplate) gemfireTemplate).getRegion()).isSameAs(mockRegion);
	}

	@Test
	public void createProperlyInitializedGemfireDaoSupportWithTemplate() {

		GemfireTemplate expectedGemfireTemplate = new GemfireTemplate();

		GemfireDaoSupport dao = new TestGemfireDaoSupport();

		dao.setGemfireTemplate(expectedGemfireTemplate);
		dao.afterPropertiesSet();

		assertThat(dao.getGemfireTemplate()).isNotNull();
		assertThat(dao.getGemfireTemplate()).isEqualTo(expectedGemfireTemplate);
	}

	@Test(expected = IllegalStateException.class)
	public void invalidGemfireDaoSupportInstanceThrowsIllegalStateException() {

		try {
			new TestGemfireDaoSupport().afterPropertiesSet();
		}
		catch (IllegalStateException expected) {

			assertThat(expected).hasMessage("A GemFire Cache Region or instance of GemfireTemplate is required");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	private static final class TestGemfireDaoSupport extends GemfireDaoSupport { }

}
