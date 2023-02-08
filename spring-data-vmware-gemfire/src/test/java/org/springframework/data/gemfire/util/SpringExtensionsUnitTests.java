/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.data.gemfire.util.ArrayUtils.asArray;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalStateException;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newRuntimeException;

import java.sql.Time;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.cache.client.Pool;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.gemfire.test.model.Person;
import org.springframework.data.gemfire.util.SpringExtensions.ValueReturningThrowableOperation;

/**
 * Unit Tests for {@link SpringExtensions}.
 *
 * @author John Blum
 * @see Function
 * @see Supplier
 * @see Test
 * @see Mock
 * @see org.mockito.Mockito
 * @see MockitoJUnitRunner
 * @see BeanFactory
 * @see BeanDefinition
 * @see SpringExtensions
 * @since 1.9.0
 */
@RunWith(MockitoJUnitRunner.class)
public class SpringExtensionsUnitTests {

	@Mock
	private BeanDefinition mockBeanDefinition;

	@Test
	public void areNotNullIsNullSafe() {
		assertThat(SpringExtensions.areNotNull((Object[]) null)).isTrue();
	}

	@Test
	public void areNotNullWithAllNullValuesReturnsFalse() {
		assertThat(SpringExtensions.areNotNull(null, null, null)).isFalse();
	}

	@Test
	public void areNotNullWithNoNullValuesReturnsTrue() {
		assertThat(SpringExtensions.areNotNull(1, 2, 3)).isTrue();
	}

	@Test
	public void areNotNullWithOneNullValueIsFalse() {

		assertThat(SpringExtensions.areNotNull(null, 2, 3)).isFalse();
		assertThat(SpringExtensions.areNotNull(1, null, 3)).isFalse();
		assertThat(SpringExtensions.areNotNull(1, 2, null)).isFalse();
	}

	@Test
	public void isMatchingBeanReturnsTrue() {

		BeanFactory mockBeanFactory = mock(BeanFactory.class);

		when(mockBeanFactory.containsBean(anyString())).thenReturn(true);
		when(mockBeanFactory.isTypeMatch(anyString(), any(Class.class))).thenReturn(true);

		assertThat(SpringExtensions.isMatchingBean(mockBeanFactory, "TestPool", Pool.class)).isTrue();

		verify(mockBeanFactory, times(1)).containsBean(eq("TestPool"));
		verify(mockBeanFactory, times(1)).isTypeMatch(eq("TestPool"), eq(Pool.class));
	}

	@Test
	public void isMatchingBeanReturnsFalseWhenBeanFactoryDoesNotContainBeanByName() {

		BeanFactory mockBeanFactory = mock(BeanFactory.class);

		when(mockBeanFactory.containsBean(anyString())).thenReturn(false);

		assertThat(SpringExtensions.isMatchingBean(mockBeanFactory, "TestPool", Pool.class)).isFalse();

		verify(mockBeanFactory, times(1)).containsBean(eq("TestPool"));
		verify(mockBeanFactory, never()).isTypeMatch(anyString(), any(Class.class));
	}

	@Test
	public void isMatchingBeanReturnsFalseWhenBeanIsNotATypeMatch() {

		BeanFactory mockBeanFactory = mock(BeanFactory.class);

		when(mockBeanFactory.containsBean(anyString())).thenReturn(true);
		when(mockBeanFactory.isTypeMatch(anyString(), any(Class.class))).thenReturn(false);

		assertThat(SpringExtensions.isMatchingBean(mockBeanFactory, "TestPool", Pool.class)).isFalse();

		verify(mockBeanFactory, times(1)).containsBean(eq("TestPool"));
		verify(mockBeanFactory, times(1)).isTypeMatch(eq("TestPool"), eq(Pool.class));
	}

