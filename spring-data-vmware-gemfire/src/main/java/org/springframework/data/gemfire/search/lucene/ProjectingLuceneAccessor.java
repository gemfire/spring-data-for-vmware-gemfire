/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.search.lucene;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.lucene.LuceneIndex;
import org.apache.geode.cache.lucene.LuceneResultStruct;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.data.gemfire.search.lucene.support.PdxInstanceMethodInterceptorFactory;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

/**
 * {@link ProjectingLuceneAccessor} is an abstract class supporting implementations of
 * the {@link ProjectingLuceneOperations} interface encapsulating common functionality
 * necessary to execute Lucene queries and work with application domain object views.
 *
 * @author John Blum
 * @see ClassLoader
 * @see BeanClassLoaderAware
 * @see BeanFactory
 * @see BeanFactoryAware
 * @see ProjectingLuceneOperations
 * @see PdxInstanceMethodInterceptorFactory
 * @see ProjectionFactory
 * @see SpelAwareProxyProjectionFactory
 * @see Region
 * @see LuceneIndex
 * @see org.apache.geode.cache.lucene.LuceneQuery
 * @see org.apache.geode.cache.lucene.LuceneQueryFactory
 * @see org.apache.geode.cache.lucene.LuceneService
 * @see org.apache.geode.cache.lucene.LuceneServiceProvider
 * @since 1.1.0
 * @Deprecated To be removed with GemFire 10 integration
 */
public abstract class ProjectingLuceneAccessor extends LuceneTemplate
		implements BeanClassLoaderAware, BeanFactoryAware, ProjectingLuceneOperations {

	private BeanFactory beanFactory;

	private ClassLoader beanClassLoader;

	private ProjectionFactory projectionFactory;

	/**
	 * Constructs a default, uninitialized instance of the {@link ProjectingLuceneAccessor}.
	 */
	public ProjectingLuceneAccessor() {
	}

	/**
	 * Constructs an instance of the {@link ProjectingLuceneAccessor} initialized with the given {@link LuceneIndex}
	 * used to perform Lucene queries (searches).
	 *
	 * @param luceneIndex {@link LuceneIndex} used in Lucene queries.
	 * @see LuceneIndex
	 */
	public ProjectingLuceneAccessor(LuceneIndex luceneIndex) {
		super(luceneIndex);
	}

	/**
	 * Constructs an instance of the {@link ProjectingLuceneAccessor} initialized with the given Lucene index name
	 * and {@link Region} reference upon which Lucene queries are executed.
	 *
	 * @param indexName {@link String} containing the name of the {@link LuceneIndex} used in Lucene queries.
	 * @param region {@link Region} on which Lucene queries are executed.
	 * @see Region
	 */
	public ProjectingLuceneAccessor(String indexName, Region<?, ?> region) {
		super(indexName, region);
	}

	/**
	 * Constructs an instance of the {@link ProjectingLuceneAccessor} initialized with the given Lucene index name
	 * and {@link Region} reference upon which Lucene queries are executed.
	 *
	 * @param indexName {@link String} containing the name of the {@link LuceneIndex} used in Lucene queries.
	 * @param regionPath {@link String} containing the name of the {@link Region} on which Lucene queries are executed.
	 */
	public ProjectingLuceneAccessor(String indexName, String regionPath) {
		super(indexName, regionPath);
	}

	/**
	 * {@inheritDoc}
	 * @see #resolveProjectionFactory()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		this.projectionFactory = resolveProjectionFactory();
	}

	/**
	 * Null-safe method to resolve the Spring Data {@link ProjectionFactory} used to create projections
	 * out of the Lucene query results.
	 *
	 * @return a resolved instance of the Spring Data {@link ProjectionFactory} used to create projections
	 * out of the Lucene query results.
	 * @see ProjectionFactory
	 * @see SpelAwareProxyProjectionFactory
	 * @see #afterPropertiesSet()
	 */
	protected ProjectionFactory resolveProjectionFactory() {
		return Optional.ofNullable(getProjectionFactory()).orElseGet(() -> {
			SpelAwareProxyProjectionFactory projectionFactory = new SpelAwareProxyProjectionFactory();
			projectionFactory.setBeanClassLoader(getBeanClassLoader());
			projectionFactory.setBeanFactory(getBeanFactory());
			projectionFactory.registerMethodInvokerFactory(PdxInstanceMethodInterceptorFactory.INSTANCE);
			return setThenGetProjectionFactory(projectionFactory);
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBeanClassLoader(ClassLoader beanClassLoader) {
		this.beanClassLoader = beanClassLoader;
	}

	/**
	 * Returns a reference to the {@link ClassLoader} used by the Spring {@link BeanFactory container}
	 * to load bean class definitions.
	 *
	 * @return a reference to the {@link ClassLoader} used by the Spring {@link BeanFactory container}
	 * to load bean class definitions.
	 * @see BeanClassLoaderAware#setBeanClassLoader(ClassLoader)
	 * @see ClassLoader
	 */
	protected ClassLoader getBeanClassLoader() {
		return this.beanClassLoader;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	/**
	 * Returns a reference to the Spring {@link BeanFactory container}.
	 *
	 * @return a reference to the Spring {@link BeanFactory container}.
	 * @see BeanFactoryAware#setBeanFactory(BeanFactory)
	 * @see BeanFactory
	 */
	protected BeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	protected ProjectionFactory setThenGetProjectionFactory(ProjectionFactory projectionFactory) {
		setProjectionFactory(projectionFactory);
		return getProjectionFactory();
	}

	/**
	 * Sets the Spring Data {@link ProjectionFactory} used to create projections out of query results.
	 *
	 * @param projectionFactory Spring Data {@link ProjectionFactory} used to created projects out of query results.
	 * @see ProjectionFactory
	 */
	public void setProjectionFactory(ProjectionFactory projectionFactory) {
		this.projectionFactory = projectionFactory;
	}

	/**
	 * Returns the Spring Data {@link ProjectionFactory} used to create projections out of query results.
	 *
	 * @return the Spring Data {@link ProjectionFactory} used to created projects out of query results.
	 * @see ProjectionFactory
	 */
	protected ProjectionFactory getProjectionFactory() {
		return this.projectionFactory;
	}

	public <T, K, V> List<T> project(List<LuceneResultStruct<K, V>> source, Class<T> projectionType) {
		return source.stream().map(luceneResultStruct -> project(luceneResultStruct, projectionType))
			.collect(Collectors.toList());
	}

	public <T, K, V> T project(LuceneResultStruct<K, V> source, Class<T> projectionType) {
		return project(source.getValue(), projectionType);
	}

	public <T> T project(Object source, Class<T> projectionType) {
		return getProjectionFactory().createProjection(projectionType, source);
	}
}
