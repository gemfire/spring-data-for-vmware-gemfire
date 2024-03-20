/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire;

import org.apache.geode.cache.server.CacheServer;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;


/**
 * Simple bean used to check initialization order
 *
 * @author Costin Leau
 */
public class Init implements InitializingBean, BeanFactoryAware {

	private BeanFactory bf;

	public void afterPropertiesSet() {
		CacheServer cs = bf.getBean(CacheServer.class);
		Assert.isTrue(!cs.isRunning(), "CacheServer should not have been started yet... ");
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.bf = beanFactory;
	}
}
