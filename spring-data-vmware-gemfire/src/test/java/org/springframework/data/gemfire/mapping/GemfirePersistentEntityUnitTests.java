/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.Region;
import org.springframework.data.mapping.IdentifierAccessor;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.util.ClassTypeInformation;

/**
 * Unit Tests for {@link GemfirePersistentEntity}.
 *
 * @author Oliver Gierke
 * @author John Blum
 * @author Gregory Green
 * @see org.junit.Test
 * @see org.springframework.data.gemfire.mapping.GemfirePersistentEntity
 */
public class GemfirePersistentEntityUnitTests {

	private final GemfireMappingContext mappingContext = new GemfireMappingContext();

	protected IdentifierAccessor getIdentifierAccessor(Object domainObject) {
		return getMappingContextPersistentEntity(domainObject).getIdentifierAccessor(domainObject);
	}

	@SuppressWarnings("unchecked")
	protected <T> GemfirePersistentEntity<T> getMappingContextPersistentEntity(Object domainObject) {
		return this.getMappingContextPersistentEntity((Class<T>) domainObject.getClass());
	}

	@SuppressWarnings("unchecked")
	protected <T> GemfirePersistentEntity<T> getMappingContextPersistentEntity(Class<T> type) {
		return (GemfirePersistentEntity<T>) this.mappingContext.getPersistentEntity(type);
	}

	protected <T> GemfirePersistentEntity<T> newPersistentEntity(Class<T> type) {
		return new GemfirePersistentEntity<>(ClassTypeInformation.from(type));
	}

	@Test
	public void defaultsRegionNameForNonRegionAnnotatedEntityToClassName() {
		assertThat(newPersistentEntity(NonRegionAnnotatedEntity.class).getRegionName())
			.isEqualTo(NonRegionAnnotatedEntity.class.getSimpleName());
	}

	@Test
	public void defaultsRegionNameForUnnamedRegionAnnotatedEntityToClassName() {
		assertThat(newPersistentEntity(UnnamedRegionAnnotatedEntity.class).getRegionName())
			.isEqualTo(UnnamedRegionAnnotatedEntity.class.getSimpleName());
	}

	@Test
	public void returnsGivenNameForNamedRegionAnnotatedEntityAsRegionName() {
		assertThat(newPersistentEntity(NamedRegionAnnotatedEntity.class).getRegionName()).isEqualTo("Foo");
	}

	@Test
	public void bigDecimalPersistentPropertyIsNotAnEntity() {

		GemfirePersistentEntity<ExampleDomainObject> entity =
			getMappingContextPersistentEntity(ExampleDomainObject.class);

		assertThat(entity).isNotNull();
		assertThat(entity.getRegionName()).isEqualTo("Example");

		GemfirePersistentProperty currency = entity.getPersistentProperty("currency");

		assertThat(currency).isNotNull();
		assertThat(currency.isEntity()).isFalse();
	}

	@Test
	public void bigIntegerPersistentPropertyIsNotAnEntity() {

		GemfirePersistentEntity<ExampleDomainObject> entity =
			getMappingContextPersistentEntity(ExampleDomainObject.class);

		assertThat(entity).isNotNull();
		assertThat(entity.getRegionName()).isEqualTo("Example");

		GemfirePersistentProperty bigNumber = entity.getPersistentProperty("bigNumber");

		assertThat(bigNumber).isNotNull();
		assertThat(bigNumber.isEntity()).isFalse();
	}

	/**
	 * <a href="https://jira.spring.io/browse/SGF-582">SGF-582</a>
	 */
	@Test
	public void identifierForNonIdAnnotatedEntityWithNoIdFieldOrPropertyIsNull() {

		IdentifierAccessor identifierAccessor = getIdentifierAccessor(new NonRegionAnnotatedEntity());

		assertThat(identifierAccessor.getIdentifier()).isNull();
	}

	/**
	 * <a href="https://jira.spring.io/browse/SGF-582">SGF-582</a>
	 */
	@Test
	public void identifierForNonIdAnnotatedEntityWithIdFieldIsNotNull() {

		IdentifierAccessor identifierAccessor = getIdentifierAccessor(new NonIdAnnotatedIdFieldEntity());

		assertThat(identifierAccessor.getIdentifier()).isEqualTo(123L);
	}

	/**
	 * <a href="https://jira.spring.io/browse/SGF-582">SGF-582</a>
	 */
	@Test
	public void identifierForNonIdAnnotatedEntityWithIdPropertyIsNotNull() {

		IdentifierAccessor identifierAccessor = getIdentifierAccessor(new NonIdAnnotatedIdGetterEntity());

		assertThat(identifierAccessor.getIdentifier()).isEqualTo(456L);
	}

	@Test
	public void identifierForIdAnnotatedFieldAndPropertyEntityShouldNotConflict() {

		IdentifierAccessor identifierAccessor = getIdentifierAccessor(new IdAnnotatedFieldAndPropertyEntity());

		assertThat(identifierAccessor.getIdentifier()).isEqualTo(1L);
	}

	@Test(expected = MappingException.class)
	public void identifierForAmbiguousIdAnnotatedFieldAndIdAnnotatedPropertyEntityThrowsMappingException() {

		AmbiguousIdAnnotatedFieldAndIdAnnotatedPropertyEntity entity =
			new AmbiguousIdAnnotatedFieldAndIdAnnotatedPropertyEntity();

		try {
			getIdentifierAccessor(new AmbiguousIdAnnotatedFieldAndIdAnnotatedPropertyEntity());
		}
		catch (MappingException expected) {

			assertThat(expected).hasMessage("Attempt to add explicit id property [ssn] but already have id property [id] registered as explicit;"
				+ " Please check your object [%s] mapping configuration", entity.getClass().getName());

			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@SuppressWarnings("unused")
	static class AmbiguousIdAnnotatedFieldAndIdAnnotatedPropertyEntity {

		@Id
		private final Long id = 1L;

		@Id
		public String getSsn() {
			return "123456789";
		}
	}

	static class IdAnnotatedFieldAndPropertyEntity {

		@Id
		private final Long id = 1L;

		@Id
		public Long getId() {
			return this.id;
		}
	}

	@SuppressWarnings("unused")
	static class NonIdAnnotatedIdFieldEntity {
		private final Long id = 123L;
	}

	static class NonIdAnnotatedIdGetterEntity {
		public Long getId() {
			return 456L;
		}
	}

	static class NonRegionAnnotatedEntity { }

	@Region("Foo")
	static class NamedRegionAnnotatedEntity { }

	@Region
	static class UnnamedRegionAnnotatedEntity { }

	@Region("Example")
	@SuppressWarnings("unused")
	static class ExampleDomainObject {

		private BigDecimal currency;

		private BigInteger bigNumber;

		public BigDecimal getCurrency() {
			return currency;
		}

		public BigInteger getBigNumber() {
			return bigNumber;
		}
	}
}
