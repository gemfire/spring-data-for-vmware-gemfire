/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire;

import java.util.Optional;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.RegionShortcut;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

/**
 * Spring {@link Converter} to convert a {@link RegionShortcut} into a {@link DataPolicy}.
 *
 * @author John Blum
 * @see DataPolicy
 * @see RegionShortcut
 * @see Converter
 * @see RegionShortcutWrapper
 * @since 2.0.2
 */
public class RegionShortcutToDataPolicyConverter implements Converter<RegionShortcut, DataPolicy> {

	public static final RegionShortcutToDataPolicyConverter INSTANCE = new RegionShortcutToDataPolicyConverter();

	/**
	 * Converts the given {@link RegionShortcut} into a corresponding {@link DataPolicy}.
	 *
	 * @param regionShortcut {@link RegionShortcut} to convert.
	 * @return a corresponding {@link DataPolicy} for the given {@link RegionShortcut}.
	 * @see RegionShortcut
	 * @see DataPolicy
	 */
	@Nullable @Override
	public DataPolicy convert(RegionShortcut regionShortcut) {

		return Optional.ofNullable(RegionShortcutWrapper.valueOf(regionShortcut))
			.map(RegionShortcutWrapper::getDataPolicy)
			.orElse(DataPolicy.DEFAULT);
	}
}
