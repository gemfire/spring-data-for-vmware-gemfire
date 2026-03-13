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

package org.springframework.data.gemfire.serialization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.gemfire.gud.api.GudDataSerializable;
import org.springframework.data.gemfire.gud.api.GudInstantiator;
import org.springframework.util.Assert;

/**
 * {@link FactoryBean} that eases registration of custom {@link GudInstantiator} through
 * {@link InstantiatorGenerator}s, inside the Spring container.
 *
 * By default, the returns {@link GudInstantiator}s (created through  {@link AsmInstantiatorGenerator} if a custom
 * generator is not specified) are registered at startup with GemFire.
 *
 * @author Costin Leau
 */
public class InstantiatorFactoryBean implements BeanClassLoaderAware, FactoryBean<Collection<GudInstantiator>>,
		InitializingBean {

	private InstantiatorGenerator generator;
	private Collection<GudInstantiator> list;
	private ClassLoader classLoader;
	private boolean autoRegister = true;
	private boolean distribute = false;

	private Map<Class<? extends GudDataSerializable>, Integer> types;


	public void afterPropertiesSet() throws Exception {
		Assert.notEmpty(types, "no custom types for generating the Instantiators");

		if (generator == null) {
			generator = new AsmInstantiatorGenerator(classLoader);
		}

		list = new ArrayList<GudInstantiator>(types.size());

		for (Entry<Class<? extends GudDataSerializable>, Integer> entry : types.entrySet()) {
			Assert.notNull(entry.getKey(), "Invalid/Null class given as custom type");
			Assert.notNull(entry.getValue(), "Invalid/Null int given as user id");

			list.add(generator.getInstantiator(entry.getKey(), entry.getValue()));
		}

		if (autoRegister) {
			for (GudInstantiator instantiator : list) {
				GudInstantiator.register(instantiator, distribute);
			}
		}
	}

	public Collection<GudInstantiator> getObject() throws Exception {
		return list;
	}

	public Class<?> getObjectType() {
		return (list != null ? list.getClass() : Collection.class);
	}

	public boolean isSingleton() {
		return true;
	}

	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * Sets the custom types and associated user ids for generating the {@link GudInstantiator}s.
	 *
	 * @param types map containing as keys the custom types and values the associated user ids.
	 */
	public void setCustomTypes(Map<Class<? extends GudDataSerializable>, Integer> types) {
		this.types = types;
	}

	/**
	 * Sets the generator to use for creating {@link GudInstantiator}s.
	 *
	 * @param generator the generator to set
	 */
	public void setGenerator(InstantiatorGenerator generator) {
		this.generator = generator;
	}

	/**
	 * Sets the auto-registration of this {@link GudInstantiator} during the container startup.
	 * Default is true, meaning the registration will occur once this factory is initialized.
	 *
	 * @see GudInstantiator#register(GudInstantiator)
	 * @param autoRegister the autoRegister to set
	 */
	public void setAutoRegister(boolean autoRegister) {
		this.autoRegister = autoRegister;
	}


	/**
	 * Sets the distribution of the region of this {@link GudInstantiator} during the container startup.
	 * Default is false, meaning the registration will not be distributed to other clients.
	 *
	 * @see GudInstantiator#register(GudInstantiator, boolean)
	 * @param distribute whether the registration is distributable or not
	 */
	public void setDistribute(boolean distribute) {
		this.distribute = distribute;
	}
}
