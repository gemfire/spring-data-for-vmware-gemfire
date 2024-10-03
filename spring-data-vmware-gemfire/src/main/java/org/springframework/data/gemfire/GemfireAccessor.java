/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import org.apache.geode.GemFireCheckedException;
import org.apache.geode.GemFireException;
import org.apache.geode.cache.Region;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.util.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link GemfireAccessor} is a base class for {@link GemfireTemplate} to encapsulate common operations and properties,
 * such as accessors to a {@link Region}.
 *
 * This class is not intended to be used directly.
 *
 * @author Costin Leau
 * @author John Blum
 * @see InitializingBean
 * @see Region
 */
public class GemfireAccessor implements InitializingBean {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@SuppressWarnings("rawtypes")
	private Region region;

	/**
	 * Returns the template GemFire Cache Region.
	 *
	 * @param <K> the Region key class type.
	 * @param <V> the Region value class type.
	 * @return the GemFire Cache Region.
	 * @see Region
	 */
	@SuppressWarnings("unchecked")
	public <K, V> Region<K, V> getRegion() {
		return this.region;
	}

	/**
	 * Sets the template GemFire Cache Region.
	 *
	 * @param region the GemFire Cache Region used by this template.
	 * @see Region
	 */
	public void setRegion(Region<?, ?> region) {
		this.region = region;
	}

	/**
	 * {@inheritDoc}
	 */
	public void afterPropertiesSet() {
		Assert.notNull(getRegion(), "Region is required");
	}

	/**
	 * Converts the given {@link GemFireCheckedException} to an appropriate exception from the
	 * <code>org.springframework.dao</code> hierarchy.
	 * May be overridden in subclasses.
	 * @param ex GemFireCheckedException that occurred
	 * @return the corresponding DataAccessException instance
	 */
	public DataAccessException convertGemFireAccessException(GemFireCheckedException ex) {
		return GemfireCacheUtils.convertGemfireAccessException(ex);
	}

	/**
	 * Converts the given {@link GemFireException} to an appropriate exception from the
	 * <code>org.springframework.dao</code> hierarchy.
	 * May be overridden in subclasses.
	 * @param ex GemFireException that occurred
	 * @return the corresponding DataAccessException instance
	 */
	public DataAccessException convertGemFireAccessException(GemFireException ex) {
		return GemfireCacheUtils.convertGemfireAccessException(ex);
	}

	/**
	 * Converts the given GemFire exception to an appropriate exception from the
	 * <code>org.springframework.dao</code> hierarchy. Note that this particular implementation
	 * is called only for GemFire querying exception that do <b>NOT</b> extend from GemFire exception.
	 * May be overridden in subclasses.
	 *
	 * @param ex GemFireException that occurred
	 * @return the corresponding DataAccessException instance
	 */
	public DataAccessException convertGemFireQueryException(RuntimeException ex) {
		return GemfireCacheUtils.convertQueryExceptions(ex);
	}
}
