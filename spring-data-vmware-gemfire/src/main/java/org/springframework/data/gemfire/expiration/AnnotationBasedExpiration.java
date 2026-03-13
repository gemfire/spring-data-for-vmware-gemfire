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

package org.springframework.data.gemfire.expiration;

import java.lang.annotation.Annotation;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.EnvironmentAccessor;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.gemfire.gud.api.GudCustomExpiry;
import org.springframework.data.gemfire.gud.api.GudExpirationAction;
import org.springframework.data.gemfire.gud.api.GudExpirationAttributes;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.springframework.expression.spel.support.StandardTypeLocator;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * The {@link AnnotationBasedExpiration} class is an implementation of the {@link GudCustomExpiry} interface
 * that determines the Time-To-Live (TTL) or Idle-Timeout (TTI) expiration policy of a {@link GudRegion} entry
 * by introspecting the {@link GudRegion} entry's class type and reflecting on any {@link GudRegion} entries annotated
 * with SDG's Expiration-based Annotations.
 *
 * @author John Blum
 * @see Annotation
 * @see BeanFactory
 * @see BeanFactoryAware
 * @see ExpirationActionType
 * @see Expiration
 * @see IdleTimeoutExpiration
 * @see TimeToLiveExpiration
 * @see GudCustomExpiry
 * @see GudExpirationAction
 * @see GudExpirationAttributes
 * @see GudRegion
 * @since 1.7.0
 */
@SuppressWarnings("unused")
public class AnnotationBasedExpiration<K, V> implements BeanFactoryAware, GudCustomExpiry<K, V> {

	protected static final AtomicReference<BeanFactory> BEAN_FACTORY_REFERENCE =
		new AtomicReference<>(null);

	protected static final AtomicReference<StandardEvaluationContext> EVALUATION_CONTEXT_REFERENCE
		= new AtomicReference<>(null);

	private GudExpirationAttributes defaultExpirationAttributes;

	/**
	 * Constructs a new instance of the AnnotationBasedExpiration class with no default expiration policy.
	 */
	public AnnotationBasedExpiration() {
		this(null);
	}

	/**
	 * Constructs a new instance of {@link AnnotationBasedExpiration} initialized with a specific, default
	 * expiration policy.
	 *
	 * @param defaultExpirationAttributes expiration settings used as the default expiration policy.
	 * @see GudExpirationAttributes
	 */
	public AnnotationBasedExpiration(GudExpirationAttributes defaultExpirationAttributes) {
		this.defaultExpirationAttributes = defaultExpirationAttributes;
	}

	/**
	 * Factory method used to construct an instance of {@link AnnotationBasedExpiration} having no default
	 * {@link GudExpirationAttributes} to process expired annotated {@link GudRegion} entries
	 * using Idle Timeout (TTI) Expiration.
	 *
	 * @param <K> {@link Class} type of the {@link GudRegion} entry key.
	 * @param <V> {@link Class} type of the {@link GudRegion} entry value.
	 * @return an {@link AnnotationBasedExpiration} instance to process expired annotated {@link GudRegion} entries
	 * using Idle Timeout expiration.
	 * @see AnnotationBasedExpiration
	 * @see IdleTimeoutExpiration
	 * @see #forIdleTimeout(GudExpirationAttributes)
	 */
	public static <K, V> AnnotationBasedExpiration<K, V> forIdleTimeout() {
		return forIdleTimeout(null);
	}

	/**
	 * Factory method used to construct an instance of {@link AnnotationBasedExpiration} initialized with
	 * default {@link GudExpirationAttributes} to process expired annotated {@link GudRegion} entries
	 * using Idle Timeout (TTI) expiration.
	 *
	 * @param <K> {@link Class} type of the {@link GudRegion} entry key.
	 * @param <V> {@link Class} type of the {@link GudRegion} entry value.
	 * @param defaultExpirationAttributes {@link GudExpirationAttributes} used by default if no expiration policy
	 * was specified on the {@link GudRegion}.
	 * @return an {@link AnnotationBasedExpiration} instance to process expired annotated {@link GudRegion} entries
	 * using Idle Timeout expiration.
	 * @see AnnotationBasedExpiration
	 * @see IdleTimeoutExpiration
	 * @see #AnnotationBasedExpiration(GudExpirationAttributes)
	 */
	public static <K, V> AnnotationBasedExpiration<K, V> forIdleTimeout(GudExpirationAttributes defaultExpirationAttributes) {

		return new AnnotationBasedExpiration<K, V>(defaultExpirationAttributes) {

			@Override
			protected ExpirationMetaData getExpirationMetaData(GudRegion.Entry<K, V> entry) {

				return isIdleTimeoutConfigured(entry)
					? ExpirationMetaData.from(getIdleTimeout(entry))
					: super.getExpirationMetaData(entry);
			}
		};
	}

