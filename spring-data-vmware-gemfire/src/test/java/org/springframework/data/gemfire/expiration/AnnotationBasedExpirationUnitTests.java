/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.expiration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.gemfire.expiration.AnnotationBasedExpiration.ExpirationMetaData;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import org.apache.geode.cache.ExpirationAction;
import org.apache.geode.cache.ExpirationAttributes;
import org.apache.geode.cache.Region;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.gemfire.TestUtils;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.TypeLocator;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Unit Tests for {@link AnnotationBasedExpiration}.
 *
 * @author John Blum
 * @see Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.mockito.Spy
 * @see AnnotationBasedExpiration
 * @since 1.7.0
 */
@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
public class AnnotationBasedExpirationUnitTests {

	@BeforeClass @AfterClass
	public static void testSetupAndTearDown() {
		AnnotationBasedExpiration.BEAN_FACTORY_REFERENCE.set(null);
		AnnotationBasedExpiration.EVALUATION_CONTEXT_REFERENCE.set(null);
	}

	private final AnnotationBasedExpiration noDefaultExpiration = new AnnotationBasedExpiration();

	private void assertExpiration(ExpirationAttributes expirationAttributes, int expectedTimeout,
			ExpirationAction expectedAction) {

		assertThat(expirationAttributes).isNotNull();
		assertThat(expirationAttributes.getTimeout()).isEqualTo(expectedTimeout);
		assertThat(expirationAttributes.getAction()).isEqualTo(expectedAction);
	}

	private void assertExpiration(ExpirationMetaData expirationMetaData, int expectedTimeout,
			ExpirationActionType expectedExpirationAction) {

		assertThat(expirationMetaData).isNotNull();
		assertThat(expirationMetaData.timeout()).isEqualTo(expectedTimeout);
		assertThat(expirationMetaData.action()).isEqualTo(expectedExpirationAction);
	}

	@Test
	public void constructUninitializedAnnotationBasedExpirationInstance() {

		AnnotationBasedExpiration expiration = new AnnotationBasedExpiration();

		assertThat(expiration.getDefaultExpirationAttributes()).isNull();
	}

	@Test
	public void constructInitializedAnnotationBasedExpirationInstance() {

		AnnotationBasedExpiration expiration = new AnnotationBasedExpiration(ExpirationAttributes.DEFAULT);

		assertThat(expiration.getDefaultExpirationAttributes()).isEqualTo(ExpirationAttributes.DEFAULT);
	}

