/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.mock;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import org.springframework.util.StringUtils;

/**
 * The {@link MockObjectsSupport} class is an abstract base class encapsulating common operations and utilities
 * used in mocking using Mockito.
 *
 * @author John Blum
 * @see AtomicBoolean
 * @see AtomicInteger
 * @see AtomicLong
 * @see AtomicReference
 * @see Consumer
 * @see Function
 * @see Supplier
 * @see InvocationOnMock
 * @see Answer
 * @since 0.0.1
 */
@SuppressWarnings("all")
public abstract class MockObjectsSupport {

	private static final AtomicLong mockObjectIdentifier = new AtomicLong(0L);

	private static final String DEFAULT_MOCK_OBJECT_NAME = "MockObject";

	public static String mockObjectIdentifier() {
		return mockObjectIdentifier(DEFAULT_MOCK_OBJECT_NAME);
	}

	public static String mockObjectIdentifier(String mockObjectName) {

		String resolvedMockObjectName = Optional.ofNullable(mockObjectName)
			.filter(StringUtils::hasText)
			.orElse(DEFAULT_MOCK_OBJECT_NAME);

		return String.format("%s%d", resolvedMockObjectName, mockObjectIdentifier.incrementAndGet());
	}

	protected static Answer<Boolean> newGetter(AtomicBoolean returnValue) {
		return invocation -> returnValue.get();
	}

	protected static Answer<Integer> newGetter(AtomicInteger returnValue) {
		return invocation -> returnValue.get();
	}

	protected static Answer<Long> newGetter(AtomicLong returnValue) {
		return invocation -> returnValue.get();
	}

	protected static <R> Answer<R> newGetter(AtomicReference<R> returnValue) {
		return invocation -> returnValue.get();
	}

	protected static <R, S> Answer<S> newGetter(AtomicReference<R> returnValue, Function<R, S> converter) {
		return invocation -> converter.apply(returnValue.get());
	}

	protected static <R> Answer<R> newGetter(Supplier<R> returnValue) {
		return invocation -> returnValue.get();
	}

	protected static <R, S> Answer<S> newGetter(Supplier<R> returnValue, Function<R, S> converter) {
		return invocation -> converter.apply(returnValue.get());
	}

	protected static <E, C extends Collection<E>, R> Answer<R> newAdder(C collection, R returnValue) {

		return invocation -> {
			collection.add(invocation.getArgument(0));
			return returnValue;
		};
	}

	protected static <R> Answer<R> newSetter(AtomicBoolean argument, R returnValue) {

		return invocation -> {
			argument.set(invocation.getArgument(0));
			return returnValue;
		};
	}

	protected static <R> Answer<R> newSetter(AtomicBoolean argument, Boolean value, R returnValue) {

		return invocation -> {
			argument.set(value);
			return returnValue;
		};
	}

	protected static <R> Answer<R> newSetter(AtomicInteger argument, R returnValue) {

		return invocation -> {
			argument.set(invocation.getArgument(0));
			return returnValue;
		};
	}

	protected static <R> Answer<R> newSetter(AtomicInteger argument, Integer value, R returnValue) {

		return invocation -> {
			argument.set(value);
			return returnValue;
		};
	}

	protected static <R> Answer<R> newSetter(AtomicLong argument, R returnValue) {

		return invocation -> {
			argument.set(invocation.getArgument(0));
			return returnValue;
		};
	}

	protected static <R> Answer<R> newSetter(AtomicLong argument, Long value, R returnValue) {

		return invocation -> {
			argument.set(value);
			return returnValue;
		};
	}

	protected static <T> Answer<T> newSetter(AtomicReference<T> argument) {
		return invocation -> argument.getAndSet(invocation.getArgument(0));
	}

	protected static <T, R> Answer<R> newSetter(AtomicReference<T> argument, Supplier<R> returnValue) {

		return invocation -> {
			argument.set(invocation.getArgument(0));
			return returnValue.get();
		};
	}

	protected static <T> Answer<T> newSetterWithArument(AtomicReference<T> argument, T value) {
		return invocation -> argument.getAndSet(value);
	}

	protected static <T, R> Answer<R> newSetterWithArgument(AtomicReference<T> argument, T value,
			Supplier<R> returnValue) {

		return invocation -> {
			argument.set(value);
			return returnValue.get();
		};
	}

	protected static <T> Answer<T> newSetter(AtomicReference<T> argument, Function<?, T> converter) {
		return invocation -> argument.getAndSet(converter.apply(invocation.getArgument(0)));
	}

	protected static <T, R> Answer<R> newSetter(AtomicReference<T> argument, Function<?, T> converter,
			Supplier<R> returnValue) {

		return invocation -> {
			argument.set(converter.apply(invocation.getArgument(0)));
			return returnValue.get();
		};
	}

	protected static <K, V> Answer<V> newSetter(Map<K, V> argument) {
		return invocation -> argument.put(invocation.getArgument(0), invocation.getArgument(1));
	}

	protected static <K, V, R> Answer<R> newSetter(Map<K, V> argument, Supplier<R> returnValue) {

		return invocation -> {
			argument.put(invocation.getArgument(0), invocation.getArgument(1));
			return returnValue.get();
		};
	}

	protected static <T> Answer<Void> newVoidAnswer(Consumer<InvocationOnMock> methodInvocation) {

		return invocation -> {
			methodInvocation.accept(invocation);
			return null;
		};
	}
}
