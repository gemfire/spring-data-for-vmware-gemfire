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

import java.beans.PropertyEditor;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.gemfire.gud.api.GudScope;
import org.springframework.data.gemfire.support.AbstractPropertyEditorConverterSupport;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * The {@link ScopeConverter} class is a Spring {@link Converter} and JavaBeans {@link PropertyEditor}
 * that converts a {@link String} into a {@link GudScope}.
 *
 * @author John Blum
 * @see PropertyEditor
 * @see GudScope
 * @see Converter
 * @see AbstractPropertyEditorConverterSupport
 * @since 1.6.0
 */
@SuppressWarnings("unused")
public class ScopeConverter extends AbstractPropertyEditorConverterSupport<GudScope> {

	/**
	 * Converts the given {@link String} into an instance of {@link GudScope}.
	 *
	 * @param source the String to convert into a GemFire Scope.
	 * @return a GemFire Scope for the given String.
	 * @throws IllegalArgumentException if the String is not a valid GemFire Scope.
	 * @see ScopeType#valueOfIgnoreCase(String)
	 * @see ScopeType#getScope(ScopeType)
	 */
	@Override
	public @NonNull GudScope convert(@Nullable String source) {

		try {
			return GudScope.valueOf(source.toUpperCase().replace("-", "_"));
		}
		catch (IllegalArgumentException cause) {
			return assertConverted(source, ScopeType.getScope(ScopeType.valueOfIgnoreCase(source)), GudScope.class);
		}
	}
}
