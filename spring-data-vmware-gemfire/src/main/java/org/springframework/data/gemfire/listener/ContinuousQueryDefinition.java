/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.listener;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;

import org.apache.geode.cache.query.CqAttributes;
import org.apache.geode.cache.query.CqAttributesFactory;
import org.apache.geode.cache.query.CqListener;
import org.apache.geode.cache.query.CqQuery;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.gemfire.listener.adapter.ContinuousQueryListenerAdapter;
import org.springframework.data.gemfire.listener.annotation.ContinuousQuery;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Class type for defining a {@link CqQuery}.
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

		return new ContinuousQueryDefinition(name, query, listener, durable);
	}

	public ContinuousQueryDefinition(String query, ContinuousQueryListener listener) {
		this(query, listener, false);
	}

	public ContinuousQueryDefinition(String query, ContinuousQueryListener listener, boolean durable) {
		this(null, query, listener, durable);
	}

	public ContinuousQueryDefinition(String name, String query, ContinuousQueryListener listener) {
		this(name, query, listener, false);
	}

	public ContinuousQueryDefinition(String name, String query, ContinuousQueryListener listener, boolean durable) {

		this.name = name;
		this.query = query;
		this.listener = listener;
		this.durable = durable;

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

	@Override
	public void afterPropertiesSet() {
		Assert.hasText(query, "Query is required");
		Assert.notNull(listener, "Listener is required");
	}

	public CqAttributes toCqAttributes(Function<ContinuousQueryListener, CqListener> listenerFunction) {

		CqAttributesFactory attributesFactory = new CqAttributesFactory();

		attributesFactory.addCqListener(listenerFunction.apply(getListener()));

		return attributesFactory.create();
	}
}
