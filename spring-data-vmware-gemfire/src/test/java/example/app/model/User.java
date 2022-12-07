/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.Region;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Abstract Data Type (ADT) modeling a user.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.mapping.annotation.Region
 * @since 2.5.0
 */
@Getter
@ToString
@EqualsAndHashCode
@Setter(AccessLevel.PROTECTED)
@Region("Users")
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "as")
@SuppressWarnings("unused")
public class User {

	@Id
	private Integer id;

	@lombok.NonNull
	private String name;

	public @NonNull User identifiedBy(@Nullable Integer id) {
		setId(id);
		return this;
	}

	public @NonNull User withName(@NonNull String name) {
		Assert.hasText(name, String.format("Name [%s] is required", name));
		setName(name);
		return this;
	}
}
