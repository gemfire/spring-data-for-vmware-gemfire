/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.test.support;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import org.apache.geode.cache.GemFireCache;

import org.springframework.data.gemfire.PeerRegionFactoryBean;
import org.springframework.data.gemfire.tests.mock.GemFireMockObjectsSupport;

/**
 * @author David Turanski
 * @author John Blum
 */
public abstract class AbstractRegionFactoryBeanTests {

	private GemFireCache cache;

	private final Map<String, RegionFactoryBeanConfig> regionFactoryBeanConfigs = new HashMap<>();

	@AfterClass
	public static void cleanup() {

		for (String name : new File(".").list((dir, name1) -> name1.startsWith("BACKUP"))) {
			new File(name).delete();
		}

		GemFireMockObjectsSupport.destroy();
	}

	@Before
	public void setUp() throws Exception {
		this.cache = GemFireMockObjectsSupport.mockPeerCache();
	}

	@After
	public void tearDown() {
		this.cache.close();
		this.cache = null;
	}

	@Test
	public void testAll() {

		createRegionFactoryBeanConfigs();

		for (RegionFactoryBeanConfig regionFactoryBeanConfig : this.regionFactoryBeanConfigs.values()) {
			regionFactoryBeanConfig.test();
		}
	}

	protected void add(RegionFactoryBeanConfig regionFactoryBeanConfig) {

		if (this.regionFactoryBeanConfigs.containsKey(regionFactoryBeanConfig.regionName)) {
			throw new RuntimeException("duplicate region name " + regionFactoryBeanConfig.regionName);
		}

		this.regionFactoryBeanConfigs.put(regionFactoryBeanConfig.regionName, regionFactoryBeanConfig);
	}

	protected abstract void createRegionFactoryBeanConfigs();

	public abstract class RegionFactoryBeanConfig {

		public Exception exception;

		@SuppressWarnings("rawtypes")
		public final PeerRegionFactoryBean regionFactoryBean;

		public final String regionName;

		@SuppressWarnings("rawtypes")
		public RegionFactoryBeanConfig(PeerRegionFactoryBean regionFactoryBean, String regionName) {

			this.regionFactoryBean = regionFactoryBean;
			this.regionName = regionName;
			regionFactoryBean.setBeanName(regionName);
			regionFactoryBean.setCache(cache);
		}

		public abstract void configureRegionFactoryBean();

		public void test() {

			configureRegionFactoryBean();

			try {
				regionFactoryBean.afterPropertiesSet();
			}
			catch (Exception e) {
				this.exception = e;
			}

			verify();
		}

		public abstract void verify();

	}
}
