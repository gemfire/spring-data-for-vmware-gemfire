/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.geode.cache.Cache;
import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.gemfire.repository.sample.User;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.CacheMockObjects;
import org.springframework.data.gemfire.tests.support.DataSourceAdapter;
import org.springframework.data.gemfire.util.PropertiesBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

/**
 * Integration Tests for {@link LazyWiringDeclarableSupport}.
 *
 * @author John Blum
 * @see java.util.Properties
 * @see javax.sql.DataSource
 * @see org.junit.Test
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.CacheMockObjects
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.3.4
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class LazyWiringDeclarableSupportIntegrationTests extends IntegrationTestsSupport {

	private static Properties createParameters(String parameter, String value) {
		return PropertiesBuilder.create().setProperty(parameter, value).build();
	}

	@AfterClass
	public static void tearDown() {
		SpringContextBootstrappingInitializer.destroy();
	}

	@Autowired
	private ApplicationContext applicationContext;

	private final Cache mockCache = CacheMockObjects
		.mockPeerCache("MockCache", null, null);

	@Test
	public void autoWiringSuccessful() {

		TestDeclarable declarable = new TestDeclarable();

		declarable.initialize(this.mockCache, createParameters("testParam", "testValue"));
		declarable.onApplicationEvent(new ContextRefreshedEvent(applicationContext));
		declarable.assertInitialized();

		Assertions.assertThat(declarable.getDataSource()).isNull();
		Assertions.assertThat(declarable.getUser()).isNotNull();
		Assertions.assertThat(declarable.getUser().getUsername()).isEqualTo("supertool");
	}

	@Test
	public void autoWiringWithBeanTemplateSuccessful() {

		TestDeclarable declarable = new TestDeclarable();

		declarable.initialize(this.mockCache, createParameters(WiringDeclarableSupport.TEMPLATE_BEAN_NAME_PROPERTY, "declarableTemplateBean"));
		declarable.onApplicationEvent(new ContextRefreshedEvent(applicationContext));
		declarable.assertInitialized();

		Assertions.assertThat(declarable.getDataSource()).isNotNull();
		Assertions.assertThat(declarable.getUser()).isNotNull();
		Assertions.assertThat(declarable.getUser().getUsername()).isEqualTo("supertool");
	}

	@Test(expected = IllegalArgumentException.class)
	public void autoWiringWithNonExistingBeanTemplateThrowsIllegalArgumentException() {

		try {

			TestDeclarable declarable = new TestDeclarable();

			declarable.initialize(this.mockCache, createParameters(WiringDeclarableSupport.TEMPLATE_BEAN_NAME_PROPERTY, "nonExistingBeanTemplate"));
			declarable.onApplicationEvent(new ContextRefreshedEvent(applicationContext));
		}
		catch (IllegalStateException expected) {

			Assertions.assertThat(expected).hasMessageStartingWith("Cannot find bean with name [nonExistingBeanTemplate]");
			Assertions.assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	protected static final class TestDataSource extends DataSourceAdapter { }

	protected static final class TestDeclarable extends LazyWiringDeclarableSupport {

		private DataSource dataSource;

		@Autowired
		private User user;

		public void setDataSource(DataSource dataSource) {
			this.dataSource = dataSource;
		}

		public DataSource getDataSource() {
			return this.dataSource;
		}

		User getUser() {
			Assert.state(this.user != null, "A reference to the User was not properly configured");
			return this.user;
		}
	}
}
