/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.schema.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.Index;

import org.springframework.data.gemfire.IndexType;
import org.springframework.data.gemfire.config.schema.SchemaObjectType;
import org.springframework.data.gemfire.config.schema.definitions.IndexDefinition;

/**
 * Unit tests for {@link IndexDefiner}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.query.Index
 * @see org.springframework.data.gemfire.IndexType
 * @see org.springframework.data.gemfire.config.schema.definitions.IndexDefinition
 * @see IndexDefiner
 * @since 1.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class IndexDefinerUnitTests {

	@Mock
	private Index mockIndex;

	@Mock
	private Region<?, ?> mockRegion;

	private IndexDefiner indexInstanceHandler = new IndexDefiner();

	@Before
	@SuppressWarnings("unchecked")
	public void setup() {
		when(this.mockIndex.getName()).thenReturn("MockIndex");
		when(this.mockIndex.getIndexedExpression()).thenReturn("testExpression");
		when(this.mockIndex.getFromClause()).thenReturn("/TestFromClause");
		when(this.mockIndex.getType()).thenReturn(IndexType.HASH.getGemfireIndexType());
	}

	@Test
	public void canHandleIndexInstanceIsTrue() {
		assertThat(this.indexInstanceHandler.canDefine(this.mockIndex)).isTrue();
	}

	@Test
	public void canHandleNullInstanceIsFalse() {
		assertThat(this.indexInstanceHandler.canDefine((Object) null)).isFalse();
	}

	@Test
	public void canHandleRegionInstanceIsFalse() {
		assertThat(this.indexInstanceHandler.canDefine(this.mockRegion)).isFalse();
	}

	@Test
	public void canHandleIndexTypeIsTrue() {
		assertThat(this.indexInstanceHandler.canDefine(Index.class)).isTrue();
	}

	@Test
	public void canHandleNullTypeIsFalse() {
		assertThat(this.indexInstanceHandler.canDefine((Class<?>) null)).isFalse();
	}

	@Test
	public void canHandleRegionTypeIsFalse() {
		assertThat(this.indexInstanceHandler.canDefine(Region.class)).isFalse();
	}

	@Test
	public void canHandleIndexSchemaObjectTypeIsTrue() {
		assertThat(this.indexInstanceHandler.canDefine(SchemaObjectType.INDEX)).isTrue();
	}

	@Test
	public void canHandleNullSchemaObjectTypeIsFalse() {
		assertThat(this.indexInstanceHandler.canDefine((SchemaObjectType) null)).isFalse();
	}

	@Test
	public void canHandleRegionSchemaObjectTypeIsFalse() {
		assertThat(this.indexInstanceHandler.canDefine(SchemaObjectType.REGION)).isFalse();
	}

	@Test
	public void defineForIndexObject() {

		IndexDefinition indexDefinition = this.indexInstanceHandler.define(this.mockIndex).orElse(null);

		assertThat(indexDefinition).isNotNull();
		assertThat(indexDefinition.getExpression()).isEqualTo("testExpression");
		assertThat(indexDefinition.getFromClause()).isEqualTo("/TestFromClause");
		assertThat(indexDefinition.getIndexType()).isEqualTo(IndexType.HASH);
		assertThat(indexDefinition.getType()).isEqualTo(SchemaObjectType.INDEX);
	}

	@Test
	public void defineForRegionObject() {
		assertThat(this.indexInstanceHandler.define(this.mockRegion).isPresent()).isFalse();
	}
}
