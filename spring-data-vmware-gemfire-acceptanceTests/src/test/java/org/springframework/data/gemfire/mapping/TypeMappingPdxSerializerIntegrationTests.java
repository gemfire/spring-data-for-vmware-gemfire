/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.mapping;

import example.app.model.ComplexType;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.geode.pdx.PdxWriter;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link MappingPdxSerializer} with {@literal complex} and {@literal simple} {@link Class types}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.pdx.PdxSerializer
 * @see org.apache.geode.pdx.PdxWriter
 * @see org.springframework.data.gemfire.mapping.MappingPdxSerializer
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
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
		Assertions.assertThat(this.pdxSerializer).isNotNull();
	}

	@Test
	public void mapsComplexTypeSuccessfully() {

		PdxWriter mockPdxWriter = Mockito.mock(PdxWriter.class);

		ComplexType complexType = new ComplexType();

		complexType.setId(2L);
		complexType.setDecimalValue(new BigDecimal(123));
		complexType.setIntegerValue(new BigInteger("987"));
		complexType.setName("TEST");

		this.pdxSerializer.toData(complexType, mockPdxWriter);

		Mockito.verify(mockPdxWriter, Mockito.times(1)).writeField(ArgumentMatchers.eq("id"), ArgumentMatchers.eq(2L), ArgumentMatchers.eq(Long.class));
		Mockito.verify(mockPdxWriter, Mockito.times(1)).writeField(ArgumentMatchers.eq("decimalValue"), ArgumentMatchers.eq(new BigDecimal(123)), ArgumentMatchers.eq(BigDecimal.class));
		Mockito.verify(mockPdxWriter, Mockito.times(1)).writeField(ArgumentMatchers.eq("integerValue"), ArgumentMatchers.eq(new BigInteger("987")), ArgumentMatchers.eq(BigInteger.class));
		Mockito.verify(mockPdxWriter, Mockito.times(1)).writeField(ArgumentMatchers.eq("name"), ArgumentMatchers.eq("TEST"), ArgumentMatchers.eq(String.class));
		Mockito.verify(mockPdxWriter, Mockito.times(1)).markIdentityField(ArgumentMatchers.eq("id"));

		Mockito.verifyNoMoreInteractions(mockPdxWriter);
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
