/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import static org.springframework.data.gemfire.support.GemfireBeanFactoryLocator.BeanFactoryReference.newBeanFactoryReference;
import static org.springframework.data.gemfire.util.CollectionUtils.nullSafeSet;
import static org.springframework.data.gemfire.util.SpringExtensions.nullOrEquals;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link GemfireBeanFactoryLocator} class stores a reference to the Spring
 * {@link org.springframework.context.ApplicationContext} / {@link BeanFactory} needed to auto-wire
 * user application Apache Geode objects implementing the {@link org.apache.geode.cache.Declarable} interface
 * and defined in Apache Geode's native configuration format (e.g. {@literal cache.xml}.
 *
 * In most cases, a developer does not need to use this class directly as it is registered by
 * the {@link org.springframework.data.gemfire.CacheFactoryBean} or {@link org.springframework.data.gemfire.LocatorFactoryBean}
 * when the {@literal useBeanFactoryLocator} property is set, and used internally by both
 * the {@link WiringDeclarableSupport} and {@link LazyWiringDeclarableSupport} SDG classes.
 *
 * @author Costin Leau
 * @author John Blum
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.beans.factory.BeanFactoryAware
 * @see org.springframework.beans.factory.BeanNameAware
 * @see org.springframework.beans.factory.DisposableBean
 * @see org.springframework.beans.factory.InitializingBean
 * @see org.springframework.data.gemfire.support.LazyWiringDeclarableSupport
 * @see org.springframework.data.gemfire.support.WiringDeclarableSupport
 */
public class GemfireBeanFactoryLocator implements BeanFactoryAware, BeanNameAware, DisposableBean, InitializingBean {

	// Bean alias/name <-> BeanFactory mapping
	protected static final ConcurrentMap<String, BeanFactory> BEAN_FACTORIES = new ConcurrentHashMap<>();

	private BeanFactory beanFactory;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private Set<String> associatedBeanNameWithAliases = Collections.emptySet();

	private String associatedBeanName;

	/**
	 * Cleans up all {@link BeanFactory} references tracked by this {@literal locator}.
	 */
	public static void clear() {
		BEAN_FACTORIES.clear();
	}

	/**
	 * Factory method used to construct a new instance of {@link GemfireBeanFactoryLocator}.
	 *
	 * The {@link #afterPropertiesSet()} will be called after construction to initialize this {@literal locator}.
	 *
	 * @return a new, initialized instance of the {@link GemfireBeanFactoryLocator}.
	 * @see org.springframework.data.gemfire.support.GemfireBeanFactoryLocator
	 * @see GemfireBeanFactoryLocator()
	 * @see #afterPropertiesSet()
	 */
	public static @NonNull GemfireBeanFactoryLocator newBeanFactoryLocator() {

		GemfireBeanFactoryLocator beanFactoryLocator = new GemfireBeanFactoryLocator();

		beanFactoryLocator.afterPropertiesSet();

		return beanFactoryLocator;
	}

	/**
	 * Factory method used to construct a new instance of {@link GemfireBeanFactoryLocator} initialized with
	 * the given, default Spring {@link BeanFactory} and associated Spring {@link String bean name}.
	 *
	 * The {@link #afterPropertiesSet()} will be called after construction to initialize this {@literal locator}.
	 *
	 * @param beanFactory reference to the Spring {@link BeanFactory} used to resolve Spring bean references.
	 * @param associatedBeanName {@link String} containing the {@literal name} of the Spring bean associated with
	 * the Spring {@link BeanFactory}.
	 * @return a new {@link GemfireBeanFactoryLocator} initialized with the given, default Spring {@link BeanFactory}
	 * and associated Spring {@link String bean name}.
	 * @see org.springframework.data.gemfire.support.GemfireBeanFactoryLocator
	 * @see org.springframework.beans.factory.BeanFactory
	 * @see GemfireBeanFactoryLocator()
	 * @see #setBeanFactory(BeanFactory)
	 * @see #setBeanName(String)
	 * @see #afterPropertiesSet()
	 */
	public static @NonNull GemfireBeanFactoryLocator newBeanFactoryLocator(BeanFactory beanFactory,
			String associatedBeanName) {

		Assert.isTrue(beanFactory == null || StringUtils.hasText(associatedBeanName),
			"associatedBeanName must be specified when BeanFactory is not null");

		GemfireBeanFactoryLocator beanFactoryLocator = new GemfireBeanFactoryLocator();

		beanFactoryLocator.setBeanFactory(beanFactory);
		beanFactoryLocator.setBeanName(associatedBeanName);
		beanFactoryLocator.afterPropertiesSet();

		return beanFactoryLocator;
	}

