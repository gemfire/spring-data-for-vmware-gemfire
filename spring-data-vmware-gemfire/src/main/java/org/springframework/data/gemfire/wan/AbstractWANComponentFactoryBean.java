// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.wan;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.GemFireCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.support.AbstractFactoryBeanSupport;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Abstract base class for WAN Gateway objects.
 *
 * @author David Turanski
 * @author John Blum
 * @author Udo Kohlmeyer
 * @see Cache
 * @see GemFireCache
 * @see DisposableBean
 * @see InitializingBean
 * @see AbstractFactoryBeanSupport
 */
public abstract class AbstractWANComponentFactoryBean<T> extends AbstractFactoryBeanSupport<T>
		implements DisposableBean, InitializingBean {

	@Autowired
	protected Cache cache;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected Object factory;

	private String beanName;
	private String name;

	protected AbstractWANComponentFactoryBean() { }

	protected AbstractWANComponentFactoryBean(GemFireCache cache) {
		this.cache = (Cache) cache;
	}

	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public Cache getCache() {
		return this.cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public void setFactory(Object factory) {
		this.factory = factory;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return StringUtils.hasText(this.name) ? this.name : this.beanName;
	}

	@Override
	public final void afterPropertiesSet() throws Exception {

		Assert.notNull(getCache(), "Cache must not be null");
		Assert.notNull(getName(), "Name must not be null");

		doInit();
	}

	protected abstract void doInit() throws Exception;

	@Override
	public void destroy() { }

}
