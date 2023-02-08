/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.expiration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.CustomExpiry;
import org.apache.geode.cache.ExpirationAction;
import org.apache.geode.cache.ExpirationAttributes;
import org.apache.geode.cache.Region;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.expression.EvaluationException;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the configuration of Annotation-defined expiration policies on {@link Region} entry
 * TTL and TTI custom expiration settings.
 *
 * @author John Blum
 * @see Test
 * @see org.mockito.Mockito
 * @see CustomExpiry
 * @see ExpirationAttributes
 * @see Region
 * @see AnnotationBasedExpiration
 * @see ExpirationAttributesFactoryBean
 * @see IntegrationTestsSupport
 * @see GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see SpringRunner
 * @since 1.7.0
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class AnnotationBasedExpirationConfigurationIntegrationTest extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("genericExpiration")
	private AnnotationBasedExpiration<Object, Object> genericExpiration;

	@Autowired
	@Qualifier("ttiExpiration")
	private AnnotationBasedExpiration<Object, Object> idleTimeoutExpiration;

	@Autowired
	@Qualifier("ttlExpiration")
	private AnnotationBasedExpiration<Object, Object> timeToLiveExpiration;

	@Autowired
	private ExpirationAttributes defaultExpirationAttributes;

	@Autowired
	@Qualifier("Example")
	private Region<Object, Object> example;

	@Before
	public void setup() {

		assertThat(example).isNotNull();
		assertThat(example.getName()).isEqualTo("Example");
		assertThat(example.getFullPath()).isEqualTo(String.format("%1$s%2$s", Region.SEPARATOR, "Example"));
		assertThat(example.getAttributes()).isNotNull();
		assertThat(defaultExpirationAttributes).isNotNull();
		assertThat(defaultExpirationAttributes.getTimeout()).isEqualTo(600);
		assertThat(defaultExpirationAttributes.getAction()).isEqualTo(ExpirationAction.DESTROY);
		assertThat(genericExpiration).isInstanceOf(CustomExpiry.class);
		assertThat(genericExpiration.getDefaultExpirationAttributes()).isNull();
		assertThat(idleTimeoutExpiration).isInstanceOf(CustomExpiry.class);
		assertThat(idleTimeoutExpiration.getDefaultExpirationAttributes()).isNull();
		assertThat(timeToLiveExpiration).isInstanceOf(CustomExpiry.class);
		assertThat(timeToLiveExpiration.getDefaultExpirationAttributes()).isSameAs(defaultExpirationAttributes);
	}

	private void assertExpiration(ExpirationAttributes expected, ExpirationAttributes actual) {
		assertExpiration(actual, expected.getTimeout(), expected.getAction());
	}

	private void assertExpiration(ExpirationAttributes expirationAttributes, int expectedTimeout,
			ExpirationAction expectedAction) {

		assertThat(expirationAttributes).isNotNull();
		assertThat(expirationAttributes.getTimeout()).isEqualTo(expectedTimeout);
		assertThat(expirationAttributes.getAction()).isEqualTo(expectedAction);
	}

	@SuppressWarnings("unchecked")
	private Region.Entry<Object, Object> mockRegionEntry(Object value) {

		Region.Entry<Object, Object> mockRegionEntry = mock(Region.Entry.class, "MockRegionEntry");

		doReturn(value).when(mockRegionEntry).getValue();

		return mockRegionEntry;
	}

	@Test
	public void exampleRegionIdleTimeoutExpirationPolicy() {

		CustomExpiry<Object, Object> expiration = example.getAttributes().getCustomEntryIdleTimeout();

		assertExpiration(expiration.getExpiry(mockRegionEntry(new ApplicationDomainObjectWithTimeToLiveAndGenericExpirationPolicies())),
			60, ExpirationAction.INVALIDATE);
		assertExpiration(expiration.getExpiry(mockRegionEntry(new ApplicationDomainObjectWithIdleTimeoutExpirationPolicy())),
			120, ExpirationAction.INVALIDATE);
		assertExpiration(expiration.getExpiry(mockRegionEntry(new ApplicationDomainObjectWithGenericExpirationPolicy())),
			60, ExpirationAction.DESTROY);
		assertThat(expiration.getExpiry(mockRegionEntry(new ApplicationDomainObjectWithNoExpirationPolicy()))).isNull();
		assertThat(expiration.getExpiry(mockRegionEntry(new RegionEntryTimeToLiveExpirationPolicy()))).isNull();
		assertExpiration(expiration.getExpiry(mockRegionEntry(new RegionEntryIdleTimeoutExpirationPolicy())),
			60, ExpirationAction.INVALIDATE);
		assertExpiration(expiration.getExpiry(mockRegionEntry(new RegionEntryGenericExpirationPolicy())),
			60, ExpirationAction.DESTROY);
	}

	@Test
	public void exampleRegionTimeToLiveExpirationPolicy() {

		CustomExpiry<Object, Object> expiration = example.getAttributes().getCustomEntryTimeToLive();

		assertExpiration(expiration.getExpiry(mockRegionEntry(new ApplicationDomainObjectWithTimeToLiveAndGenericExpirationPolicies())),
			300, ExpirationAction.DESTROY);
		assertExpiration(defaultExpirationAttributes, expiration.getExpiry(mockRegionEntry(new ApplicationDomainObjectWithIdleTimeoutExpirationPolicy())));
		assertExpiration(expiration.getExpiry(mockRegionEntry(new ApplicationDomainObjectWithGenericExpirationPolicy())),
			60, ExpirationAction.DESTROY);
		assertExpiration(defaultExpirationAttributes, expiration.getExpiry(mockRegionEntry(new ApplicationDomainObjectWithNoExpirationPolicy())));
		assertExpiration(expiration.getExpiry(mockRegionEntry(new RegionEntryTimeToLiveExpirationPolicy())),
			60, ExpirationAction.LOCAL_INVALIDATE);
		assertExpiration(defaultExpirationAttributes, expiration.getExpiry(mockRegionEntry(new RegionEntryIdleTimeoutExpirationPolicy())));
		assertExpiration(expiration.getExpiry(mockRegionEntry(new RegionEntryGenericExpirationPolicy())),
			60, ExpirationAction.DESTROY);
	}

	@Test
	public void genericExpirationPolicy() {

		assertExpiration(genericExpiration.getExpiry(mockRegionEntry(new ApplicationDomainObjectWithTimeToLiveAndGenericExpirationPolicies())), 60, ExpirationAction.INVALIDATE);
		assertThat(genericExpiration.getExpiry(mockRegionEntry(new ApplicationDomainObjectWithIdleTimeoutExpirationPolicy()))).isNull();
		assertExpiration(genericExpiration.getExpiry(mockRegionEntry(new ApplicationDomainObjectWithGenericExpirationPolicy())), 60, ExpirationAction.DESTROY);
		assertThat(genericExpiration.getExpiry(mockRegionEntry(new ApplicationDomainObjectWithNoExpirationPolicy()))).isNull();
		assertThat(genericExpiration.getExpiry(mockRegionEntry(new RegionEntryTimeToLiveExpirationPolicy()))).isNull();
		assertThat(genericExpiration.getExpiry(mockRegionEntry(new RegionEntryIdleTimeoutExpirationPolicy()))).isNull();
		assertExpiration(genericExpiration.getExpiry(mockRegionEntry(new RegionEntryGenericExpirationPolicy())), 60, ExpirationAction.DESTROY);
	}

	@Test(expected = EvaluationException.class)
	public void invalidExpirationAction() {

		try {
			genericExpiration.getExpiry(mockRegionEntry(new RegionEntryWithInvalidExpirationAction()));
		}
		catch (EvaluationException expected) {

			assertThat(expected).hasMessage("[%s] is not resolvable as an ExpirationAction(Type)",
				"@expirationProperties['gemfire.region.entry.expiration.invalid.action.string']");
			assertThat(expected).hasCauseInstanceOf(IllegalArgumentException.class);

			throw expected;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidExpirationTimeout() {

		try {
			genericExpiration.getExpiry(mockRegionEntry(new RegionEntryWithInvalidExpirationTimeout()));
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Expiration(timeout = "60", action = "INVALIDATE")
	@TimeToLiveExpiration(timeout = "300", action = "DESTROY")
	static class ApplicationDomainObjectWithTimeToLiveAndGenericExpirationPolicies { }

	@IdleTimeoutExpiration(timeout = "120", action = "INVALIDATE")
	static class ApplicationDomainObjectWithIdleTimeoutExpirationPolicy { }

	@Expiration(timeout = "60", action = "DESTROY")
	static class ApplicationDomainObjectWithGenericExpirationPolicy { }

	static class ApplicationDomainObjectWithNoExpirationPolicy { }

	@TimeToLiveExpiration(timeout = "${gemfire.region.entry.expiration.timeout}",
		action = "${gemfire.region.entry.expiration.action.string}")
	static class RegionEntryTimeToLiveExpirationPolicy { }

	@IdleTimeoutExpiration(timeout = "@expirationProperties['gemfire.region.entry.expiration.timeout']",
		action = "@expirationProperties['gemfire.region.entry.expiration.action.gemfire.type']")
	static class RegionEntryIdleTimeoutExpirationPolicy { }

	@Expiration(timeout = "${gemfire.region.entry.expiration.timeout}",
		action = "@expirationProperties['gemfire.region.entry.expiration.action.spring.type']")
	static class RegionEntryGenericExpirationPolicy { }

	@Expiration(timeout = "${gemfire.region.entry.expiration.timeout}",
		action = "@expirationProperties['gemfire.region.entry.expiration.invalid.action.string']")
	static class RegionEntryWithInvalidExpirationAction { }

	@Expiration(timeout = "${gemfire.region.entry.expiration.invalid.timeout}",
		action = "@expirationProperties['gemfire.region.entry.expiration.action.spring.type']")
	static class RegionEntryWithInvalidExpirationTimeout { }

}