	@Test
	public void addDependsOnToExistingDependencies() {

		when(this.mockBeanDefinition.getDependsOn())
			.thenReturn(asArray("testBeanNameOne", "testBeanNameTwo"));

		assertThat(SpringExtensions.addDependsOn(this.mockBeanDefinition, "testBeanNameThree"))
			.isSameAs(this.mockBeanDefinition);

		verify(this.mockBeanDefinition, times(1)).getDependsOn();
		verify(this.mockBeanDefinition, times(1))
			.setDependsOn("testBeanNameOne", "testBeanNameTwo", "testBeanNameThree");
	}

	@Test
	public void addDependsOnToNonExistingDependencies() {

		when(this.mockBeanDefinition.getDependsOn()).thenReturn(null);

		assertThat(SpringExtensions.addDependsOn(this.mockBeanDefinition, "testBeanName"))
			.isSameAs(this.mockBeanDefinition);

		verify(this.mockBeanDefinition, times(1)).getDependsOn();
		verify(this.mockBeanDefinition, times(1)).setDependsOn("testBeanName");
	}

	@Test
	public void addDependsOnWithMultipleDependenciesToExistingDependencies() {

		when(this.mockBeanDefinition.getDependsOn()).thenReturn(asArray("testBeanNameOne", "testBeanNameTwo"));

		assertThat(SpringExtensions.addDependsOn(this.mockBeanDefinition, "testBeanNameThree", "testBeanNameFour"))
			.isSameAs(this.mockBeanDefinition);

		verify(this.mockBeanDefinition, times(1)).getDependsOn();
		verify(this.mockBeanDefinition, times(1))
			.setDependsOn("testBeanNameOne", "testBeanNameTwo", "testBeanNameThree", "testBeanNameFour");
	}

	@Test
	public void getPropertyValueForExistingPropertyHavingValueReturnsValue() {

		MutablePropertyValues propertyValues =
			new MutablePropertyValues(Collections.singletonMap("testProperty", "testValue"));

		when(this.mockBeanDefinition.getPropertyValues()).thenReturn(propertyValues);

		assertThat(SpringExtensions.getPropertyValue(this.mockBeanDefinition, "testProperty").orElse(null))
			.isEqualTo("testValue");

		verify(this.mockBeanDefinition, times(1)).getPropertyValues();
	}

	@Test
	public void getPropertyValueForExistingPropertyHavingNullValueReturnsNull() {

		MutablePropertyValues testPropertyValues = spy(new MutablePropertyValues());

		PropertyValue testPropertyValue = spy(new PropertyValue("testProperty", null));

		when(this.mockBeanDefinition.getPropertyValues()).thenReturn(testPropertyValues);
		doReturn(testPropertyValue).when(testPropertyValues).getPropertyValue(anyString());

		assertThat(SpringExtensions.getPropertyValue(this.mockBeanDefinition, "testProperty").orElse(null))
			.isNull();

		verify(this.mockBeanDefinition, times(1)).getPropertyValues();
		verify(testPropertyValues, times(1)).getPropertyValue(eq("testProperty"));
		verify(testPropertyValue, times(1)).getValue();
	}

	@Test
	public void getPropertyValueForNonExistingPropertyReturnsNull() {

		MutablePropertyValues testPropertyValues = spy(new MutablePropertyValues());

		when(this.mockBeanDefinition.getPropertyValues()).thenReturn(testPropertyValues);

		assertThat(SpringExtensions.getPropertyValue(this.mockBeanDefinition, "testProperty").orElse(null))
			.isNull();

		verify(this.mockBeanDefinition, times(1)).getPropertyValues();
		verify(testPropertyValues, times(1)).getPropertyValue(eq("testProperty"));
	}

	@Test
	public void getPropertyValueWithNullPropertyValuesReturnsNull() {

		when(this.mockBeanDefinition.getPropertyValues()).thenReturn(null);

		assertThat(SpringExtensions.getPropertyValue(this.mockBeanDefinition, "testProperty").orElse(null))
			.isNull();

		verify(this.mockBeanDefinition, times(1)).getPropertyValues();
	}

	@Test
	public void getPropertyValueWithNullBeanDefinitionReturnsNull() {
		assertThat(SpringExtensions.getPropertyValue(null, "testProperty").orElse(null))
			.isNull();
	}

