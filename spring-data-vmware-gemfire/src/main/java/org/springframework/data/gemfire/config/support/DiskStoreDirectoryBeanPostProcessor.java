/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.support;

import java.io.File;
import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.gemfire.DiskStoreFactoryBean.DiskDir;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * The {@link DiskStoreDirectoryBeanPostProcessor} processes any GemFire {@link org.apache.geode.cache.DiskStore},
 * {@link DiskDir} Spring beans defined in the application context to ensure that the directory actually exists
 * before creating the {@link org.apache.geode.cache.DiskStore}.
 *
 * @author John Blum
 * @see BeanPostProcessor
 * @see org.apache.geode.cache.DiskStore
 * @since 1.5.0
 */
@SuppressWarnings("unused")
public class DiskStoreDirectoryBeanPostProcessor implements BeanPostProcessor {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

		if (logger.isDebugEnabled()) {
			logger.debug("Processing Bean [{}] of Type [{}] with Name [{}] before initialization%n",
				bean, ObjectUtils.nullSafeClassName(bean), beanName);
		}

		if (bean instanceof DiskDir) {
			createIfNotExists((DiskDir) bean);
		}

		return bean;
	}

	/* (non-Javadoc) */
	private void createIfNotExists(DiskDir diskDirectory) {

		String location = readField(diskDirectory, "location");

		File diskDirectoryFile = new File(location);

		Assert.isTrue(diskDirectoryFile.isDirectory() || diskDirectoryFile.mkdirs(),
			String.format("Failed to create Disk Directory [%s]%n", location));

		if (logger.isInfoEnabled()) {
			logger.info("Disk Directory is @ Location [{}].%n", location);
		}
	}

	/* (non-Javadoc) */
	@SuppressWarnings("unchecked")
	private <T> T readField(Object obj, String fieldName) {

		try {

			Class type = obj.getClass();
			Field field;

			do {
				field = type.getDeclaredField(fieldName);
				type = type.getSuperclass();
			}
			while (field == null && !Object.class.equals(type));

			if (field == null) {
				throw new NoSuchFieldException(String.format("No field with name [%1$s] found on object of type [%2$s]",
					fieldName, ObjectUtils.nullSafeClassName(obj)));
			}

			field.setAccessible(true);

			return (T) field.get(obj);
		}
		catch (Exception e) {
			throw new RuntimeException(String.format("Failed to read field [%1$s] from object of type [%2$s]",
				fieldName, ObjectUtils.nullSafeClassName(obj)), e);
		}
	}
}
