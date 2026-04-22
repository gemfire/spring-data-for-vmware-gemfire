/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-01: Replaced Apache Geode Region with GudRegion
 */

package org.springframework.data.gemfire.tests.mock.beans.factory.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.Test;

import org.springframework.data.gemfire.gud.api.GudRegion;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Unit Tests for {@link RegionSpyingBeanPostProcessor}.
 *
 * @author John Blum
 * @see Test
 * @see org.mockito.Mockito
 * @see GudRegion
 * @see RegionSpyingBeanPostProcessor
 * @since 0.0.22
 */
public class RegionSpyingBeanPostProcessorUnitTests {

	@Test
	public void spiesOnAllRegions() {

		GudRegion<?, ?> mockRegionOne = mock(GudRegion.class, "MockRegionOne");
		GudRegion<?, ?> mockRegionTwo = mock(GudRegion.class, "MockRegionTwo");

		RegionSpyingBeanPostProcessor beanPostProcessor = spy(new RegionSpyingBeanPostProcessor());

		doAnswer(invocation -> invocation.getArgument(0)).when(beanPostProcessor).doSpy(any());

		assertThat(beanPostProcessor).isNotNull();

		assertThat(beanPostProcessor.postProcessAfterInitialization(mockRegionOne, "MockRegionOne"))
			.isEqualTo(mockRegionOne);

		assertThat(beanPostProcessor.postProcessAfterInitialization(mockRegionTwo, "MockRegionTwo"))
			.isEqualTo(mockRegionTwo);

		verify(beanPostProcessor, times(1)).doSpy(eq(mockRegionOne));
		verify(beanPostProcessor, times(1)).doSpy(eq(mockRegionTwo));

		verifyNoInteractions(mockRegionOne, mockRegionTwo);
	}

	@Test
	public void spiesOnTargetedRegions() {

		GudRegion<?, ?> mockRegionOne = mock(GudRegion.class, "MockRegionOne");
		GudRegion<?, ?> mockRegionTwo = mock(GudRegion.class, "MockRegionTwo");

		RegionSpyingBeanPostProcessor beanPostProcessor =
			spy(new RegionSpyingBeanPostProcessor("MockRegionOne"));

		doAnswer(invocation -> invocation.getArgument(0)).when(beanPostProcessor).doSpy(any());

		assertThat(beanPostProcessor).isNotNull();

		assertThat(beanPostProcessor.postProcessAfterInitialization(mockRegionOne, "MockRegionOne"))
			.isEqualTo(mockRegionOne);

		assertThat(beanPostProcessor.postProcessAfterInitialization(mockRegionTwo, "MockRegionTwo"))
			.isEqualTo(mockRegionTwo);

		verify(beanPostProcessor, times(1)).doSpy(eq(mockRegionOne));
		verify(beanPostProcessor, never()).doSpy(eq(mockRegionTwo));

		verifyNoInteractions(mockRegionOne, mockRegionTwo);
	}

	@Test
	public void willNotSpyOnNonRegion() {

		Object bean = "TEST";

		RegionSpyingBeanPostProcessor beanPostProcessor = spy(new RegionSpyingBeanPostProcessor());

		assertThat(beanPostProcessor.postProcessAfterInitialization(bean, "TestBeanName")).isEqualTo(bean);

		verify(beanPostProcessor, never()).doSpy(any());
	}

	@Test
	public void doSpyIsNullSafe() {
		assertThat(new RegionSpyingBeanPostProcessor().<Object>doSpy(null)).isNull();
	}

	@Test
	public void doSpySpiesOnNonNullObject() {

		User jonDoe = User.as("Jon Doe").identifiedBy(1L);
		User jonDoeSpy = new RegionSpyingBeanPostProcessor().doSpy(jonDoe);

		assertThat(jonDoeSpy).isNotNull();
		assertThat(jonDoeSpy).isInstanceOf(User.class);
		assertThat(jonDoeSpy).isNotSameAs(jonDoe);
		assertThat(jonDoeSpy.getName()).isEqualTo(jonDoe.getName());
	}

	@Test
	public void isRegionReturnsTrue() {
		assertThat(new RegionSpyingBeanPostProcessor().isGudRegion(mock(GudRegion.class))).isTrue();
	}

	@Test
	public void isRegionReturnsFalse() {
		assertThat(new RegionSpyingBeanPostProcessor().isGudRegion("TEST")).isFalse();
	}

	@Test
	public void isRegionBeanNameMatchReturnsTrue() {

		assertThat(new RegionSpyingBeanPostProcessor().isGudRegionBeanNameMatch("TestRegionBeanName")).isTrue();

		assertThat(new RegionSpyingBeanPostProcessor("TestRegionBeanName")
			.isGudRegionBeanNameMatch("TestRegionBeanName")).isTrue();
	}

	@Test
	public void isRegionBeanNameMatchReturnsFalse() {

		assertThat(new RegionSpyingBeanPostProcessor("TestRegionBeanName")
			.isGudRegionBeanNameMatch("MockRegionBeanName")).isFalse();
	}

	@Getter
	@ToString(of = "name")
	@EqualsAndHashCode(of = "name")
	@RequiredArgsConstructor(staticName = "as")
	static class User {

		private Long id;

		@NonNull
		private final String name;

		public User identifiedBy(Long id) {
			this.id = id;
			return this;
		}
	}
}
