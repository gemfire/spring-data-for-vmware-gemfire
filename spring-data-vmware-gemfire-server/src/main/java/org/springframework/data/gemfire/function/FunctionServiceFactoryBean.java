/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function;

import java.util.List;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;

/**
 * Spring FactoryBean for registering instance of GemFire Function with the GemFire FunctionService.
 *
 * @author David Turanski
 * @author John Blum
 * @see FactoryBean
 * @see InitializingBean
 * @see Function
 * @see FunctionService
 */
public class FunctionServiceFactoryBean implements FactoryBean<FunctionService>, InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(FunctionServiceFactoryBean.class);

	private List<Function> functions;

	@Override
	public void afterPropertiesSet() throws Exception {

		if (!CollectionUtils.isEmpty(this.functions)) {
			for (Function function : this.functions) {
				if (logger.isInfoEnabled()) {
					logger.info("registering Function with ID [{}]", function.getId());
				}
				FunctionService.registerFunction(function);
			}
		}
	}

	public void setFunctions(List<Function> functions) {
		this.functions = functions;
	}

	@Override
	public FunctionService getObject() throws Exception {
		return null;
	}

	@Override
	public Class<?> getObjectType() {
		return FunctionService.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
