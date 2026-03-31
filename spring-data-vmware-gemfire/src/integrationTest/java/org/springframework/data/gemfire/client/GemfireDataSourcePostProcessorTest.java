/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalArgumentException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.data.gemfire.gud.api.GudDataPolicy;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudRegionAttributes;
import org.springframework.data.gemfire.gud.api.GudScope;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientRegionFactory;
import org.springframework.data.gemfire.gud.api.GudClientRegionShortcut;
import org.springframework.data.gemfire.gud.api.GudFunction;
import org.apache.geode.management.internal.cli.domain.RegionInformation;
import org.apache.geode.management.internal.cli.functions.GetRegionsFunction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.data.gemfire.util.RegionUtils;

/**
 * Unit tests for {@link GemfireDataSourcePostProcessor}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.GudRegion
 * @see org.apache.geode.cache.GudRegionAttributes
 * @see org.apache.geode.cache.client.GudClientCache
 * @see org.apache.geode.cache.client.GudClientRegionFactory
 * @see org.apache.geode.cache.execute.GudFunction
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory
 * @see org.springframework.data.gemfire.client.GemfireDataSourcePostProcessor
 * @see org.springframework.data.gemfire.client.function.ListRegionsOnServerFunction
 * @since 1.7.0
 */
@RunWith(MockitoJUnitRunner.class)
public class GemfireDataSourcePostProcessorTest {

	@Mock
	private ConfigurableBeanFactory mockBeanFactory;

	@Mock
	private GudClientCache mockClientCache;

	@SuppressWarnings("unchecked")
	private GudRegion<Object, Object> mockRegion(String name) {

		GudRegion<Object, Object> mockRegion = mock(GudRegion.class, name);

		GudRegionAttributes<Object, Object> mockRegionAttributes =
			mock(GudRegionAttributes.class, String.format("%s-GudRegionAttributes", name));

		when(mockRegion.getParentRegion()).thenReturn(null);
		when(mockRegion.getFullPath()).thenReturn(RegionUtils.toRegionPath(name));
		when(mockRegion.getAttributes()).thenReturn(mockRegionAttributes);
		when(mockRegionAttributes.getDataPolicy()).thenReturn(GudDataPolicy.REPLICATE);
		when(mockRegionAttributes.getScope()).thenReturn(GudScope.DISTRIBUTED_ACK);

		return mockRegion;
	}

	private RegionInformation newRegionInformation(GudRegion<?, ?> region) {
		return new RegionInformation(region, false);
	}

	@Test
	public void constructGemfireDataSourcePostProcessor() {

		GemfireDataSourcePostProcessor postProcessor = new GemfireDataSourcePostProcessor();

		assertThat(postProcessor).isNotNull();
		assertThat(postProcessor.getClientRegionShortcut().orElse(null)).isNull();
		assertThat(postProcessor.resolveClientRegionShortcut()).isEqualTo(GudClientRegionShortcut.PROXY);
		assertThat(postProcessor.getLogger()).isNotNull();
	}

	@Test
	public void setAndGetBeanFactory() {

		GemfireDataSourcePostProcessor postProcessor = new GemfireDataSourcePostProcessor();

		assertThat(postProcessor).isNotNull();
		assertThat(postProcessor.getBeanFactory().orElse(null)).isNull();

		postProcessor.setBeanFactory(this.mockBeanFactory);

		assertThat(postProcessor.getBeanFactory().orElse(null)).isSameAs(this.mockBeanFactory);
	}

	@Test(expected = TypeMismatchException.class)
	public void setBeanFactoryToIncompatibleTypeThrowsBeansException() {
		new GemfireDataSourcePostProcessor().setBeanFactory(mock(BeanFactory.class));
	}

	@Test(expected = TypeMismatchException.class)
	public void setBeanFactoryToNullThrowsBeansException() {
		new GemfireDataSourcePostProcessor().setBeanFactory(null);
	}

