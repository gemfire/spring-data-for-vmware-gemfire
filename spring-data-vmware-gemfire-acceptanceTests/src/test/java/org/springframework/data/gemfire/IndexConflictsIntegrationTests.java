/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.query.Index;
import org.apache.geode.cache.query.IndexExistsException;
import org.apache.geode.cache.query.IndexNameConflictException;
import org.apache.geode.cache.query.QueryService;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.util.CollectionUtils;

/**
 * Integration Tests for numerous conflicting {@link Index} configurations.
 *
 * An {@link IndexExistsException} is thrown when 2 or more {@link Index Indexes} share the same definition
 * but have different names.
 *
 * An {@link IndexNameConflictException} is thrown when 2 or more {@link Index Indexes} share the same name
 * but have potentially different definitions.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.query.Index
 * @see org.springframework.context.ConfigurableApplicationContext
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.context.annotation.Import
 * @see org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport
 * @see <a href="https://jira.spring.io/browse/SGF-432">IndexFactoryBean traps IndexExistsException instead of IndexNameConflictException</a>
 * @see <a href="https://jira.spring.io/browse/SGF-637">Improve IndexFactoryBean's resilience and options for handling GemFire IndexExistsExceptions and IndexNameConflictExceptions</a>
 * @since 1.6.3
 */
public class IndexConflictsIntegrationTests extends SpringApplicationContextIntegrationTestsSupport {

	private static final AtomicBoolean IGNORE = new AtomicBoolean(false);
	private static final AtomicBoolean OVERRIDE = new AtomicBoolean(false);

	private void assertIndex(Index index, String expectedName, String expectedExpression, String expectedFromClause,
			IndexType expectedType) {

		Assertions.assertThat(index).isNotNull();
		Assertions.assertThat(index.getName()).isEqualTo(expectedName);
		Assertions.assertThat(index.getIndexedExpression()).isEqualTo(expectedExpression);
		Assertions.assertThat(index.getFromClause()).isEqualTo(expectedFromClause);
		Assertions.assertThat(IndexType.valueOf(index.getType())).isEqualTo(expectedType);
	}

	private void assertIndexCount(int count) {
		Assertions.assertThat(getIndexCount()).isEqualTo(count);
	}

	private Index getIndex(String indexName) {

		for (Index index : CollectionUtils.nullSafeCollection(getQueryService().getIndexes())) {
			if (index.getName().equalsIgnoreCase(indexName)) {
				return index;
			}
		}

		return null;
	}

	private int getIndexCount() {
		return CollectionUtils.nullSafeCollection(getQueryService().getIndexes()).size();
	}

	private QueryService getQueryService() {
		return getBean("gemfireCache", GemFireCache.class).getQueryService();
	}

	private boolean hasIndex(String indexName) {
		return getIndex(indexName) != null;
	}

	@Before
	public void setup() {

		Assertions.assertThat(IGNORE.get()).isFalse();
		Assertions.assertThat(OVERRIDE.get()).isFalse();
	}

	@After
	public void tearDown() {

		OVERRIDE.set(false);
		IGNORE.set(false);
	}

	@Test
	public void indexDefinitionConflictIgnoresIndex() {

		Assertions.assertThat(IGNORE.compareAndSet(false, true)).isTrue();

		newApplicationContext(IndexDefinitionConflictConfiguration.class);

		Assertions.assertThat(requireApplicationContext().containsBean("customerIdIndex")).isTrue();
		Assertions.assertThat(requireApplicationContext().containsBean("customerIdentifierIndex")).isTrue();
		assertIndexCount(1);
		Assertions.assertThat(hasIndex("customerIdIndex")).isTrue();
		Assertions.assertThat(hasIndex("customerIdentifierIndex")).isFalse();

		Index customersIdIndex = getIndex("customerIdIndex");

		assertIndex(customersIdIndex, "customerIdIndex",
			"id", "/Customers", IndexType.PRIMARY_KEY);
	}

