/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit Tests for {@link DiskStoreFactoryBean}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.DiskStore
 * @see DiskStoreFactoryBean
 * @since 1.3.4
 */
public class DiskStoreFactoryBeanUnitTests {

	private final DiskStoreFactoryBean factoryBean = new DiskStoreFactoryBean();

	@Before
	public void setup() {
		factoryBean.setBeanName("testDiskStore");
	}

	@Test
	public void testValidateCompactionThresholdWhenNull() {
		factoryBean.validateCompactionThreshold(null);
	}

	@Test
	public void testValidateCompactionThresholdWhenValid() {

		factoryBean.validateCompactionThreshold(0);
		factoryBean.validateCompactionThreshold(50);
		factoryBean.validateCompactionThreshold(100);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateCompactionThresholdWhenLessThan0() {

		try {
			factoryBean.validateCompactionThreshold(-1);
		}
		catch (IllegalArgumentException expected) {
			assertThat(expected.getMessage()).isEqualTo(String.format(
				"The DiskStore's (%1$s) compaction threshold (%2$d) must be an integer value between 0 and 100 inclusive.",
				factoryBean.resolveDiskStoreName(), -1));
			throw expected;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateCompactionThresholdWhenGreaterThan100() {

		try {
			factoryBean.validateCompactionThreshold(101);
		}
		catch (IllegalArgumentException expected) {
			assertThat(expected.getMessage()).isEqualTo(String.format(
				"The DiskStore's (%1$s) compaction threshold (%2$d) must be an integer value between 0 and 100 inclusive.",
				factoryBean.resolveDiskStoreName(), 101));
			throw expected;
		}
	}

	@Test
	public void testSetCompactionThreshold() {
		factoryBean.setCompactionThreshold(75);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetCompactionThreadWithIllegalArgument() {

		try {
			factoryBean.setCompactionThreshold(200);
		}
		catch (IllegalArgumentException expected) {
			assertThat(expected.getMessage()).isEqualTo(String.format(
				"The DiskStore's (%1$s) compaction threshold (%2$d) must be an integer value between 0 and 100 inclusive.",
				factoryBean.resolveDiskStoreName(), 200));
			throw expected;
		}
	}

	@Test(expected = IllegalStateException.class)
	public void testAfterPropertiesSetWithNoCacheReference() throws Exception {

		try {
			factoryBean.afterPropertiesSet();
		}
		catch (IllegalStateException expected) {
			assertThat(expected.getMessage()).isEqualTo("Cache is required to create DiskStore [testDiskStore]");
			throw expected;
		}
	}
}
