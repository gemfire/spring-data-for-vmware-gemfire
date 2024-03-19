/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;

import org.apache.geode.cache.EvictionAction;
import org.apache.geode.cache.ExpirationAction;
import org.apache.geode.cache.InterestPolicy;
import org.apache.geode.cache.InterestResultPolicy;
import org.apache.geode.cache.Scope;
import org.apache.geode.cache.wan.GatewaySender;

import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.data.gemfire.IndexMaintenancePolicyConverter;
import org.springframework.data.gemfire.IndexMaintenancePolicyType;
import org.springframework.data.gemfire.IndexType;
import org.springframework.data.gemfire.IndexTypeConverter;
import org.springframework.data.gemfire.InterestPolicyConverter;
import org.springframework.data.gemfire.ScopeConverter;
import org.springframework.data.gemfire.client.InterestResultPolicyConverter;
import org.springframework.data.gemfire.eviction.EvictionActionConverter;
import org.springframework.data.gemfire.eviction.EvictionPolicyConverter;
import org.springframework.data.gemfire.eviction.EvictionPolicyType;
import org.springframework.data.gemfire.expiration.ExpirationActionConverter;
import org.springframework.data.gemfire.server.SubscriptionEvictionPolicy;
import org.springframework.data.gemfire.server.SubscriptionEvictionPolicyConverter;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.data.gemfire.support.ConnectionEndpointList;
import org.springframework.data.gemfire.wan.OrderPolicyConverter;
import org.springframework.util.StringUtils;

/**
 * Unit Tests for {@link CustomEditorBeanFactoryPostProcessor}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see CustomEditorBeanFactoryPostProcessor
 * @since 1.6.0
 */
public class CustomEditorBeanFactoryPostProcessorUnitTests {

	private ConnectionEndpoint newConnectionEndpoint(String host, int port) {
		return new ConnectionEndpoint(host, port);
	}

	@Test
	public void customEditorRegistrationIsSuccessful() {

		PropertyEditorRegistry mockRegistry = mock(PropertyEditorRegistry.class);

		new CustomEditorBeanFactoryPostProcessor.CustomEditorPropertyEditorRegistrar().registerCustomEditors(mockRegistry);

		verify(mockRegistry, times(1)).registerCustomEditor(eq(ConnectionEndpoint.class),
			isA(CustomEditorBeanFactoryPostProcessor.StringToConnectionEndpointConverter.class));
		//verify(mockBeanFactory, times(1)).registerCustomEditor(eq(ConnectionEndpoint[].class),
		//	eq(CustomEditorBeanFactoryPostProcessor.ConnectionEndpointArrayToIterableConverter.class));
		verify(mockRegistry, times(1)).registerCustomEditor(eq(ConnectionEndpointList.class),
			isA(CustomEditorBeanFactoryPostProcessor.StringToConnectionEndpointListConverter.class));
		verify(mockRegistry, times(1)).registerCustomEditor(eq(EvictionAction.class),
			isA(EvictionActionConverter.class));
		verify(mockRegistry, times(1)).registerCustomEditor(eq(EvictionPolicyType.class),
			isA(EvictionPolicyConverter.class));
		verify(mockRegistry, times(1)).registerCustomEditor(eq(ExpirationAction.class),
			isA(ExpirationActionConverter.class));
		verify(mockRegistry, times(1)).registerCustomEditor(eq(IndexMaintenancePolicyType.class),
			isA(IndexMaintenancePolicyConverter.class));
		verify(mockRegistry, times(1)).registerCustomEditor(eq(IndexType.class),
			isA(IndexTypeConverter.class));
		verify(mockRegistry, times(1)).registerCustomEditor(eq(InterestPolicy.class),
			isA(InterestPolicyConverter.class));
		verify(mockRegistry, times(1)).registerCustomEditor(eq(InterestResultPolicy.class),
			isA(InterestResultPolicyConverter.class));
		verify(mockRegistry, times(1)).registerCustomEditor(eq(Scope.class), isA(ScopeConverter.class));
		verify(mockRegistry, times(1)).registerCustomEditor(eq(GatewaySender.OrderPolicy.class),
			isA(OrderPolicyConverter.class));
		verify(mockRegistry, times(1)).registerCustomEditor(eq(Scope.class), isA(ScopeConverter.class));
		verify(mockRegistry, times(1)).registerCustomEditor(eq(SubscriptionEvictionPolicy.class),
			isA(SubscriptionEvictionPolicyConverter.class));

		verifyNoMoreInteractions(mockRegistry);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void connectionEndpointArrayToIterableConversionIsSuccessful() {

		ConnectionEndpoint[] array = {
			newConnectionEndpoint("localhost", 10334),
			newConnectionEndpoint("localhost", 40404)
		};

		Iterable<ConnectionEndpoint> iterable = new CustomEditorBeanFactoryPostProcessor
			.ConnectionEndpointArrayToIterableConverter().convert(array);

		assertThat(iterable).isNotNull();

		int index = 0;

		for (ConnectionEndpoint connectionEndpoint : iterable) {
			assertThat(connectionEndpoint).isEqualTo(array[index++]);
		}

		assertThat(index).isEqualTo(array.length);
	}

	@Test
	public void stringToConnectionEndpointConversionIsSuccessful() {

		String hostPort = "skullbox[54321]";

		ConnectionEndpoint connectionEndpoint = new CustomEditorBeanFactoryPostProcessor
			.StringToConnectionEndpointConverter().convert(hostPort);

		assertThat(connectionEndpoint).isNotNull();
		assertThat(connectionEndpoint.getHost()).isEqualTo("skullbox");
		assertThat(connectionEndpoint.getPort()).isEqualTo(54321);
	}

	@Test
	public void stringToConnectionEndpointListConversionIsSuccessful() {

		String[] hostsPorts = { "toolbox[10334]", "skullbox", "[40404]" };
		String source = StringUtils.arrayToCommaDelimitedString(hostsPorts);

		ConnectionEndpointList connectionEndpoints = new CustomEditorBeanFactoryPostProcessor
			.StringToConnectionEndpointListConverter().convert(source);

		assertThat(connectionEndpoints).isNotNull();
		assertThat(connectionEndpoints.size()).isEqualTo(hostsPorts.length);
		assertThat(connectionEndpoints.findOne("toolbox").getPort()).isEqualTo(10334);
		assertThat(connectionEndpoints.findOne("skullbox").getPort()).isEqualTo(0);
		assertThat(connectionEndpoints.findOne(40404).getHost()).isEqualTo("localhost");
	}
}
