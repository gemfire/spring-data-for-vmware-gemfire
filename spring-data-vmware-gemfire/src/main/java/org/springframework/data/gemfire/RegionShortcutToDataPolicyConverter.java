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

import java.util.Optional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.gemfire.gud.api.GudDataPolicy;
import org.springframework.data.gemfire.gud.api.GudRegionShortcut;
import org.springframework.lang.Nullable;

/**
 * Spring {@link Converter} to convert a {@link GudRegionShortcut} into a {@link GudDataPolicy}.
 *
 * @author John Blum
 * @see GudDataPolicy
 * @see GudRegionShortcut
 * @see Converter
 * @see RegionShortcutWrapper
 * @since 2.0.2
 */
public class RegionShortcutToDataPolicyConverter implements Converter<GudRegionShortcut, GudDataPolicy> {

	public static final RegionShortcutToDataPolicyConverter INSTANCE = new RegionShortcutToDataPolicyConverter();

	/**
	 * Converts the given {@link GudRegionShortcut} into a corresponding {@link GudDataPolicy}.
	 *
	 * @param regionShortcut {@link GudRegionShortcut} to convert.
	 * @return a corresponding {@link GudDataPolicy} for the given {@link GudRegionShortcut}.
	 * @see GudRegionShortcut
	 * @see GudDataPolicy
	 */
	@Nullable @Override
	public GudDataPolicy convert(GudRegionShortcut regionShortcut) {

		return Optional.ofNullable(RegionShortcutWrapper.valueOf(regionShortcut))
			.map(RegionShortcutWrapper::getDataPolicy)
			.orElse(GudDataPolicy.DEFAULT);
	}
}