	/**
	 * Factory method used to construct an instance of {@link AnnotationBasedExpiration} having no default
	 * {@link GudExpirationAttributes} to process expired annotated {@link GudRegion} entries
	 * using Time-To-Live (TTL) Expiration.
	 *
	 * @param <K> {@link Class} type of the {@link GudRegion} entry key.
	 * @param <V> {@link Class} type of the {@link GudRegion} entry value.
	 * @return an {@link AnnotationBasedExpiration} instance to process expired annotated {@link GudRegion} entries
	 * using Time-To-Live expiration.
	 * @see AnnotationBasedExpiration
	 * @see TimeToLiveExpiration
	 * @see #forTimeToLive(GudExpirationAttributes)
	 */
	public static <K, V> AnnotationBasedExpiration<K, V> forTimeToLive() {
		return forTimeToLive(null);
	}

	/**
	 * Factory method used to construct an instance of {@link AnnotationBasedExpiration} initialized with
	 * default {@link GudExpirationAttributes} to process expired annotated {@link GudRegion} entries
	 * using Time-To-Live (TTL) expiration.
	 *
	 * @param <K> {@link Class} type of the {@link GudRegion} entry key.
	 * @param <V> {@link Class} type of the {@link GudRegion} entry value.
	 * @param defaultExpirationAttributes {@link GudExpirationAttributes} used by default if no expiration policy
	 * was specified on the {@link GudRegion}.
	 * @return an {@link AnnotationBasedExpiration} instance to process expired annotated {@link GudRegion} entries
	 * using Time-To-Live expiration.
	 * @see AnnotationBasedExpiration
	 * @see TimeToLiveExpiration
	 * @see #AnnotationBasedExpiration(GudExpirationAttributes)
	 */
	public static <K, V> AnnotationBasedExpiration<K, V> forTimeToLive(GudExpirationAttributes defaultExpirationAttributes) {

		return new AnnotationBasedExpiration<K, V>(defaultExpirationAttributes) {

			@Override
			protected ExpirationMetaData getExpirationMetaData(GudRegion.Entry<K, V> entry) {

				return isTimeToLiveConfigured(entry)
					? ExpirationMetaData.from(getTimeToLive(entry))
					: super.getExpirationMetaData(entry);
			}
		};
	}

	/**
	 * Initializes the Spring Expression Language (SpEL) {@link EvaluationContext} used to parse property placeholder
	 * and SpEL expressions in the Expiration annotation attribute values.
	 */
	protected void initEvaluationContext() {

		BeanFactory beanFactory = getBeanFactory();

		if (EVALUATION_CONTEXT_REFERENCE.compareAndSet(null, newEvaluationContext())) {

			StandardEvaluationContext evaluationContext = EVALUATION_CONTEXT_REFERENCE.get();

			evaluationContext.addPropertyAccessor(new BeanFactoryAccessor());
			evaluationContext.addPropertyAccessor(new EnvironmentAccessor());
			evaluationContext.addPropertyAccessor(new MapAccessor());

			if (beanFactory instanceof ConfigurableBeanFactory) {

				ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;

				ConversionService conversionService = configurableBeanFactory.getConversionService();

				if (conversionService != null) {
					evaluationContext.setTypeConverter(new StandardTypeConverter(conversionService));
				}

				evaluationContext.setTypeLocator(new StandardTypeLocator(configurableBeanFactory.getBeanClassLoader()));
			}
		}

		EVALUATION_CONTEXT_REFERENCE.get().setBeanResolver(new BeanFactoryResolver(beanFactory));
	}

	StandardEvaluationContext newEvaluationContext() {
		return new StandardEvaluationContext();
	}

	/**
	 * Sets the {@link BeanFactory} managing this {@link AnnotationBasedExpiration} bean in the Spring context.
	 *
	 * @param beanFactory the Spring {@link BeanFactory} to which this bean belongs.
	 * @throws BeansException if the {@link BeanFactory} reference cannot be initialized.
	 * @see BeanFactory
	 * @see #initEvaluationContext()
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		BEAN_FACTORY_REFERENCE.set(beanFactory);
		initEvaluationContext();
	}

	/**
	 * Gets a reference to the Spring {@link BeanFactory} in which this {@link AnnotationBasedExpiration} bean
	 * is managed.
	 *
	 * @return a reference to the Spring {@link BeanFactory}.
	 * @throws IllegalStateException if the {@link BeanFactory} reference was not properly initialized.
	 * @see BeanFactory
	 */
	protected BeanFactory getBeanFactory() {
		BeanFactory localBeanFactory = BEAN_FACTORY_REFERENCE.get();
		Assert.state(localBeanFactory != null, "beanFactory was not properly initialized");
		return localBeanFactory;
	}

