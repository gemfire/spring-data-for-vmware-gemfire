/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation.support;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalArgumentException;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.context.expression.EnvironmentAccessor;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.springframework.expression.spel.support.StandardTypeLocator;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * An abstract base class encapsulating functionality common to all Spring Data for Apache Geode (SDG) annotations
 * and configuration classes used to configure Apache Geode objects using Spring Data for Apache Geode.
 *
 * @author John Blum
 * @author Udo Kohlmeyer
 * @see ClassLoader
 * @see Annotation
 * @see AnnotatedElement
 * @see BeanClassLoaderAware
 * @see BeanFactory
 * @see BeanFactoryAware
 * @see AnnotatedBeanDefinition
 * @see ConfigurableBeanFactory
 * @see AbstractBeanDefinition
 * @see BeanDefinitionRegistry
 * @see EnvironmentAware
 * @see AnnotationAttributes
 * @see Environment
 * @see AnnotationMetadata
 * @see MethodMetadata
 * @see EvaluationContext
 * @since 1.9.0
 */
@SuppressWarnings("unused")
public abstract class AbstractAnnotationConfigSupport
		implements BeanClassLoaderAware, BeanFactoryAware, EnvironmentAware {

	protected static final Set<Integer> INFRASTRUCTURE_ROLES =
		CollectionUtils.asSet(BeanDefinition.ROLE_INFRASTRUCTURE, BeanDefinition.ROLE_SUPPORT);

	protected static final String ORG_SPRINGFRAMEWORK_DATA_GEMFIRE_PACKAGE = "org.springframework.data.gemfire";
	protected static final String ORG_SPRINGFRAMEWORK_PACKAGE = "org.springframework";
	protected static final String SPRING_DATA_GEMFIRE_PROPERTY_PREFIX = "spring.data.gemfire.";

	protected static final Supplier<ClassLoader> CURRENT_THREAD_CONTEXT_CLASS_LOADER =
		() -> Thread.currentThread().getContextClassLoader();

	private BeanFactory beanFactory;

	private ClassLoader beanClassLoader;

	private Environment environment;

	private final EvaluationContext evaluationContext;

	private final Logger logger;

	/**
	 * Asserts the given, configured {@link Object} is not {@literal null}.
	 *
	 * @param <T> {@link Class type} of the {@link Object} to assert.
	 * @param object {@link Object} reference to assert as not {@literal null}.
	 * @param message {@link String} containing the message used in the {@link IllegalStateException}
	 * thrown by this assertion if the {@link Object} is {@literal null}.
	 * @return the given {@link Object} used as the subject of this assertion.
	 * @throws IllegalStateException if the {@link Object} is {@literal null}.
	 */
	private static <T> T assertReferenceAndReturn(T object, String message) {
		Assert.state(object != null, message);
		return object;
	}

	/**
	 * Determines whether the given {@link Number} has value.
	 *
	 * The {@link Number} is considered valuable if it is not {@literal null} and is not equal to {@literal 0.0d}.
	 *
	 * @param value {@link Number} to evaluate.
	 * @return a boolean value indicating whether the given {@link Number} has value.
	 * @see Number
	 */
	public static boolean hasValue(@Nullable Number value) {
		return value != null && value.doubleValue() != 0.0d;
	}

	/**
	 * Determines whether the given {@link Object} has value.
	 *
	 * The {@link Object} is considered valuable if it is not {@literal null}.
	 *
	 * @param value {@link Object} to evaluate.
	 * @return a boolean value indicating whether the given {@link Object} has value.
	 * @see Object
	 */
	public static boolean hasValue(@Nullable Object value) {
		return value != null;
	}

	/**
	 * Determines whether the given {@link String} has value.
	 *
	 * The {@link String} is considered valuable if it is not {@literal null} and not {@literal empty}.
	 *
	 * @param value {@link String} to evaluate.
	 * @return a boolean value indicating whether the given {@link String} is valuable.
	 * @see String
	 */
	public static boolean hasValue(@Nullable String value) {
		return StringUtils.hasText(value);
	}

	/**
	 * Constructs a new instance of {@link AbstractAnnotationConfigSupport}.
	 *
	 * @see #AbstractAnnotationConfigSupport(BeanFactory)
	 */
	public AbstractAnnotationConfigSupport() {
		this(null);
	}

	/**
	 * Constructs a new instance of {@link AbstractAnnotationConfigSupport} initialized with the given, non-required
	 * {@link BeanFactory}.
	 *
	 * @param beanFactory reference to the Spring {@link BeanFactory}.
	 * @see BeanFactory
	 * @see #newEvaluationContext(BeanFactory)
	 * @see #newLogger()
	 */
	public AbstractAnnotationConfigSupport(@Nullable BeanFactory beanFactory) {

		this.evaluationContext = newEvaluationContext(beanFactory);
		this.logger = newLogger();
	}

	/**
	 * Constructs, configures and initializes a new instance of an {@link EvaluationContext}.
	 *
	 * @param beanFactory reference to the Spring {@link BeanFactory}.
	 * @return a new {@link EvaluationContext}.
	 * @see BeanFactory
	 * @see EvaluationContext
	 * @see #getBeanFactory()
	 */
	protected EvaluationContext newEvaluationContext(@Nullable BeanFactory beanFactory) {

		StandardEvaluationContext evaluationContext = new StandardEvaluationContext();

		evaluationContext.addPropertyAccessor(new BeanFactoryAccessor());
		evaluationContext.addPropertyAccessor(new EnvironmentAccessor());
		evaluationContext.addPropertyAccessor(new MapAccessor());
		evaluationContext.setTypeLocator(new StandardTypeLocator(getBeanClassLoader()));

		configureTypeConverter(evaluationContext, beanFactory);

		return evaluationContext;
	}

	private void configureTypeConverter(@Nullable EvaluationContext evaluationContext,
			@Nullable BeanFactory beanFactory) {

		Optional.ofNullable(evaluationContext)
			.filter(StandardEvaluationContext.class::isInstance)
			.map(StandardEvaluationContext.class::cast)
			.ifPresent(evalContext ->
				Optional.ofNullable(beanFactory)
					.filter(ConfigurableBeanFactory.class::isInstance)
					.map(ConfigurableBeanFactory.class::cast)
					.map(ConfigurableBeanFactory::getConversionService)
					.ifPresent(conversionService ->
						evalContext.setTypeConverter(new StandardTypeConverter(conversionService))));
	}

	/**
	 * Constructs a new instance of {@link Logger} to log statements printed by Spring Data for Apache Geode.
	 *
	 * @return a new instance of {@link Logger}.
	 * @see LoggerFactory#getLogger(Class)
	 * @see Logger
	 */
	protected @NonNull Logger newLogger() {
		return LoggerFactory.getLogger(getClass());
	}

	/**
	 * Determines whether the given {@link AnnotationMetadata type meta-data} for a particular {@link Class}
	 * is annotated with the declared {@link #getAnnotationTypeName()}.
	 *
	 * @param importingClassMetadata {@link AnnotationMetadata type meta-data} for a particular {@link Class}.
	 * @return a boolean indicating whether the particular {@link Class} is annotated with
	 * the declared {@link #getAnnotationTypeName()}.
	 * @see #isAnnotationPresent(AnnotationMetadata, String)
	 * @see #getAnnotationTypeName()
	 * @see AnnotationMetadata
	 */
	protected boolean isAnnotationPresent(@NonNull AnnotationMetadata importingClassMetadata) {
		return isAnnotationPresent(importingClassMetadata, getAnnotationTypeName());
	}

	/**
	 * Determines whether the given {@link AnnotationMetadata type meta-data} for a particular {@link Class}
	 * is annotated with the given {@link Annotation} defined by {@link String name}.
	 *
	 * @param importingClassMetadata {@link AnnotationMetadata type meta-data} for a particular {@link Class}.
	 * @param annotationName {@link String name} of the {@link Annotation} of interests.
	 * @return a boolean indicating whether the particular {@link Class} is annotated with
	 * the given {@link Annotation} defined by {@link String name}.
	 * @see AnnotationMetadata
	 */
	protected boolean isAnnotationPresent(@NonNull AnnotationMetadata importingClassMetadata,
			@NonNull String annotationName) {

		return importingClassMetadata.hasAnnotation(annotationName);
	}

	/**
	 * Returns the {@link AnnotationAttributes} for the given {@link Annotation}.
	 *
	 * @param annotation {@link Annotation} to get the {@link AnnotationAttributes} for.
	 * @return the {@link AnnotationAttributes} for the given {@link Annotation}.
	 * @see AnnotationAttributes
	 * @see Annotation
	 */
	protected @NonNull AnnotationAttributes getAnnotationAttributes(@NonNull Annotation annotation) {
		return AnnotationAttributes.fromMap(AnnotationUtils.getAnnotationAttributes(annotation));
	}

	/**
	 * Returns {@link AnnotationAttributes} for the declared {@link #getAnnotationTypeName()}.
	 *
	 * @param importingClassMetadata {@link AnnotationMetadata type meta-data} for a particular {@link Class}.
	 * @return {@link AnnotationAttributes} for the declared {@link #getAnnotationTypeName()}.
	 * @see AnnotationAttributes
	 * @see AnnotationMetadata
	 * @see #getAnnotationAttributes(AnnotationMetadata, String)
	 * @see #getAnnotationTypeName()
	 */
	protected @NonNull AnnotationAttributes getAnnotationAttributes(@NonNull AnnotationMetadata importingClassMetadata) {
		return getAnnotationAttributes(importingClassMetadata, getAnnotationTypeName());
	}

	/**
	 * Returns {@link AnnotationAttributes} for the given {@link String named} {@link Annotation} from the given
	 * {@link AnnotationMetadata type meta-data}.
	 *
	 * @param importingClassMetadata {@link AnnotationMetadata type meta-data} for a particular {@link Class}.
	 * @param annotationName {@link String name} of the {@link Annotation} of interests.
	 * @return {@link AnnotationAttributes} for the given {@link String named} {@link Annotation}.
	 * @see AnnotationAttributes
	 * @see AnnotationMetadata
	 */
	protected @NonNull AnnotationAttributes getAnnotationAttributes(@NonNull AnnotationMetadata importingClassMetadata,
			@NonNull String annotationName) {

		return AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(annotationName));
	}

	/**
	 * Returns the cache application {@link Annotation} type pertaining to this configuration.
	 *
	 * @return the cache application {@link Annotation} type used by this application.
	 */
	protected abstract @NonNull Class<? extends Annotation> getAnnotationType();

	/**
	 * Returns the fully-qualified {@link Class#getName() class name} of the cache application
	 * {@link Annotation} type.
	 *
	 * @return the fully-qualified {@link Class#getName() class name} of the cache application
	 * {@link Annotation} type.
	 * @see Class#getName()
	 * @see #getAnnotationType()
	 */
	protected @NonNull String getAnnotationTypeName() {
		return getAnnotationType().getName();
	}

	/**
	 * Returns the simple {@link Class#getName() class name} of the cache application
	 * {@link Annotation} type.
	 *
	 * @return the simple {@link Class#getName() class name} of the cache application
	 * {@link Annotation} type.
	 * @see Class#getSimpleName()
	 * @see #getAnnotationType()
	 */
	protected @NonNull String getAnnotationTypeSimpleName() {
		return getAnnotationType().getSimpleName();
	}

	/**
	 * Null-safe method used to determine whether the given {@link Object bean} is a Spring container provided
	 * infrastructure bean.
	 *
	 * @param bean {@link Object} to evaluate.
	 * @return {@literal true} iff the {@link Object bean} is not a Spring container provided infrastructure bean.
	 * @see #isNotInfrastructureClass(String)
	 * @see Object#getClass()
	 * @see Class#getName()
	 */
	protected boolean isNotInfrastructureBean(@Nullable Object bean) {

		return Optional.ofNullable(bean)
			.map(Object::getClass)
			.map(Class::getName)
			.filter(this::isNotInfrastructureClass)
			.isPresent();
	}

	/**
	 * Null-safe method used to determine whether the bean defined by the given {@link BeanDefinition}
	 * is a Spring container provided infrastructure bean.
	 *
	 * @param beanDefinition {@link BeanDefinition} to evaluate.
	 * @return {@literal true} iff the bean defined by the given {@link BeanDefinition} is not a Spring container
	 * provided infrastructure bean.
	 * @see BeanDefinition
	 * @see #isNotInfrastructureClass(BeanDefinition)
	 * @see #isNotInfrastructureRole(BeanDefinition)
	 */
	protected boolean isNotInfrastructureBean(@Nullable BeanDefinition beanDefinition) {
		return isNotInfrastructureRole(beanDefinition) && isNotInfrastructureClass(beanDefinition);
	}

	/**
	 * Null-safe method used to determine whether the bean defined by the given {@link BeanDefinition}
	 * is a Spring container infrastructure bean based on the bean's class name.
	 *
	 * @param beanDefinition {@link BeanDefinition} of the bean to evaluate.
	 * @return {@literal true} iff the bean defined in the given {@link BeanDefinition} is not a Spring container
	 * infrastructure bean. Returns {@literal false} if the bean class name cannot be resolved.
	 * @see BeanDefinition
	 * @see #resolveBeanClassName(BeanDefinition)
	 * @see #isNotInfrastructureClass(String)
	 */
	protected boolean isNotInfrastructureClass(@Nullable BeanDefinition beanDefinition) {

		return resolveBeanClassName(beanDefinition)
			.filter(this::isNotInfrastructureClass)
			.isPresent();
	}

	/**
	 * Determines whether the given {@link Class#getName() class name} is considered a Spring container
	 * infrastructure type.
	 *
	 * The {@link String class name} is considered a Spring container infrastructure type if the package name
	 * begins with {@literal org.springframework}, excluding {@literal org.springframework.data.gemfire}.
	 *
	 * @param className {@link String} containing the {@literal name} of the class type to evaluate;
	 * must not be {@literal null}.
	 * @return {@literal true} iff the given {@link Class#getName() class name} is not considered a Spring container
	 * infrastructure type.
	 */
	boolean isNotInfrastructureClass(@NonNull String className) {

		return className.startsWith(ORG_SPRINGFRAMEWORK_DATA_GEMFIRE_PACKAGE)
			|| !className.startsWith(ORG_SPRINGFRAMEWORK_PACKAGE);
	}

	/**
	 * Null-safe method to determines whether the bean defined by the given {@link BeanDefinition}
	 * is a Spring container infrastructure bean based on the bean's role.
	 *
	 * @param beanDefinition {@link BeanDefinition} of the bean to evaluate.
	 * @return {@literal true} iff the bean defined in the given {@link BeanDefinition} is not a Spring container
	 * infrastructure bean.
	 * @see BeanDefinition
	 */
	protected boolean isNotInfrastructureRole(@Nullable BeanDefinition beanDefinition) {

		return Optional.ofNullable(beanDefinition)
			.map(BeanDefinition::getRole)
			.filter(role -> !INFRASTRUCTURE_ROLES.contains(role))
			.isPresent();
	}

	/**
	 * Determines whether the given {@link Method} was declared and defined by the user.
	 *
	 * A {@link Method} is considered a user-level {@link Method} if the {@link Method} is not
	 * an {@link Object} class method, is a {@link Method#isBridge() Bridge Method}
	 * or is not {@link Method#isSynthetic()} nor a Groovy method.
	 *
	 * @param method {@link Method} to evaluate.
	 * @return a boolean value indicating whether the {@link Method} was declared/defined by the user.
	 * @see Method
	 */
	protected boolean isUserLevelMethod(@Nullable Method method) {

		return Optional.ofNullable(method)
			.filter(ClassUtils::isUserLevelMethod)
			.filter(it -> !Object.class.equals(it.getDeclaringClass()))
			.isPresent();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBeanClassLoader(@Nullable ClassLoader beanClassLoader) {
		this.beanClassLoader = beanClassLoader;
	}

	/**
	 * Returns a reference to the {@link ClassLoader} use by the Spring {@link BeanFactory} to load classes
	 * for bean definitions.
	 *
	 * @return the {@link ClassLoader} used by the Spring {@link BeanFactory} to load classes for bean definitions.
	 * @see #setBeanClassLoader(ClassLoader)
	 */
	protected @Nullable ClassLoader getBeanClassLoader() {
		return this.beanClassLoader;
	}

	/**
	 * Resolves the {@link ClassLoader bean ClassLoader} to the configured {@link ClassLoader}
	 * or the {@link Thread#getContextClassLoader() Thread Context ClassLoader}.
	 *
	 * @return the configured {@link ClassLoader} or the
	 * {@link Thread#getContextClassLoader() Thread Context ClassLoader}.
	 * @see Thread#getContextClassLoader()
	 * @see #getBeanClassLoader()
	 */
	protected @NonNull ClassLoader resolveBeanClassLoader() {

		ClassLoader beanClassLoader = getBeanClassLoader();

		return beanClassLoader != null ? beanClassLoader : CURRENT_THREAD_CONTEXT_CLASS_LOADER.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBeanFactory(@Nullable BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
		configureTypeConverter(getEvaluationContext(), beanFactory);
	}

	/**
	 * Returns a reference to the Spring {@link BeanFactory} in the current application context.
	 *
	 * @return a reference to the Spring {@link BeanFactory}.
	 * @throws IllegalStateException if the Spring {@link BeanFactory} was not properly configured.
	 * @see BeanFactory
	 */
	protected @NonNull BeanFactory getBeanFactory() {
		return assertReferenceAndReturn(this.beanFactory, "BeanFactory is required");
	}

	/**
	 * Sets a reference to the Spring {@link Environment}.
	 *
	 * @param environment Spring {@link Environment}.
	 * @see EnvironmentAware#setEnvironment(Environment)
	 * @see Environment
	 */
	@Override
	public void setEnvironment(@Nullable Environment environment) {
		this.environment = environment;
	}

	/**
	 * Returns a reference to the Spring {@link Environment}.
	 *
	 * @return a reference to the Spring {@link Environment}.
	 * @see Environment
	 */
	protected @Nullable Environment getEnvironment() {
		return this.environment;
	}

	/**
	 * Returns a reference to the {@link EvaluationContext} used to evaluate SpEL expressions.
	 *
	 * @return a reference to the {@link EvaluationContext} used to evaluate SpEL expressions.
	 * @see EvaluationContext
	 */
	protected @NonNull EvaluationContext getEvaluationContext() {
		return this.evaluationContext;
	}

	/**
	 * Returns a reference to the {@link Logger} used by this class to log {@link String messages}.
	 *
	 * @return a reference to the {@link Logger} used by this class to log {@link String messages}.
	 * @see org.apache.commons.logging.Log
	 */
	protected @NonNull Logger getLogger() {
		return this.logger;
	}

	/**
	 * Logs the {@link String message} formatted with the array of {@link Object arguments} at debug level.
	 *
	 * @param message {@link String} containing the message to log.
	 * @param args array of {@link Object arguments} used to format the {@code message}.
	 * @see #logDebug(Supplier)
	 */
	protected void logDebug(String message, Object... args) {
		logDebug(() -> String.format(message, args));
	}

	/**
	 * Logs the {@link String message} supplied by the given {@link Supplier} at debug level.
	 *
	 * @param message {@link Supplier} containing the {@link String message} and arguments to log.
	 * @see org.apache.commons.logging.Log#isDebugEnabled()
	 * @see org.apache.commons.logging.Log#debug(Object)
	 * @see #getLogger()
	 */
	protected void logDebug(Supplier<String> message) {
		Optional.ofNullable(getLogger())
			.filter(Logger::isDebugEnabled)
			.ifPresent(log -> log.debug(message.get()));
	}

	/**
	 * Logs the {@link String message} formatted with the array of {@link Object arguments} at info level.
	 *
	 * @param message {@link String} containing the message to log.
	 * @param args array of {@link Object arguments} used to format the {@code message}.
	 * @see #logInfo(Supplier)
	 */
	protected void logInfo(String message, Object... args) {
		logInfo(() -> String.format(message, args));
	}

	/**
	 * Logs the {@link String message} supplied by the given {@link Supplier} at info level.
	 *
	 * @param message {@link Supplier} containing the {@link String message} and arguments to log.
	 * @see org.apache.commons.logging.Log#isInfoEnabled()
	 * @see org.apache.commons.logging.Log#info(Object)
	 * @see #getLogger()
	 */
	protected void logInfo(Supplier<String> message) {
		Optional.ofNullable(getLogger())
			.filter(Logger::isInfoEnabled)
			.ifPresent(log -> log.info(message.get()));
	}

	/**
	 * Logs the {@link String message} formatted with the array of {@link Object arguments} at warn level.
	 *
	 * @param message {@link String} containing the message to log.
	 * @param args array of {@link Object arguments} used to format the {@code message}.
	 * @see #logWarning(Supplier)
	 */
	protected void logWarning(String message, Object... args) {
		logWarning(() -> String.format(message, args));
	}

	/**
	 * Logs the {@link String message} supplied by the given {@link Supplier} at warning level.
	 *
	 * @param message {@link Supplier} containing the {@link String message} and arguments to log.
	 * @see org.apache.commons.logging.Log#isWarnEnabled()
	 * @see org.apache.commons.logging.Log#warn(Object)
	 * @see #getLogger()
	 */
	protected void logWarning(Supplier<String> message) {
		Optional.ofNullable(getLogger())
			.filter(Logger::isWarnEnabled)
			.ifPresent(log -> log.info(message.get()));
	}

	/**
	 * Logs the {@link String message} formatted with the array of {@link Object arguments} at error level.
	 *
	 * @param message {@link String} containing the message to log.
	 * @param args array of {@link Object arguments} used to format the {@code message}.
	 * @see #logError(Supplier)
	 */
	protected void logError(String message, Object... args) {
		logError(() -> String.format(message, args));
	}

	/**
	 * Logs the {@link String message} supplied by the given {@link Supplier} at error level.
	 *
	 * @param message {@link Supplier} containing the {@link String message} and arguments to log.
	 * @see org.apache.commons.logging.Log#isErrorEnabled()
	 * @see org.apache.commons.logging.Log#error(Object)
	 * @see #getLogger()
	 */
	protected void logError(Supplier<String> message) {
		Optional.ofNullable(getLogger())
			.filter(Logger::isErrorEnabled)
			.ifPresent(log -> log.info(message.get()));
	}

	/**
	 * Registers the {@link AbstractBeanDefinition} with the {@link BeanDefinitionRegistry} using a generated
	 * {@link String bean name}.
	 *
	 * @param beanDefinition {@link AbstractBeanDefinition} to register.
	 * @return the given {@link AbstractBeanDefinition}.
	 * @see BeanFactory
	 * @see AbstractBeanDefinition
	 * @see BeanDefinitionRegistry
	 * @see BeanDefinitionReaderUtils#registerWithGeneratedName(AbstractBeanDefinition, BeanDefinitionRegistry)
	 * @see #getBeanFactory()
	 */
	protected @NonNull AbstractBeanDefinition register(@NonNull AbstractBeanDefinition beanDefinition) {

		BeanFactory beanFactory = getBeanFactory();

		return beanFactory instanceof BeanDefinitionRegistry
			? register(beanDefinition, (BeanDefinitionRegistry) beanFactory)
			: beanDefinition;
	}

	/**
	 * Registers the {@link AbstractBeanDefinition} with the given {@link BeanDefinitionRegistry} using a generated
	 * {@link String bean name}.
	 *
	 * @param beanDefinition {@link AbstractBeanDefinition} to register.
	 * @param registry {@link BeanDefinitionRegistry} used to register the {@link AbstractBeanDefinition}.
	 * @return the given {@link AbstractBeanDefinition}.
	 * @see AbstractBeanDefinition
	 * @see BeanDefinitionRegistry
	 * @see BeanDefinitionReaderUtils#registerWithGeneratedName(AbstractBeanDefinition, BeanDefinitionRegistry)
	 */
	protected @NonNull AbstractBeanDefinition register(@NonNull AbstractBeanDefinition beanDefinition,
			@Nullable BeanDefinitionRegistry registry) {

		if (registry != null) {
			BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition, registry);
		}

		return beanDefinition;
	}

	protected List<String> arrayOfPropertyNamesFor(String propertyNamePrefix) {
		return arrayOfPropertyNamesFor(propertyNamePrefix, null);
	}

	protected List<String> arrayOfPropertyNamesFor(String propertyNamePrefix, String propertyNameSuffix) {

		List<String> propertyNames = new ArrayList<>();

		boolean found = true;

		for (int index = 0; (found && index < Integer.MAX_VALUE); index++) {

			String propertyName = asArrayProperty(propertyNamePrefix, index, propertyNameSuffix);

			found = getEnvironment().containsProperty(propertyName);

			if (found) {
				propertyNames.add(propertyName);
			}
		}

		return propertyNames;
	}

	protected String asArrayProperty(String propertyNamePrefix, int index, String propertyNameSuffix) {
		return String.format("%1$s[%2$d]%3$s", propertyNamePrefix, index,
			Optional.ofNullable(propertyNameSuffix).filter(StringUtils::hasText).map("."::concat).orElse(""));
	}

	protected String cacheProperty(String propertyNameSuffix) {
		return String.format("%1$s%2$s", propertyName("cache."), propertyNameSuffix);
	}

	protected String cacheClientProperty(String propertyNameSuffix) {
		return String.format("%1$s%2$s", propertyName("cache.client."), propertyNameSuffix);
	}

	protected String cacheCompressionProperty(String propertyNameSuffix) {
		return String.format("%1$s%2$s", propertyName("cache.compression."), propertyNameSuffix);
	}

	protected String cacheOffHeapProperty(String propertyNameSuffix) {
		return String.format("%1$s%2$s", propertyName("cache.off-heap."), propertyNameSuffix);
	}

	protected String cachePeerProperty(String propertyNameSuffix) {
		return String.format("%1$s%2$s", propertyName("cache.peer."), propertyNameSuffix);
	}

	protected String cacheServerProperty(String propertyNameSuffix) {
		return String.format("%1$s%2$s", propertyName("cache.server."), propertyNameSuffix);
	}

	protected String namedCacheServerProperty(String name, String propertyNameSuffix) {
		return String.format("%1$s%2$s.%3$s", propertyName("cache.server."), name, propertyNameSuffix);
	}

	protected String clusterProperty(String propertyNameSuffix) {
		return String.format("%1$s%2$s", propertyName("cluster."), propertyNameSuffix);
	}

	protected String diskStoreProperty(String propertyNameSuffix) {
		return String.format("%1$s%2$s", propertyName("disk.store."), propertyNameSuffix);
	}

	protected String namedDiskStoreProperty(String name, String propertyNameSuffix) {
		return String.format("%1$s%2$s.%3$s", propertyName("disk.store."), name, propertyNameSuffix);
	}

	protected String entitiesProperty(String propertyNameSuffix) {
		return String.format("%1$s%2$s", propertyName("entities."), propertyNameSuffix);
	}

	protected String locatorProperty(String propertyNameSuffix) {
		return String.format("%1$s%2$s", propertyName("locator."), propertyNameSuffix);
	}

	protected String loggingProperty(String propertyNameSuffix) {
		return String.format("%1$s%2$s", propertyName("logging."), propertyNameSuffix);
	}

	protected String managementProperty(String propertyNameSuffix) {
		return String.format("%1$s%2$s", propertyName("management."), propertyNameSuffix);
	}

	protected String managerProperty(String propertyNameSuffix) {
		return String.format("%1$s%2$s", propertyName("manager."), propertyNameSuffix);
	}

	protected String pdxProperty(String propertyNameSuffix) {
		return String.format("%1$s%2$s", propertyName("pdx."), propertyNameSuffix);
	}

	protected String poolProperty(String propertyNameSuffix) {
		return String.format("%1$s%2$s", propertyName("pool."), propertyNameSuffix);
	}

	protected String namedPoolProperty(String name, String propertyNameSuffix) {
		return String.format("%1$s%2$s.%3$s", propertyName("pool."), name, propertyNameSuffix);
	}

	protected String securityProperty(String propertyNameSuffix) {
		return String.format("%1$s%2$s", propertyName("security."), propertyNameSuffix);
	}

	protected String sslProperty(String propertyNameSuffix) {
		return String.format("%1$s%2$s", securityProperty("ssl."), propertyNameSuffix);
	}

	protected String statsProperty(String propertyNameSuffix) {
		return String.format("%1$s%2$s", propertyName("stats."), propertyNameSuffix);
	}

	protected String serviceProperty(String propertyNameSuffix) {
		return String.format("%1$s%2$s", propertyName("service."), propertyNameSuffix);
	}

	@Deprecated
	protected String redisServiceProperty(String propertyNameSuffix) {
		return String.format("%1$s%2$s", serviceProperty("redis."), propertyNameSuffix);
	}

	protected String memcachedServiceProperty(String propertyNameSuffix) {
		return String.format("%1$s%2$s", serviceProperty("memcached."), propertyNameSuffix);
	}

	protected String httpServiceProperty(String propertyNameSuffix) {
		return String.format("%1$s%2$s", serviceProperty("http."), propertyNameSuffix);
	}

	protected String gatewayReceiverProperty(String propertyNameSuffix) {
		return String.format("%1$s%2$s", propertyName("gateway.receiver."), propertyNameSuffix);
	}

	protected String gatewaySenderProperty(String propertyNameSuffix) {
		return String.format("%1$s%2$s", propertyName("gateway.sender."), propertyNameSuffix);
	}

	protected String namedGatewaySenderProperty(String name, String propertyNameSuffix) {
		return String.format("%1$s%2$s.%3$s", propertyName("gateway.sender."), name, propertyNameSuffix);
	}

	/**
	 * Returns the fully-qualified {@link String property name}.
	 *
	 * The fully qualified {@link String property name} consists of the {@link String property name}
	 * concatenated with the {@code propertyNameSuffix}.
	 *
	 * @param propertyNameSuffix {@link String} containing the property name suffix
	 * concatenated with the {@link String base property name}.
	 * @return the fully-qualified {@link String property name}.
	 * @see String
	 */
	protected String propertyName(String propertyNameSuffix) {
		return String.format("%1$s%2$s", SPRING_DATA_GEMFIRE_PROPERTY_PREFIX, propertyNameSuffix);
	}

	/**
	 * Resolves the value for the given property identified by {@link String name} from the Spring {@link Environment}
	 * as an instance of the specified {@link Class type}.
	 *
	 * @param <T> {@link Class} type of the {@code propertyName property's} assigned value.
	 * @param propertyName {@link String} containing the name of the required property to resolve.
	 * @param type {@link Class} type of the property's assigned value.
	 * @return the assigned value of the {@link String named} property.
	 * @throws IllegalArgumentException if the property has not been assigned a value.
	 * For {@link String} values, this also means the value cannot be {@link String#isEmpty() empty}.
	 * For non-{@link String} values, this means the value must not be {@literal null}.
	 * @see #resolveProperty(String, Class, Object)
	 */
	protected <T> T requireProperty(String propertyName, Class<T> type) {

		return Optional.of(propertyName)
			.map(it -> resolveProperty(propertyName, type, null))
			.filter(Objects::nonNull)
			.filter(value -> !(value instanceof String) || StringUtils.hasText((String) value))
			.orElseThrow(() -> newIllegalArgumentException("Property [%s] is required", propertyName));
	}

	/**
	 * Resolves the {@link Annotation} with the given {@link Class type} from the {@link AnnotatedElement}.
	 *
	 * @param <A> {@link Class Subclass type} of the resolved {@link Annotation}.
	 * @param annotatedElement {@link AnnotatedElement} from which to resolve the {@link Annotation}.
	 * @param annotationType {@link Class type} of the {@link Annotation} to resolve from the {@link AnnotatedElement}.
	 * @return the resolved {@link Annotation}.
	 * @see Annotation
	 * @see AnnotatedElement
	 * @see Class
	 */
	protected <A extends Annotation> A resolveAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType) {

		return annotatedElement instanceof Class
			? AnnotatedElementUtils.findMergedAnnotation(annotatedElement, annotationType)
			: AnnotationUtils.findAnnotation(annotatedElement, annotationType);
	}

	/**
	 * Resolves the {@link Class type} of the bean defined by the given {@link BeanDefinition}.
	 *
	 * @param beanDefinition {@link BeanDefinition} defining the bean from which the {@link Class type} is resolved.
	 * @param registry {@link BeanDefinitionRegistry} used to resolve the {@link ClassLoader} used to resolve
	 * the bean's {@link Class type}.
	 * @return an {@link Optional} {@link Class} specifying the resolved type of the bean.
	 * @see BeanDefinition
	 * @see BeanDefinitionRegistry
	 * @see #resolveBeanClassLoader(BeanDefinitionRegistry)
	 * @see #resolveBeanClass(BeanDefinition, ClassLoader)
	 */
	protected Optional<Class<?>> resolveBeanClass(@Nullable BeanDefinition beanDefinition,
			@Nullable BeanDefinitionRegistry registry) {

		return resolveBeanClass(beanDefinition, resolveBeanClassLoader(registry));
	}

	/**
	 * Resolves the {@link Class type} of the bean defined by the given {@link BeanDefinition}.
	 *
	 * @param beanDefinition {@link BeanDefinition} defining the bean from which the {@link Class type} is resolved.
	 * @param beanFactory {@link ConfigurableBeanFactory} used to resolve the {@link ClassLoader} used to resolve
	 * the bean's {@link Class type}.
	 * @return an {@link Optional} {@link Class} specifying the resolved type of the bean.
	 * @see BeanDefinition
	 * @see ConfigurableBeanFactory
	 * @see #resolveBeanClassLoader(ConfigurableBeanFactory)
	 * @see #resolveBeanClass(BeanDefinition, ClassLoader)
	 */
	protected Optional<Class<?>> resolveBeanClass(@Nullable BeanDefinition beanDefinition,
			@NonNull ConfigurableBeanFactory beanFactory) {

		return resolveBeanClass(beanDefinition, resolveBeanClassLoader(beanFactory));
	}

	/**
	 * Resolves the {@link Class type} of the bean defined by the given {@link BeanDefinition}.
	 *
	 * @param beanDefinition {@link BeanDefinition} defining the bean from which the {@link Class type} is resolved.
	 * @param classLoader {@link ClassLoader} used to resolve the bean's {@link Class type}.
	 * @return an {@link Optional} resolved {@link Class type} of the bean.
	 * @see ClassLoader
	 * @see BeanDefinition
	 * @see AbstractBeanDefinition#resolveBeanClass(ClassLoader)
	 * @see ClassUtils#forName(String, ClassLoader)
	 * @see #resolveBeanClassName(BeanDefinition)
	 */
	protected Optional<Class<?>> resolveBeanClass(@Nullable BeanDefinition beanDefinition,
			@Nullable ClassLoader classLoader) {

		Class<?> beanClass = beanDefinition instanceof AbstractBeanDefinition
			? safeResolveType(() -> ((AbstractBeanDefinition) beanDefinition).resolveBeanClass(classLoader))
			: null;

		if (beanClass == null) {
			beanClass = resolveBeanClassName(beanDefinition)
				.map(beanClassName -> safeResolveType(() -> ClassUtils.forName(beanClassName, classLoader)))
				.orElse(null);
		}

		return Optional.ofNullable(beanClass);
	}

	/**
	 * Attempts to resolve the {@link ClassLoader} used by the {@link BeanDefinitionRegistry}
	 * to load {@link Class} definitions of the beans defined in the registry.
	 *
	 * @param registry {@link BeanDefinitionRegistry} from which to resolve the {@link ClassLoader}.
	 * @return the resolved {@link ClassLoader} from the {@link BeanDefinitionRegistry}
	 * or the {@link Thread#currentThread() current Thread's} {@link Thread#getContextClassLoader() context ClassLoader}.
	 * @see ConfigurableBeanFactory#getBeanClassLoader()
	 * @see BeanDefinitionRegistry
	 * @see Thread#getContextClassLoader()
	 * @see Thread#currentThread()
	 */
	protected @NonNull ClassLoader resolveBeanClassLoader(@Nullable BeanDefinitionRegistry registry) {

		return Optional.ofNullable(registry)
			.filter(ConfigurableBeanFactory.class::isInstance)
			.map(ConfigurableBeanFactory.class::cast)
			.map(ConfigurableBeanFactory::getBeanClassLoader)
			.orElseGet(CURRENT_THREAD_CONTEXT_CLASS_LOADER);
	}

	/**
	 * Attempts to resolve the {@link ClassLoader} used by the {@link ConfigurableBeanFactory}
	 * to load {@link Class} definitions of the beans created by the factory.
	 *
	 * @param beanFactory {@link ConfigurableBeanFactory} from which to resolve the {@link ClassLoader}.
	 * @return the resolved {@link ClassLoader} from the {@link ConfigurableBeanFactory}
	 * or the {@link Thread#currentThread() current Thread's} {@link Thread#getContextClassLoader() context ClassLoader}.
	 * @see ConfigurableBeanFactory#getBeanClassLoader()
	 * @see ConfigurableBeanFactory
	 * @see Thread#getContextClassLoader()
	 * @see Thread#currentThread()
	 */
	protected @NonNull ClassLoader resolveBeanClassLoader(@Nullable ConfigurableBeanFactory beanFactory) {

		return Optional.ofNullable(beanFactory)
			.map(ConfigurableBeanFactory::getBeanClassLoader)
			.orElseGet(CURRENT_THREAD_CONTEXT_CLASS_LOADER);
	}

	/**
	 * Resolves the class type name of the bean defined by the given {@link BeanDefinition}.
	 *
	 * @param beanDefinition {@link BeanDefinition} defining the bean from which to resolve the class type name.
	 * @return an {@link Optional} {@link String} containing the resolved class type name of the bean defined
	 * by the given {@link BeanDefinition}.
	 * @see BeanDefinition#getBeanClassName()
	 */
	protected Optional<String> resolveBeanClassName(@Nullable BeanDefinition beanDefinition) {

		Optional<BeanDefinition> optionalBeanDefinition = Optional.ofNullable(beanDefinition);

		Optional<String> beanClassName = optionalBeanDefinition
			.map(BeanDefinition::getBeanClassName)
			.filter(StringUtils::hasText);

		boolean beanClassNameNotPresent = !beanClassName.isPresent();

		if (beanClassNameNotPresent) {
			beanClassName = optionalBeanDefinition
				.filter(AnnotatedBeanDefinition.class::isInstance)
				.filter(it -> StringUtils.hasText(it.getFactoryMethodName()))
				.map(AnnotatedBeanDefinition.class::cast)
				.map(AnnotatedBeanDefinition::getFactoryMethodMetadata)
				.map(MethodMetadata::getReturnTypeName);
		}

		return beanClassName;
	}

	/**
	 * Attempts to resolve the property with the given {@link String name} from the Spring {@link Environment}
	 * as a {@link Boolean}.
	 *
	 * @param propertyName {@link String name} of the property to resolve.
	 * @param defaultValue default value to return if the property is not defined or not set.
	 * @return the value of the property identified by {@link String name} or default value if the property
	 * is not defined or not set.
	 * @see #resolveProperty(String, Class, Object)
	 */
	protected Boolean resolveProperty(String propertyName, Boolean defaultValue) {
		return resolveProperty(propertyName, Boolean.class, defaultValue);
	}

	/**
	 * Attempts to resolve the property with the given {@link String name} from the Spring {@link Environment}
	 * as an {@link Double}.
	 *
	 * @param propertyName {@link String name} of the property to resolve.
	 * @param defaultValue default value to return if the property is not defined or not set.
	 * @return the value of the property identified by {@link String name} or default value if the property
	 * is not defined or not set.
	 * @see #resolveProperty(String, Class, Object)
	 */
	protected Double resolveProperty(String propertyName, Double defaultValue) {
		return resolveProperty(propertyName, Double.class, defaultValue);
	}

	/**
	 * Attempts to resolve the property with the given {@link String name} from the Spring {@link Environment}
	 * as an {@link Float}.
	 *
	 * @param propertyName {@link String name} of the property to resolve.
	 * @param defaultValue default value to return if the property is not defined or not set.
	 * @return the value of the property identified by {@link String name} or default value if the property
	 * is not defined or not set.
	 * @see #resolveProperty(String, Class, Object)
	 */
	protected Float resolveProperty(String propertyName, Float defaultValue) {
		return resolveProperty(propertyName, Float.class, defaultValue);
	}

	/**
	 * Attempts to resolve the property with the given {@link String name} from the Spring {@link Environment}
	 * as an {@link Integer}.
	 *
	 * @param propertyName {@link String name} of the property to resolve.
	 * @param defaultValue default value to return if the property is not defined or not set.
	 * @return the value of the property identified by {@link String name} or default value if the property
	 * is not defined or not set.
	 * @see #resolveProperty(String, Class, Object)
	 */
	protected Integer resolveProperty(String propertyName, Integer defaultValue) {
		return resolveProperty(propertyName, Integer.class, defaultValue);
	}

	/**
	 * Attempts to resolve the property with the given {@link String name} from the Spring {@link Environment}
	 * as a {@link Long}.
	 *
	 * @param propertyName {@link String name} of the property to resolve.
	 * @param defaultValue default value to return if the property is not defined or not set.
	 * @return the value of the property identified by {@link String name} or default value if the property
	 * is not defined or not set.
	 * @see #resolveProperty(String, Class, Object)
	 */
	protected Long resolveProperty(String propertyName, Long defaultValue) {
		return resolveProperty(propertyName, Long.class, defaultValue);
	}

	/**
	 * Attempts to resolve the property with the given {@link String name} from the Spring {@link Environment}
	 * as a {@link String}.
	 *
	 * @param propertyName {@link String name} of the property to resolve.
	 * @param defaultValue default value to return if the property is not defined or not set.
	 * @return the value of the property identified by {@link String name} or default value if the property
	 * is not defined or not set.
	 * @see #resolveProperty(String, Class, Object)
	 */
	protected String resolveProperty(String propertyName, String defaultValue) {
		return resolveProperty(propertyName, String.class, defaultValue);
	}

	/**
	 * Attempts to resolve the property with the given {@link String name} from the Spring {@link Environment}.
	 *
	 * @param <T> {@link Class} type of the property value.
	 * @param propertyName {@link String name} of the property to resolve.
	 * @param targetType {@link Class} type of the property's value.
	 * @return the value of the property identified by {@link String name} or {@literal null} if the property
	 * is not defined or not set.
	 * @see #resolveProperty(String, Class, Object)
	 */
	protected <T> T resolveProperty(String propertyName, Class<T> targetType) {
		return resolveProperty(propertyName, targetType, null);
	}

	/**
	 * Attempts to resolve the property with the given {@link String name} from the Spring {@link Environment}.
	 *
	 * @param <T> {@link Class} type of the property value.
	 * @param propertyName {@link String name} of the property to resolve.
	 * @param targetType {@link Class} type of the property's value.
	 * @param defaultValue default value to return if the property is not defined or not set.
	 * @return the value of the property identified by {@link String name} or default value if the property
	 * is not defined or not set.
	 * @see #getEnvironment()
	 */
	protected <T> T resolveProperty(String propertyName, Class<T> targetType, T defaultValue) {

		return Optional.ofNullable(getEnvironment())
			.filter(environment -> environment.containsProperty(propertyName))
			.map(environment -> {

				String resolvedPropertyName = environment.resolveRequiredPlaceholders(propertyName);

				return environment.getProperty(resolvedPropertyName, targetType, defaultValue);
			})
			.orElse(defaultValue);
	}

	/**
	 * Safely resolves a {@link Class type} returned by the given {@link TypeResolver} where the {@link Class type}
	 * resolution might result in a {@link ClassNotFoundException} or {@link NoClassDefFoundError}.
	 *
	 * @param <T> {@link Class} of the type being resolved.
	 * @param typeResolver {@link TypeResolver} used to resolve a specific {@link Class type}.
	 * @return the resolved {@link Class type} or {@literal null} if the {@link Class type} returned by
	 * the {@link TypeResolver} could not be resolved.
	 * @see TypeResolver
	 * @see ClassNotFoundException
	 * @see NoClassDefFoundError
	 * @see Class
	 */
	protected @Nullable <T> Class<T> safeResolveType(@NonNull TypeResolver<T> typeResolver) {

		try {
			return typeResolver.resolve();
		}
		catch (ClassNotFoundException | NoClassDefFoundError cause) {
			return null;
		}
	}

	/**
	 * {@link TypeResolver} is a {@link FunctionalInterface} defining a contract to encapsulate logic
	 * used to resolve a particular {@link Class type}.
	 *
	 * Implementations are free to decide on how a {@link Class type} gets resolved, such as
	 * with {@link Class#forName(String)} or by using {@literal ClassLoader#defineClass(String, byte[], int, int)}.
	 *
	 * @param <T> {@link Class} of the type to resolve.
	 */
	@FunctionalInterface
	protected interface TypeResolver<T> {
		Class<T> resolve() throws ClassNotFoundException;
	}
}
