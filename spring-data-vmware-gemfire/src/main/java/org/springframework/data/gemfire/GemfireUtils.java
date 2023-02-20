/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import org.apache.geode.cache.CacheFactory;

import org.w3c.dom.Element;

import org.springframework.data.gemfire.config.support.GemfireFeature;
import org.springframework.data.gemfire.util.RegionUtils;
import org.springframework.util.ClassUtils;

/**
 * {@link GemfireUtils} is an abstract utility class encapsulating common functionality for accessing features
 * and capabilities of Apache Geode based on version as well as other configuration meta-data.
 *
 * @author John Blum
 * @see org.apache.geode.cache.CacheFactory
 * @see org.springframework.data.gemfire.config.support.GemfireFeature
 * @see org.springframework.data.gemfire.util.RegionUtils
 * @since 1.3.3
 */
@SuppressWarnings("unused")
public abstract class GemfireUtils extends RegionUtils {
  public final static String GEMFIRE_NAME = "VMware GemFire";
  public final static String GEMFIRE_VERSION = CacheFactory.getVersion();
  private static final String ASYNC_EVENT_QUEUE_ELEMENT_NAME = "async-event-queue";
  private static final String ASYNC_EVENT_QUEUE_TYPE_NAME =
      "org.apache.geode.cache.asyncqueue.AsyncEventQueue";
  private static final String CQ_ELEMENT_NAME = "cq-listener-container";
  private static final String CQ_TYPE_NAME = "org.apache.geode.cache.query.CqQuery";
  private static final String GATEWAY_RECEIVER_ELEMENT_NAME = "gateway-receiver";
  private static final String GATEWAY_RECEIVER_TYPE_NAME =
      "org.apache.geode.cache.wan.GatewayReceiverFactory";
  private static final String GATEWAY_SENDER_ELEMENT_NAME = "gateway-sender";
  private static final String GATEWAY_SENDER_TYPE_NAME =
      "org.apache.geode.cache.wan.GatewaySenderFactory";

  public static String gemFireProductName() {
    return GEMFIRE_NAME;
  }

  public static String gemFireVersion() {
    return GEMFIRE_VERSION;
  }

	public static boolean isClassAvailable(String fullyQualifiedClassName) {
		return ClassUtils.isPresent(fullyQualifiedClassName, GemfireUtils.class.getClassLoader());
	}

	public static boolean isGemfireFeatureAvailable(GemfireFeature feature) {

		boolean featureAvailable = (!GemfireFeature.AEQ.equals(feature) || isAsyncEventQueueAvailable());

		featureAvailable &= (!GemfireFeature.CONTINUOUS_QUERY.equals(feature) || isContinuousQueryAvailable());
		featureAvailable &= (!GemfireFeature.WAN.equals(feature) || isGatewayAvailable());

		return featureAvailable;
	}

	public static boolean isGemfireFeatureAvailable(Element element) {

		boolean featureAvailable = (!isAsyncEventQueue(element) || isAsyncEventQueueAvailable());

		featureAvailable &= (!isContinuousQuery(element) || isContinuousQueryAvailable());
		featureAvailable &= (!isGateway(element) || isGatewayAvailable());

		return featureAvailable;
	}

	public static boolean isGemfireFeatureUnavailable(GemfireFeature feature) {
		return !isGemfireFeatureAvailable(feature);
	}

	public static boolean isGemfireFeatureUnavailable(Element element) {
		return !isGemfireFeatureAvailable(element);
	}

	private static boolean isAsyncEventQueue(Element element) {
		return ASYNC_EVENT_QUEUE_ELEMENT_NAME.equals(element.getLocalName());
	}

	private static boolean isAsyncEventQueueAvailable() {
		return isClassAvailable(ASYNC_EVENT_QUEUE_TYPE_NAME);
	}

	private static boolean isContinuousQuery(Element element) {
		return CQ_ELEMENT_NAME.equals(element.getLocalName());
	}

	private static boolean isContinuousQueryAvailable() {
		return isClassAvailable(CQ_TYPE_NAME);
	}

	private static boolean isGateway(Element element) {

		String elementLocalName = element.getLocalName();

		return (GATEWAY_RECEIVER_ELEMENT_NAME.equals(elementLocalName)
			|| GATEWAY_SENDER_ELEMENT_NAME.equals(elementLocalName));
	}

	private static boolean isGatewayAvailable() {
		return isClassAvailable(GATEWAY_SENDER_TYPE_NAME);
	}

	public static void main(final String... args) {
		System.out.printf("Product Name [%1$s] Version [%2$s]%n", GEMFIRE_NAME, GEMFIRE_VERSION);
	}
}