	/**
	 * Sets the expiration policy to use by default when no application domain object specific expiration meta-data
	 * has been specified.
	 *
	 * @param defaultExpirationAttributes expiration settings used as the default expiration policy.
	 * @see #getDefaultExpirationAttributes()
	 * @see GudExpirationAttributes
	 */
	public void setDefaultExpirationAttributes(GudExpirationAttributes defaultExpirationAttributes) {
		this.defaultExpirationAttributes = defaultExpirationAttributes;
	}

	/**
	 * Gets the expiration policy used by default when no application domain object specific expiration meta-data
	 * has been specified.
	 *
	 * @return an instance of GudExpirationAttributes with expiration settings defining the default expiration policy.
	 * @see #setDefaultExpirationAttributes(GudExpirationAttributes)
	 * @see GudExpirationAttributes
	 */
	protected GudExpirationAttributes getDefaultExpirationAttributes() {
		return this.defaultExpirationAttributes;
	}

	/**
	 * Calculate the expiration for a given entry. Returning {@literal null} indicates that the default
	 * for the {@link GudRegion} should be used. The entry parameter should not be used after this method
	 * invocation completes.
	 *
	 * @param entry the entry used to determine the appropriate expiration policy.
	 * @return the expiration configuration to be used or {@literal null} if the Region's defaults should be used.
	 * @see GudExpirationAttributes
	 * @see GudRegion
	 * @see #getExpirationMetaData(GudRegion.Entry)
	 * @see #newExpirationAttributes(ExpirationMetaData)
	 */
	@Override
	public GudExpirationAttributes getExpiry(GudRegion.Entry<K, V> entry) {
		return newExpirationAttributes(getExpirationMetaData(entry));
	}

	/**
	 * Gets custom expiration (Annotation-based) policy meta-data for the given {@link GudRegion} entry.
	 *
	 * @param entry {@link GudRegion} entry used as the source of the expiration policy meta-data.
	 * @return {@link ExpirationMetaData} extracted from the {@link GudRegion} entry or {@literal null}
	 * if the expiration policy meta-data could not be determined from the {@link GudRegion} entry.
	 * @see ExpirationMetaData
	 */
	protected ExpirationMetaData getExpirationMetaData(GudRegion.Entry<K, V> entry) {
		return isExpirationConfigured(entry) ? ExpirationMetaData.from(getExpiration(entry)) : null;
	}

	/**
	 * Constructs a new instance of {@link GudExpirationAttributes} configured with the application domain object
	 * specific expiration policy.  If the application domain object type has not been annotated with
	 * custom expiration meta-data, then the default expiration settings are used.
	 *
	 * @param expirationMetaData application domain object specific expiration policy meta-data used to construct
	 * the {@link GudExpirationAttributes}.
	 * @return custom {@link GudExpirationAttributes} configured from the application domain object specific
	 * expiration policy or the default expiration settings if the application domain object has not been
	 * annotated with custom expiration meta-data.
	 * @see ExpirationMetaData
	 * @see GudExpirationAttributes
	 * @see #getDefaultExpirationAttributes()
	 */
	protected GudExpirationAttributes newExpirationAttributes(ExpirationMetaData expirationMetaData) {

		return expirationMetaData != null
			? expirationMetaData.toExpirationAttributes()
			: getDefaultExpirationAttributes();
	}

	/**
	 * Determines whether the Region Entry has been annotated with the Expiration Annotation.
	 *
	 * @param entry the GudRegion.Entry to evaluate for the presence of the Expiration Annotation.
	 * @return a boolean value indicating whether the Region Entry has been annotated with @Expiration.
	 * @see Expiration
	 * @see #isAnnotationPresent(Object, Class)
	 */
	protected boolean isExpirationConfigured(GudRegion.Entry<K, V> entry) {
		return entry != null && isExpirationConfigured(entry.getValue());
	}

	private boolean isExpirationConfigured(Object obj) {
		return isAnnotationPresent(obj, Expiration.class);
	}

