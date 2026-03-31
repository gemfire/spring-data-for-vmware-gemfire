/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.eviction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.data.gemfire.gud.api.GudEvictionAction;
import org.springframework.data.gemfire.gud.api.GudEvictionAlgorithm;
import org.springframework.data.gemfire.gud.api.GudEvictionAttributes;
import org.springframework.data.gemfire.gud.api.GudObjectSizer;

/**
 * Unit Tests for {@link EvictionAttributesFactoryBean}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.GudEvictionAttributes
 * @see org.springframework.data.gemfire.eviction.EvictionAttributesFactoryBean
 * @since 1.3.4
 */
public class EvictionAttributesFactoryBeanTest {

	private EvictionAttributesFactoryBean factoryBean;

	private GudObjectSizer mockObjectSizer;

	@Before
	public void setup() {
		factoryBean = new EvictionAttributesFactoryBean();
		mockObjectSizer = mock(GudObjectSizer.class, "MockObjectSizer");
	}

	@After
	public void tearDown() {
		factoryBean = null;
		mockObjectSizer = null;
	}

	@Test
	public void testIsSingleton() {
		assertThat(new EvictionAttributesFactoryBean().isSingleton()).isTrue();
	}

	@Test
	public void testCreateEntryCountEvictionAttributesWithNullAction() {

		factoryBean.setAction(null);
		factoryBean.setObjectSizer(mockObjectSizer);
		factoryBean.setThreshold(1024);
		factoryBean.setType(EvictionPolicyType.ENTRY_COUNT);
		factoryBean.afterPropertiesSet();

		GudEvictionAttributes evictionAttributes = factoryBean.getObject();

		assertThat(evictionAttributes).isNotNull();
		assertThat(evictionAttributes.getAction()).isEqualTo(GudEvictionAction.DEFAULT_EVICTION_ACTION);
		assertThat(evictionAttributes.getObjectSizer()).isNull();
		assertThat(evictionAttributes.getMaximum()).isEqualTo(1024);
		assertThat(evictionAttributes.getAlgorithm()).isEqualTo(GudEvictionAlgorithm.LRU_ENTRY);
	}

	@Test
	public void testCreateEntryCountEvictionAttributesWithLocalDestroy() {

		factoryBean.setAction(GudEvictionAction.LOCAL_DESTROY);
		factoryBean.setObjectSizer(mockObjectSizer);
		factoryBean.setThreshold(128);
		factoryBean.setType(EvictionPolicyType.ENTRY_COUNT);
		factoryBean.afterPropertiesSet();

		GudEvictionAttributes evictionAttributes = factoryBean.getObject();

		assertThat(evictionAttributes).isNotNull();
		assertThat(evictionAttributes.getAction()).isEqualTo(GudEvictionAction.LOCAL_DESTROY);
		assertThat(evictionAttributes.getObjectSizer()).isNull();
		assertThat(evictionAttributes.getMaximum()).isEqualTo(128);
		assertThat(evictionAttributes.getAlgorithm()).isEqualTo(GudEvictionAlgorithm.LRU_ENTRY);
	}

	@Test
	public void testCreateEntryCountEvictionAttributesWithNone() {

		factoryBean.setAction(GudEvictionAction.NONE);
		factoryBean.setObjectSizer(mockObjectSizer);
		factoryBean.setThreshold(null);
		factoryBean.setType(EvictionPolicyType.ENTRY_COUNT);
		factoryBean.afterPropertiesSet();

		GudEvictionAttributes evictionAttributes = factoryBean.getObject();

		assertThat(evictionAttributes).isNotNull();
		assertThat(evictionAttributes.getAction()).isEqualTo(GudEvictionAction.NONE);
		assertThat(evictionAttributes.getObjectSizer()).isNull();
		assertThat(evictionAttributes.getMaximum())
			.isEqualTo(EvictionAttributesFactoryBean.DEFAULT_LRU_MAXIMUM_ENTRIES);
		assertThat(evictionAttributes.getAlgorithm()).isEqualTo(GudEvictionAlgorithm.LRU_ENTRY);
	}

