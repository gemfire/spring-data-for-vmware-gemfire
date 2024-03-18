/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client.function;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;

import org.springframework.lang.NonNull;

/**
 * {@link ListRegionsOnServerFunction} is an Apache Geode {@link Function}
 * returning a {@link List} of {@link String names} for all {@link Region Regions}
 * defined in the Apache Geode cache.
 *
 * @author David Turanski
 * @author John Blum
 * @see Serial
 * @see Function
 */
public class ListRegionsOnServerFunction implements Function<Object> {

	@Serial
	private static final long serialVersionUID = 867530169L;

	public static final String ID = ListRegionsOnServerFunction.class.getName();

	@Override
	@SuppressWarnings("unchecked")
	public void execute(@NonNull FunctionContext functionContext) {

		List<String> regionNames = new ArrayList<>();

		for (Region<?, ?> region : getCache().rootRegions()) {
			regionNames.add(region.getName());
		}

		functionContext.getResultSender().lastResult(regionNames);
	}

	Cache getCache() {
		return CacheFactory.getAnyInstance();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return getClass().getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasResult() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isHA() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean optimizeForWrite() {
		return false;
	}
}
