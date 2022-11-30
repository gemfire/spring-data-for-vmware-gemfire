// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.dao;

import org.apache.geode.cache.Region;

import org.springframework.dao.support.DaoSupport;
import org.springframework.data.gemfire.GemfireOperations;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.util.Assert;

/**
 * Convenient super class for GemFire Data Access Objects (DAO) implementing the Spring
 * {@link DaoSupport} abstract class. Intended for use with {@link GemfireTemplate}.
 *
 * Requires a GemFire {@link Region} to be set, providing a {@link GemfireTemplate} based on it to subclasses.
 * Can alternatively be initialized directly via a {@link GemfireTemplate} reusing the template's  settings.
 *
 * This class will create its own {@link GemfireTemplate} if a GemFire {@link Region} reference is passed in.
 * A custom {@link GemfireTemplate} instance can be used through overriding <code>createGemfireTemplate</code>.
 *
 * @author Costin Leau
 * @author John Blum
 * @see Region
 * @see DaoSupport
 * @see GemfireTemplate
 */
public abstract class GemfireDaoSupport extends DaoSupport {

	private GemfireOperations gemfireTemplate;

	/**
	 * Set the GemfireTemplate for this DAO explicitly as an alternative to specifying a GemFire Cache {@link Region}.
	 *
	 * @param gemfireTemplate the GemfireTemplate to be use by this DAO.
	 * @see GemfireOperations
	 * @see GemfireTemplate
	 * @see #setRegion
	 */
	public final void setGemfireTemplate(GemfireOperations gemfireTemplate) {
		this.gemfireTemplate = gemfireTemplate;
	}

	/**
	 * Returns the GemfireTemplate for this DAO, pre-initialized with the Region or set explicitly.
	 *
	 * @return an instance of the GemfireTemplate to perform data access operations on the GemFire Cache Region.
	 * @see GemfireOperations
	 * @see GemfireTemplate
	 */
	public final GemfireOperations getGemfireTemplate() {
		return this.gemfireTemplate;
	}

	/**
	 * Sets the GemFire Cache Region to be used by this DAO. Will automatically create
	 * an instance of the GemfireTemplate for the given Region.
	 *
	 * @param region the GemFire Cache Region upon which this DAO operates.
	 * @see Region
	 * @see #createGemfireTemplate(Region)
	 */
	public void setRegion(Region<?, ?> region) {
		this.gemfireTemplate = createGemfireTemplate(region);
	}

	/**
	 * Creates an instance of the GemfireTemplate for the given GemFire Cache Region.
	 * <p>Can be overridden in subclasses to provide a GemfireTemplate instance with different configuration,
	 * or even a custom GemfireTemplate subclass.
	 *
	 * @param region the GemFire Cache Region for which the GemfireTemplate is created.
	 * @return a new GemfireTemplate instance configured with the given GemFire Cache Region.
	 * @see Region
	 * @see #setRegion
	 */
	protected GemfireTemplate createGemfireTemplate(Region<?, ?> region) {
		return new GemfireTemplate(region);
	}

	/**
	 * Verifies that this DAO has been configured properly.
	 */
	@Override
	protected final void checkDaoConfig() {
		Assert.state(gemfireTemplate != null, "A GemFire Cache Region or instance of GemfireTemplate is required");
	}
}
