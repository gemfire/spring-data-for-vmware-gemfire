/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.wan;

import java.beans.PropertyEditor;

import org.apache.geode.cache.wan.GatewaySender;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.gemfire.support.AbstractPropertyEditorConverterSupport;

/**
 * The {@link OrderPolicyConverter} class is a Spring {@link Converter} and JavaBeans {@link PropertyEditor} used to
 * convert a {@link String} into an appropriate {@link GatewaySender.OrderPolicy} enum.
 *
 * @author John Blum
 * @see org.apache.geode.cache.wan.GatewaySender.OrderPolicy
 * @see org.springframework.data.gemfire.support.AbstractPropertyEditorConverterSupport
 * @see org.springframework.data.gemfire.wan.OrderPolicyType
 * @since 1.7.0
 */
@SuppressWarnings({ "deprecation", "unused" })
public class OrderPolicyConverter extends AbstractPropertyEditorConverterSupport<GatewaySender.OrderPolicy> {

	/**
	 * Converts the given String into a GemFire Gateway.OrderPolicy enum.
	 *
	 * @param source the String to convert.
	 * @return a GemFire Gateway.OrderPolicy enum for the given String.
	 * @throws java.lang.IllegalArgumentException if the String is not a valid GemFire Gateway.OrderPolicy.
	 * @see org.springframework.data.gemfire.wan.OrderPolicyType#getOrderPolicy()
	 * @see org.springframework.data.gemfire.wan.OrderPolicyType#valueOfIgnoreCase(String)
	 * @see org.apache.geode.cache.util.Gateway.OrderPolicy
	 */
	@Override
	public GatewaySender.OrderPolicy convert(String source) {

		return assertConverted(source, OrderPolicyType.getOrderPolicy(OrderPolicyType.valueOfIgnoreCase(source)),
			GatewaySender.OrderPolicy.class);
	}
}