	@Test
	public void setAndGetClientRegionShortcut() {

		GemfireDataSourcePostProcessor postProcessor = new GemfireDataSourcePostProcessor();

		assertThat(postProcessor).isNotNull();
		assertThat(postProcessor.getClientRegionShortcut().orElse(null)).isNull();
		assertThat(postProcessor.resolveClientRegionShortcut()).isEqualTo(GudClientRegionShortcut.PROXY);

		postProcessor.setClientRegionShortcut(GudClientRegionShortcut.CACHING_PROXY);

		assertThat(postProcessor.getClientRegionShortcut().orElse(null))
			.isEqualTo(GudClientRegionShortcut.CACHING_PROXY);
		assertThat(postProcessor.resolveClientRegionShortcut()).isEqualTo(GudClientRegionShortcut.CACHING_PROXY);

		postProcessor.setClientRegionShortcut(GudClientRegionShortcut.LOCAL);

		assertThat(postProcessor.getClientRegionShortcut().orElse(null))
			.isEqualTo(GudClientRegionShortcut.LOCAL);
		assertThat(postProcessor.resolveClientRegionShortcut()).isEqualTo(GudClientRegionShortcut.LOCAL);

		postProcessor.setClientRegionShortcut(null);

		assertThat(postProcessor.getClientRegionShortcut().orElse(null)).isNull();
		assertThat(postProcessor.resolveClientRegionShortcut()).isEqualTo(GudClientRegionShortcut.PROXY);
	}

	@Test
	public void usingBeanFactory() {

		GemfireDataSourcePostProcessor postProcessor = spy(new GemfireDataSourcePostProcessor());

		assertThat(postProcessor).isNotNull();
		assertThat(postProcessor.getBeanFactory().orElse(null)).isNull();
		assertThat(postProcessor.using(this.mockBeanFactory)).isSameAs(postProcessor);
		assertThat(postProcessor.getBeanFactory().orElse(null)).isSameAs(this.mockBeanFactory);

		verify(postProcessor, times(1)).setBeanFactory(eq(this.mockBeanFactory));
	}

