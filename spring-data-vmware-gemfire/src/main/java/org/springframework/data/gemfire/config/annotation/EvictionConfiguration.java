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

package org.springframework.data.gemfire.config.annotation;

import static org.springframework.data.gemfire.config.annotation.EnableEviction.EvictionPolicy;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalStateException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import org.springframework.data.gemfire.gud.api.GudEvictionAttributes;
import org.springframework.data.gemfire.gud.api.GudObjectSizer;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudRegionAttributes;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.ResolvableRegionFactoryBean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.AbstractAnnotationConfigSupport;
import org.springframework.data.gemfire.eviction.EvictingRegionFactoryBean;
import org.springframework.data.gemfire.eviction.EvictionActionType;
import org.springframework.data.gemfire.eviction.EvictionAttributesFactoryBean;
import org.springframework.data.gemfire.eviction.EvictionPolicyType;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * The {@link EvictionConfiguration} class is a Spring {@link Configuration @Configuration} annotated class to enable
 * Eviction policy configuration on cache {@link GudRegion Regions}.
 *
 * @author John Blum
 * @see GudEvictionAttributes
 * @see GudRegion
 * @see GudObjectSizer
 * @see BeanPostProcessor
 * @see ApplicationContext
 * @see ApplicationContextAware
 * @see Bean
 * @see Configuration
 * @see ImportAware
 * @see ResolvableRegionFactoryBean
 * @see ClientRegionFactoryBean
 * @see AbstractAnnotationConfigSupport
 * @see EvictionActionType
 * @see EvictionAttributesFactoryBean
 * @see EvictionPolicyType
 * @since 1.9.0
 */
