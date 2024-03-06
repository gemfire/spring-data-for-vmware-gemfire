/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.Index;

import org.springframework.data.gemfire.config.schema.SchemaObjectDefinition;
import org.springframework.data.gemfire.config.schema.SchemaObjectType;
import org.springframework.data.gemfire.config.schema.definitions.IndexDefinition;
import org.springframework.data.gemfire.config.schema.definitions.RegionDefinition;

/**
 * The GemfireAdminOperationsUnitTests class...
 *
 * @author John Blum
 * @since 1.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class GemfireAdminOperationsUnitTests {

	@Mock
	private GemfireAdminOperations adminOperations;

	private Index mockIndex(String name) {

		Index mockIndex = mock(Index.class, name);

		when(mockIndex.getName()).thenReturn(name);

		return mockIndex;
	}

	@SuppressWarnings("unchecked")
	private <K, V> Region<K, V> mockRegion(String name) {

		Region<K, V> mockRegion = mock(Region.class, name);

		when(mockRegion.getName()).thenReturn(name);

		return mockRegion;
	}

	private SchemaObjectDefinition newGenericSchemaObjectDefinition(String name, SchemaObjectType type) {
		return mock(SchemaObjectDefinition.class, name);
	}

	@Test
	public void createRegionsWithArrayCallsCreateRegion() {

		doCallRealMethod().when(adminOperations).createRegions(ArgumentMatchers.<RegionDefinition[]>any());

		RegionDefinition definitionOne = RegionDefinition.from(mockRegion("RegionOne"));
		RegionDefinition definitionTwo = RegionDefinition.from(mockRegion("RegionTwo"));

		adminOperations.createRegions(definitionOne, definitionTwo);

		verify(adminOperations, times(1)).createRegion(eq(definitionOne));
		verify(adminOperations, times(1)).createRegion(eq(definitionTwo));
	}

	@Test
	public void createRegionsWithEmptyArray() {

		doCallRealMethod().when(adminOperations).createRegions(ArgumentMatchers.<RegionDefinition[]>any());

		adminOperations.createRegions();

		verify(adminOperations, never()).createRegion(any(RegionDefinition.class));
	}

	@Test
	public void createRegionsWithNullArray() {

		doCallRealMethod().when(adminOperations).createRegions(ArgumentMatchers.<RegionDefinition[]>any());

		adminOperations.createRegions((RegionDefinition[]) null);

		verify(adminOperations, never()).createRegion(any(RegionDefinition.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void createRegionsWithIterableCallsCreateRegion() {

		doCallRealMethod().when(adminOperations).createRegions(any(Iterable.class));

		RegionDefinition definitionOne = RegionDefinition.from(mockRegion("RegionOne"));
		RegionDefinition definitionTwo = RegionDefinition.from(mockRegion("RegionTwo"));

		adminOperations.createRegions(Arrays.asList(definitionOne, definitionTwo));

		verify(adminOperations, times(1)).createRegion(eq(definitionOne));
		verify(adminOperations, times(1)).createRegion(eq(definitionTwo));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void createRegionsWithEmptyIterableCallsCreateRegion() {

		doCallRealMethod().when(adminOperations).createRegions(any(Iterable.class));

		adminOperations.createRegions(Collections.emptyList());

		verify(adminOperations, never()).createRegion(any(RegionDefinition.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void createRegionsWithNullIterableCallsCreateRegion() {

		adminOperations.createRegions((Iterable) null);

		verify(adminOperations, never()).createRegion(any(RegionDefinition.class));
	}

	@Test
	public void createIndexesWithArrayCallsCreateIndex() {

		doCallRealMethod().when(adminOperations).createIndexes(ArgumentMatchers.<IndexDefinition[]>any());

		IndexDefinition definitionOne = IndexDefinition.from(mockIndex("IndexOne"));
		IndexDefinition definitionTwo = IndexDefinition.from(mockIndex("IndexTwo"));

		adminOperations.createIndexes(definitionOne, definitionTwo);

		verify(adminOperations, times(1)).createIndex(eq(definitionOne));
		verify(adminOperations, times(1)).createIndex(eq(definitionTwo));
	}

	@Test
	public void createIndexesWithEmptyArray() {

		doCallRealMethod().when(adminOperations).createIndexes(ArgumentMatchers.<IndexDefinition[]>any());

		adminOperations.createIndexes();

		verify(adminOperations, never()).createIndex(any(IndexDefinition.class));
	}

	@Test
	public void createIndexesWithNullArray() {

		doCallRealMethod().when(adminOperations).createIndexes(ArgumentMatchers.<IndexDefinition[]>any());

		adminOperations.createIndexes();

		verify(adminOperations, never()).createIndex(any(IndexDefinition.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void createIndexesWithIterableCallsCreateIndex() {

		doCallRealMethod().when(adminOperations).createIndexes(any(Iterable.class));

		IndexDefinition definitionOne = IndexDefinition.from(mockIndex("IndexOne"));
		IndexDefinition definitionTwo = IndexDefinition.from(mockIndex("IndexTwo"));

		adminOperations.createIndexes(Arrays.asList(definitionOne, definitionTwo));

		verify(adminOperations, times(1)).createIndex(eq(definitionOne));
		verify(adminOperations, times(1)).createIndex(eq(definitionTwo));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void createIndexesWithEmptyIterable() {

		doCallRealMethod().when(adminOperations).createIndexes(any(Iterable.class));

		adminOperations.createIndexes(Collections.emptyList());

		verify(adminOperations, never()).createIndex(any(IndexDefinition.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void createIndexesWithNullIterable() {

		adminOperations.createIndexes((Iterable) null);

		verify(adminOperations, never()).createIndex(any(IndexDefinition.class));
	}
}