	@Test
	public void setBeanDefinitionPropertyReference() {

		MutablePropertyValues mutablePropertyValues = new MutablePropertyValues();

		when(this.mockBeanDefinition.getPropertyValues()).thenReturn(mutablePropertyValues);

		assertThat(mutablePropertyValues.size()).isEqualTo(0);

		SpringExtensions.setPropertyReference(this.mockBeanDefinition, "testProperty", "testBean");

		assertThat(mutablePropertyValues.size()).isEqualTo(1);
		assertThat(mutablePropertyValues.getPropertyValue("testProperty")).isNotNull();
		assertThat(mutablePropertyValues.getPropertyValue("testProperty").getValue())
			.isInstanceOf(RuntimeBeanReference.class);
		assertThat(((RuntimeBeanReference) mutablePropertyValues.getPropertyValue("testProperty").getValue()).getBeanName())
			.isEqualTo("testBean");
	}

	@Test
	public void setBeanDefinitionPropertyValue() {

		MutablePropertyValues mutablePropertyValues = new MutablePropertyValues();

		when(this.mockBeanDefinition.getPropertyValues()).thenReturn(mutablePropertyValues);

		assertThat(mutablePropertyValues.size()).isEqualTo(0);

		SpringExtensions.setPropertyValue(this.mockBeanDefinition, "testProperty", "testValue");

		assertThat(mutablePropertyValues.size()).isEqualTo(1);
		assertThat(mutablePropertyValues.getPropertyValue("testProperty")).isNotNull();
		assertThat(mutablePropertyValues.getPropertyValue("testProperty").getValue())
			.isEqualTo("testValue");
	}

	@Test
	public void defaultIfEmptyReturnsValue() {

		assertThat(SpringExtensions.defaultIfEmpty("test", "DEFAULT")).isEqualTo("test");
		assertThat(SpringExtensions.defaultIfEmpty("abc123", "DEFAULT")).isEqualTo("abc123");
		assertThat(SpringExtensions.defaultIfEmpty("123", "DEFAULT")).isEqualTo("123");
		assertThat(SpringExtensions.defaultIfEmpty("X", "DEFAULT")).isEqualTo("X");
		assertThat(SpringExtensions.defaultIfEmpty("$", "DEFAULT")).isEqualTo("$");
		assertThat(SpringExtensions.defaultIfEmpty("_", "DEFAULT")).isEqualTo("_");
		assertThat(SpringExtensions.defaultIfEmpty("nil", "DEFAULT")).isEqualTo("nil");
		assertThat(SpringExtensions.defaultIfEmpty("null", "DEFAULT")).isEqualTo("null");
	}

	@Test
	public void defaultIfEmptyReturnsDefault() {

		assertThat(SpringExtensions.defaultIfEmpty("  ", "DEFAULT")).isEqualTo("DEFAULT");
		assertThat(SpringExtensions.defaultIfEmpty("", "DEFAULT")).isEqualTo("DEFAULT");
		assertThat(SpringExtensions.defaultIfEmpty(null, "DEFAULT")).isEqualTo("DEFAULT");
	}

	@Test
	public void defaultIfNullReturnsValue() {

		assertThat(SpringExtensions.defaultIfNull(true, false)).isTrue();
		assertThat(SpringExtensions.defaultIfNull('x', 'A')).isEqualTo('x');
		assertThat(SpringExtensions.defaultIfNull(1, 2)).isEqualTo(1);
		assertThat(SpringExtensions.defaultIfNull(Math.PI, 2.0d)).isEqualTo(Math.PI);
		assertThat(SpringExtensions.defaultIfNull("test", "DEFAULT")).isEqualTo("test");
	}

