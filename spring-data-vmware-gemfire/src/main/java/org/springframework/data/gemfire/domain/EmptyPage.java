/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.domain;

import java.util.function.Function;

import org.springframework.data.domain.Page;

/**
 * The {@link EmptyPage} class is an implementation of an empty Spring Data {@link Page}.
 *
 * @author John Blum
 * @param <T> {@link Class} type of the elements in this {@link Page}.
 * @see org.springframework.core.convert.converter.Converter
 * @see Page
 * @since 1.1.0
 */
@SuppressWarnings("unused")
public final class EmptyPage<T> extends EmptySlice<T> implements Page<T> {

	@SuppressWarnings("all")
	public static final EmptyPage<?> EMPTY_PAGE = new EmptyPage<>();

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
	public long getTotalElements() {
		return 0;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <S> Page<S> map(Function<? super T, ? extends S> converter) {
		return (Page<S>) EMPTY_PAGE;
	}
}
