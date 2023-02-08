/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire;

import org.apache.geode.cache.RegionShortcut;

import org.springframework.core.convert.converter.Converter;

/**
 * The RegionShortcutConverter class is a Spring Converter implementation converting String value Region Shortcut
 * representations into actual GemFire RegionShortcut enumerated values.
 *
 * @author John Blum
 * @see org.springframework.core.convert.converter.Converter
 * @see org.apache.geode.cache.RegionShortcut
 * @since 1.3.4
 */
@SuppressWarnings("unused")
public class RegionShortcutConverter implements Converter<String, RegionShortcut> {

	/**
	 * Converts the String value to upper case, trimming all whitespace.  This method guards against null values
	 * and returns the "null" String if value is null.
	 *
	 * @param value the String to convert to a trimmed, upper case value.
	 * @return a trimmed, upper case value of the specified String, or "null" if the String value reference is null.
	 * @see java.lang.String#toUpperCase()
	 * @see java.lang.String#trim()
	 * @see java.lang.String#valueOf(Object)
	 */
	protected static String toUpperCase(final String value) {
		return (value != null ? value.toUpperCase().trim() : String.valueOf(value));
	}

	/**
	 * Converts the source String representation of a Region Shortcut into a RegionShortcut enumerated value.
	 *
	 * @param source the String representation of the Region Shortcut to convert.
	 * @return a RegionShortcut enumerated value for the String representation.
	 * @throws IllegalArgumentException if the String source is not a valid RegionShortcut enumerated value.
	 * @see org.apache.geode.cache.RegionShortcut#valueOf(String)
	 */
	@Override
	public RegionShortcut convert(final String source) {
		return RegionShortcut.valueOf(toUpperCase(source));
	}

}
