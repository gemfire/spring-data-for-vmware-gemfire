/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.repository.sample;

import org.springframework.data.gemfire.mapping.annotation.Region;
import org.springframework.util.StringUtils;

/**
 * The Programmer class is a User representing/modeling a software engineer/developer.
 *
 * @author John J. Blum
 * @see Region
 * @see org.springframework.data.gemfire.repository.sample.User
 * @since 1.4.0
 */
@Region("Programmers")
@SuppressWarnings("unused")
public class Programmer extends User {

	protected static final String DEFAULT_PROGRAMMING_LANGUAGE = "?";

	private String programmingLanguage;

	public Programmer(final String username) {
		super(username);
	}

	public String getProgrammingLanguage() {
		return (StringUtils.hasText(programmingLanguage) ? programmingLanguage : DEFAULT_PROGRAMMING_LANGUAGE);
	}

	public void setProgrammingLanguage(final String programmingLanguage) {
		this.programmingLanguage = programmingLanguage;
	}

	@Override
	public String toString() {
		return String.format("%1$s programs in '%2$s.", getUsername(), getProgrammingLanguage());
	}

}
