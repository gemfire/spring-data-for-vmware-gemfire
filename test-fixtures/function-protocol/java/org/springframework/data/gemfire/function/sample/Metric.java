/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.sample;

import java.io.Serializable;

/**
 * @author Patrick Johnson
 */
public class Metric implements Serializable {

	private final String name;

	private final Number value;

	private final String category;

	private final String type;

	public Metric(String name, Number value, String category, String type) {
		this.name = name;
		this.value = value;
		this.category = category;
		this.type = type;
	}

	public String getName() { return this.name; }

	public Number getValue() { return this.value; }

	public String getCategory() { return this.category; }

	public String getType() { return this.type; }

	@Override
	public String toString() {
		return "Metric(name=" + this.name + ", value=" + this.value
			+ ", category=" + this.category + ", type=" + this.type + ")";
	}

}
