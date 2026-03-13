/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.client;

import java.util.Collections;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientRegionFactory;
import org.springframework.data.gemfire.gud.api.GudClientRegionShortcut;
import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/**
 * A Spring {@link BeanFactoryPostProcessor} used to register a Client Region beans for each Region accessible to
 * an Apache Geode or Pivotal GemFire DataSource. If the Region is already defined, the bean definition
 * will not be overridden.
 *
 * This class is abstract because it requires driver-specific implementations for executing functions
 * and retrieving region information from the server cluster.
 *
 * @author David Turanski
 * @author John Blum
 * @see GudRegion
 * @see GudClientCache
 * @see GudClientRegionFactory
 * @see GudClientRegionShortcut
 * @see GudFunction
 * @see BeanFactoryPostProcessor
 * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 * @since 1.2.0
 */
public class GemfireDataSourcePostProcessor implements BeanFactoryAware, BeanPostProcessor {

	private static final GudClientRegionShortcut DEFAULT_CLIENT_REGION_SHORTCUT = GudClientRegionShortcut.PROXY;

	private GudClientRegionShortcut clientRegionShortcut;

	private ConfigurableBeanFactory beanFactory;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Set a reference to the {@link BeanFactory}.
	 *
	 * @param beanFactory reference to the {@link BeanFactory}.
	 * @throws BeansException if the {@link BeanFactory} is not a {@link ConfigurableBeanFactory}.
	 * @see ConfigurableBeanFactory
	 * @see BeanFactory
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

		if (beanFactory instanceof ConfigurableBeanFactory) {
			this.beanFactory = (ConfigurableBeanFactory) beanFactory;
		}
		else {
			throw new TypeMismatchException(beanFactory, ConfigurableBeanFactory.class);
		}
	}

	/**
	 * Returns a reference to the configured {@link ConfigurableBeanFactory}.
	 *
	 * @return a reference to the configured {@link ConfigurableBeanFactory}.
	 * @see ConfigurableBeanFactory
	 */
	public Optional<ConfigurableBeanFactory> getBeanFactory() {
		return Optional.ofNullable(this.beanFactory);
	}

	/**
	 * Set the data policy used to configure the client {@link GudRegion}.
	 *
	 * @param clientRegionShortcut {@link GudClientRegionShortcut} used to define the data policy
	 * used by the client {@link GudRegion}.
	 * @see GudClientRegionShortcut
	 */
	public void setClientRegionShortcut(GudClientRegionShortcut clientRegionShortcut) {
		this.clientRegionShortcut = clientRegionShortcut;
	}

	/**
	 * Returns the data policy used to configure the client {@link GudRegion}.
	 *
	 * @return the configured {@link GudClientRegionShortcut} used to define the data policy
	 * used by the client {@link GudRegion}.
	 * @see GudClientRegionShortcut
	 * @see Optional
	 */
	public Optional<GudClientRegionShortcut> getClientRegionShortcut() {
		return Optional.ofNullable(this.clientRegionShortcut);
	}

	/**
	 * Resolves the {@link GudClientRegionShortcut} used to configure and create client {@link GudRegion Regions}.
	 *
	 * @return the resolved {@link GudClientRegionShortcut}.
	 * @see GudClientRegionShortcut
	 * @see #getClientRegionShortcut()
	 */
	protected GudClientRegionShortcut resolveClientRegionShortcut() {
		return getClientRegionShortcut().orElse(DEFAULT_CLIENT_REGION_SHORTCUT);
	}

	/**
	 * Returns a reference to the configured {@link Logger} used to log messages.
	 *
	 * @return a reference to the configured {@link Logger}.
	 * @see Logger
	 */
	protected Logger getLogger() {
		return this.logger;
	}

	@Nullable @Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

		if (bean instanceof GudClientCache) {

			GudClientCache clientCache = (GudClientCache) bean;

			getBeanFactory().ifPresent(it -> createClientProxyRegions(it, clientCache, regionNames(clientCache)));
		}

		return bean;
	}

	/**
	 * Retrieves the names of all regions accessible from the server cluster.
	 * Subclasses may override this to provide driver-specific implementations.
	 *
	 * @param clientCache the client cache to query for region names
	 * @return an iterable of region names available on the server
	 */
	protected Iterable<String> regionNames(GudClientCache clientCache) {
		return Collections.emptyList();
	}

	/**
	 * Executes a function on the server cluster.
	 * Subclasses may override this to provide driver-specific implementations.
	 *
	 * @param <T> the expected return type
	 * @param clientCache the client cache to use for function execution
	 * @param gemfireFunction the function to execute
	 * @param arguments the arguments to pass to the function
	 * @return the result of the function execution
	 */
	protected <T> T execute(GudClientCache clientCache, GudFunction gemfireFunction, Object... arguments) {
		throw new UnsupportedOperationException("Must be implemented by driver-specific subclass");
	}

	void createClientProxyRegions(ConfigurableBeanFactory beanFactory, GudClientCache clientCache,
			Iterable<String> regionNames) {

		if (regionNames.iterator().hasNext()) {

			GudClientRegionShortcut resolvedClientRegionShortcut = resolveClientRegionShortcut();

			GudClientRegionFactory<?, ?> clientRegionFactory =
				clientCache.createClientRegionFactory(resolvedClientRegionShortcut);

			for (String regionName : regionNames) {

				if (beanFactory.containsBean(regionName)) {

					Object bean = beanFactory.getBean(regionName);

					logWarn("Cannot create a client {} Region bean named {}; A bean with name {} having type {} already exists",
						resolvedClientRegionShortcut.name(), regionName, regionName, ObjectUtils.nullSafeClassName(bean));
				}
				else {
					logInfo("Creating Region bean with name {}...", regionName);
					beanFactory.registerSingleton(regionName, clientRegionFactory.create(regionName));
				}
			}
		}
	}

	void logDebug(String message, Object... arguments) {

		Logger logger = getLogger();

		if (logger.isDebugEnabled()) {
			logger.debug(String.format(message, arguments));
		}
	}

	void logInfo(String message, Object... arguments) {

		Logger logger = getLogger();

		if (logger.isInfoEnabled()) {
			logger.info(message, arguments);
		}
	}

	void logWarn(String message, Object... arguments) {

		Logger logger = getLogger();

		if (logger.isWarnEnabled()) {
			logger.warn(message, arguments);
		}
	}

	public GemfireDataSourcePostProcessor using(GudClientRegionShortcut clientRegionShortcut) {

		setClientRegionShortcut(clientRegionShortcut);

		return this;
	}

	public GemfireDataSourcePostProcessor using(BeanFactory beanFactory) {

		setBeanFactory(beanFactory);

		return this;
	}
}
