/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.sample;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Patrick Johnson
 */
@Getter
@ToString
@AllArgsConstructor
public class Metric implements Serializable {

	private final String name;

	private final Number value;

	private final String category;

	private final String type;

}
