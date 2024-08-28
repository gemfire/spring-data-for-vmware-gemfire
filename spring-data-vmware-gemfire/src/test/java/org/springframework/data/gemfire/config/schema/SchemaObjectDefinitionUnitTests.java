/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.schema;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.springframework.core.Ordered;

/**
 * Unit tests for {@link SchemaObjectDefinition}.
 *
 * @author John Blum
 * @see Test
 * @see SchemaObjectDefinition
 * @since 2.0.0
 */
public class SchemaObjectDefinitionUnitTests {

	private SchemaObjectDefinition newSchemaObjectDefinition(String name) {
		return new TestSchemaObjectDefinition(name);
	}

	@Test
	public void constructSchemaObjectDefinitionWithName() {

		SchemaObjectDefinition schemaObjectDefinition = newSchemaObjectDefinition("TEST");

		assertThat(schemaObjectDefinition).isNotNull();
		assertThat(schemaObjectDefinition.getName()).isEqualTo("TEST");
		assertThat(schemaObjectDefinition.getType()).isEqualTo(SchemaObjectType.UNKNOWN);
	}

	private SchemaObjectDefinition testConstructSchemaObjectDefinitionWithInvalidName(String name) {
		try {
			return newSchemaObjectDefinition(name);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("Name [%s] is required", name);
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructSchemaObjectDefinitionWithNull() {
		testConstructSchemaObjectDefinitionWithInvalidName(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructSchemaObjectDefinitionWithNoName() {
		testConstructSchemaObjectDefinitionWithInvalidName("  ");
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructSchemaObjectDefinitionWithEmptyName() {
		testConstructSchemaObjectDefinitionWithInvalidName("");
	}

	@Test
	public void equalSchemaObjectDefinitionsAreEqual() {

		SchemaObjectDefinition schemaObjectDefinitionOne = newSchemaObjectDefinition("TEST");
		SchemaObjectDefinition schemaObjectDefinitionTwo = newSchemaObjectDefinition("TEST");

		assertThat(schemaObjectDefinitionOne).isNotSameAs(schemaObjectDefinitionTwo);
		assertThat(schemaObjectDefinitionOne).isEqualTo(schemaObjectDefinitionTwo);
	}

	@Test
	public void identicalSchemaObjectDefinitionsAreEquals() {

		SchemaObjectDefinition schemaObjectDefinition = newSchemaObjectDefinition("TEST");

		assertThat(schemaObjectDefinition).isEqualTo(schemaObjectDefinition);
	}

	@Test
	public void unequalSchemaObjectDefinitionsAreNotEqual() {

		SchemaObjectDefinition schemaObjectDefinitionOne = newSchemaObjectDefinition("TEST");
		SchemaObjectDefinition schemaObjectDefinitionTwo = newSchemaObjectDefinition("test");

		assertThat(schemaObjectDefinitionOne).isNotSameAs(schemaObjectDefinitionTwo);
		assertThat(schemaObjectDefinitionOne).isNotEqualTo(schemaObjectDefinitionTwo);
	}

	@Test
	public void equalSchemaObjectDefinitionsHaveTheSameHashCode() {

		SchemaObjectDefinition schemaObjectDefinitionOne = newSchemaObjectDefinition("TEST");
		SchemaObjectDefinition schemaObjectDefinitionTwo = newSchemaObjectDefinition("TEST");

		assertThat(schemaObjectDefinitionOne).isNotSameAs(schemaObjectDefinitionTwo);
		assertThat(schemaObjectDefinitionOne.hashCode()).isEqualTo(schemaObjectDefinitionTwo.hashCode());
	}

	@Test
	public void identicalSchemaObjectDefinitionsHaveTheSameHashCode() {

		SchemaObjectDefinition schemaObjectDefinition = newSchemaObjectDefinition("TEST");

		assertThat(schemaObjectDefinition.hashCode()).isEqualTo(schemaObjectDefinition.hashCode());
	}

	@Test
	public void unequalSchemaObjectDefinitionsHaveDifferentHashCodes() {

		SchemaObjectDefinition schemaObjectDefinitionOne = newSchemaObjectDefinition("TEST");
		SchemaObjectDefinition schemaObjectDefinitionTwo = newSchemaObjectDefinition("test");

		assertThat(schemaObjectDefinitionOne).isNotSameAs(schemaObjectDefinitionTwo);
		assertThat(schemaObjectDefinitionOne.hashCode()).isNotEqualTo(schemaObjectDefinitionTwo.hashCode());
	}

	@Test
	public void toStringPrintsNameAndType() {
		assertThat(newSchemaObjectDefinition("test").toString()).isEqualTo("UNKNOWN[test]");
	}

	private static final class TestSchemaObjectDefinition extends SchemaObjectDefinition {

		private TestSchemaObjectDefinition(String name) {
			super(name);
		}

		@Override
		public int getOrder() {
			return Ordered.LOWEST_PRECEDENCE;
		}

		@Override
		public SchemaObjectType getType() {
			return SchemaObjectType.UNKNOWN;
		}
	}
}
