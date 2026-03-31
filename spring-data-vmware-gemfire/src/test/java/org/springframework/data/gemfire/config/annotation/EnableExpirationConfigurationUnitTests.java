/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.data.gemfire.config.annotation.EnableExpiration.ExpirationPolicy;
import static org.springframework.data.gemfire.config.annotation.EnableExpiration.ExpirationType;
import org.springframework.data.gemfire.gud.api.GudCustomExpiry;
import org.springframework.data.gemfire.gud.api.GudExpirationAction;
import org.springframework.data.gemfire.gud.api.GudExpirationAttributes;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.junit.After;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.expiration.ExpirationActionType;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.data.gemfire.util.ArrayUtils;

/**
 * Unit Tests for the {@link EnableExpiration} annotation and {@link ExpirationConfiguration} class.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.GudCustomExpiry
 * @see org.apache.geode.cache.GudExpirationAttributes
 * @see org.apache.geode.cache.client.GudClientCache
 * @see org.apache.geode.cache.GudRegion
 * @see org.springframework.context.ConfigurableApplicationContext
 * @see org.springframework.context.annotation.AnnotationConfigApplicationContext
 * @see org.springframework.data.gemfire.config.annotation.EnableExpiration
 * @see org.springframework.data.gemfire.config.annotation.ExpirationConfiguration
 * @see org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @since 1.9.0
 */
public class EnableExpirationConfigurationUnitTests extends SpringApplicationContextIntegrationTestsSupport {

