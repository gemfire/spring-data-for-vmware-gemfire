/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Annotation;

import org.apache.geode.cache.wan.GatewaySender;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.util.ArrayUtils;

/**
 * Spring {@link Configuration} class used to construct, configure and initialize {@link GatewaySender} instances
 * in a Spring application context.
 *
 * @author Udo Kohlmeyer
 * @author John Blum
 * @see Annotation
 * @see org.apache.geode.cache.wan.GatewayReceiver
 * @see GatewaySender
 * @see Configuration
 * @see AnnotationAttributes
 * @see AnnotationMetadata
 * @see EnableGatewaySender
 * @see EnableGatewaySenders
 * @since 2.2.0
 */
@Configuration
public class GatewaySendersConfiguration extends GatewaySenderConfiguration {

	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return EnableGatewaySenders.class;
	}

	@Override
	public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {

		if (isAnnotationPresent(annotationMetadata)) {

			AnnotationAttributes parentGatewaySendersAnnotation = getAnnotationAttributes(annotationMetadata);

			registerGatewaySenders(parentGatewaySendersAnnotation, registry);
		}
	}

	private void registerGatewaySenders(AnnotationAttributes parentGatewaySendersAnnotation,
			BeanDefinitionRegistry registry) {

		AnnotationAttributes[] gatewaySenderAnnotations =
			parentGatewaySendersAnnotation.getAnnotationArray("gatewaySenders");

		if (ArrayUtils.isNotEmpty(gatewaySenderAnnotations)) {
			for (AnnotationAttributes gatewaySenderAnnotation : gatewaySenderAnnotations) {
				registerGatewaySender(gatewaySenderAnnotation, parentGatewaySendersAnnotation, registry);
			}
		}
		else {
			registerDefaultGatewaySender(parentGatewaySendersAnnotation, registry);
		}
	}

	private void registerDefaultGatewaySender(AnnotationAttributes parentGatewaySendersAnnotation,
			BeanDefinitionRegistry registry) {

		registerGatewaySender("GatewaySender", parentGatewaySendersAnnotation,
			parentGatewaySendersAnnotation, registry);
	}
}
