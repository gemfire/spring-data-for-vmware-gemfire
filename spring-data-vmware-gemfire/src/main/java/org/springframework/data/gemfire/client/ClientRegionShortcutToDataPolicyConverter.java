/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client;

import java.util.Optional;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.client.ClientRegionShortcut;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

/**
 * Spring {@link Converter} to convert a {@link ClientRegionShortcut} into a {@link DataPolicy}.
 *
 * @author John Blum
 * @see org.apache.geode.cache.DataPolicy
 * @see org.apache.geode.cache.client.ClientRegionShortcut
 * @see org.springframework.core.convert.converter.Converter
 * @see org.springframework.data.gemfire.client.ClientRegionShortcutWrapper
 * @since 2.0.2
 */
public class ClientRegionShortcutToDataPolicyConverter implements Converter<ClientRegionShortcut, DataPolicy> {

	public static final ClientRegionShortcutToDataPolicyConverter INSTANCE =
		new ClientRegionShortcutToDataPolicyConverter();

	/**
	 * Converts the given {@link ClientRegionShortcut} into a corresponding {@link DataPolicy}.
	 *
	 * @param clientRegionShortcut {@link ClientRegionShortcut} to convert.
	 * @return a corresponding {@link DataPolicy} for the given {@link ClientRegionShortcut}.
	 * @see org.apache.geode.cache.client.ClientRegionShortcut
	 * @see org.apache.geode.cache.DataPolicy
	 */
	@Nullable @Override
	public DataPolicy convert(ClientRegionShortcut clientRegionShortcut) {

		return Optional.ofNullable(ClientRegionShortcutWrapper.valueOf(clientRegionShortcut))
			.map(ClientRegionShortcutWrapper::getDataPolicy)
			.orElse(DataPolicy.DEFAULT);
	}
}
