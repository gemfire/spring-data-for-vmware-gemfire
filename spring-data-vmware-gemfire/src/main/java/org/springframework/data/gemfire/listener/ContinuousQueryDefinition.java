/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-12: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.listener;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.gemfire.gud.api.GudCqAttributes;
import org.springframework.data.gemfire.gud.api.GudCqAttributesFactory;
import org.springframework.data.gemfire.gud.api.GudCqListener;
import org.springframework.data.gemfire.gud.api.GudCqQuery;
import org.springframework.data.gemfire.gud.api.GudExcludedEvent;
import org.springframework.data.gemfire.listener.adapter.ContinuousQueryListenerAdapter;
import org.springframework.data.gemfire.listener.annotation.ContinuousQuery;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Class type for defining a {@link GudCqQuery}.
 *
 * @author Costin Leau
 * @author John Blum
 * @see InitializingBean
 */
@SuppressWarnings("unused")
public class ContinuousQueryDefinition implements InitializingBean {

	private final boolean durable;

	private final ContinuousQueryListener listener;

	private final String name;
	private final String query;

	private final Set<CQEvent> excludedEvents;

	public static ContinuousQueryDefinition from(Object delegate, Method method) {

		Assert.notNull(method, "Method must not be null");

		ContinuousQuery continuousQuery = method.getAnnotation(ContinuousQuery.class);

		Assert.notNull(continuousQuery, () -> String.format("Method [%1$s] must be annotated with [%2$s]",
			method, ContinuousQuery.class.getName()));

		String name = Optional.of(continuousQuery.name())
			.filter(StringUtils::hasText)
			.orElseGet(() -> String.format("%1$s.%2$s", method.getDeclaringClass().getName(), method.getName()));

		String query = continuousQuery.query();

		ContinuousQueryListenerAdapter listener = new ContinuousQueryListenerAdapter(delegate);

		listener.setDefaultListenerMethod(method.getName());

		boolean durable = continuousQuery.durable();

		Set<CQEvent> excludedEvents = Set.of(continuousQuery.excludedEvents());

		return new ContinuousQueryDefinition(name, query, listener, durable, excludedEvents);
	}

	public ContinuousQueryDefinition(String query, ContinuousQueryListener listener) {
		this(query, listener, Collections.emptySet());
	}

	public ContinuousQueryDefinition(String query, ContinuousQueryListener listener, Set<CQEvent> excludedEvents) {
		this(query, listener, false, excludedEvents);
	}

	public ContinuousQueryDefinition(String query, ContinuousQueryListener listener, boolean durable) {
		this(query, listener, durable, Collections.emptySet());
	}

	public ContinuousQueryDefinition(String query, ContinuousQueryListener listener, boolean durable, Set<CQEvent> excludedEvents) {
		this(null, query, listener, durable, excludedEvents);
	}

	public ContinuousQueryDefinition(String name, String query, ContinuousQueryListener listener) {
		this(name, query, listener, Collections.emptySet());
	}

	public ContinuousQueryDefinition(String name, String query, ContinuousQueryListener listener, Set<CQEvent> excludedEvents) {
		this(name, query, listener, false, excludedEvents);
	}

	public ContinuousQueryDefinition(String name, String query, ContinuousQueryListener listener, boolean durable) {
		this(name, query, listener, durable, Collections.emptySet());
	}

	public ContinuousQueryDefinition(String name, String query, ContinuousQueryListener listener, boolean durable, Set<CQEvent> excludedEvents) {

		this.name = name;
		this.query = query;
		this.listener = listener;
		this.durable = durable;
		this.excludedEvents = excludedEvents;

		afterPropertiesSet();
	}

	/**
	 * Determines whether the CQ is durable.
	 *
	 * @return a boolean indicating if the CQ is durable.
	 */
	public boolean isDurable() {
		return this.durable;
	}

	/**
	 * Determines whether the CQ was named.
	 *
	 * @return a boolean value indicating whether the CQ is named.
	 * @see #getName()
	 */
	public boolean isNamed() {
		return StringUtils.hasText(getName());
	}

	/**
	 * Returns a reference to the {@link ContinuousQueryListener} that will process/handle CQ event notifications.
	 *
	 * @return the CQ listener registered with the CQ to handle CQ events.
	 */
	public ContinuousQueryListener getListener() {
		return this.listener;
	}

	/**
	 * Gets the {@link String name} of the CQ.
	 *
	 * @return the {@link String name} of the CQ.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the {@link String query} executed by the CQ.
	 *
	 * @return the {@link String query} executed by the CQ.
	 */
	public String getQuery() {
		return this.query;
	}

	/**
	 * Gets the {@link CQEvent}s to be excluded from the CQ.
	 *
	 * @return the {@link CQEvent}s excluded from the CQ.
	 */
	public Set<CQEvent> getExcludedEvents() {
		return this.excludedEvents;
	}

	@Override
	public void afterPropertiesSet() {
		Assert.hasText(query, "Query is required");
		Assert.notNull(listener, "Listener is required");
	}

	public GudCqAttributes toCqAttributes(GudCqAttributesFactory attributesFactory, Function<ContinuousQueryListener, GudCqListener> listenerFunction, Set<CQEvent> excludedEvents) {

		attributesFactory.addCqListener(listenerFunction.apply(getListener()));
		attributesFactory.setExcludedEvents(excludedEvents.stream().map(this::mapCQEventToExcludedEvent).collect(Collectors.toSet()));

		return attributesFactory.create();
	}

	private GudExcludedEvent mapCQEventToExcludedEvent(CQEvent cqEvent) {
        return switch (cqEvent) {
            case UPDATE -> GudExcludedEvent.UPDATE;
            case CREATE -> GudExcludedEvent.CREATE;
            case INVALIDATE -> GudExcludedEvent.INVALIDATE;
            case DESTROY -> GudExcludedEvent.DESTROY;
        };
	}
}
