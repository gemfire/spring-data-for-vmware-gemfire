/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.Declarable;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.data.gemfire.util.CacheUtils;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SpringContextBootstrappingInitializer} class is a GemFire configuration initializer used to bootstrap
 * a Spring {@link ApplicationContext} inside a GemFire Server JVM-based process.  This enables a GemFire Server
 * resource to be mostly configured with Spring Data GemFire's configuration meta-data.  The GemFire {@link Cache}
 * itself is the only resource that cannot be configured and initialized in a Spring context since the initializer
 * is not invoked until after GemFire creates and initializes the GemFire {@link Cache} for use.
 *
 * @author John Blum
 * @see Properties
 * @see Cache
 * @see Declarable
 * @see ApplicationContext
 * @see ApplicationListener
 * @see ConfigurableApplicationContext
 * @see AnnotationConfigApplicationContext
 * @see ApplicationContextEvent
 * @see ApplicationEventMulticaster
 * @see ClassPathXmlApplicationContext
 * @see DefaultResourceLoader
 * @link https://gemfire.docs.pivotal.io/latest/userguide/index.html#basic_config/the_cache/setting_cache_initializer.html
 * @link https://jira.springsource.org/browse/SGF-248
 * @since 1.4.0
 */
@SuppressWarnings("unused")
public class SpringContextBootstrappingInitializer implements ApplicationListener<ApplicationContextEvent>, Declarable {

	public static final String BASE_PACKAGES_PARAMETER = "basePackages";
	public static final String CONTEXT_CONFIG_LOCATIONS_PARAMETER = "contextConfigLocations";

	protected static final String CHARS_TO_DELETE = " \n\t";
	protected static final String COMMA_DELIMITER = ",";

	private static final ApplicationEventMulticaster applicationEventNotifier = new SimpleApplicationEventMulticaster();

	private static final AtomicReference<ClassLoader> beanClassLoaderReference = new AtomicReference<>(null);

	static volatile ConfigurableApplicationContext applicationContext;

	static volatile ContextRefreshedEvent contextRefreshedEvent;

	private static final List<Class<?>> registeredAnnotatedClasses = new CopyOnWriteArrayList<>();

	protected final Logger logger = initLogger();

	/**
	 * Gets a reference to the Spring ApplicationContext constructed, configured and initialized inside the GemFire
	 * Server-based JVM process.
	 *
	 * @return a reference to the Spring ApplicationContext bootstrapped by GemFire.
	 * @see ConfigurableApplicationContext
	 */
	public static synchronized ConfigurableApplicationContext getApplicationContext() {

		Assert.state(applicationContext != null,
			"A Spring ApplicationContext was not configured and initialized properly");

		return applicationContext;
	}

	/**
	 * Sets the ClassLoader used by the Spring ApplicationContext, created by this GemFire Initializer, when creating
	 * bean definition classes.
	 *
	 * @param beanClassLoader the ClassLoader used by the Spring ApplicationContext to load bean definition classes.
	 * @throws IllegalStateException if the Spring ApplicationContext has already been created
	 * and initialized.
	 * @see ClassLoader
	 */
	public static void setBeanClassLoader(ClassLoader beanClassLoader) {

		if (isApplicationContextInitializable()) {
			beanClassLoaderReference.set(beanClassLoader);
		}
		else {
			throw new IllegalStateException("A Spring ApplicationContext has already been initialized");
		}
	}

	/**
	 * Destroy the state of the {@link SpringContextBootstrappingInitializer}.
	 */
	public static void destroy() {

		beanClassLoaderReference.set(null);
		applicationContext = null;
		contextRefreshedEvent = null;
		registeredAnnotatedClasses.clear();

		synchronized (applicationEventNotifier) {
			applicationEventNotifier.removeAllListeners();
		}
	}