	@Test
	public void testCreateEntryCountEvictionAttributesWithOverflowToDisk() {

		factoryBean.setAction(GudEvictionAction.OVERFLOW_TO_DISK);
		factoryBean.setObjectSizer(mockObjectSizer);
		factoryBean.setThreshold(null);
		factoryBean.setType(EvictionPolicyType.ENTRY_COUNT);
		factoryBean.afterPropertiesSet();

		GudEvictionAttributes evictionAttributes = factoryBean.getObject();

		assertThat(evictionAttributes).isNotNull();
		assertThat(evictionAttributes.getAction()).isEqualTo(GudEvictionAction.OVERFLOW_TO_DISK);
		assertThat(evictionAttributes.getObjectSizer()).isNull();
		assertThat(evictionAttributes.getMaximum())
			.isEqualTo(EvictionAttributesFactoryBean.DEFAULT_LRU_MAXIMUM_ENTRIES);
		assertThat(evictionAttributes.getAlgorithm()).isEqualTo(GudEvictionAlgorithm.LRU_ENTRY);
	}

	@Test
	public void testCreateHeapPercentageEvictionAttributesWithNullAction() {

		factoryBean.setAction(null);
		factoryBean.setObjectSizer(mockObjectSizer);
		factoryBean.setType(EvictionPolicyType.HEAP_PERCENTAGE);
		factoryBean.afterPropertiesSet();

		GudEvictionAttributes evictionAttributes = factoryBean.getObject();

		assertThat(evictionAttributes).isNotNull();
		assertThat(evictionAttributes.getAction()).isEqualTo(GudEvictionAction.DEFAULT_EVICTION_ACTION);
		assertThat(evictionAttributes.getObjectSizer()).isSameAs(mockObjectSizer);
		assertThat(evictionAttributes.getAlgorithm()).isEqualTo(GudEvictionAlgorithm.LRU_HEAP);
	}

	@Test
	public void testCreateHeapPercentageEvictionAttributesWithLocalDestroy() {

		factoryBean.setAction(GudEvictionAction.LOCAL_DESTROY);
		factoryBean.setObjectSizer(null);
		factoryBean.setThreshold(null);
		factoryBean.setType(EvictionPolicyType.HEAP_PERCENTAGE);
		factoryBean.afterPropertiesSet();

		GudEvictionAttributes evictionAttributes = factoryBean.getObject();

		assertThat(evictionAttributes).isNotNull();
		assertThat(evictionAttributes.getAction()).isEqualTo(GudEvictionAction.LOCAL_DESTROY);
		assertThat(evictionAttributes.getObjectSizer()).isNull();
		assertThat(evictionAttributes.getAlgorithm()).isEqualTo(GudEvictionAlgorithm.LRU_HEAP);
	}

	@Test
	public void testCreateHeapPercentageEvictionAttributesWithNone() {

		factoryBean.setAction(GudEvictionAction.NONE);
		factoryBean.setObjectSizer(mockObjectSizer);
		factoryBean.setThreshold(null);
		factoryBean.setType(EvictionPolicyType.HEAP_PERCENTAGE);
		factoryBean.afterPropertiesSet();

		GudEvictionAttributes evictionAttributes = factoryBean.getObject();

		assertThat(evictionAttributes).isNotNull();
		assertThat(evictionAttributes.getAction()).isEqualTo(GudEvictionAction.NONE);
		assertThat(evictionAttributes.getObjectSizer()).isSameAs(mockObjectSizer);
		assertThat(evictionAttributes.getAlgorithm()).isEqualTo(GudEvictionAlgorithm.LRU_HEAP);
	}

