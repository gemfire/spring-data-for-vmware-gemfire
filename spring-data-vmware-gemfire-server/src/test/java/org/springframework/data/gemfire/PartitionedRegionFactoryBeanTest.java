/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.RegionFactory;

/**
 * Unit Tests for {@link PartitionedRegionFactoryBean}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.RegionFactory
 * @see org.springframework.data.gemfire.PartitionedRegionFactoryBean
 * @since 1.3.3
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PartitionedRegionFactoryBeanTest {

	private final PartitionedRegionFactoryBean factoryBean = new PartitionedRegionFactoryBean();

	protected RegionFactory<?, ?> createMockRegionFactory() {
		return mock(RegionFactory.class);
	}

	@Test
	public void testResolveDataPolicyWithPersistentUnspecifiedAndDataPolicyUnspecified() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		factoryBean.resolveDataPolicy(mockRegionFactory, null, (String) null);

		verify(mockRegionFactory).setDataPolicy(eq(DataPolicy.PARTITION));
	}

	@Test
	public void testResolveDataPolicyWhenNotPersistentAndDataPolicyUnspecified() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		factoryBean.setPersistent(false);
		factoryBean.resolveDataPolicy(mockRegionFactory, false, (String) null);

		verify(mockRegionFactory).setDataPolicy(eq(DataPolicy.PARTITION));
	}

	@Test
	public void testResolveDataPolicyWhenPersistentAndDataPolicyUnspecified() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		factoryBean.setPersistent(true);
		factoryBean.resolveDataPolicy(mockRegionFactory, true, (String) null);

		verify(mockRegionFactory).setDataPolicy(eq(DataPolicy.PERSISTENT_PARTITION));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveDataPolicyWithBlankDataPolicy() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		try {
			factoryBean.resolveDataPolicy(mockRegionFactory, null, "  ");
		}
		catch (IllegalArgumentException e) {
			assertThat(e.getMessage()).isEqualTo("Data Policy [  ] is invalid.");
			throw e;
		}
		finally {
			verify(mockRegionFactory, never()).setDataPolicy(null);
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.NORMAL));
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.PERSISTENT_REPLICATE));
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.PRELOADED));
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveDataPolicyWithEmptyDataPolicy() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		try {
			factoryBean.resolveDataPolicy(mockRegionFactory, null, "");
		}
		catch (IllegalArgumentException e) {
			assertThat(e.getMessage()).isEqualTo("Data Policy [] is invalid.");
			throw e;
		}
		finally {
			verify(mockRegionFactory, never()).setDataPolicy(null);
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.NORMAL));
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.PERSISTENT_REPLICATE));
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.PRELOADED));
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveDataPolicyWithInvalidDataPolicyName() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		try {
			factoryBean.resolveDataPolicy(mockRegionFactory, null, "INVALID_DATA_POLICY_NAME");
		}
		catch (IllegalArgumentException e) {
			assertThat(e.getMessage()).isEqualTo("Data Policy [INVALID_DATA_POLICY_NAME] is invalid.");
			throw e;
		}
		finally {
			verify(mockRegionFactory, never()).setDataPolicy(null);
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.PARTITION));
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.PERSISTENT_PARTITION));
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveDataPolicyWithInvalidDataPolicyType() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		try {
			factoryBean.resolveDataPolicy(mockRegionFactory, null, "REPLICATE");
		}
		catch (IllegalArgumentException e) {
			assertThat(e.getMessage()).isEqualTo("Data Policy [REPLICATE] is not supported in Partitioned Regions.");
			throw e;
		}
		finally {
			verify(mockRegionFactory, never()).setDataPolicy(null);
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.REPLICATE));
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.PARTITION));
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.PERSISTENT_PARTITION));
		}
	}

	@Test
	public void testResolveDataPolicyWhenPersistentUnspecifiedAndPartitionDataPolicy() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		factoryBean.resolveDataPolicy(mockRegionFactory, null, "PARTITION");

		verify(mockRegionFactory).setDataPolicy(eq(DataPolicy.PARTITION));
	}

	@Test
	public void testResolveDataPolicyWhenNotPersistentAndPartitionDataPolicy() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		factoryBean.setPersistent(false);
		factoryBean.resolveDataPolicy(mockRegionFactory, false, "PARTITION");

		verify(mockRegionFactory).setDataPolicy(eq(DataPolicy.PARTITION));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveDataPolicyWhenPersistentAndPartitionDataPolicy() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		try {
			factoryBean.setPersistent(true);
			factoryBean.resolveDataPolicy(mockRegionFactory, true, "PARTITION");
		}
		catch (IllegalArgumentException e) {
			assertThat(e.getMessage()).isEqualTo("Data Policy [PARTITION] is not valid when persistent is true");
			throw e;
		}
		finally {
			verify(mockRegionFactory, never()).setDataPolicy(null);
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.PARTITION));
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.PERSISTENT_PARTITION));
		}
	}

	@Test
	public void testResolveDataPolicyWhenPersistentUnspecifiedAndPersistentPartitionDataPolicy() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		factoryBean.resolveDataPolicy(mockRegionFactory, null, "PERSISTENT_PARTITION");

		verify(mockRegionFactory).setDataPolicy(eq(DataPolicy.PERSISTENT_PARTITION));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveDataPolicyWhenNotPersistentAndPersistentPartitionedDataPolicy() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		try {
			factoryBean.setPersistent(false);
			factoryBean.resolveDataPolicy(mockRegionFactory, false, "PERSISTENT_PARTITION");
		}
		catch (IllegalArgumentException e) {
			assertThat(e.getMessage())
				.isEqualTo("Data Policy [PERSISTENT_PARTITION] is not valid when persistent is false");
			throw e;
		}
		finally {
			verify(mockRegionFactory, never()).setDataPolicy(null);
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.PARTITION));
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.PERSISTENT_PARTITION));
		}
	}

	@Test
	public void testResolveDataPolicyWhenPersistentAndPersistentPartitionedDataPolicy() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		factoryBean.setPersistent(true);
		factoryBean.resolveDataPolicy(mockRegionFactory, true, "PERSISTENT_PARTITION");

		verify(mockRegionFactory).setDataPolicy(eq(DataPolicy.PERSISTENT_PARTITION));
	}
}
