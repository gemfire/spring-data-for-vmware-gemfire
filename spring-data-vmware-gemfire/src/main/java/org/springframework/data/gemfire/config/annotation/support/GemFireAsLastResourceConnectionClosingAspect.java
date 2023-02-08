/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.annotation.support;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

/**
 * The {@link GemFireAsLastResourceConnectionClosingAspect} class is a {@link AbstractGemFireAsLastResourceAspectSupport}
 * implementation responsible for closing the GemFire Connection obtained from the GemFire JCA ResourceAdapter
 * deployed in a managed environment when using GemFire as the Last Resource in a CMT/JTA Transaction
 * initiated from Spring's Transaction infrastructure.
 *
 * @author John Blum
 * @see org.aspectj.lang.annotation.Aspect
 * @see org.aspectj.lang.annotation.After
 * @see org.springframework.data.gemfire.config.annotation.support.AbstractGemFireAsLastResourceAspectSupport
 * @since 2.0.0
 */
@SuppressWarnings("unused")
@Aspect
public class GemFireAsLastResourceConnectionClosingAspect extends AbstractGemFireAsLastResourceAspectSupport {

	private static final int DEFAULT_ORDER = 1024000;

	/**
	 * Closes the GemFire JCA ResourceAdapter Connection after the Spring CMT/JTA Transaction completes.
	 */
	@After("atTransactionalType() || atTransactionalMethod()")
	public void doGemFireConnectionClose() {

		logTraceInfo("Closing GemFire Connection...");

		GemFireConnectionHolder.close(isThrowOnError(), this::logWarning);
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
}
