/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.util.ArrayUtils;

import java.util.Arrays;

/**
 * The {@link DiskStoresConfiguration} class is a Spring {@link org.springframework.context.annotation.ImportBeanDefinitionRegistrar}
 * used to register multiple GemFire/Geode {@link org.apache.geode.cache.DiskStore} bean definitions.
 *
 * @author John Blum
 * @see BeanDefinitionRegistry
 * @see DiskStoreConfiguration
 * @see EnableDiskStore
 * @see EnableDiskStores
 * @see org.apache.geode.cache.DiskStore
 * @since 1.9.0
 */
public class DiskStoresConfiguration extends DiskStoreConfiguration {

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

		if (importingClassMetadata.hasAnnotation(EnableDiskStores.class.getName())) {

			AnnotationAttributes enableDiskStoresAttributes =
				getAnnotationAttributes(importingClassMetadata, EnableDiskStores.class.getName());

			AnnotationAttributes[] diskStores =
				enableDiskStoresAttributes.getAnnotationArray("diskStores");

			Arrays.stream(ArrayUtils.nullSafeArray(diskStores, AnnotationAttributes.class))
				.forEach(diskStoreAttributes ->  registerDiskStoreBeanDefinition(
					mergeDiskStoreAttributes(enableDiskStoresAttributes, diskStoreAttributes), registry));
		}
	}

	protected AnnotationAttributes mergeDiskStoreAttributes(AnnotationAttributes enableDiskStoresAttributes,
			AnnotationAttributes diskStoreAttributes) {

		setAttributeIfNotDefault(diskStoreAttributes, "autoCompact",
			enableDiskStoresAttributes.getBoolean("autoCompact"), false);

		setAttributeIfNotDefault(diskStoreAttributes, "compactionThreshold",
			enableDiskStoresAttributes.<Integer>getNumber("compactionThreshold"), 50);

		setAttributeIfNotDefault(diskStoreAttributes, "maxOplogSize",
			enableDiskStoresAttributes.<Long>getNumber("maxOplogSize"), 1024L);

		return diskStoreAttributes;
	}

	private <T> void setAttributeIfNotDefault(AnnotationAttributes diskStoreAttributes,
			String attributeName, T newValue, T defaultValue) {

		if (!diskStoreAttributes.containsKey(attributeName)
				|| toString(diskStoreAttributes.get(attributeName)).equals(toString(defaultValue))) {

			diskStoreAttributes.put(attributeName, newValue);
		}
	}

	private String toString(Object value) {
		return String.valueOf(value);
	}
}
