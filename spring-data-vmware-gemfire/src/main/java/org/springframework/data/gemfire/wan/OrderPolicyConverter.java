/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
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
 * @see GatewaySender.OrderPolicy
 * @see AbstractPropertyEditorConverterSupport
 * @see OrderPolicyType
 * @since 1.7.0
 */
@SuppressWarnings({ "deprecation", "unused" })
public class OrderPolicyConverter extends AbstractPropertyEditorConverterSupport<GatewaySender.OrderPolicy> {

	/**
	 * Converts the given String into a GemFire Gateway.OrderPolicy enum.
	 *
	 * @param source the String to convert.
	 * @return a GemFire Gateway.OrderPolicy enum for the given String.
	 * @throws IllegalArgumentException if the String is not a valid GemFire Gateway.OrderPolicy.
	 * @see OrderPolicyType#getOrderPolicy()
	 * @see OrderPolicyType#valueOfIgnoreCase(String)
	 * @see org.apache.geode.cache.util.Gateway.OrderPolicy
	 */
	@Override
	public GatewaySender.OrderPolicy convert(String source) {

		return assertConverted(source, OrderPolicyType.getOrderPolicy(OrderPolicyType.valueOfIgnoreCase(source)),
			GatewaySender.OrderPolicy.class);
	}
}
