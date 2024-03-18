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
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionFactory;
import org.apache.geode.cache.RegionShortcut;

import org.springframework.data.gemfire.test.support.AbstractRegionFactoryBeanTests;

/**
 * Unit Tests for {@link LocalRegionFactoryBean}.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.DataPolicy
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.RegionFactory
 * @see org.apache.geode.cache.RegionShortcut
 * @see org.springframework.data.gemfire.LocalRegionFactoryBean
 * @since 1.3.x
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class LocalRegionFactoryBeanUnitTests extends AbstractRegionFactoryBeanTests {

	private final LocalRegionFactoryBean factoryBean = new LocalRegionFactoryBean();

	private RegionFactoryBeanConfig defaultConfig() {

		return new RegionFactoryBeanConfig(new LocalRegionFactoryBean(), "default") {

			@Override
			public void configureRegionFactoryBean() { }

			@Override
			public void verify() {

				Region region = regionFactoryBean.getRegion();

				assertThat(region).isNotNull();
				assertThat(region.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.DEFAULT);
			}
		};
	}

	private RegionFactoryBeanConfig invalidConfig() {

		return new RegionFactoryBeanConfig(new LocalRegionFactoryBean(), "local-replicate") {

			@Override
			public void configureRegionFactoryBean() {
				regionFactoryBean.setDataPolicy(DataPolicy.REPLICATE);
			}

			@Override
			public void verify() {
				assertThat(this.exception).isNotNull();
			}
		};
	}

	@Override
	protected void createRegionFactoryBeanConfigs() {
		add(defaultConfig());
		add(invalidConfig());
	}

	protected RegionFactory<?, ?> createMockRegionFactory() {
		return mock(RegionFactory.class);
	}

	@Test
	public void testResolveDataPolicyWhenPersistentUnspecifiedAndDataPolicyUnspecified() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		factoryBean.resolveDataPolicy(mockRegionFactory, null, (String) null);

		verify(mockRegionFactory).setDataPolicy(eq(DataPolicy.NORMAL));
	}

	@Test
	public void testResolveDataPolicyWhenNotPersistentAndDataPolicyUnspecified() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		factoryBean.setPersistent(false);
		factoryBean.resolveDataPolicy(mockRegionFactory, false, (String) null);

		verify(mockRegionFactory).setDataPolicy(eq(DataPolicy.NORMAL));
	}

	@Test
	public void testResolveDataPolicyWhenPersistentAndDataPolicyUnspecified() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		factoryBean.setPersistent(true);
		factoryBean.resolveDataPolicy(mockRegionFactory, true, (String) null);

		verify(mockRegionFactory).setDataPolicy(eq(DataPolicy.PERSISTENT_REPLICATE));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveDataPolicyWithBlankDataPolicy() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		try {
			factoryBean.resolveDataPolicy(mockRegionFactory, null, "  ");
		}
		catch (IllegalArgumentException e) {
			assertThat(e.getMessage()).isEqualTo("Data Policy [  ] is invalid");
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
			assertThat(e.getMessage()).isEqualTo("Data Policy [] is invalid");
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
			assertThat(e.getMessage()).isEqualTo("Data Policy [INVALID_DATA_POLICY_NAME] is invalid");
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
	public void testResolveDataPolicyWithInvalidDataPolicyType() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		try {
			factoryBean.resolveDataPolicy(mockRegionFactory, null, "PARTITION");
		}
		catch (IllegalArgumentException e) {
			assertThat(e.getMessage()).isEqualTo("Data Policy [PARTITION] is not supported for Local Regions");
			throw e;
		}
		finally {
			verify(mockRegionFactory, never()).setDataPolicy(null);
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.NORMAL));
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.PERSISTENT_REPLICATE));
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.PRELOADED));
		}
	}

	@Test
	public void testResolveDataPolicyWhenPersistentUnspecifiedAndNormalDataPolicy() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		factoryBean.resolveDataPolicy(mockRegionFactory, null, "Normal");

		verify(mockRegionFactory).setDataPolicy(eq(DataPolicy.NORMAL));
	}

	@Test
	public void testResolveDataPolicyWhenNotPersistentAndNormalDataPolicy() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		factoryBean.setPersistent(false);
		factoryBean.resolveDataPolicy(mockRegionFactory, false, "NORMAL");

		verify(mockRegionFactory).setDataPolicy(eq(DataPolicy.NORMAL));
	}

	@Test
	public void testResolveDataPolicyWhenPersistentAndNormalDataPolicy() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		factoryBean.setPersistent(true);
		factoryBean.resolveDataPolicy(mockRegionFactory, true, "NORMAL");

		verify(mockRegionFactory).setDataPolicy(eq(DataPolicy.PERSISTENT_REPLICATE));
	}

	@Test
	public void testResolveDataPolicyWhenPersistentUnspecifiedAndPreloadedDataPolicy() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		factoryBean.resolveDataPolicy(mockRegionFactory, null, "preloaded");

		verify(mockRegionFactory).setDataPolicy(eq(DataPolicy.PRELOADED));
	}

	@Test
	public void testResolveDataPolicyWhenNotPersistentAndPreloadedDataPolicy() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		factoryBean.setPersistent(false);
		factoryBean.resolveDataPolicy(mockRegionFactory, false, "PreLoaded");

		verify(mockRegionFactory).setDataPolicy(eq(DataPolicy.PRELOADED));
	}

	@Test
	public void testResolveDataPolicyWhenPersistentAndPreloadedDataPolicy() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		factoryBean.setPersistent(true);
		factoryBean.resolveDataPolicy(mockRegionFactory, true, "PRELOADED");

		verify(mockRegionFactory).setDataPolicy(eq(DataPolicy.PERSISTENT_REPLICATE));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveDataPolicyWhenShortcutIsNullAndPersistentReplicateDataPolicy() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		try {
			factoryBean.setShortcut(null);
			factoryBean.resolveDataPolicy(mockRegionFactory, null, DataPolicy.PERSISTENT_REPLICATE);
		}
		catch (IllegalArgumentException expected) {
			assertThat(expected.getMessage())
				.isEqualTo("Data Policy [PERSISTENT_REPLICATE] is not supported for Local Regions");
			throw expected;
		}
		finally {
			verify(mockRegionFactory, never()).setDataPolicy(null);
			verify(mockRegionFactory, never()).setDataPolicy(DataPolicy.NORMAL);
			verify(mockRegionFactory, never()).setDataPolicy(DataPolicy.PRELOADED);
			verify(mockRegionFactory, never()).setDataPolicy(DataPolicy.PERSISTENT_REPLICATE);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveDataPolicyWhenShortcutNotPersistentAndPersistentReplicateDataPolicy() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		try {
			factoryBean.setShortcut(RegionShortcut.LOCAL_OVERFLOW);
			factoryBean.resolveDataPolicy(mockRegionFactory, true, DataPolicy.PERSISTENT_REPLICATE);
		}
		catch (IllegalArgumentException expected) {
			assertThat(expected.getMessage())
				.isEqualTo("Data Policy [PERSISTENT_REPLICATE] is not supported for Local Regions");
			throw expected;
		}
		finally {
			verify(mockRegionFactory, never()).setDataPolicy(null);
			verify(mockRegionFactory, never()).setDataPolicy(DataPolicy.NORMAL);
			verify(mockRegionFactory, never()).setDataPolicy(DataPolicy.PRELOADED);
			verify(mockRegionFactory, never()).setDataPolicy(DataPolicy.PERSISTENT_REPLICATE);
		}
	}

	@Test
	public void testResolveDataPolicyWhenPersistentPersistentReplicateDataPolicy() {

		RegionFactory mockRegionFactory = createMockRegionFactory();

		factoryBean.setShortcut(RegionShortcut.LOCAL_PERSISTENT_OVERFLOW);
		factoryBean.resolveDataPolicy(mockRegionFactory, false, DataPolicy.PERSISTENT_REPLICATE);

		verify(mockRegionFactory).setDataPolicy(DataPolicy.PERSISTENT_REPLICATE);
	}
}
