/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.annotation.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.resource.ResourceException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.ra.GFConnection;

import org.slf4j.Logger;

/**
 * Unit tests for {@link GemFireAsLastResourceConnectionClosingAspect}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.junit.runner.RunWith
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.mockito.Spy
 * @see org.mockito.junit.MockitoJUnitRunner
 * @see org.springframework.data.gemfire.config.annotation.support.GemFireAsLastResourceConnectionClosingAspect
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class GemFireAsLastResourceConnectionClosingAspectUnitTests {

	private GemFireAsLastResourceConnectionClosingAspect aspect;

	@Mock
	private GFConnection mockGemFireConnection;

	@Mock
	private Logger mockLogger;

	@Before
	public void setup() {
		aspect = spy(new GemFireAsLastResourceConnectionClosingAspect());
		when(aspect.getLogger()).thenReturn(mockLogger);
	}

	@Test
	public void connectionClosingAspectHasHigherPriorityThanConnectionAcquiringAspect() {
		assertThat(aspect.getOrder()).isLessThan(new GemFireAsLastResourceConnectionAcquiringAspect().getOrder());
	}

	@Test
	public void doGemFireConnectionCloseIsSuccessful() throws ResourceException {

		AbstractGemFireAsLastResourceAspectSupport.GemFireConnectionHolder.of(mockGemFireConnection);

		when(mockLogger.isTraceEnabled()).thenReturn(true);

		aspect.doGemFireConnectionClose();;

		verify(mockGemFireConnection, times(1)).close();
		verify(mockLogger, times(1)).trace(eq("Closing GemFire Connection..."));
	}
}