	/**
	 * Resolves the {@link BeanFactory} mapped to the given {@link String beanFactoryKey}.
	 *
	 * @param beanFactoryKey {@link String} containing a key used to lookup the {@link BeanFactory}.
	 * @return the {@link BeanFactory} mapped to the given key.
	 * @throws IllegalArgumentException if a Spring {@link BeanFactory} could not be found
	 * for the given {@link String beanFactoryKey}.
	 * @see org.springframework.beans.factory.BeanFactory
	 */
	protected static @Nullable BeanFactory resolveBeanFactory(@NonNull String beanFactoryKey) {

		BeanFactory beanFactory = BEAN_FACTORIES.get(beanFactoryKey);

		Assert.isTrue(BEAN_FACTORIES.isEmpty() || beanFactory != null,
			String.format("BeanFactory for key [%s] was not found", beanFactoryKey));

		return beanFactory;
	}

	/**
	 * Resolves a single Spring {@link BeanFactory} from the mapping of registered {@link BeanFactory BeanFactories}.
	 *
	 * This class method is synchronized because it contains a "compound action", even though separate actions
	 * are performed on a {@link ConcurrentMap}, the actions are not independent and therefore must operate
	 * atomically.
	 *
	 * @return a single Spring {@link BeanFactory} from the registry.
	 * @throws IllegalStateException if the registry contains more than 1 registered Spring {@link BeanFactory},
	 * or no Spring {@link BeanFactory BeanFactories}.
	 * @see org.springframework.beans.factory.BeanFactory
	 */
	protected static synchronized @Nullable BeanFactory resolveSingleBeanFactory() {

		if (!BEAN_FACTORIES.isEmpty()) {

			boolean allTheSameBeanFactory = true;

			BeanFactory currentBeanFactory = null;

			for (BeanFactory beanFactory : BEAN_FACTORIES.values()) {

				allTheSameBeanFactory &= nullOrEquals(currentBeanFactory, beanFactory);
				currentBeanFactory = beanFactory;

				if (!allTheSameBeanFactory) {
					break;
				}
			}

			Assert.state(allTheSameBeanFactory,
				String.format("BeanFactory key must be specified when more than one BeanFactory %s is registered",
					new TreeSet<>(BEAN_FACTORIES.keySet())));

			return BEAN_FACTORIES.values().iterator().next();
		}

		return null;
	}

	/**
	 * Registers all the provided names for given Spring {@link BeanFactory}.
	 *
	 * @param names {@link Set} of names and aliases to associate with the Spring {@link BeanFactory}.
	 * @param beanFactory reference to the Spring {@link BeanFactory}.
	 * @see org.springframework.beans.factory.BeanFactory
	 * @throws IllegalArgumentException if {@link BeanFactory} is {@literal null}.
	 * @throws IllegalStateException if one of the provided names is already associated with
	 * an existing, other than given, Spring {@link BeanFactory}.
	 * @see org.springframework.beans.factory.BeanFactory
	 */
	protected static synchronized void registerAliases(Set<String> names, BeanFactory beanFactory) {

		Set<String> safeNames = nullSafeSet(names);

		Assert.isTrue(safeNames.isEmpty() || beanFactory != null,
			"BeanFactory must not be null when aliases are specified");

		for (String name : safeNames) {

			BeanFactory existingBeanFactory = BEAN_FACTORIES.putIfAbsent(name, beanFactory);

			Assert.isTrue(nullOrEquals(existingBeanFactory, beanFactory),
				String.format("BeanFactory reference already exists for key [%s]", name));
		}
	}

