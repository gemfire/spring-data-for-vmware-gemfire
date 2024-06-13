/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalArgumentException;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.geode.cache.GemFireCache;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.OrderComparator;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.data.gemfire.config.annotation.support.AbstractAnnotationConfigSupport;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.data.gemfire.util.SpringExtensions;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/**
 * The {@link ApacheShiroSecurityConfiguration} class is a Spring {@link Configuration @Configuration} component
 * responsible for configuring and initializing the Apache Shiro security framework in order to secure Apache Geode
 * administrative and data access operations.
 *
 * @author John Blum
 * @see GemFireCache
 * @see org.apache.geode.internal.security.SecurityService
 * @see DefaultSecurityManager
 * @see Realm
 * @see LifecycleBeanPostProcessor
 * @see BeanFactory
 * @see ListableBeanFactory
 * @see Bean
 * @see Condition
 * @see Conditional
 * @see Configuration
 * @see org.springframework.core.type.AnnotationMetadata
 * @see ApacheShiroPresentCondition
 * @see AbstractAnnotationConfigSupport
 * @since 1.9.0
 */
@Configuration
@Conditional(ApacheShiroSecurityConfiguration.ApacheShiroPresentCondition.class)
@SuppressWarnings("unused")
public class ApacheShiroSecurityConfiguration extends AbstractAnnotationConfigSupport {

