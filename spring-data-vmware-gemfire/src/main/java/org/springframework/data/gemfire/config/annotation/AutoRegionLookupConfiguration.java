/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.config.annotation.support.AbstractAnnotationConfigSupport;
import org.springframework.data.gemfire.config.support.AutoRegionLookupBeanPostProcessor;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.springframework.expression.spel.support.StandardTypeLocator;
import org.springframework.util.StringUtils;

/**
 * The {@link AutoRegionLookupConfiguration} class is a Spring {@link ImportBeanDefinitionRegistrar} that enables
 * the automatic lookup of GemFire Regions, which may have been defined else where, such as in {@literal cache.xml}
 * or using GemFire's Cluster Configuration Service.
 *
 * This registrar works by registering the {@link AutoRegionLookupBeanPostProcessor} in the Spring application context,
 * which is enabled when a Spring {@link org.springframework.context.annotation.Configuration @Configuration} annotated
 * GemFire cache application class is annotated with {@link EnableAutoRegionLookup}.
 *
 * @author John Blum
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.beans.factory.support.AbstractBeanDefinition
 * @see org.springframework.beans.factory.support.BeanDefinitionBuilder
 * @see org.springframework.beans.factory.support.BeanDefinitionRegistry
 * @see org.springframework.context.annotation.ImportBeanDefinitionRegistrar
 * @see org.springframework.core.env.Environment
 * @see org.springframework.core.type.AnnotationMetadata
 * @see org.springframework.data.gemfire.config.annotation.EnableAutoRegionLookup
 * @see org.springframework.data.gemfire.config.annotation.support.AbstractAnnotationConfigSupport
 * @see org.springframework.data.gemfire.config.support.AutoRegionLookupBeanPostProcessor
 * @see org.springframework.expression.ExpressionParser
 * @since 1.9.0
 */
public class AutoRegionLookupConfiguration extends AbstractAnnotationConfigSupport
		implements ImportBeanDefinitionRegistrar {

	private static final boolean DEFAULT_ENABLED = true;

	private static final AtomicBoolean AUTO_REGION_LOOKUP_BEAN_POST_PROCESSOR_REGISTERED = new AtomicBoolean(false);

	private ExpressionParser spelParser = new SpelExpressionParser();

	private StandardEvaluationContext evaluationContext = new StandardEvaluationContext();

	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return EnableAutoRegionLookup.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

		super.setBeanFactory(beanFactory);

		this.evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));

		if (beanFactory instanceof ConfigurableBeanFactory) {

			ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;

			this.evaluationContext.setTypeLocator(new StandardTypeLocator(configurableBeanFactory.getBeanClassLoader()));

			Optional.ofNullable(configurableBeanFactory.getConversionService())
				.ifPresent(conversionService ->
					this.evaluationContext.setTypeConverter(new StandardTypeConverter(conversionService)));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

		AnnotationAttributes enableAutoRegionLookupAttributes = getAnnotationAttributes(importingClassMetadata);

		Optional.ofNullable(resolveProperty(cacheProperty("enable-auto-region-lookup"),
			resolveProperty(propertyName("enable-auto-region-lookup"),
				enableAutoRegionLookupAttributes.getBoolean("enabled"))))
			.filter(Boolean.TRUE::equals)
			.ifPresent(enabled -> registerAutoRegionLookupBeanPostProcessor(registry));
	}

	/**
	 * This method is used to support Spring property placeholders and SpEL Expressions
	 * in the {@link EnableAutoRegionLookup#enabled()} attribute.  However, this requires the attribute to be of type
	 * {@link String}, which violates type-safety.  We are favoring type-safety over configuration flexibility
	 * and offering alternative means to achieve flexible and dynamic configuration, e.g. properties
	 * from an {@literal application.properties} file.
	 */
	@SuppressWarnings("unused")
	private boolean isEnabled(String enabled) {

		enabled = StringUtils.trimWhitespace(enabled);

		if (!Boolean.parseBoolean(enabled)) {
			try {
				// try parsing as a SpEL expression...
				return Boolean.TRUE.equals(this.spelParser.parseExpression(enabled)
					.getValue(this.evaluationContext, Boolean.TYPE));
			}
			catch (EvaluationException ignore) {
				return false;
			}
			catch (ParseException ignore) {
				// try resolving as a Spring property placeholder expression...
				return getEnvironment().getProperty(enabled, Boolean.TYPE, false);
			}
		}

		return DEFAULT_ENABLED;
	}

	private void registerAutoRegionLookupBeanPostProcessor(BeanDefinitionRegistry registry) {

		if (AUTO_REGION_LOOKUP_BEAN_POST_PROCESSOR_REGISTERED.compareAndSet(false, true)) {

			AbstractBeanDefinition autoRegionLookupBeanPostProcessor = BeanDefinitionBuilder
				.rootBeanDefinition(AutoRegionLookupBeanPostProcessor.class)
				.setRole(AbstractBeanDefinition.ROLE_INFRASTRUCTURE)
				.getBeanDefinition();

			BeanDefinitionReaderUtils.registerWithGeneratedName(autoRegionLookupBeanPostProcessor, registry);
		}
	}
}