	@Test
	public void defaultIfNullReturnsDefault() {

		assertThat(SpringExtensions.defaultIfNull(null, false)).isFalse();
		assertThat(SpringExtensions.defaultIfNull(null, 'A')).isEqualTo('A');
		assertThat(SpringExtensions.defaultIfNull(null, 2)).isEqualTo(2);
		assertThat(SpringExtensions.defaultIfNull(null, 2.0d)).isEqualTo(2.0d);
		assertThat(SpringExtensions.defaultIfNull(null, "DEFAULT")).isEqualTo("DEFAULT");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void defaultIfNullWithSupplierReturnsValue() {

		Supplier<String> mockSupplier = mock(Supplier.class);

		assertThat(SpringExtensions.defaultIfNull("value", mockSupplier)).isEqualTo("value");

		verify(mockSupplier, never()).get();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void defaultIfNullWithSupplierReturnsSupplierValue() {

		Supplier<String> mockSupplier = mock(Supplier.class);

		when(mockSupplier.get()).thenReturn("supplier");

		assertThat(SpringExtensions.defaultIfNull(null, mockSupplier)).isEqualTo("supplier");

		verify(mockSupplier, times(1)).get();
	}

	@Test
	public void dereferenceBean() {
		assertThat(SpringExtensions.dereferenceBean("example")).isEqualTo("&example");
	}

	@Test
	public void equalsIgnoreNullIsTrue() {

		assertThat(SpringExtensions.equalsIgnoreNull(null, null)).isTrue();
		assertThat(SpringExtensions.equalsIgnoreNull(true, true)).isTrue();
		assertThat(SpringExtensions.equalsIgnoreNull('x', 'x')).isTrue();
		assertThat(SpringExtensions.equalsIgnoreNull(1, 1)).isTrue();
		assertThat(SpringExtensions.equalsIgnoreNull(Math.PI, Math.PI)).isTrue();
		assertThat(SpringExtensions.equalsIgnoreNull("null", "null")).isTrue();
		assertThat(SpringExtensions.equalsIgnoreNull("test", "test")).isTrue();
	}

	@Test
	public void equalsIgnoreNullIsFalse() {

		assertThat(SpringExtensions.equalsIgnoreNull(null, "null")).isFalse();
		assertThat(SpringExtensions.equalsIgnoreNull(true, false)).isFalse();
		assertThat(SpringExtensions.equalsIgnoreNull('x', 'X')).isFalse();
		assertThat(SpringExtensions.equalsIgnoreNull(1, 2)).isFalse();
		assertThat(SpringExtensions.equalsIgnoreNull(3.14159d, Math.PI)).isFalse();
		assertThat(SpringExtensions.equalsIgnoreNull("nil", "null")).isFalse();
	}

	@Test
	public void nullOrEqualsWithEqualObjectsIsTrue() {
		assertThat(SpringExtensions.nullOrEquals("test", "test")).isTrue();
	}

	@Test
	public void nullOrEqualsWithNonNullObjectAndNullIsFalse() {
		assertThat(SpringExtensions.nullOrEquals("test", null)).isFalse();
	}

	@Test
	public void nullOrEqualsWithNullIsTrue() {
		assertThat(SpringExtensions.nullOrEquals(null, "test")).isTrue();
	}

	@Test
	public void nullOrEqualsWithUnequalObjectsIsFalse() {
		assertThat(SpringExtensions.nullOrEquals("test", "mock")).isFalse();
	}

	@Test
	public void nullSafeEqualsWithEqualObjectsIsTrue() {
		assertThat(SpringExtensions.nullSafeEquals("test", "test")).isTrue();
	}

	@Test
	public void nullSafeEqualsWithNullObjectsIsFalse() {
		assertThat(SpringExtensions.nullSafeEquals(null, "test")).isFalse();
		assertThat(SpringExtensions.nullSafeEquals("test", null)).isFalse();
	}

	@Test
	public void nullSafeEqualsWithUnequalObjectsIsFalse() {
		assertThat(SpringExtensions.nullSafeEquals("test", "mock")).isFalse();
	}

	@Test
	public void nullSafeNameWithType() {

		assertThat(SpringExtensions.nullSafeName(Boolean.class)).isEqualTo(Boolean.class.getName());
		assertThat(SpringExtensions.nullSafeName(Integer.class)).isEqualTo(Integer.class.getName());
		assertThat(SpringExtensions.nullSafeName(Double.class)).isEqualTo(Double.class.getName());
		assertThat(SpringExtensions.nullSafeName(String.class)).isEqualTo(String.class.getName());
		assertThat(SpringExtensions.nullSafeName(Time.class)).isEqualTo(Time.class.getName());
		assertThat(SpringExtensions.nullSafeName(Person.class)).isEqualTo(Person.class.getName());
	}

	@Test
	public void nullSafeNameWithNull() {
		assertThat(SpringExtensions.nullSafeName(null)).isNull();
	}

	@Test
	public void nullSafeSimpleNameWithType() {

		assertThat(SpringExtensions.nullSafeSimpleName(Boolean.class)).isEqualTo(Boolean.class.getSimpleName());
		assertThat(SpringExtensions.nullSafeSimpleName(Integer.class)).isEqualTo(Integer.class.getSimpleName());
		assertThat(SpringExtensions.nullSafeSimpleName(Double.class)).isEqualTo(Double.class.getSimpleName());
		assertThat(SpringExtensions.nullSafeSimpleName(String.class)).isEqualTo(String.class.getSimpleName());
		assertThat(SpringExtensions.nullSafeSimpleName(Time.class)).isEqualTo(Time.class.getSimpleName());
		assertThat(SpringExtensions.nullSafeSimpleName(Person.class)).isEqualTo(Person.class.getSimpleName());
	}

	@Test
	public void nullSafeSimpleNameWithNull() {
		assertThat(SpringExtensions.nullSafeSimpleName(null)).isNull();
	}

	@Test
	public void nullSafeTypeWithObject() {
		assertThat(SpringExtensions.nullSafeType(new Object())).isEqualTo(Object.class);
	}

	@Test
	public void nullSafeTypeWithObjectAndDefaultType() {
		assertThat(SpringExtensions.nullSafeType("test", Person.class)).isEqualTo(String.class);
	}

	@Test
	public void nullSafeTypeWithNull() {
		assertThat(SpringExtensions.nullSafeType(null)).isNull();
	}

	@Test
	public void nullSafeTypeWithNullAndDefaultType() {
		assertThat(SpringExtensions.nullSafeType(null, Person.class)).isEqualTo(Person.class);
	}

	@Test
	public void requireObjectReturnsObject() {
		assertThat(SpringExtensions.requireObject("TEST", "Test Object must not be null")).isEqualTo("TEST");
	}

	@Test(expected = IllegalStateException.class)
	public void requireObjectWithNullObjectThrowsIllegalStateException() {

		try {
			SpringExtensions.requireObject(null, "Test Object must not be null");
		}
		catch (IllegalStateException expected) {

			assertThat(expected).hasMessage("Test Object must not be null");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void safeDoOperationWithNonThrowingOperation() {

		AtomicReference<Object> operationValue = new AtomicReference<>();

		assertThat(SpringExtensions.safeDoOperation(() -> operationValue.set("TEST"))).isTrue();
		assertThat(operationValue.get()).isEqualTo("TEST");
	}

	@Test
	public void safeDoOperationWithThrowingOperation() {
		assertThat(SpringExtensions.safeDoOperation(() -> { throw new RuntimeException("TEST"); })).isFalse();
	}

	@Test
	public void safeDoOperationWithNonThrowingOperationAndBackupOperation() {

		AtomicReference<Object> operationValue = new AtomicReference<>();

		Runnable mockRunnable = mock(Runnable.class);

		assertThat(SpringExtensions.safeDoOperation(() -> operationValue.set("MOCK"), mockRunnable)).isTrue();
		assertThat(operationValue.get()).isEqualTo("MOCK");

		verifyNoInteractions(mockRunnable);
	}

	@Test
	public void safeDoOperationWithThrowingOperationAndBackupOperation() {

		Runnable mockRunnable = mock(Runnable.class);

		assertThat(SpringExtensions.safeDoOperation(() -> { throw new RuntimeException("TEST"); }, mockRunnable)).isFalse();

		verify(mockRunnable, times(1)).run();
		verifyNoMoreInteractions(mockRunnable);
	}

	@Test
	public void safeGetValueReturnsSuppliedValue() {
		assertThat(SpringExtensions.safeGetValue(() -> "test")).isEqualTo("test");
	}

	@Test
	public void safeGetValueReturnsNull() {
		assertThat(SpringExtensions.<Object>safeGetValue(() -> { throw newRuntimeException("error"); })).isNull();
	}

	@Test
	public void safeGetValueReturnsDefaultValue() {
		assertThat(SpringExtensions.safeGetValue(() -> { throw newRuntimeException("error"); },  "test"))
			.isEqualTo("test");
	}

	@Test
	public void safeGetValueReturnsSuppliedDefaultValue() {

		ValueReturningThrowableOperation<String> exceptionThrowingOperation =
			() -> { throw newRuntimeException("error"); };

		Supplier<String> defaultValueSupplier = () -> "test";

		assertThat(SpringExtensions.safeGetValue(exceptionThrowingOperation, defaultValueSupplier)).isEqualTo("test");
	}

	@Test
	public void safeGetValueHandlesExceptionReturnsValue() {

		ValueReturningThrowableOperation<String> exceptionThrowingOperation =
			() -> { throw newRuntimeException("error"); };

		Function<Throwable, String> exceptionHandler = exception -> {

			assertThat(exception).isInstanceOf(RuntimeException.class);
			assertThat(exception).hasMessage("error");
			assertThat(exception).hasNoCause();

			return "test";
		};

		assertThat(SpringExtensions.safeGetValue(exceptionThrowingOperation, exceptionHandler)).isEqualTo("test");
	}

	@Test(expected = IllegalStateException.class)
	public void safeGetValueHandlesExceptionAndCanThrowException() {

		ValueReturningThrowableOperation<String> exceptionThrowingOperation =
			() -> { throw newRuntimeException("error"); };

		Function<Throwable, String> exceptionHandler = exception -> {

			assertThat(exception).isInstanceOf(RuntimeException.class);
			assertThat(exception).hasMessage("error");
			assertThat(exception).hasNoCause();

			throw newIllegalStateException(exception, "test");
		};

		try {
			SpringExtensions.safeGetValue(exceptionThrowingOperation, exceptionHandler);
		}
		catch (IllegalStateException expected) {

			assertThat(expected).hasMessage("test");
			assertThat(expected).hasCauseInstanceOf(RuntimeException.class);
			assertThat(expected.getCause()).hasMessage("error");
			assertThat(expected.getCause()).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void safeRunOperationRunsSuccessfully() {

		AtomicBoolean operationRan = new AtomicBoolean(false);

		SpringExtensions.safeRunOperation(() -> operationRan.set(true));

		assertThat(operationRan.get()).isTrue();
	}

	@Test(expected = InvalidDataAccessApiUsageException.class)
	public void safeRunOperationThrowsInvalidDataAccessApiUsageException() {

		try {
			SpringExtensions.safeRunOperation(() -> { throw new Exception("TEST"); });
		}
		catch (InvalidDataAccessApiUsageException expected) {

			assertThat(expected).hasMessageContaining("Failed to run operation");
			assertThat(expected).hasCauseInstanceOf(Exception.class);
			assertThat(expected.getCause()).hasMessage("TEST");
			assertThat(expected.getCause()).hasNoCause();

			throw expected;
		}
	}

	@Test(expected = IllegalStateException.class)
	public void safeRunOperationThrowsCustomRuntimeException() {

		try {
			SpringExtensions.safeRunOperation(() -> { throw new Exception("TEST"); },
				cause -> new IllegalStateException("FOO", cause));
		}
		catch (IllegalStateException expected) {

			assertThat(expected).hasMessage("FOO");
			assertThat(expected).hasCauseInstanceOf(Exception.class);
			assertThat(expected.getCause()).hasMessage("TEST");
			assertThat(expected.getCause()).hasNoCause();

			throw expected;
		}
	}
}
