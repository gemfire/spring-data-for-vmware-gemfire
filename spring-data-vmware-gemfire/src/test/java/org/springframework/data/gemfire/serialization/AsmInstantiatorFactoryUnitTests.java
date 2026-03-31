/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.serialization;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Serializable;

import org.junit.Before;
import org.junit.Test;

import org.springframework.data.gemfire.gud.api.GudDataSerializable;
import org.springframework.data.gemfire.gud.api.GudInstantiator;

/**
 * Unit Tests for {@link AsmInstantiatorGenerator}.
 *
 * @author Costin Leau
 * @author John Blum
 * @see org.apache.geode.GudInstantiator
 * @see org.springframework.data.gemfire.serialization.AsmInstantiatorGenerator
 */
public class AsmInstantiatorFactoryUnitTests {

	public static class SomeClass implements GudDataSerializable {

		public static boolean instantiated = false;

		public SomeClass() {
			instantiated = true;
		}

		public void fromData(DataInput in) { }

		public void toData(DataOutput out) { }

	}

	private AsmInstantiatorGenerator asmFactory = null;

	@Before
	public void setUp() {
		SomeClass.instantiated = false;
		asmFactory = new AsmInstantiatorGenerator();
	}

	@Test
	public void testClassGeneration() {

		GudInstantiator instantiator = asmFactory.getInstantiator(SomeClass.class, 100);

		assertThat(instantiator.getId()).isEqualTo(100);
		assertThat(instantiator.getInstantiatedClass()).isEqualTo(SomeClass.class);

		Object instance = instantiator.newInstance();

		assertThat(instance.getClass()).isEqualTo(SomeClass.class);
		assertThat(SomeClass.instantiated).isTrue();
	}

	@Test
	public void testGeneratedClassName() {

		GudInstantiator instantiator = asmFactory.getInstantiator(SomeClass.class, 100);

		assertThat(instantiator.getClass().getName().contains("$")).isTrue();
	}

	@Test
	public void testInterfaces() {

		GudInstantiator instantiator = asmFactory.getInstantiator(SomeClass.class, 100);

		assertThat(instantiator instanceof Serializable).isTrue();
	}

	@Test
	public void testCacheInPlace() {

		GudInstantiator instance1 = asmFactory.getInstantiator(SomeClass.class, 120);
		GudInstantiator instance2 = asmFactory.getInstantiator(SomeClass.class, 125);

		assertThat(instance2).isSameAs(instance1);
	}
}