@Configuration
public class EvictionConfiguration extends AbstractAnnotationConfigSupport
		implements ApplicationContextAware, ImportAware {

	private ApplicationContext applicationContext;

	private EvictionPolicyConfigurer evictionPolicyConfigurer;

	/**
	 * Returns the {@link Annotation} {@link Class type} that enables and configures Eviction.
	 *
	 * @return the {@link Annotation} {@link Class type} to enable and configure Eviction.
	 * @see Annotation
	 * @see Class
	 */
	@Override
	protected @NonNull Class<? extends Annotation> getAnnotationType() {
		return EnableEviction.class;
	}

	/**
	 * Sets a reference to the Spring {@link ApplicationContext}.
	 *
	 * @param applicationContext Spring {@link ApplicationContext} in use.
	 * @throws BeansException if an error occurs while storing a reference to the Spring {@link ApplicationContext}.
	 * @see ApplicationContextAware#setApplicationContext(ApplicationContext)
	 * @see ApplicationContext
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setImportMetadata(@NonNull AnnotationMetadata importMetadata) {

		if (isAnnotationPresent(importMetadata)) {

			AnnotationAttributes enableEvictionAttributes = getAnnotationAttributes(importMetadata);

			AnnotationAttributes[] policies = enableEvictionAttributes.getAnnotationArray("policies");

			for (AnnotationAttributes evictionPolicyAttributes :
					ArrayUtils.nullSafeArray(policies, AnnotationAttributes.class)) {

				this.evictionPolicyConfigurer =
					ComposableEvictionPolicyConfigurer.compose(this.evictionPolicyConfigurer,
						EvictionPolicyMetaData.from(evictionPolicyAttributes, this.applicationContext));
			}

			this.evictionPolicyConfigurer = Optional.ofNullable(this.evictionPolicyConfigurer)
				.orElseGet(EvictionPolicyMetaData::fromDefaults);
		}
	}

	/**
	 * Determines whether the Spring bean is an instance of {@link EvictingRegionFactoryBean}.
	 *
	 * @param bean Spring bean to evaluate.
	 * @return a boolean value indicating whether the Spring bean is an instance of {@link EvictingRegionFactoryBean}.
	 * @see EvictingRegionFactoryBean
	 * @see ClientRegionFactoryBean
	 */
	protected static boolean isRegionFactoryBean(Object bean) {
		return bean instanceof EvictingRegionFactoryBean;
	}

	/**
	 * Returns a reference to the configured {@link EvictionPolicyConfigurer} used to configure the Eviction policy
	 * of a {@link GudRegion}.
	 *
	 * @return a reference to the configured {@link EvictionPolicyConfigurer}.
	 * @see EvictionPolicyConfigurer
	 */
	protected EvictionPolicyConfigurer getEvictionPolicyConfigurer() {

		return Optional.ofNullable(this.evictionPolicyConfigurer).orElseThrow(() ->
			newIllegalStateException("EvictionPolicyConfigurer was not properly configured and initialized"));
	}

	@Bean
	@SuppressWarnings("unused")
	public BeanPostProcessor evictionBeanPostProcessor() {

		return new BeanPostProcessor() {

			@Override
			public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
				return isRegionFactoryBean(bean) ? getEvictionPolicyConfigurer().configure(bean) : bean;
			}
		};
	}

	@SuppressWarnings("unused")
	@EventListener(ContextRefreshedEvent.class)
	public void evictionContextRefreshedListener(@NonNull ContextRefreshedEvent event) {

		ApplicationContext applicationContext = event.getApplicationContext();

		for (GudRegion<?, ?> region : applicationContext.getBeansOfType(GudRegion.class).values()) {
			getEvictionPolicyConfigurer().configure(region);
		}
	}

	/**
	 * {@link EvictionPolicyConfigurer} configures the Eviction policy of an Apache Geode {@link GudRegion}.
	 *
	 * @see FunctionalInterface
	 */
	@FunctionalInterface
	protected interface EvictionPolicyConfigurer {

		/**
		 * Configure the Eviction policy on the given SDG {@link ClientRegionFactoryBean}
		 * or {@link ClientRegionFactoryBean} used to create an Apache Geode {@link GudRegion}.
		 *
		 * @param regionBean {@link ClientRegionFactoryBean} or {@link ClientRegionFactoryBean} used to create
		 * an Apache Geode {@link GudRegion}.
		 * @return the given {@code regionFactoryBean}.
		 * @see ClientRegionFactoryBean
		 * @see ClientRegionFactoryBean
		 */
		Object configure(Object regionBean);

		/**
		 * Configures the Eviction policy of the given Apache Geode {@link GudRegion}.
		 *
		 * @param region {@link GudRegion} on which to configure the Eviction policy.
		 * @return the given {@link GudRegion}.
		 * @see GudRegion
		 */
		default GudRegion<?, ?> configure(GudRegion<?, ?> region) {
			return region;
		}
	}

	/**
	 * {@link ComposableEvictionPolicyConfigurer} is a {@link EvictionPolicyConfigurer} implementation that composes
	 * multiple {@link EvictionPolicyConfigurer} objects into a composition using the Composite Software Design Pattern
	 * making the composition appear as a single {@link EvictionPolicyConfigurer}.
	 *
	 * @see EvictionPolicyConfigurer
	 */
	protected static class ComposableEvictionPolicyConfigurer implements EvictionPolicyConfigurer {

		/**
		 * Composes the array of {@link EvictionPolicyConfigurer} objects into a single
		 * {@link EvictionPolicyConfigurer} implementation using the Composite Software Design Pattern.
		 *
		 * @param array array of {@link EvictionPolicyConfigurer} objects to compose.
		 * @return an {@link EvictionPolicyConfigurer} implementation composed from the array
		 * of {@link EvictionPolicyConfigurer} objects.
		 * @see EvictionPolicyConfigurer
		 * @see #compose(Iterable)
		 */
		@SuppressWarnings("unused")
		protected static @Nullable EvictionPolicyConfigurer compose(EvictionPolicyConfigurer[] array) {
			return compose(Arrays.asList(ArrayUtils.nullSafeArray(array, EvictionPolicyConfigurer.class)));
		}

		/**
		 * Composes the {@link Iterable} of {@link EvictionPolicyConfigurer} objects into a single
		 * {@link EvictionPolicyConfigurer} implementation using the Composite Software Design Pattern.
		 *
		 * @param iterable {@link Iterable} of {@link EvictionPolicyConfigurer} objects to compose.
		 * @return an {@link EvictionPolicyConfigurer} implementation composed from the {@link Iterable}
		 * of {@link EvictionPolicyConfigurer} objects.
		 * @see EvictionPolicyConfigurer
		 * @see #compose(EvictionPolicyConfigurer, EvictionPolicyConfigurer)
		 */
		protected static @Nullable EvictionPolicyConfigurer compose(Iterable<EvictionPolicyConfigurer> iterable) {

			EvictionPolicyConfigurer current = null;

			for (EvictionPolicyConfigurer evictionPolicyConfigurer : CollectionUtils.nullSafeIterable(iterable)) {
				current = compose(current, evictionPolicyConfigurer);
			}

			return current;
		}

		/**
		 * Composes two {@link EvictionPolicyConfigurer} objects into a composition object
		 * implementing the {@link EvictionPolicyConfigurer} interface.
		 *
		 * @param one first {@link EvictionPolicyConfigurer} object to compose.
		 * @param two second {@link EvictionPolicyConfigurer} object to compose.
		 * @return an {@link EvictionPolicyConfigurer} object implementation composed of
		 * multiple {@link EvictionPolicyConfigurer} objects using the Composite Software Design Pattern.
		 */
		protected static @Nullable EvictionPolicyConfigurer compose(@Nullable EvictionPolicyConfigurer one,
				@Nullable EvictionPolicyConfigurer two) {

			return one == null ? two
				: two == null ? one
				: new ComposableEvictionPolicyConfigurer(one, two);
		}

		private final EvictionPolicyConfigurer one;
		private final EvictionPolicyConfigurer two;

		/**
		 * Constructs a new instance of the {@link ComposableEvictionPolicyConfigurer} initialized with the two
		 * {@link EvictionPolicyConfigurer} objects.
		 *
		 * @param one first {@link EvictionPolicyConfigurer} object to compose.
		 * @param two second {@link EvictionPolicyConfigurer} object to compose.
		 */
		private ComposableEvictionPolicyConfigurer(EvictionPolicyConfigurer one, EvictionPolicyConfigurer two) {

			this.one = one;
			this.two = two;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object configure(Object regionBean) {
			return this.two.configure(this.one.configure(regionBean));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GudRegion<?, ?> configure(GudRegion<?, ?> region) {
			return this.two.configure(this.one.configure(region));
		}
	}

	@SuppressWarnings("unused")
	protected static class EvictionPolicyMetaData implements EvictionPolicyConfigurer {

		protected static final String[] ALL_REGIONS = new String[0];

		protected static EvictionPolicyMetaData from(@NonNull AnnotationAttributes evictionPolicyAttributes,
				@NonNull ApplicationContext applicationContext) {

			Assert.isAssignable(EvictionPolicy.class, evictionPolicyAttributes.annotationType());

			return from(evictionPolicyAttributes.getEnum("type"),
				(Integer) evictionPolicyAttributes.get("maximum"),
				evictionPolicyAttributes.getEnum("action"),
				resolveObjectSizer(evictionPolicyAttributes.getString("objectSizerName"), applicationContext),
				evictionPolicyAttributes.getStringArray("regionNames"));
		}

		protected static EvictionPolicyMetaData from(EvictionPolicy evictionPolicy,
				ApplicationContext applicationContext) {

			return from(evictionPolicy.type(), evictionPolicy.maximum(), evictionPolicy.action(),
				resolveObjectSizer(evictionPolicy.objectSizerName(), applicationContext), evictionPolicy.regionNames());
		}

		protected static EvictionPolicyMetaData from(EvictionPolicyType type, int maximum, EvictionActionType action,
				GudObjectSizer objectSizer, String... regionNames) {

			EvictionAttributesFactoryBean factoryBean = new EvictionAttributesFactoryBean();

			factoryBean.setAction(action.getEvictionAction());
			factoryBean.setObjectSizer(objectSizer);
			factoryBean.setThreshold(resolveThreshold(maximum, type));
			factoryBean.setType(type);
			factoryBean.afterPropertiesSet();

			return new EvictionPolicyMetaData(factoryBean.getObject(), regionNames);
		}

		protected static EvictionPolicyMetaData fromDefaults() {
			return new EvictionPolicyMetaData(createDefaultEvictionAttributes());
		}

		private static GudEvictionAttributes createDefaultEvictionAttributes() {
			EvictionAttributesFactoryBean factoryBean = new EvictionAttributesFactoryBean();
			factoryBean.setType(EvictionPolicyType.ENTRY_COUNT);
			factoryBean.setThreshold(GudEvictionAttributes.DEFAULT_ENTRIES_MAXIMUM);
			factoryBean.afterPropertiesSet();
			return factoryBean.getObject();
		}

		protected static GudObjectSizer resolveObjectSizer(String objectSizerName, ApplicationContext applicationContext) {

			boolean resolvable = StringUtils.hasText(objectSizerName)
				&& applicationContext.containsBean(objectSizerName);

			return resolvable ? applicationContext.getBean(objectSizerName, GudObjectSizer.class) : null;
		}

		/**
		 * Resolves the Eviction policy threshold (a.k.a. maximum) based on the {@link EvictionPolicyType}.
		 *
		 * For instance {@link EvictionPolicyType#HEAP_PERCENTAGE} does not support maximum/threshold since
		 * the settings are determined by the GemFire/Geode cache critical heap percentage and eviction heap percentage
		 * System property settings.
		 *
		 * @param maximum integer value specifying the configured Eviction threshold.
		 * @param type {@link EvictionPolicyType} specifying the type of Eviction algorithm.
		 * @return a resolved value for the Eviction maximum/threshold.
		 * @see EvictionPolicyType
		 */
		protected static Integer resolveThreshold(int maximum, EvictionPolicyType type) {
			return EvictionPolicyType.HEAP_PERCENTAGE.equals(type) ? null : maximum;
		}

		private final GudEvictionAttributes evictionAttributes;

		private final Set<String> regionNames = new HashSet<>();

		/**
		 * Constructs an instance of {@link EvictionPolicyMetaData} initialized with the given
		 * {@link GudEvictionAttributes} applying to all {@link GudRegion Regions}.
		 *
		 * @param evictionAttributes {@link GudEvictionAttributes} specifying the Eviction policy configuration
		 * for a {@link GudRegion}.
		 * @see GudEvictionAttributes
		 * @see #EvictionPolicyMetaData(GudEvictionAttributes, String[])
		 */
		protected EvictionPolicyMetaData(GudEvictionAttributes evictionAttributes) {
			this(evictionAttributes, ALL_REGIONS);
		}

		/**
		 * Constructs an instance of {@link EvictionPolicyMetaData} initialized with the given
		 * {@link GudEvictionAttributes} to apply to the specific {@link GudRegion Regions}.
		 *
		 * @param evictionAttributes {@link GudEvictionAttributes} specifying the Eviction policy configuration
		 * for a {@link GudRegion}.
		 * @param regionNames names of {@link GudRegion Regions} on which the Eviction policy is applied.
		 * @see GudEvictionAttributes
		 */
		protected EvictionPolicyMetaData(GudEvictionAttributes evictionAttributes, String[] regionNames) {

			Assert.notNull(evictionAttributes, "EvictionAttributes must not be null");

			this.evictionAttributes = evictionAttributes;

			Collections.addAll(this.regionNames, ArrayUtils.nullSafeArray(regionNames, String.class));
		}

		/**
		 * Determines whether the given {@link Object} (e.g. Spring bean) is accepted for Eviction policy configuration.
		 *
		 * @param regionFactoryBean {@link Object} being evaluated as an Eviction policy configuration candidate.
		 * @return a boolean value indicating whether the {@link Object} is accepted for Eviction policy configuration.
		 * @see #isRegionFactoryBean(Object)
		 * @see #resolveRegionName(Object)
		 * @see #accepts(Supplier)
		 */
		protected boolean accepts(@Nullable Object regionFactoryBean) {
			return isRegionFactoryBean(regionFactoryBean) && accepts(() -> resolveRegionName(regionFactoryBean));
		}

		/**
		 * Determines whether the given {@link GudRegion} is accepted for Eviction policy configuration.
		 *
		 * @param region {@link GudRegion} evaluated for Eviction policy configuration.
		 * @return a boolean value indicating whether the given {@link GudRegion} is accepted for
		 * Eviction policy configuration.
		 * @see GudRegion
		 * @see #accepts(Supplier)
		 */
		protected boolean accepts(@Nullable GudRegion<?, ?> region) {
			return region != null && accepts(() -> region.getName());
		}

		/**
		 * Determine whether the {@link GudRegion} identified by name is accepted for Eviction policy configuration.
		 *
		 * @param regionName name of the {@link GudRegion} targeted for Eviction policy configuration.
		 * @return a boolean value if the named {@link GudRegion} is accepted for Eviction policy configuration.
		 */
		protected boolean accepts(Supplier<String> regionName) {
			return this.regionNames.isEmpty() || this.regionNames.contains(regionName.get());
		}

		/**
		 * Resolves the name of a given {@link GudRegion} from the corresponding {@link ResolvableRegionFactoryBean} object.
		 *
		 * @param regionFactoryBean {@link ResolvableRegionFactoryBean} from which to resolve the {@link GudRegion} name.
		 * @return the resolved name of the {@link GudRegion} created from the given {@link ResolvableRegionFactoryBean}.
		 * @see ResolvableRegionFactoryBean#resolveRegionName()
		 */
		protected String resolveRegionName(Object regionFactoryBean) {

			return regionFactoryBean instanceof ResolvableRegionFactoryBean
				? ((ResolvableRegionFactoryBean<?, ?>) regionFactoryBean).resolveRegionName()
				: null;
		}

		/**
		 * Sets the {@link GudEvictionAttributes} on the {@link ClientRegionFactoryBean} or {@link ClientRegionFactoryBean}
		 * used to create the targeted {@link GudRegion}.
		 *
		 * @param regionFactoryBean {@link ClientRegionFactoryBean} or {@link ClientRegionFactoryBean} on which to
		 * set the {@link GudEvictionAttributes} encapsulating the Eviction policy for the targeted {@link GudRegion}.
		 * @return the {@code regionFactoryBean}.
		 * @see EvictingRegionFactoryBean#setEvictionAttributes(GudEvictionAttributes)
		 * @see GudEvictionAttributes
		 * @see #getEvictionAttributes()
		 */
		protected EvictingRegionFactoryBean setEvictionAttributes(EvictingRegionFactoryBean regionFactoryBean) {

			regionFactoryBean.setEvictionAttributes(getEvictionAttributes());

			return regionFactoryBean;
		}

		/**
		 * Returns an instance of the {@link GudEvictionAttributes} specifying the Eviction policy configuration
		 * captured in this Eviction policy meta-data.
		 *
		 * @return an instance of the {@link GudEvictionAttributes} specifying the {@link GudRegion}
		 * Eviction policy configuration.
		 * @throws IllegalStateException if the {@link GudEvictionAttributes} were not properly initialized.
		 * @see GudEvictionAttributes
		 */
		protected GudEvictionAttributes getEvictionAttributes() {

			return Optional.ofNullable(this.evictionAttributes).orElseThrow(() ->
				newIllegalStateException("EvictionAttributes was not properly configured and initialized"));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object configure(Object regionBean) {

			return accepts(regionBean)
				? setEvictionAttributes((EvictingRegionFactoryBean) regionBean)
				: regionBean;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GudRegion<?, ?> configure(GudRegion<?, ?> region) {

			Optional.ofNullable(region)
				.filter(this::accepts)
				.filter(this::isDefaultEvictionEntryMaximum)
				.map(GudRegion::getAttributesMutator)
				.ifPresent(attributesMutator ->
					attributesMutator.setEvictionMaximum(getEvictionAttributes().getMaximum()));

			return region;
		}

		private boolean isDefaultEvictionEntryMaximum(GudRegion<?, ?> region) {
			return region != null && isDefaultEvictionEntryMaximum(region.getAttributes());
		}

		private boolean isDefaultEvictionEntryMaximum(GudRegionAttributes<?, ?> regionAttributes) {
			return regionAttributes != null && isDefaultEvictionEntryMaximum(regionAttributes.getEvictionAttributes());
		}

		private boolean isDefaultEvictionEntryMaximum(GudEvictionAttributes evictionAttributes) {
			return GudEvictionAttributes.DEFAULT_ENTRIES_MAXIMUM == evictionAttributes.getMaximum();
		}
	}
}
