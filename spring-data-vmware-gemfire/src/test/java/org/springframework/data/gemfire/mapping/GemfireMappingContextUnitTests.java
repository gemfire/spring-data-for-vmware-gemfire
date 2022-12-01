/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

import org.springframework.data.gemfire.mapping.annotation.Region;

import lombok.Data;

/**
 * Unit tests for {@link GemfireMappingContext} class.
 *
 * @author John Blum
 * @see Test
 * @see GemfireMappingContext
 * @since 1.6.3
 */
public class GemfireMappingContextUnitTests {

	private GemfireMappingContext mappingContext = new GemfireMappingContext();

	@Test
	@SuppressWarnings("unchecked")
	public void getPersistentEntityForPerson() throws Exception {

		GemfirePersistentEntity<Person> personPersistentEntity =
			(GemfirePersistentEntity<Person>) mappingContext.getPersistentEntity(Person.class);

		assertThat(personPersistentEntity).isNotNull();
		assertThat(personPersistentEntity.getRegionName()).isEqualTo("People");

		GemfirePersistentProperty namePersistentProperty = personPersistentEntity.getPersistentProperty("name");

		assertThat(namePersistentProperty).isNotNull();
		assertThat(namePersistentProperty.isEntity()).isFalse();
		assertThat(namePersistentProperty.getName()).isEqualTo("name");
		assertThat(namePersistentProperty.getOwner()).isEqualTo(personPersistentEntity);
	}

	@Test
	public void getPersistentEntityForBigDecimal() {
		assertThat(mappingContext.getPersistentEntity(BigDecimal.class)).isNull();
	}

	@Test
	public void getPersistentEntityForBigInteger() {
		assertThat(mappingContext.getPersistentEntity(BigInteger.class)).isNull();
	}

	@Data
	@Region("People")
	class Person {
		private String name;
	}
}