	/**
	 * Notifies any Spring ApplicationListeners of a current and existing ContextRefreshedEvent if the
	 * ApplicationContext had been previously created, initialized and refreshed before any ApplicationListeners
	 * interested in ContextRefreshedEvents were registered so that application components (such as the
	 * GemFire CacheLoaders extending LazyWiringDeclarableSupport objects) registered late, requiring configuration
	 * (auto-wiring), also get notified and wired accordingly.
	 *
	 * @param listener a Spring ApplicationListener requiring notification of any ContextRefreshedEvents after the
	 * ApplicationContext has already been created, initialized and/or refreshed.
	 * @see ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 * @see ContextRefreshedEvent
	 */
	protected static void notifyOnExistingContextRefreshedEvent(ApplicationListener<ContextRefreshedEvent> listener) {

		synchronized (applicationEventNotifier) {
			if (contextRefreshedEvent != null) {
				listener.onApplicationEvent(contextRefreshedEvent);
			}
		}
	}

	/**
	 * Registers a Spring ApplicationListener to be notified when the Spring ApplicationContext is created by GemFire
	 * when instantiating and initializing Declarables declared inside the &lt;initializer&gt; block inside GemFire's
	 * cache.xml file.
	 *
	 * @param <T> the Class type of the Spring ApplicationListener.
	 * @param listener the ApplicationListener to register for ContextRefreshedEvents multi-casted by this
	 * SpringContextBootstrappingInitializer.
	 * @return the reference to the ApplicationListener for method call chaining purposes.
	 * @see #notifyOnExistingContextRefreshedEvent(ApplicationListener)
	 * @see #unregister(ApplicationListener)
	 * @see ApplicationListener
	 * @see ContextRefreshedEvent
	 * @see SimpleApplicationEventMulticaster
	 * 	#addApplicationListener(org.springframework.context.ApplicationListener)
	 */
	public static <T extends ApplicationListener<ContextRefreshedEvent>> T register(T listener) {

		synchronized (applicationEventNotifier) {
			applicationEventNotifier.addApplicationListener(listener);
			notifyOnExistingContextRefreshedEvent(listener);
		}

		return listener;
	}

	/**
	 * Registers the specified Spring annotated POJO class, which will be used to configure and initialize
	 * the Spring ApplicationContext.
	 *
	 * @param annotatedClass the Spring annotated (@Configuration) POJO class to register.
	 * @return a boolean value indicating whether the Spring annotated POJO class was successfully registered.
	 * @see #unregister(Class)
	 */
	public static boolean register(Class<?> annotatedClass) {

		Assert.notNull(annotatedClass, "The Spring annotated class to register must not be null");

		return registeredAnnotatedClasses.add(annotatedClass);
	}

	/**
	 * Un-registers the Spring ApplicationListener from this SpringContextBootstrappingInitializer in order to stop
	 * receiving ApplicationEvents on Spring context refreshes.
	 *
	 * @param <T> the Class type of the Spring ApplicationListener.
	 * @param listener the ApplicationListener to unregister from receiving ContextRefreshedEvents by this
	 * SpringContextBootstrappingInitializer.
	 * @return the reference to the ApplicationListener for method call chaining purposes.
	 * @see #register(ApplicationListener)
	 * @see ApplicationListener
	 * @see ContextRefreshedEvent
	 * @see SimpleApplicationEventMulticaster
	 * 	#removeApplicationListener(org.springframework.context.ApplicationListener)
	 */
	public static <T extends ApplicationListener<ContextRefreshedEvent>> T unregister(T listener) {

		synchronized (applicationEventNotifier) {
			applicationEventNotifier.removeApplicationListener(listener);
		}

		return listener;
	}

	/**
	 * Un-registers the specified Spring annotated POJO class used to configure and initialize
	 * the Spring ApplicationContext.
	 *
	 * @param annotatedClass the Spring annotated (@Configuration) POJO class to unregister.
	 * @return a boolean value indicating whether the Spring annotated POJO class was successfully un-registered.
	 * @see #register(Class)
	 */
	public static boolean unregister(Class<?> annotatedClass) {
		return registeredAnnotatedClasses.remove(annotatedClass);
	}

