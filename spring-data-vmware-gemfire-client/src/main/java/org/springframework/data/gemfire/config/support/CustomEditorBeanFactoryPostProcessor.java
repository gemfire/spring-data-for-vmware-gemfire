/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.support;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

import org.apache.geode.cache.EvictionAction;
import org.apache.geode.cache.ExpirationAction;
import org.apache.geode.cache.InterestPolicy;
import org.apache.geode.cache.InterestResultPolicy;
import org.apache.geode.cache.Scope;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.convert.converter.Converter;
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
import org.springframework.data.gemfire.support.AbstractPropertyEditorConverterSupport;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.data.gemfire.support.ConnectionEndpointList;

/**
 * {@link CustomEditorBeanFactoryPostProcessor} is a Spring {@link BeanFactoryPostProcessor} implementation
 * used to register custom {@link PropertyEditor PropertyEditors} / Spring {@link Converter Converters}
 * that are used to perform type conversions between {@link String String-based} configuration metadata
 * and actual Apache Geode or Spring Data for Apache Geode defined (enumerated) types.
 *
 * @author John Blum
 * @see PropertyEditor
 * @see PropertyEditorSupport
 * @see PropertyEditorRegistrar
 * @see PropertyEditorRegistry
 * @see BeanFactoryPostProcessor
 * @since 1.6.0
 */
public class CustomEditorBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

		beanFactory.addPropertyEditorRegistrar(new CustomEditorPropertyEditorRegistrar());
		//registerCustomEditors(beanFactory);
	}

	@SuppressWarnings("unused")
	private void registerCustomEditors(ConfigurableListableBeanFactory beanFactory) {

		if (beanFactory != null) {
			beanFactory.registerCustomEditor(ConnectionEndpoint.class, StringToConnectionEndpointConverter.class);
			//beanFactory.registerCustomEditor(ConnectionEndpoint[].class, ConnectionEndpointArrayToIterableConverter.class);
			beanFactory.registerCustomEditor(ConnectionEndpointList.class, StringToConnectionEndpointListConverter.class);
			beanFactory.registerCustomEditor(EvictionAction.class, EvictionActionConverter.class);
			beanFactory.registerCustomEditor(EvictionPolicyType.class, EvictionPolicyConverter.class);
			beanFactory.registerCustomEditor(ExpirationAction.class, ExpirationActionConverter.class);
			beanFactory.registerCustomEditor(IndexMaintenancePolicyType.class, IndexMaintenancePolicyConverter.class);
			beanFactory.registerCustomEditor(IndexType.class, IndexTypeConverter.class);
			beanFactory.registerCustomEditor(InterestPolicy.class, InterestPolicyConverter.class);
			beanFactory.registerCustomEditor(InterestResultPolicy.class, InterestResultPolicyConverter.class);
			beanFactory.registerCustomEditor(Scope.class, ScopeConverter.class);
		}
	}

	public static class CustomEditorPropertyEditorRegistrar implements PropertyEditorRegistrar {

		@Override
		public void registerCustomEditors(PropertyEditorRegistry registry) {

			if (registry != null) {
				registry.registerCustomEditor(ConnectionEndpoint.class, new StringToConnectionEndpointConverter());
				//registry.registerCustomEditor(ConnectionEndpoint[].class, new ConnectionEndpointArrayToIterableConverter()));
				registry.registerCustomEditor(ConnectionEndpointList.class, new StringToConnectionEndpointListConverter());
				registry.registerCustomEditor(EvictionAction.class, new EvictionActionConverter());
				registry.registerCustomEditor(EvictionPolicyType.class, new EvictionPolicyConverter());
				registry.registerCustomEditor(ExpirationAction.class, new ExpirationActionConverter());
				registry.registerCustomEditor(IndexMaintenancePolicyType.class, new IndexMaintenancePolicyConverter());
				registry.registerCustomEditor(IndexType.class, new IndexTypeConverter());
				registry.registerCustomEditor(InterestPolicy.class, new InterestPolicyConverter());
				registry.registerCustomEditor(InterestResultPolicy.class, new InterestResultPolicyConverter());
				registry.registerCustomEditor(Scope.class, new ScopeConverter());
			}
		}
	}
	public static class ConnectionEndpointArrayToIterableConverter extends PropertyEditorSupport
			implements Converter<ConnectionEndpoint[], Iterable<?>>  {

		/**
		 * {@inheritDoc}
		 */
		@Override
		@SuppressWarnings("all")
		public Iterable convert(ConnectionEndpoint[] source) {
			return ConnectionEndpointList.from(source);
		}
	}

	public static class StringToConnectionEndpointConverter
			extends AbstractPropertyEditorConverterSupport<ConnectionEndpoint> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ConnectionEndpoint convert(String source) {
			return assertConverted(source, ConnectionEndpoint.parse(source), ConnectionEndpoint.class);
		}
	}

	public static class StringToConnectionEndpointListConverter
			extends AbstractPropertyEditorConverterSupport<ConnectionEndpointList> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ConnectionEndpointList convert(String source) {
			return assertConverted(source, ConnectionEndpointList.parse(0, source.split(",")),
				ConnectionEndpointList.class);
		}
	}
}