	@Test
	public void forIdleTimeoutNoDefaultExpiration() {

		Region.Entry mockRegionEntry = mock(Region.Entry.class, "MockRegionEntry");

		AnnotationBasedExpiration expiration = AnnotationBasedExpiration.forIdleTimeout();

		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithTimeToLiveIdleTimeoutExpiration());
		assertExpiration(expiration.getExpiry(mockRegionEntry), 120, ExpirationAction.LOCAL_INVALIDATE);
		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithTimeToLiveGenericExpiration());
		assertExpiration(expiration.getExpiry(mockRegionEntry), 60, ExpirationAction.INVALIDATE);
		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithNoExpiration());
		assertThat(expiration.getExpiry(mockRegionEntry)).isNull();
		verify(mockRegionEntry, atLeast(3)).getValue();
	}

	@Test
	public void forIdleTimeoutWithDefaultExpiration() {

		Region.Entry mockRegionEntry = mock(Region.Entry.class, "MockRegionEntry");

		ExpirationAttributes defaultExpiration = new ExpirationAttributes(300, ExpirationAction.DESTROY);

		AnnotationBasedExpiration expiration = AnnotationBasedExpiration.forIdleTimeout(defaultExpiration);

		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithTimeToLiveIdleTimeoutExpiration());
		assertExpiration(expiration.getExpiry(mockRegionEntry), 120, ExpirationAction.LOCAL_INVALIDATE);
		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithTimeToLiveGenericExpiration());
		assertExpiration(expiration.getExpiry(mockRegionEntry), 60, ExpirationAction.INVALIDATE);
		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithNoExpiration());
		assertThat(expiration.getExpiry(mockRegionEntry)).isEqualTo(defaultExpiration);
		verify(mockRegionEntry, atLeast(3)).getValue();
	}

	@Test
	public void forTimeToLiveNoDefaultExpiration() {

		Region.Entry mockRegionEntry = mock(Region.Entry.class, "MockRegionEntry");

		AnnotationBasedExpiration expiration = AnnotationBasedExpiration.forTimeToLive();

		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithTimeToLiveIdleTimeoutExpiration());
		assertExpiration(expiration.getExpiry(mockRegionEntry), 300, ExpirationAction.LOCAL_DESTROY);
		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithIdleTimeoutGenericExpiration());
		assertExpiration(expiration.getExpiry(mockRegionEntry), 60, ExpirationAction.INVALIDATE);
		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithNoExpiration());
		assertThat(expiration.getExpiry(mockRegionEntry)).isNull();
		verify(mockRegionEntry, atLeast(3)).getValue();
	}

	@Test
	public void forTimeToLiveWithDefaultExpiration() {

		Region.Entry mockRegionEntry = mock(Region.Entry.class, "MockRegionEntry");

		ExpirationAttributes defaultExpiration = new ExpirationAttributes(300, ExpirationAction.DESTROY);

		AnnotationBasedExpiration expiration = AnnotationBasedExpiration.forTimeToLive(defaultExpiration);

		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithTimeToLiveIdleTimeoutExpiration());
		assertExpiration(expiration.getExpiry(mockRegionEntry), 300, ExpirationAction.LOCAL_DESTROY);
		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithIdleTimeoutGenericExpiration());
		assertExpiration(expiration.getExpiry(mockRegionEntry), 60, ExpirationAction.INVALIDATE);
		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithNoExpiration());
		assertThat(expiration.getExpiry(mockRegionEntry)).isEqualTo(defaultExpiration);
		verify(mockRegionEntry, atLeast(3)).getValue();
	}

	@Test
	public void setAndGetBeanFactory() {

		StandardEvaluationContext mockEvaluationContext =
			mock(StandardEvaluationContext.class, "MockStandardEvaluationContext");

		ConversionService mockConversionService = mock(ConversionService.class, "MockConversionService");

		ConfigurableBeanFactory mockBeanFactory = mock(ConfigurableBeanFactory.class, "MockBeanFactory");

		when(mockBeanFactory.getConversionService()).thenReturn(mockConversionService);
		when(mockBeanFactory.getBeanClassLoader()).thenReturn(Thread.currentThread().getContextClassLoader());

		doAnswer((Answer<Void>) invocation -> {

			BeanResolver beanResolver = invocation.getArgument(0);

			assertThat(beanResolver).isInstanceOf(BeanFactoryResolver.class);
			assertThat(TestUtils.<ConfigurableBeanFactory>readField("beanFactory", beanResolver)).isEqualTo(mockBeanFactory);

			return null;

		}).when(mockEvaluationContext).setBeanResolver(any(BeanResolver.class));

		AnnotationBasedExpiration<Object, Object> annotationBasedExpiration = spy(new AnnotationBasedExpiration<>());

		doReturn(mockEvaluationContext).when(annotationBasedExpiration).newEvaluationContext();

		annotationBasedExpiration.setBeanFactory(mockBeanFactory);

		assertThat(annotationBasedExpiration.getBeanFactory()).isSameAs(mockBeanFactory);

		verify(mockEvaluationContext, times(3)).addPropertyAccessor(any(PropertyAccessor.class));
		verify(mockEvaluationContext, times(1)).setTypeConverter(any(TypeConverter.class));
		verify(mockEvaluationContext, times(1)).setTypeLocator(any(TypeLocator.class));
		verify(mockEvaluationContext, times(1)).setBeanResolver(any(BeanResolver.class));
		verify(mockBeanFactory, times(1)).getConversionService();
		verify(mockBeanFactory, times(1)).getBeanClassLoader();
	}

	@Test(expected = IllegalStateException.class)
	public void getUninitializedBeanFactory() {
		new AnnotationBasedExpiration<>().getBeanFactory();
	}

	@Test
	public void setAndGetDefaultExpirationAttributes() {

		ExpirationAttributes expectedExpirationAttributes = new ExpirationAttributes(120, ExpirationAction.INVALIDATE);

		AnnotationBasedExpiration expiration = new AnnotationBasedExpiration();

		expiration.setDefaultExpirationAttributes(expectedExpirationAttributes);

		assertThat(expiration.getDefaultExpirationAttributes()).isEqualTo(expectedExpirationAttributes);

		expiration.setDefaultExpirationAttributes(null);

		assertThat(expiration.getDefaultExpirationAttributes()).isNull();

		expiration.setDefaultExpirationAttributes(ExpirationAttributes.DEFAULT);

		assertThat(expiration.getDefaultExpirationAttributes()).isEqualTo(ExpirationAttributes.DEFAULT);
	}

	@Test
	public void getExpiryCallsGetExpirationMetaDataOnRegionEntryFollowedByNewExpirationAttributes() {

		ExpirationAttributes expectedExpirationAttributes =
			new ExpirationAttributes(60, ExpirationAction.LOCAL_DESTROY);

		final Region.Entry mockRegionEntry = mock(Region.Entry.class, "MockRegionEntry");

		AnnotationBasedExpiration expiration = new AnnotationBasedExpiration() {

			@Override
			protected ExpirationMetaData getExpirationMetaData(Region.Entry entry) {
				assertThat(entry).isSameAs(mockRegionEntry);
				return ExpirationMetaData.from(expectedExpirationAttributes);
			}

			@Override
			protected ExpirationAttributes newExpirationAttributes(ExpirationMetaData expirationMetaData) {
				assertThat(expirationMetaData.timeout()).isEqualTo(expectedExpirationAttributes.getTimeout());
				assertThat(expirationMetaData.expirationAction()).isEqualTo(expectedExpirationAttributes.getAction());
				return expectedExpirationAttributes;
			}
		};

		assertThat(expiration.getExpiry(mockRegionEntry)).isEqualTo(expectedExpirationAttributes);
	}

	@Test
	public void isExpirationConfiguredWithGenericExpirationBasedRegionEntry() {

		Region.Entry mockRegionEntry = mock(Region.Entry.class, "MockRegionEntry");

		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithGenericExpiration());
		assertThat(noDefaultExpiration.isExpirationConfigured(mockRegionEntry)).isTrue();
		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithTimeToLiveIdleTimeoutGenericExpiration());
		assertThat(noDefaultExpiration.isExpirationConfigured(mockRegionEntry)).isTrue();
		verify(mockRegionEntry, times(2)).getValue();
	}

	@Test
	public void isExpirationConfiguredWithNoGenericExpirationRegionEntry() {

		Region.Entry mockRegionEntry = mock(Region.Entry.class, "MockRegionEntry");

		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithTimeToLiveIdleTimeoutExpiration());
		assertThat(noDefaultExpiration.isExpirationConfigured(mockRegionEntry)).isFalse();
		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithNoExpiration());
		assertThat(noDefaultExpiration.isExpirationConfigured(mockRegionEntry)).isFalse();
		verify(mockRegionEntry, times(2)).getValue();
	}

	@Test
	public void isIdleTimeoutConfiguredWithIdleTimeoutExpirationBasedRegionEntry() {

		Region.Entry mockRegionEntry = mock(Region.Entry.class, "MockRegionEntry");

		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithIdleTimeoutExpiration());
		assertThat(noDefaultExpiration.isIdleTimeoutConfigured(mockRegionEntry)).isTrue();
		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithTimeToLiveIdleTimeoutGenericExpiration());
		assertThat(noDefaultExpiration.isIdleTimeoutConfigured(mockRegionEntry)).isTrue();
		verify(mockRegionEntry, times(2)).getValue();
	}

	@Test
	public void isIdleTimeoutConfiguredWithNoIdleTimeoutExpirationRegionEntry() {

		Region.Entry mockRegionEntry = mock(Region.Entry.class, "MockRegionEntry");

		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithTimeToLiveGenericExpiration());
		assertThat(noDefaultExpiration.isIdleTimeoutConfigured(mockRegionEntry)).isFalse();
		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithNoExpiration());
		assertThat(noDefaultExpiration.isIdleTimeoutConfigured(mockRegionEntry)).isFalse();
		verify(mockRegionEntry, times(2)).getValue();
	}

	@Test
	public void isTimeToLiveConfiguredWithTimeToLiveExpirationBasedRegionEntry() {

		Region.Entry mockRegionEntry = mock(Region.Entry.class, "MockRegionEntry");

		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithTimeToLiveExpiration());
		assertThat(noDefaultExpiration.isTimeToLiveConfigured(mockRegionEntry)).isTrue();
		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithTimeToLiveIdleTimeoutGenericExpiration());
		assertThat(noDefaultExpiration.isTimeToLiveConfigured(mockRegionEntry)).isTrue();
		verify(mockRegionEntry, times(2)).getValue();
	}

	public void isTimeToLiveConfiguredWithNoTimeToLiveExpirationRegionEntry() {

		Region.Entry mockRegionEntry = mock(Region.Entry.class, "MockRegionEntry");

		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithIdleTimeoutGenericExpiration());
		assertThat(noDefaultExpiration.isTimeToLiveConfigured(mockRegionEntry)).isFalse();
		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithNoExpiration());
		assertThat(noDefaultExpiration.isTimeToLiveConfigured(mockRegionEntry)).isFalse();
		verify(mockRegionEntry, times(2)).getValue();
	}

	@Test
	public void getExpirationWithGenericExpirationBasedRegionEntry() {

		Region.Entry mockRegionEntry = mock(Region.Entry.class, "MockRegionEntry");

		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithGenericExpiration());
		assertThat(noDefaultExpiration.getExpiration(mockRegionEntry)).isInstanceOf(Expiration.class);
		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithTimeToLiveIdleTimeoutGenericExpiration());
		assertThat(noDefaultExpiration.getExpiration(mockRegionEntry)).isInstanceOf(Expiration.class);
		verify(mockRegionEntry, times(2)).getValue();
	}

	@Test
	public void getExpirationWithNoGenericExpirationRegionEntry() {

		Region.Entry mockRegionEntry = mock(Region.Entry.class, "MockRegionEntry");

		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithTimeToLiveIdleTimeoutExpiration());
		assertThat(noDefaultExpiration.getExpiration(mockRegionEntry)).isNull();
		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithNoExpiration());
		assertThat(noDefaultExpiration.getExpiration(mockRegionEntry)).isNull();
		verify(mockRegionEntry, times(2)).getValue();
	}

	@Test
	public void getIdleTimeoutWithIdleTimeoutExpirationBasedRegionEntry() {

		Region.Entry mockRegionEntry = mock(Region.Entry.class, "MockRegionEntry");

		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithIdleTimeoutExpiration());
		assertThat(noDefaultExpiration.getIdleTimeout(mockRegionEntry)).isInstanceOf(IdleTimeoutExpiration.class);
		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithTimeToLiveIdleTimeoutGenericExpiration());
		assertThat(noDefaultExpiration.getIdleTimeout(mockRegionEntry)).isInstanceOf(IdleTimeoutExpiration.class);
		verify(mockRegionEntry, times(2)).getValue();
	}

	@Test
	public void getIdleTimeoutWithNoIdleTimeoutExpirationRegionEntry() {

		Region.Entry mockRegionEntry = mock(Region.Entry.class, "MockRegionEntry");

		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithTimeToLiveGenericExpiration());
		assertThat(noDefaultExpiration.getIdleTimeout(mockRegionEntry)).isNull();
		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithNoExpiration());
		assertThat(noDefaultExpiration.getIdleTimeout(mockRegionEntry)).isNull();
		verify(mockRegionEntry, times(2)).getValue();
	}

	@Test
	public void getTimeToLiveWithTimeToLiveExpirationBasedRegionEntry() {

		Region.Entry mockRegionEntry = mock(Region.Entry.class, "MockRegionEntry");

		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithTimeToLiveExpiration());
		assertThat(noDefaultExpiration.getTimeToLive(mockRegionEntry)).isInstanceOf(TimeToLiveExpiration.class);
		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithTimeToLiveIdleTimeoutGenericExpiration());
		assertThat(noDefaultExpiration.getTimeToLive(mockRegionEntry)).isInstanceOf(TimeToLiveExpiration.class);
		verify(mockRegionEntry, times(2)).getValue();
	}

	@Test
	public void getTimeToLiveWithNoTimeToLiveExpirationRegionEntry() {

		Region.Entry mockRegionEntry = mock(Region.Entry.class, "MockRegionEntry");

		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithIdleTimeoutGenericExpiration());
		assertThat(noDefaultExpiration.getTimeToLive(mockRegionEntry)).isNull();
		when(mockRegionEntry.getValue()).thenReturn(new RegionEntryValueWithNoExpiration());
		assertThat(noDefaultExpiration.getTimeToLive(mockRegionEntry)).isNull();
		verify(mockRegionEntry, times(2)).getValue();
	}

	@Test
	public void fromExpiration() {

		ExpirationMetaData expirationMetaData =
			ExpirationMetaData.from(RegionEntryValueWithGenericExpiration.class.getAnnotation(Expiration.class));

		assertExpiration(expirationMetaData, 60, ExpirationActionType.INVALIDATE);
	}

	@Test
	public void fromExpirationIdleTimeout() {

		ExpirationMetaData expirationMetaData =
			ExpirationMetaData.from(RegionEntryValueWithIdleTimeoutExpiration.class.getAnnotation(IdleTimeoutExpiration.class));

		assertExpiration(expirationMetaData, 120, ExpirationActionType.LOCAL_INVALIDATE);
	}

	@Test
	public void fromExpirationTimeToLive() {

		ExpirationMetaData expirationMetaData =
			ExpirationMetaData.from(RegionEntryValueWithTimeToLiveExpiration.class.getAnnotation(TimeToLiveExpiration.class));

		assertExpiration(expirationMetaData, 300, ExpirationActionType.LOCAL_DESTROY);
	}

	@Test
	public void toExpirationAttributes() {

		ExpirationMetaData expirationMetaData = new ExpirationMetaData(90, ExpirationActionType.DESTROY);

		assertExpiration(expirationMetaData.toExpirationAttributes(), expirationMetaData.timeout(),
			expirationMetaData.expirationAction());
	}

	@Expiration(timeout = "60", action = "INVALIDATE")
	@IdleTimeoutExpiration(timeout = "120", action = "LOCAL_INVALIDATE")
	@TimeToLiveExpiration(timeout = "300", action = "LOCAL_DESTROY")
	public static class RegionEntryValueWithTimeToLiveIdleTimeoutGenericExpiration { }

	@IdleTimeoutExpiration(timeout = "120", action = "LOCAL_INVALIDATE")
	@TimeToLiveExpiration(timeout = "300", action = "LOCAL_DESTROY")
	public static class RegionEntryValueWithTimeToLiveIdleTimeoutExpiration { }

	@Expiration(timeout = "60", action = "INVALIDATE")
	@TimeToLiveExpiration(timeout = "300", action = "LOCAL_DESTROY")
	public static class RegionEntryValueWithTimeToLiveGenericExpiration { }

	@TimeToLiveExpiration(timeout = "300", action = "LOCAL_DESTROY")
	public static class RegionEntryValueWithTimeToLiveExpiration { }

	@Expiration(timeout = "60", action = "INVALIDATE")
	@IdleTimeoutExpiration(timeout = "120", action = "LOCAL_INVALIDATE")
	public static class RegionEntryValueWithIdleTimeoutGenericExpiration { }

	@IdleTimeoutExpiration(timeout = "120", action = "LOCAL_INVALIDATE")
	public static class RegionEntryValueWithIdleTimeoutExpiration { }

	@Expiration(timeout = "60", action = "INVALIDATE")
	public static class RegionEntryValueWithGenericExpiration { }

	public static class RegionEntryValueWithNoExpiration { }

}
