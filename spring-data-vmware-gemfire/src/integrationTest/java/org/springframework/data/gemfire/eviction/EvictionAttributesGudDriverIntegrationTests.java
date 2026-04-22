/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-17: Integration coverage for GudEvictionAttributes / EvictionAttributesFactoryBean with real GemFire 10.3 driver
 */

package org.springframework.data.gemfire.eviction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.apache.geode.cache.util.ObjectSizer;
import org.junit.BeforeClass;
import org.junit.Test;

import org.springframework.data.gemfire.gud.api.GudEvictionAction;
import org.springframework.data.gemfire.gud.api.GudEvictionAlgorithm;
import org.springframework.data.gemfire.gud.api.GudEvictionAttributes;
import org.springframework.data.gemfire.gud.api.GudObjectSizer;
import org.springframework.data.gemfire.gud.core.GudDriver;
import org.springframework.data.gemfire.gud.core.GudDriverManager;
import org.springframework.data.gemfire.gud.driver.GemFireDriver;
import org.springframework.data.gemfire.gud.driver.GemFireObjectSizerWrapper;

/**
 * Confirms eviction-attribute creation against the native GemFire driver (integrationTest classpath).
 * Unit tests in {@link EvictionAttributesFactoryBeanTest} use {@code gud-driver-mock}; this class validates
 * the same contracts with {@link GemFireDriver}.
 */
public class EvictionAttributesGudDriverIntegrationTests {

	@BeforeClass
	public static void loadDrivers() {
		GudDriverManager.loadDrivers();
	}

	/**
	 * Resolves whichever {@link GemFireDriver} is on the integrationTest classpath
	 * (see {@code gudIntegrationDriver} Gradle property; default {@code 10.3}).
	 */
	private static GudDriver gemfireDriver() {
		return GudDriverManager.getAllDrivers().stream()
			.filter(GemFireDriver.class::isInstance)
			.findFirst()
			.orElseThrow(() -> new IllegalStateException(
				"No GemFireDriver on integrationTest classpath; check gudIntegrationDriver dependency"));
	}

	@Test
	public void driverCreatesLruEntryEvictionAttributes() {
		GudDriver driver = gemfireDriver();
		GudEvictionAttributes attrs = driver.createLruEntryEvictionAttributes(2048, GudEvictionAction.OVERFLOW_TO_DISK);
		assertThat(attrs.getAlgorithm()).isEqualTo(GudEvictionAlgorithm.LRU_ENTRY);
		assertThat(attrs.getMaximum()).isEqualTo(2048);
		assertThat(attrs.getAction()).isEqualTo(GudEvictionAction.OVERFLOW_TO_DISK);
		assertThat(attrs.getObjectSizer()).isNull();
	}

	@Test
	public void driverCreatesLruHeapEvictionAttributes() {
		GudDriver driver = gemfireDriver();
		ObjectSizer nativeSizer = mock(ObjectSizer.class);
		GudObjectSizer sizer = new GemFireObjectSizerWrapper(nativeSizer);
		GudEvictionAttributes attrs = driver.createLruHeapEvictionAttributes(sizer, GudEvictionAction.LOCAL_DESTROY);
		assertThat(attrs.getAlgorithm()).isEqualTo(GudEvictionAlgorithm.LRU_HEAP);
		assertThat(attrs.getAction()).isEqualTo(GudEvictionAction.LOCAL_DESTROY);
		assertThat(attrs.getObjectSizer()).isSameAs(sizer);
	}

	@Test
	public void factoryBeanMatchesDriverForEntryCount() throws Exception {
		GudDriver driver = gemfireDriver();
		EvictionAttributesFactoryBean factoryBean = new EvictionAttributesFactoryBean();
		factoryBean.setAction(GudEvictionAction.LOCAL_DESTROY);
		factoryBean.setThreshold(512);
		factoryBean.setType(EvictionPolicyType.ENTRY_COUNT);
		factoryBean.afterPropertiesSet();

		GudEvictionAttributes expected = driver.createLruEntryEvictionAttributes(512, GudEvictionAction.LOCAL_DESTROY);
		GudEvictionAttributes actual = factoryBean.getObject();
		assertThat(actual.getAlgorithm()).isEqualTo(expected.getAlgorithm());
		assertThat(actual.getAction()).isEqualTo(expected.getAction());
		assertThat(actual.getMaximum()).isEqualTo(expected.getMaximum());
	}
}
