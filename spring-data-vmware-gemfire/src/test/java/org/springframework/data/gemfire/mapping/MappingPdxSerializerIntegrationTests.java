/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.apache.geode.DataSerializable;
import org.apache.geode.Instantiator;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.pdx.PdxReader;
import org.apache.geode.pdx.PdxSerializer;
import org.apache.geode.pdx.PdxWriter;

import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.data.gemfire.repository.sample.Address;
import org.springframework.data.gemfire.repository.sample.Person;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.model.EntityInstantiator;
import org.springframework.data.util.ClassTypeInformation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Integration Tests for {@link MappingPdxSerializer}.
 *
 * @author Oliver Gierke
 * @author John Blum
 * @see Cache
 * @see Region
 * @see PdxReader
 * @see PdxSerializer
 * @see PdxWriter
 * @see MappingPdxSerializer
 * @see PersistentEntity
 * @see EntityInstantiator
 */
public class MappingPdxSerializerIntegrationTests {

	static Cache cache;

	static Region<Object, Object> region;

	@BeforeClass
	public static void setUp() {

		MappingPdxSerializer serializer = MappingPdxSerializer.newMappingPdxSerializer();

		serializer.setIncludeTypeFilters(type ->
			type.getPackage().getName().startsWith("org.springframework.data.gemfire"));

		cache = new CacheFactory()
			.set("name", MappingPdxSerializerIntegrationTests.class.getSimpleName())
			.set("log-level", "error")
			.setPdxSerializer(serializer)
			.setPdxPersistent(true)
			.create();

		region = cache.createRegionFactory()
			.setDataPolicy(DataPolicy.PARTITION)
			.create("TemporaryRegion");
	}

	@AfterClass
	public static void tearDown() {
		GemfireUtils.close(cache);
	}

	private final GemfireMappingContext mappingContext = new GemfireMappingContext();

	@After
	public void clearRegion() {
		region.removeAll(region.keySet());
	}

	@Test
	public void handlesEntityWithReadOnlyProperty() {

		EntityWithReadOnlyProperty entity = new EntityWithReadOnlyProperty();

		entity.setName("ReadOnlyEntity");
		entity.setTimestamp(LocalDateTime.now());
		entity.processId = 123;

		region.put(100L, entity);

		Object target = region.get(100L);

		assertThat(target).isInstanceOf(EntityWithReadOnlyProperty.class);
		assertThat(target).isNotSameAs(entity);

		EntityWithReadOnlyProperty deserializedEntity = (EntityWithReadOnlyProperty) target;

		assertThat(deserializedEntity.getName()).isEqualTo(entity.getName());
		assertThat(deserializedEntity.getTimestamp()).isEqualTo(entity.getTimestamp());
		assertThat(deserializedEntity.getProcessId()).isNull();
	}

