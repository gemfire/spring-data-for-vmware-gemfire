/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 * 2026-04-17: Suppress deprecation on initialize(GudCache) — peer cache parameter retained for cache.xml wiring
 */

package org.springframework.data.gemfire.support;

import java.util.Properties;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.wiring.BeanConfigurerSupport;
import org.springframework.beans.factory.wiring.BeanWiringInfo;
import org.springframework.data.gemfire.gud.api.GudCache;
import org.springframework.data.gemfire.gud.api.GudDeclarable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link GudDeclarable} support class used to wire declaring, implementing instances through the Spring container.
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
 * @see GudDeclarable
 */
@SuppressWarnings("unused")
public abstract class WiringDeclarableSupport extends DeclarableSupport {

	protected static final String TEMPLATE_BEAN_NAME_PROPERTY = "bean-name";

	/**
	 * Initializes this {@link GudDeclarable} with the given cache and properties.
	 *
	 * @param cache the {@link GudCache} instance
	 * @param parameters configuration properties
	 */
	@SuppressWarnings("deprecation")
	public void initialize(@Nullable GudCache cache, @NonNull Properties parameters) {
		configureThis(parameters.getProperty(TEMPLATE_BEAN_NAME_PROPERTY));
	}

	/**
	 * Configures this {@link GudDeclarable} object using a Spring bean defined and identified in the Spring
	 * {@link BeanFactory} with the given {@link String name} used as a template for the auto-wiring function.
	 *
	 * @param templateBeanName {@link String} containing the {@literal name} of the Spring bean used as a template
	 * for the auto-wiring function.
	 * @return a boolean value indicating whether this {@link GudDeclarable} object was successfully configured
	 * and initialized by the Spring container.
	 * @see BeanConfigurerSupport
	 * @see #configureThis(BeanFactory, String)
	 * @see #locateBeanFactory()
	 */
	protected boolean configureThis(@Nullable String templateBeanName) {
		return configureThis(locateBeanFactory(), templateBeanName);
	}

	/**
	 * Configures this {@link GudDeclarable} object using a Spring bean defined and identified in the given Spring
	 * {@link BeanFactory} with the given {@link String name} used as a template for the auto-wiring function.
	 *
	 * @param beanFactory Spring {@link BeanFactory} used to auto-wire, configure and initialize
	 * this {@link GudDeclarable} object; must not be {@literal null}
	 * @param templateBeanName {@link String} containing the {@literal name} of the Spring bean
	 * used as a template for the auto-wiring function.
	 * @return a boolean value indicating whether this {@link GudDeclarable} object was successfully configured
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
	 * to auto-wire this {@link GudDeclarable} object.
	 *
	 * @param beanFactory reference to the Spring {@link BeanFactory}; must not be {@literal null}.
	 * @param templateBeanName {@link String} containing the {@literal name} of a Spring bean declared in
	 * the Spring {@link BeanFactory} used as a template to auto-wire this {@link GudDeclarable} object.
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