	@Test
	public void usingClientRegionShortcut() {

		GemfireDataSourcePostProcessor postProcessor = spy(new GemfireDataSourcePostProcessor())
			.using(GudClientRegionShortcut.LOCAL_PERSISTENT);

		assertThat(postProcessor).isNotNull();
		assertThat(postProcessor.getClientRegionShortcut().orElse(null)).isEqualTo(GudClientRegionShortcut.LOCAL_PERSISTENT);
		assertThat(postProcessor.resolveClientRegionShortcut()).isEqualTo(GudClientRegionShortcut.LOCAL_PERSISTENT);
		assertThat(postProcessor.using(GudClientRegionShortcut.LOCAL_OVERFLOW)).isSameAs(postProcessor);
		assertThat(postProcessor.getClientRegionShortcut().orElse(null)).isEqualTo(GudClientRegionShortcut.LOCAL_OVERFLOW);
		assertThat(postProcessor.resolveClientRegionShortcut()).isEqualTo(GudClientRegionShortcut.LOCAL_OVERFLOW);

		verify(postProcessor, times(1))
			.setClientRegionShortcut(eq(GudClientRegionShortcut.LOCAL_PERSISTENT));

		verify(postProcessor, times(1))
			.setClientRegionShortcut(eq(GudClientRegionShortcut.LOCAL_OVERFLOW));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void postProcessAfterInitializationCallsCreateClientRegionProxiesWithRegionNames() {

		String[] testRegionNames = { "Test" };

		GemfireDataSourcePostProcessor postProcessor = spy(new GemfireDataSourcePostProcessor());

		postProcessor.setBeanFactory(this.mockBeanFactory);

		doReturn(Arrays.asList(testRegionNames)).when(postProcessor).regionNames(any(GudClientCache.class));

		doAnswer(invocation -> {

			ConfigurableBeanFactory beanFactory = invocation.getArgument(0);
			GudClientCache clientCache = invocation.getArgument(1);
			Iterable<String> regionNames = invocation.getArgument(2);

			assertThat(beanFactory).isSameAs(this.mockBeanFactory);
			assertThat(clientCache).isSameAs(this.mockClientCache);
			assertThat(regionNames).containsExactly(testRegionNames);

			return null;

		}).when(postProcessor)
			.createClientProxyRegions(any(ConfigurableBeanFactory.class), any(GudClientCache.class), any(Iterable.class));

		postProcessor.postProcessAfterInitialization(this.mockClientCache, "mockClientCache");

		verify(postProcessor, times(1)).regionNames(eq(this.mockClientCache));
		verify(postProcessor, times(1))
			.createClientProxyRegions(eq(this.mockBeanFactory), eq(this.mockClientCache), isA(Iterable.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void postProcessAfterInitializationWithNoBeanFactoryDoesNothing() {

		GemfireDataSourcePostProcessor postProcessor = spy(new GemfireDataSourcePostProcessor());

		assertThat(postProcessor.getBeanFactory().orElse(null)).isNull();

		postProcessor.postProcessAfterInitialization(this.mockClientCache, "mockClientCache");

		verify(postProcessor, never()).regionNames(any(GudClientCache.class));
		verify(postProcessor, never()).createClientProxyRegions(any(ConfigurableBeanFactory.class),
			any(GudClientCache.class), any(Iterable.class));
	}

	@Test
	public void regionNamesWithGetRegionsFunction() {

		String[] expectedRegionNames = { "ExampleOne", "ExampleTwo" };

		GemfireDataSourcePostProcessor postProcessor = spy(new GemfireDataSourcePostProcessor());

		doAnswer(invocation ->
			Arrays.stream(expectedRegionNames)
				.map(this::mockRegion)
				.map(this::newRegionInformation)
				.collect(Collectors.toList())
				.toArray()
		).when(postProcessor).execute(isA(GudClientCache.class), isA(GetRegionsFunction.class), any());

		List<String> actualRegionNames =
			StreamSupport.stream(postProcessor.regionNames(this.mockClientCache).spliterator(), false)
				.collect(Collectors.toList());

		assertThat(actualRegionNames).containsExactly(expectedRegionNames);

		verify(postProcessor, times(1))
			.execute(eq(this.mockClientCache), isA(GetRegionsFunction.class), anyBoolean());
	}

	@Test
	public void regionNamesWithGetRegionsFunctionReturningNoResults() {

		GemfireDataSourcePostProcessor postProcessor = spy(new GemfireDataSourcePostProcessor());

		doReturn(null).when(postProcessor)
			.execute(any(GudClientCache.class), isA(GetRegionsFunction.class), any());

		Iterable<String> actualRegionNames = postProcessor.regionNames(this.mockClientCache);

		assertThat(actualRegionNames).isNotNull();
		assertThat(actualRegionNames).isEmpty();

		verify(postProcessor, times(1))
			.execute(eq(this.mockClientCache), isA(GetRegionsFunction.class), anyBoolean());
	}

	@Test
	public void regionNamesWithGetRegionsFunctionThrowingException() {

		GemfireDataSourcePostProcessor postProcessor = spy(new GemfireDataSourcePostProcessor());

		doAnswer(invocation -> {

			GudFunction function = invocation.getArgument(0);

			throw newIllegalArgumentException("GudFunction [%1$s] with ID [%2$s] not registered",
				function.getClass().getName(), function.getId());

		}).when(postProcessor).execute(any(GudClientCache.class), any(GudFunction.class), any());

		Iterable<String> actualRegionNames = postProcessor.regionNames(this.mockClientCache);

		assertThat(actualRegionNames).isNotNull();
		assertThat(actualRegionNames).isEmpty();

		verify(postProcessor, times(1))
			.execute(eq(this.mockClientCache), isA(GetRegionsFunction.class), anyBoolean());

		verify(postProcessor, times(1))
			.logDebug(startsWith("Failed to determine the Regions available on the Server:"), any());
	}

	@Test
	public void containsRegionInformationIsTrue() {
		assertThat(new GemfireDataSourcePostProcessor()
			.containsRegionInformation(new Object[] { newRegionInformation(mockRegion("Example")) }))
				.isTrue();
	}

	@Test
	public void containsRegionInformationWithEmptyArrayIsFalse() {
		assertThat(new GemfireDataSourcePostProcessor().containsRegionInformation(new Object[0])).isFalse();
	}

	@Test
	public void containsRegionInformationWithListOfRegionInformationIsFalse() {
		assertThat(new GemfireDataSourcePostProcessor()
			.containsRegionInformation(Collections.singletonList(newRegionInformation(mockRegion("Example")))))
				.isFalse();
	}

	@Test
	public void containsRegionInformationWithNonEmptyArrayContainingNonRegionInformationIsFalse() {
		assertThat(new GemfireDataSourcePostProcessor().containsRegionInformation(new Object[] { "TEST" })).isFalse();
	}

	@Test
	public void containsRegionInformationWithNullIsFalse() {
		assertThat(new GemfireDataSourcePostProcessor().containsRegionInformation(null)).isFalse();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void createClientProxyRegionsIsSuccessful() {

		GudClientRegionFactory mockClientRegionFactory = mock(GudClientRegionFactory.class);

		when(this.mockClientCache.createClientRegionFactory(eq(GudClientRegionShortcut.PROXY)))
			.thenReturn(mockClientRegionFactory);

		GudRegion mockRegionOne = mock(GudRegion.class, "MockGemFireRegionOne");
		GudRegion mockRegionTwo = mock(GudRegion.class, "MockGemFireRegionTwo");

		Map<String, GudRegion<?, ?>> regionMap = new HashMap<>(2);

		regionMap.put("RegionOne", mockRegionOne);
		regionMap.put("RegionTwo", mockRegionTwo);

		doAnswer(invocation -> {

			String regionName = invocation.getArgument(0);

			assertThat(regionMap.containsKey(regionName)).isTrue();

			return regionMap.get(regionName);

		}).when(mockClientRegionFactory).create(any(String.class));

		when(mockBeanFactory.containsBean(any(String.class))).thenReturn(false);

		GemfireDataSourcePostProcessor postProcessor = new GemfireDataSourcePostProcessor();

		postProcessor.createClientProxyRegions(this.mockBeanFactory, this.mockClientCache, regionMap.keySet());

		verify(this.mockClientCache, times(1)).createClientRegionFactory(eq(GudClientRegionShortcut.PROXY));
		verify(mockClientRegionFactory, times(1)).create(eq("RegionOne"));
		verify(mockClientRegionFactory, times(1)).create(eq("RegionTwo"));
		verify(this.mockBeanFactory, times(1)).registerSingleton(eq("RegionOne"), same(mockRegionOne));
		verify(this.mockBeanFactory, times(1)).registerSingleton(eq("RegionTwo"), same(mockRegionTwo));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void createClientProxyRegionsWhenRegionBeanExists() {

		GudClientRegionFactory mockClientRegionFactory = mock(GudClientRegionFactory.class);

		when(this.mockClientCache.createClientRegionFactory(eq(GudClientRegionShortcut.PROXY)))
			.thenReturn(mockClientRegionFactory);

		GudRegion mockRegion = mock(GudRegion.class);

		when(this.mockBeanFactory.containsBean(any(String.class))).thenReturn(true);
		when(this.mockBeanFactory.getBean(eq("Example"))).thenReturn(mockRegion);

		GemfireDataSourcePostProcessor postProcessor = new GemfireDataSourcePostProcessor();

		postProcessor.createClientProxyRegions(this.mockBeanFactory, this.mockClientCache, Collections.singletonList("Example"));

		verify(this.mockClientCache, times(1)).createClientRegionFactory(eq(GudClientRegionShortcut.PROXY));
		verify(mockClientRegionFactory, never()).create(any(String.class));
		verify(this.mockBeanFactory, never()).registerSingleton(any(String.class), any(GudRegion.class));
	}
}
