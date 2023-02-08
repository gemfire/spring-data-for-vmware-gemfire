/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.Serializable;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.DiskStore;
import org.apache.geode.cache.Region;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

/**
 * Integration Tests for PDX {@link DiskStore}
 * and to reproduce the issue in JIRA SGF-197.
 *
 * @author John Blum
 * @see File
 * @see org.junit.Test
 * @see DiskStore
 * @see Region
 * @see DiskStoreFactoryBean
 * @see IntegrationTestsSupport
 * @see org.springframework.test.annotation.DirtiesContext
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @link https://jira.springsource.org/browse/SGF-197
 * @since 1.3.3
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SuppressWarnings("unused")
public class PdxDiskStoreIntegrationTests extends IntegrationTestsSupport {

	protected static final int NUMBER_OF_REGION_ENTRIES = 1000;

	@Autowired
	@Qualifier("pdxDataRegion")
	private Region<KeyHolder<String>, ValueHolder<Integer>> pdxDataRegion;

	protected static void assertRegionExists(String expectedRegionName, String expectedRegionPath, Region<?, ?> region) {

		assertThat(region).isNotNull();

		assertThat(region.getName())
			.describedAs("Expected Region with name %1$s; but was %2$s", expectedRegionName, region.getName())
			.isEqualTo(expectedRegionName);

		assertThat(region.getFullPath())
			.describedAs("Expected Region with path %1$s; but was %2$s", expectedRegionPath, region.getFullPath())
			.isEqualTo(expectedRegionPath);
	}

	private static File createFile(String pathname) {
		return new File(pathname);
	}

	private static void deleteRecursive(File path) {

		if (path.isDirectory()) {
			for (File file : path.listFiles()) {
				deleteRecursive(file);
			}
		}

		path.delete();
	}

	@BeforeClass
	public static void setupBeforeClass() {

		assertThat(createDirectory(createFile("./gemfire/data-store")).isDirectory()).isTrue();
		assertThat(createDirectory(createFile("./gemfire/pdx-store")).isDirectory()).isTrue();
	}

	@AfterClass
	public static void cleanupAfterClass() {
		deleteRecursive(createFile("./gemfire"));
	}

	@Before
	public void setup() {

		assertThat(pdxDataRegion).as("The PdxData GemFire Region was not created successfully").isNotNull();

		if (pdxDataRegion.size() == 0) {
			for (int index = 1; index <= NUMBER_OF_REGION_ENTRIES; index++) {
				pdxDataRegion.put(new KeyHolder<>("key" + index), new ValueHolder<>(index));
			}
		}
	}

	@Test
	public void testPersistentRegionWithDataCreation() {

		assertRegionExists("PdxData", "/PdxData", pdxDataRegion);
		assertThat(pdxDataRegion.size()).isEqualTo(NUMBER_OF_REGION_ENTRIES);
	}

	@Test
	public void testPersistentRegionWithDataRecovery() {

		assertRegionExists("PdxData", "/PdxData", pdxDataRegion);
		assertThat(pdxDataRegion.size()).isEqualTo(NUMBER_OF_REGION_ENTRIES);
	}

	protected static class AbstractHolderSupport {

		protected static boolean equals(Object obj1, Object obj2) {
			return obj1 != null && obj1.equals(obj2);
		}

		protected static int hashCode(Object obj) {
			return obj == null ? 0 : obj.hashCode();
		}
	}

	@SuppressWarnings("unused")
	public static class KeyHolder<T extends Serializable> extends AbstractHolderSupport {

		private T key;

		public KeyHolder() { }

		public KeyHolder(T key) {

			Assert.notNull(key, "The key cannot be null");

			this.key = key;
		}

		public T getKey() {
			return key;
		}

		public void setKey(T key) {
			this.key = key;
		}

		@Override
		public boolean equals(Object obj) {

			if (this == obj) {
				return true;
			}

			if (!(obj instanceof KeyHolder)) {
				return false;
			}

			KeyHolder<?> that = (KeyHolder<?>) obj;

			return equals(this.getKey(), that.getKey());
		}

		@Override
		public int hashCode() {

			int hashValue = 17;

			hashValue = 37 * hashValue + hashCode(this.getKey());

			return hashValue;
		}

		@Override
		public String toString() {
			return String.valueOf(getKey());
		}
	}

	@SuppressWarnings("unused")
	public static class ValueHolder<T extends Serializable> extends AbstractHolderSupport {

		private T value;

		public ValueHolder() { }

		public ValueHolder(T value) {
			this.value = value;
		}

		public T getValue() {
			return value;
		}

		public void setValue(T value) {
			this.value = value;
		}

		@Override
		public boolean equals(Object obj) {

			if (this == obj) {
				return true;
			}

			if (!(obj instanceof ValueHolder)) {
				return false;
			}

			ValueHolder<?> that = (ValueHolder<?>) obj;

			return equals(this.getValue(), that.getValue());
		}

		@Override
		public int hashCode() {

			int hashValue = 17;

			hashValue = 37 * hashValue + hashCode(this.getValue());

			return hashValue;
		}

		@Override
		public String toString() {
			return String.valueOf(getValue());
		}
	}
}
