// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.domain.support;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newUnsupportedOperationException;

import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.data.gemfire.util.RuntimeExceptionFactory;

/**
 * The {@link AbstractPageSupport} class is an abstract Spring Data {@link Page} type supporting the implementation of
 * application specific {@link Page} implementations.
 *
 * @author John Blum
 * @param <T> {@link Class} type of the individual elements on this {@link Slice}.
 * @see org.springframework.core.convert.converter.Converter
 * @see Page
 * @see Slice
 * @since 1.1.0
 */
@SuppressWarnings("unused")
public abstract class AbstractPageSupport<T> extends AbstractSliceSupport<T> implements Page<T> {

	/**
	 * @inheritDoc
	 */
	@Override
	public long getTotalElements() {
		throw newUnsupportedOperationException(RuntimeExceptionFactory.NOT_IMPLEMENTED);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public int getTotalPages() {
		throw newUnsupportedOperationException(RuntimeExceptionFactory.NOT_IMPLEMENTED);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public <S> Page<S> map(Function<? super T, ? extends S> converter) {
		throw newUnsupportedOperationException(RuntimeExceptionFactory.NOT_IMPLEMENTED);
	}
}
