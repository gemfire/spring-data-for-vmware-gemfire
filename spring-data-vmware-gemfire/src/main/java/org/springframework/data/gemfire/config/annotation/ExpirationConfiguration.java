/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.springframework.data.gemfire.config.annotation.EnableExpiration.ExpirationPolicy;
import static org.springframework.data.gemfire.config.annotation.EnableExpiration.ExpirationType;
import static org.springframework.data.gemfire.util.CollectionUtils.nullSafeIterable;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalStateException;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.geode.cache.AttributesMutator;
import org.apache.geode.cache.CustomExpiry;
import org.apache.geode.cache.ExpirationAction;
import org.apache.geode.cache.ExpirationAttributes;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.PeerRegionFactoryBean;
import org.springframework.data.gemfire.ResolvableRegionFactoryBean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.AbstractAnnotationConfigSupport;
import org.springframework.data.gemfire.expiration.AnnotationBasedExpiration;
import org.springframework.data.gemfire.expiration.ExpirationActionType;
import org.springframework.data.gemfire.expiration.ExpiringRegionFactoryBean;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.data.gemfire.util.SpringExtensions;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * {@link ExpirationConfiguration} is a Spring {@link Configuration} class used to configure expiration policies
 * for GemFire/Geode {@link Region Regions}.
 *
 * @author John Blum
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.context.annotation.ImportAware
 * @see org.springframework.data.gemfire.config.annotation.EnableExpiration
 * @see org.apache.geode.cache.ExpirationAttributes
 * @see org.apache.geode.cache.Region
 * @since 1.9.0
 */
@Configuration
public class ExpirationConfiguration extends AbstractAnnotationConfigSupport implements ImportAware {

	protected static final int DEFAULT_TIMEOUT = 0;

	protected static final ExpirationActionType DEFAULT_ACTION = ExpirationActionType.DEFAULT;

	protected static final ExpirationType[] DEFAULT_EXPIRATION_TYPES = {
		ExpirationType.IDLE_TIMEOUT,
		ExpirationType.TIME_TO_LIVE,
	};

	private ExpirationPolicyConfigurer expirationPolicyConfigurer;

	/**
	 * Returns the {@link Annotation} {@link Class type} that enables and configures Expiration.
	 *
	 * @return the {@link Annotation} {@link Class type} that enables and configures Expiration.
	 * @see java.lang.annotation.Annotation
	 * @see java.lang.Class
	 */
	protected @NonNull Class<? extends Annotation> getAnnotationType() {
		return EnableExpiration.class;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void setImportMetadata(@NonNull AnnotationMetadata importMetadata) {

		if (isAnnotationPresent(importMetadata)) {

			AnnotationAttributes enableExpirationAttributes = getAnnotationAttributes(importMetadata);

			AnnotationAttributes[] policies = enableExpirationAttributes.getAnnotationArray("policies");

			for (AnnotationAttributes expirationPolicyAttributes :
					ArrayUtils.nullSafeArray(policies, AnnotationAttributes.class)) {

				this.expirationPolicyConfigurer =
					ComposableExpirationPolicyConfigurer.compose(this.expirationPolicyConfigurer,
						ExpirationPolicyMetaData.from(expirationPolicyAttributes));
			}

			this.expirationPolicyConfigurer = Optional.ofNullable(this.expirationPolicyConfigurer)
				.orElseGet(ExpirationPolicyMetaData::fromDefaults);
		}
	}

	/**
	 * Determines whether the Spring bean is an instance of {@link ExpiringRegionFactoryBean}
	 *
	 * @param bean Spring bean to evaluate.
	 * @return a boolean value indicating whether the Spring bean is an instance of {@link ExpiringRegionFactoryBean}.
	 * @see org.springframework.data.gemfire.expiration.ExpiringRegionFactoryBean
	 * @see org.springframework.data.gemfire.client.ClientRegionFactoryBean
	 * @see PeerRegionFactoryBean
	 */
	protected static boolean isRegionFactoryBean(Object bean) {
		return bean instanceof ExpiringRegionFactoryBean;
	}

	protected ExpirationPolicyConfigurer getExpirationPolicyConfigurer() {

		return Optional.ofNullable(this.expirationPolicyConfigurer).orElseThrow(() ->
			newIllegalStateException("ExpirationPolicyConfigurer was not properly configured and initialized"));
	}

	@Bean
	@SuppressWarnings("unused")
	public BeanPostProcessor expirationBeanPostProcessor() {

		return new BeanPostProcessor() {

			@Override
			public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
				return isRegionFactoryBean(bean) ? getExpirationPolicyConfigurer().configure(bean) : bean;
			}
		};
	}

