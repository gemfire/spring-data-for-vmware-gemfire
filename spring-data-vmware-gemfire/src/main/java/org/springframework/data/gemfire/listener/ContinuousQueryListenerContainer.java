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

import static org.springframework.data.gemfire.util.CollectionUtils.nullSafeList;
import static org.springframework.data.gemfire.util.CollectionUtils.nullSafeSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.gemfire.gud.api.GudCqAttributes;
import org.springframework.data.gemfire.gud.api.GudCqEvent;
import org.springframework.data.gemfire.gud.api.GudCqException;
import org.springframework.data.gemfire.gud.api.GudCqListener;
import org.springframework.data.gemfire.gud.api.GudCqQuery;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudQueryException;
import org.springframework.data.gemfire.gud.api.GudQueryService;
import org.springframework.data.gemfire.gud.api.GudRegionService;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.gemfire.GemfireQueryException;
import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.data.gemfire.client.PoolResolver;
import org.springframework.data.gemfire.client.support.DefaultableDelegatingPoolAdapter;
import org.springframework.data.gemfire.client.support.DelegatingPoolAdapter;
import org.springframework.data.gemfire.client.support.PoolManagerPoolResolver;
import org.springframework.data.gemfire.config.annotation.ContinuousQueryListenerContainerConfigurer;
import org.springframework.data.gemfire.config.xml.GemfireConstants;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.data.gemfire.util.SpringExtensions;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ErrorHandler;
import org.springframework.util.StringUtils;

/**
 * Container providing asynchronous processing/handling for Apache Geode Continuous Queries (CQ).
 *
 * @author Costin Leau
 * @author John Blum
 * @see Executor
 * @see GudRegionService
 * @see GudPool
 * @see GudCqAttributes
 * @see GudCqEvent
 * @see GudCqListener
 * @see GudCqQuery
 * @see GudQueryService
 * @see BeanFactory
 * @see BeanFactoryAware
 * @see BeanNameAware
 * @see DisposableBean
 * @see InitializingBean
 * @see SmartLifecycle
 * @see SimpleAsyncTaskExecutor
 * @see TaskExecutor
 * @see PoolResolver
 * @see DefaultableDelegatingPoolAdapter
 * @see DelegatingPoolAdapter
 * @see ErrorHandler
 * @since 1.1.0
 */
