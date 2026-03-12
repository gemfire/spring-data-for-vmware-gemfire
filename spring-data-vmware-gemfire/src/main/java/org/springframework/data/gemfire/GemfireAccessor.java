/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire;

import org.springframework.data.gemfire.gud.api.GudGemFireCheckedException;
import org.springframework.data.gemfire.gud.api.GudGemFireException;
import org.springframework.data.gemfire.gud.api.GudRegion;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.util.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link GemfireAccessor} is a base class for {@link GemfireTemplate} to encapsulate common operations and properties,
 * such as accessors to a {@link GudRegion}.
 *
 * This class is not intended to be used directly.
 *
 * @author Costin Leau
 * @author John Blum
 * @see InitializingBean
 * @see GudRegion
 */
public class GemfireAccessor implements InitializingBean {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@SuppressWarnings("rawtypes")
	private GudRegion region;

	/**
	 * Returns the template GemFire Cache Region.
	 *
	 * @param <K> the Region key class type.
	 * @param <V> the Region value class type.
	 * @return the GemFire Cache Region.
	 * @see GudRegion
	 */
	@SuppressWarnings("unchecked")
	public <K, V> GudRegion<K, V> getRegion() {
		return this.region;
	}

	/**
	 * Sets the template GemFire Cache Region.
	 *
	 * @param region the GemFire Cache Region used by this template.
	 * @see GudRegion
	 */
	public void setRegion(GudRegion<?, ?> region) {
		this.region = region;
	}

	/**
	 * {@inheritDoc}
	 */
	public void afterPropertiesSet() {
		Assert.notNull(getRegion(), "Region is required");
	}

	/**
	 * Converts the given {@link GudGemFireCheckedException} to an appropriate exception from the
	 * <code>org.springframework.dao</code> hierarchy.
	 * May be overridden in subclasses.
	 * @param ex GudGemFireCheckedException that occurred
	 * @return the corresponding DataAccessException instance
	 */
	public DataAccessException convertGemFireAccessException(GudGemFireCheckedException ex) {
		return GemfireCacheUtils.convertGemfireAccessException(ex);
	}

	/**
	 * Converts the given {@link GudGemFireException} to an appropriate exception from the
	 * <code>org.springframework.dao</code> hierarchy.
	 * May be overridden in subclasses.
	 * @param ex GudGemFireException that occurred
	 * @return the corresponding DataAccessException instance
	 */
	public DataAccessException convertGemFireAccessException(GudGemFireException ex) {
		return GemfireCacheUtils.convertGemfireAccessException(ex);
	}

	/**
	 * Converts the given GemFire exception to an appropriate exception from the
	 * <code>org.springframework.dao</code> hierarchy. Note that this particular implementation
	 * is called only for GemFire querying exception that do <b>NOT</b> extend from GemFire exception.
	 * May be overridden in subclasses.
	 *
	 * @param ex GudGemFireException that occurred
	 * @return the corresponding DataAccessException instance
	 */
	public DataAccessException convertGemFireQueryException(RuntimeException ex) {
		return GemfireCacheUtils.convertQueryExceptions(ex);
	}
}