	/**
	 * Removes all Spring {@link BeanFactory} associations/mappings for the given {@link Set} of names.
	 *
	 * @param names {@link Set} of names identifying the associations/mappings to remove.
	 * @return a boolean value indicating whether all associations/mappings were removed successfully.
	 */
	protected static synchronized boolean unregisterAliases(Set<String> names) {
		return BEAN_FACTORIES.keySet().removeAll(names);
	}

	@Override
	public void afterPropertiesSet() {

		BeanFactory beanFactory = getBeanFactory();

		registerAliases(resolveAndInitializeBeanNamesWithAliases(beanFactory), beanFactory);
	}

	/**
	 * Resolves all names (including aliases) from the given Spring {@link BeanFactory}
	 * assigned to the {@link #getAssociatedBeanName()}.
	 *
	 * @param beanFactory {@link BeanFactory} used to resolve the names assigned to the Spring bean
	 * identified by the {@link #getAssociatedBeanName()}.
	 * @return a {@link Set} of all the names assigned to the Spring bean identified by
	 * the {@link #getAssociatedBeanName()} using the provided Spring {@link BeanFactory}.
	 * @see org.springframework.beans.factory.BeanFactory
	 * @see #getAssociatedBeanName()
	 */
	Set<String> resolveAndInitializeBeanNamesWithAliases(BeanFactory beanFactory) {

		String associatedBeanName = getAssociatedBeanName();

		if (beanFactory != null && StringUtils.hasText(associatedBeanName)) {

			String[] beanAliases = beanFactory.getAliases(associatedBeanName);

			this.associatedBeanNameWithAliases = new TreeSet<>();
			this.associatedBeanNameWithAliases.add(associatedBeanName);

			Collections.addAll(this.associatedBeanNameWithAliases, beanAliases);
		}

		return this.associatedBeanNameWithAliases;
	}

	@Override
	public void destroy() {
		unregisterAliases(getAssociatedBeanNameWithAliases());
	}

	/**
	 * Attempts to use a single, existing Spring {@link BeanFactory} from the registry based on
	 * the {@link #setBeanName(String)} beanName} property.
	 *
	 * @return the single Spring {@link BeanFactory} from the registry.
	 * @throws IllegalArgumentException if more than Spring {@link BeanFactory} is registered.
	 * @throws IllegalStateException if the {@link BeanFactory} with the associated
	 * {@link #setBeanName(String) beanName} is not found.
	 * @see org.springframework.beans.factory.BeanFactory
	 * @see #getAssociatedBeanName()
	 * @see #useBeanFactory(String)
	 */
	public BeanFactory useBeanFactory() {
		return useBeanFactory(getAssociatedBeanName());
	}

