/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.springframework.data.gemfire.CacheFactoryBean.JndiDataSource;
import static org.springframework.data.gemfire.util.CollectionUtils.nullSafeList;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.TransactionListener;
import org.apache.geode.cache.TransactionWriter;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.server.CacheServer;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.CacheFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.AbstractAnnotationConfigSupport;
import org.springframework.data.gemfire.config.support.CustomEditorBeanFactoryPostProcessor;
import org.springframework.data.gemfire.config.support.DefinedIndexesApplicationListener;
import org.springframework.data.gemfire.config.support.DiskStoreDirectoryBeanPostProcessor;
import org.springframework.data.gemfire.util.PropertiesBuilder;
import org.springframework.util.StringUtils;

/**
 * {@link AbstractCacheConfiguration} is an abstract base class for configuring either a Pivotal GemFire/Apache Geode
 * client or peer-based cache instance using Spring's Java-based, Annotation {@link Configuration} support.
 *
 * This class encapsulates configuration settings common to both Pivotal GemFire/Apache Geode
 * {@link Cache peer caches}
 * and {@link ClientCache client caches}.
 *
 * @author John Blum
 * @see Annotation
 * @see Properties
 * @see Cache
 * @see org.apache.geode.cache.GemFireCache
 * @see ClientCache
 * @see CacheServer
 * @see org.springframework.beans.factory.BeanFactory
 * @see BeanDefinition
 * @see BeanDefinitionBuilder
 * @see org.springframework.beans.factory.support.BeanDefinitionRegistry
 * @see Bean
 * @see Configuration
 * @see ImportAware
 * @see AnnotationAttributes
 * @see Resource
 * @see AnnotationMetadata
 * @see CacheFactoryBean
 * @see org.springframework.data.gemfire.client.ClientCacheFactoryBean
 * @see AbstractAnnotationConfigSupport
 * @see CustomEditorBeanFactoryPostProcessor
 * @see DefinedIndexesApplicationListener
 * @see DiskStoreDirectoryBeanPostProcessor
 * @see PropertiesBuilder
 * @since 1.9.0
 */
@Configuration
@SuppressWarnings("unused")
public abstract class AbstractCacheConfiguration extends AbstractAnnotationConfigSupport implements ImportAware {

	private static final AtomicBoolean CUSTOM_EDITORS_BEAN_FACTORY_POST_PROCESSOR_REGISTERED =
		new AtomicBoolean(false);

	private static final AtomicBoolean DEFINED_INDEXES_APPLICATION_LISTENER_REGISTERED =
		new AtomicBoolean(false);

	private static final AtomicBoolean DISK_STORE_DIRECTORY_BEAN_POST_PROCESSOR_REGISTERED =
		new AtomicBoolean(false);

	protected static final boolean DEFAULT_CLOSE = true;
	protected static final boolean DEFAULT_COPY_ON_READ = false;
	protected static final boolean DEFAULT_USE_BEAN_FACTORY_LOCATOR = false;

	protected static final int DEFAULT_MCAST_PORT = 0;

	protected static final String DEFAULT_LOCATORS = "";
	protected static final String DEFAULT_LOG_LEVEL = "config";
	protected static final String DEFAULT_NAME = "SpringDataGemFireApplication";

	private boolean close = DEFAULT_CLOSE;
	private boolean copyOnRead = DEFAULT_COPY_ON_READ;
	private boolean useBeanFactoryLocator = DEFAULT_USE_BEAN_FACTORY_LOCATOR;

	private Integer mcastPort = DEFAULT_MCAST_PORT;

	private Float criticalHeapPercentage;
	private Float evictionHeapPercentage;

	private List<JndiDataSource> jndiDataSources;
	private List<TransactionListener> transactionListeners;

	private final PropertiesBuilder customGemFireProperties = PropertiesBuilder.create();

	private Resource cacheXml;

	private String locators = DEFAULT_LOCATORS;
	private String logLevel = DEFAULT_LOG_LEVEL;
	private String name;
	private String startLocator;

	private TransactionWriter transactionWriter;

