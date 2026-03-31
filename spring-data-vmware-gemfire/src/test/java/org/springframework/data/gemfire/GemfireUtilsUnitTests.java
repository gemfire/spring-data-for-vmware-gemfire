/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Properties;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudDistributedSystem;
import org.junit.Test;

/**
 * Unit Tests for {@link GemfireUtils}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.data.gemfire.GemfireUtils
 * @since 1.3.3
 */
public class GemfireUtilsUnitTests {

	@Test
	public void isDurableWithDurableClientIsTrue() {

		GudClientCache mockClientCache = mock(GudClientCache.class);

		GudDistributedSystem mockDistributedSystem = mock(GudDistributedSystem.class);

		Properties gemfireProperties = new Properties();

		gemfireProperties.setProperty(GemfireUtils.DURABLE_CLIENT_ID_PROPERTY_NAME, "123");

		when(mockClientCache.getDistributedSystem()).thenReturn(mockDistributedSystem);
		when(mockDistributedSystem.isConnected()).thenReturn(true);
		when(mockDistributedSystem.getProperties()).thenReturn(gemfireProperties);

		assertThat(GemfireUtils.isDurable(mockClientCache)).isTrue();

		verify(mockClientCache, times(1)).getDistributedSystem();
		verify(mockDistributedSystem, times(1)).isConnected();
		verify(mockDistributedSystem, times(1)).getProperties();
	}

	@Test
	public void isDurableWithNonDurableClientIsFalse() {

		GudClientCache mockClientCache = mock(GudClientCache.class);

		GudDistributedSystem mockDistributedSystem = mock(GudDistributedSystem.class);

		Properties gemfireProperties = new Properties();

		gemfireProperties.setProperty(GemfireUtils.DURABLE_CLIENT_ID_PROPERTY_NAME, "  ");

		when(mockClientCache.getDistributedSystem()).thenReturn(mockDistributedSystem);
		when(mockDistributedSystem.isConnected()).thenReturn(true);
		when(mockDistributedSystem.getProperties()).thenReturn(gemfireProperties);

		assertThat(GemfireUtils.isDurable(mockClientCache)).isFalse();

		verify(mockClientCache, times(1)).getDistributedSystem();
		verify(mockDistributedSystem, times(1)).isConnected();
		verify(mockDistributedSystem, times(1)).getProperties();
	}

	@Test
	public void isDurableWhenDistributedSystemIsNotConnectedIsFalse() {

		GudClientCache mockClientCache = mock(GudClientCache.class);

		GudDistributedSystem mockDistributedSystem = mock(GudDistributedSystem.class);

		when(mockClientCache.getDistributedSystem()).thenReturn(mockDistributedSystem);
		when(mockDistributedSystem.isConnected()).thenReturn(false);

		assertThat(GemfireUtils.isDurable(mockClientCache)).isFalse();

		verify(mockClientCache, times(1)).getDistributedSystem();
		verify(mockDistributedSystem, times(1)).isConnected();
		verify(mockDistributedSystem, never()).getProperties();
	}
}