	/**
	 * Initialization method for the logger used to log important messages from this initializer.
	 *
	 * @return a Apache Commons Log used to log messages from this initializer
	 * @see org.apache.commons.logging.LogFactory#getLog(Class)
	 * @see org.apache.commons.logging.Log
	 */
	protected Logger initLogger() {
		return LoggerFactory.getLogger(getClass());
	}

	private boolean isConfigurable(Collection<Class<?>> annotatedClasses, String[] basePackages,
			String[] contextConfigLocations) {

		return !(CollectionUtils.isEmpty(annotatedClasses)
			&& ObjectUtils.isEmpty(basePackages)
			&& ObjectUtils.isEmpty(contextConfigLocations));
	}

	/**
	 * Creates (constructs and configures) an instance of the ConfigurableApplicationContext based on either the
	 * specified base packages containing @Configuration, @Component or JSR 330 annotated classes to scan, or the
	 * specified locations of context configuration meta-data files.  The created ConfigurableApplicationContext
	 * is not automatically "refreshed" and therefore must be "refreshed" by the caller manually.
	 *
	 * When basePackages are specified, an instance of AnnotationConfigApplicationContext is constructed and a scan
	 * is performed; otherwise an instance of the ClassPathXmlApplicationContext is initialized with the
	 * configLocations.  This method prefers the ClassPathXmlApplicationContext to the
	 * AnnotationConfigApplicationContext when both basePackages and configLocations are specified.
	 *
	 * @param basePackages the base packages to scan for application @Components and @Configuration classes.
	 * @param configLocations a String array indicating the locations of the context configuration meta-data files
	 * used to configure the ClassPathXmlApplicationContext instance.
	 * @return an instance of ConfigurableApplicationContext configured and initialized with either configLocations
	 * or the basePackages when configLocations is unspecified.  Note, the "refresh" method must be called manually
	 * before using the context.
	 * @throws IllegalArgumentException if both the basePackages and configLocation parameter arguments
	 * are null or empty.
	 * @see #newApplicationContext(String[])
	 * @see AnnotationConfigApplicationContext
	 * @see AnnotationConfigApplicationContext#scan(String...)
	 * @see ClassPathXmlApplicationContext
	 */
	protected ConfigurableApplicationContext createApplicationContext(String[] basePackages, String[] configLocations) {

		String message = "'AnnotatedClasses', 'basePackages' or 'configLocations' must be specified"
			+ " in order to construct and configure an instance of the ConfigurableApplicationContext";

		Assert.isTrue(isConfigurable(registeredAnnotatedClasses, basePackages, configLocations), message);

		Class<?>[] annotatedClasses = registeredAnnotatedClasses.toArray(new Class<?>[0]);

		ConfigurableApplicationContext applicationContext = newApplicationContext(configLocations);

		return scanBasePackages(registerAnnotatedClasses(applicationContext, annotatedClasses), basePackages);
	}

	ConfigurableApplicationContext newApplicationContext(String[] configLocations) {

		return ObjectUtils.isEmpty(configLocations)
			? new AnnotationConfigApplicationContext()
			: new ClassPathXmlApplicationContext(configLocations, false);
	}

	/**
	 * Initializes the given ApplicationContext by registering this SpringContextBootstrappingInitializer as an
	 * ApplicationListener and registering a runtime shutdown hook.
	 *
	 * @param applicationContext the ConfigurableApplicationContext to initialize.
	 * @return the initialized ApplicationContext.
	 * @see ConfigurableApplicationContext
	 * @see ConfigurableApplicationContext#addApplicationListener(ApplicationListener)
	 * @see ConfigurableApplicationContext#registerShutdownHook()
	 * @throws IllegalArgumentException if the ApplicationContext reference is null!
	 */
	protected ConfigurableApplicationContext initApplicationContext(ConfigurableApplicationContext applicationContext) {

		Assert.notNull(applicationContext, "ConfigurableApplicationContext must not be null");

		applicationContext.addApplicationListener(this);
		applicationContext.registerShutdownHook();

		return setClassLoader(applicationContext);
	}

