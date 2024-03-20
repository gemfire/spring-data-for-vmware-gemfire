/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.mock.context.event;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.gemfire.tests.mock.GemFireMockObjectsSupport;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * A Spring {@link ApplicationListener} interface implementation handling Spring {@link ApplicationContext} (container)
 * {@link ApplicationEvent ApplicationEvents} by destroying all GemFire/Geode {@link Object Mock Objects}.
 *
 * @author John Blum
 * @see ApplicationContext
 * @see ApplicationEvent
 * @see ApplicationListener
 * @see GemFireMockObjectsSupport
 * @since 0.0.16
 */
@SuppressWarnings("unused")
public class DestroyGemFireMockObjectsApplicationListener implements ApplicationListener<ApplicationEvent> {

	/**
	 * Null-safe factory method used to construct a new instance of {@link DestroyGemFireMockObjectsApplicationListener}
	 * initialized with an array of {@link ApplicationEvent} {@link Class types} that will trigger the destruction
	 * of all GemFire/Geode {@link Object Mock Objects}.
	 *
	 * @param destroyEventTypes array of {@link ApplicationEvent} {@link Class types} that will trigger the destruction
	 * of all GemFire/Geode {@link Object Mock Objects}.
	 * @return an new instance of {@link DestroyGemFireMockObjectsApplicationListener}.
	 * @see ApplicationEvent
	 * @see Class
	 * @see #newInstance(Iterable)
	 */
	@SuppressWarnings("unchecked")
	public static DestroyGemFireMockObjectsApplicationListener newInstance(
			@Nullable Class<? extends ApplicationEvent>... destroyEventTypes) {

		return newInstance(Arrays.asList(ArrayUtils.nullSafeArray(destroyEventTypes, Class.class)));
	}

	/**
	 * Null-safe factory method used to construct a new instance of {@link DestroyGemFireMockObjectsApplicationListener}
	 * initialized with an {@link Iterable} of {@link ApplicationEvent} {@link Class types} that will trigger
	 * the destruction of all GemFire/Geode {@link Object Mock Objects}.
	 *
	 * @param destroyEventTypes {@link Iterable} of {@link ApplicationEvent} {@link Class types} that will trigger
	 * the destruction of all GemFire/Geode {@link Object Mock Objects}.
	 * @return an new instance of {@link DestroyGemFireMockObjectsApplicationListener}.
	 * @see ApplicationEvent
	 * @see Class
	 * @see Iterable
	 * @see #newInstance(Iterable)
	 */
	public static DestroyGemFireMockObjectsApplicationListener newInstance(
			@Nullable Iterable<Class<? extends ApplicationEvent>> destroyEventTypes) {

		return new DestroyGemFireMockObjectsApplicationListener(destroyEventTypes);
	}


	private final Set<Class<? extends ApplicationEvent>> configuredDestroyEventTypes;

	/**
	 * Constructs a new instance of {@link DestroyGemFireMockObjectsApplicationListener} initialized with the given
	 * {@link ApplicationEvent} {@link Class types} that when fired will trigger the destruction of all GemFire/Geode
	 * {@link Object Mock Objects}.
	 *
	 * @param destroyEventTypes {@link Iterable} of {@link ApplicationEvent} {@link Class types} that will trigger
	 * the destruction of all GemFire/Geode {@link Object Mock Objects}.
	 * @see ApplicationEvent
	 * @see Iterable
	 */
	public DestroyGemFireMockObjectsApplicationListener(
			@Nullable Iterable<Class<? extends ApplicationEvent>> destroyEventTypes) {

		Set<Class<? extends ApplicationEvent>> resolvedDestroyEventTypes =
			StreamSupport.stream(CollectionUtils.nullSafeIterable(destroyEventTypes).spliterator(), false)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());

		this.configuredDestroyEventTypes = Collections.unmodifiableSet(resolvedDestroyEventTypes);
	}

	/**
	 * Returns the configured {@link Set} of {@link ApplicationEvent} {@link Class types} that trigger the destruction
	 * of all GemFire/Geode {@link Object Mock Objects}.
	 *
	 * @return the configured {@link Set} of {@link ApplicationEvent} {@link Class types} that trigger the destruction
	 * of all GemFire/Geode {@link Object Mock Objects}.
	 * @see ApplicationEvent
	 * @see Class
	 * @see Set
	 */
	protected @NonNull Set<Class<? extends ApplicationEvent>> getConfiguredDestroyEventTypes() {
		return this.configuredDestroyEventTypes;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void onApplicationEvent(@NonNull ApplicationEvent event) {

		if (isDestroyEvent(event)) {
			destroyGemFireMockObjects();
		}
	}

	/**
	 * Null-safe method to determine whether the given {@link ApplicationEvent} will cause the destruction
	 * of all GemFire/Geode {@link Object Mock Objects}.
	 *
	 * @param event {@link ApplicationEvent} to evaluate.
	 * @return a boolean value indicating whether the given {@link ApplicationEvent} will cause the destruction
	 * of all GemFire/Geode {@link Object Mock Objects}.
	 * @see ApplicationEvent
	 * @see #getConfiguredDestroyEventTypes()
	 */
	protected boolean isDestroyEvent(@Nullable ApplicationEvent event) {

		for (Class<? extends ApplicationEvent> configuredDestroyEventType : getConfiguredDestroyEventTypes()) {
			if (configuredDestroyEventType.isInstance(event)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Destroys all GemFire/Geode {@link Object Mock Objects}.
	 *
	 * @see GemFireMockObjectsSupport#destroy()
	 */
	protected void destroyGemFireMockObjects() {
		GemFireMockObjectsSupport.destroy();
	}
}
