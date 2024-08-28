/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.pdx.PdxReader;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;

/**
 * Unit Tests for {@link PdxReaderPropertyAccessor}.
 *
 * @author Oliver Gierke
 * @author John Blum
 * @see Test
 * @see Mock
 * @see org.mockito.Mockito
 * @see MockitoJUnitRunner
 * @see PdxReaderPropertyAccessor
 */
@RunWith(MockitoJUnitRunner.class)
public class PdxReaderPropertyAccessorUnitTests {

	@Mock
	private EvaluationContext mockEvaluationContext;

	@Mock
	private PdxReader mockReader;

	@Test
	public void appliesToPdxReadersOnly() {

		List<Class<?>> classes = Arrays.asList(PdxReaderPropertyAccessor.INSTANCE.getSpecificTargetClasses());

		assertThat(classes).contains(PdxReader.class);
	}

	@Test
	public void canReadPropertyIfReaderHasField() {

		when(this.mockReader.hasField("key")).thenReturn(true);

		assertThat(PdxReaderPropertyAccessor.INSTANCE.canRead(this.mockEvaluationContext, this.mockReader, "key"))
			.isTrue();
	}

	@Test
	public void cannotReadPropertyWhenReaderDoesNotHaveField() {

		when(this.mockReader.hasField("key")).thenReturn(false);

		assertThat(PdxReaderPropertyAccessor.INSTANCE.canRead(this.mockEvaluationContext, this.mockReader, "key"))
			.isFalse();
	}

	@Test
	public void returnsTypedNullIfNullIsReadFromReader() {

		when(this.mockReader.readObject("key")).thenReturn(null);

		assertThat(PdxReaderPropertyAccessor.INSTANCE.read(this.mockEvaluationContext, this.mockReader, "key"))
			.isEqualTo(TypedValue.NULL);
	}

	@Test
	public void returnsTypeValueWithValueReadFromReader() {

		when(this.mockReader.readObject("key")).thenReturn("String");

		TypedValue result = PdxReaderPropertyAccessor.INSTANCE.read(this.mockEvaluationContext, this.mockReader, "key");

		assertThat(result.getTypeDescriptor()).isEqualTo(TypeDescriptor.valueOf(String.class));
		assertThat(result.getValue()).isEqualTo("String");
	}

	@SuppressWarnings("all")
	@Test(expected = UnsupportedOperationException.class)
	public void doesNotSupportWrites() {

		assertThat(PdxReaderPropertyAccessor.INSTANCE.canWrite(this.mockEvaluationContext, null, null)).isFalse();

		PdxReaderPropertyAccessor.INSTANCE.write(this.mockEvaluationContext, null, null, this.mockReader);
	}
}
