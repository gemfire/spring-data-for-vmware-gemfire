/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.wan.GatewayEventSubstitutionFilter;
import org.apache.geode.cache.wan.GatewayReceiver;
import org.apache.geode.cache.wan.GatewaySender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing GemFire 8 {@link GatewaySender} and {@link GatewayReceiver} configuration
 * using SDG XML namespace configuration metadata.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.wan.GatewayReceiver
 * @see org.apache.geode.cache.wan.GatewaySender
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest
 * @see org.springframework.data.gemfire.wan.GatewayReceiverFactoryBean
 * @see org.springframework.data.gemfire.wan.GatewaySenderFactoryBean
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.0.0
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class GemfireV8GatewayNamespaceIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("gateway-sender-with-event-substitution-filter")
	private GatewaySender gatewaySenderWithEventSubstitutionFilter;

	@Autowired
	@Qualifier("gateway-sender-with-event-substitution-filter-ref")
	private GatewaySender gatewaySenderWithEventSubstitutionFilterRef;

	@Test
	public void testGatewaySenderEventSubstitutionFilter() {

		assertThat(gatewaySenderWithEventSubstitutionFilter)
			.describedAs("The 'gatewaySenderEventSubtitutionFilter' bean was not properly configured and initialized")
			.isNotNull();

		assertThat(gatewaySenderWithEventSubstitutionFilter.getId()).isEqualTo("gateway-sender-with-event-substitution-filter");
		assertThat(gatewaySenderWithEventSubstitutionFilter.getRemoteDSId()).isEqualTo(3);
		assertThat(gatewaySenderWithEventSubstitutionFilter.getDispatcherThreads()).isEqualTo(10);
		assertThat(gatewaySenderWithEventSubstitutionFilter.isParallel()).isTrue();
		assertThat(gatewaySenderWithEventSubstitutionFilter.isRunning()).isFalse();
		assertThat(gatewaySenderWithEventSubstitutionFilter.getGatewayEventSubstitutionFilter()).isNotNull();
		assertThat(gatewaySenderWithEventSubstitutionFilter.getGatewayEventSubstitutionFilter().toString())
			.isEqualTo("inner");
	}

	@Test
	public void testGatewaySenderEventSubstitutionFilterRef() {

		assertThat(gatewaySenderWithEventSubstitutionFilterRef)
			.describedAs("The 'gatewaySenderEventSubtitutionFilter' bean was not properly configured and initialized")
			.isNotNull();

		assertThat(gatewaySenderWithEventSubstitutionFilterRef.getId()).isEqualTo("gateway-sender-with-event-substitution-filter-ref");
		assertThat(gatewaySenderWithEventSubstitutionFilterRef.getRemoteDSId()).isEqualTo(33);
		assertThat(gatewaySenderWithEventSubstitutionFilterRef.getDispatcherThreads()).isEqualTo(1);
		assertThat(gatewaySenderWithEventSubstitutionFilterRef.isParallel()).isFalse();
		assertThat(gatewaySenderWithEventSubstitutionFilterRef.isRunning()).isFalse();
		assertThat(gatewaySenderWithEventSubstitutionFilterRef.getGatewayEventSubstitutionFilter()).isNotNull();
		assertThat(gatewaySenderWithEventSubstitutionFilterRef.getGatewayEventSubstitutionFilter().toString())
			.isEqualTo("ref");
	}

	public static class TestGatewayEventSubstitutionFilter implements GatewayEventSubstitutionFilter<Object, Object> {

		private String name;

		public final void setName(String name) {
			this.name = name;
		}

		protected String getName() {
			return this.name;
		}

		@Override
		public Object getSubstituteValue(EntryEvent<Object, Object> objectObjectEntryEvent) {
			throw new UnsupportedOperationException("Not Implemented");
		}

		@Override
		public void close() { }

		@Override
		public String toString() {
			return getName();
		}
	}
}
