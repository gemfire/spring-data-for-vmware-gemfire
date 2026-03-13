/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.client;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.gemfire.gud.api.GudClientRegionShortcut;

/**
 * The ClientRegionShortcutConverter class is a Spring Converter implementation converting String value Client Region
 * Shortcut representations into actual GemFire ClientRegionShortcut enumerated values.
 *
 * @author John Blum
 * @see Converter
 * @see GudClientRegionShortcut
 * @since 1.3.4
 */
@SuppressWarnings("unused")
public class ClientRegionShortcutConverter implements Converter<String, GudClientRegionShortcut> {

	/**
	 * Converts the String value to upper case, trimming all whitespace.  This method guards against null values
	 * and returns the "null" String if value is null.
	 *
	 * @param value the String to convert to a trimmed, upper case value.
	 * @return a trimmed, upper case value of the specified String, or "null" if the String value reference is null.
	 * @see String#toUpperCase()
	 * @see String#trim()
	 * @see String#valueOf(Object)
	 */
	protected static String toUpperCase(final String value) {
		return (value != null ? value.toUpperCase().trim() : String.valueOf(value));
	}

	/**
	 * Converts the source String representation of a Client Region Shortcut into a ClientRegionShortcut enumerated
	 * value.
	 *
	 * @param source the String representation of the Client Region Shortcut to convert.
	 * @return a ClientRegionShortcut enumerated value for the String representation.
	 * @throws IllegalArgumentException if the String source is not a valid ClientRegionShortcut enumerated value.
	 * @see GudClientRegionShortcut#valueOf(String)
	 */
	@Override
	public GudClientRegionShortcut convert(final String source) {
		return GudClientRegionShortcut.valueOf(toUpperCase(source));
	}

}
