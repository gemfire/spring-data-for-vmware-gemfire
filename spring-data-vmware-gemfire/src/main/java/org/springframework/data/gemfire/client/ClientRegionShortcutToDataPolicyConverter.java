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

import java.util.Optional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.gemfire.gud.api.GudClientRegionShortcut;
import org.springframework.data.gemfire.gud.api.GudDataPolicy;
import org.springframework.lang.Nullable;

/**
 * Spring {@link Converter} to convert a {@link GudClientRegionShortcut} into a {@link GudDataPolicy}.
 *
 * @author John Blum
 * @see GudDataPolicy
 * @see GudClientRegionShortcut
 * @see Converter
 * @see ClientRegionShortcutWrapper
 * @since 2.0.2
 */
public class ClientRegionShortcutToDataPolicyConverter implements Converter<GudClientRegionShortcut, GudDataPolicy> {

	public static final ClientRegionShortcutToDataPolicyConverter INSTANCE =
		new ClientRegionShortcutToDataPolicyConverter();

	/**
	 * Converts the given {@link GudClientRegionShortcut} into a corresponding {@link GudDataPolicy}.
	 *
	 * @param clientRegionShortcut {@link GudClientRegionShortcut} to convert.
	 * @return a corresponding {@link GudDataPolicy} for the given {@link GudClientRegionShortcut}.
	 * @see GudClientRegionShortcut
	 * @see GudDataPolicy
	 */
	@Nullable @Override
	public GudDataPolicy convert(GudClientRegionShortcut clientRegionShortcut) {

		return Optional.ofNullable(ClientRegionShortcutWrapper.valueOf(clientRegionShortcut))
			.map(ClientRegionShortcutWrapper::getDataPolicy)
			.orElse(GudDataPolicy.DEFAULT);
	}
}
