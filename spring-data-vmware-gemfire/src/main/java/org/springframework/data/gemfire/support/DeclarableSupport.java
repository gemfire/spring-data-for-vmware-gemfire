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

package org.springframework.data.gemfire.support;

import static org.springframework.data.gemfire.support.GemfireBeanFactoryLocator.newBeanFactoryLocator;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.gud.api.GudCacheCallback;
import org.springframework.data.gemfire.gud.api.GudDeclarable;

/**
 * Abstract base class for implementing Spring aware, Apache Geode {@link GudDeclarable} components.
 *
 * Provides subclasses with a reference to the current Spring {@link BeanFactory} in order to
 * perform Spring bean lookups or resource loading.
 *
 * Note, in most cases, the developer should just declare the same Apache Geode components as Spring beans
 * in the Spring container through the {@link ClientRegionFactoryBean}, which gives full access to the Spring container
 * capabilities and does not enforce the {@link GudDeclarable} interface to be implemented.
 *
 * @author Costin Leau
 * @author John Blum
 * @see GudCacheCallback
 * @see GudDeclarable
 * @see BeanFactory
 * @see GemfireBeanFactoryLocator
 */
@SuppressWarnings("unused")
public abstract class DeclarableSupport implements GudCacheCallback, GudDeclarable {

	private String beanFactoryKey = null;

	/**
	 * Returns a reference to the Spring {@link BeanFactory}.
	 *
	 * @return a reference to the Spring {@link BeanFactory}.
	 * @see GemfireBeanFactoryLocator#useBeanFactory(String)
	 * @see BeanFactory
	 * @see #locateBeanFactory()
	 */
	protected BeanFactory getBeanFactory() {
		return locateBeanFactory();
	}

	/**
	 * Set the key used to lookup the Spring {@link BeanFactory}.
	 *
	 * @param beanFactoryKey {@link String} containing the key used to lookup the Spring {@link BeanFactory}.
	 */
	public void setBeanFactoryKey(String beanFactoryKey) {
		this.beanFactoryKey = beanFactoryKey;
	}

	/**
	 * Returns the key used to lookup the Spring {@link BeanFactory}.
	 *
	 * @return a {@link String} containing the key used to lookup the Spring {@link BeanFactory}.
	 */
	protected String getBeanFactoryKey() {
		return this.beanFactoryKey;
	}

	/**
	 * Returns a reference to the Spring {@link BeanFactory}.
	 *
	 * @return a reference to the Spring {@link BeanFactory}.
	 * @see BeanFactory
	 * @see #locateBeanFactory(String)
	 * @see #getBeanFactoryKey()
	 */
	protected BeanFactory locateBeanFactory() {
		return locateBeanFactory(getBeanFactoryKey());
	}

	/**
	 * Returns a reference to the Spring {@link BeanFactory} for the given {@code beanFactoryKey}.
	 *
	 * @param beanFactoryKey {@link String} containing the key used to lookup the Spring {@link BeanFactory}.
	 * @return a reference to the Spring {@link BeanFactory} for the given {@code beanFactoryKey}.
	 * @see GemfireBeanFactoryLocator#useBeanFactory(String)
	 * @see BeanFactory
	 */
	protected BeanFactory locateBeanFactory(String beanFactoryKey) {
		return newBeanFactoryLocator().useBeanFactory(beanFactoryKey);
	}

	@Override
	public void close() { }

}
