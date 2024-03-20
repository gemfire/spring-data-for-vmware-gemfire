/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.RegionService;
import org.apache.geode.cache.Scope;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.query.Query;
import org.apache.geode.cache.query.QueryService;
import org.apache.geode.cache.query.SelectResults;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;

/**
 * Unit Tests for {@link GemfireTemplate}
 *
 * @author Costin Leau
 * @author David Turanski
 * @author John Blum
 * @see org.junit.Test
 * @see Mock
 * @see org.mockito.Mockito
 * @see MockitoJUnitRunner
 * @see Region
 * @see ClientCache
 * @see Query
 * @see QueryService
 * @see SelectResults
 * @see GemfireTemplate
 * @see IntegrationTestsSupport
 */
@SuppressWarnings("unused")
@RunWith(MockitoJUnitRunner.class)
public class GemfireTemplateUnitTests extends IntegrationTestsSupport {

	private GemfireTemplate template;

	@Mock
	private Query mockQuery;

	@Mock
	private QueryService mockQueryService;

	@Mock
	private Region<?, ?> mockRegion;

	@Mock
	private RegionService mockRegionService;

	@Before
	public void setUp() {

		when(mockRegion.getRegionService()).thenReturn(mockRegionService);
		when(mockRegionService.getQueryService()).thenReturn(mockQueryService);
		when(mockQueryService.newQuery(anyString())).thenReturn(mockQuery);

		template = new GemfireTemplate(mockRegion);
	}