	/**
	 * Refreshes the given ApplicationContext making the context active.
	 *
	 * @param applicationContext the ConfigurableApplicationContext to refresh.
	 * @return the refreshed ApplicationContext.
	 * @see ConfigurableApplicationContext
	 * @see ConfigurableApplicationContext#refresh()
	 * @throws IllegalArgumentException if the ApplicationContext reference is null!
	 */
	protected ConfigurableApplicationContext refreshApplicationContext(ConfigurableApplicationContext applicationContext) {

		Assert.notNull(applicationContext, "ConfigurableApplicationContext must not be null");

		applicationContext.refresh();

		return applicationContext;
	}

	/**
	 * Registers the given Spring annotated (@Configuration) POJO classes with the specified
	 * AnnotationConfigApplicationContext.
	 *
	 * @param applicationContext the AnnotationConfigApplicationContext used to register the Spring annotated,
	 * POJO classes.
	 * @param annotatedClasses a Class array of Spring annotated (@Configuration) classes used to configure
	 * and initialize the Spring AnnotationConfigApplicationContext.
	 * @return the given AnnotationConfigApplicationContext.
	 * @see AnnotationConfigApplicationContext#register(Class[])
	 */
	ConfigurableApplicationContext registerAnnotatedClasses(ConfigurableApplicationContext applicationContext,
			Class<?>[] annotatedClasses) {

		return applicationContext instanceof AnnotationConfigApplicationContext && !ObjectUtils.isEmpty(annotatedClasses)
			? doRegister(applicationContext, annotatedClasses)
			: applicationContext;
	}

	ConfigurableApplicationContext doRegister(ConfigurableApplicationContext applicationContext,
			Class<?>[] annotatedClasses) {

		((AnnotationConfigApplicationContext) applicationContext).register(annotatedClasses);

		return applicationContext;
	}

	/**
	 * Configures classpath component scanning using the specified base packages on the specified
	 * AnnotationConfigApplicationContext.
	 *
	 * @param applicationContext the AnnotationConfigApplicationContext to setup with classpath component scanning
	 * using the specified base packages.
	 * @param basePackages an array of Strings indicating the base packages to use in the classpath component scan.
	 * @return the given AnnotationConfigApplicationContext.
	 * @see AnnotationConfigApplicationContext#scan(String...)
	 */
	ConfigurableApplicationContext scanBasePackages(ConfigurableApplicationContext applicationContext,
			String[] basePackages) {

		return applicationContext instanceof AnnotationConfigApplicationContext && !ObjectUtils.isEmpty(basePackages)
			? doScan(applicationContext, basePackages)
			: applicationContext;
	}

	ConfigurableApplicationContext doScan(ConfigurableApplicationContext applicationContext, String[] basePackages) {

		((AnnotationConfigApplicationContext) applicationContext).scan(basePackages);

		return applicationContext;
	}

