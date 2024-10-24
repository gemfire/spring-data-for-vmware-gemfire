/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.apache.geode.GemFireCheckedException;
import org.apache.geode.GemFireException;
import org.apache.geode.cache.DiskStore;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.TransactionListener;
import org.apache.geode.cache.TransactionWriter;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.pdx.PdxSerializer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Phased;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer;
import org.springframework.data.gemfire.support.AbstractFactoryBeanSupport;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Abstract base class for {@link ClientCacheFactoryBean} and {@link ClientCacheFactoryBean} classes,
 * used to create Apache Geode {@link ClientCache} instances, respectively.
 *
 * This class implements Spring's {@link PersistenceExceptionTranslator} interface and is auto-detected by Spring's
 * {@link PersistenceExceptionTranslationPostProcessor} to enable AOP-based translation of native Apache Geode
 * {@link RuntimeException RuntimeExceptions} to Spring's {@link DataAccessException} hierarchy. Therefore,
 * the presence of this class automatically enables Spring's {@link PersistenceExceptionTranslationPostProcessor}
 * to translate native Apache Geode thrown {@link GemFireException} and {@link GemFireCheckedException} types
 * as Spring {@link DataAccessException DataAccessExceptions}.
 *
 * In addition, this class also assumes the responsibility of positioning the creation of the cache in the appropriate
 * phase of the Spring container's lifecycle, providing default callbacks for both initialization and destruction.
 *
 * More importantly, this abstract class encapsulates configuration applicable to tuning Apache Geode in order to
 * efficiently use JVM Heap memory. Since Apache Geode stores data in-memory, on the JVM Heap, it is important that
 * Apache Geode be tuned to monitor the JVM Heap and respond to memory pressure accordingly, by evicting data
 * and issuing warnings when the JVM Heap reaches critical mass.
 *
 * This abstract class is also concerned with the configuration of PDX and transaction event handling along with
 * whether the contents (entries) of the cache should be made effectively immutable on reads (i.e. get(key)).
 *
 * In summary, this abstract class primarily handles and encapsulates the configuration of the following concerns:
 *
 * <ul>
 *     <li>copy-on-read semantics</li>
 *     <li>JVM Heap memory management</li>
 *     <li>PDX serialization</li>
 *     <li>Transaction event processing</li>
 * </ul>
 *
 * All of these concerns are applicable to both Apache Geode {@link ClientCache} instances.
 *
 * @author John Blum
 * @see GemFireCheckedException
 * @see GemFireException
 * @see DiskStore
 * @see ClientCache
 * @see Region
 * @see TransactionListener
 * @see TransactionWriter
 * @see ClientCache
 * @see ClientCacheFactory
 * @see PdxSerializer
 * @see DisposableBean
 * @see FactoryBean
 * @see InitializingBean
 * @see Phased
 * @see DataAccessException
 * @see PersistenceExceptionTranslationPostProcessor
 * @see PersistenceExceptionTranslator
 * @see ClientCacheFactoryBean
 * @see ClientCacheFactoryBean
 * @see ClientCacheConfigurer
 * @see ClientCacheConfigurer
 * @see AbstractFactoryBeanSupport
 * @since 2.5.0
 */