	@Test
	public void constructWithNonNullRegionIsSuccessful() {

		GemfireTemplate localTemplate = new GemfireTemplate(mockRegion);

		assertThat(localTemplate).isNotNull();
		assertThat(localTemplate.getRegion()).isSameAs(mockRegion);
		assertThat(localTemplate.isExposeNativeRegion()).isFalse();
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructWithNullRegionThrowsIllegalArgumentException() {

		try {
			new GemfireTemplate(null);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("Region is required");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void executeWithGemfireCallbackUsesNativeRegion() {

		template.setExposeNativeRegion(true);

		assertThat(template.isExposeNativeRegion()).isTrue();
		assertThat(template.getRegion()).isSameAs(mockRegion);

		final AtomicBoolean callbackInvoked = new AtomicBoolean(false);

		template.execute(region -> {
			callbackInvoked.set(true);
			assertThat(region).isSameAs(mockRegion);
			return null;
		});

		assertThat(callbackInvoked.get()).isTrue();
	}

	@Test
	public void executeWithGemfireCallbackUsesProxyRegion() {

		assertThat(template.isExposeNativeRegion()).isFalse();

		AtomicBoolean callbackInvoked = new AtomicBoolean(false);

		template.execute(region -> {
			callbackInvoked.set(true);
			assertThat(region).isNotSameAs(mockRegion);
			return null;
		});

		assertThat(callbackInvoked.get()).isTrue();
	}

	@Test
	public void queryCallsRegionQuery() throws Exception {

		String expectedQuery = "SELECT * FROM /Example";

		template.query(expectedQuery);

		verify(mockRegion, times(1)).query(eq(expectedQuery));
	}

	@Test
	public void findIsSuccessful() throws Exception {

		Object[] expectedParams = { "arg" };

		String expectedQuery = "SELECT * FROM /Example";

		SelectResults<?> mockSelectResults = mock(SelectResults.class);

		when(mockQuery.execute(any(Object.class))).thenReturn(mockSelectResults);

		assertThat(template.find(expectedQuery, expectedParams)).isEqualTo(mockSelectResults);

		verify(mockRegion, atLeastOnce()).getRegionService();
		verify(mockRegionService, times(1)).getQueryService();
		verify(mockQueryService, times(1)).newQuery(eq(expectedQuery));
		verify(mockQuery, times(1)).execute(eq("arg"));

		verifyNoMoreInteractions(mockSelectResults);
	}

	@Test(expected = InvalidDataAccessApiUsageException.class)
	public void findWithSingleResultQueryThrowsInvalidDataAccessApiUsageException() throws Exception {

		Object[] expectedParams = { "arg" };

		String expectedQuery = "SELECT 1 FROM /Example";

		when(mockQuery.execute(any(Object.class))).thenReturn(1);

		try {
			template.find(expectedQuery, expectedParams);
		}
		finally {
			verify(mockRegion, atLeastOnce()).getRegionService();
			verify(mockRegionService, times(1)).getQueryService();
			verify(mockQueryService, times(1)).newQuery(eq(expectedQuery));
			verify(mockQuery, times(1)).execute(eq("arg"));
		}
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void findUniqueReturnsSelectResultsIsSuccessful() throws Exception {

		Object[] expectedParams = { "arg" };

		String expectedQuery = "SELECT 1 FROM /Example";

		SelectResults mockSelectResults = mock(SelectResults.class);

		when(mockQuery.execute(any(Object.class))).thenReturn(mockSelectResults);
		when(mockSelectResults.asList()).thenReturn(Collections.singletonList(1));

		assertThat((Object) template.findUnique(expectedQuery, expectedParams)).isEqualTo(1);

		verify(mockRegion, atLeastOnce()).getRegionService();
		verify(mockRegionService, times(1)).getQueryService();
		verify(mockQueryService, times(1)).newQuery(eq(expectedQuery));
		verify(mockQuery, times(1)).execute(eq("arg"));
		verify(mockSelectResults, times(1)).asList();
	}

	@Test
	public void findUniqueReturnsObjectIsSuccessful() throws Exception {

		Object[] expectedParams = { "arg" };

		String expectedQuery = "SELECT 1 FROM /Example";

		when(mockQuery.execute(any(Object.class))).thenReturn("test");

		assertThat((Object) template.findUnique(expectedQuery, expectedParams)).isEqualTo("test");

		verify(mockRegion, atLeastOnce()).getRegionService();
		verify(mockRegionService, times(1)).getQueryService();
		verify(mockQueryService, times(1)).newQuery(eq(expectedQuery));
		verify(mockQuery, times(1)).execute(eq("arg"));
	}

	@SuppressWarnings("rawtypes")
	@Test(expected = InvalidDataAccessApiUsageException.class)
	public void findUniqueWithMultiResultQueryThrowsInvalidDataAccessApiUsageException() throws Exception {

		Object[] expectedParams = { "arg" };

		String expectedQuery = "SELECT 1 FROM /Example";

		SelectResults mockSelectResults = mock(SelectResults.class);

		when(mockQuery.execute(any(Object.class))).thenReturn(mockSelectResults);
		when(mockSelectResults.asList()).thenReturn(Arrays.asList(1, 2));

		try {
			assertThat((Object) template.findUnique(expectedQuery, expectedParams)).isEqualTo(1);
		}
		finally {
			verify(mockRegion, atLeastOnce()).getRegionService();
			verify(mockRegionService, times(1)).getQueryService();
			verify(mockQueryService, times(1)).newQuery(eq(expectedQuery));
			verify(mockQuery, times(1)).execute(eq("arg"));
			verify(mockSelectResults, times(1)).asList();
		}
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void removeAllCallsRegionRemoveAll() {

		Collection keys = Arrays.asList(1, 2, 3);

		this.template.removeAll(keys);

		verify(this.mockRegion, times(1)).removeAll(eq(keys));
		verifyNoMoreInteractions(this.mockRegion);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void resolveClientQueryService() {

		ClientCache mockClientCache = mock(ClientCache.class);
		Region<Object, Object> mockRegion = mock(Region.class);
		RegionAttributes<Object, Object> mockRegionAttributes = mock(RegionAttributes.class);

		when(mockRegion.getAttributes()).thenReturn(mockRegionAttributes);
		when(mockRegionAttributes.getScope()).thenReturn(Scope.GLOBAL);
		when(mockRegion.getRegionService()).thenReturn(mockClientCache);
		when(mockClientCache.getQueryService()).thenReturn(mockQueryService);

		GemfireTemplate localTemplate = new GemfireTemplate(mockRegion) {
			@Override boolean isLocalWithNoServerProxy(Region<?, ?> region) {
				return false;
			}
		};

		assertThat(localTemplate.resolveQueryService(mockRegion)).isSameAs(mockQueryService);

		verify(mockClientCache, never()).getLocalQueryService();
		verify(mockClientCache, times(1)).getQueryService();
		verify(mockClientCache, never()).getQueryService(anyString());
		verify(mockRegion, times(2)).getAttributes();
		verify(mockRegion, times(3)).getRegionService();
		verify(mockRegionAttributes, times(1)).getScope();

		verifyNoMoreInteractions(mockQueryService);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void resolvesClientLocalQueryService() {

		ClientCache mockClientCache = mock(ClientCache.class);
		Region<Object, Object> mockRegion = mock(Region.class);
		RegionAttributes<Object, Object> mockRegionAttributes = mock(RegionAttributes.class);

		when(mockClientCache.getLocalQueryService()).thenReturn(mockQueryService);
		when(mockRegion.getAttributes()).thenReturn(mockRegionAttributes);
		when(mockRegion.getRegionService()).thenReturn(mockClientCache);
		when(mockRegionAttributes.getScope()).thenReturn(Scope.LOCAL);

		GemfireTemplate localTemplate = new GemfireTemplate(mockRegion) {
			@Override boolean isLocalWithNoServerProxy(Region<?, ?> region) {
				return true;
			}
		};

		assertThat(localTemplate.resolveQueryService(mockRegion)).isSameAs(mockQueryService);

		verify(mockClientCache, times(1)).getLocalQueryService();
		verify(mockClientCache, never()).getQueryService();
		verify(mockClientCache, never()).getQueryService(anyString());
		verify(mockRegion, times(2)).getRegionService();
		verify(mockRegionAttributes, times(1)).getScope();

		verifyNoMoreInteractions(mockQueryService);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void resolvesClientPooledQueryService() {

		ClientCache mockClientCache = mock(ClientCache.class);
		Region<Object, Object> mockRegion = mock(Region.class);
		RegionAttributes<Object, Object> mockRegionAttributes = mock(RegionAttributes.class);

		when(mockClientCache.getQueryService(anyString())).thenReturn(mockQueryService);
		when(mockRegion.getAttributes()).thenReturn(mockRegionAttributes);
		when(mockRegion.getRegionService()).thenReturn(mockClientCache);
		when(mockRegionAttributes.getPoolName()).thenReturn("TestPool");
		when(mockRegionAttributes.getScope()).thenReturn(Scope.LOCAL);

		GemfireTemplate localTemplate = new GemfireTemplate(mockRegion) {
			@Override boolean isLocalWithNoServerProxy(Region<?, ?> region) {
				return false;
			}
		};

		assertThat(localTemplate.resolveQueryService(mockRegion)).isSameAs(mockQueryService);

		verify(mockClientCache, never()).getLocalQueryService();
		verify(mockClientCache, never()).getQueryService();
		verify(mockClientCache, times(1)).getQueryService(eq("TestPool"));
		verify(mockRegion, times(2)).getRegionService();
		verify(mockRegionAttributes, times(2)).getPoolName();
		verify(mockRegionAttributes, times(1)).getScope();

		verifyNoMoreInteractions(mockQueryService);
	}

	@Test
	public void resolvePeerQueryService() {

		assertThat(template.resolveQueryService(mockRegion)).isEqualTo(mockQueryService);

		verify(mockRegion, times(2)).getRegionService();
		verify(mockRegion, never()).getAttributes();
		verify(mockRegionService, times(1)).getQueryService();
	}
}