	@SuppressWarnings("unused")
	@EventListener(ContextRefreshedEvent.class)
	public void expirationContextRefreshedListener(@NonNull ContextRefreshedEvent event) {

		ApplicationContext applicationContext = event.getApplicationContext();

		for (Region<?, ?> region : applicationContext.getBeansOfType(Region.class).values()) {
			getExpirationPolicyConfigurer().configure(region);
		}
	}

	/**
	 * Interface defining a contract for implementations that configure a {@link Region Region's} expiration policy.
	 *
	 * @see java.lang.FunctionalInterface
	 */
	@FunctionalInterface
	protected interface ExpirationPolicyConfigurer {

		/**
		 * Configures the expiration policy for the given {@link Region}.
		 *
		 * @param regionBean {@link Region} object who's expiration policy will be configured.
		 * @return the given {@link Region} object.
		 * @see org.apache.geode.cache.Region
		 */
		Object configure(Object regionBean);

		/**
		 * Configures the expiration policy for the given {@link Region}.
		 *
		 * @param region {@link Region} who's expiration policy will be configured.
		 * @return the given {@link Region}.
		 * @see org.apache.geode.cache.Region
		 */
		default Region<?, ?> configure(Region<?, ?> region) {
			return region;
		}
	}

	/**
	 * {@link ComposableExpirationPolicyConfigurer} is a {@link ExpirationPolicyConfigurer} implementation
	 * that additionally implements the Composition Software Design Pattern to treat a collection of
	 * {@link ExpirationPolicyConfigurer} objects as a single instace of the {@link ExpirationPolicyConfigurer}.
	 *
	 * @see org.springframework.data.gemfire.config.annotation.ExpirationConfiguration.ExpirationPolicyConfigurer
	 * @see <a href="https://en.wikipedia.org/wiki/Composite_pattern">Composition Software Design Pattern</a>
	 */
	protected static class ComposableExpirationPolicyConfigurer implements ExpirationPolicyConfigurer {

		private final ExpirationPolicyConfigurer one;
		private final ExpirationPolicyConfigurer two;

		/**
		 * Factory method to compose an array of {@link ExpirationPolicyConfigurer} objects.
		 *
		 * @param array array of {@link ComposableExpirationPolicyConfigurer} objects to compose.
		 * @return a composition containing all the {@link ExpirationPolicyConfigurer} objects in the array.
		 * @see org.springframework.data.gemfire.config.annotation.ExpirationConfiguration.ExpirationPolicyConfigurer
		 * @see #compose(Iterable)
		 */
		protected static ExpirationPolicyConfigurer compose(ExpirationPolicyConfigurer[] array) {
			return compose(Arrays.asList(ArrayUtils.nullSafeArray(array, ExpirationPolicyConfigurer.class)));
		}

		/**
		 * Factory method to compose an {@link Iterable} of {@link ExpirationPolicyConfigurer} objects.
		 *
		 * @param iterable {@link Iterable} of {@link ComposableExpirationPolicyConfigurer} objects to compose.
		 * @return a composition containing all the {@link ExpirationPolicyConfigurer} objects in the {@link Iterable}.
		 * @see org.springframework.data.gemfire.config.annotation.ExpirationConfiguration.ExpirationPolicyConfigurer
		 * @see #compose(ExpirationPolicyConfigurer, ExpirationPolicyConfigurer)
		 */
		protected static ExpirationPolicyConfigurer compose(Iterable<ExpirationPolicyConfigurer> iterable) {

			ExpirationPolicyConfigurer current = null;

			for (ExpirationPolicyConfigurer configurer : nullSafeIterable(iterable)) {
				current = compose(current, configurer);
			}

			return current;
		}

		/**
		 * Factory method to compose 2 {@link ExpirationPolicyConfigurer} objects.
		 *
		 * @param one first {@link ComposableExpirationPolicyConfigurer} to compose.
		 * @param two second {@link ComposableExpirationPolicyConfigurer} to compose.
		 * @return a composition of the 2 {@link ExpirationPolicyConfigurer} objects.
		 * Returns {@code one} if {@code two} is {@literal null} or {@code two} if {@code one} is {@literal null}.
		 * @see org.springframework.data.gemfire.config.annotation.ExpirationConfiguration.ExpirationPolicyConfigurer
		 */
		protected static ExpirationPolicyConfigurer compose(ExpirationPolicyConfigurer one,
				ExpirationPolicyConfigurer two) {

			return one == null ? two
				: two == null ? one
				: new ComposableExpirationPolicyConfigurer(one, two);
		}

