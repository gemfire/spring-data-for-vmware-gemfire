/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.test.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.LuceneIndexed;
import org.springframework.data.gemfire.mapping.annotation.Region;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Abstract Data Type (ADT) modeling a {@literal Book} application domain type.
 *
 * @author John Blum
 * @see Comparable
 * @see Serializable
 * @see LuceneIndexed
 * @see Region
 * @see lombok
 * @since 2.2.0
 */
@Data
@Region("Books")
@RequiredArgsConstructor(staticName = "newBook")
public class Book implements Comparable<Book>, Serializable {

	@NonNull @Id
	private final Long id;

	@NonNull @LuceneIndexed(name = "BookTitleIdx")
	private String title;

	@Override
	public int compareTo(Book other) {
		return this.getTitle().compareTo(other.getTitle());
	}

	@Override
	public String toString() {
		return this.getTitle();
	}
}