@SuppressWarnings("unused")
public class ContinuousQueryListenerContainer implements BeanFactoryAware, BeanNameAware,
		InitializingBean, DisposableBean, SmartLifecycle {

	// Default Thread name prefix is "ContinuousQueryListenerContainer-"
	public static final String DEFAULT_THREAD_NAME_PREFIX =
		String.format("%s-", ContinuousQueryListenerContainer.class.getSimpleName());

	// Default PoolResolver uses Apache Geode's PoolManager
	protected static final PoolResolver DEFAULT_POOL_RESOLVER = new PoolManagerPoolResolver();

	private boolean autoStartup = true;

	private volatile boolean initialized = false;
	private volatile boolean manageExecutor = false;
	private volatile boolean running = false;

	private int phase = Integer.MAX_VALUE;

	private BeanFactory beanFactory;

	private ErrorHandler errorHandler;

	private Executor taskExecutor;

	private List<ContinuousQueryListenerContainerConfigurer> cqListenerContainerConfigurers = Collections.emptyList();

	private ContinuousQueryListenerContainerConfigurer compositeCqListenerContainerConfigurer =
		(beanName, container) -> nullSafeList(this.cqListenerContainerConfigurers).forEach(configurer ->
			configurer.configure(beanName, container));

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private PoolResolver poolResolver = DEFAULT_POOL_RESOLVER;

	private Queue<GudCqQuery> continuousQueries = new ConcurrentLinkedQueue<>();

	private GudQueryService queryService;

	private Set<ContinuousQueryDefinition> continuousQueryDefinitions = new LinkedHashSet<>();

	private Supplier<org.springframework.data.gemfire.gud.api.GudCqAttributesFactory> cqAttributesFactorySupplier;

	private String beanName;
	private String poolName;

	@Override
	public void afterPropertiesSet() {

		applyContinuousQueryListenerContainerConfigurers();
		validateQueryService(initQueryService(eagerlyInitializePool(resolvePoolName())));
		initExecutor();
		initContinuousQueries();

		this.initialized = true;
	}

	/**
	 * Applies configuration customizations to this {@link ContinuousQueryListenerContainer} from the registered
	 * composite {@link ContinuousQueryListenerContainerConfigurer} objects.
	 *
	 * @see #getCompositeContinuousQueryListenerContainerConfigurer()
	 * @see #applyContinuousQueryListenerContainerConfigurers(ContinuousQueryListenerContainerConfigurer...)
	 */
	private void applyContinuousQueryListenerContainerConfigurers() {
		applyContinuousQueryListenerContainerConfigurers(getCompositeContinuousQueryListenerContainerConfigurer());
	}

	/**
	 * Applies an array of {@link ContinuousQueryListenerContainerConfigurer} objects to customize the configuration
	 * of this {@link ContinuousQueryListenerContainer}.
	 *
	 * @param configurers array of {@link ContinuousQueryListenerContainerConfigurer} used to customize
	 * the configuration of this {@link ContinuousQueryListenerContainer}.
	 * @see ContinuousQueryListenerContainerConfigurer
	 */
	protected void applyContinuousQueryListenerContainerConfigurers(
			ContinuousQueryListenerContainerConfigurer... configurers) {

		List<ContinuousQueryListenerContainerConfigurer> configurerList =
			Arrays.asList(ArrayUtils.nullSafeArray(configurers, ContinuousQueryListenerContainerConfigurer.class));

		applyContinuousQueryListenerContainerConfigurers(configurerList);
	}

	/**
	 * Applies an {@link Iterable} of {@link ContinuousQueryListenerContainerConfigurer} objects to customize
	 * the configuration of this {@link ContinuousQueryListenerContainer}.
	 *
	 * @param configurers {@link Iterable} of {@link ContinuousQueryListenerContainerConfigurer} used to customize
	 * the configuration of this {@link ContinuousQueryListenerContainer}.
	 * @see ContinuousQueryListenerContainerConfigurer
	 */
	protected void applyContinuousQueryListenerContainerConfigurers(
			Iterable<ContinuousQueryListenerContainerConfigurer> configurers) {

		StreamSupport.stream(CollectionUtils.nullSafeIterable(configurers).spliterator(), false)
			.forEach(configurer -> configurer.configure(getBeanName(), this));
	}

	/**
	 * Resolves a {@link GudPool} object with the given {@link String name} from the configured {@link PoolResolver}.
	 *
	 * @param poolName {@link String name} of the {@link GudPool} to resolve.
	 * @return a resolved {@link GudPool} object from the given {@link String name}.
	 * @see GudPool
	 * @see #getPoolResolver()
	 */
	@Nullable GudPool resolvePool(String poolName) {
		return getPoolResolver().resolve(poolName);
	}

	/**
	 * Resolves the name of the {@link GudPool} configured to handle the registered Continuous Queries.
	 *
	 * Note, the {@link GudPool} must have subscription enabled.
	 *
	 * @return the {@link String name} of the {@link GudPool} configured to handle the registered Continuous Queries.
	 */
	String resolvePoolName() {

		return Optional.ofNullable(getPoolName())
			.filter(StringUtils::hasText)
			.orElseGet(() ->
				Optional.ofNullable(getBeanFactory())
					.filter(it -> SpringExtensions.isMatchingBean(it, GemfireConstants.DEFAULT_GEMFIRE_POOL_NAME, GudPool.class))
					.map(it -> GemfireConstants.DEFAULT_GEMFIRE_POOL_NAME)
					.orElse(GemfireUtils.DEFAULT_POOL_NAME));
	}

	/**
	 * Eagerly initializes the {@link GudPool} with the given {@link String name}.
	 *
	 * First, this method attempts to use the configured {@link BeanFactory}, if not {@literal null}, to find a bean
	 * in the Spring container of type {@link GudPool} having the given {@link String name }and fetch the bean,
	 * thereby causing the {@link GudPool} bean to be initialized.
	 *
	 * However, if the {@link BeanFactory} was not configured, or no bean exists in the Spring container
	 * with the given {@link String name} or of the {@link GudPool} type, then the named {@link GudPool} is looked up
	 * via the configured PoolResolver.
	 *
	 * @param poolName {@link String} containing the name of the {@link GudPool} to initialize.
	 * @return the given {@link GudPool} name.
	 */
	String eagerlyInitializePool(String poolName) {

		Supplier<String> poolNameResolver = () -> {
			Assert.notNull(resolvePool(poolName), String.format("No Pool with name [%s] was found", poolName));
			return poolName;
		};

		return Optional.ofNullable(getBeanFactory())
			.filter(it -> SpringExtensions.isMatchingBean(it, poolName, GudPool.class))
			.map(it -> {
				try {
					it.getBean(poolName, GudPool.class);
					return poolName;
				}
				catch (BeansException ignore) {
					return poolNameResolver.get();
				}
			})
			.orElseGet(poolNameResolver);
	}

	/**
	 * Initializes the {@link GudQueryService} used to register Continuous Queries (CQ).
	 *
	 * @param poolName {@link String} containing the name of the {@link GudPool} used obtain the {@link GudQueryService}
	 * if CQs are tied to a specific {@link GudPool}.
	 * @return the initialized {@link GudQueryService}.
	 * @see GudQueryService
	 */
	GudQueryService initQueryService(String poolName) {

		GudQueryService queryService = getQueryService();

		if (queryService == null || StringUtils.hasText(poolName)) {

			GudPool resolvedPool = resolvePool(poolName);

			DefaultableDelegatingPoolAdapter poolAdapter =
				DefaultableDelegatingPoolAdapter.from(DelegatingPoolAdapter.from(resolvedPool));

			setQueryService(poolAdapter.preferPool().getQueryService(queryService));
		}

		return getQueryService();
	}

	/**
	 * Verifies the given {@link GudQueryService} is valid.
	 *
	 * @param queryService {@link GudQueryService} to validate.
	 * @throws IllegalStateException if the {@link GudQueryService} is {@literal null}.
	 * @return the given {@link GudQueryService}
	 * @see GudQueryService
	 */
	private GudQueryService validateQueryService(GudQueryService queryService) {

		Assert.state(queryService != null, "QueryService is required");

		return queryService;
	}

	/**
	 * Initialize the {@link Executor} used to process CQ events asynchronously.
	 *
	 * @return a new isntance of {@link Executor} used to process CQ events asynchronously.
	 * @see Executor
	 */
	Executor initExecutor() {

		if (getTaskExecutor() == null) {
			setTaskExecutor(createDefaultTaskExecutor());
			this.manageExecutor = true;
		}

		return getTaskExecutor();
	}

	/**
	 * Creates a default {@link TaskExecutor}.
	 *
	 * <p>Called if no explicit {@link TaskExecutor} has been configured.
	 *
	 * <p>The default implementation builds a {@link SimpleAsyncTaskExecutor} with the specified bean name
	 * (or the class name, if no bean name is specified) as the Thread name prefix.</p>
	 *
	 * @return an instance of the {@link TaskExecutor} used to process CQ events asynchronously.
	 * @see SimpleAsyncTaskExecutor
	 */
	protected Executor createDefaultTaskExecutor() {

		String threadNamePrefix = Optional.ofNullable(getBeanName())
			.filter(StringUtils::hasText)
			.map(it -> String.format("%s-", it))
			.orElse(DEFAULT_THREAD_NAME_PREFIX);

		return new SimpleAsyncTaskExecutor(threadNamePrefix);
	}

	/**
	 * Initializes all the {@link GudCqQuery Continuous Queries} defined by
	 * the {@link ContinuousQueryDefinition Continuous Query Defintions}.
	 *
	 * @see #getContinuousQueryDefinitions()
	 * @see #initContinuousQueries(Set)
	 */
	private void initContinuousQueries() {
		initContinuousQueries(getContinuousQueryDefinitions());
	}

	private void initContinuousQueries(Set<ContinuousQueryDefinition> continuousQueryDefinitions) {

		// Stop the ContinuousQueryListenerContainer if currently running...
		stop();

		// Close any existing continuous queries...
		closeQueries();

		// Add current continuous queries based on the definitions from the configuration...
		continuousQueryDefinitions.forEach(this::addContinuousQuery);
	}

	/**
	 * Determines whether this container is currently active, i.e., whether it has been setup and initialized
	 * but not shutdown yet.
	 *
	 * @return a boolean indicating whether the container is active.
	 */
	public boolean isActive() {
		return this.initialized;
	}

	/**
	 * Sets whether the CQ listener container should automatically start on startup.
	 *
	 * @param autoStartup a boolean value indicating whether this CQ listener container should automatically start.
	 */
	public void setAutoStartup(final boolean autoStartup) {
		this.autoStartup = autoStartup;
	}

	/**
	 * Determines whether this CQ listener container will automatically start on startup.
	 *
	 * @return a boolean value indicating whether this CQ listener container automatically starts.
	 * @see SmartLifecycle#isAutoStartup()
	 */
	@Override
	public boolean isAutoStartup() {
		return this.autoStartup;
	}

	/**
	 * Determines whether the container has be started and is currently running.
	 *
	 * @return a boolean value indicating whether the container has been started and is currently running.
	 */
	@Override
	public synchronized boolean isRunning() {
		return this.running;
	}

	/**
	 * Sets the {@link BeanFactory} containing this bean.
	 *
	 * @param beanFactory the Spring {@link BeanFactory} containing this bean.
	 * @throws BeansException if an initialization error occurs.
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	/**
	 * Returns a reference to the configured {@link BeanFactory}.
	 *
	 * @return a reference to the configured {@link BeanFactory}.
	 * @see BeanFactory
	 */
	protected BeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	/**
	 * Set the name of the bean in the bean factory that created this bean.
	 * <p>Invoked after population of normal bean properties but before an
	 * init callback such as {@link InitializingBean#afterPropertiesSet()}
	 * or a custom init-method.</p>
	 *
	 * @param name the name of the bean in the factory.
	 */
	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

	/**
	 * Returns the configured {@link String bean name} of this container.
	 *
	 * @return the configured {@link String bean name} of this container.
	 */
	protected String getBeanName() {
		return this.beanName;
	}

	/**
	 * Set the underlying RegionService (GemFire Cache) used for registering Queries.
	 *
	 * @param cache the RegionService (GemFire Cache) used for registering Queries.
	 * @see GudRegionService
	 */
	public void setCache(GudRegionService cache) {
		setQueryService(cache.getQueryService());
	}

	/**
	 * Returns a reference to all the configured/registered {@link GudCqQuery Continuous Queries}.
	 *
	 * @return a reference to all the configured/registered {@link GudCqQuery Continuous Queries}.
	 * @see GudCqQuery
	 * @see Queue
	 */
	protected Queue<GudCqQuery> getContinuousQueries() {
		return this.continuousQueries;
	}

	/**
	 * Returns a reference to all the configured {@link ContinuousQueryDefinition ContinuousQueryDefinitions}.
	 *
	 * @return a reference to all the configured {@link ContinuousQueryDefinition ContinuousQueryDefinitions}.
	 * @see ContinuousQueryDefinition
	 * @see Set
	 */
	protected Set<ContinuousQueryDefinition> getContinuousQueryDefinitions() {
		return this.continuousQueryDefinitions;
	}

	/**
	 * Null-safe operation setting an array of {@link ContinuousQueryListenerContainerConfigurer} objects used to
	 * customize the configuration of this {@link ContinuousQueryListenerContainer}.
	 *
	 * @param configurers array of {@link ContinuousQueryListenerContainerConfigurer} objects used to customize
	 * the configuration of this {@link ContinuousQueryListenerContainer}.
	 * @see ContinuousQueryListenerContainerConfigurer
	 * @see #setContinuousQueryListenerContainerConfigurers(List)
	 */
	public void setContinuousQueryListenerContainerConfigurers(ContinuousQueryListenerContainerConfigurer... configurers) {

		List<ContinuousQueryListenerContainerConfigurer> configurerList =
			Arrays.asList(ArrayUtils.nullSafeArray(configurers, ContinuousQueryListenerContainerConfigurer.class));

		setContinuousQueryListenerContainerConfigurers(configurerList);
	}

	/**
	 * Null-safe operation setting an {@link Iterable} of {@link ContinuousQueryListenerContainerConfigurer} objects
	 * used to customize the configuration of this {@link ContinuousQueryListenerContainer}.
	 *
	 * @param configurers {@link Iterable} of {@link ContinuousQueryListenerContainerConfigurer} objects used to
	 * customize the configuration of this {@link ContinuousQueryListenerContainer}.
	 * @see ContinuousQueryListenerContainerConfigurer
	 */
	public void setContinuousQueryListenerContainerConfigurers(List<ContinuousQueryListenerContainerConfigurer> configurers) {
		this.cqListenerContainerConfigurers = CollectionUtils.nullSafeList(configurers);
	}

	/**
	 * Returns a <a href="https://en.wikipedia.org/wiki/Composite_pattern">Composite</a> object containing
	 * the collection of {@link ContinuousQueryListenerContainerConfigurer} objects used to customize the configuration
	 * of this {@link ContinuousQueryListenerContainer}.
	 *
	 * @return a Composite object containing a collection of {@link ContinuousQueryListenerContainerConfigurer} objects
	 * used to customize the configuration of this {@link ContinuousQueryListenerContainer}.
	 * @see ContinuousQueryListenerContainerConfigurer
	 */
	protected ContinuousQueryListenerContainerConfigurer getCompositeContinuousQueryListenerContainerConfigurer() {
		return this.compositeCqListenerContainerConfigurer;
	}

	/**
	 * Set an {@link ErrorHandler} to be invoked in case of any uncaught {@link Exception Exceptions} thrown
	 * while processing a CQ event.
	 *
	 * By default there is <b>no</b> {@link ErrorHandler} configured so error-level logging is the only result.
	 *
	 * @param errorHandler {@link ErrorHandler} invoked when uncaught {@link Exception Exceptions} are thrown
	 * while processing the CQ event.
	 * @see ErrorHandler
	 */
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	/**
	 * Returns an {@link Optional} reference to the configured {@link ErrorHandler} invoked when
	 * any unhandled {@link Exception Exceptions} are thrown when invoking CQ listeners processing CQ events.
	 *
	 * @return an {@link Optional} reference to the configured {@link ErrorHandler}.
	 * @see ErrorHandler
	 */
	public Optional<ErrorHandler> getErrorHandler() {
		return Optional.ofNullable(this.errorHandler);
	}

	/**
	 * Sets the phase in which this CQ listener container will start in the Spring container.
	 *
	 * @param phase the phase value of this CQ listener container.
	 */
	public void setPhase(final int phase) {
		this.phase = phase;
	}

	/**
	 * Gets the phase in which this CQ listener container will start in the Spring container.
	 *
	 * @return the phase value of this CQ listener container.
	 * @see org.springframework.context.Phased#getPhase()
	 */
	@Override
	public int getPhase() {
		return this.phase;
	}

	/**
	 * Set the name of the {@link GudPool} used for performing the queries by this container.
	 *
	 * @param poolName the name of the pool to be used by the container
	 */
	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	/**
	 * Returns the configured {@link String pool name}.
	 *
	 * @return the configured {@link String pool name}.
	 */
	public String getPoolName() {
		return this.poolName;
	}

	/**
	 * Configures the {@link PoolResolver} to resolve {@link GudPool} objects by {@link String name}
	 * from the Apache Geode cache.
	 *
	 * @param poolResolver the configured {@link PoolResolver} used to resolve {@link GudPool} objects
	 * by {@link String name}.
	 * @see PoolResolver
	 */
	public void setPoolResolver(PoolResolver poolResolver) {
		this.poolResolver = poolResolver;
	}

	/**
	 * Returns the configured {@link PoolResolver} used to resolve {@link GudPool} object by {@link String name}.
	 *
	 * @return the configured {@link PoolResolver}.
	 * @see PoolResolver
	 */
	public PoolResolver getPoolResolver() {
		return this.poolResolver != null ? this.poolResolver : DEFAULT_POOL_RESOLVER;
	}

	/**
	 * Attaches the given query definitions.
	 *
	 * @param queries set of queries
	 */
	public void setQueryListeners(Set<ContinuousQueryDefinition> queries) {

		getContinuousQueryDefinitions().clear();
		getContinuousQueryDefinitions().addAll(nullSafeSet(queries));
	}

	/**
	 * Set the GemFire QueryService used by this container to create ContinuousQueries (CQ).
	 *
	 * @param queryService the GemFire QueryService object used by the container to create ContinuousQueries (CQ).
	 * @see GudQueryService
	 */
	public void setQueryService(GudQueryService queryService) {
		this.queryService = queryService;
	}

	/**
	 * Returns a reference to the configured {@link GudQueryService}.
	 *
	 * @return a reference to the configured {@link GudQueryService}.
	 * @see GudQueryService
	 */
	public GudQueryService getQueryService() {
		return this.queryService;
	}

	/**
	 * Sets the Task Executor used for running the event listeners when messages are received.
	 * If no task executor is set, an instance of {@link SimpleAsyncTaskExecutor} will be used by default.
	 * The task executor can be adjusted depending on the work done by the listeners and the number of
	 * messages coming in.
	 *
	 * @param taskExecutor The Task Executor used to run event listeners when query results messages are received.
	 * @see Executor
	 */
	public void setTaskExecutor(Executor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	/**
	 * Returns a reference to the configured {@link Executor TaskExecutor}.
	 *
	 * @return a reference to the configured {@link Executor TaskExecutor}.
	 * @see Executor
	 */
	public Executor getTaskExecutor() {
		return this.taskExecutor;
	}

	/**
	 * Adds a {@link ContinuousQueryDefinition Continuous Query (CQ) definition} to the (potentially running) container.
	 *
	 * If the container is running, the listener starts receiving (matching) messages as soon as possible.
	 *
	 * @param definition {@link ContinuousQueryDefinition Continuous Query (CQ) definition} to register.
	 * @see ContinuousQueryDefinition
	 */
	public void addListener(ContinuousQueryDefinition definition) {

		GudCqQuery query = addContinuousQuery(definition);

		if (isRunning()) {
			execute(query);
		}
	}

	public boolean addContinuousQueryDefinition(ContinuousQueryDefinition definition) {

		return Optional.ofNullable(definition)
			.map(getContinuousQueryDefinitions()::add)
			.orElse(false);
	}

	GudCqQuery addContinuousQuery(ContinuousQueryDefinition definition) {

		try {

			GudCqAttributes attributes = definition.toCqAttributes(newCqAttributesFactory(), this::newCqListener, definition.getExcludedEvents());

			GudCqQuery query = definition.isNamed()
				? newNamedContinuousQuery(definition, attributes)
				: newUnnamedContinuousQuery(definition, attributes);

			return manage(query);
		}
		catch (GudQueryException cause) {
			throw new GemfireQueryException(String.format("Unable to create query [%s]", definition.getQuery()), cause);
		}
	}

	/**
	 * Sets the supplier for creating {@link org.springframework.data.gemfire.gud.api.GudCqAttributesFactory} instances.
	 *
	 * @param supplier the supplier for creating CqAttributesFactory instances
	 */
	public void setCqAttributesFactorySupplier(Supplier<org.springframework.data.gemfire.gud.api.GudCqAttributesFactory> supplier) {
		this.cqAttributesFactorySupplier = supplier;
	}

	protected org.springframework.data.gemfire.gud.api.GudCqAttributesFactory newCqAttributesFactory() {
		Assert.state(this.cqAttributesFactorySupplier != null,
			"A GudCqAttributesFactory supplier must be configured");
		return this.cqAttributesFactorySupplier.get();
	}

	protected GudCqListener newCqListener(ContinuousQueryListener listener) {
		return new EventDispatcherAdapter(listener);
	}

	private GudCqQuery newNamedContinuousQuery(ContinuousQueryDefinition definition, GudCqAttributes attributes)
			throws GudQueryException {

		return getQueryService().newCq(definition.getName(), definition.getQuery(), attributes, definition.isDurable());
	}

	private GudCqQuery newUnnamedContinuousQuery(ContinuousQueryDefinition definition, GudCqAttributes attributes)
			throws GudCqException {

		return getQueryService().newCq(definition.getQuery(), attributes, definition.isDurable());
	}

	private GudCqQuery manage(GudCqQuery query) {

		getContinuousQueries().add(query);

		return query;
	}

	@Override
	public synchronized void start() {

		if (!isRunning()) {

			doStart();
			this.running = true;

			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Started ContinuousQueryListenerContainer");
			}
		}
	}

	void doStart() {
		getContinuousQueries().forEach(this::execute);
	}

	private void execute(GudCqQuery query) {

		try {
			query.execute();
		}
		catch (GudCqException cause) {
			throw new GemfireQueryException(String.format("Could not execute query [%1$s]; state is [%2$s]",
				query.getName(), query.getState()), cause);
		}
	}

	/**
	 * Asynchronously dispatches the {@link GudCqEvent CQ event} to the targeted {@link ContinuousQueryListener}.
	 *
	 * @param listener {@link ContinuousQueryListener} which will process/handle the {@link GudCqEvent CQ event}.
	 * @param event {@link GudCqEvent CQ event} to process.
	 * @see ContinuousQueryListener
	 * @see GudCqEvent
	 */
	protected void dispatchEvent(ContinuousQueryListener listener, GudCqEvent event) {
		getTaskExecutor().execute(() -> notify(listener, event));
	}

	/**
	 * Invoke the specified {@link ContinuousQueryListener listener} to process/handle the {@link GudCqEvent CQ event}.
	 *
	 * @param listener {@link ContinuousQueryListener} to notify of the {@link GudCqEvent CQ event}.
	 * @param event {@link GudCqEvent CQ event} to process/handle.
	 * @see #handleListenerError(Throwable)
	 */
	private void notify(ContinuousQueryListener listener, GudCqEvent event) {

		try {
			listener.onEvent(event);
		}
		catch (Throwable cause) {
			handleListenerError(cause);
		}
	}

	/**
	 * Invokes the configured {@link ErrorHandler} (if any) to handle the {@link Exception} thrown by the CQ listener.
	 *
	 * Logs at warning level if no {@link ErrorHandler} was configured.
	 *
	 * Logs at debug level if the CQ listener container was shutdown at the time when the CQ listener
	 * {@link Exception} was thrown.
	 *
	 * @param cause {@link Throwable uncaught error} thrown during normal CQ event processing.
	 * @see #setErrorHandler(ErrorHandler)
	 * @see #getErrorHandler()
	 */
	private void handleListenerError(Throwable cause) {

		getErrorHandler().filter(errorHandler -> {

				boolean active = this.isActive();

				if (!active && this.logger.isDebugEnabled()) {
					this.logger.debug("A CQ listener exception occurred after container shutdown; ErrorHandler will not be invoked", cause);
				}

				return active;
			})
			.ifPresent(errorHandler -> errorHandler.handleError(cause));

		if (!getErrorHandler().isPresent() && this.logger.isWarnEnabled()) {
			this.logger.warn("Execution of CQ listener failed; No ErrorHandler was configured", cause);
		}
	}

	@Override
	public void stop(Runnable callback) {

		stop();
		callback.run();
	}

	@Override
	public synchronized void stop() {

		if (isRunning()) {
			doStop();
			this.running = false;
		}

		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Stopped ContinuousQueryListenerContainer");
		}
	}

	void doStop() {

		getContinuousQueries().forEach(query -> {
			try {
				query.stop();
			}
			catch (Exception cause) {
				if (this.logger.isWarnEnabled()) {
					this.logger.warn(String.format("Cannot stop query [%1$s]; state is [%2$s]",
						query.getName(), query.getState()), cause);
				}
			}
		});
	}

	@Override
	public void destroy() {

		stop();
		closeQueries();
		destroyExecutor();

		this.initialized = false;
	}

	private void closeQueries() {

		getContinuousQueries().stream()
			.filter(query -> !query.isClosed())
			.forEach(query -> {
				try {
					query.close();
				}
				catch (Exception cause) {
					if (logger.isWarnEnabled()) {
						logger.warn(String.format("Cannot close query [%1$s]; state is [%2$s]",
							query.getName(), query.getState()), cause);
					}
				}
			});

		getContinuousQueries().clear();
	}

	private void destroyExecutor() {

		Optional.ofNullable(getTaskExecutor())
			.filter(it -> this.manageExecutor)
			.filter(DisposableBean.class::isInstance)
			.ifPresent(it -> {
				try {

					((DisposableBean) it).destroy();

					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Stopped internally-managed TaskExecutor {}", it);
					}
				}
				catch (Exception cause) {
					this.logger.warn("Failed to properly destroy the managed TaskExecutor {}: {}",
						it, cause.getMessage());
				}
			});
	}

	protected class EventDispatcherAdapter implements GudCqListener {

		private final ContinuousQueryListener listener;

		protected EventDispatcherAdapter(ContinuousQueryListener listener) {

			Assert.notNull(listener, "ContinuousQueryListener is required");

			this.listener = listener;
		}

		protected ContinuousQueryListener getListener() {
			return this.listener;
		}

		public void onError(GudCqEvent event) {
			dispatchEvent(getListener(), event);
		}

		public void onEvent(GudCqEvent event) {
			dispatchEvent(getListener(), event);
		}

		public void close() { }

	}
}
