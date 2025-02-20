/*
 * Copyright 2022-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import org.apache.geode.cache.CacheListener;
import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheLoaderException;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.EvictionAction;
import org.apache.geode.cache.EvictionAlgorithm;
import org.apache.geode.cache.EvictionAttributes;
import org.apache.geode.cache.ExpirationAction;
import org.apache.geode.cache.InterestResultPolicy;
import org.apache.geode.cache.LoaderHelper;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.util.CacheWriterAdapter;
import org.apache.geode.compression.Compressor;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.data.gemfire.SimpleCacheListener;
import org.springframework.data.gemfire.SimpleObjectSizer;
import org.springframework.data.gemfire.TestUtils;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.client.Interest;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ObjectUtils;

/**
 * Unit Tests for SDG's XML namespace configuration metadata for client {@link Region Regions}.
 *
 * @author Costin Leau
 * @author David Turanski
 * @author John Blum
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.data.gemfire.client.ClientRegionFactoryBean
 * @see org.springframework.data.gemfire.config.xml.ClientRegionParser
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest
 * @see org.springframework.test.context.junit4.SpringRunner
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class ClientRegionNamespaceIntegrationTests extends IntegrationTestsSupport {

	private void assertInterest(boolean expectedDurable, boolean expectedReceiveValues,

		InterestResultPolicy expectedPolicy, Interest<Object> actualInterest) {

		Assertions.assertThat(actualInterest).isNotNull();
		Assertions.assertThat(actualInterest.isDurable()).isEqualTo(expectedDurable);
		Assertions.assertThat(actualInterest.isReceiveValues()).isEqualTo(expectedReceiveValues);
		Assertions.assertThat(actualInterest.getPolicy()).isEqualTo(expectedPolicy);
	}

	@SuppressWarnings("rawtypes")
	private Interest getInterestWithKey(String key, Interest... interests) {

		for (Interest interest : interests) {
			if (interest.getKey().equals(key)) {
				return interest;
			}
		}

		return null;
	}

	@Test
	public void beanNamesAreCorrect() {

		Assertions.assertThat(requireApplicationContext().containsBean("SimpleRegion")).isTrue();
		Assertions.assertThat(requireApplicationContext().containsBean("Publisher")).isTrue();
		Assertions.assertThat(requireApplicationContext().containsBean("ComplexRegion")).isTrue();
		Assertions.assertThat(requireApplicationContext().containsBean("PersistentRegion")).isTrue();
		Assertions.assertThat(requireApplicationContext().containsBean("OverflowRegion")).isTrue();
		Assertions.assertThat(requireApplicationContext().containsBean("Compressed")).isTrue();
	}

	@Test
	public void simpleClientRegionConfigurationIsCorrect() {

		Assertions.assertThat(requireApplicationContext().containsBean("simple")).isTrue();

		Region<?, ?> simple = requireApplicationContext().getBean("simple", Region.class);

		Assertions.assertThat(simple).as("The 'SimpleRegion' Client Region was not properly configured and initialized")
			.isNotNull();
		Assertions.assertThat(simple.getName()).isEqualTo("SimpleRegion");
		Assertions.assertThat(simple.getFullPath()).isEqualTo(Region.SEPARATOR + "SimpleRegion");
		Assertions.assertThat(simple.getAttributes()).isNotNull();
		Assertions.assertThat(simple.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void publishingClientRegionConfigurationIsCorrect() throws Exception {

		Assertions.assertThat(requireApplicationContext().containsBean("empty")).isTrue();

		ClientRegionFactoryBean emptyClientRegionFactoryBean =
			requireApplicationContext().getBean("&empty", ClientRegionFactoryBean.class);

		Assertions.assertThat(emptyClientRegionFactoryBean).isNotNull();
		Assertions.assertThat(TestUtils.<Object>readField("dataPolicy", emptyClientRegionFactoryBean)).isEqualTo(DataPolicy.EMPTY);
		Assertions.assertThat(TestUtils.<Object>readField("beanName", emptyClientRegionFactoryBean)).isEqualTo("empty");
		Assertions.assertThat(TestUtils.<Object>readField("name", emptyClientRegionFactoryBean)).isEqualTo("Publisher");
		Assertions.assertThat(TestUtils.<Object>readField("poolName", emptyClientRegionFactoryBean)).isEqualTo("gemfire-pool");
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void complexClientRegionConfigurationIsCorrect() throws Exception {

		Assertions.assertThat(requireApplicationContext().containsBean("complex")).isTrue();

		ClientRegionFactoryBean complexClientRegionFactoryBean =
			requireApplicationContext().getBean("&complex", ClientRegionFactoryBean.class);

		Assertions.assertThat(complexClientRegionFactoryBean).isNotNull();

		CacheListener[] cacheListeners = TestUtils.readField("cacheListeners", complexClientRegionFactoryBean);

		Assertions.assertThat(ObjectUtils.isEmpty(cacheListeners)).isFalse();
		Assertions.assertThat(cacheListeners.length).isEqualTo(2);
		Assertions.assertThat(requireApplicationContext().getBean("c-listener")).isSameAs(cacheListeners[0]);
		Assertions.assertThat(cacheListeners[1] instanceof SimpleCacheListener).isTrue();
		Assertions.assertThat(cacheListeners[1]).isNotSameAs(cacheListeners[0]);

		RegionAttributes complexRegionAttributes =
			TestUtils.<RegionAttributes<?, ?>>readField("attributes", complexClientRegionFactoryBean);

		Assertions.assertThat(complexRegionAttributes).isNotNull();
		Assertions.assertThat(complexRegionAttributes.getLoadFactor()).isCloseTo(0.5f, Offset.offset(0.001f));
		Assertions.assertThat(complexRegionAttributes.getEntryTimeToLive().getAction()).isEqualTo(ExpirationAction.INVALIDATE);
		Assertions.assertThat(complexRegionAttributes.getEntryTimeToLive().getTimeout()).isEqualTo(500);
		Assertions.assertThat(complexRegionAttributes.getEvictionAttributes().getMaximum()).isEqualTo(5);
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void persistentClientRegionConfigurationIsCorrect() {

		Assertions.assertThat(requireApplicationContext().containsBean("persistent")).isTrue();

		Region<?, ?> persistent = requireApplicationContext().getBean("persistent", Region.class);

		Assertions.assertThat(persistent)
			.describedAs("The 'PersistentRegion' Region was not properly configured and initialized")
			.isNotNull();

		Assertions.assertThat(persistent.getName()).isEqualTo("PersistentRegion");
		Assertions.assertThat(persistent.getFullPath()).isEqualTo(Region.SEPARATOR + "PersistentRegion");

		RegionAttributes persistentRegionAttributes = persistent.getAttributes();

		Assertions.assertThat(persistentRegionAttributes.getDataPolicy()).isEqualTo(DataPolicy.PERSISTENT_REPLICATE);
		Assertions.assertThat(persistentRegionAttributes.getDiskStoreName()).isEqualTo("diskStore");
		Assertions.assertThat(persistentRegionAttributes.getPoolName()).isEqualTo("gemfire-pool");
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void overflowClientRegionConfigurationIsCorrect() throws Exception {

		Assertions.assertThat(requireApplicationContext().containsBean("overflow")).isTrue();

		ClientRegionFactoryBean overflowClientRegionFactoryBean =
			requireApplicationContext().getBean("&overflow", ClientRegionFactoryBean.class);

		Assertions.assertThat(overflowClientRegionFactoryBean).isNotNull();
		Assertions.assertThat(TestUtils.<Object>readField("diskStoreName", overflowClientRegionFactoryBean)).isEqualTo("diskStore");
		Assertions.assertThat(TestUtils.<Object>readField("poolName", overflowClientRegionFactoryBean)).isEqualTo("gemfire-pool");

		RegionAttributes overflowRegionAttributes =
			TestUtils.<RegionAttributes<?, ?>>readField("attributes", overflowClientRegionFactoryBean);

		Assertions.assertThat(overflowRegionAttributes).isNotNull();
		Assertions.assertThat(overflowRegionAttributes.getDataPolicy()).isEqualTo(DataPolicy.NORMAL);

		EvictionAttributes overflowEvictionAttributes = overflowRegionAttributes.getEvictionAttributes();

		Assertions.assertThat(overflowEvictionAttributes).isNotNull();
		Assertions.assertThat(overflowEvictionAttributes.getAction()).isEqualTo(EvictionAction.OVERFLOW_TO_DISK);
		Assertions.assertThat(overflowEvictionAttributes.getAlgorithm()).isEqualTo(EvictionAlgorithm.LRU_MEMORY);
		Assertions.assertThat(overflowEvictionAttributes.getMaximum()).isEqualTo(10);
		Assertions.assertThat(overflowEvictionAttributes.getObjectSizer() instanceof SimpleObjectSizer).isTrue();
	}

	@Test
	public void clientRegionWithCacheLoaderAndCacheWriterConfigurationIsCorrect() throws Exception {

		Assertions.assertThat(requireApplicationContext().containsBean("loadWithWrite")).isTrue();

		ClientRegionFactoryBean<?, ?> factory =
			requireApplicationContext().getBean("&loadWithWrite", ClientRegionFactoryBean.class);

		Assertions.assertThat(factory).isNotNull();
		Assertions.assertThat(TestUtils.<Object>readField("name", factory)).isEqualTo("LoadedFullOfWrites");
		Assertions.assertThat(TestUtils.<Object>readField("shortcut", factory)).isEqualTo(ClientRegionShortcut.LOCAL);
		Assertions.assertThat(TestUtils.<Object>readField("cacheLoader", factory)).isInstanceOf(TestCacheLoader.class);
		Assertions.assertThat(TestUtils.<Object>readField("cacheWriter", factory)).isInstanceOf(TestCacheWriter.class);
	}

	@Test
	public void compressedRegionConfigurationIsCorrect() {

		Assertions.assertThat(requireApplicationContext().containsBean("Compressed")).isTrue();

		Region<?, ?> compressed = requireApplicationContext().getBean("Compressed", Region.class);

		Assertions.assertThat(compressed).as("The 'Compressed' Client Region was not properly configured and initialized")
			.isNotNull();
		Assertions.assertThat(compressed.getName()).isEqualTo("Compressed");
		Assertions.assertThat(compressed.getFullPath()).isEqualTo(Region.SEPARATOR + "Compressed");
		Assertions.assertThat(compressed.getAttributes()).isNotNull();
		Assertions.assertThat(compressed.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.EMPTY);
		Assertions.assertThat(compressed.getAttributes().getPoolName()).isEqualTo("gemfire-pool");
		Assertions.assertThat(compressed.getAttributes().getCompressor() instanceof TestCompressor)
			.describedAs(String.format("Expected 'TestCompressor'; but was '%s'",
				ObjectUtils.nullSafeClassName(compressed.getAttributes().getCompressor())))
			.isTrue();
		Assertions.assertThat(compressed.getAttributes().getCompressor().toString()).isEqualTo("STD");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void clientRegionWithAttributesConfigurationIsCorrect() {

		Assertions.assertThat(requireApplicationContext().containsBean("client-with-attributes")).isTrue();

		Region<Long, String> clientRegion =
			requireApplicationContext().getBean("client-with-attributes", Region.class);

		Assertions.assertThat(clientRegion)
			.describedAs("The 'client-with-attributes' Client Region was not properly configured and initialized")
			.isNotNull();

		Assertions.assertThat(clientRegion.getName()).isEqualTo("client-with-attributes");
		Assertions.assertThat(clientRegion.getFullPath()).isEqualTo(Region.SEPARATOR + "client-with-attributes");
		Assertions.assertThat(clientRegion.getAttributes()).isNotNull();
		Assertions.assertThat(clientRegion.getAttributes().getCloningEnabled()).isFalse();
		Assertions.assertThat(clientRegion.getAttributes().getConcurrencyChecksEnabled()).isTrue();
		Assertions.assertThat(clientRegion.getAttributes().getConcurrencyLevel()).isEqualTo(8);
		Assertions.assertThat(clientRegion.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		Assertions.assertThat(clientRegion.getAttributes().getDataPolicy().withPersistence()).isFalse();
		Assertions.assertThat(clientRegion.getAttributes().getInitialCapacity()).isEqualTo(64);
		Assertions.assertThat(clientRegion.getAttributes().getKeyConstraint()).isEqualTo(Long.class);
		Assertions.assertThat(String.valueOf(clientRegion.getAttributes().getLoadFactor())).isEqualTo("0.85");
		Assertions.assertThat(clientRegion.getAttributes().getPoolName()).isEqualTo("gemfire-pool");
		Assertions.assertThat(clientRegion.getAttributes().getValueConstraint()).isEqualTo(String.class);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void clientRegionWithRegisteredInterestsConfigurationIsCorrect() throws Exception {

		Assertions.assertThat(requireApplicationContext().containsBean("client-with-interests")).isTrue();

		ClientRegionFactoryBean<?, ?> factoryBean =
			requireApplicationContext().getBean("&client-with-interests", ClientRegionFactoryBean.class);

		Assertions.assertThat(factoryBean).isNotNull();

		Interest<?>[] interests = TestUtils.readField("interests", factoryBean);

		Assertions.assertThat(interests).isNotNull();
		Assertions.assertThat(interests.length).isEqualTo(2);

		assertInterest(true, false, InterestResultPolicy.KEYS, getInterestWithKey(".*", interests));
		assertInterest(true, false, InterestResultPolicy.KEYS_VALUES, getInterestWithKey("keyPrefix.*", interests));

		Region<Object, Object> mockClientRegion =
			requireApplicationContext().getBean("client-with-interests", Region.class);

		Assertions.assertThat(mockClientRegion).isNotNull();

		Mockito.verify(mockClientRegion, Mockito.times(1)).registerInterest(ArgumentMatchers.eq(".*"),
			ArgumentMatchers.eq(InterestResultPolicy.KEYS), ArgumentMatchers.eq(true), ArgumentMatchers.eq(false));

		Mockito.verify(mockClientRegion, Mockito.times(1)).registerInterestRegex(ArgumentMatchers.eq("keyPrefix.*"),
			ArgumentMatchers.eq(InterestResultPolicy.KEYS_VALUES), ArgumentMatchers.eq(true), ArgumentMatchers.eq(false));
	}

	public static final class TestCacheLoader implements CacheLoader<Object, Object> {

		@Override
		public Object load(final LoaderHelper<Object, Object> helper) throws CacheLoaderException {
			throw new UnsupportedOperationException("Not Implemented");
		}

		@Override
		public void close() { }

	}

	public static final class TestCacheWriter extends CacheWriterAdapter<Object, Object> { }

	public static class TestCompressor implements Compressor {

		private String name;

		public void setName(final String name) {
			this.name = name;
		}

		@Override
		public byte[] compress(final byte[] input) {
			throw new UnsupportedOperationException("Not Implemented");
		}

		@Override
		public byte[] decompress(final byte[] input) {
			throw new UnsupportedOperationException("Not Implemented");
		}

		@Override
		public String toString() {
			return this.name;
		}
	}
}
