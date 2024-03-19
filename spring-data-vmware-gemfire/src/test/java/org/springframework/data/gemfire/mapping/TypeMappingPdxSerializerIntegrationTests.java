/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.pdx.PdxWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import example.app.model.ComplexType;

/**
 * Integration Tests for {@link MappingPdxSerializer} with {@literal complex} and {@literal simple} {@link Class types}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.pdx.PdxSerializer
 * @see PdxWriter
 * @see MappingPdxSerializer
 * @see IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.5.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class TypeMappingPdxSerializerIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private MappingPdxSerializer pdxSerializer;

	@Before
	public void setup() {
		assertThat(this.pdxSerializer).isNotNull();
	}

	@Test
	public void mapsComplexTypeSuccessfully() {

		PdxWriter mockPdxWriter = mock(PdxWriter.class);

		ComplexType complexType = new ComplexType();

		complexType.setId(2L);
		complexType.setDecimalValue(new BigDecimal(123));
		complexType.setIntegerValue(new BigInteger("987"));
		complexType.setName("TEST");

		this.pdxSerializer.toData(complexType, mockPdxWriter);

		verify(mockPdxWriter, times(1)).writeField(eq("id"), eq(2L), eq(Long.class));
		verify(mockPdxWriter, times(1)).writeField(eq("decimalValue"), eq(new BigDecimal(123)), eq(BigDecimal.class));
		verify(mockPdxWriter, times(1)).writeField(eq("integerValue"), eq(new BigInteger("987")), eq(BigInteger.class));
		verify(mockPdxWriter, times(1)).writeField(eq("name"), eq("TEST"), eq(String.class));
		verify(mockPdxWriter, times(1)).markIdentityField(eq("id"));

		verifyNoMoreInteractions(mockPdxWriter);
	}

	@Configuration
	@SuppressWarnings("unused")
	static class TestConfiguration {

		@Bean
		MappingPdxSerializer testMappingPdxSerializer(ConfigurableApplicationContext applicationContext) {
			return MappingPdxSerializer.create(applicationContext.getBeanFactory().getConversionService());
		}
	}
}
