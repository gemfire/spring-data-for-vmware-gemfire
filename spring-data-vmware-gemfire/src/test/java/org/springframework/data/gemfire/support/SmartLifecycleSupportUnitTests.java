/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import org.springframework.context.SmartLifecycle;

/**
 * Unit tests for {@link SmartLifecycleSupport}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see SmartLifecycleSupport
 * @since 1.0.0
 */
public class SmartLifecycleSupportUnitTests {

	@Test
	public void isAutoStartupReturnsTrueByDefault() {

		SmartLifecycle smartLifecycle = mock(SmartLifecycleSupport.class);

		when(smartLifecycle.isAutoStartup()).thenCallRealMethod();

		assertThat(smartLifecycle.isAutoStartup()).isTrue();

		verify(smartLifecycle, times(1)).isAutoStartup();
	}

	@Test
	public void stopWithRunnableCallsStopCallsRunnableRun() {

		Runnable mockRunnable = mock(Runnable.class);

		SmartLifecycle smartLifecycle = mock(SmartLifecycleSupport.class);

		doCallRealMethod().when(smartLifecycle).stop(any(Runnable.class));

		smartLifecycle.stop(mockRunnable);

		verify(smartLifecycle, times(1)).stop();
		verify(mockRunnable, times(1)).run();
	}

	@Test
	public void isRunningReturnsFalse() {

		SmartLifecycle smartLifecycle = mock(SmartLifecycleSupport.class);

		when(smartLifecycle.isRunning()).thenCallRealMethod();

		assertThat(smartLifecycle.isRunning()).isFalse();

		verify(smartLifecycle, times(1)).isRunning();
	}

	@Test
	public void getPhaseReturnsDefaultPhase() {

		SmartLifecycle smartLifecycle = mock(SmartLifecycleSupport.class);

		when(smartLifecycle.getPhase()).thenCallRealMethod();

		assertThat(smartLifecycle.getPhase()).isEqualTo(SmartLifecycleSupport.DEFAULT_PHASE);

		verify(smartLifecycle, times(1)).getPhase();
	}
}