	/**
	 * Attempts to use the Spring {@link BeanFactory} idenified by the given {@code beanFactoryKey}.
	 *
	 * @param beanFactoryKey {@link String} containing the key used to lookup the Spring {@link BeanFactory}.
	 * @return the Spring {@link BeanFactory} for the given {@code beanFactoryKey}.
	 * @throws IllegalArgumentException if a Spring {@link BeanFactory} could not be found for {@code beanFactoryKey}.
	 * @throws IllegalStateException if {@literal useBeanFactoryLocator} was not configured.
	 * @see org.springframework.beans.factory.BeanFactory
	 * @see BeanFactoryReference#newBeanFactoryReference(BeanFactory)
	 * @see #resolveBeanFactory(String)
	 * @see #resolveSingleBeanFactory()
	 */
	public BeanFactory useBeanFactory(String beanFactoryKey) {
		return newBeanFactoryReference(StringUtils.hasText(beanFactoryKey)
			? resolveBeanFactory(beanFactoryKey) : resolveSingleBeanFactory()).get();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Returns a reference to the {@link BeanFactory} managed by this {@link GemfireBeanFactoryLocator} instance;
	 * Might be {@literal null} if this {@link GemfireBeanFactoryLocator} is just used to lookup
	 * an existing {@link BeanFactory} reference.
	 *
	 * @return the managed {@link BeanFactory} reference.
	 * @see org.springframework.beans.factory.BeanFactory
	 */
	protected BeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void setBeanName(String name) {
		this.associatedBeanName = name;
	}

	/**
	 * Gets the name of the Spring bean associated with the Spring {@link BeanFactory} that possibly created the bean.
	 *
	 * @return a {@link String} containing the name of the Spring bean associated with the Spring {@link BeanFactory}.
	 * @see #setBeanFactory(BeanFactory)
	 */
	protected String getAssociatedBeanName() {
		return this.associatedBeanName;
	}

	/**
	 * Returns a {@link Set} of all names and aliases assigned to the Spring bean that is associated with
	 * the Spring {@link BeanFactory}.
	 *
	 * @return a {@link Set} containing all the names and aliases assigned to the Spring bean associated with
	 * the Spring {@link BeanFactory}.
	 * @see #setBeanName(String)
	 */
	protected Set<String> getAssociatedBeanNameWithAliases() {
		return Collections.unmodifiableSet(nullSafeSet(this.associatedBeanNameWithAliases));
	}

	/**
	 * Builder method to set the bean name used by this locator to lookup a Spring {@link BeanFactory}.
	 *
	 * @param beanName {@link String} containing the bean name to set on this locator.
	 * @return this {@link GemfireBeanFactoryLocator}.
	 * @see #setBeanName(String)
	 */
	public GemfireBeanFactoryLocator withBeanName(String beanName) {
		setBeanName(beanName);
		return this;
	}

	/**
	 * Reference holder storing a reference to a Spring {@link BeanFactory}.
	 *
	 * @see org.springframework.beans.factory.BeanFactory
	 */
	protected static class BeanFactoryReference {

		protected static final String UNINITIALIZED_BEAN_FACTORY_REFERENCE_MESSAGE =
			"A BeanFactory was not initialized; Please verify the useBeanFactoryLocator property was properly set";

		private final AtomicReference<BeanFactory> beanFactory = new AtomicReference<>(null);

		/**
		 * Factory method to construct an instance of {@link BeanFactoryReference} initialized
		 * with the given {@link BeanFactory}.
		 *
		 * @param beanFactory {@link BeanFactory} reference to store.
		 * @return a new instance of {@link BeanFactoryReference} initialized with the given {@link BeanFactory}.
		 * @see org.springframework.beans.factory.BeanFactory
		 * @see GemfireBeanFactoryLocator.BeanFactoryReference(BeanFactory)
		 */
		protected static BeanFactoryReference newBeanFactoryReference(BeanFactory beanFactory) {
			return new BeanFactoryReference(beanFactory);
		}

		/**
		 * Constructs an instance of {@link BeanFactoryReference} initialized with the given {@link BeanFactory}.
		 *
		 * @param beanFactory {@link BeanFactory} reference to store; may be {@literal null}.
		 * @see org.springframework.beans.factory.BeanFactory
		 */
		protected BeanFactoryReference(BeanFactory beanFactory) {
			this.beanFactory.set(beanFactory);
		}

		/**
		 * Returns the reference to the Spring {@link BeanFactory}.
		 *
		 * @return a reference to the Spring {@link BeanFactory}.
		 * @see org.springframework.beans.factory.BeanFactory
		 */
		public BeanFactory get() {

			BeanFactory beanFactory = this.beanFactory.get();

			Assert.state(beanFactory != null, UNINITIALIZED_BEAN_FACTORY_REFERENCE_MESSAGE);

			return beanFactory;
		}

		/**
		 * Releases the stored reference to the Spring {@link BeanFactory}.
		 */
		public void release() {
			this.beanFactory.set(null);
		}
	}
}