	@Test
	public void indexDefinitionConflictOverridesIndex() {

		Assertions.assertThat(OVERRIDE.compareAndSet(false, true)).isTrue();

		newApplicationContext(IndexDefinitionConflictConfiguration.class);

		Assertions.assertThat(containsBean("customerIdIndex")).isTrue();
		Assertions.assertThat(containsBean("customerIdentifierIndex")).isTrue();
		assertIndexCount(1);
		Assertions.assertThat(hasIndex("customerIdIndex")).isFalse();
		Assertions.assertThat(hasIndex("customerIdentifierIndex")).isTrue();

		Index customersIdentifierIndex = getIndex("customerIdentifierIndex");

		assertIndex(customersIdentifierIndex, "customerIdentifierIndex",
			"id", "/Customers", IndexType.PRIMARY_KEY);
	}

	@Test(expected = IndexExistsException.class)
	public void indexDefinitionConflictThrowsIndexExistsException() throws Throwable {

		try {
			newApplicationContext(IndexDefinitionConflictConfiguration.class);
		}
		catch (BeanCreationException expected) {

			Assertions.assertThat(expected).hasMessageStartingWith("Error creating bean with name 'customerIdentifierIndex'");

			Assertions.assertThat(expected).hasCauseInstanceOf(GemfireIndexException.class);

			String existingIndexDefinition = String.format(IndexFactoryBean.BASIC_INDEX_DEFINITION,
				"id", "/Customers", IndexType.PRIMARY_KEY);

			Assertions.assertThat(expected.getCause()).hasMessageStartingWith(String.format(
				"An Index with a different name [customerIdIndex] having the same definition [%s] already exists",
					existingIndexDefinition));

			Assertions.assertThat(expected.getCause()).hasCauseInstanceOf(IndexExistsException.class);

			Assertions.assertThat(expected.getCause().getCause()).hasMessage("Similar Index Exists");

			Assertions.assertThat(expected.getCause().getCause()).hasNoCause();

			throw expected.getCause().getCause();
		}
	}

	@Test
	public void indexNameConflictIgnoresIndex() {

		Assertions.assertThat(IGNORE.compareAndSet(false, true)).isTrue();

		newApplicationContext(IndexNameConflictConfiguration.class);

		Assertions.assertThat(containsBean("customerLastNameIndex")).isTrue();
		Assertions.assertThat(containsBean("customerFirstNameIndex")).isTrue();
		assertIndexCount(1);
		Assertions.assertThat(hasIndex(IndexNameConflictConfiguration.INDEX_NAME)).isTrue();

		Index customerLastNameIndex = getIndex(IndexNameConflictConfiguration.INDEX_NAME);

		assertIndex(customerLastNameIndex, IndexNameConflictConfiguration.INDEX_NAME,
			"lastName", "/Customers", IndexType.HASH);
	}

	@Test
	public void indexNameConflictOverridesIndex() {

		Assertions.assertThat(OVERRIDE.compareAndSet(false, true)).isTrue();

		newApplicationContext(IndexNameConflictConfiguration.class);

		Assertions.assertThat(getBeansOfType(Index.class)).hasSize(2);
		Assertions.assertThat(containsBean("customerLastNameIndex")).isTrue();
		Assertions.assertThat(containsBean("customerFirstNameIndex")).isTrue();
		assertIndexCount(1);
		Assertions.assertThat(hasIndex(IndexNameConflictConfiguration.INDEX_NAME)).isTrue();

		Index customerFirstNameIndex = getIndex(IndexNameConflictConfiguration.INDEX_NAME);

		assertIndex(customerFirstNameIndex, IndexNameConflictConfiguration.INDEX_NAME,
			"firstName", "/Customers", IndexType.FUNCTIONAL);
	}

	@Test(expected = IndexNameConflictException.class)
	public void indexNameConflictThrowsIndexNameConflictException() throws Throwable {

		try {
			newApplicationContext(IndexNameConflictConfiguration.class);
		}
		catch (BeanCreationException expected) {

			Assertions.assertThat(expected).hasMessageStartingWith("Error creating bean with name 'customerFirstNameIndex'");

			Assertions.assertThat(expected).hasCauseInstanceOf(GemfireIndexException.class);

			Assertions.assertThat(expected.getCause()).hasMessageStartingWith(String.format(
				"An Index with the same name [%s] having possibly a different definition already exists;",
					IndexNameConflictConfiguration.INDEX_NAME));

			Assertions.assertThat(expected.getCause()).hasCauseInstanceOf(IndexNameConflictException.class);

			Assertions.assertThat(expected.getCause().getCause()).hasMessage(String.format("Index named ' %s ' already exists.",
				IndexNameConflictConfiguration.INDEX_NAME));

			Assertions.assertThat(expected.getCause().getCause()).hasNoCause();

			throw expected.getCause().getCause();
		}
	}

