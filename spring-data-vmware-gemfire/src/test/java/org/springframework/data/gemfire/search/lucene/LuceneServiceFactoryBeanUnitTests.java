/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.search.lucene;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.lucene.LuceneService;

/**
 * Unit Tests for {@link LuceneServiceFactoryBean}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.mockito.Spy
 * @see org.mockito.junit.MockitoJUnitRunner
 * @see org.springframework.data.gemfire.search.lucene.LuceneServiceFactoryBean
 * @since 1.1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class LuceneServiceFactoryBeanUnitTests {

	@Mock
	private GemFireCache mockCache;

	@Mock
	private LuceneService mockLuceneService;

	private LuceneServiceFactoryBean factoryBean;

	@Before
	public void setup() {
		factoryBean = spy(new LuceneServiceFactoryBean());
		doReturn(mockLuceneService).when(factoryBean).resolveLuceneService(eq(mockCache));
	}

	@Test
	public void setAndGetCache() {

		assertThat(factoryBean.getCache()).isNull();

		factoryBean.setCache(mockCache);

		assertThat(factoryBean.getCache()).isSameAs(mockCache);

		factoryBean.setCache(null);

		assertThat(factoryBean.getCache()).isNull();
	}

	@Test
	public void afterPropertiesSetInitializesLuceneService() throws Exception {

		assertThat(factoryBean.getObject()).isNull();

		factoryBean.setCache(mockCache);
		factoryBean.afterPropertiesSet();

		assertThat(factoryBean.getObject()).isEqualTo(mockLuceneService);

		verify(factoryBean, times(1)).resolveLuceneService(eq(mockCache));
	}

	@Test(expected = IllegalStateException.class)
	public void afterPropertiesSetThrowsIllegalStateExceptionWhenGemFireCacheIsNull() throws Exception {

		assertThat(factoryBean.getCache()).isNull();

		try {
			factoryBean.afterPropertiesSet();
		}
		catch (IllegalStateException expected) {

			assertThat(expected).hasMessage("A reference to the GemFireCache was not properly configured");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void getObjectTypeBeforeInitialization() throws Exception {

		assertThat(factoryBean.getObject()).isNull();
		assertThat(factoryBean.getObjectType()).isEqualTo(LuceneService.class);
	}

	@Test
	public void getObjectTypeAfterInitialization() throws Exception {

		factoryBean.setCache(mockCache);
		factoryBean.afterPropertiesSet();

		assertThat(factoryBean.getObject()).isSameAs(mockLuceneService);
		assertThat(factoryBean.getObjectType()).isEqualTo(mockLuceneService.getClass());
	}

	@Test
	public void isSingletonReturnsTrue() {
		assertThat(factoryBean.isSingleton()).isTrue();
	}
}