	/**
	 * Gets the Expiration Annotation meta-data from the Region Entry.
	 *
	 * @param entry the GudRegion.Entry from which to extract the Expiration Annotation meta-data.
	 * @return the Expiration Annotation meta-data for the given Region Entry or {@code null}
	 * if the Region Entry has not been annotated with @Expiration.
	 * @see Expiration
	 * @see #getAnnotation(Object, Class)
	 */
	protected Expiration getExpiration(GudRegion.Entry<K, V> entry) {
		return getExpiration(entry.getValue());
	}

	private Expiration getExpiration(Object obj) {
		return getAnnotation(obj, Expiration.class);
	}

	/**
	 * Determines whether the Region Entry has been annotated with the IdleTimeoutExpiration Annotation.
	 *
	 * @param entry the GudRegion.Entry to evaluate for the presence of the IdleTimeoutExpiration Annotation.
	 * @return a boolean value indicating whether the Region Entry has been annotated with @IdleTimeoutExpiration.
	 * @see IdleTimeoutExpiration
	 * @see #isAnnotationPresent(Object, Class)
	 */
	protected boolean isIdleTimeoutConfigured(GudRegion.Entry<K, V> entry) {
		return entry != null && isIdleTimeoutConfigured(entry.getValue());
	}

	private boolean isIdleTimeoutConfigured(Object obj) {
		return isAnnotationPresent(obj, IdleTimeoutExpiration.class);
	}

	/**
	 * Gets the IdleTimeoutExpiration Annotation meta-data from the Region Entry.
	 *
	 * @param entry the GudRegion.Entry from which to extract the IdleTimeoutExpiration Annotation meta-data.
	 * @return the IdleTimeoutExpiration Annotation meta-data for the given Region Entry or {@code null}
	 * if the Region Entry has not been annotated with @IdleTimeoutExpiration.
	 * @see IdleTimeoutExpiration
	 * @see #getAnnotation(Object, Class)
	 */
	protected IdleTimeoutExpiration getIdleTimeout(GudRegion.Entry<K, V> entry) {
		return getIdleTimeout(entry.getValue());
	}

	private IdleTimeoutExpiration getIdleTimeout(Object obj) {
		return getAnnotation(obj, IdleTimeoutExpiration.class);
	}

	/**
	 * Determines whether the Region Entry has been annotated with the TimeToLiveExpiration Annotation.
	 *
	 * @param entry the GudRegion.Entry to evaluate for the presence of the TimeToLiveExpiration Annotation.
	 * @return a boolean value indicating whether the Region Entry has been annotated with @TimeToLiveExpiration.
	 * @see TimeToLiveExpiration
	 * @see #isAnnotationPresent(Object, Class)
	 */
	protected boolean isTimeToLiveConfigured(GudRegion.Entry<K, V> entry) {
		return entry != null && isTimeToLiveConfigured(entry.getValue());
	}

	private boolean isTimeToLiveConfigured(Object value) {
		return isAnnotationPresent(value, TimeToLiveExpiration.class);
	}

	/**
	 * Gets the TimeToLiveExpiration Annotation meta-data from the Region Entry.
	 *
	 * @param entry the GudRegion.Entry from which to extract the TimeToLiveExpiration Annotation meta-data.
	 * @return the TimeToLiveExpiration Annotation meta-data for the given Region Entry or {@code null}
	 * if the Region Entry has not been annotated with @TimeToLiveExpiration.
	 * @see TimeToLiveExpiration
	 * @see #getAnnotation(Object, Class)
	 */
	protected TimeToLiveExpiration getTimeToLive(GudRegion.Entry<K, V> entry) {
		return getTimeToLive(entry.getValue());
	}

	private TimeToLiveExpiration getTimeToLive(Object obj) {
		return getAnnotation(obj, TimeToLiveExpiration.class);
	}

	private <T extends Annotation> boolean isAnnotationPresent(Object obj, Class<T> annotationType) {
		return (obj != null && obj.getClass().isAnnotationPresent(annotationType));
	}

	private <T extends Annotation> T getAnnotation(Object obj, Class<T> annotationType) {
		return AnnotationUtils.getAnnotation(obj.getClass(), annotationType);
	}

	/**
	 * Called when the Region containing this callback is closed or destroyed, when the Cache is closed,
	 * or when a callback is removed from a Region using an AttributesMutator.
	 */
	@Override
	public void close() { }

	/**
	 * The ExpirationMetaData class encapsulates the settings constituting the expiration policy including
	 * the expiration timeout and the action performed when expiration occurs.
	 *
	 * @see GudExpirationAttributes
	 */
	protected static class ExpirationMetaData {

		private static final ExpirationActionConverter EXPIRATION_ACTION_CONVERTER = new ExpirationActionConverter();

		private final int timeout;