	@Test
	public void handlesEntityWithTransientProperty() {

		EntityWithTransientProperty entity = new EntityWithTransientProperty();

		entity.setName("TransientEntity");
		entity.setValueOne("testOne");
		entity.setValueTwo("testTwo");

		region.put(101L, entity);

		Object target = region.get(101L);

		assertThat(target).isInstanceOf(EntityWithTransientProperty.class);
		assertThat(target).isNotSameAs(entity);

		EntityWithTransientProperty deserializedEntity = (EntityWithTransientProperty) target;

		assertThat(deserializedEntity.getName()).isEqualTo(entity.getName());
		assertThat(deserializedEntity.getValueOne()).isNull();
		assertThat(deserializedEntity.getValueTwo()).isNull();
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void resolveEntityInstantiatorForManagedPersistentEntityWithEntityInstantiator() {

		EntityInstantiator mockEntityInstantiator = mock(EntityInstantiator.class);

		PersistentEntity entity = this.mappingContext.createPersistentEntity(ClassTypeInformation.from(Person.class));

		assertThat(cache.getPdxSerializer()).isInstanceOf(MappingPdxSerializer.class);

		MappingPdxSerializer serializer = ((MappingPdxSerializer) cache.getPdxSerializer());

		try {
			serializer.setEntityInstantiators(Collections.singletonMap(Person.class, mockEntityInstantiator));

			assertThat(serializer.resolveEntityInstantiator(entity)).isEqualTo(mockEntityInstantiator);
		}
		finally {
			serializer.setEntityInstantiators(Collections.emptyMap());
		}

		verifyNoInteractions(mockEntityInstantiator);
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void resolveEntityInstantiatorForNonManagedPersistentEntityWithNoEntityInstantiator() {

		EntityInstantiator mockEntityInstantiator = mock(EntityInstantiator.class);

		PersistentEntity entity = this.mappingContext.createPersistentEntity(ClassTypeInformation.from(Address.class));

		assertThat(cache.getPdxSerializer()).isInstanceOf(MappingPdxSerializer.class);

		MappingPdxSerializer serializer = ((MappingPdxSerializer) cache.getPdxSerializer());

		try {
			serializer.setEntityInstantiators(Collections.singletonMap(Person.class, mockEntityInstantiator));

			assertThat(serializer.resolveEntityInstantiator(entity)).isNotEqualTo(mockEntityInstantiator);
		}
		finally {
			serializer.setEntityInstantiators(Collections.emptyMap());
		}

		verifyNoInteractions(mockEntityInstantiator);
	}

	@Test
	public void serializesAndDeserializesEntity() {

		Address address = new Address();

		address.street = "100 Main St.";
		address.city = "London";
		address.zipCode = "01234";

		Person person = new Person(1L, "Oliver", "Gierke");

		person.address = address;

		region.put(1L, person);

		Object result = region.get(1L);

		assertThat(result).isInstanceOf(Person.class);
		assertThat(result).isNotSameAs(person);

		Person reference = (Person) result;

		assertThat(reference.getFirstname()).isEqualTo(person.getFirstname());
		assertThat(reference.getLastname()).isEqualTo(person.getLastname());
		assertThat(reference.getAddress()).isEqualTo(person.getAddress());
	}

	@Test
	public void serializesAndDeserializesEntityWithDataSerializableProperty() {

		Address address = new Address();

		address.street = "100 Main St.";
		address.city = "London";
		address.zipCode = "01234";

		PersonWithDataSerializableProperty person =
			new PersonWithDataSerializableProperty(2L, "Oliver", "Gierke",
				new DataSerializableProperty("foo"));

		person.address = address;

		region.put(2L, person);

		Object result = region.get(2L);

		assertThat(result).isInstanceOf(PersonWithDataSerializableProperty.class);
		assertThat(result).isNotSameAs(person);

		PersonWithDataSerializableProperty reference = (PersonWithDataSerializableProperty) result;

		assertThat(reference.getFirstname()).isEqualTo(person.getFirstname());
		assertThat(reference.getLastname()).isEqualTo(person.getLastname());
		assertThat(reference.getAddress()).isEqualTo(person.getAddress());
		assertThat(reference.property.getValue()).isEqualTo("foo");
	}

	@Test
	public void serializationUsesCustomPropertyNameBasedPdxSerializer() {

		PdxSerializer mockPasswordSerializer = mock(PdxSerializer.class);

		when(mockPasswordSerializer.toData(any(), any(PdxWriter.class))).thenAnswer(invocation -> {

			String password = invocation.getArgument(0);

			PdxWriter pdxWriter = invocation.getArgument(1);

			pdxWriter.writeByteArray("password", Base64.getEncoder().encode(password.getBytes()));

			return true;
		});

		when(mockPasswordSerializer.fromData(any(Class.class), any(PdxReader.class))).thenAnswer(invocation -> {

			PdxReader pdxReader = invocation.getArgument(1);

			return new String(pdxReader.readByteArray("password"));
		});

		User jonDoe = User.newUser("jdoe", "p@55w0rd!");

		assertThat(jonDoe).isNotNull();
		assertThat(jonDoe.getName()).isEqualTo("jdoe");
		assertThat(jonDoe.getPassword()).isEqualTo("p@55w0rd!");

		Optional.of(region.getRegionService())
			.filter(regionService -> regionService instanceof Cache)
			.map(regionService -> ((Cache) regionService).getPdxSerializer())
			.filter(pdxSerializer -> pdxSerializer instanceof MappingPdxSerializer)
			.ifPresent(pdxSerializer -> {

				String passwordPropertyName = User.class.getName().concat(".password");

				((MappingPdxSerializer) pdxSerializer)
					.setCustomPdxSerializers(Collections.singletonMap(passwordPropertyName, mockPasswordSerializer));

			});

		region.put(4L, jonDoe);

		Object result = region.get(4L);

		assertThat(result).isInstanceOf(User.class);
		assertThat(result).isNotSameAs(jonDoe);

		User jonDoeLoaded = (User) result;

		assertThat(jonDoeLoaded.getName()).isEqualTo(jonDoe.getName());
		assertThat(jonDoeLoaded.getPassword()).describedAs("Password was [%s]", jonDoeLoaded.getPassword())
			.isNotEqualTo(jonDoe.getPassword());
		assertThat(new String(Base64.getDecoder().decode(jonDoeLoaded.getPassword()))).isEqualTo(jonDoe.getPassword());

		verify(mockPasswordSerializer, atLeastOnce()).toData(eq("p@55w0rd!"), isA(PdxWriter.class));

		verify(mockPasswordSerializer, times(1))
			.fromData(eq(String.class), isA(PdxReader.class));
	}

	@Test
	public void serializationUsesCustomPropertyTypeBasedPdxSerializer() {

		PdxSerializer mockCreditCardSerializer = mock(PdxSerializer.class);

		when(mockCreditCardSerializer.toData(any(), any(PdxWriter.class))).thenAnswer(invocation -> {

			CreditCard creditCard = invocation.getArgument(0);

			PdxWriter pdxWriter = invocation.getArgument(1);

			pdxWriter.writeLong("creditCard.expirationDate", creditCard.getExpirationDate().toEpochDay());
			pdxWriter.writeByteArray("creditCard.number",
				Base64.getEncoder().encode(creditCard.getNumber().getBytes()));
			pdxWriter.writeString("creditCard.type", creditCard.getType().name());

			return true;
		});

		when(mockCreditCardSerializer.fromData(any(Class.class), any(PdxReader.class))).thenAnswer(invocation -> {

			PdxReader pdxReader = invocation.getArgument(1);

			LocalDate creditCardExpirationDate =
				LocalDate.ofEpochDay(pdxReader.readLong("creditCard.expirationDate"));

			String creditCardNumber =
				new String(Base64.getDecoder().decode(pdxReader.readByteArray("creditCard.number")));

			creditCardNumber = "xxxx-".concat(creditCardNumber.substring(creditCardNumber.length() - 4));

			CreditCard.Type creditCardType = CreditCard.Type.valueOf(pdxReader.readString("creditCard.type"));

			return CreditCard.of(creditCardExpirationDate, creditCardNumber, creditCardType);
		});

		Optional.of(region.getRegionService())
			.filter(regionService -> regionService instanceof Cache)
			.map(regionService -> ((Cache) regionService).getPdxSerializer())
			.filter(pdxSerializer -> pdxSerializer instanceof MappingPdxSerializer)
			.ifPresent(pdxSerializer -> ((MappingPdxSerializer) pdxSerializer)
				.setCustomPdxSerializers(Collections.singletonMap(CreditCard.class, mockCreditCardSerializer)));

		CreditCard creditCard = CreditCard.of(LocalDate.of(2020, Month.FEBRUARY, 12),
			"8842-6789-4186-7981", CreditCard.Type.VISA);

		Customer jonDoe = Customer.newCustomer(creditCard, "Jon Doe");

		region.put(8L, jonDoe);

		Object result = region.get(8L);

		assertThat(result).isInstanceOf(Customer.class);
		assertThat(result).isNotSameAs(jonDoe);

		Customer jonDoeLoaded = (Customer) result;

		assertThat(jonDoeLoaded.getName()).isEqualTo(jonDoe.getName());
		assertThat(jonDoeLoaded.getCreditCard()).isNotEqualTo(jonDoe.getCreditCard());
		assertThat(jonDoeLoaded.getCreditCard().getExpirationDate())
			.isEqualTo(jonDoe.getCreditCard().getExpirationDate());
		assertThat(jonDoeLoaded.getCreditCard().getNumber()).isEqualTo("xxxx-7981");
		assertThat(jonDoeLoaded.getCreditCard().getType()).isEqualTo(jonDoe.getCreditCard().getType());

		verify(mockCreditCardSerializer, atLeastOnce()).toData(eq(creditCard), isA(PdxWriter.class));

		verify(mockCreditCardSerializer, times(1))
			.fromData(eq(CreditCard.class), isA(PdxReader.class));
	}

	@SuppressWarnings({ "serial", "unused" })
	public static class PersonWithDataSerializableProperty extends Person {

		private DataSerializableProperty property;

		public PersonWithDataSerializableProperty(Long id, String firstname,
				String lastname, DataSerializableProperty property) {

			super(id, firstname, lastname);

			this.property = property;
		}

		public DataSerializableProperty getDataSerializableProperty() {
			return this.property;
		}

		public void setDataSerializableProperty(DataSerializableProperty property) {
			this.property = property;
		}
	}

	@SuppressWarnings("serial")
	public static class DataSerializableProperty implements DataSerializable {

		static {
			Instantiator.register(new Instantiator(DataSerializableProperty.class,101) {
				public DataSerializable newInstance() {
					return new DataSerializableProperty("");
				}
			});
		}

		private String value;

		public DataSerializableProperty(String value) {
			this.value = value;
		}


		@Override
		public void fromData(DataInput dataInput) throws IOException {
			this.value = dataInput.readUTF();

		}

		@Override
		public void toData(DataOutput dataOutput) throws IOException {
			dataOutput.writeUTF(this.value);
		}

		public String getValue() {
			return this.value;
		}
	}

	@Getter
	static class EntityWithReadOnlyProperty {

		@Setter
		LocalDateTime timestamp;

		@Setter
		String name;

		// TODO: if there is no setter, then effectively this field/property is read-only
		// and should not require the @ReadOnlyProperty
		@ReadOnlyProperty
		Object processId;

	}

	@Getter @Setter
	static class EntityWithTransientProperty {

		private String name;

		private transient Object valueOne;

		@Transient
		private Object valueTwo;

	}

	@Data
	@AllArgsConstructor(staticName = "newUser")
	static class User {

		String name;
		String password;

		@SuppressWarnings("unused")
		User() {}

	}

	@Data
	@AllArgsConstructor(staticName = "newCustomer")
	static class Customer {

		CreditCard creditCard;
		String name;

		@SuppressWarnings("unused")
		Customer() {}

	}

	@Data
	@RequiredArgsConstructor(staticName = "of")
	static class CreditCard {

		@NonNull LocalDate expirationDate;
		@NonNull String number;
		@NonNull Type type;

		enum Type {

			AMERICAN_EXPRESS,
			MASTER_CARD,
			VISA,

		}
	}
}