	@Test
	public void testCreateHeapPercentageEvictionAttributesWithOverflowToDisk() {

		factoryBean.setAction(GudEvictionAction.OVERFLOW_TO_DISK);
		factoryBean.setObjectSizer(mockObjectSizer);
		factoryBean.setThreshold(null);
		factoryBean.setType(EvictionPolicyType.HEAP_PERCENTAGE);
		factoryBean.afterPropertiesSet();

		GudEvictionAttributes evictionAttributes = factoryBean.getObject();

		assertThat(evictionAttributes).isNotNull();
		assertThat(evictionAttributes.getAction()).isEqualTo(GudEvictionAction.OVERFLOW_TO_DISK);
		assertThat(evictionAttributes.getObjectSizer()).isSameAs(mockObjectSizer);
		assertThat(evictionAttributes.getAlgorithm()).isEqualTo(GudEvictionAlgorithm.LRU_HEAP);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateHeapPercentageEvictionAttributesSettingThreshold() {

		EvictionAttributesFactoryBean factoryBean = new EvictionAttributesFactoryBean();

		try {
			factoryBean.setType(EvictionPolicyType.HEAP_PERCENTAGE);
			factoryBean.setThreshold(85);
			factoryBean.afterPropertiesSet();
		}
		catch (IllegalArgumentException expected) {
			assertThat(expected.getMessage())
				.isEqualTo("HEAP_PERCENTAGE (LRU_HEAP algorithm) does not support threshold (a.k.a. maximum)");
			assertThat(factoryBean.getThreshold().intValue()).isEqualTo(85);
			assertThat(factoryBean.getType()).isEqualTo(EvictionPolicyType.HEAP_PERCENTAGE);
			throw expected;
		}
	}

	@Test
	public void testCreateMemorySizeEvictionAttributesWithNullAction() {

		factoryBean.setAction(null);
		factoryBean.setObjectSizer(mockObjectSizer);
		factoryBean.setThreshold(null);
		factoryBean.setType(EvictionPolicyType.MEMORY_SIZE);
		factoryBean.afterPropertiesSet();

		GudEvictionAttributes evictionAttributes = factoryBean.getObject();

		assertThat(evictionAttributes).isNotNull();
		assertThat(evictionAttributes.getAction()).isEqualTo(GudEvictionAction.DEFAULT_EVICTION_ACTION);
		assertThat(evictionAttributes.getObjectSizer()).isSameAs(mockObjectSizer);
		assertThat(evictionAttributes.getMaximum())
			.isEqualTo(EvictionAttributesFactoryBean.DEFAULT_MEMORY_MAXIMUM_SIZE);
		assertThat(evictionAttributes.getAlgorithm()).isEqualTo(GudEvictionAlgorithm.LRU_MEMORY);
	}

	@Test
	public void testCreateMemorySizeEvictionAttributesWithLocalDestroy() {

		factoryBean.setAction(GudEvictionAction.LOCAL_DESTROY);
		factoryBean.setObjectSizer(mockObjectSizer);
		factoryBean.setThreshold(1024);
		factoryBean.setType(EvictionPolicyType.MEMORY_SIZE);
		factoryBean.afterPropertiesSet();

		GudEvictionAttributes evictionAttributes = factoryBean.getObject();

		assertThat(evictionAttributes).isNotNull();
		assertThat(evictionAttributes.getAction()).isEqualTo(GudEvictionAction.LOCAL_DESTROY);
		assertThat(evictionAttributes.getObjectSizer()).isSameAs(mockObjectSizer);
		assertThat(evictionAttributes.getMaximum()).isEqualTo(1024);
		assertThat(evictionAttributes.getAlgorithm()).isEqualTo(GudEvictionAlgorithm.LRU_MEMORY);
	}

	@Test
	public void testCreateMemorySizeEvictionAttributesWithNone() {

		factoryBean.setAction(GudEvictionAction.NONE);
		factoryBean.setObjectSizer(null);
		factoryBean.setThreshold(256);
		factoryBean.setType(EvictionPolicyType.MEMORY_SIZE);
		factoryBean.afterPropertiesSet();

		GudEvictionAttributes evictionAttributes = factoryBean.getObject();

		assertThat(evictionAttributes).isNotNull();
		assertThat(evictionAttributes.getAction()).isEqualTo(GudEvictionAction.NONE);
		assertThat(evictionAttributes.getObjectSizer()).isNull();
		assertThat(evictionAttributes.getMaximum()).isEqualTo(256);
		assertThat(evictionAttributes.getAlgorithm()).isEqualTo(GudEvictionAlgorithm.LRU_MEMORY);
	}

	@Test
	public void testCreateMemorySizeEvictionAttributesWithOverflowToDisk() {

		factoryBean.setAction(GudEvictionAction.OVERFLOW_TO_DISK);
		factoryBean.setObjectSizer(null);
		factoryBean.setThreshold(null);
		factoryBean.setType(EvictionPolicyType.MEMORY_SIZE);
		factoryBean.afterPropertiesSet();

		GudEvictionAttributes evictionAttributes = factoryBean.getObject();

		assertThat(evictionAttributes).isNotNull();
		assertThat(evictionAttributes.getAction()).isEqualTo(GudEvictionAction.OVERFLOW_TO_DISK);
		assertThat(evictionAttributes.getObjectSizer()).isNull();
		assertThat(evictionAttributes.getMaximum())
			.isEqualTo(EvictionAttributesFactoryBean.DEFAULT_MEMORY_MAXIMUM_SIZE);
		assertThat(evictionAttributes.getAlgorithm()).isEqualTo(GudEvictionAlgorithm.LRU_MEMORY);
	}
}
