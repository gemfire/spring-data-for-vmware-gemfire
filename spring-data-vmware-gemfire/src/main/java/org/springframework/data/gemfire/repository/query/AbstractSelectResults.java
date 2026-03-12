/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.repository.query;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.data.gemfire.gud.api.GudSelectResults;
import org.springframework.data.gemfire.gud.api.GudCollectionType;
import org.springframework.data.gemfire.gud.api.GudObjectType;

import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * An abstract base class implementation of Apache Geode's {@link GudSelectResults} interface and Java {@link Collection}
 * interface, which delegates to, and is backed by a given, required {@link GudSelectResults} instance.
 *
 * @author John Blum
 * @see Collection
 * @see GudSelectResults
 * @since 2.4.0
 */
public class AbstractSelectResults<T> implements GudSelectResults<T> {

	private final GudSelectResults<T> selectResults;

	/**
	 * Constructs a new instance of {@link GudSelectResults} initialized with the given, required {@link GudSelectResults}
	 * instance backing this base class.
	 *
	 * @param selectResults {@link GudSelectResults} delegate backing this implementation; must not be {@literal null}.
	 * @throws IllegalArgumentException if {@link GudSelectResults} is {@literal null}.
	 * @see GudSelectResults
	 */
	public AbstractSelectResults(@NonNull GudSelectResults<T> selectResults) {

		Assert.notNull(selectResults, "SelectResults must not be null");

		this.selectResults = selectResults;
	}

	/**
	 * Return the configured, underlying {@link GudSelectResults} used as the delegate
	 * backing this {@link GudSelectResults} implementation.
	 *
	 * @return the configured, underlying {@link GudSelectResults}.
	 * @see GudSelectResults
 	 */
	protected @NonNull GudSelectResults<T> getSelectResults() {
		return this.selectResults;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> asList() {
		return getSelectResults().asList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<T> asSet() {
		return getSelectResults().asSet();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GudCollectionType getCollectionType() {
		return getSelectResults().getCollectionType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isModifiable() {
		return getSelectResults().isModifiable();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int occurrences(T result) {
		return getSelectResults().occurrences(result);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setElementType(GudObjectType objectType) {
		getSelectResults().setElementType(objectType);
	}

	// java.util.Collection interface methods

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(T result) {
		return getSelectResults().add(result);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addAll(Collection<? extends T> results) {
		return getSelectResults().addAll(results);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		getSelectResults().clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(Object result) {
		return getSelectResults().contains(result);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsAll(Collection<?> results) {
		return getSelectResults().containsAll(results);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return getSelectResults().isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<T> iterator() {
		return getSelectResults().iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean remove(Object result) {
		return getSelectResults().remove(result);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeAll(Collection<?> results) {
		return getSelectResults().removeAll(results);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean retainAll(Collection<?> results) {
		return getSelectResults().retainAll(results);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return getSelectResults().size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] toArray() {
		return getSelectResults().toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("all")
	public <E> E[] toArray(E[] array) {
		return getSelectResults().toArray(array);
	}
}
