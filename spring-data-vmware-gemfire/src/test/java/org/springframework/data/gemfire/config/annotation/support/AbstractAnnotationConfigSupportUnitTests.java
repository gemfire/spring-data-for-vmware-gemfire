/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation.support;

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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.cache.GemFireCache;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.SpringVersion;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.MethodMetadata;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.mapping.annotation.Region;
import org.springframework.data.gemfire.test.model.Person;

/**
 * Unit Tests for {@link AbstractAnnotationConfigSupport}.
 *
 * @author John Blum
 * @see java.lang.annotation.Annotation
 * @see org.junit.Test
 * @see org.junit.runner.RunWith
 * @see org.mockito.Mockito
 * @see org.mockito.junit.MockitoJUnitRunner
 * @see org.springframework.data.gemfire.config.annotation.support.AbstractAnnotationConfigSupport
 * @since 1.1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractAnnotationConfigSupportUnitTests {

	private AbstractAnnotationConfigSupport support;

	@Before
	public void setup() {
		this.support = spy(new TestAnnotationConfigSupport());
	}

	private BeanDefinition mockBeanDefinition(Class<?> beanClass) {

		AbstractBeanDefinition mockBeanDefinition = mock(AbstractBeanDefinition.class, beanClass.getSimpleName());

		doReturn(beanClass.getName()).when(mockBeanDefinition).getBeanClassName();
		doReturn(BeanDefinition.ROLE_APPLICATION).when(mockBeanDefinition).getRole();

		return mockBeanDefinition;
	}

	@Test
	public void isNotInfrastructureBeanWithApacheGeodeClassIsTrue() {

		BeanDefinition mockBeanDefinition = mockBeanDefinition(GemFireCache.class);

		assertThat(this.support.isNotInfrastructureBean(mockBeanDefinition)).isTrue();

		verify(mockBeanDefinition, times(1)).getBeanClassName();
		verify(mockBeanDefinition, times(1)).getRole();
		verifyNoMoreInteractions(mockBeanDefinition);
	}

	@Test
	public void isNotInfrastructureBeanWithObjectClassIsTrue() {

		BeanDefinition mockBeanDefinition = mockBeanDefinition(Object.class);

		assertThat(this.support.isNotInfrastructureBean(mockBeanDefinition)).isTrue();

		verify(mockBeanDefinition, times(1)).getBeanClassName();
		verify(mockBeanDefinition, times(1)).getRole();
		verifyNoMoreInteractions(mockBeanDefinition);
	}

	@Test
	public void isNotInfrastructureBeanWithSpringDataGeodeGemfireTemplateClassIsTrue() {

		BeanDefinition mockBeanDefinition = mockBeanDefinition(GemfireTemplate.class);

		assertThat(this.support.isNotInfrastructureBean(mockBeanDefinition)).isTrue();

		verify(mockBeanDefinition, times(1)).getBeanClassName();
		verify(mockBeanDefinition, times(1)).getRole();
		verifyNoMoreInteractions(mockBeanDefinition);
	}

	@Test
	public void isNotInfrastructureBeanWithSpringFrameworkInfrastructureClassIsFalse() {

		BeanDefinition mockBeanDefinition = mockBeanDefinition(SpringVersion.class);

		assertThat(this.support.isNotInfrastructureBean(mockBeanDefinition)).isFalse();

		verify(mockBeanDefinition, times(1)).getBeanClassName();
		verify(mockBeanDefinition, times(1)).getRole();
		verifyNoMoreInteractions(mockBeanDefinition);
	}

	@Test
	public void isNotInfrastructureBeanWithInfrastructureRoleIsFalse() {

		BeanDefinition mockBeanDefinition = mockBeanDefinition(Object.class);

		doReturn(BeanDefinition.ROLE_INFRASTRUCTURE).when(mockBeanDefinition).getRole();

		assertThat(this.support.isNotInfrastructureBean(mockBeanDefinition)).isFalse();

		verify(mockBeanDefinition, never()).getBeanClassName();
		verify(mockBeanDefinition, times(1)).getRole();
		verifyNoMoreInteractions(mockBeanDefinition);
	}

	@Test
	public void isNotInfrastructureClassWithObjectBeanDefinitionIsTrue() {

		BeanDefinition mockBeanDefinition = mockBeanDefinition(Object.class);

		assertThat(this.support.isNotInfrastructureClass(mockBeanDefinition)).isTrue();

		verify(mockBeanDefinition, times(1)).getBeanClassName();
		verifyNoMoreInteractions(mockBeanDefinition);
	}

	@Test
	public void isNotInfrastructureClassWithSpringInfrastructureBeanDefinitionIsFalse() {

		BeanDefinition mockBeanDefinition = mockBeanDefinition(SpringVersion.class);

		assertThat(this.support.isNotInfrastructureClass(mockBeanDefinition)).isFalse();

		verify(mockBeanDefinition, times(1)).getBeanClassName();
		verifyNoMoreInteractions(mockBeanDefinition);
	}

	@Test
	public void isNotInfrastructureClassIsTrue() {
		assertThat(this.support.isNotInfrastructureClass("org.example.app.model.MyClass")).isTrue();
	}

	@Test
	public void isNotInfrastructureClassIsFalse() {
		assertThat(this.support.isNotInfrastructureClass("org.springframework.SomeType")).isFalse();
		assertThat(this.support.isNotInfrastructureClass("org.springframework.core.type.SomeType")).isFalse();
	}

	@Test
	public void isNotInfrastructureRoleIsTrue() {

		BeanDefinition mockBeanDefinition = mock(BeanDefinition.class);

		doReturn(BeanDefinition.ROLE_APPLICATION).doReturn(Integer.MAX_VALUE).when(mockBeanDefinition).getRole();

		assertThat(this.support.isNotInfrastructureRole(mockBeanDefinition)).isTrue();
		assertThat(this.support.isNotInfrastructureRole(mockBeanDefinition)).isTrue();

		verify(mockBeanDefinition, times(2)).getRole();
		verifyNoMoreInteractions(mockBeanDefinition);
	}

	@Test
	public void isNotInfrastructureRoleIsFalse() {

		BeanDefinition mockBeanDefinition = mock(BeanDefinition.class);

		doReturn(BeanDefinition.ROLE_INFRASTRUCTURE).doReturn(BeanDefinition.ROLE_SUPPORT)
			.when(mockBeanDefinition).getRole();

		assertThat(this.support.isNotInfrastructureRole(mockBeanDefinition)).isFalse();
		assertThat(this.support.isNotInfrastructureRole(mockBeanDefinition)).isFalse();

		verify(mockBeanDefinition, times(2)).getRole();
		verifyNoMoreInteractions(mockBeanDefinition);
	}

	@Test
	public void isUserLevelMethodWithNullMethodReturnsFalse() {
		assertThat(this.support.isUserLevelMethod(null)).isFalse();
	}

	@Test
	public void isUserLevelMethodWithObjectMethodReturnsFalse() throws NoSuchMethodException {
		assertThat(this.support.isUserLevelMethod(Object.class.getMethod("equals", Object.class)))
			.isFalse();
	}

	@Test
	public void isUserLevelMethodWithUserMethodReturnsTrue() throws NoSuchMethodException {
		assertThat(this.support.isUserLevelMethod(Person.class.getMethod("getName"))).isTrue();
	}

	@Test
	public void requirePropertyWithNonStringValueIsSuccessful() {

		when(this.support.resolveProperty(anyString(), eq(Integer.class), eq(null))).thenReturn(1);

		assertThat(this.support.requireProperty("key", Integer.class)).isEqualTo(1);

		verify(support, times(1))
			.resolveProperty(eq("key"), eq(Integer.class), eq(null));
	}

	@Test
	public void requirePropertyWithStringValueIsSuccessful() {

		when(this.support.resolveProperty(anyString(), eq(String.class), eq(null))).thenReturn("test");

		assertThat(this.support.requireProperty("key", String.class)).isEqualTo("test");

		verify(this.support, times(1))
			.resolveProperty(eq("key"), eq(String.class), eq(null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void requirePropertyWithEmptyStringThrowsIllegalArgumentException() {

		when(this.support.resolveProperty(anyString(), eq(String.class), eq(null))).thenReturn("  ");

		try {
			this.support.requireProperty("key", String.class);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("Property [key] is required");
			assertThat(expected).hasNoCause();

			throw expected;
		}
		finally {
			verify(this.support, times(1))
				.resolveProperty(eq("key"), eq(String.class), eq(null));
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void requirePropertyWithNullValueThrowsIllegalArgumentException() {

		when(this.support.resolveProperty(anyString(), any(), eq(null))).thenReturn(null);

		try {
			this.support.requireProperty("key", Integer.class);
		}
		catch (IllegalArgumentException expected) {
			assertThat(expected).hasMessage("Property [key] is required");
			assertThat(expected).hasNoCause();

			throw expected;
		}
		finally {
			verify(this.support, times(1))
				.resolveProperty(eq("key"), eq(Integer.class), eq(null));
		}
	}

	@Test
	public void resolveAnnotationFromClass() {

		Annotation region = this.support.resolveAnnotation(Person.class, Region.class);

		assertThat(region).isNotNull();
		assertThat(AnnotationUtils.getAnnotationAttributes(region).get("value")).isEqualTo("People");
	}

	@Test
	public void resolveAnnotationFromMethod() throws NoSuchMethodException {

		Method cacheableMethod = TestService.class.getMethod("cacheableMethod");

		Annotation cacheable = this.support.resolveAnnotation(cacheableMethod, Cacheable.class);

		assertThat(cacheable).isNotNull();
		assertThat(AnnotationUtils.getAnnotationAttributes(cacheable).get("cacheNames"))
			.isEqualTo(asArray("cacheOne", "cacheTwo"));
	}

	@Test
	public void resolveAnnotationIsUnresolvable() throws NoSuchMethodException {

		Method cacheableMethodFive = TestService.class.getMethod("nonCacheableMethod");

		assertThat(this.support.resolveAnnotation(cacheableMethodFive, Cacheable.class)).isNull();
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void resolveBeanClassFromBeanDefinition() throws ClassNotFoundException {

		AbstractBeanDefinition mockBeanDefinition = mock(AbstractBeanDefinition.class);

		BeanDefinitionRegistry mockBeanDefinitionRegistry = mock(BeanDefinitionRegistryConfigurableBeanFactory.class);

		ClassLoader mockClassLoader = mock(ClassLoader.class);

		when(mockBeanDefinition.resolveBeanClass(any(ClassLoader.class))).thenReturn((Class) Object.class);
		when(((ConfigurableBeanFactory) mockBeanDefinitionRegistry).getBeanClassLoader()).thenReturn(mockClassLoader);

		assertThat(this.support.resolveBeanClass(mockBeanDefinition, mockBeanDefinitionRegistry).orElse(null))
			.isEqualTo(Object.class);

		verify(mockBeanDefinition, never()).getBeanClassName();
		verify(mockBeanDefinition, never()).getFactoryMethodName();
		verify(mockBeanDefinition, times(1)).resolveBeanClass(eq(mockClassLoader));
		verify(((ConfigurableBeanFactory) mockBeanDefinitionRegistry), times(1))
			.getBeanClassLoader();
	}

	@Test
	public void resolveBeanClassUsingRegistryClassLoaderAndBeanClassName() throws ClassNotFoundException {

		AbstractBeanDefinition mockBeanDefinition = mock(AbstractBeanDefinition.class);

		BeanDefinitionRegistry mockBeanDefinitionRegistry = mock(BeanDefinitionRegistryConfigurableBeanFactory.class);

		ClassLoader testClassLoader = AbstractAnnotationConfigSupport.class.getClassLoader();

		when(mockBeanDefinition.getBeanClassName()).thenReturn("java.lang.Object");
		when(mockBeanDefinition.resolveBeanClass(any(ClassLoader.class))).thenThrow(new ClassNotFoundException("test"));
		when(((ConfigurableBeanFactory) mockBeanDefinitionRegistry).getBeanClassLoader()).thenReturn(testClassLoader);

		assertThat(this.support.resolveBeanClass(mockBeanDefinition, mockBeanDefinitionRegistry).orElse(null))
			.isEqualTo(Object.class);

		verify(mockBeanDefinition, times(1)).getBeanClassName();
		verify(mockBeanDefinition, never()).getFactoryMethodName();
		verify(mockBeanDefinition, times(1)).resolveBeanClass(eq(testClassLoader));
		verify((ConfigurableBeanFactory) mockBeanDefinitionRegistry, times(1))
			.getBeanClassLoader();
	}

	@Test
	public void resolveBeanClassUsingThreadContextClassLoaderAndFactoryMethodReturnType() {

		AnnotatedBeanDefinition mockBeanDefinition = mock(AnnotatedBeanDefinition.class);

		BeanDefinitionRegistry mockBeanDefinitionRegistry = mock(BeanDefinitionRegistryConfigurableBeanFactory.class);

		MethodMetadata mockFactoryMethodMetadata = mock(MethodMetadata.class);

		when(mockBeanDefinition.getBeanClassName()).thenReturn(null);
		when(mockBeanDefinition.getFactoryMethodName()).thenReturn("testFactoryMethod");
		when(mockBeanDefinition.getFactoryMethodMetadata()).thenReturn(mockFactoryMethodMetadata);
		when(((ConfigurableBeanFactory) mockBeanDefinitionRegistry).getBeanClassLoader()).thenReturn(null);
		when(mockFactoryMethodMetadata.getReturnTypeName()).thenReturn("java.lang.Object");

		assertThat(this.support.resolveBeanClass(mockBeanDefinition, mockBeanDefinitionRegistry)
			.orElse(null)).isEqualTo(Object.class);

		verify(mockBeanDefinition, times(1)).getBeanClassName();
		verify(mockBeanDefinition, times(1)).getFactoryMethodName();
		verify(mockBeanDefinition, times(1)).getFactoryMethodMetadata();
		verify((ConfigurableBeanFactory) mockBeanDefinitionRegistry, times(1))
			.getBeanClassLoader();
		verify(mockFactoryMethodMetadata, times(1)).getReturnTypeName();
	}

	@Test
	public void resolveBeanClassIsUnresolvable() throws ClassNotFoundException {

		AbstractBeanDefinition mockBeanDefinition = mock(AbstractBeanDefinition.class);

		when(mockBeanDefinition.getBeanClassName()).thenReturn("non.existing.bean.Class");
		when(mockBeanDefinition.resolveBeanClass(any(ClassLoader.class)))
			.thenThrow(new ClassNotFoundException("Class [non.existing.bean.Class] not found"));

		assertThat(this.support.resolveBeanClass(mockBeanDefinition, (BeanDefinitionRegistry) null)
			.orElse(null)).isNull();

		verify(mockBeanDefinition, times(1)).getBeanClassName();
		verify(mockBeanDefinition, never()).getFactoryMethodName();
		verify(mockBeanDefinition, times(1))
			.resolveBeanClass(eq(Thread.currentThread().getContextClassLoader()));

		verifyNoMoreInteractions(mockBeanDefinition);
	}

	@Test
	public void resolveBeanClassLoaderIsNullSafe() {
		assertThat(this.support.resolveBeanClassLoader((BeanDefinitionRegistry) null))
			.isEqualTo(Thread.currentThread().getContextClassLoader());
	}

	@Test
	public void resolveBeanClassLoaderReturnsRegistryClassLoader() {

		ClassLoader mockClassLoader = mock(ClassLoader.class);

		BeanDefinitionRegistry mockBeanDefinitionRegistry = mock(BeanDefinitionRegistryConfigurableBeanFactory.class);

		when(((ConfigurableBeanFactory) mockBeanDefinitionRegistry).getBeanClassLoader()).thenReturn(mockClassLoader);

		assertThat(this.support.resolveBeanClassLoader(mockBeanDefinitionRegistry)).isEqualTo(mockClassLoader);

		verify((ConfigurableBeanFactory) mockBeanDefinitionRegistry, times(1))
			.getBeanClassLoader();
	}

	@Test
	public void resolveBeanClassLoaderReturnsThreadContextClassLoaderWhenRegistryClassLoaderIsNull() {

		BeanDefinitionRegistry mockBeanDefinitionRegistry = mock(BeanDefinitionRegistryConfigurableBeanFactory.class);

		when(((ConfigurableBeanFactory) mockBeanDefinitionRegistry).getBeanClassLoader()).thenReturn(null);

		assertThat(this.support.resolveBeanClassLoader(mockBeanDefinitionRegistry))
			.isEqualTo(Thread.currentThread().getContextClassLoader());

		verify((ConfigurableBeanFactory) mockBeanDefinitionRegistry, times(1))
			.getBeanClassLoader();
	}

	@Test
	public void resolveBeanClassLoaderReturnsThreadContextClassLoaderWhenRegistryIsNotConfigurable() {

		BeanDefinitionRegistry mockBeanDefinitionRegistry = mock(BeanDefinitionRegistry.class);

		assertThat(this.support.resolveBeanClassLoader(mockBeanDefinitionRegistry))
			.isEqualTo(Thread.currentThread().getContextClassLoader());

		verifyNoInteractions(mockBeanDefinitionRegistry);
	}

	@Test
	public void resolveBeanClassNameIsNullSafe() {
		assertThat(this.support.resolveBeanClassName(null).orElse(null)).isNull();
	}

	@Test
	public void resolveBeanClassNameReturnsBeanClassName() {

		BeanDefinition mockBeanDefinition = mockBeanDefinition(Person.class);

		assertThat(this.support.resolveBeanClassName(mockBeanDefinition).orElse(null))
			.isEqualTo(Person.class.getName());

		verify(mockBeanDefinition, times(1)).getBeanClassName();
		verifyNoMoreInteractions(mockBeanDefinition);
	}

	@Test
	public void resolveBeanClassNameReturnsFactoryMethodReturnTypeName() {

		AnnotatedBeanDefinition mockBeanDefinition = mock(AnnotatedBeanDefinition.class);

		MethodMetadata mockFactoryMethodMetadata = mock(MethodMetadata.class);

		when(mockBeanDefinition.getBeanClassName()).thenReturn(null);
		when(mockBeanDefinition.getFactoryMethodName()).thenReturn("testFactoryMethod");
		when(mockBeanDefinition.getFactoryMethodMetadata()).thenReturn(mockFactoryMethodMetadata);
		when(mockFactoryMethodMetadata.getReturnTypeName()).thenReturn(Person.class.getName());

		assertThat(this.support.resolveBeanClassName(mockBeanDefinition).orElse(null))
			.isEqualTo(Person.class.getName());

		verify(mockBeanDefinition, times(1)).getBeanClassName();
		verify(mockBeanDefinition, times(1)).getFactoryMethodName();
		verify(mockBeanDefinition, times(1)).getFactoryMethodMetadata();
		verify(mockFactoryMethodMetadata, times(1)).getReturnTypeName();
		verifyNoMoreInteractions(mockBeanDefinition);
	}

	@Test
	public void resolveBeanClassNameWithNoFactoryMethodMetadataReturnsEmpty() {

		AnnotatedBeanDefinition mockBeanDefinition = mock(AnnotatedBeanDefinition.class);

		when(mockBeanDefinition.getBeanClassName()).thenReturn("  ");
		when(mockBeanDefinition.getFactoryMethodName()).thenReturn("testFactoryMethod");
		when(mockBeanDefinition.getFactoryMethodMetadata()).thenReturn(null);

		assertThat(this.support.resolveBeanClassName(mockBeanDefinition).orElse(null)).isNull();

		verify(mockBeanDefinition, times(1)).getBeanClassName();
		verify(mockBeanDefinition, times(1)).getFactoryMethodName();
		verify(mockBeanDefinition, times(1)).getFactoryMethodMetadata();
		verifyNoMoreInteractions(mockBeanDefinition);
	}

	@Test
	public void resolveBeanClassNameWithNoFactoryMethodNameReturnsEmpty() {

		AnnotatedBeanDefinition mockBeanDefinition = mock(AnnotatedBeanDefinition.class);

		when(mockBeanDefinition.getBeanClassName()).thenReturn("");
		when(mockBeanDefinition.getFactoryMethodName()).thenReturn("  ");

		assertThat(this.support.resolveBeanClassName(mockBeanDefinition).orElse(null)).isNull();

		verify(mockBeanDefinition, times(1)).getBeanClassName();
		verify(mockBeanDefinition, times(1)).getFactoryMethodName();
		verify(mockBeanDefinition, never()).getFactoryMethodMetadata();
		verifyNoMoreInteractions(mockBeanDefinition);
	}

	@Test
	public void resolveBeanClassNameWithNonAnnotatedBeanDefinitionReturnsEmpty() {

		BeanDefinition mockBeanDefinition = mock(BeanDefinition.class);

		when(mockBeanDefinition.getBeanClassName()).thenReturn(null);

		assertThat(this.support.resolveBeanClassName(mockBeanDefinition).orElse(null)).isNull();

		verify(mockBeanDefinition, times(1)).getBeanClassName();
		verify(mockBeanDefinition, never()).getFactoryMethodName();
		verifyNoMoreInteractions(mockBeanDefinition);
	}

	@Test
	public void safeResolveTypeReturnsType() {
		assertThat(this.support.safeResolveType(() -> Object.class)).isEqualTo(Object.class);
	}

	@Test
	public void safeResolveTypeThrowingClassNotFoundExceptionReturnsNull() {
		assertThat(this.support.safeResolveType(() -> { throw new ClassNotFoundException("TEST"); })).isNull();
	}

	interface BeanDefinitionRegistryConfigurableBeanFactory extends BeanDefinitionRegistry, ConfigurableBeanFactory {
	}

	@SuppressWarnings("unused")
	interface TestService {

		@Cacheable({ "cacheOne", "cacheTwo" })
		void cacheableMethod();

		void nonCacheableMethod();

	}

	static class TestAnnotationConfigSupport extends AbstractAnnotationConfigSupport {

		@Override
		protected Class<? extends Annotation> getAnnotationType() {
			throw new UnsupportedOperationException("Not Implemented");
		}
	}
}