		/**
		 * Constructs an instance of the {@link ComposableExpirationPolicyConfigurer} initialized with
		 * 2 {@link ExpirationPolicyConfigurer} objects.
		 *
		 * @param one first {@link ComposableExpirationPolicyConfigurer} to compose.
		 * @param two second {@link ComposableExpirationPolicyConfigurer} to compose.
		 * @see org.springframework.data.gemfire.config.annotation.ExpirationConfiguration.ExpirationPolicyConfigurer
		 */
		private ComposableExpirationPolicyConfigurer(@NonNull ExpirationPolicyConfigurer one,
				@NonNull ExpirationPolicyConfigurer two) {

			this.one = one;
			this.two = two;
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public Object configure(Object regionBean) {
			return this.two.configure(this.one.configure(regionBean));
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public Region<?, ?> configure(Region<?, ?> region) {
			return this.two.configure(this.one.configure(region));
		}
	}

	/**
	 * {@link ExpirationPolicyMetaData} is a {@link ExpirationPolicyConfigurer} implementation that encapsulates
	 * the expiration configuration meta-data (e.g. expiration timeout and action) necessary to configure
	 * a {@link Region Regions's} expiration policy and behavior.
	 *
	 * This class is meant to capture the expiration configuration meta-data specified in the {@link ExpirationPolicy}
	 * nested annotation in the application-level {@link EnableExpiration} annotation.

	 * @see org.springframework.data.gemfire.config.annotation.ExpirationConfiguration.ExpirationPolicyConfigurer
	 */
	protected static class ExpirationPolicyMetaData implements ExpirationPolicyConfigurer {

		protected static final String[] ALL_REGIONS = new String[0];

		/**
		 * Factory method to construct an instance of {@link ExpirationPolicyMetaData} initialized with
		 * the given {@link AnnotationAttributes} from the nested {@link ExpirationPolicy} annotation
		 * specified in an application-level {@link EnableExpiration} annotation.
		 *
		 * @param expirationPolicyAttributes {@link AnnotationAttributes} from a {@link ExpirationPolicy} annotation.
		 * @return an instance of the {@link ExpirationPolicyMetaData} initialized from
		 * {@link ExpirationPolicy} {@link AnnotationAttributes}.
		 * @throws IllegalArgumentException if {@link AnnotationAttributes#annotationType()} is not assignable to
		 * {@link ExpirationPolicy}.
		 * @see #newExpirationPolicyMetaData(int, ExpirationActionType, String[], ExpirationType[])
		 * @see org.springframework.core.annotation.AnnotationAttributes
		 */
		protected static ExpirationPolicyMetaData from(AnnotationAttributes expirationPolicyAttributes) {

			Assert.isAssignable(ExpirationPolicy.class, expirationPolicyAttributes.annotationType());

			return newExpirationPolicyMetaData((Integer) expirationPolicyAttributes.get("timeout"),
				expirationPolicyAttributes.getEnum("action"),
				expirationPolicyAttributes.getStringArray("regionNames"),
				(ExpirationType[]) expirationPolicyAttributes.get("types"));
		}

		/**
		 * Factory method to construct an instance of {@link ExpirationPolicyMetaData} initialized with
		 * the given attribute values from the nested {@link ExpirationPolicy} annotation specified in
		 * an application-level {@link EnableExpiration} annotation.
		 *
		 * @param expirationPolicy {@link ExpirationPolicy} annotation containing the attribute values
		 * used to initialize the {@link ExpirationPolicyMetaData} instance.
		 * @return an instance of the {@link ExpirationPolicyMetaData} initialized from
		 * {@link ExpirationPolicy} attributes values.
		 * @see org.springframework.data.gemfire.config.annotation.EnableExpiration.ExpirationPolicy
		 * @see #newExpirationPolicyMetaData(int, ExpirationActionType, String[], ExpirationType[])
		 */
		protected static ExpirationPolicyMetaData from(ExpirationPolicy expirationPolicy) {

			return newExpirationPolicyMetaData(expirationPolicy.timeout(), expirationPolicy.action(),
				expirationPolicy.regionNames(), expirationPolicy.types());
		}

		/**
		 * Factory method to construct an instance of {@link ExpirationPolicyMetaData} using default expiration policy
		 * settings.
		 *
		 * @see #newExpirationPolicyMetaData(int, ExpirationActionType, String[], ExpirationType[])
		 */
		protected static ExpirationPolicyMetaData fromDefaults() {
			return newExpirationPolicyMetaData(DEFAULT_TIMEOUT, DEFAULT_ACTION, ALL_REGIONS, DEFAULT_EXPIRATION_TYPES);
		}

		/**
		 * Factory method used to construct a new instance of the {@link ExpirationAttributes} initialized with
		 * the given expiration timeout and action that is taken when an {@link Region} entry times out.
		 *
		 * @param timeout int value indicating the expiration timeout in seconds.
		 * @param action expiration action to take when the {@link Region} entry times out.
		 * @return a new instance of {@link ExpirationAttributes} initialized with the given expiration timeout
		 * and action.
		 * @see org.apache.geode.cache.ExpirationAttributes
		 * @see #newExpirationAttributes(int, ExpirationAction)
		 */
		protected static ExpirationAttributes newExpirationAttributes(int timeout, ExpirationActionType action) {
			return newExpirationAttributes(timeout, action.getExpirationAction());
		}

		/**
		 * Factory method used to construct a new instance of the {@link ExpirationAttributes} initialized with
		 * the given expiration timeout and action that is taken when an {@link Region} entry times out.
		 *
		 * @param timeout int value indicating the expiration timeout in seconds.
		 * @param action expiration action to take when the {@link Region} entry times out.
		 * @return a new instance of {@link ExpirationAttributes} initialized with the given expiration timeout
		 * and action.
		 * @see org.apache.geode.cache.ExpirationAttributes
		 */
		protected static ExpirationAttributes newExpirationAttributes(int timeout, ExpirationAction action) {
			return new ExpirationAttributes(timeout, action);
		}

		/**
		 * Factory method used to construct an instance of {@link ExpirationPolicyMetaData} initialized with
		 * the given expiration policy meta-data.
		 *
		 * @param timeout int value indicating the expiration timeout in seconds.
		 * @param action expiration action taken when the {@link Region} entry expires.
		 * @param regionNames names of {@link Region Regions} configured with the expiration policy meta-data.
		 * @param types type of expiration algorithm/behavior (TTI/TTL) configured for the {@link Region}.
		 * @return an instance of {@link ExpirationPolicyMetaData} initialized with the given expiration policy
		 * meta-data.
		 * @throws IllegalArgumentException if the {@link ExpirationType} array is empty.
		 * @see org.springframework.data.gemfire.config.annotation.EnableExpiration.ExpirationType
		 * @see ExpirationActionType
		 * @see #ExpirationPolicyMetaData(ExpirationAttributes, Set, Set)
		 * @see #newExpirationAttributes(int, ExpirationActionType)
		 */
		protected static ExpirationPolicyMetaData newExpirationPolicyMetaData(int timeout, ExpirationActionType action,
				String[] regionNames, ExpirationType[] types) {

			return new ExpirationPolicyMetaData(newExpirationAttributes(timeout, action),
				CollectionUtils.asSet(ArrayUtils.nullSafeArray(regionNames, String.class)),
				CollectionUtils.asSet(ArrayUtils.nullSafeArray(types, ExpirationType.class)));
		}

		/**
		 * Resolves the {@link ExpirationAction} used in the expiration policy.  Defaults to
		 * {@link ExpirationActionType#INVALIDATE} if {@code action} is {@literal null}.
		 *
		 * @param action given {@link ExpirationActionType} to evaluate.
		 * @return the resolved {@link ExpirationActionType} or the default if {@code action} is {@literal null}.
		 * @see ExpirationActionType
		 */
		protected static ExpirationActionType resolveAction(ExpirationActionType action) {
			return action != null ? action : DEFAULT_ACTION;
		}

		/**
		 * Resolves the expiration timeout used in the expiration policy.  Defaults to {@literal 0} if {@code timeout}
		 * is less than {@literal 0}.
		 *
		 * @param timeout int value expressing the expiration timeout in seconds.
		 * @return the resolved expiration policy timeout.
		 */
		protected static int resolveTimeout(int timeout) {
			return Math.max(timeout, DEFAULT_TIMEOUT);
		}

		private final ExpirationAttributes defaultExpirationAttributes;

		private final Set<String> regionNames = new HashSet<>();

		private final Set<ExpirationType> types = new HashSet<>();

		/**
		 * Constructs an instance of {@link ExpirationPolicyMetaData} initialized with the given expiration policy
		 * configuraiton meta-data and {@link Region} expiration settings.
		 *
		 * @param timeout int value indicating the expiration timeout in seconds.
		 * @param action expiration action taken when the {@link Region} entry expires.
		 * @param regionNames names of {@link Region Regions} configured with the expiration policy meta-data.
		 * @param types type of expiration algorithm/behavior (TTI/TTL) configured for the {@link Region}.
		 * @throws IllegalArgumentException if the {@link ExpirationType} {@link Set} is empty.
		 * @see org.springframework.data.gemfire.config.annotation.EnableExpiration.ExpirationType
		 * @see ExpirationActionType
		 * @see #ExpirationPolicyMetaData(ExpirationAttributes, Set, Set)
		 * @see #newExpirationAttributes(int, ExpirationActionType)
		 * @see #resolveAction(ExpirationActionType)
		 * @see #resolveTimeout(int)
		 */
		@SuppressWarnings("unused")
		protected ExpirationPolicyMetaData(int timeout, ExpirationActionType action, Set<String> regionNames,
				Set<ExpirationType> types) {

			this(newExpirationAttributes(resolveTimeout(timeout), resolveAction(action)), regionNames, types);
		}

		/**
		 * Constructs an instance of {@link ExpirationPolicyMetaData} initialized with the given expiration policy
		 * configuraiton meta-data and {@link Region} expiration settings.
		 *
		 * @param expirationAttributes {@link ExpirationAttributes} specifying the expiration timeout in seconds
		 * and expiration action taken when the {@link Region} entry expires.
		 * @param regionNames names of {@link Region Regions} configured with the expiration policy meta-data.
		 * @param types type of expiration algorithm/behaviors (TTI/TTL) configured for the {@link Region}.
		 * @throws IllegalArgumentException if the {@link ExpirationType} {@link Set} is empty.
		 * @see org.springframework.data.gemfire.config.annotation.EnableExpiration.ExpirationType
		 * @see org.apache.geode.cache.ExpirationAttributes
		 */
		protected ExpirationPolicyMetaData(ExpirationAttributes expirationAttributes, Set<String> regionNames,
				Set<ExpirationType> types) {

			Assert.notEmpty(types, "At least one ExpirationPolicy type [TTI, TTL] is required");

			this.defaultExpirationAttributes = expirationAttributes;
			this.regionNames.addAll(CollectionUtils.nullSafeSet(regionNames));
			this.types.addAll(CollectionUtils.nullSafeSet(types));
		}

		/**
		 * Determines whether the given {@link Object} (e.g. Spring bean) is accepted for Eviction policy configuration.
		 *
		 * @param regionBean {@link Object} being evaluated as an Eviction policy configuration candidate.
		 * @return a boolean value indicating whether the {@link Object} is accepted for Eviction policy configuration.
		 * @see #isRegionFactoryBean(Object)
		 * @see #resolveRegionName(Object)
		 * @see #accepts(Supplier)
		 */
		protected boolean accepts(Object regionBean) {
			return isRegionFactoryBean(regionBean) && accepts(() -> resolveRegionName(regionBean));
		}

		/**
		 * Determines whether the given {@link Region} is accepted for Eviction policy configuration.
		 *
		 * @param region {@link Region} being evaluated as a Eviction policy configuration candidate.
		 * @return a boolean value indicated whether the given {@link Region} is accepted as an Expiration policy
		 * configuration candidate.
		 * @see org.apache.geode.cache.Region
		 * @see #accepts(Supplier)
		 */
		protected boolean accepts(Region<?, ?> region) {
			return region != null && accepts(() -> region.getName());
		}

		/**
		 * Determines whether to apply this expiration policy to the given {@link Region} identified by name.
		 *
		 * @param regionName name of the {@link Region} to evaluate.
		 * @return a boolean value indicating whether the expiration policy applies to the given {@link Region}
		 * identified by name.
		 */
		protected boolean accepts(Supplier<String> regionName) {
			return this.regionNames.isEmpty() || this.regionNames.contains(regionName.get());
		}

		/**
		 * Determines whether Idle Timeout Expiration (TTI) was configured for this expiration policy.
		 *
		 * @return a boolean value indicating whether Idle Timeout Expiration (TTI) was configuration for
		 * this expiration policy.
		 */
		protected boolean isIdleTimeout() {
			return this.types.contains(ExpirationType.IDLE_TIMEOUT);
		}

		/**
		 * Determines whether Time-To-Live Expiration (TTL) was configured for this expiration policy.
		 *
		 * @return a boolean value indicating whether Time-To-Live Expiration (TTL) was configuration for
		 * this expiration policy.
		 */
		protected boolean isTimeToLive() {
			return this.types.contains(ExpirationType.TIME_TO_LIVE);
		}

		/**
		 * Resolves the name of a given {@link Region} from the corresponding {@link ResolvableRegionFactoryBean} object.
		 *
		 * @param regionFactoryBean {@link ResolvableRegionFactoryBean} from which to resolve the {@link Region} name.
		 * @return the resolved name of the {@link Region} created from the given {@link ResolvableRegionFactoryBean}.
		 * @see org.springframework.data.gemfire.ResolvableRegionFactoryBean#resolveRegionName()
		 */
		protected String resolveRegionName(Object regionFactoryBean) {

			return regionFactoryBean instanceof ResolvableRegionFactoryBean
				? ((ResolvableRegionFactoryBean<?, ?>) regionFactoryBean).resolveRegionName()
				: null;
		}

		/**
		 * Configures the Expiration policies on the targeted {@link ExpiringRegionFactoryBean}, which may be
		 * either a {@link PeerRegionFactoryBean} or {@link ClientRegionFactoryBean}.
		 *
		 * @param regionFactoryBean {@link ExpiringRegionFactoryBean} to configure.
		 * @return the given {@link ExpiringRegionFactoryBean}.
		 * @see org.springframework.data.gemfire.expiration.AnnotationBasedExpiration#forIdleTimeout(ExpirationAttributes)
		 * @see org.springframework.data.gemfire.expiration.AnnotationBasedExpiration#forTimeToLive(ExpirationAttributes)
		 * @see org.springframework.data.gemfire.expiration.ExpiringRegionFactoryBean
		 * @see #defaultExpirationAttributes()
		 */
		protected ExpiringRegionFactoryBean<?, ?> setExpirationAttributes(
				ExpiringRegionFactoryBean<?, ?> regionFactoryBean) {

			ExpirationAttributes defaultExpirationAttributes = defaultExpirationAttributes();

			if (isIdleTimeout()) {
				regionFactoryBean.setCustomEntryIdleTimeout(
					AnnotationBasedExpiration.forIdleTimeout(defaultExpirationAttributes));
			}

			if (isTimeToLive()) {
				regionFactoryBean.setCustomEntryTimeToLive(
					AnnotationBasedExpiration.forTimeToLive(defaultExpirationAttributes));
			}

			return regionFactoryBean;
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public Object configure(Object regionBean) {

			return accepts(regionBean)
				? setExpirationAttributes((ExpiringRegionFactoryBean<?, ?>) regionBean)
				: regionBean;
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public Region<?, ?> configure(Region<?, ?> region) {

			if (accepts(region)) {

				RegionAttributes<?, ?> regionAttributes = region.getAttributes();

				ExpirationAttributes expirationAttributes = defaultExpirationAttributes();

				AttributesMutator<?, ?> regionAttributesMutator = region.getAttributesMutator();

				if (SpringExtensions.areNotNull(regionAttributes, regionAttributesMutator)) {

					CustomExpiry<?, ?> customEntryIdleTimeout = regionAttributes.getCustomEntryIdleTimeout();
					CustomExpiry<?, ?> customEntryTimeToLive = regionAttributes.getCustomEntryTimeToLive();

					if (isIdleTimeout() && customEntryIdleTimeout == null) {
						regionAttributesMutator.setCustomEntryIdleTimeout(
							AnnotationBasedExpiration.forIdleTimeout(expirationAttributes));
					}

					if (isTimeToLive() && customEntryTimeToLive == null) {
						regionAttributesMutator.setCustomEntryTimeToLive(
							AnnotationBasedExpiration.forTimeToLive(expirationAttributes));
					}
				}
			}

			return region;
		}

		/**
		 * Returns the default {@link ExpirationAttributes}.
		 *
		 * @return an {@link ExpirationAttributes} containing the defaults.
		 * @see org.apache.geode.cache.ExpirationAttributes
		 */
		protected ExpirationAttributes defaultExpirationAttributes() {
			return this.defaultExpirationAttributes;
		}
	}
}
