/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheCallback;
import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.LoaderHelper;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.gemfire.util.SpringExtensions;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Implementation of Apache Geode's {@link Declarable} interface that enables support for wiring Apache Geode components
 * with Spring bean dependencies defined in a Spring {@link ApplicationContext}.
 *
 * @author John Blum
 * @see Properties
 * @see Cache
 * @see CacheCallback
 * @see Declarable
 * @see BeanFactory
 * @see DisposableBean
 * @see ApplicationContext
 * @see ApplicationListener
 * @see ContextRefreshedEvent
 * @see SpringContextBootstrappingInitializer
 * @see WiringDeclarableSupport
 * @since 1.3.4
 */
@SuppressWarnings("unused")
public abstract class LazyWiringDeclarableSupport extends WiringDeclarableSupport
		implements ApplicationListener<ContextRefreshedEvent>, DisposableBean {

	// Atomic reference to the parameters passed by Apache Geode when this Declarable object
	// was constructed, configured and its initialize(..) method called
	private final AtomicReference<Properties> parametersReference = new AtomicReference<>();

	// condition to determine the initialized state of this Declarable object
	volatile boolean initialized = false;

	/**
	 * Constructs a new instance of the {@link LazyWiringDeclarableSupport} class registered with the
	 * {@link SpringContextBootstrappingInitializer} as a Spring {@link ApplicationListener}.
	 *
	 * This {@link Declarable} object will receive notifications from the {@link SpringContextBootstrappingInitializer}
	 * when the Spring context is created and initialized (refreshed).  The notification is necessary in order for
	 * this {@link Declarable} object to be properly configured and initialized with any required,
	 * Spring-defined dependencies.
	 *
	 * @see SpringContextBootstrappingInitializer
	 * 	#register(org.springframework.context.ApplicationListener)
	 */
	public LazyWiringDeclarableSupport() {
		SpringContextBootstrappingInitializer.register(this);
	}

	/**
	 * Asserts that this {@link Declarable} object has been properly configured and initialized by the Spring container
	 * after has GemFire constructed this {@link Declarable} object during startup.
	 *
	 * This method is recommended to be called before any of this {@link Declarable} object's {@link CacheCallback}
	 * methods (e.g. {@link CacheLoader#load(LoaderHelper)} are invoked in order to ensure that this {@link Declarable}
	 * object was properly constructed, configured and initialized by the Spring container before hand.
	 *
	 * @throws IllegalStateException if this {@link Declarable} object was not been properly constructed, configured
	 * and initialized by the Spring container.
	 * @see #init(Properties)
	 * @see #isInitialized()
	 */
	protected void assertInitialized() {

		Assert.state(isInitialized(),
			String.format("This Declarable object [%s] has not been properly configured and initialized",
				getClass().getName()));
	}

	/**
	 * Asserts that this {@link Declarable} object has not yet been used, or activated prior to being fully constructed,
	 * configured and initialized by the Spring container.
	 *
	 * It is possible, though rare, that the {@link #init(Properties)} method might be called multiple times by GemFire
	 * before the Spring container constructs, configures, initializes and generally puts this component to use.
	 *
	 * @throws IllegalStateException if the Declarable object has already been configured and initialized
	 * by the Spring container.
	 * @see #init(Properties)
	 * @see #isNotInitialized()
	 */
	protected void assertUninitialized() {

		Assert.state(isNotInitialized(),
			String.format("This Declarable object [%s] has already been configured and initialized",
				getClass().getName()));
	}

	/**
	 * Determines whether this {@link Declarable} object has been properly configured and initialized
	 * by the Spring container.
	 *
	 * @return a boolean value indicating whether this {@link Declarable} object has been properly configured
	 * and initialized by the Spring container.
	 * @see #doInit(BeanFactory, Properties)
	 * @see #assertInitialized()
	 */
	protected boolean isInitialized() {
		return this.initialized;
	}

	/**
	 * Determines whether this {@link Declarable} object has been properly configured and initialized
	 * by the Spring container.
	 *
	 * @return a boolean value indicating whether this {@link Declarable} object has been properly configured
	 * and initialized by the Spring container.
	 * @see #doInit(BeanFactory, Properties)
	 * @see #isInitialized()
	 */
	protected boolean isNotInitialized() {
		return !isInitialized();
	}

	/**
	 * Initialization method called by GemFire with the configured parameters once this {@link Declarable} object
	 * has been constructed by GemFire and the &lt;initalizer&gt; element is parsed
	 * in GemFire's configuration meta-data during startup.
	 *
	 * @param parameters {@link Properties} containing the configured parameters parsed from GemFire's
	 * configuration meta-data (e.g. {@literal cache.xml}) and passed to this {@link Declarable} object.
	 * @see #doInit(BeanFactory, Properties)
	 * @see Properties
	 */
	@Override
	public final void initialize(@Nullable Cache cache, @NonNull Properties parameters) {

		// Set a reference to the Apache Geode (configuration) Properties
		setParameters(parameters);

		// A Throwable maybe thrown iff the BeanFactory does not exist, has been closed
		// or the GemfireBeanFactoryLocator is not in use.
		SpringExtensions.safeDoOperation(() -> doInit(locateBeanFactory(), nullSafeGetParameters()));
	}

	/**
	 * Performs the actual configuration and initialization of this {@link Declarable} object before use.
	 *
	 * This method is triggered by the Spring {@link ApplicationContext}, Spring application
	 * {@link ContextRefreshedEvent}) indicating that the Spring container (context) has been created and refreshed.
	 *
	 * @param parameters {@link Properties} containing the configured parameters parsed from GemFire's
	 * configuration meta-data (e.g. {@literal cache.xml}) and passed to this {@link Declarable} object.
	 * @throws IllegalArgumentException if the {@literal bean-name} parameter was specified in GemFire's
	 * configuration meta-data but no bean with the specified name could be found in the Spring context.
	 * @see #init(Properties)
	 * @see #configureThis(BeanFactory, String)
	 * @see #doPostInit(Properties)
	 * @see Properties
	 */
	synchronized void doInit(@NonNull BeanFactory beanFactory, @NonNull Properties parameters) {

		this.initialized = isInitialized()
			|| configureThis(beanFactory, parameters.getProperty(TEMPLATE_BEAN_NAME_PROPERTY));

		doPostInit(parameters);
	}

	/**
	 * Performs any post configuration and initialization activities required by the application.
	 *
	 * By default, this method does nothing.
	 *
	 * @param parameters {@link Properties} containing the configured parameters parsed from GemFire's
	 * configuration meta-data (e.g. {@literal cache.xml}) and passed to this {@link Declarable} object.
	 * @see #doInit(BeanFactory, Properties)
	 * @see Properties
	 */
	protected void doPostInit(@NonNull Properties parameters) { }

	/**
	 * Null-safe operation to return the parameters passed to this {@link Declarable} object when created by GemFire
	 * from it's own configuration meta-data (e.g. {@literal cache.xml}).
	 *
	 * @return a {@link Properties} containing the configured parameters parsed from GemFire's configuration meta-data
	 * (e.g. {@literal cache.xml}) and passed to this {@link Declarable} object.
	 * @see Properties
	 */
	protected @NonNull Properties nullSafeGetParameters() {

		Properties parameters = this.parametersReference.get();

		return parameters != null ? parameters : new Properties();
	}

	/**
	 * Stores a reference to the {@link Properties parameters} passed to the {@link Declarable#init(Properties)} method.
	 *
	 * @param parameters {@link Properties} containing the configured parameters parsed from GemFire's
	 * configuration meta-data (e.g. {@literal cache.xml}) and passed to this {@link Declarable} object.
	 * @see Properties
	 */
	protected void setParameters(@Nullable Properties parameters) {
		this.parametersReference.set(parameters);
	}

	/**
	 * Event handler method called when GemFire has created and initialized (refreshed)
	 * the Spring {@link ApplicationContext} using the {@link SpringContextBootstrappingInitializer}.
	 *
	 * @param event {@link ContextRefreshedEvent} published by the Spring {@link ApplicationContext} after it is
	 * successfully created and initialized by GemFire.
	 * @see ContextRefreshedEvent
	 * @see #doInit(BeanFactory, Properties)
	 * @see #nullSafeGetParameters()
	 */
	@Override
	@SuppressWarnings("all")
	public final void onApplicationEvent(@NonNull ContextRefreshedEvent event) {

		ApplicationContext applicationContext = event.getApplicationContext();

		Assert.isTrue(applicationContext instanceof ConfigurableApplicationContext,
			String.format("The Spring ApplicationContext [%s] must be an instance of ConfigurableApplicationContext",
				applicationContext));

		ConfigurableListableBeanFactory beanFactory =
			((ConfigurableApplicationContext) applicationContext).getBeanFactory();

		doInit(beanFactory, nullSafeGetParameters());
	}

	/**
	 * When this {@link Declarable} object/bean gets destroyed by the Spring container, {@code destroy()} will
	 * make sure this component gets unregistered from the {@link SpringContextBootstrappingInitializer} properly.
	 *
	 * @throws Exception if bean destruction is unsuccessful.
	 * @see SpringContextBootstrappingInitializer
	 * 	#unregister(org.springframework.context.ApplicationListener)
	 * @see #setParameters(Properties)
	 */
	@Override
	public void destroy() throws Exception {

		SpringContextBootstrappingInitializer.unregister(this);
		setParameters(null);
		this.initialized = false;
	}
}
