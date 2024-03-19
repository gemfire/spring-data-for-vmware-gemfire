/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.gemfire.domain.support.AbstractPageSupport;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * The {@link ListablePage} class is a Spring Data {@link Page} implementation wrapping a {@link List} as the content
 * for this {@link ListablePage page}.
 *
 * @author John Blum
 * @see java.util.Iterator
 * @see java.util.List
 * @see org.springframework.data.domain.Page
 * @see org.springframework.data.gemfire.domain.support.AbstractPageSupport
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class ListablePage<T> extends AbstractPageSupport<T> {

	/**
	 * Factory method used to construct a new instance of {@link ListablePage} initialized with the given array,
	 * serving as the content for this {@link Page page}.
	 *
	 * @param <T> {@link Class} type of the elements in the array.
	 * @param content array of elements serving as the content for this {@link ListablePage page}.
	 * @return a new {@link ListablePage} initialized with the given array for content.
	 * @see java.util.Arrays#asList(Object[])
	 * @see #ListablePage(List)
	 */
	@SafeVarargs
	public static <T> ListablePage<T> newListablePage(@NonNull T... content) {
		return new ListablePage<>(Arrays.asList(content));
	}

	/**
	 * Factory method used to construct a new instance of {@link ListablePage} initialized with the given {@link List},
	 * serving as the content for this {@link Page page}.
	 *
	 * @param <T> {@link Class} type of the elements in the {@link List}.
	 * @param content {@link List} of elements serving as the content for this {@link ListablePage page}.
	 * @return a new {@link ListablePage} initialized with the given {@link List} for content.
	 * @see #ListablePage(List)
	 */
	public static <T> ListablePage<T> newListablePage(@Nullable List<T> content) {
		return new ListablePage<>(content);
	}

	private final List<T> content;

	/**
	 * Constructs an new instance of {@link ListablePage} initialized with the given {@link List} used as the content
	 * for this {@link ListablePage page}.
	 *
	 * @param content {@link List} of elements serving as the content for this {@link ListablePage page}.
	 * Guards against {@literal null} by initializing the {@code content} to an empty {@link List}
	 * if the given {@link List} is {@literal null}.
	 * @see java.util.Collections#emptyList()
	 * @see java.util.List
	 */
	public ListablePage(@Nullable List<T> content) {
		this.content = content != null ? content : Collections.emptyList();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean hasContent() {
		return !getContent().isEmpty();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean hasNext() {
		return false;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean hasPrevious() {
		return false;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public List<T> getContent() {
		return Collections.unmodifiableList(this.content);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public int getNumber() {
		return 1;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Sort getSort() {
		return null;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public long getTotalElements() {
		return getSize();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public int getTotalPages() {
		return 1;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Iterator<T> iterator() {
		return getContent().iterator();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public <S> Page<S> map(Function<? super T, ? extends S> converter) {

		return newListablePage(getContent().stream()
			.map(converter)
			.collect(Collectors.toList()));
	}
}
