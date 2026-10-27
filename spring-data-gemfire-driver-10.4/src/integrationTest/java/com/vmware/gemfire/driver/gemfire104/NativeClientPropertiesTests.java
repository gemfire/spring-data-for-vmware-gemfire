/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.internal.net.SSLConfigurationFactory;
import org.apache.geode.internal.security.SecurableCommunicationChannel;
import org.apache.geode.net.SSLParameterExtension;
import org.apache.geode.net.SSLParameterExtensionContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Native consumer evidence for shared properties retained by the client-only Spring API. */
public class NativeClientPropertiesTests {

	@Test
	void clientIdentityIncludesNameGroupsAndDurableAttributes() {

		try (var cache = new ClientCacheFactory()
				.set("log-level", "error")
				.set("name", "client-property-test")
				.set("groups", "client-one,client-two")
				.set("durable-client-id", "durable-property-test")
				.set("durable-client-timeout", "123")
				.create()) {
			var member = cache.getDistributedSystem().getDistributedMember();
			assertThat(member.getName()).isEqualTo("client-property-test");
			assertThat(member.getGroups()).containsExactly("client-one", "client-two");
			assertThat(member.getDurableClientAttributes().getId()).isEqualTo("durable-property-test");
			assertThat(member.getDurableClientAttributes().getTimeout()).isEqualTo(123);
		}
	}

	@Test
	void clientTlsExtensionReceivesConfiguredDistributedSystemId() {

		try (var cache = new ClientCacheFactory()
				.set("log-level", "error")
				.set("distributed-system-id", "17")
				.set("ssl-enabled-components", "locator,server")
				.set("ssl-use-default-context", "true")
				.set("ssl-parameter-extension", CapturingSslExtension.class.getName())
				.create()) {
			assertThat(cache.isClosed()).isFalse();
			for (var channel : new SecurableCommunicationChannel[] {
					SecurableCommunicationChannel.LOCATOR, SecurableCommunicationChannel.SERVER }) {
				var ssl = SSLConfigurationFactory.getSSLConfigForComponent(
					cache.getDistributedSystem().getProperties(), channel);
				assertThat(ssl.isEnabled()).as("TLS enabled for %s", channel).isTrue();
				assertThat(ssl.getSSLParameterExtension()).isInstanceOfSatisfying(CapturingSslExtension.class,
					extension -> assertThat(extension.distributedSystemId).as("extension context for %s", channel)
						.isEqualTo(17));
			}
		}
	}

	public static class CapturingSslExtension implements SSLParameterExtension {

		private Integer distributedSystemId;

		@Override
		public void init(SSLParameterExtensionContext context) {
			this.distributedSystemId = context.getDistributedSystemId();
		}
	}
}
