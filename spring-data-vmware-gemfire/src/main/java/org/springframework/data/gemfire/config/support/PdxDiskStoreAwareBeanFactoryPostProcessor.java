/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.config.support;

import static org.springframework.data.gemfire.util.ArrayUtils.nullSafeArray;

import java.util.Arrays;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.data.gemfire.gud.api.GudDiskStore;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.data.gemfire.util.SpringExtensions;
import org.springframework.util.Assert;

/**
 * {@link PdxDiskStoreAwareBeanFactoryPostProcessor} is a Spring {@link BeanFactoryPostProcessor} that modifies
 * all GemFire Region and Disk Store beans in the Spring container to form a dependency on
 * the Cache's PDX {@link GudDiskStore} bean.
 *
 * A persistent Region may contain PDX typed data, in which case, the PDX type meta-data stored to disk needs to be
 * loaded before the Region having PDX data is loaded from disk.
 *
 * @author John Blum
 * @see BeanFactoryPostProcessor
 * @see ConfigurableListableBeanFactory
 * @see GudDiskStore
 * @see GudRegion
 * @since 1.3.3
 */
public class PdxDiskStoreAwareBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	protected static final String[] EMPTY_STRING_ARRAY = new String[0];

	private final String pdxDiskStoreName;

	/**
	 * Constructs an instance of the {@link PdxDiskStoreAwareBeanFactoryPostProcessor} class initialized with
	 * the given PDX {@link GudDiskStore} name.
	 *
	 * @param pdxDiskStoreName name of the GemFire PDX {@link GudDiskStore}.
	 * @throws IllegalArgumentException if the GemFire PDX {@link GudDiskStore} name is unspecified.
	 */
	public PdxDiskStoreAwareBeanFactoryPostProcessor(String pdxDiskStoreName) {

		Assert.hasText(pdxDiskStoreName, "PDX DiskStore name is required");

		this.pdxDiskStoreName = pdxDiskStoreName;
	}

	/**
	 * Returns the name of the GemFire PDX {@link GudDiskStore}.
	 *
	 * @return the name of the GemFire PDX {@link GudDiskStore}.
	 */
	public String getPdxDiskStoreName() {
		return this.pdxDiskStoreName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("all")
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		postProcessPdxDiskStoreDependencies(beanFactory, GudDiskStore.class, GudRegion.class);
	}

	/**
	 * Post processes all beans in the Spring container, application context that may potentially have a dependency,
	 * or requirement on the PDX-based Disk Store being present before the bean itself can be processed and created.
	 * For instance, Regions that might contained PDX-based Key types require the PDX type meta-data to be loaded
	 * before the Region's data (Keys) are accessed.
	 *
	 * @param beanFactory the BeanFactory used to evaluate beans in context for PDX Disk Store dependencies.
	 * @param beanTypes an array of Class types indicating the type of beans to evaluate.
	 * @see ConfigurableListableBeanFactory#getBeanNamesForType(Class)
	 */
	private void postProcessPdxDiskStoreDependencies(ConfigurableListableBeanFactory beanFactory,
			Class<?>... beanTypes) {

		Arrays.stream(nullSafeArray(beanTypes, Class.class)).forEach(beanType -> {

			for (String beanName : beanFactory.getBeanNamesForType(beanType)) {
				if (!beanName.equalsIgnoreCase(getPdxDiskStoreName())) {

					BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);

					// NOTE for simplicity sake, we add a bean dependency to any bean definition for a bean
					// (Disk Store, Region, or otherwise) that may potentially require
					// the PDX Disk Store to exist.
					// NOTE this logic could be optimized to include Disk Store
					// and persistent Regions (either by way of 'persistent' attribute, Data Policy
					// or [Client]RegionShortcut) that also does not have an explicit Disk Store reference.
					addPdxDiskStoreDependency(beanDefinition);
				}
			}
		});
	}

	/**
	 * Adds the PDX Disk Store bean name as a dependency at the beginning of the list of dependencies declared
	 * by the Bean.
	 *
	 * @param beanDefinition the BeanDefinition to add the PDX Disk Store dependency to.
	 * @see #getDependsOn(BeanDefinition)
	 * @see BeanDefinition#setDependsOn(String[])
	 */
	private void addPdxDiskStoreDependency(BeanDefinition beanDefinition) {

		String[] newDependsOn =
			(String[]) ArrayUtils.insert(getDependsOn(beanDefinition), 0, getPdxDiskStoreName());

		beanDefinition.setDependsOn(newDependsOn);
	}

	/**
	 * Gets the current list of dependencies declared in the BeanDefinition for the Bean, returning an
	 * empty String array if the dependsOn property is null.
	 *
	 * @param beanDefinition the BeanDefinition of the Bean containing the dependencies.
	 * @return an array of Bean names that this Bean depends on, or an empty String array if the dependencies
	 * are undefined.
	 * @see #addPdxDiskStoreDependency(BeanDefinition)
	 * @see BeanDefinition#getDependsOn()
	 */
	private String[] getDependsOn(BeanDefinition beanDefinition) {
		return SpringExtensions.defaultIfNull(beanDefinition.getDependsOn(), EMPTY_STRING_ARRAY);
	}
}
