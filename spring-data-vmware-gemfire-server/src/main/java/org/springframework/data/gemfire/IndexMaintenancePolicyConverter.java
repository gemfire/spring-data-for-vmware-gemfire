/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire;

import org.springframework.data.gemfire.support.AbstractPropertyEditorConverterSupport;

/**
 * The IndexMaintenanceTypeConverter class is a Spring Converter and JavaBeans PropertyEditor capable of converting
 * a String into a specific SDG IndexMaintenancePolicyType.
 *
 * @author John Blum
 * @see IndexMaintenancePolicyType
 * @see AbstractPropertyEditorConverterSupport
 * @since 1.6.0
 */
@SuppressWarnings("unused")
public class IndexMaintenancePolicyConverter extends AbstractPropertyEditorConverterSupport<IndexMaintenancePolicyType> {

	/**
	 * Converts the given String value into an appropriate IndexMaintenancePolicyType.
	 *
	 * @param source the String value to convert into a IndexMaintenancePolicyType.
	 * @return an IndexMaintenancePolicyType converted from the given String value.
	 * @throws IllegalArgumentException if the String is not a valid IndexMaintenancePolicyType.
	 * @see IndexMaintenancePolicyType#valueOfIgnoreCase(String)
	 * @see #assertConverted(String, Object, Class)
	 */
	@Override
	public IndexMaintenancePolicyType convert(final String source) {
		return assertConverted(source, IndexMaintenancePolicyType.valueOfIgnoreCase(source),
			IndexMaintenancePolicyType.class);
	}

}
