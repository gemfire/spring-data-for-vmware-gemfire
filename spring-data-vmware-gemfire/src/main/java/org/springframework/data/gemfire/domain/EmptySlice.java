// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.domain;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.gemfire.domain.support.AbstractSliceSupport;

/**
 * The {@link EmptySlice} class is an implementation of an empty Spring Data {@link Slice}.
 *
 * @author John Blum
 * @param <T> {@link Class} type of the elements in this {@link Slice}.
 * @see Pageable
 * @see Slice
 * @see Sort
 * @see AbstractSliceSupport
 * @since 1.1.0
 */
@SuppressWarnings("unused")
public abstract class EmptySlice<T> extends AbstractSliceSupport<T> {

	@SuppressWarnings("all")
	public static final EmptySlice<Object> EMPTY_SLICE = new EmptySlice<Object>() { };

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
		return Collections.emptyList();
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
	public Pageable nextPageable() {
		return null;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Pageable previousPageable() {
		return null;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <S> Slice<S> map(Function<? super T, ? extends S> converter) {
		return (Slice<S>) EMPTY_SLICE;
	}
}
