/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import org.apache.geode.cache.CacheFactory;
import org.springframework.data.gemfire.config.support.GemfireFeature;
import org.springframework.data.gemfire.util.RegionUtils;
import org.springframework.util.ClassUtils;
import org.w3c.dom.Element;

/**
 * {@link GemfireUtils} is an abstract utility class encapsulating common functionality for accessing features
 * and capabilities of Apache Geode based on version as well as other configuration meta-data.
 *
 * @author John Blum
 * @see CacheFactory
 * @see GemfireFeature
 * @see RegionUtils
 * @since 1.3.3
 */
@SuppressWarnings("unused")
public abstract class GemfireUtils extends RegionUtils {

	public final static String GEMFIRE_VERSION = apacheGeodeVersion();
	public final static String UNKNOWN = "unknown";

	private static final String CQ_ELEMENT_NAME = "cq-listener-container";
	private static final String CQ_TYPE_NAME = "org.apache.geode.cache.query.CqQuery";
	public static final String GEMFIRE_PRODUCT_NAME = "VMware GemFire";

	public static String apacheGeodeProductName() {
		return GEMFIRE_PRODUCT_NAME;
	}

	public static String apacheGeodeVersion() {

		try {
			return CacheFactory.getVersion();
		}
		catch (Throwable ignore) {
			return UNKNOWN;
		}
	}

	public static boolean isClassAvailable(String fullyQualifiedClassName) {
		return ClassUtils.isPresent(fullyQualifiedClassName, GemfireUtils.class.getClassLoader());
	}

	public static boolean isGemfireFeatureAvailable(GemfireFeature feature) {

    return (!GemfireFeature.CONTINUOUS_QUERY.equals(feature) || isContinuousQueryAvailable());
	}

	public static boolean isGemfireFeatureAvailable(Element element) {
		return (!isContinuousQuery(element) || isContinuousQueryAvailable());
	}

	public static boolean isGemfireFeatureUnavailable(GemfireFeature feature) {
		return !isGemfireFeatureAvailable(feature);
	}

	public static boolean isGemfireFeatureUnavailable(Element element) {
		return !isGemfireFeatureAvailable(element);
	}

	private static boolean isContinuousQuery(Element element) {
		return CQ_ELEMENT_NAME.equals(element.getLocalName());
	}

	private static boolean isContinuousQueryAvailable() {
		return isClassAvailable(CQ_TYPE_NAME);
	}

	public static void main(final String... args) {
		System.out.printf("Product Name [%1$s] Version [%2$s]%n", GEMFIRE_PRODUCT_NAME, GEMFIRE_VERSION);
	}
}
