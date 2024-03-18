/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.mapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;

import org.apache.geode.DataSerializable;
import org.apache.geode.Instantiator;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.pdx.PdxReader;
import org.apache.geode.pdx.PdxSerializer;
import org.apache.geode.pdx.PdxWriter;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.data.gemfire.repository.sample.Address;
import org.springframework.data.gemfire.repository.sample.Person;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.model.EntityInstantiator;
import org.springframework.data.util.TypeInformation;

/**
 * Integration Tests for {@link MappingPdxSerializer}.
 *
 * @author Oliver Gierke
 * @author John Blum
 * @see org.apache.geode.cache.Cache
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.pdx.PdxReader
 * @see org.apache.geode.pdx.PdxSerializer
 * @see org.apache.geode.pdx.PdxWriter
 * @see org.springframework.data.gemfire.mapping.MappingPdxSerializer
 * @see org.springframework.data.mapping.PersistentEntity
 * @see org.springframework.data.mapping.model.EntityInstantiator
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

		Assertions.assertThat(target).isInstanceOf(EntityWithReadOnlyProperty.class);
		Assertions.assertThat(target).isNotSameAs(entity);

		EntityWithReadOnlyProperty deserializedEntity = (EntityWithReadOnlyProperty) target;

		Assertions.assertThat(deserializedEntity.getName()).isEqualTo(entity.getName());
		Assertions.assertThat(deserializedEntity.getTimestamp()).isEqualTo(entity.getTimestamp());
		Assertions.assertThat(deserializedEntity.getProcessId()).isNull();
	}

	@Test
	public void handlesEntityWithTransientProperty() {

		EntityWithTransientProperty entity = new EntityWithTransientProperty();

		entity.setName("TransientEntity");
		entity.setValueOne("testOne");
		entity.setValueTwo("testTwo");

		region.put(101L, entity);

		Object target = region.get(101L);

		Assertions.assertThat(target).isInstanceOf(EntityWithTransientProperty.class);
		Assertions.assertThat(target).isNotSameAs(entity);

		EntityWithTransientProperty deserializedEntity = (EntityWithTransientProperty) target;

		Assertions.assertThat(deserializedEntity.getName()).isEqualTo(entity.getName());
		Assertions.assertThat(deserializedEntity.getValueOne()).isNull();
		Assertions.assertThat(deserializedEntity.getValueTwo()).isNull();
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void resolveEntityInstantiatorForManagedPersistentEntityWithEntityInstantiator() {

		EntityInstantiator mockEntityInstantiator = Mockito.mock(EntityInstantiator.class);

		PersistentEntity entity = this.mappingContext.createPersistentEntity(TypeInformation.of(Person.class));

		Assertions.assertThat(cache.getPdxSerializer()).isInstanceOf(MappingPdxSerializer.class);

		MappingPdxSerializer serializer = ((MappingPdxSerializer) cache.getPdxSerializer());

		try {
			serializer.setEntityInstantiators(Collections.singletonMap(Person.class, mockEntityInstantiator));

			Assertions.assertThat(serializer.resolveEntityInstantiator(entity)).isEqualTo(mockEntityInstantiator);
		}
		finally {
			serializer.setEntityInstantiators(Collections.emptyMap());
		}

		Mockito.verifyNoInteractions(mockEntityInstantiator);
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void resolveEntityInstantiatorForNonManagedPersistentEntityWithNoEntityInstantiator() {

		EntityInstantiator mockEntityInstantiator = Mockito.mock(EntityInstantiator.class);

		PersistentEntity entity = this.mappingContext.createPersistentEntity(TypeInformation.of(Address.class));

		Assertions.assertThat(cache.getPdxSerializer()).isInstanceOf(MappingPdxSerializer.class);

		MappingPdxSerializer serializer = ((MappingPdxSerializer) cache.getPdxSerializer());

		try {
			serializer.setEntityInstantiators(Collections.singletonMap(Person.class, mockEntityInstantiator));

			Assertions.assertThat(serializer.resolveEntityInstantiator(entity)).isNotEqualTo(mockEntityInstantiator);
		}
		finally {
			serializer.setEntityInstantiators(Collections.emptyMap());
		}

		Mockito.verifyNoInteractions(mockEntityInstantiator);
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

		Assertions.assertThat(result).isInstanceOf(Person.class);
		Assertions.assertThat(result).isNotSameAs(person);

		Person reference = (Person) result;

		Assertions.assertThat(reference.getFirstname()).isEqualTo(person.getFirstname());
		Assertions.assertThat(reference.getLastname()).isEqualTo(person.getLastname());
		Assertions.assertThat(reference.getAddress()).isEqualTo(person.getAddress());
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

		Assertions.assertThat(result).isInstanceOf(PersonWithDataSerializableProperty.class);
		Assertions.assertThat(result).isNotSameAs(person);

		PersonWithDataSerializableProperty reference = (PersonWithDataSerializableProperty) result;

		Assertions.assertThat(reference.getFirstname()).isEqualTo(person.getFirstname());
		Assertions.assertThat(reference.getLastname()).isEqualTo(person.getLastname());
		Assertions.assertThat(reference.getAddress()).isEqualTo(person.getAddress());
		Assertions.assertThat(reference.property.getValue()).isEqualTo("foo");
	}

	@Test
	public void serializationUsesCustomPropertyNameBasedPdxSerializer() {

		PdxSerializer mockPasswordSerializer = Mockito.mock(PdxSerializer.class);

		Mockito.when(mockPasswordSerializer.toData(ArgumentMatchers.any(), ArgumentMatchers.any(PdxWriter.class))).thenAnswer(invocation -> {

			String password = invocation.getArgument(0);

			PdxWriter pdxWriter = invocation.getArgument(1);

			pdxWriter.writeByteArray("password", Base64.getEncoder().encode(password.getBytes()));

			return true;
		});

		Mockito.when(mockPasswordSerializer.fromData(ArgumentMatchers.any(Class.class), ArgumentMatchers.any(PdxReader.class))).thenAnswer(invocation -> {

			PdxReader pdxReader = invocation.getArgument(1);

			return new String(pdxReader.readByteArray("password"));
		});

		User jonDoe = User.newUser("jdoe", "p@55w0rd!");

		Assertions.assertThat(jonDoe).isNotNull();
		Assertions.assertThat(jonDoe.getName()).isEqualTo("jdoe");
		Assertions.assertThat(jonDoe.getPassword()).isEqualTo("p@55w0rd!");

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

		Assertions.assertThat(result).isInstanceOf(User.class);
		Assertions.assertThat(result).isNotSameAs(jonDoe);

		User jonDoeLoaded = (User) result;

		Assertions.assertThat(jonDoeLoaded.getName()).isEqualTo(jonDoe.getName());
		Assertions.assertThat(jonDoeLoaded.getPassword()).describedAs("Password was [%s]", jonDoeLoaded.getPassword())
			.isNotEqualTo(jonDoe.getPassword());
		Assertions.assertThat(new String(Base64.getDecoder().decode(jonDoeLoaded.getPassword()))).isEqualTo(jonDoe.getPassword());

		Mockito.verify(mockPasswordSerializer, Mockito.atLeastOnce()).toData(ArgumentMatchers.eq("p@55w0rd!"), ArgumentMatchers.isA(PdxWriter.class));

		Mockito.verify(mockPasswordSerializer, Mockito.times(1))
			.fromData(ArgumentMatchers.eq(String.class), ArgumentMatchers.isA(PdxReader.class));
	}

	@Test
	public void serializationUsesCustomPropertyTypeBasedPdxSerializer() {

		PdxSerializer mockCreditCardSerializer = Mockito.mock(PdxSerializer.class);

		Mockito.when(mockCreditCardSerializer.toData(ArgumentMatchers.any(), ArgumentMatchers.any(PdxWriter.class))).thenAnswer(invocation -> {

			CreditCard creditCard = invocation.getArgument(0);

			PdxWriter pdxWriter = invocation.getArgument(1);

			pdxWriter.writeLong("creditCard.expirationDate", creditCard.getExpirationDate().toEpochDay());
			pdxWriter.writeByteArray("creditCard.number",
				Base64.getEncoder().encode(creditCard.getNumber().getBytes()));
			pdxWriter.writeString("creditCard.type", creditCard.getType().name());

			return true;
		});

		Mockito.when(mockCreditCardSerializer.fromData(ArgumentMatchers.any(Class.class), ArgumentMatchers.any(PdxReader.class))).thenAnswer(invocation -> {

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

		Assertions.assertThat(result).isInstanceOf(Customer.class);
		Assertions.assertThat(result).isNotSameAs(jonDoe);

		Customer jonDoeLoaded = (Customer) result;

		Assertions.assertThat(jonDoeLoaded.getName()).isEqualTo(jonDoe.getName());
		Assertions.assertThat(jonDoeLoaded.getCreditCard()).isNotEqualTo(jonDoe.getCreditCard());
		Assertions.assertThat(jonDoeLoaded.getCreditCard().getExpirationDate())
			.isEqualTo(jonDoe.getCreditCard().getExpirationDate());
		Assertions.assertThat(jonDoeLoaded.getCreditCard().getNumber()).isEqualTo("xxxx-7981");
		Assertions.assertThat(jonDoeLoaded.getCreditCard().getType()).isEqualTo(jonDoe.getCreditCard().getType());

		Mockito.verify(mockCreditCardSerializer, Mockito.atLeastOnce()).toData(ArgumentMatchers.eq(creditCard), ArgumentMatchers.isA(PdxWriter.class));

		Mockito.verify(mockCreditCardSerializer, Mockito.times(1))
			.fromData(ArgumentMatchers.eq(CreditCard.class), ArgumentMatchers.isA(PdxReader.class));
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
