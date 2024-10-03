/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * Unit Tests for {@link IntegrationTestsSupport}.
 *
 * @author John Blum
 * @see Test
 * @see IntegrationTestsSupport
 * @since 1.0.0
 */
public class IntegrationTestsSupportUnitTests {

	@Test
	public void asDirectoryNameIsCorrect() {

		String directoryName = IntegrationTestsSupport.asDirectoryName(OuterType.InnerType.class);

		Object[] args = {
			IntegrationTestsSupportUnitTests.class.getSimpleName(),
			OuterType.class.getSimpleName(),
			OuterType.InnerType.class.getSimpleName()
		};

		assertThat(directoryName).isNotBlank();
		assertThat(directoryName).matches(String.format("%s\\.%s\\.%s", args).concat("[\\w-]+"));
	}

	interface OuterType {
		interface InnerType { }
	}
}
