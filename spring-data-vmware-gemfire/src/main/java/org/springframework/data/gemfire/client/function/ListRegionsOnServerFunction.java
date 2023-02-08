/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client.function;

import java.util.ArrayList;
import java.util.List;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;

/**
 * ListRegionsOnServerFunction is a GemFire Function class that returns a List of names for all Regions
 * defined in the GemFire cluster.
 *
 * @author David Turanski
 * @author John Blum
 * @see Function
 */
@SuppressWarnings("serial")
public class ListRegionsOnServerFunction implements Function {

	private static final long serialVersionUID = 867530169L;

	public static final String ID = ListRegionsOnServerFunction.class.getName();

	/*
	 * (non-Javadoc)
	 * @see org.apache.geode.cache.execute.Function#execute(org.apache.geode.cache.execute.FunctionContext)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void execute(FunctionContext functionContext) {

		List<String> regionNames = new ArrayList<>();

		for (Region<?, ?> region : getCache().rootRegions()) {
			regionNames.add(region.getName());
		}

		functionContext.getResultSender().lastResult(regionNames);
	}

	Cache getCache() {
		return CacheFactory.getAnyInstance();
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.geode.cache.execute.Function#getId()
	 */
	@Override
	public String getId() {
		return this.getClass().getName();
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.geode.cache.execute.Function#hasResult()
	 */
	@Override
	public boolean hasResult() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.geode.cache.execute.Function#isHA()
	 */
	@Override
	public boolean isHA() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.geode.cache.execute.Function#optimizeForWrite()
	 */
	@Override
	public boolean optimizeForWrite() {
		return false;
	}
}
