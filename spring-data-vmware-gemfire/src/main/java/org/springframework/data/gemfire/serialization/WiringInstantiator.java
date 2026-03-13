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

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.wiring.BeanConfigurerSupport;
import org.springframework.data.gemfire.gud.api.GudDataSerializable;
import org.springframework.data.gemfire.gud.api.GudInstantiator;

/**
 * Apache Geode {@link GudInstantiator} that performs instance wiring using the Spring IoC container, allowing common
 * properties to be injected before the object is hydrated/deserialized. The newly created instances can be configured
 * either by relying on an existing bean definition (which acts as a template) or by providing an embedded configuration
 * through annotations.
 *
 * Can reuse existing {@link GudInstantiator Instantiators} to optimize instance creation. If one is not provided,
 * it will fallback to reflection invocation.
 *
 * By default, on initialization, the class will register itself as an {@link GudInstantiator} through
 * {@link GudInstantiator#register(GudInstantiator)}. This behaviour can be disabled through {@link #setAutoRegister(boolean)}.
 * Additionally, the instantiator registration is not distributed by default, to allow the application context
 * to be reused. This can be changed through {@link #setDistribute(boolean)}.
 *
 * @author Costin Leau
 * @see BeanConfigurerSupport
 * @see org.springframework.beans.factory.wiring.BeanWiringInfoResolver
 * @see org.springframework.beans.factory.annotation.Autowired
 */
public class WiringInstantiator extends GudInstantiator implements BeanFactoryAware, InitializingBean, DisposableBean {

	private final GudInstantiator instantiator;
	private final Class<? extends GudDataSerializable> clazz;
	private BeanConfigurerSupport configurer;
	private BeanFactory beanFactory;
	private boolean autoRegister = true;
	private boolean distribute = false;

	public WiringInstantiator(GudInstantiator instantiator) {
		super(instantiator.getInstantiatedClass(), instantiator.getId());
		this.instantiator = instantiator;
		this.clazz = null;
	}

	public WiringInstantiator(Class<? extends GudDataSerializable> c, int classId) {
		super(c, classId);
		instantiator = null;
		clazz = c;
	}


	public void afterPropertiesSet() {
		if (configurer == null) {
			configurer = new BeanConfigurerSupport();
			configurer.setBeanFactory(beanFactory);
			configurer.afterPropertiesSet();
		}

		if (autoRegister) {
			GudInstantiator.register(this, distribute);
		}
	}

	public void destroy() throws Exception {
		configurer.destroy();
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}


	@Override
	public GudDataSerializable newInstance() {
		GudDataSerializable instance = createInstance();
		configurer.configureBean(instance);
		return instance;
	}

	private GudDataSerializable createInstance() {
		if (instantiator != null) {
			return instantiator.newInstance();
		}

		return BeanUtils.instantiate(clazz);
	}

	/**
	 * Sets the manager responsible for configuring the newly created instances.
	 * The given configurer needs to be configured and initialized before-hand.
	 *
	 * @param configurer the configurer to set
	 */
	public void setConfigurer(BeanConfigurerSupport configurer) {
		this.configurer = configurer;
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