	/**
	 * Sets the ClassLoader used to load bean definition classes on the Spring ApplicationContext.
	 *
	 * @param applicationContext the Spring ApplicationContext in which to configure the ClassLoader.
	 * @return the given Spring ApplicationContext.
	 * @see DefaultResourceLoader#setClassLoader(ClassLoader)
	 * @see ClassLoader
	 */
	ConfigurableApplicationContext setClassLoader(ConfigurableApplicationContext applicationContext) {

		ClassLoader beanClassLoader = beanClassLoaderReference.get();

		if (applicationContext instanceof DefaultResourceLoader && beanClassLoader != null) {
			((DefaultResourceLoader) applicationContext).setClassLoader(beanClassLoader);
		}

		return applicationContext;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void init(Properties parameters) {
		init(CacheUtils.getCache(), parameters);
	}

	/**
	 * Initializes a Spring {@link ApplicationContext} with the given parameters specified with an Apache Geode
	 * or Pivotal GemFire &lt;initializer&gt; block in {@literal cache.xml}.
	 *
	 * @param parameters {@link Properties} object containing the configuration parameters and settings defined in the
	 * Apache Geode/Pivotal GemFire {@literal cache.xml} &lt;initializer&gt; block for the declared
	 * {@link SpringContextBootstrappingInitializer} Apache Geode/Pivotal GemFire {@link Declarable} object.
	 * @param cache reference to the peer {@link Cache}.
	 * @throws ApplicationContextException if the Spring {@link ApplicationContext}
	 * could not be successfully constructed, configured and initialized.
	 * @see #createApplicationContext(String[], String[])
	 * @see #initApplicationContext(ConfigurableApplicationContext)
	 * @see #refreshApplicationContext(ConfigurableApplicationContext)
	 * @see Properties
	 */
	public void init(Cache cache, Properties parameters) {

		try {
			synchronized (SpringContextBootstrappingInitializer.class) {
				if (isApplicationContextInitializable()) {

					String basePackages = parameters.getProperty(BASE_PACKAGES_PARAMETER);
					String contextConfigLocations = parameters.getProperty(CONTEXT_CONFIG_LOCATIONS_PARAMETER);

					String[] basePackagesArray = StringUtils.delimitedListToStringArray(
						StringUtils.trimWhitespace(basePackages), COMMA_DELIMITER, CHARS_TO_DELETE);

					String[] contextConfigLocationsArray = StringUtils.delimitedListToStringArray(
						StringUtils.trimWhitespace(contextConfigLocations), COMMA_DELIMITER, CHARS_TO_DELETE);

					ConfigurableApplicationContext localApplicationContext =
						refreshApplicationContext(initApplicationContext(createApplicationContext(basePackagesArray,
							contextConfigLocationsArray)));

					Assert.state(localApplicationContext.isRunning(), String.format(
						"The Spring ApplicationContext (%1$s) failed to be properly initialized with the context config files (%2$s) or base packages (%3$s)",
							nullSafeGetApplicationContextId(localApplicationContext), Arrays.toString(contextConfigLocationsArray),
								Arrays.toString(basePackagesArray)));

					applicationContext = localApplicationContext;
				}
			}
		}
		catch (Throwable cause) {
			String message = "Failed to bootstrap the Spring ApplicationContext";
			logger.error(message, cause);
			throw new ApplicationContextException(message, cause);
		}
	}

	private static boolean isApplicationContextInitializable() {
		return applicationContext == null || !applicationContext.isActive();
	}

	/**
	 * Null-safe operation used to get the ID of the Spring ApplicationContext.
	 *
	 * @param applicationContext the Spring ApplicationContext from which to get the ID.
	 * @return the ID of the given Spring ApplicationContext or null if the ApplicationContext reference is null.
	 * @see ApplicationContext#getId()
	 */
	String nullSafeGetApplicationContextId(ApplicationContext applicationContext) {
		return applicationContext != null ? applicationContext.getId() : null;
	}

	/**
	 * Gets notified when the Spring ApplicationContext gets created and refreshed by GemFire, once the
	 * &lt;initializer&gt; block is processed and the SpringContextBootstrappingInitializer Declarable component
	 * is initialized.  This handler method proceeds in notifying any other GemFire components that need to be aware
	 * that the Spring ApplicationContext now exists and is ready for use, such as other Declarable GemFire objects
	 * requiring auto-wiring support, etc.
	 *
	 * In addition, this method handles the ContextClosedEvent by removing the ApplicationContext reference.
	 *
	 * @param event the ApplicationContextEvent signaling that the Spring ApplicationContext has been created
	 * and refreshed by GemFire, or closed when the JVM process exits.
	 * @see ContextClosedEvent
	 * @see ContextRefreshedEvent
	 * @see ApplicationEventMulticaster
	 *  #multicastEvent(org.springframework.context.ApplicationEvent)
	 */
	@Override
	public void onApplicationEvent(ApplicationContextEvent event) {

		if (event instanceof ContextRefreshedEvent) {
			synchronized (applicationEventNotifier) {
				contextRefreshedEvent = (ContextRefreshedEvent) event;
				applicationEventNotifier.multicastEvent(event);
			}
		}
		else if (event instanceof ContextClosedEvent) {
			synchronized (applicationEventNotifier) {
				contextRefreshedEvent = null;
			}
		}
	}
}
