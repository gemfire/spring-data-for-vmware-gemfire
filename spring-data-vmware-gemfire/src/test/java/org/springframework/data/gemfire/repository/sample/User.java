// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.repository.sample;

import java.time.Instant;
import java.util.Calendar;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.Region;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Abstract Data Type (ADT) modeling an authorized user of an application, software service or computer system.
 *
 * @author John Blum
 * @see java.lang.Comparable
 * @see org.springframework.data.annotation.Id
 * @see org.springframework.data.gemfire.mapping.annotation.Region
 * @see Region
 * @since 1.4.0
 */
@Region("Users")
@SuppressWarnings("unused")
public class User implements Comparable<User> {

	private Boolean active = true;

	private Instant since;

	private String email;

	@Id
	private final String username;

	public User(String username) {
		Assert.hasText(username, "Username is required");
		this.username = username;
	}

	public void setActive(Boolean active) {
		this.active = Boolean.TRUE.equals(active);
	}

	public Boolean getActive() {
		return active;
	}

	public boolean isActive() {
		return Boolean.TRUE.equals(getActive());
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public String getEmail() {
		return this.email;
	}

	@Deprecated
	public void setSince(Calendar since) {
		setSince(Instant.ofEpochMilli(since.getTimeInMillis()));
	}

	public void setSince(Instant since) {
		this.since = since;
	}

	public Instant getSince() {
		return this.since;
	}

	public String getUsername() {
		return this.username;
	}

	@Override
	public int compareTo(final User user) {
		return getUsername().compareTo(user.getUsername());
	}

	protected static boolean equalsIgnoreNull(Object obj1, Object obj2) {
		return Objects.equals(obj1, obj2);
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof User)) {
			return false;
		}

		User that = (User) obj;

		return this.getUsername().equals(that.getUsername())
			&& ObjectUtils.nullSafeEquals(this.getEmail(), that.getEmail());
	}

	protected static int hashCodeIgnoreNull(Object obj) {
		return obj != null ? obj.hashCode() : 0;
	}

	@Override
	public int hashCode() {

		int hashValue = 17;

		hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getEmail());
		hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getUsername());

		return hashValue;
	}

	@Override
	public String toString() {
		return getUsername();
	}
}