	/**
	 * Returns the {@link EnableSecurity} {@link Annotation} {@link Class} type.
	 *
	 * @return the {@link EnableSecurity} {@link Annotation} {@link Class} type.
	 * @see EnableSecurity
	 */
	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return EnableSecurity.class;
	}

	/**
	 * Sets a reference to the Spring {@link BeanFactory}.
	 *
	 * @param beanFactory reference to the Spring {@link BeanFactory}.
	 * @throws IllegalArgumentException if the Spring {@link BeanFactory} is not
	 * an instance of {@link ListableBeanFactory}.
	 * @see BeanFactory
	 */
	@Override
	@SuppressWarnings("all")
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

		super.setBeanFactory(Optional.ofNullable(beanFactory)
			.filter(ListableBeanFactory.class::isInstance)
			.orElseThrow(() -> newIllegalArgumentException("BeanFactory [%1$s] must be an instance of [%2$s]",
				ObjectUtils.nullSafeClassName(beanFactory), ListableBeanFactory.class.getName())));
	}

	/**
	 * Returns a reference to the Spring {@link BeanFactory}.
	 *
	 * @return a reference to the Spring {@link BeanFactory}.
	 * @throws IllegalStateException if the Spring {@link BeanFactory} was not set.
	 * @see BeanFactory
	 */
	protected ListableBeanFactory getListableBeanFactory() {
		return (ListableBeanFactory) getBeanFactory();
	}

	@Bean
	public BeanFactoryPostProcessor shiroGemFireBeanFactoryPostProcessor() {

		return configurableListableBeanFactory -> {

			if (configurableListableBeanFactory.containsBean("gemfireCache")) {
				SpringExtensions.addDependsOn(configurableListableBeanFactory.getBeanDefinition("gemfireCache"),
					"shiroSecurityManager");
			}
		};
	}

	/**
	 * {@link Bean} definition to define, configure and register an Apache Shiro Spring
	 * {@link LifecycleBeanPostProcessor} to automatically call lifecycle callback methods
	 * on Shiro security components during Spring container initialization and destruction phases.
	 *
	 * @return an instance of the Apache Shiro Spring {@link LifecycleBeanPostProcessor} to handle the lifecycle
	 * of Apache Shiro security framework components.
	 * @see LifecycleBeanPostProcessor
	 */
	@Bean
	public BeanPostProcessor shiroLifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	/**
	 * {@link Bean} definition to define, configure and register an Apache Shiro
	 * {@link org.apache.shiro.mgt.SecurityManager} implementation to secure Apache Geode.
	 *
	 * The registration of this {@link Bean} definition is dependent upon whether the user is using Apache Shiro
	 * to secure Apache Geode, which is determined by the presence of Apache Shiro {@link Realm Realms}
	 * declared in the Spring {@link org.springframework.context.ApplicationContext}.
	 *
	 * This {@link Bean} definition declares a dependency on the Apache Geode {@link GemFireCache} instance
	 * in order to ensure the Geode cache is created and initialized first.  This ensures that any internal Geode
	 * security configuration logic is evaluated and processed before SDG attempts to configure Apache Shiro
	 * as Apache Geode's security provider.
	 *
	 * Additionally, this {@link Bean} definition will register the Apache Shiro
	 * {@link org.apache.geode.security.SecurityManager} with the Apache Shiro security framework
	 *
	 * Finally, this method proceeds to enable Apache Geode security.

	 * @return an Apache Shiro {@link org.apache.shiro.mgt.SecurityManager} implementation used to secure Apache Geode.
	 * @throws IllegalStateException if an Apache Shiro {@link org.apache.shiro.mgt.SecurityManager} was registered
	 * with the Apache Shiro security framework but Apache Geode security could not be enabled.
	 * @see org.apache.shiro.mgt.SecurityManager
	 * @see #registerSecurityManager(org.apache.shiro.mgt.SecurityManager)
	 * @see #resolveRealms()
	 */
	@Bean
	public org.apache.shiro.mgt.SecurityManager shiroSecurityManager() {

		return Optional.ofNullable(resolveRealms())
			.filter(realms -> !realms.isEmpty())
			.map(realms -> new DefaultSecurityManager(realms))
			.map(this::registerSecurityManager)
			.orElse(null);
	}

	/**
	 * Resolves all the Apache Shiro {@link Realm Realms} declared and configured as Spring managed beans
	 * in the Spring {@link org.springframework.context.ApplicationContext}.
	 *
	 * This method will order the Realms according to priority order to ensure that the Apache Shiro Realms
	 * are applied in the correct sequence, as declared/configured.
	 *
	 * @return a {@link List} of all Apache Shiro {@link Realm Realms} declared and configured as Spring managed beans
	 * in the Spring {@link org.springframework.context.ApplicationContext}.
	 * @see ListableBeanFactory#getBeansOfType(Class, boolean, boolean)
	 * @see OrderComparator
	 * @see Realm
	 */
	protected List<Realm> resolveRealms() {

		try {

			Map<String, Realm> realmBeans =
				getListableBeanFactory().getBeansOfType(Realm.class, false, false);

			List<Realm> realms = new ArrayList<>(CollectionUtils.nullSafeMap(realmBeans).values());

			realms.sort(OrderComparator.INSTANCE);

			return realms;
		}
		catch (Exception ignore) {
			return Collections.emptyList();
		}
	}

	/**
	 * Registers the given Apache Shiro {@link org.apache.shiro.mgt.SecurityManager} with the Apache Shiro
	 * security framework.
	 *
	 * @param securityManager {@link org.apache.shiro.mgt.SecurityManager} to register.
	 * @return the given {@link org.apache.shiro.mgt.SecurityManager} reference.
	 * @throws IllegalArgumentException if {@link org.apache.shiro.mgt.SecurityManager} is {@literal null}.
	 * @see SecurityUtils#setSecurityManager(org.apache.shiro.mgt.SecurityManager)
	 * @see org.apache.shiro.mgt.SecurityManager
	 */
	protected org.apache.shiro.mgt.SecurityManager registerSecurityManager(
			org.apache.shiro.mgt.SecurityManager securityManager) {

		Assert.notNull(securityManager, "The Apache Shiro SecurityManager to register must not be null");

		SecurityUtils.setSecurityManager(securityManager);

		return securityManager;
	}

	/**
	 * A Spring {@link Condition} to determine whether the user has included (declared) the 'shiro-spring' dependency
	 * on their application's classpath, which is necessary for configuring Apache Shiro to secure Apache Geode
	 * in a Spring context.
	 *
	 * @see Condition
	 */
	public static class ApacheShiroPresentCondition implements Condition {

		protected static final String APACHE_SHIRO_LIFECYCLE_BEAN_POST_PROCESSOR_CLASS_NAME =
			"org.apache.shiro.spring.LifecycleBeanPostProcessor";

		public static final String SPRING_DATA_GEMFIRE_SECURITY_SHIRO_ENABLED =
			"spring.data.gemfire.security.shiro.enabled";

		private boolean isApacheShiroPresent(ConditionContext context) {
			return ClassUtils.isPresent(APACHE_SHIRO_LIFECYCLE_BEAN_POST_PROCESSOR_CLASS_NAME,
				context.getClassLoader());
		}

		private boolean isEnabled(Environment environment) {
			return environment.getProperty(SPRING_DATA_GEMFIRE_SECURITY_SHIRO_ENABLED, Boolean.class, true);
		}

		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			return isEnabled(context.getEnvironment()) && isApacheShiroPresent(context);
		}
	}
}
