/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.beans.factory.BeanFactory;

/**
 * Unit Tests for {@link DeclarableSupport}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.mockito.Spy
 * @see org.mockito.junit.MockitoJUnitRunner
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class DeclarableSupportUnitTests {

	@Mock
	private BeanFactory mockBeanFactoryOne;

	@Mock
	private BeanFactory mockBeanFactoryTwo;

	@Spy
	private DeclarableSupport testDeclarableSupport;

	@After
	public void tearDown() {
		testDeclarableSupport.setBeanFactoryKey(null);
		GemfireBeanFactoryLocator.BEAN_FACTORIES.clear();
	}

	@Test
	public void setAndGetBeanFactoryKey() {
		assertThat(testDeclarableSupport.getBeanFactoryKey()).isNull();

		testDeclarableSupport.setBeanFactoryKey("testKey");

		assertThat(testDeclarableSupport.getBeanFactoryKey()).isEqualTo("testKey");

		testDeclarableSupport.setBeanFactoryKey(null);

		assertThat(testDeclarableSupport.getBeanFactoryKey()).isNull();
	}

	@Test
	public void locateBeanFactoryReturnsBeanFactory() {
		GemfireBeanFactoryLocator.BEAN_FACTORIES.put("keyOne", mockBeanFactoryOne);
		GemfireBeanFactoryLocator.BEAN_FACTORIES.put("keyTwo", mockBeanFactoryTwo);

		testDeclarableSupport.setBeanFactoryKey("keyOne");

		assertThat(testDeclarableSupport.getBeanFactoryKey()).isEqualTo("keyOne");
		assertThat(testDeclarableSupport.locateBeanFactory()).isSameAs(mockBeanFactoryOne);
	}

	@Test
	public void locateBeanFactoryWithKeyReturnsBeanFactory() {
		GemfireBeanFactoryLocator.BEAN_FACTORIES.put("keyOne", mockBeanFactoryOne);
		GemfireBeanFactoryLocator.BEAN_FACTORIES.put("keyTwo", mockBeanFactoryTwo);

		assertThat(testDeclarableSupport.getBeanFactoryKey()).isNull();
		assertThat(testDeclarableSupport.locateBeanFactory("keyTwo")).isSameAs(mockBeanFactoryTwo);
	}

	@Test
	public void locateBeanFactoryWithoutKeyReturnsBeanFactory() {
		GemfireBeanFactoryLocator.BEAN_FACTORIES.put("keyOne", mockBeanFactoryOne);
		GemfireBeanFactoryLocator.BEAN_FACTORIES.put("refTwo", mockBeanFactoryOne);

		assertThat(testDeclarableSupport.getBeanFactoryKey()).isNull();
		assertThat(testDeclarableSupport.locateBeanFactory()).isSameAs(mockBeanFactoryOne);
	}

	@Test(expected = IllegalArgumentException.class)
	public void locateBeanFactoryWithUnknownKeyHavingMultipleBeanFactoriesRegisteredThrowsIllegalArgumentException() {

		GemfireBeanFactoryLocator.BEAN_FACTORIES.put("keyOne", mockBeanFactoryOne);
		GemfireBeanFactoryLocator.BEAN_FACTORIES.put("keyTwo", mockBeanFactoryTwo);

		testDeclarableSupport.setBeanFactoryKey("keyOne");

		assertThat(testDeclarableSupport.getBeanFactoryKey()).isEqualTo("keyOne");

		try {
			testDeclarableSupport.locateBeanFactory("UnknownKey");
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("BeanFactory for key [UnknownKey] was not found");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test(expected = IllegalStateException.class)
	public void locateBeanFactoryWithoutKeyHavingMultipleBeanFactoriesRegisteredThrowsIllegalStateException() {

		GemfireBeanFactoryLocator.BEAN_FACTORIES.put("keyOne", mockBeanFactoryOne);
		GemfireBeanFactoryLocator.BEAN_FACTORIES.put("keyTwo", mockBeanFactoryTwo);

		assertThat(testDeclarableSupport.getBeanFactoryKey()).isNull();

		try {
			testDeclarableSupport.locateBeanFactory();
		}
		catch (IllegalStateException expected) {

			assertThat(expected)
				.hasMessage("BeanFactory key must be specified when more than one BeanFactory [keyOne, keyTwo] is registered");

			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test(expected = IllegalStateException.class)
	public void locateBeanFactoryWithKeyWhenNoBeanFactoriesAreRegisteredThrowsIllegalStateException() {

		assertThat(GemfireBeanFactoryLocator.BEAN_FACTORIES).isEmpty();

		try {
			testDeclarableSupport.locateBeanFactory("testKey");
		}
		catch (IllegalStateException expected) {

			assertThat(expected).hasMessage("A BeanFactory was not initialized;"
				+ " Please verify the useBeanFactoryLocator property was properly set");

			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test(expected = IllegalStateException.class)
	public void locateBeanFactoryWithoutKeyWhenNoBeanFactoriesAreRegisteredThrowsIllegalStateException() {

		assertThat(GemfireBeanFactoryLocator.BEAN_FACTORIES).isEmpty();

		try {
			testDeclarableSupport.locateBeanFactory();
		}
		catch (IllegalStateException expected) {

			assertThat(expected).hasMessage("A BeanFactory was not initialized;"
				+ " Please verify the useBeanFactoryLocator property was properly set");

			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void getBeanFactoryReturnsBeanFactory() {

		GemfireBeanFactoryLocator.BEAN_FACTORIES.put("keyOne", mockBeanFactoryOne);
		GemfireBeanFactoryLocator.BEAN_FACTORIES.put("keyTwo", mockBeanFactoryTwo);

		testDeclarableSupport.setBeanFactoryKey("keyOne");

		assertThat(testDeclarableSupport.getBeanFactoryKey()).isEqualTo("keyOne");
		assertThat(testDeclarableSupport.getBeanFactory()).isSameAs(mockBeanFactoryOne);
	}

	@Test
	public void closeIsSuccessful() {

		testDeclarableSupport.close();

		verify(testDeclarableSupport, times(1)).close();
	}
}
