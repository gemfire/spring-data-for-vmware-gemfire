/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire;

import org.springframework.data.gemfire.gud.api.GudCacheServer;

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
		GudCacheServer cs = bf.getBean(GudCacheServer.class);
		Assert.isTrue(!cs.isRunning(), "GudCacheServer should not have been started yet... ");
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.bf = beanFactory;
	}
}
