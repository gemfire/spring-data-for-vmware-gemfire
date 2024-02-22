/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.annotation.support;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newRuntimeException;

import javax.naming.NamingException;

import org.apache.geode.ra.GFConnectionFactory;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * The {@link GemFireAsLastResourceConnectionAcquiringAspect} class is a {@link AbstractGemFireAsLastResourceAspectSupport}
 * implementation responsible for acquiring a GemFire Connection from GemFire's JCA ResourceAdapter,
 * {@link GFConnectionFactory} after a CMT/JTA Transaction is began, which is initiated by
 * Spring's Transaction infrastructure.
 *
 * @author John Blum
 * @see GFConnectionFactory
 * @see Aspect
 * @see Before
 * @see AbstractGemFireAsLastResourceAspectSupport
 * @since 2.0.0
 */
@SuppressWarnings("unused")
@Aspect
public class GemFireAsLastResourceConnectionAcquiringAspect extends AbstractGemFireAsLastResourceAspectSupport {

	private static final int DEFAULT_ORDER = 2048000;

	@Autowired(required = false)
	private GFConnectionFactory gemfireConnectionFactory;

	/**
	 * Acquires (opens) a GemFire JCA ResourceAdapter Connection after the Spring CMT/JTA Transaction begins.
	 */
	@Before("atTransactionalType() || atTransactionalMethod()")
	public void doGemFireConnectionFactoryGetConnection() {

		logTraceInfo("Acquiring GemFire Connection from GemFire JCA ResourceAdapter registered at [%s]...",
			resolveGemFireJcaResourceAdapterJndiName());

		GemFireConnectionHolder.acquire(resolveGemFireConnectionFactory(), isThrowOnError(), this::logError);
	}

	/* (non-Javadoc) */
	synchronized GFConnectionFactory resolveGemFireConnectionFactory() {

		GFConnectionFactory connectionFactory = getGemFireConnectionFactory();

		if (connectionFactory == null) {

			String resolvedGemFireJcaResourceAdapterJndiName = resolveGemFireJcaResourceAdapterJndiName();

			try {
				connectionFactory = this.gemfireConnectionFactory =
					(GFConnectionFactory) resolveContext().lookup(resolvedGemFireJcaResourceAdapterJndiName);
			}
			catch (NamingException cause) {
				throw newRuntimeException(cause,
					"Failed to resolve a GFConnectionFactory from the configured JNDI context name [%s]",
						resolvedGemFireJcaResourceAdapterJndiName);
			}
		}

		return connectionFactory;
	}


	/**
	 * Returns the default order used by this AOP Aspect in the chain of Aspects configured
	 * in Spring's Transaction Management.
	 *
	 * @return an int value specifying the default order used by this AOP Aspect in the chain of Aspects
	 * configured in Spring's Transaction Management.
	 */
	@Override
	protected Integer getDefaultOrder() {
		return DEFAULT_ORDER;
	}

	/**
	 * Returns a reference to the configured {@link GFConnectionFactory} instance.
	 *
	 * @return a reference to the configured {@link GFConnectionFactory} instance; may be {@literal null}.
	 * @see GFConnectionFactory
	 */
	public synchronized GFConnectionFactory getGemFireConnectionFactory() {
		return this.gemfireConnectionFactory;
	}
}
