/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;

import org.apache.geode.internal.net.SSLConfigurationFactory;

import org.springframework.util.ReflectionUtils;

/**
 * Integration Tests asserting that {@link IntegrationTestsSupport} clears the SSL configuration of Apache Geode
 * between test case runs.
 *
 * @author John Blum
 * @see SSLConfigurationFactory
 * @see IntegrationTestsSupport
 * @since 0.0.8
 */
public class SslConfigurationIntegrationTests {

	private SSLConfigurationFactory getInstance() {

		Method getInstance = ReflectionUtils.findMethod(SSLConfigurationFactory.class, "getInstance");

		return Optional.ofNullable(getInstance)
			.map(method -> {

				ReflectionUtils.makeAccessible(method);

				return (SSLConfigurationFactory) ReflectionUtils.invokeMethod(method, null);
			})
			.orElse(null);
	}

	@Test
	@Ignore
	public void getInstanceReturnsSameReferenceBeforeCloseThenReturnsDifferentReferenceAfterClose() {

		SSLConfigurationFactory sslConfigurationFactoryOne = getInstance();

		assertThat(sslConfigurationFactoryOne).isNotNull();
		assertThat(getInstance()).isSameAs(sslConfigurationFactoryOne);

		IntegrationTestsSupport.closeAnySslConfiguration();

		SSLConfigurationFactory sslConfigurationFactoryTwo = getInstance();

		assertThat(sslConfigurationFactoryTwo).isNotNull();
		assertThat(sslConfigurationFactoryTwo).isNotSameAs(sslConfigurationFactoryOne);
	}
}
