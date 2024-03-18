/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.annotation.test.entities;

import org.apache.geode.cache.client.ClientRegionShortcut;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.ClientRegion;

/**
 * {@link ClientRegionEntity} persistent entity stored in the "Users" {@link org.apache.geode.cache.DataPolicy#NORMAL},
 * client {@link org.apache.geode.cache.Region}.
 *
 * @author John Blum
 * @since 1.9.0
 */
@ClientRegion(name = "Sessions", shortcut = ClientRegionShortcut.CACHING_PROXY)
public class ClientRegionEntity {

	@Id
	private String id;

}