	@After
	public void cleanupAfterTests() {
		destroyAllGemFireMockObjects();
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private <K, V> void assertRegionExpiration(GudExpirationAttributes expectedExpirationAttributes,
			GudRegion<K, V> region, V... applicationDomainObjects) {

		assertIdleTimeoutExpiration(expectedExpirationAttributes, region, applicationDomainObjects);
		assertTimeToLiveExpiration(expectedExpirationAttributes, region, applicationDomainObjects);
	}

	@SuppressWarnings("unchecked")
	private <K, V> void assertIdleTimeoutExpiration(GudExpirationAttributes expectedExpirationAttributes,
			GudRegion<K, V> region, V... applicationDomainObjects) {

		assertExpiration(expectedExpirationAttributes, region.getAttributes().getCustomEntryIdleTimeout(),
			applicationDomainObjects);
	}

	private <K, V> void assertNoIdleTimeoutExpiration(GudRegion<K, V> region) {
		assertThat(region.getAttributes().getCustomEntryIdleTimeout()).isNull();
	}

	@SuppressWarnings("unchecked")
	private <K, V> void assertTimeToLiveExpiration(GudExpirationAttributes expectedExpirationAttributes,
			GudRegion<K, V> region, V... applicationDomainObjects) {

		assertExpiration(expectedExpirationAttributes, region.getAttributes().getCustomEntryTimeToLive(),
			applicationDomainObjects);
	}

	private <K, V> void assertNoTimeToLiveExpiration(GudRegion<K, V> region) {
		assertThat(region.getAttributes().getCustomEntryTimeToLive()).isNull();
	}

	@SuppressWarnings("unchecked")
	private <K, V> void assertExpiration(GudExpirationAttributes expectedExpirationAttributes,
			GudCustomExpiry<K, V> customExpiry, V... applicationDomainObjects) {

		GudRegion.Entry<K, V> regionEntry = mockRegionEntry(ArrayUtils.getFirst(applicationDomainObjects));

		assertExpiration(customExpiry.getExpiry(regionEntry), expectedExpirationAttributes);
	}

	private void assertExpiration(GudExpirationAttributes actualExpirationAttributes,
			GudExpirationAttributes expectedExpirationAttributes) {

		assertThat(actualExpirationAttributes).isEqualTo(expectedExpirationAttributes);
	}

	@SuppressWarnings("unchecked")
	private <K, V> GudRegion<K, V> getRegion(String beanName) {
		return getBean(beanName, GudRegion.class);
	}

	private  GudExpirationAttributes newExpirationAttributes(int timeout, ExpirationActionType action) {
		return newExpirationAttributes(timeout, action.getExpirationAction());
	}

	private GudExpirationAttributes newExpirationAttributes(int timeout, GudExpirationAction action) {
		return new GudExpirationAttributes(timeout, action);
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void usesDefaultExpirationPolicyConfiguration() {

		newApplicationContext(DefaultExpirationPolicyConfiguration.class);

		GudExpirationAttributes expectedExpiration = newExpirationAttributes(0, ExpirationActionType.INVALIDATE);

		GudRegion one = getRegion("One");
		GudRegion two = getRegion("Two");

		assertIdleTimeoutExpiration(expectedExpiration, one);
		assertIdleTimeoutExpiration(expectedExpiration, two);
		assertTimeToLiveExpiration(expectedExpiration, one);
		assertTimeToLiveExpiration(expectedExpiration, two);
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void usesCustomIdleTimeoutExpirationPolicyConfiguration() {

		newApplicationContext(CustomIdleTimeoutExpirationPolicyConfiguration.class);

		GudExpirationAttributes expectedExpiration = newExpirationAttributes(300, ExpirationActionType.LOCAL_DESTROY);

		GudRegion one = getRegion("One");
		GudRegion two = getRegion("Two");

		assertIdleTimeoutExpiration(expectedExpiration, one);
		assertIdleTimeoutExpiration(expectedExpiration, two);

		assertNoTimeToLiveExpiration(one);
		assertNoTimeToLiveExpiration(two);
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void usesCustomTimeToLiveExpirationPolicyConfiguration() {

		newApplicationContext(CustomTimeToLiveTimeoutExpirationPolicyConfiguration.class);

		GudExpirationAttributes expectedExpiration = newExpirationAttributes(900, ExpirationActionType.LOCAL_INVALIDATE);

		GudRegion one = getRegion("One");
		GudRegion two = getRegion("Two");

		assertTimeToLiveExpiration(expectedExpiration, one);
		assertTimeToLiveExpiration(expectedExpiration, two);

		assertNoIdleTimeoutExpiration(one);
		assertNoIdleTimeoutExpiration(two);
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void usesRegionSpecificExpirationPolicyConfiguration() {

		newApplicationContext(RegionSpecificExpirationPolicyConfiguration.class);

		GudExpirationAttributes expectedIdleTimeout = newExpirationAttributes(180, ExpirationActionType.INVALIDATE);
		GudExpirationAttributes expectedTimeToLive = newExpirationAttributes(360, ExpirationActionType.DESTROY);

		GudRegion one = getRegion("One");
		GudRegion two = getRegion("Two");

		assertIdleTimeoutExpiration(expectedIdleTimeout, one);
		assertNoIdleTimeoutExpiration(two);

		assertNoTimeToLiveExpiration(one);
		assertTimeToLiveExpiration(expectedTimeToLive, two);
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void usesMixedExpirationPolicyConfiguration() {

		newApplicationContext(MixedExpirationPolicyConfiguration.class);

		GudExpirationAttributes expectedIdleTimeout = newExpirationAttributes(60, ExpirationActionType.LOCAL_INVALIDATE);
		GudExpirationAttributes expectedTimeToLive = newExpirationAttributes(600, ExpirationActionType.DESTROY);

		GudRegion one = getRegion("One");
		GudRegion two = getRegion("Two");

		assertIdleTimeoutExpiration(expectedIdleTimeout, one);
		assertNoIdleTimeoutExpiration(two);

		assertTimeToLiveExpiration(expectedTimeToLive, one);
		assertTimeToLiveExpiration(expectedTimeToLive, two);
	}

	@SuppressWarnings("unchecked")
	static <K, V> GudRegion.Entry<K, V> mockRegionEntry(V applicationDomainObject) {

		GudRegion.Entry<K, V> mockRegionEntry = mock(GudRegion.Entry.class);

		when(mockRegionEntry.getValue()).thenReturn(applicationDomainObject);

		return mockRegionEntry;
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@SuppressWarnings("unused")
	static class RegionConfiguration {

		@Bean("One")
		public ClientRegionFactoryBean<Object, Object> regionOne(GudClientCache gemfireCache) {

			ClientRegionFactoryBean<Object, Object> regionOne = new ClientRegionFactoryBean<>();

			regionOne.setCache(gemfireCache);
			regionOne.setClose(false);
			regionOne.setPersistent(false);

			return regionOne;
		}

		@Bean("Two")
		public ClientRegionFactoryBean<Object, Object> regionTwo(GudClientCache gemfireCache) {

			ClientRegionFactoryBean<Object, Object> regionTwo = new ClientRegionFactoryBean<>();

			regionTwo.setCache(gemfireCache);
			regionTwo.setClose(false);
			regionTwo.setPersistent(false);

			return regionTwo;
		}

	}

	@EnableExpiration
	static class DefaultExpirationPolicyConfiguration extends RegionConfiguration { }

	@EnableExpiration(policies = { @ExpirationPolicy(timeout = 300, action = ExpirationActionType.LOCAL_DESTROY) })
	static class CustomIdleTimeoutExpirationPolicyConfiguration extends RegionConfiguration { }

	@EnableExpiration(policies = { @ExpirationPolicy(timeout = 900, action = ExpirationActionType.LOCAL_INVALIDATE, types = ExpirationType.TIME_TO_LIVE) })
	static class CustomTimeToLiveTimeoutExpirationPolicyConfiguration extends RegionConfiguration { }

	@EnableExpiration(policies = {
		@ExpirationPolicy(timeout = 180, action = ExpirationActionType.INVALIDATE, regionNames = "One"),
		@ExpirationPolicy(timeout = 360, action = ExpirationActionType.DESTROY, regionNames = "Two", types = ExpirationType.TIME_TO_LIVE)
	})
	static class RegionSpecificExpirationPolicyConfiguration extends RegionConfiguration { }

	@EnableExpiration(policies = {
		@ExpirationPolicy(timeout = 60, action = ExpirationActionType.LOCAL_INVALIDATE, regionNames = "One", types = ExpirationType.IDLE_TIMEOUT),
		@ExpirationPolicy(timeout = 600, action = ExpirationActionType.DESTROY, types = ExpirationType.TIME_TO_LIVE)
	})
	static class MixedExpirationPolicyConfiguration extends RegionConfiguration { }

}
