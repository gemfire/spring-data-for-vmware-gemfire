/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Migrated from org.apache.geode imports to GUD API types
 */

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientCacheFactory;
import org.springframework.data.gemfire.gud.api.GudDiskStore;
import org.springframework.data.gemfire.gud.api.GudGemFireCheckedException;
import org.springframework.data.gemfire.gud.api.GudGemFireException;
import org.springframework.data.gemfire.gud.api.GudPdxSerializer;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudTransactionListener;
import org.springframework.data.gemfire.gud.api.GudTransactionWriter;
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
public abstract class AbstractBasicCacheFactoryBean extends AbstractFactoryBeanSupport<GudClientCache>
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

	private volatile GudClientCache cache;

	private List<GudTransactionListener> transactionListeners;

	private GudPdxSerializer pdxSerializer;

	private String pdxDiskStoreName;

	private GudTransactionWriter transactionWriter;

	/**
	 * Sets a reference to the constructed, configured an initialized {@link GudClientCache} instance created by
	 * this cache {@link FactoryBean}.
	 *
	 * @param cache {@link GudClientCache} created by this cache {@link FactoryBean}.
	 * @see GudClientCache
	 */
	protected void setCache(@Nullable GudClientCache cache) {
		this.cache = cache;
	}

	/**
	 * Returns a reference to the constructed, configured an initialized {@link GudClientCache} instance created by
	 * this cache {@link FactoryBean}.
	 *
	 * @param <T> parameterized {@link Class} type extending {@link GudClientCache}.
	 * @return a reference to the {@link GudClientCache} created by this cache {@link FactoryBean}.
	 * @see GudClientCache
	 */
	@SuppressWarnings("unchecked")
	public @Nullable <T extends GudClientCache> T getCache() {
		return (T) this.cache;
	}

	/**
	 * Returns an {@link Optional} reference to the constructed, configured and initialized {@link GudClientCache}
	 * instance created by this cache {@link FactoryBean}.
	 *
	 * @param <T> parameterized {@link Class} type extending {@link GudClientCache}.
	 * @return an {@link Optional} reference to the {@link GudClientCache} created by this {cache @link FactoryBean}.
	 * @see GudClientCache
	 * @see Optional
	 * @see #getCache()
	 */
	public <T extends GudClientCache> Optional<T> getOptionalCache() {
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
	 * Sets the {@link GudClientCache#getCopyOnRead()} property of the {@link GudClientCache}.
	 *
	 * @param copyOnRead a {@link Boolean} value to indicate whether {@link Object objects}
	 * stored in the {@link GudClientCache} are copied on read (i.e. {@link GudRegion#get(Object)}.
	 */
	public void setCopyOnRead(@Nullable Boolean copyOnRead) {
		this.copyOnRead = copyOnRead;
	}

	/**
	 * Returns the configuration of the {@link GudClientCache#getCopyOnRead()} property set on the {@link GudClientCache}.
	 *
	 * @return a {@link Boolean} value to indicate whether {@link Object objects}
	 * stored in the {@link GudClientCache} are copied on read (i.e. {@link GudRegion#get(Object)}.
	 */
	public @Nullable Boolean getCopyOnRead() {
		return this.copyOnRead;
	}

	/**
	 * Determines whether {@link Object objects} stored in the {@link GudClientCache} are copied when read
	 * (i.e. {@link GudRegion#get(Object)}.
	 *
	 * Defaults to {@literal false}.
	 *
	 * @return a boolean value indicating whether {@link Object objects} stored in the {@link GudClientCache}
	 * are copied when read (i.e. {@link GudRegion#get(Object)}.
	 * @see #getCopyOnRead()
	 */
	@SuppressWarnings("unused")
	public boolean isCopyOnRead() {
		return Boolean.TRUE.equals(getCopyOnRead());
	}

	/**
	 * Set the {@link GudClientCache} critical heap percentage property.
	 *
	 * @param criticalHeapPercentage {@link Float} value specifying the configuration for the {@link GudClientCache}
	 * critical heap percentage.
	 */
	public void setCriticalHeapPercentage(@Nullable Float criticalHeapPercentage) {
		this.criticalHeapPercentage = criticalHeapPercentage;
	}

	/**
	 * Gets the configuration of the {@link GudClientCache} critical heap percentage property.
	 *
	 * @return a {@link Float} value specifying the configuration for the {@link GudClientCache} critical heap percentage.
	 */
	public Float getCriticalHeapPercentage() {
		return this.criticalHeapPercentage;
	}

	/**
	 * Set the {@link GudClientCache} eviction heap percentage property.
	 *
	 * @param evictionHeapPercentage {@link Float} value specifying the configuration for the {@link GudClientCache}
	 * eviction heap percentage.
	 */
	public void setEvictionHeapPercentage(Float evictionHeapPercentage) {
		this.evictionHeapPercentage = evictionHeapPercentage;
	}

	/**
	 * Gets the configuration of the {@link GudClientCache} eviction heap percentage property.
	 *
	 * @return a {@link Float} value specifying the configuration for the {@link GudClientCache} eviction heap percentage.
	 */
	public Float getEvictionHeapPercentage() {
		return this.evictionHeapPercentage;
	}

	/**
	 * Returns the {@link GudClientCache cache object reference} created by this cache {@link FactoryBean}.
	 *
	 * @return the {@link GudClientCache cache object reference} created by this cache {@link FactoryBean}.
	 * @see FactoryBean#getObject()
	 * @see GudClientCache
	 * @see #doGetObject()
	 * @see #getCache()
	 */
	@Override
	public GudClientCache getObject() throws Exception {

		GudClientCache cache = getCache();

		return cache != null ? cache : doGetObject();
	}

	/**
	 * Called if {@link #getCache()} returns a {@literal null} {@link GudClientCache} reference from {@link #getObject()}.
	 *
	 * @return a new constructed, configured and initialized {@link GudClientCache} instance.
	 * @see GudClientCache
	 * @see #getObject()
	 */
	protected abstract GudClientCache doGetObject();

	/**
	 * Returns the {@link Class type} of {@link GudClientCache} created by this cache {@link FactoryBean}.
	 *
	 * @return the {@link Class type} of {@link GudClientCache} created by this cache {@link FactoryBean}.
	 * @see FactoryBean#getObjectType()
	 * @see #doGetObjectType()
	 */
	@Override
	public Class<? extends GudClientCache> getObjectType() {

		GudClientCache cache = getCache();

		return cache != null ? cache.getClass() : doGetObjectType();
	}

	/**
	 * By default, returns {@link GudClientCache} {@link Class}.
	 *
	 * @return {@link GudClientCache} {@link Class} by default.
	 * @see GudClientCache
	 * @see #getObjectType()
	 * @see Class
	 */
	protected Class<? extends GudClientCache> doGetObjectType() {
		return GudClientCache.class;
	}

	/**
	 * Sets the {@link String name} of the Apache Geode {@link GudDiskStore} used to store PDX metadata.
	 *
	 * @param pdxDiskStoreName {@link String name} for the PDX {@link GudDiskStore}.
	 * @see GudClientCacheFactory#setPdxDiskStore(String)
	 * @see GudDiskStore#getName()
	 */
	public void setPdxDiskStoreName(@Nullable String pdxDiskStoreName) {
		this.pdxDiskStoreName = pdxDiskStoreName;
	}

	/**
	 * Gets the {@link String name} of the Apache Geode {@link GudDiskStore} used to store PDX metadata.
	 *
	 * @return the {@link String name} of the PDX {@link GudDiskStore}.
	 * @see GudClientCache#getPdxDiskStore()
	 * @see GudDiskStore#getName()
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
	 * @see GudClientCacheFactory#setPdxIgnoreUnreadFields(boolean)
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
	 * @see GudClientCache#getPdxIgnoreUnreadFields()
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
	 * @see GudClientCacheFactory#setPdxPersistent(boolean)
	 */
	public void setPdxPersistent(@Nullable Boolean pdxPersistent) {
		this.pdxPersistent = pdxPersistent;
	}

	/**
	 * Gets the configuration determining whether {@link Class type} metadata for {@link Object objects} serialized
	 * to PDX will be persisted to disk.
	 *
	 * @return a {@link Boolean} value controlling whether PDX {@link Class type} metadata will be persisted to disk.
	 * @see GudClientCache#getPdxPersistent()
	 */
	public @Nullable Boolean getPdxPersistent() {
		return this.pdxPersistent;
	}

	/**
	 * Configures whether {@link Object objects} stored in the Apache Geode {@link GudClientCache cache} as PDX
	 * will be read back as PDX bytes or (deserialized) as an {@link Object} when {@link GudRegion#get(Object)}
	 * is called.
	 *
	 * @param pdxReadSerialized {@link Boolean} value controlling the PDX read serialized function.
	 * @see GudClientCacheFactory#setPdxReadSerialized(boolean)
	 */
	public void setPdxReadSerialized(@Nullable Boolean pdxReadSerialized) {
		this.pdxReadSerialized = pdxReadSerialized;
	}

	/**
	 * Gets the configuration determining whether {@link Object objects} stored in the Apache Geode
	 * {@link GudClientCache cache} as PDX will be read back as PDX bytes or (deserialized) as an {@link Object}
	 * when {@link GudRegion#get(Object)} is called.
	 *
	 * @return a {@link Boolean} value controlling the PDX read serialized function.
	 * @see GudClientCache#getPdxReadSerialized()
	 */
	public @Nullable Boolean getPdxReadSerialized() {
		return this.pdxReadSerialized;
	}

	/**
	 * Configures a reference to {@link GudPdxSerializer} used by this cache to de/serialize {@link Object objects}
	 * stored in the cache and distributed/transferred across the distributed system as PDX bytes.
	 *
	 * @param serializer {@link GudPdxSerializer} used by this cache to de/serialize {@link Object objects} as PDX.
	 * @see GudClientCacheFactory#setPdxSerializer(GudPdxSerializer)
	 * @see GudPdxSerializer
	 */
	public void setPdxSerializer(@Nullable GudPdxSerializer serializer) {
		this.pdxSerializer = serializer;
	}

	/**
	 * Get a reference to the configured {@link GudPdxSerializer} used by this cache to de/serialize {@link Object objects}
	 * stored in the cache and distributed/transferred across the distributed system as PDX bytes.
	 *
	 * @return a reference to the configured {@link GudPdxSerializer}.
	 * @see GudClientCache#getPdxSerializer()
	 * @see GudPdxSerializer
	 */
	public @Nullable GudPdxSerializer getPdxSerializer() {
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
	 * Configures the cache (transaction manager) with a {@link List} of {@link GudTransactionListener GudTransactionListeners}
	 * implemented by applications to listen for and receive transaction events after a transaction is processed
	 * (i.e. committed or rolled back).
	 *
	 * @param transactionListeners {@link List} of application-defined {@link GudTransactionListener GudTransactionListeners}
	 * registered with the cache to listen for and receive transaction events.
	 * @see GudTransactionListener
	 */
	public void setTransactionListeners(List<GudTransactionListener> transactionListeners) {
		this.transactionListeners = transactionListeners;
	}

	/**
	 * Returns the {@link List} of configured, application-defined {@link GudTransactionListener GudTransactionListeners}
	 * registered with the cache (transaction manager) to enable applications to receive transaction events after a
	 * transaction is processed (i.e. committed or rolled back).
	 *
	 * @return a {@link List} of application-defined {@link GudTransactionListener GudTransactionListeners} registered with
	 * the cache (transaction manager) to listen for and receive transaction events.
	 * @see GudTransactionListener
	 */
	public List<GudTransactionListener> getTransactionListeners() {
		return CollectionUtils.nullSafeList(this.transactionListeners);
	}

	/**
	 * Configures a {@link GudTransactionWriter} implemented by the application to receive transaction events and perform
	 * a action, like a veto.
	 *
	 * @param transactionWriter {@link GudTransactionWriter} receiving transaction events.
	 * @see GudTransactionWriter
	 */
	public void setTransactionWriter(@Nullable GudTransactionWriter transactionWriter) {
		this.transactionWriter = transactionWriter;
	}

	/**
	 * Return the configured {@link GudTransactionWriter} used to process and handle transaction events.
	 *
	 * @return the configured {@link GudTransactionWriter}.
	 * @see GudTransactionWriter
	 */
	public @Nullable GudTransactionWriter getTransactionWriter() {
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
	 * Null-safe method used to close the {@link GudClientCache} by calling {@link GudClientCache#close()}
	 * iff the cache is not already closed.
	 *
	 * @param cache {@link GudClientCache} to close.
	 * @see GudClientCache#isClosed()
	 * @see GudClientCache#close()
	 * @see #isNotClosed(GudClientCache)
	 */
	protected void close(@Nullable GudClientCache cache) {

		Optional.ofNullable(cache)
			.filter(this::isNotClosed)
			.ifPresent(GudClientCache::close);

		setCache(null);
	}

	/**
	 * Determines if the {@link GudClientCache} has not been closed yet.
	 *
	 * @param cache {@link GudClientCache} to evaluate.
	 * @return a boolean value indicating if the {@link GudClientCache} is not yet closed.
	 * @see GudClientCache
	 */
	protected boolean isNotClosed(@Nullable GudClientCache cache) {
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
	 * Configures the {@link GudClientCache} critical and eviction heap thresholds as percentages.
	 *
	 * @param cache {@link GudClientCache} to configure the critical and eviction heap thresholds;
	 * must not be {@literal null}.
	 * @return the given {@link GudClientCache}.
	 * @throws IllegalArgumentException if the critical or eviction heap thresholds are not valid percentages.
	 * @see GudClientCache#getResourceManager()
	 * @see GudClientCache
	 */
	protected @NonNull GudClientCache configureHeapPercentages(@NonNull GudClientCache cache) {

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
	 * @param <T> parameterized {@link Class} type extending {@link GudClientCache}.
	 * @return an existing cache instance if available.
	 * @see GudClientCacheFactory#getAnyInstance()
	 * @see GudClientCache
	 * @see #doFetchCache()
	 * @see #getCache()
	 */
	protected <T extends GudClientCache> T fetchCache() {

		T cache = getCache();

		return cache != null ? cache : doFetchCache();
	}

	/**
	 * Called by {@link #fetchCache()} if the {@link GudClientCache} reference returned by {@link #getCache()}
	 * is {@literal null}.
	 *
	 * This method is typically implemented by calling {@link GudClientCacheFactory#getAnyInstance()}
	 * depending on the {@link GudClientCache} type declared and used in the Spring application.
	 *
	 * @param <T> parameterized {@link Class} type extending {@link GudClientCache}.
	 * @return a (existing) reference to a {@link GudClientCache} instance.
	 * @see #fetchCache()
	 */
	protected abstract <T extends GudClientCache> T doFetchCache();

	/**
	 * Initializes the given {@link GudClientCacheFactory}
	 * with the configured {@link CacheFactoryInitializer}.
	 *
	 * @param factory {@link GudClientCacheFactory} to initialize.
	 * @return the initialized {@link GudClientCacheFactory}.
	 * @see CacheFactoryInitializer#initialize(Object)
	 * @see GudClientCacheFactory
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
	 * Registers configured, application-defined {@link GudTransactionListener GudTransactionListeners} with the cache
	 * (transaction manager) to listen for and receive transaction events when a (cache) transaction is processed
	 * (e.g. committed or rolled back).
	 *
	 * @param cache {@link GudClientCache} used to register the configured, application-defined
	 * {@link GudTransactionListener GudTransactionListeners}; must not be {@literal null}.
	 * @return the given {@link GudClientCache}.
	 * @see GudClientCache#getCacheTransactionManager()
	 * @see GudTransactionListener
	 * @see GudClientCache
	 */
	protected @NonNull GudClientCache registerTransactionListeners(@NonNull GudClientCache cache) {

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

		if (exception instanceof GudGemFireException) {
			return GemfireCacheUtils.convertGemfireAccessException((GudGemFireException) exception);
		}

		if (exception.getCause() instanceof GudGemFireException) {
			return GemfireCacheUtils.convertGemfireAccessException((GudGemFireException) exception.getCause());
		}

		if (exception.getCause() instanceof GudGemFireCheckedException) {
			return GemfireCacheUtils.convertGemfireAccessException((GudGemFireCheckedException) exception.getCause());
		}

		return null;
	}

	/**
	 * Callback interface for initializing a {@link GudClientCacheFactory} instance,
	 * which is used to create an instance of {@link GudClientCache}.
	 *
	 * @see GudClientCacheFactory
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
		 * @see GudClientCacheFactory
		 */
		T initialize(T cacheFactory);

	}

	/**
	 * Callback interface to configure PDX.
	 *
	 * @param <T> parameterized {@link Class} type capable of configuring Apache Geode PDX functionality.
	 * @see GudClientCacheFactory
	 * @see GudClientCache
	 */
	public interface PdxConfigurer<T> {

		T getTarget();

		PdxConfigurer<T> setDiskStoreName(String diskStoreName);

		PdxConfigurer<T> setIgnoreUnreadFields(Boolean ignoreUnreadFields);

		PdxConfigurer<T> setPersistent(Boolean persistent);

		PdxConfigurer<T> setReadSerialized(Boolean readSerialized);

		PdxConfigurer<T> setSerializer(GudPdxSerializer pdxSerializer);

	}
}