	@Configuration
	@SuppressWarnings("unused")
	public static class GemFireConfiguration {

		public Properties gemfireProperties() {

			Properties gemfireProperties = new Properties();

			gemfireProperties.setProperty("name", IndexConflictsIntegrationTests.class.getSimpleName());
			gemfireProperties.setProperty("log-level", "error");

			return gemfireProperties;
		}

		@Bean
		public CacheFactoryBean gemfireCache() {

			CacheFactoryBean cacheFactoryBean = new CacheFactoryBean();

			cacheFactoryBean.setClose(true);
			cacheFactoryBean.setProperties(gemfireProperties());

			return cacheFactoryBean;
		}

		@Bean(name = "Customers")
		public ReplicatedRegionFactoryBean<?, ?> customersRegion(GemFireCache gemfireCache) {

			ReplicatedRegionFactoryBean<?, ?> customersRegionFactory = new ReplicatedRegionFactoryBean<>();

			customersRegionFactory.setCache(gemfireCache);
			customersRegionFactory.setPersistent(false);

			return customersRegionFactory;
		}
	}

	@Configuration
	@Import(GemFireConfiguration.class)
	@SuppressWarnings("unused")
	public static class IndexDefinitionConflictConfiguration {

		@Bean
		public IndexFactoryBean customerIdIndex(GemFireCache gemfireCache) {

			IndexFactoryBean indexFactoryBean = new IndexFactoryBean();

			indexFactoryBean.setCache(gemfireCache);
			indexFactoryBean.setExpression("id");
			indexFactoryBean.setFrom("/Customers");
			indexFactoryBean.setType(IndexType.PRIMARY_KEY);

			return indexFactoryBean;
		}

		@Bean
		@DependsOn("customerIdIndex")
		public IndexFactoryBean customerIdentifierIndex(GemFireCache gemfireCache) {

			IndexFactoryBean indexFactoryBean = new IndexFactoryBean();

			indexFactoryBean.setCache(gemfireCache);
			indexFactoryBean.setExpression("id");
			indexFactoryBean.setIgnoreIfExists(IGNORE.get());
			indexFactoryBean.setFrom("/Customers");
			indexFactoryBean.setOverride(OVERRIDE.get());
			indexFactoryBean.setType(IndexType.PRIMARY_KEY);

			return indexFactoryBean;
		}
	}

	@Configuration
	@Import(GemFireConfiguration.class)
	@SuppressWarnings("unused")
	public static class IndexNameConflictConfiguration {

		protected static final String INDEX_NAME = "CustomerNameIdx";

		@Bean
		public IndexFactoryBean customerLastNameIndex(GemFireCache gemfireCache) {

			IndexFactoryBean indexFactoryBean = new IndexFactoryBean();

			indexFactoryBean.setCache(gemfireCache);
			indexFactoryBean.setExpression("lastName");
			indexFactoryBean.setFrom("/Customers");
			indexFactoryBean.setName(INDEX_NAME);
			indexFactoryBean.setType(IndexType.HASH);

			return indexFactoryBean;
		}

		@Bean
		@DependsOn("customerLastNameIndex")
		public IndexFactoryBean customerFirstNameIndex(GemFireCache gemfireCache) {

			IndexFactoryBean indexFactoryBean = new IndexFactoryBean();

			indexFactoryBean.setCache(gemfireCache);
			indexFactoryBean.setExpression("firstName");
			indexFactoryBean.setFrom("/Customers");
			indexFactoryBean.setIgnoreIfExists(IGNORE.get());
			indexFactoryBean.setName(INDEX_NAME);
			indexFactoryBean.setOverride(OVERRIDE.get());
			indexFactoryBean.setType(IndexType.FUNCTIONAL);

			return indexFactoryBean;
		}
	}
}
