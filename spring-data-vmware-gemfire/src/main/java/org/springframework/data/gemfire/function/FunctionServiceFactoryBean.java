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

package org.springframework.data.gemfire.function;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.data.gemfire.gud.api.GudFunctionService;
import org.springframework.util.CollectionUtils;

/**
 * Spring FactoryBean for registering instance of GemFire Function with the GemFire FunctionService.
 *
 * @author David Turanski
 * @author John Blum
 * @see FactoryBean
 * @see InitializingBean
 * @see GudFunction
 * @see GudFunctionService
 */
public class FunctionServiceFactoryBean implements FactoryBean<GudFunctionService>, InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(FunctionServiceFactoryBean.class);

	private List<GudFunction> functions;

	@Override
	public void afterPropertiesSet() throws Exception {

		if (!CollectionUtils.isEmpty(this.functions)) {
			for (GudFunction function : this.functions) {
				if (logger.isInfoEnabled()) {
					logger.info("registering Function with ID [{}]", function.getId());
				}
				GudFunctionService.registerFunction(function);
			}
		}
	}

	public void setFunctions(List<GudFunction> functions) {
		this.functions = functions;
	}

	@Override
	public GudFunctionService getObject() throws Exception {
		return null;
	}

	@Override
	public Class<?> getObjectType() {
		return GudFunctionService.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