	/**
	 * Returns a {@link Properties} object containing Pivotal GemFire/Apache Geode properties used to configure
	 * the Pivotal GemFire/Apache Geode cache.
	 *
	 * The {@literal name} of the Pivotal GemFire/Apache Geode member/node in the cluster is set to a default,
	 * pre-defined and descriptive value depending on the type of configuration meta-data applied.
	 *
	 * {@literal mcast-port} is set to {@literal 0} and {@literal locators} is set to an {@link String empty String},
	 * which is necessary for {@link ClientCache cache client}-based applications.  These values can be changed
	 * and set accordingly for {@link Cache peer cache} and {@link CacheServer cache server} applications.
	 *
	 * Finally, the {@literal log-level} property defaults to {@literal config}.
	 *
	 * @return a {@link Properties} object containing Pivotal GemFire/Apache Geode properties used to configure
	 * the Pivotal GemFire/Apache Geode cache instance.
	 * @see <a href="https://geode.apache.org/docs/guide/113/reference/topics/gemfire_properties.html">Geode Properties</a>
	 * @see Properties
	 * @see #locators()
	 * @see #logLevel()
	 * @see #mcastPort()
	 * @see #name()
	 * @see #startLocator()
	 */
	@Bean
	protected Properties gemfireProperties() {

		PropertiesBuilder gemfireProperties = PropertiesBuilder.create();

		gemfireProperties.setProperty("name", name());
		gemfireProperties.setProperty("mcast-port", mcastPort());
		gemfireProperties.setProperty("log-level", logLevel());
		gemfireProperties.setProperty("locators", locators());
		gemfireProperties.setProperty("start-locator", startLocator());
		gemfireProperties.add(this.customGemFireProperties);

		return gemfireProperties.build();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {

		configureInfrastructure(importMetadata);
		configureCache(importMetadata);
		configureOptional(importMetadata);
	}

	/**
	 * Configures Spring container infrastructure components and beans used by Spring Data GemFire
	 * to enable Pivotal GemFire or Apache Geode to function properly inside a Spring context.
	 *
	 * @param importMetadata {@link AnnotationMetadata} containing annotation meta-data
	 * for the Spring Data GemFire cache application class.
	 * @see AnnotationMetadata
	 */
	protected void configureInfrastructure(AnnotationMetadata importMetadata) {

		registerCustomEditorBeanFactoryPostProcessor(importMetadata);
		registerDefinedIndexesApplicationListener(importMetadata);
		registerDiskStoreDirectoryBeanPostProcessor(importMetadata);
	}

	private void registerCustomEditorBeanFactoryPostProcessor(AnnotationMetadata importMetadata) {

		if (CUSTOM_EDITORS_BEAN_FACTORY_POST_PROCESSOR_REGISTERED.compareAndSet(false, true)) {
			register(BeanDefinitionBuilder.rootBeanDefinition(CustomEditorBeanFactoryPostProcessor.class)
				.setRole(BeanDefinition.ROLE_INFRASTRUCTURE).getBeanDefinition());
		}
	}

	private void registerDefinedIndexesApplicationListener(AnnotationMetadata importMetadata) {

		if (DEFINED_INDEXES_APPLICATION_LISTENER_REGISTERED.compareAndSet(false, true)) {
			register(BeanDefinitionBuilder.rootBeanDefinition(DefinedIndexesApplicationListener.class)
				.setRole(BeanDefinition.ROLE_INFRASTRUCTURE).getBeanDefinition());
		}
	}

	private void registerDiskStoreDirectoryBeanPostProcessor(AnnotationMetadata importMetadata) {

		if (DISK_STORE_DIRECTORY_BEAN_POST_PROCESSOR_REGISTERED.compareAndSet(false, true)) {
			register(BeanDefinitionBuilder.rootBeanDefinition(DiskStoreDirectoryBeanPostProcessor.class)
				.setRole(BeanDefinition.ROLE_INFRASTRUCTURE).getBeanDefinition());
		}
	}

	/**
	 * Configures Pivotal GemFire/Apache Geode cache specific settings.
	 *
	 * @param importMetadata {@link AnnotationMetadata} containing the cache meta-data used to configure the cache.
	 * @see AnnotationMetadata
	 */
	protected void configureCache(AnnotationMetadata importMetadata) {

		if (isClientPeerOrServerCacheApplication(importMetadata)) {

			AnnotationAttributes cacheMetadataAttributes = getAnnotationAttributes(importMetadata);

			setCopyOnRead(resolveProperty(cacheProperty("copy-on-read"),
				Boolean.TRUE.equals(cacheMetadataAttributes.get("copyOnRead"))));

			Optional.ofNullable(resolveProperty(cacheProperty("critical-heap-percentage"), (Float) null))
				.ifPresent(this::setCriticalHeapPercentage);

			Optional.ofNullable((Float) cacheMetadataAttributes.get("criticalHeapPercentage"))
				.filter(it -> getCriticalHeapPercentage() == null)
				.filter(AbstractAnnotationConfigSupport::hasValue)
				.ifPresent(this::setCriticalHeapPercentage);

			Optional.ofNullable(resolveProperty(cacheProperty("eviction-heap-percentage"), (Float) null))
				.ifPresent(this::setEvictionHeapPercentage);

			Optional.ofNullable((Float) cacheMetadataAttributes.get("evictionHeapPercentage"))
				.filter(it -> getEvictionHeapPercentage() == null)
				.filter(AbstractAnnotationConfigSupport::hasValue)
				.ifPresent(this::setEvictionHeapPercentage);

			setLogLevel(resolveProperty(cacheProperty("log-level"),
				(String) cacheMetadataAttributes.get("logLevel")));

			setName(resolveProperty(propertyName("name"),
				resolveProperty(cacheProperty("name"),
					(String) cacheMetadataAttributes.get("name"))));

			setUseBeanFactoryLocator(resolveProperty(propertyName("use-bean-factory-locator"),
				Boolean.TRUE.equals(cacheMetadataAttributes.get("useBeanFactoryLocator"))));
		}
	}

	/**
	 * Callback method allowing developers to configure other cache or application specific configuration settings.
	 *
	 * @param importMetadata {@link AnnotationMetadata} containing meta-data used to configure the cache or application.
	 * @see AnnotationMetadata
	 */
	protected void configureOptional(AnnotationMetadata importMetadata) { }

	/**
	 * Constructs a new, initialized instance of {@link CacheFactoryBean} based on the Spring application's
	 * cache type preference (i.e. client or peer), which is expressed via the appropriate annotation.
	 *
	 * Use the {@link ClientCacheApplication} Annotation to construct a {@link ClientCache cache client} application.
	 *
	 * Use the {@link PeerCacheApplication} Annotation to construct a {@link Cache peer cache} application.
	 *
	 * @param <T> {@link Class} specific sub-type of the {@link CacheFactoryBean}.
	 * @return a new instance of the appropriate {@link CacheFactoryBean} given the Spring application's
	 * cache type preference (i.e client or peer),  (e.g. {@link ClientCacheApplication}
	 * or {@link PeerCacheApplication}).
	 * @see org.springframework.data.gemfire.client.ClientCacheFactoryBean
	 * @see CacheFactoryBean
	 * @see #configureCacheFactoryBean(CacheFactoryBean)
	 * @see #newCacheFactoryBean()
	 */
	protected <T extends CacheFactoryBean> T constructCacheFactoryBean() {
		return configureCacheFactoryBean(newCacheFactoryBean());
	}

	/**
	 * Constructs a new, uninitialized instance of {@link CacheFactoryBean} based on the Spring application's
	 * cache type preference (i.e. client or peer), which is expressed via the appropriate annotation.
	 *
	 * Use the {@link ClientCacheApplication} Annotation to construct a {@link ClientCache cache client} application.
	 *
	 * Use the {@link PeerCacheApplication} Annotation to construct a {@link Cache peer cache} application.
	 *
	 * @param <T> {@link Class} specific sub-type of the {@link CacheFactoryBean}.
	 * @return a new instance of the appropriate {@link CacheFactoryBean} given the Spring application's
	 * cache type preference (i.e client or peer),  (e.g. {@link ClientCacheApplication}
	 * or {@link PeerCacheApplication}).
	 * @see org.springframework.data.gemfire.client.ClientCacheFactoryBean
	 * @see CacheFactoryBean
	 */
	protected abstract <T extends CacheFactoryBean> T newCacheFactoryBean();

	/**
	 * Configures the {@link CacheFactoryBean} with common cache configuration settings.
	 *
	 * @param <T> {@link Class} specific sub-type of the {@link CacheFactoryBean}.
	 * @param gemfireCache {@link CacheFactoryBean} to configure.
	 * @return the given {@link CacheFactoryBean} with common cache configuration settings applied.
	 * @see org.springframework.data.gemfire.client.ClientCacheFactoryBean
	 * @see CacheFactoryBean
	 */
	protected <T extends CacheFactoryBean> T configureCacheFactoryBean(T gemfireCache) {

		gemfireCache.setBeanClassLoader(getBeanClassLoader());
		gemfireCache.setBeanFactory(getBeanFactory());
		gemfireCache.setCacheXml(getCacheXml());
		gemfireCache.setClose(isClose());
		gemfireCache.setCopyOnRead(getCopyOnRead());
		gemfireCache.setCriticalHeapPercentage(getCriticalHeapPercentage());
		gemfireCache.setEvictionHeapPercentage(getEvictionHeapPercentage());
		gemfireCache.setJndiDataSources(getJndiDataSources());
		gemfireCache.setProperties(gemfireProperties());
		gemfireCache.setTransactionListeners(getTransactionListeners());
		gemfireCache.setTransactionWriter(getTransactionWriter());
		gemfireCache.setUseBeanFactoryLocator(useBeanFactoryLocator());

		return gemfireCache;
	}

	// TODO: Review Javadoc from here

	/**
	 * Determines whether this is a GemFire {@link CacheServer} application,
	 * which is indicated by the presence of the {@link CacheServerApplication} annotation on a Spring application
	 * {@link Configuration @Configuration} class.
	 *
	 * @param importMetadata {@link AnnotationMetadata} containing application configuration meta-data
	 * from the annotations used to configure the Spring application.
	 * @return a boolean value indicating whether this is a GemFire cache server application.
	 * @see CacheServerApplication
	 * @see #isTypedCacheApplication(Class, AnnotationMetadata)
	 */
	protected boolean isCacheServerApplication(AnnotationMetadata importMetadata) {
		return isTypedCacheApplication(CacheServerApplication.class, importMetadata);
	}

	/**
	 * Determines whether this is a GemFire {@link ClientCache} application,
	 * which is indicated by the presence of the {@link ClientCacheApplication} annotation on a Spring application
	 * {@link Configuration @Configuration} class.
	 *
	 * @param importMetadata {@link AnnotationMetadata} containing application configuration meta-data
	 * from the annotations used to configure the Spring application.
	 * @return a boolean value indicating whether this is a GemFire cache client application.
	 * @see ClientCacheApplication
	 * @see #isTypedCacheApplication(Class, AnnotationMetadata)
	 */
	protected boolean isClientCacheApplication(AnnotationMetadata importMetadata) {
		return isTypedCacheApplication(ClientCacheApplication.class, importMetadata);
	}

	/**
	 * Determines whether this is a GemFire peer {@link Cache} application,
	 * which is indicated by the presence of the {@link PeerCacheApplication} annotation on a Spring application
	 * {@link Configuration @Configuration} class.
	 *
	 * @param importMetadata {@link AnnotationMetadata} containing application configuration meta-data
	 * from the annotations used to configure the Spring application.
	 * @return a boolean value indicating whether this is a GemFire peer cache application.
	 * @see PeerCacheApplication
	 * @see #isTypedCacheApplication(Class, AnnotationMetadata)
	 */
	protected boolean isPeerCacheApplication(AnnotationMetadata importMetadata) {
		return isTypedCacheApplication(PeerCacheApplication.class, importMetadata);
	}

	/**
	 * Determines whether this Spring application is annotated with the given GemFire cache type annotation.
	 *
	 * @param annotationType {@link Annotation} cache type.
	 * @param importMetadata {@link AnnotationMetadata} containing application configuration meta-data
	 * from the annotations used to configure the Spring application.
	 * @return a boolean value indicating if this Spring application is annotated with the given GemFire
	 * cache type annotation.
	 * @see AnnotationMetadata
	 * @see Annotation
	 * @see #getAnnotationTypeName()
	 * @see #getAnnotationType()
	 */
	protected boolean isTypedCacheApplication(Class<? extends Annotation> annotationType,
			AnnotationMetadata importMetadata) {

		return annotationType.equals(getAnnotationType()) && importMetadata.hasAnnotation(getAnnotationTypeName());
	}

	/**
	 * Determines whether this is a GemFire {@link CacheServer} or
	 * {@link Cache peer cache} application, which is indicated by the presence
	 * of either the {@link CacheServerApplication} annotation or the {@link PeerCacheApplication} annotation
	 * on a Spring application {@link Configuration @Configuration} class.
	 *
	 * @param importMetadata {@link AnnotationMetadata} containing application configuration meta-data
	 * from the annotations used to configure the Spring application.
	 * @return a boolean value indicating whether this is a GemFire cache server or peer cache application.
	 * @see AnnotationMetadata
	 * @see CacheServerApplication
	 * @see PeerCacheApplication
	 * @see #isCacheServerApplication(AnnotationMetadata)
	 * @see #isPeerCacheApplication(AnnotationMetadata)
	 */
	protected boolean isCacheServerOrPeerCacheApplication(AnnotationMetadata importMetadata) {
		return isCacheServerApplication(importMetadata) || isPeerCacheApplication(importMetadata);
	}

	/**
	 * Determine whether this Spring application is a {@link CacheServer},
	 * {@link ClientCache} or a {@link Cache} application.
	 *
	 * @param importMetadata {@link AnnotationMetadata} containing application configuration meta-data
	 * from the class type-level annotations used to configure the Spring application.
	 * @return a boolean value indicating whether this is a GemFire cache server, client cache or peer cache
	 * Spring application.
	 * @see #isCacheServerApplication(AnnotationMetadata)
	 * @see #isClientCacheApplication(AnnotationMetadata)
	 * @see #isPeerCacheApplication(AnnotationMetadata)
	 */
	protected boolean isClientPeerOrServerCacheApplication(AnnotationMetadata importMetadata) {

		return isCacheServerApplication(importMetadata)
			|| isClientCacheApplication(importMetadata)
			|| isPeerCacheApplication(importMetadata);
	}

	void setCacheXml(Resource cacheXml) {
		this.cacheXml = cacheXml;
	}

	protected Resource getCacheXml() {
		return this.cacheXml;
	}

	void setClose(boolean close) {
		this.close = close;
	}

	protected boolean isClose() {
		return this.close;
	}

	void setCopyOnRead(boolean copyOnRead) {
		this.copyOnRead = copyOnRead;
	}

	protected boolean getCopyOnRead() {
		return this.copyOnRead;
	}

	void setCriticalHeapPercentage(Float criticalHeapPercentage) {
		this.criticalHeapPercentage = criticalHeapPercentage;
	}

	protected Float getCriticalHeapPercentage() {
		return this.criticalHeapPercentage;
	}

	void setEvictionHeapPercentage(Float evictionHeapPercentage) {
		this.evictionHeapPercentage = evictionHeapPercentage;
	}

	protected Float getEvictionHeapPercentage() {
		return this.evictionHeapPercentage;
	}

	void setJndiDataSources(List<JndiDataSource> jndiDataSources) {
		this.jndiDataSources = jndiDataSources;
	}

	protected List<JndiDataSource> getJndiDataSources() {
		return nullSafeList(this.jndiDataSources);
	}

	void setLocators(String locators) {
		this.locators = locators;
		this.mcastPort = DEFAULT_MCAST_PORT;
	}

	protected String locators() {
		return this.locators;
	}

	void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	protected String logLevel() {
		return Optional.ofNullable(this.logLevel).orElse(DEFAULT_LOG_LEVEL);
	}

	void setMcastPort(Integer mcastPort) {
		this.mcastPort = mcastPort;
		this.locators = DEFAULT_LOCATORS;
	}

	protected Integer mcastPort() {
		return Optional.ofNullable(mcastPort).orElse(DEFAULT_MCAST_PORT);
	}

	void setName(String name) {
		this.name = name;
	}

	protected String name() {
		return Optional.ofNullable(this.name).filter(StringUtils::hasText).orElseGet(this::toString);
	}

	void setStartLocator(String startLocator) {
		this.startLocator = startLocator;
	}

	protected String startLocator() {
		return this.startLocator;
	}

	void setTransactionListeners(List<TransactionListener> transactionListeners) {
		this.transactionListeners = transactionListeners;
	}

	protected List<TransactionListener> getTransactionListeners() {
		return nullSafeList(this.transactionListeners);
	}

	void setTransactionWriter(TransactionWriter transactionWriter) {
		this.transactionWriter = transactionWriter;
	}

	protected TransactionWriter getTransactionWriter() {
		return this.transactionWriter;
	}

	void setUseBeanFactoryLocator(boolean useBeanFactoryLocator) {
		this.useBeanFactoryLocator = useBeanFactoryLocator;
	}

	protected boolean useBeanFactoryLocator() {
		return this.useBeanFactoryLocator;
	}

	public void add(Properties gemfireProperties) {
		this.customGemFireProperties.add(gemfireProperties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return DEFAULT_NAME;
	}
}
