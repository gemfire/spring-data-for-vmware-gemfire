/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.repository.sample;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.Region;

/**
 * The Algorithm interface define abstract data type modeling a computer algorithm.
 *
 * @author John Blum
 * @see org.springframework.data.annotation.Id
 * @see Region
 * @since 1.4.0
 */
@Region("Algorithms")
@SuppressWarnings("unused")
public interface Algorithm {

	@Id String getName();

}
