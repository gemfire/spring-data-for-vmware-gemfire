// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.support;

import java.util.Properties;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.Declarable;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.wiring.BeanConfigurerSupport;
import org.springframework.beans.factory.wiring.BeanWiringInfo;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link Declarable} support class used to wire declaring, implementing instances through the Spring container.
 *
 * This implementation first looks for a {@literal 'bean-name'} property, which will be used to locate
 * a Spring bean definition used as the 'template' for auto-wiring purposes.  Auto-wiring will be performed
 * based on the settings defined in the Spring container.
 *
 * @author Costin Leau
 * @author John Blum
 * @see BeanFactory
 * @see BeanConfigurerSupport
 * @see BeanWiringInfo
 * @see DeclarableSupport
 * @see LazyWiringDeclarableSupport
 * @see Declarable
 */
@SuppressWarnings("unused")
public abstract class WiringDeclarableSupport extends DeclarableSupport {

	protected static final String TEMPLATE_BEAN_NAME_PROPERTY = "bean-name";

	@Override
	public void initialize(@Nullable Cache cache, @NonNull Properties parameters) {
		configureThis(parameters.getProperty(TEMPLATE_BEAN_NAME_PROPERTY));
	}

	/**
	 * Configures this {@link Declarable} object using a Spring bean defined and identified in the Spring
	 * {@link BeanFactory} with the given {@link String name} used as a template for the auto-wiring function.
	 *
	 * @param templateBeanName {@link String} containing the {@literal name} of the Spring bean used as a template
	 * for the auto-wiring function.
	 * @return a boolean value indicating whether this {@link Declarable} object was successfully configured
	 * and initialized by the Spring container.
	 * @see BeanConfigurerSupport
	 * @see #configureThis(BeanFactory, String)
	 * @see #locateBeanFactory()
	 */
	protected boolean configureThis(@Nullable String templateBeanName) {
		return configureThis(locateBeanFactory(), templateBeanName);
	}

	/**
	 * Configures this {@link Declarable} object using a Spring bean defined and identified in the given Spring
	 * {@link BeanFactory} with the given {@link String name} used as a template for the auto-wiring function.
	 *
	 * @param beanFactory Spring {@link BeanFactory} used to auto-wire, configure and initialize
	 * this {@link Declarable} object; must not be {@literal null}
	 * @param templateBeanName {@link String} containing the {@literal name} of the Spring bean
	 * used as a template for the auto-wiring function.
	 * @return a boolean value indicating whether this {@link Declarable} object was successfully configured
	 * and initialized by the Spring container.
	 * @see BeanConfigurerSupport
	 * @see #newBeanConfigurer(BeanFactory, String)
	 */
	protected boolean configureThis(@NonNull BeanFactory beanFactory, @Nullable String templateBeanName) {

		BeanConfigurerSupport beanConfigurer = newBeanConfigurer(beanFactory, templateBeanName);

		beanConfigurer.configureBean(this);
		beanConfigurer.destroy();

		return true;
	}

	/**
	 * Constructs a new instance of {@link BeanConfigurerSupport} configured with the given Spring {@link BeanFactory}.
	 *
	 * @param beanFactory reference to the Spring {@link BeanFactory}; must not be {@literal null}.
	 * @return a new {@link BeanConfigurerSupport} configured with the given Spring {@link BeanFactory}.
	 * @see BeanConfigurerSupport
	 * @see BeanFactory
	 * @see #newBeanConfigurer(BeanFactory, String)
	 */
	protected @NonNull BeanConfigurerSupport newBeanConfigurer(@NonNull BeanFactory beanFactory) {
		return newBeanConfigurer(beanFactory, null);
	}

	/**
	 * Constructs a new instance of {@link BeanConfigurerSupport} configured with the given Spring {@link BeanFactory}
	 * and {@link String name} of a Spring bean defined in the Spring {@link BeanFactory} used as a template
	 * to auto-wire this {@link Declarable} object.
	 *
	 * @param beanFactory reference to the Spring {@link BeanFactory}; must not be {@literal null}.
	 * @param templateBeanName {@link String} containing the {@literal name} of a Spring bean declared in
	 * the Spring {@link BeanFactory} used as a template to auto-wire this {@link Declarable} object.
	 * @return a new {@link BeanConfigurerSupport} configured with the given Spring {@link BeanFactory}.
	 * @see BeanConfigurerSupport
	 * @see BeanFactory
	 */
	protected @NonNull BeanConfigurerSupport newBeanConfigurer(@NonNull BeanFactory beanFactory,
			@Nullable String templateBeanName) {

		BeanConfigurerSupport beanConfigurer = new BeanConfigurerSupport();

		beanConfigurer.setBeanFactory(beanFactory);

		if (StringUtils.hasText(templateBeanName)) {

			Assert.isTrue(beanFactory.containsBean(templateBeanName),
				String.format("Cannot find bean with name [%s]", templateBeanName));

			beanConfigurer.setBeanWiringInfoResolver(beanInstance -> new BeanWiringInfo(templateBeanName));
		}

		beanConfigurer.afterPropertiesSet();

		return beanConfigurer;
	}
}
