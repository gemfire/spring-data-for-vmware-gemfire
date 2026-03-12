/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Migrated to GUD API types
 */

package org.springframework.data.gemfire;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.gemfire.gud.api.GudRegionShortcut;

/**
 * The RegionShortcutConverter class is a Spring Converter implementation converting String value Region Shortcut
 * representations into actual GemFire RegionShortcut enumerated values.
 *
 * @author John Blum
 * @see Converter
 * @see GudRegionShortcut
 * @since 1.3.4
 */
@SuppressWarnings("unused")
public class RegionShortcutConverter implements Converter<String, GudRegionShortcut> {

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
	 * Converts the source String representation of a Region Shortcut into a RegionShortcut enumerated value.
	 *
	 * @param source the String representation of the Region Shortcut to convert.
	 * @return a RegionShortcut enumerated value for the String representation.
	 * @throws IllegalArgumentException if the String source is not a valid RegionShortcut enumerated value.
	 * @see GudRegionShortcut#valueOf(String)
	 */
	@Override
	public GudRegionShortcut convert(final String source) {
		return GudRegionShortcut.valueOf(toUpperCase(source));
	}

}