public abstract class AbstractBasicCacheFactoryBean extends AbstractFactoryBeanSupport<ClientCache>
		implements DisposableBean, InitializingBean, PersistenceExceptionTranslator, Phased {

	private boolean close = true;

	private int phase = -1;

	private Boolean copyOnRead;
	private Boolean pdxIgnoreUnreadFields;
	private Boolean pdxPersistent;
	private Boolean pdxReadSerialized;

	private CacheFactoryInitializer<?> cacheFactoryInitializer;

	private Float criticalHeapPercentage;
	private Float evictionHeapPercentage;

	private volatile ClientCache cache;

	private List<TransactionListener> transactionListeners;

	private PdxSerializer pdxSerializer;

	private String pdxDiskStoreName;

	private TransactionWriter transactionWriter;

	/**
	 * Sets a reference to the constructed, configured an initialized {@link ClientCache} instance created by
	 * this cache {@link FactoryBean}.
	 *
	 * @param cache {@link ClientCache} created by this cache {@link FactoryBean}.
	 * @see ClientCache
	 */
	protected void setCache(@Nullable ClientCache cache) {
		this.cache = cache;
	}

	/**
	 * Returns a reference to the constructed, configured an initialized {@link ClientCache} instance created by
	 * this cache {@link FactoryBean}.
	 *
	 * @param <T> parameterized {@link Class} type extending {@link ClientCache}.
	 * @return a reference to the {@link ClientCache} created by this cache {@link FactoryBean}.
	 * @see ClientCache
	 */
	@SuppressWarnings("unchecked")
	public @Nullable <T extends ClientCache> T getCache() {
		return (T) this.cache;
	}

	/**
	 * Returns an {@link Optional} reference to the constructed, configured and initialized {@link ClientCache}
	 * instance created by this cache {@link FactoryBean}.
	 *
	 * @param <T> parameterized {@link Class} type extending {@link ClientCache}.
	 * @return an {@link Optional} reference to the {@link ClientCache} created by this {cache @link FactoryBean}.
	 * @see ClientCache
	 * @see Optional
	 * @see #getCache()
	 */
	public <T extends ClientCache> Optional<T> getOptionalCache() {
		return Optional.ofNullable(getCache());
	}

	/**
	 * Set the {@link CacheFactoryInitializer} called by this {@link FactoryBean} to initialize the Apache Geode
	 * cache factory used to create the cache constructed by this {@link FactoryBean}.
	 *
	 * @param cacheFactoryInitializer {@link CacheFactoryInitializer} called to initialize the cache factory.
	 * @see CacheFactoryInitializer
	 */
	@SuppressWarnings("rawtypes")
	public void setCacheFactoryInitializer(@Nullable CacheFactoryInitializer cacheFactoryInitializer) {
		this.cacheFactoryInitializer = cacheFactoryInitializer;
	}

	/**
	 * Return the {@link CacheFactoryInitializer} called by this {@link FactoryBean} to initialize the Apache Geode
	 * cache factory used to create the cache constructed by this {@link FactoryBean}.
	 *
	 * @return the {@link CacheFactoryInitializer} called to initialize the cache factory.
	 * @see CacheFactoryInitializer
	 */
	@SuppressWarnings("rawtypes")
	public @Nullable CacheFactoryInitializer getCacheFactoryInitializer() {
		return this.cacheFactoryInitializer;
	}

	/**
	 * Sets a boolean value used to determine whether the cache should be closed on shutdown of the Spring container.
	 *
	 * @param close boolean value used to determine whether the cache will be closed on shutdown of the Spring container.
	 */
	public void setClose(boolean close) {
		this.close = close;
	}

	/**
	 * Returns a boolean value used to determine whether the cache will be closed on shutdown of the Spring container.
	 *
	 * Defaults to {@literal true}.
	 *
	 * @return a boolean value used to determine whether the cache will be closed on shutdown of the Spring container.
	 */
	public boolean isClose() {
		return this.close;
	}

	/**
	 * Sets the {@link ClientCache#getCopyOnRead()} property of the {@link ClientCache}.
	 *
	 * @param copyOnRead a {@link Boolean} value to indicate whether {@link Object objects}
	 * stored in the {@link ClientCache} are copied on read (i.e. {@link Region#get(Object)}.
	 */
	public void setCopyOnRead(@Nullable Boolean copyOnRead) {
		this.copyOnRead = copyOnRead;
	}

	/**
	 * Returns the configuration of the {@link ClientCache#getCopyOnRead()} property set on the {@link ClientCache}.
	 *
	 * @return a {@link Boolean} value to indicate whether {@link Object objects}
	 * stored in the {@link ClientCache} are copied on read (i.e. {@link Region#get(Object)}.
	 */
	public @Nullable Boolean getCopyOnRead() {
		return this.copyOnRead;
	}

	/**
	 * Determines whether {@link Object objects} stored in the {@link ClientCache} are copied when read
	 * (i.e. {@link Region#get(Object)}.
	 *
	 * Defaults to {@literal false}.
	 *
	 * @return a boolean value indicating whether {@link Object objects} stored in the {@link ClientCache}
	 * are copied when read (i.e. {@link Region#get(Object)}.
	 * @see #getCopyOnRead()
	 */
	@SuppressWarnings("unused")
	public boolean isCopyOnRead() {
		return Boolean.TRUE.equals(getCopyOnRead());
	}

	/**
	 * Set the {@link ClientCache} critical heap percentage property.
	 *
	 * @param criticalHeapPercentage {@link Float} value specifying the configuration for the {@link ClientCache}
	 * critical heap percentage.
	 */
	public void setCriticalHeapPercentage(@Nullable Float criticalHeapPercentage) {
		this.criticalHeapPercentage = criticalHeapPercentage;
	}

	/**
	 * Gets the configuration of the {@link ClientCache} critical heap percentage property.
	 *
	 * @return a {@link Float} value specifying the configuration for the {@link ClientCache} critical heap percentage.
	 */
	public Float getCriticalHeapPercentage() {
		return this.criticalHeapPercentage;
	}

	/**
	 * Set the {@link ClientCache} eviction heap percentage property.
	 *
	 * @param evictionHeapPercentage {@link Float} value specifying the configuration for the {@link ClientCache}
	 * eviction heap percentage.
	 */
	public void setEvictionHeapPercentage(Float evictionHeapPercentage) {
		this.evictionHeapPercentage = evictionHeapPercentage;
	}

	/**
	 * Gets the configuration of the {@link ClientCache} eviction heap percentage property.
	 *
	 * @return a {@link Float} value specifying the configuration for the {@link ClientCache} eviction heap percentage.
	 */
	public Float getEvictionHeapPercentage() {
		return this.evictionHeapPercentage;
	}

	/**
	 * Returns the {@link ClientCache cache object reference} created by this cache {@link FactoryBean}.
	 *
	 * @return the {@link ClientCache cache object reference} created by this cache {@link FactoryBean}.
	 * @see FactoryBean#getObject()
	 * @see ClientCache
	 * @see #doGetObject()
	 * @see #getCache()
	 */
	@Override
	public ClientCache getObject() throws Exception {

		ClientCache cache = getCache();

		return cache != null ? cache : doGetObject();
	}

	/**
	 * Called if {@link #getCache()} returns a {@literal null} {@link ClientCache} reference from {@link #getObject()}.
	 *
	 * @return a new constructed, configured and initialized {@link ClientCache} instance.
	 * @see ClientCache
	 * @see #getObject()
	 */
	protected abstract ClientCache doGetObject();

	/**
	 * Returns the {@link Class type} of {@link ClientCache} created by this cache {@link FactoryBean}.
	 *
	 * @return the {@link Class type} of {@link ClientCache} created by this cache {@link FactoryBean}.
	 * @see FactoryBean#getObjectType()
	 * @see #doGetObjectType()
	 */
	@Override
	public Class<? extends ClientCache> getObjectType() {

		ClientCache cache = getCache();

		return cache != null ? cache.getClass() : doGetObjectType();
	}

	/**
	 * By default, returns {@link ClientCache} {@link Class}.
	 *
	 * @return {@link ClientCache} {@link Class} by default.
	 * @see ClientCache
	 * @see #getObjectType()
	 * @see Class
	 */
	protected Class<? extends ClientCache> doGetObjectType() {
		return ClientCache.class;
	}

	/**
	 * Sets the {@link String name} of the Apache Geode {@link DiskStore} used to store PDX metadata.
	 *
	 * @param pdxDiskStoreName {@link String name} for the PDX {@link DiskStore}.
	 * @see ClientCacheFactory#setPdxDiskStore(String)
	 * @see DiskStore#getName()
	 */
	public void setPdxDiskStoreName(@Nullable String pdxDiskStoreName) {
		this.pdxDiskStoreName = pdxDiskStoreName;
	}

	/**
	 * Gets the {@link String name} of the Apache Geode {@link DiskStore} used to store PDX metadata.
	 *
	 * @return the {@link String name} of the PDX {@link DiskStore}.
	 * @see ClientCache#getPdxDiskStore()
	 * @see DiskStore#getName()
	 */
	public @Nullable String getPdxDiskStoreName() {
		return this.pdxDiskStoreName;
	}

	/**
	 * Configures whether PDX will ignore unread fields when deserializing PDX bytes back to an {@link Object}.
	 *
	 * Defaults to {@literal false}.
	 *
	 * @param pdxIgnoreUnreadFields {@link Boolean} value controlling ignoring unread fields.
	 * @see ClientCacheFactory#setPdxIgnoreUnreadFields(boolean)
	 */
	public void setPdxIgnoreUnreadFields(@Nullable Boolean pdxIgnoreUnreadFields) {
		this.pdxIgnoreUnreadFields = pdxIgnoreUnreadFields;
	}

	/**
	 * Gets the configuration determining whether PDX will ignore unread fields when deserializing PDX bytes
	 * back to an {@link Object}.
	 *
	 * Defaults to {@literal false}.
	 *
	 * @return a {@link Boolean} value controlling ignoring unread fields.
	 * @see ClientCache#getPdxIgnoreUnreadFields()
	 */
	public @Nullable Boolean getPdxIgnoreUnreadFields() {
		return this.pdxIgnoreUnreadFields;
	}

	/**
	 * Configures whether {@link Class type} metadata for {@link Object objects} serialized to PDX
	 * will be persisted to disk.
	 *
	 * @param pdxPersistent {@link Boolean} value controlling whether PDX {@link Class type} metadata
	 * will be persisted to disk.
	 * @see ClientCacheFactory#setPdxPersistent(boolean)
	 */
	public void setPdxPersistent(@Nullable Boolean pdxPersistent) {
		this.pdxPersistent = pdxPersistent;
	}

	/**
	 * Gets the configuration determining whether {@link Class type} metadata for {@link Object objects} serialized
	 * to PDX will be persisted to disk.
	 *
	 * @return a {@link Boolean} value controlling whether PDX {@link Class type} metadata will be persisted to disk.
	 * @see ClientCache#getPdxPersistent()
	 */
	public @Nullable Boolean getPdxPersistent() {
		return this.pdxPersistent;
	}

	/**
	 * Configures whether {@link Object objects} stored in the Apache Geode {@link ClientCache cache} as PDX
	 * will be read back as PDX bytes or (deserialized) as an {@link Object} when {@link Region#get(Object)}
	 * is called.
	 *
	 * @param pdxReadSerialized {@link Boolean} value controlling the PDX read serialized function.
	 * @see ClientCacheFactory#setPdxReadSerialized(boolean)
	 */
	public void setPdxReadSerialized(@Nullable Boolean pdxReadSerialized) {
		this.pdxReadSerialized = pdxReadSerialized;
	}

	/**
	 * Gets the configuration determining whether {@link Object objects} stored in the Apache Geode
	 * {@link ClientCache cache} as PDX will be read back as PDX bytes or (deserialized) as an {@link Object}
	 * when {@link Region#get(Object)} is called.
	 *
	 * @return a {@link Boolean} value controlling the PDX read serialized function.
	 * @see ClientCache#getPdxReadSerialized()
	 */
	public @Nullable Boolean getPdxReadSerialized() {
		return this.pdxReadSerialized;
	}

	/**
	 * Configures a reference to {@link PdxSerializer} used by this cache to de/serialize {@link Object objects}
	 * stored in the cache and distributed/transferred across the distributed system as PDX bytes.
	 *
	 * @param serializer {@link PdxSerializer} used by this cache to de/serialize {@link Object objects} as PDX.
	 * @see ClientCacheFactory#setPdxSerializer(PdxSerializer)
	 * @see PdxSerializer
	 */
	public void setPdxSerializer(@Nullable PdxSerializer serializer) {
		this.pdxSerializer = serializer;
	}

	/**
	 * Get a reference to the configured {@link PdxSerializer} used by this cache to de/serialize {@link Object objects}
	 * stored in the cache and distributed/transferred across the distributed system as PDX bytes.
	 *
	 * @return a reference to the configured {@link PdxSerializer}.
	 * @see ClientCache#getPdxSerializer()
	 * @see PdxSerializer
	 */
	public @Nullable PdxSerializer getPdxSerializer() {
		return this.pdxSerializer;
	}

	/**
	 * Set the lifecycle phase for this cache bean in the Spring container.
	 *
	 * @param phase {@link Integer#TYPE} value specifying the lifecycle phase for this cache bean
	 * in the Spring container.
	 * @see Phased#getPhase()
	 */
	@SuppressWarnings("unused")
	protected void setPhase(int phase) {
		this.phase = phase;
	}

	/**
	 * Returns the configured lifecycle phase for this cache bean in the Spring container.
	 *
	 * @return an {@link Integer#TYPE} used specifying the lifecycle phase for this cache bean in the Spring container.
	 * @see Phased#getPhase()
	 */
	@Override
	public int getPhase() {
		return this.phase;
	}

	/**
	 * Configures the cache (transaction manager) with a {@link List} of {@link TransactionListener TransactionListeners}
	 * implemented by applications to listen for and receive transaction events after a transaction is processed
	 * (i.e. committed or rolled back).
	 *
	 * @param transactionListeners {@link List} of application-defined {@link TransactionListener TransactionListeners}
	 * registered with the cache to listen for and receive transaction events.
	 * @see TransactionListener
	 */
	public void setTransactionListeners(List<TransactionListener> transactionListeners) {
		this.transactionListeners = transactionListeners;
	}

	/**
	 * Returns the {@link List} of configured, application-defined {@link TransactionListener TransactionListeners}
	 * registered with the cache (transaction manager) to enable applications to receive transaction events after a
	 * transaction is processed (i.e. committed or rolled back).
	 *
	 * @return a {@link List} of application-defined {@link TransactionListener TransactionListeners} registered with
	 * the cache (transaction manager) to listen for and receive transaction events.
	 * @see TransactionListener
	 */
	public List<TransactionListener> getTransactionListeners() {
		return CollectionUtils.nullSafeList(this.transactionListeners);
	}

	/**
	 * Configures a {@link TransactionWriter} implemented by the application to receive transaction events and perform
	 * a action, like a veto.
	 *
	 * @param transactionWriter {@link TransactionWriter} receiving transaction events.
	 * @see TransactionWriter
	 */
	public void setTransactionWriter(@Nullable TransactionWriter transactionWriter) {
		this.transactionWriter = transactionWriter;
	}

	/**
	 * Return the configured {@link TransactionWriter} used to process and handle transaction events.
	 *
	 * @return the configured {@link TransactionWriter}.
	 * @see TransactionWriter
	 */
	public @Nullable TransactionWriter getTransactionWriter() {
		return this.transactionWriter;
	}

	/**
	 * Initializes this cache {@link FactoryBean} after all properties for this cache bean have been set
	 * by the Spring container.
	 *
	 * @throws Exception if initialization fails.
	 * @see InitializingBean#afterPropertiesSet()
	 * @see #applyCacheConfigurers()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		applyCacheConfigurers();
	}

	/**
	 * Applies any user-defined cache configurers (e.g. {@link ClientCacheConfigurer} or {@link ClientCacheConfigurer})
	 * to this cache {@link FactoryBean} before cache construction, configuration and initialization.
 	 */
	protected abstract void applyCacheConfigurers();

	/**
	 * Null-safe method used to close the {@link ClientCache} by calling {@link ClientCache#close()}
	 * iff the cache is not already closed.
	 *
	 * @param cache {@link ClientCache} to close.
	 * @see ClientCache#isClosed()
	 * @see ClientCache#close()
	 * @see #isNotClosed(ClientCache)
	 */
	protected void close(@Nullable ClientCache cache) {

		Optional.ofNullable(cache)
			.filter(this::isNotClosed)
			.ifPresent(ClientCache::close);

		setCache(null);
	}

	/**
	 * Determines if the {@link ClientCache} has not been closed yet.
	 *
	 * @param cache {@link ClientCache} to evaluate.
	 * @return a boolean value indicating if the {@link ClientCache} is not yet closed.
	 * @see ClientCache
	 */
	protected boolean isNotClosed(@Nullable ClientCache cache) {
		return cache != null && !cache.isClosed();
	}

	/**
	 * Destroys the cache bean on Spring container shutdown.
	 *
	 * @see DisposableBean#destroy()
	 * @see #close(ClientCache)
	 * @see #fetchCache()
	 * @see #isClose()
	 */
	@Override
	public void destroy() {

		if (isClose()) {
			close(fetchCache());
		}
	}

	private boolean isHeapPercentageValid(@NonNull Float heapPercentage) {
		return heapPercentage >= 0.0f && heapPercentage <= 100.0f;
	}

	/**
	 * Configures the {@link ClientCache} critical and eviction heap thresholds as percentages.
	 *
	 * @param cache {@link ClientCache} to configure the critical and eviction heap thresholds;
	 * must not be {@literal null}.
	 * @return the given {@link ClientCache}.
	 * @throws IllegalArgumentException if the critical or eviction heap thresholds are not valid percentages.
	 * @see org.apache.geode.cache.control.ResourceManager#setCriticalHeapPercentage(float)
	 * @see org.apache.geode.cache.control.ResourceManager#setEvictionHeapPercentage(float)
	 * @see org.apache.geode.cache.control.ResourceManager
	 * @see ClientCache#getResourceManager()
	 * @see ClientCache
	 */
	protected @NonNull ClientCache configureHeapPercentages(@NonNull ClientCache cache) {

		Optional.ofNullable(getCriticalHeapPercentage()).ifPresent(criticalHeapPercentage -> {

			Assert.isTrue(isHeapPercentageValid(criticalHeapPercentage),
				() -> String.format("criticalHeapPercentage [%s] is not valid; must be >= 0.0 and <= 100.0",
					criticalHeapPercentage));

			cache.getResourceManager().setCriticalHeapPercentage(criticalHeapPercentage);
		});

		Optional.ofNullable(getEvictionHeapPercentage()).ifPresent(evictionHeapPercentage -> {

			Assert.isTrue(isHeapPercentageValid(evictionHeapPercentage),
				() -> String.format("evictionHeapPercentage [%s] is not valid; must be >= 0.0 and <= 100.0",
					evictionHeapPercentage));

			cache.getResourceManager().setEvictionHeapPercentage(evictionHeapPercentage);
		});

		return cache;
	}

	/**
	 * Configures the cache to use PDX serialization.
	 *
	 * @param pdxConfigurer {@link PdxConfigurer} used to configure the cache with PDX serialization.
	 * @return the {@link PdxConfigurer#getTarget()}.
	 */
	protected <T> T configurePdx(PdxConfigurer<T> pdxConfigurer) {

		Optional.ofNullable(getPdxDiskStoreName())
			.filter(StringUtils::hasText)
			.ifPresent(pdxConfigurer::setDiskStoreName);

		Optional.ofNullable(getPdxIgnoreUnreadFields()).ifPresent(pdxConfigurer::setIgnoreUnreadFields);

		Optional.ofNullable(getPdxPersistent()).ifPresent(pdxConfigurer::setPersistent);

		Optional.ofNullable(getPdxReadSerialized()).ifPresent(pdxConfigurer::setReadSerialized);

		Optional.ofNullable(getPdxSerializer()).ifPresent(pdxConfigurer::setSerializer);

		return pdxConfigurer.getTarget();
	}

	/**
	 * Fetches an existing cache instance from the Apache Geode cache factory.
	 *
	 * @param <T> parameterized {@link Class} type extending {@link ClientCache}.
	 * @return an existing cache instance if available.
	 * @throws org.apache.geode.cache.CacheClosedException if an existing cache instance does not exist.
	 * @see ClientCacheFactory#getAnyInstance()
	 * @see ClientCacheFactory#getAnyInstance()
	 * @see ClientCache
	 * @see #doFetchCache()
	 * @see #getCache()
	 */
	protected <T extends ClientCache> T fetchCache() {

		T cache = getCache();

		return cache != null ? cache : doFetchCache();
	}

	/**
	 * Called by {@link #fetchCache()} if the {@link ClientCache} reference returned by {@link #getCache()}
	 * is {@literal null}.
	 *
	 * This method is typically implemented by calling {@link ClientCacheFactory#getAnyInstance()}
	 * or {@link ClientCacheFactory#getAnyInstance()} depending on the {@link ClientCache} type declared
	 * and used in the Spring application.
	 *
	 * @param <T> parameterized {@link Class} type extending {@link ClientCache}.
	 * @return a (existing) reference to a {@link ClientCache} instance.
	 * @throws org.apache.geode.cache.CacheClosedException if a {@link ClientCache} reference does not exist.
	 * @see #fetchCache()
	 */
	protected abstract <T extends ClientCache> T doFetchCache();

	/**
	 * Initializes the given {@link ClientCacheFactory} or {@link ClientCacheFactory}
	 * with the configured {@link CacheFactoryInitializer}.
	 *
	 * @param factory {@link ClientCacheFactory} or {@link ClientCacheFactory} to initialize.
	 * @return the initialized {@link ClientCacheFactory} or {@link ClientCacheFactory}.
	 * @see CacheFactoryInitializer#initialize(Object)
	 * @see ClientCacheFactory
	 * @see ClientCacheFactory
	 * @see #getCacheFactoryInitializer()
	 */
	@Nullable
	@SuppressWarnings("unchecked")
	protected Object initializeFactory(Object factory) {

		return Optional.ofNullable(getCacheFactoryInitializer())
			.map(cacheFactoryInitializer -> cacheFactoryInitializer.initialize(factory))
			.orElse(factory);
	}

	/**
	 * Registers configured, application-defined {@link TransactionListener TransactionListeners} with the cache
	 * (transaction manager) to listen for and receive transaction events when a (cache) transaction is processed
	 * (e.g. committed or rolled back).
	 *
	 * @param cache {@link ClientCache} used to register the configured, application-defined
	 * {@link TransactionListener TransactionListeners}; must not be {@literal null}.
	 * @return the given {@link ClientCache}.
	 * @see ClientCache#getCacheTransactionManager()
	 * @see org.apache.geode.cache.CacheTransactionManager#addListener(TransactionListener)
	 * @see org.apache.geode.cache.CacheTransactionManager
	 * @see TransactionListener
	 * @see ClientCache
	 */
	protected @NonNull ClientCache registerTransactionListeners(@NonNull ClientCache cache) {

		CollectionUtils.nullSafeCollection(getTransactionListeners()).stream()
			.filter(Objects::nonNull)
			.forEach(transactionListener -> cache.getCacheTransactionManager().addListener(transactionListener));

		return cache;
	}

	/**
	 * Translates the thrown Apache Geode {@link RuntimeException} into a corresponding {@link Exception} from Spring's
	 * generic {@link DataAccessException} hierarchy if possible.
	 *
	 * @param exception the Apache Geode {@link RuntimeException} to translate.
	 * @return the translated Spring {@link DataAccessException} or {@literal null}
	 * if the Apache Geode {@link RuntimeException} could not be translated.
	 * @see PersistenceExceptionTranslator#translateExceptionIfPossible(RuntimeException)
	 * @see DataAccessException
	 */
	@Override
	public @Nullable DataAccessException translateExceptionIfPossible(@Nullable RuntimeException exception) {

		if (exception instanceof IllegalArgumentException) {

			DataAccessException wrapped = GemfireCacheUtils.convertQueryExceptions(exception);

			// ignore conversion if generic exception is returned
			if (!(wrapped instanceof GemfireSystemException)) {
				return wrapped;
			}
		}

		if (exception instanceof GemFireException) {
			return GemfireCacheUtils.convertGemfireAccessException((GemFireException) exception);
		}

		if (exception.getCause() instanceof GemFireException) {
			return GemfireCacheUtils.convertGemfireAccessException((GemFireException) exception.getCause());
		}

		if (exception.getCause() instanceof GemFireCheckedException) {
			return GemfireCacheUtils.convertGemfireAccessException((GemFireCheckedException) exception.getCause());
		}

		return null;
	}

	/**
	 * Callback interface for initializing a {@link ClientCacheFactory} or a {@link ClientCacheFactory} instance,
	 * which is used to create an instance of {@link ClientCache}.
	 *
	 * @see ClientCacheFactory
	 * @see ClientCacheFactory
	 * @see Function
	 */
	@FunctionalInterface
	public interface CacheFactoryInitializer<T> extends Function<T, T> {

		/**
		 * Alias for {@link #initialize(Object)}.
		 *
		 * @param t cache factory to initialize.
		 * @return the initialized cache factory.
		 * @see #initialize(Object)
		 */
		@Override
		default T apply(T t) {
			return initialize(t);
		}

		/**
		 * Initialize the given cache factory.
		 *
		 * @param cacheFactory cache factory to initialize.
		 * @return the given cache factory.
		 * @see ClientCacheFactory
		 * @see ClientCacheFactory
		 */
		T initialize(T cacheFactory);

	}

	/**
	 * Callback interface to configure PDX.
	 *
	 * @param <T> parameterized {@link Class} type capable of configuring Apache Geode PDX functionality.
	 * @see ClientCacheFactory
	 * @see ClientCacheFactory
	 * @see ClientCache
	 */
	public interface PdxConfigurer<T> {

		T getTarget();

		PdxConfigurer<T> setDiskStoreName(String diskStoreName);

		PdxConfigurer<T> setIgnoreUnreadFields(Boolean ignoreUnreadFields);

		PdxConfigurer<T> setPersistent(Boolean persistent);

		PdxConfigurer<T> setReadSerialized(Boolean readSerialized);

		PdxConfigurer<T> setSerializer(PdxSerializer pdxSerializer);

	}
}
