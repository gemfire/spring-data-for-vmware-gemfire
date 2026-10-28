/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.fixtures.template;

import java.io.Serializable;
import java.time.Instant;
import java.util.Calendar;
import java.util.Objects;

/**
 * Abstract Data Type (ADT) modeling an authorized user of an application, software service or computer system.
 *
 * @author John Blum
 * @see java.lang.Comparable
 * @since 1.4.0
 */
@SuppressWarnings("unused")
public class User implements Comparable<User>, Serializable {

	private static final long serialVersionUID = 1L;

	private Boolean active = true;

	private Instant since;

	private String email;

	private final String username;

	public User(String username) {
		if (username == null || username.isBlank()) {
			throw new IllegalArgumentException("Username is required");
		}
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
			&& Objects.equals(this.getEmail(), that.getEmail());
	}

	protected static int hashCodeIgnoreNull(Object obj) {
		return obj != null ? obj.hashCode() : 0;
	}

	@Override
	public int hashCode() {

		int hashValue = 17;

		hashValue = 37 * hashValue + Objects.hashCode(getEmail());
		hashValue = 37 * hashValue + Objects.hashCode(getUsername());

		return hashValue;
	}

	@Override
	public String toString() {
		return getUsername();
	}
}
