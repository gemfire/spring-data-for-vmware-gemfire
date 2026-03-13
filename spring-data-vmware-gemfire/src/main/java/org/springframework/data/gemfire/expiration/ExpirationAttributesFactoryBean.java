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

package org.springframework.data.gemfire.expiration;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.gemfire.gud.api.GudExpirationAction;
import org.springframework.data.gemfire.gud.api.GudExpirationAttributes;

/**
 * The ExpirationAttributesFactoryBean class is a Spring FactoryBean used to create GemFire ExpirationAttributes
 * to specify Expiration policies for Region Time-to-Live (TTL) and Idle-Timeouts (TTI) as well as
 * Entry Time-to-Live (TTL) and Idle-Timeouts (TTI).
 *
 * @author John Blum
 * @see FactoryBean
 * @see InitializingBean
 * @see GudExpirationAttributes
 * @since 1.6.0
 */
@SuppressWarnings("unused")
public class ExpirationAttributesFactoryBean implements FactoryBean<GudExpirationAttributes>, InitializingBean {

	protected static final int DEFAULT_TIMEOUT = 0;

	protected static final GudExpirationAction DEFAULT_EXPIRATION_ACTION =
		ExpirationActionType.DEFAULT.getExpirationAction();

	private GudExpirationAction action;

	private GudExpirationAttributes expirationAttributes;

	private Integer timeout;

	/* non-Javadoc */
	@Override
	public GudExpirationAttributes getObject() throws Exception {
		return expirationAttributes;
	}

	/* non-Javadoc */
	@Override
	public Class<?> getObjectType() {
		return (expirationAttributes != null ? expirationAttributes.getClass() : GudExpirationAttributes.class);
	}

	/* non-Javadoc */
	@Override
	public boolean isSingleton() {
		return true;
	}

	/**
	 * Sets the action to perform when a Region or an Entry expire.
	 *
	 * @param action the type of action to perform on expiration
	 * @see GudExpirationAction
	 */
	public void setAction(final GudExpirationAction action) {
		this.action = action;
	}

	/**
	 * Gets the action to perform when a Region or an Entry expires.
	 *
	 * @return the type of action to perform on expiration.
	 * @see ExpirationActionType
	 * @see GudExpirationAttributes#getAction()
	 */
	public GudExpirationAction getAction() {
		return (action != null ? action : DEFAULT_EXPIRATION_ACTION);
	}

	/**
	 * Sets the number of seconds before a Region or an Entry expires.
	 *
	 * @param timeout the number of seconds before a Region or an Entry expires.
	 */
	public void setTimeout(final Integer timeout) {
		this.timeout = timeout;
	}

	/**
	 * Gets the number of seconds before a Region or an Entry expires.
	 *
	 * @return the number of seconds before a Region or an Entry expires.
	 * @see GudExpirationAttributes#getTimeout()
	 */
	public int getTimeout() {
		return (timeout != null ? timeout : DEFAULT_TIMEOUT);
	}

	/**
	 * Initializes the GemFire ExpirationAttributes produced by this factory.
	 *
	 * @throws Exception if the construction of the ExpirationAttributes was not successful.
	 * @see #getAction()
	 * @see #getTimeout()
	 * @see ExpirationActionType#getExpirationAction()
	 * @see GudExpirationAttributes
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		expirationAttributes = GudExpirationAttributes.of(getTimeout(), getAction());
	}

}
