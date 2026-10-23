/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.LoaderHelper;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

/**
 * Verifies lazy Spring wiring of a client cache loader declared in native cache XML.
 *
 * @author John Blum
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
public class LazyWiringDeclarableSupportClientLoaderIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private ClientCache gemfireCache;

	@Test
	public void nativeXmlParametersOverrideInjectedDefaultAfterLazyWiring() {

		Region<String, String> greetings = this.gemfireCache.getRegion("greetings");
		HelloCacheLoader loader = (HelloCacheLoader) greetings.getAttributes().getCacheLoader();

		assertThat(loader.isInitialized()).isTrue();
		assertThat(loader.getDefaultAddressTo()).isEqualTo("Universe");
		assertThat(greetings.get("defaultGreeting")).isEqualTo("Hello Everyone");
	}

	@Test
	public void callerArgumentOverridesNativeXmlParameter() {

		Region<String, String> greetings = this.gemfireCache.getRegion("greetings");

		assertThat(greetings.get("callerGreeting", "Ada")).isEqualTo("Hello Ada");
	}

	public static class HelloCacheLoader extends LazyWiringDeclarableSupport implements CacheLoader<String, String> {

		@Value("${hello.default.address.to}")
		private String defaultAddressTo;

		private String addressTo;

		String getDefaultAddressTo() {
			return StringUtils.hasText(this.defaultAddressTo) ? this.defaultAddressTo : "World";
		}

		@Override
		protected void doPostInit(Properties parameters) {
			this.addressTo = parameters.getProperty("hello.address.to", getDefaultAddressTo());
		}

		@Override
		public String load(LoaderHelper<String, String> helper) {

			assertInitialized();

			Object argument = helper.getArgument();
			String recipient = argument != null ? argument.toString() : null;

			return "Hello " + (StringUtils.hasText(recipient) ? recipient : this.addressTo);
		}

		@Override
		public void close() {
			try {
				destroy();
			}
			catch (Exception cause) {
				throw new IllegalStateException("Failed to destroy client cache loader", cause);
			}
		}
	}
}