		private final ExpirationActionType action;

		protected ExpirationMetaData(int timeout, ExpirationActionType action) {
			this.timeout = timeout;
			this.action = action;
		}

		protected static ExpirationMetaData from(GudExpirationAttributes expirationAttributes) {
			return new ExpirationMetaData(expirationAttributes.getTimeout(), ExpirationActionType.valueOf(
				expirationAttributes.getAction()));
		}

		protected static ExpirationMetaData from(Expiration expiration) {
			return new ExpirationMetaData(parseTimeout(expiration.timeout()), parseAction(expiration.action()));
		}

		protected static ExpirationMetaData from(IdleTimeoutExpiration expiration) {
			return new ExpirationMetaData(parseTimeout(expiration.timeout()), parseAction(expiration.action()));
		}

		protected static ExpirationMetaData from(TimeToLiveExpiration expiration) {
			return new ExpirationMetaData(parseTimeout(expiration.timeout()), parseAction(expiration.action()));
		}

		public GudExpirationAttributes toExpirationAttributes() {
			return GudExpirationAttributes.of(timeout(), expirationAction());
		}

		@SuppressWarnings("all")
		protected static int parseTimeout(String timeout) {

			try {
				return Integer.parseInt(timeout);
			}
			catch (NumberFormatException cause) {

				try {
					// Next, try to parse the 'timeout' as a Spring Expression using SpEL.
					return new SpelExpressionParser()
						.parseExpression(timeout)
						.getValue(EVALUATION_CONTEXT_REFERENCE.get(), Integer.TYPE);
				}
				catch (ParseException e) {

					// Finally, try to process the 'timeout' as a Spring Property Placeholder.
					if (BEAN_FACTORY_REFERENCE.get() instanceof ConfigurableBeanFactory) {
						return Integer.parseInt(((ConfigurableBeanFactory) BEAN_FACTORY_REFERENCE.get())
							.resolveEmbeddedValue(timeout));
					}

					throw cause;
				}
			}
		}

		protected static ExpirationActionType parseAction(String action) {

			try {
				return ExpirationActionType.valueOf(EXPIRATION_ACTION_CONVERTER.convert(action));
			}
			catch (IllegalArgumentException cause) {

				// Next, try to parse the 'action' as a Spring Expression using SpEL.
				EvaluationException evaluationException = new EvaluationException(String.format(
					"[%s] is not resolvable as an ExpirationAction(Type)", action), cause);

				EvaluationContext evaluationContext = EVALUATION_CONTEXT_REFERENCE.get();

				try {

					Expression expression = new SpelExpressionParser().parseExpression(action);

					Class<?> valueType = expression.getValueType(evaluationContext);

					if (String.class.equals(valueType)) {
						return ExpirationActionType.valueOf(EXPIRATION_ACTION_CONVERTER
							.convert(expression.getValue(evaluationContext, String.class)));
					}
					else if (GudExpirationAction.class.equals(valueType)) {
						return ExpirationActionType.valueOf(expression.getValue(evaluationContext, GudExpirationAction.class));
					}
					else if (ExpirationActionType.class.equals(valueType)) {
						return expression.getValue(evaluationContext, ExpirationActionType.class);
					}

					throw evaluationException;
				}
				catch (ParseException e) {

					// Finally, try to process the 'action' as a Spring Property Placeholder.
					if (BEAN_FACTORY_REFERENCE.get() instanceof ConfigurableBeanFactory) {
						try {

							String resolvedValue =
								((ConfigurableBeanFactory) BEAN_FACTORY_REFERENCE.get()).resolveEmbeddedValue(action);

							return ExpirationActionType.valueOf(EXPIRATION_ACTION_CONVERTER.convert(resolvedValue));
						}
						catch (IllegalArgumentException ignore) {
						}
					}

					throw evaluationException;
				}
			}
		}

		public ExpirationActionType action() {
			return action;
		}

		public GudExpirationAction expirationAction() {
			return action().getGudExpirationAction();
		}

		public int timeout() {
			return timeout;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object obj) {

			if (this == obj) {
				return true;
			}

			if (!(obj instanceof ExpirationMetaData)) {
				return false;
			}

			ExpirationMetaData that = (ExpirationMetaData) obj;

			return (this.timeout() == that.timeout()
				&& ObjectUtils.nullSafeEquals(this.action(), that.action()));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {

			int hashValue = 17;

			hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(timeout());
			hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(action());

			return hashValue;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return String.format("{ @type = %1$s, timeout = %2$d, action = %3$s }",
				getClass().getName(), timeout(), action());
		}
	}
}
