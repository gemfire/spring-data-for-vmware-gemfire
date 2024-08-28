/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.query.Index;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.data.gemfire.IndexType;
import org.springframework.data.gemfire.mapping.annotation.Indexed;
import org.springframework.data.gemfire.test.model.Book;
import org.springframework.data.gemfire.test.model.Person;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link EnableIndexing}, {@link IndexConfiguration} and the {@link Indexed} annotation.
 *
 * @author John Blum
 * @see Test
 * @see Region
 * @see Index
 * @see EnableIndexing
 * @see IndexConfiguration
 * @see Indexed
 * @see IntegrationTestsSupport
 * @see ContextConfiguration
 * @see SpringRunner
 * @since 2.0.3
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class EnableOqlIndexingConfigurationIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("People")
	private Region<Long, Person> people;

	@Autowired
	@Qualifier("PeopleIdKeyIdx")
	private Index personIdKeyIndex;

	@Autowired
	@Qualifier("PeopleLastNameFunctionalIdx")
	private Index personLastNameHashIndex;

	@Test
	public void peopleRegionIsSetupCorrectly() {

		assertThat(this.people).isNotNull();
		assertThat(this.people.getName()).isEqualTo("People");
		assertThat(this.people.getFullPath()).isEqualTo(GemfireUtils.toRegionPath("People"));
		assertThat(this.people.getAttributes()).isNotNull();
		assertThat(this.people.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
	}

	private void assertIndex(Index index, String name, String expression, String from, IndexType indexType) {

		assertThat(index).isNotNull();
		assertThat(index.getName()).isEqualTo(name);
		assertThat(index.getIndexedExpression()).isEqualTo(expression);
		assertThat(index.getFromClause()).isEqualTo(from);
		assertThat(index.getType()).isEqualTo(indexType.getGemfireIndexType());
	}

	/**
	 * @see <a href="https://jira.spring.io/browse/DATAGEODE-68">From clause Region path error occurs when creating Indexes from application domain object fields annotated with @Indexed or @Id</a>
	 */
	@Test
	public void idKeyIndexAndLastNameHashIndexAreSetupCorrectly() {

		assertIndex(this.personIdKeyIndex, "PeopleIdKeyIdx", "id", "/People", IndexType.KEY);
		assertIndex(this.personLastNameHashIndex, "PeopleLastNameFunctionalIdx",
			"lastName", "/People", IndexType.FUNCTIONAL);
	}

	@ClientCacheApplication
	@EnableEntityDefinedRegions(
		basePackageClasses = Person.class,
		clientRegionShortcut = ClientRegionShortcut.LOCAL,
		excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = Book.class)
	)
	@EnableIndexing
	static class TestConfiguration { }

}
