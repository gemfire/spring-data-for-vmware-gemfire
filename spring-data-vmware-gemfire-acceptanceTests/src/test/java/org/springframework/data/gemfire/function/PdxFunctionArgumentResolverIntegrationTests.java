/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.internal.cache.GemFireCacheImpl;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.PdxInstanceFactory;
import org.apache.geode.pdx.PdxReader;
import org.apache.geode.pdx.PdxSerializer;
import org.apache.geode.pdx.PdxWriter;
import org.apache.geode.pdx.internal.PdxInstanceEnum;
import org.apache.geode.pdx.internal.PdxInstanceFactoryImpl;
import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.util.RuntimeExceptionFactory;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Unit Tests for {@link PdxFunctionArgumentResolver}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.Cache
 * @see org.apache.geode.cache.execute.FunctionContext
 * @see org.apache.geode.pdx.PdxInstance
 * @see org.apache.geode.pdx.PdxInstanceFactory
 * @see org.apache.geode.pdx.PdxSerializer
 * @see org.apache.geode.pdx.internal.PdxInstanceEnum
 * @see org.springframework.data.gemfire.function.PdxFunctionArgumentResolver
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @since 1.5.2
 */
@SuppressWarnings("rawtypes")
public class PdxFunctionArgumentResolverIntegrationTests extends IntegrationTestsSupport {

	private static Cache gemfireCache;

	private PdxFunctionArgumentResolver functionArgumentResolver;

	@BeforeClass
	public static void setupGemFire() {

		gemfireCache = new CacheFactory()
			.setPdxSerializer(new PersonPdxSerializer())
			.setPdxReadSerialized(true)
			.set("name", PdxFunctionArgumentResolverIntegrationTests.class.getSimpleName())
			.set("log-level", "error")
			.create();
	}

	@AfterClass
	public static void tearDown() {
		gemfireCache.close();
		gemfireCache = null;
	}

	private Method getMethod(Class<?> type, String methodName, Class<?>... parameterTypes) {

		try {
			return type.getDeclaredMethod(methodName, parameterTypes);
		}
		catch (NoSuchMethodException cause) {
			throw RuntimeExceptionFactory.newRuntimeException("Failed to get method [%1$s] with signature [%2$s] on Class type [%3$s]",
				methodName, getMethodSignature(methodName, parameterTypes), type.getName());
		}
	}

	private Object getMethodSignature(final String methodName, final Class<?>... parameterTypes) {

		StringBuilder methodSignature = new StringBuilder(methodName);

		int count = 0;

		methodSignature.append("(");

		for (Class<?> parameterType : parameterTypes) {
			methodSignature.append(count++ > 0 ? ", :" : ":").append(parameterType.getSimpleName());
		}

		methodSignature.append("):Void");

		return methodSignature.toString();
	}

	private void assertArguments(final Object[] expectedArguments, final Object[] actualArguments) {

		Assertions.assertThat(actualArguments).isNotNull();
		Assertions.assertThat(actualArguments).isNotSameAs(expectedArguments);
		Assertions.assertThat(actualArguments.length).isEqualTo(expectedArguments.length);

		for (int index = 0; index < expectedArguments.length; index++) {
			Assertions.assertThat(actualArguments[index]).isEqualTo(expectedArguments[index]);
		}
	}

	private Person createPerson(final String firstName, final String lastName, final Gender gender) {
		return new Person(firstName, lastName, gender);
	}

	private PdxInstance toPdxInstance(final Person person) {

		PdxInstanceFactory pdxInstanceFactory = gemfireCache.createPdxInstanceFactory(person.getClass().getName());

		pdxInstanceFactory.writeString("firstName", person.getFirstName());
		pdxInstanceFactory.writeString("lastName", person.getLastName());
		pdxInstanceFactory.writeObject("gender", person.getGender());

		return pdxInstanceFactory.create();
	}

	private PdxInstance toPdxInstance(final Map<String, Object> objectData) {

		PdxInstanceFactory pdxInstanceFactory = gemfireCache.createPdxInstanceFactory(objectData.get("@type").toString());

		for (Map.Entry<String, Object> entry : objectData.entrySet()) {
			if (!"@type".equals(entry.getKey())) {
				pdxInstanceFactory.writeObject(entry.getKey(), entry.getValue());
			}
		}

		return pdxInstanceFactory.create();
	}

	private PdxInstance toPdxInstance(Enum<?> enumeratedType) {

		return PdxInstanceFactoryImpl.createPdxEnum(enumeratedType.getClass().getName(), enumeratedType.name(),
			enumeratedType.ordinal(), (GemFireCacheImpl) gemfireCache);
	}

	@Test
	public void testResolveSimpleFunctionArguments() {

		functionArgumentResolver = new PdxFunctionArgumentResolver() {

			@Override
			public Method getFunctionAnnotatedMethod() {
				return getMethod(FunctionExecutions.class, "simpleMethod", Boolean.class, Character.class,
					Integer.class, Double.class, String.class);
			}
		};

		Object[] expectedArguments = { Boolean.TRUE, 'C', 123, Math.PI, "TEST" };

		FunctionContext mockFunctionContext = Mockito.mock(FunctionContext.class, "testResolveSimpleFunctionArguments");

		Mockito.when(mockFunctionContext.getArguments()).thenReturn(expectedArguments);

		Object[] actualArguments = functionArgumentResolver.resolveFunctionArguments(mockFunctionContext);

		assertArguments(expectedArguments, actualArguments);
	}

	@Test
	public void testResolveNonSerializedApplicationDomainTypeFunctionArguments() {

		functionArgumentResolver = new PdxFunctionArgumentResolver() {

			@Override
			public Method getFunctionAnnotatedMethod() {
				return getMethod(FunctionExecutions.class, "nonSerializedMethod", Boolean.class, Person.class,
					Character.class, Person.class, Integer.class, Double.class, Gender.class, String.class);
			}
		};

		Object[] expectedArguments = { Boolean.TRUE, createPerson("Jon", "Doe", Gender.MALE), 'C',
			createPerson("Jane", "Doe", Gender.FEMALE), 123, Math.PI, Gender.FEMALE, "test" };

		FunctionContext mockFunctionContext = Mockito.mock(FunctionContext.class, "testResolveNonSerializedApplicationDomainTypeFunctionArguments");

		Mockito.when(mockFunctionContext.getArguments()).thenReturn(expectedArguments);

		Object[] actualArguments = functionArgumentResolver.resolveFunctionArguments(mockFunctionContext);

		assertArguments(expectedArguments, actualArguments);
	}

	@Test
	public void testResolveSerializedApplicationDomainTypeFunctionArguments() {

		functionArgumentResolver = new PdxFunctionArgumentResolver() {

			@Override
			public Method getFunctionAnnotatedMethod() {
				return getMethod(FunctionExecutions.class, "serializedMethod", Boolean.class, Person.class,
					String.class, Gender.class);
			}
		};

		Person jackHandy = createPerson("Jack", "Handy", Gender.MALE);

		Object[] serializedArguments = { Boolean.TRUE, toPdxInstance(jackHandy), "test", toPdxInstance(Gender.MALE) };
		Object[] expectedArguments = { Boolean.TRUE, jackHandy, "test", Gender.MALE };

		FunctionContext mockFunctionContext = Mockito.mock(FunctionContext.class, "testResolveSerializedApplicationDomainTypeFunctionArguments");

		Mockito.when(mockFunctionContext.getArguments()).thenReturn(serializedArguments);

		Object[] actualArguments = functionArgumentResolver.resolveFunctionArguments(mockFunctionContext);

		assertArguments(expectedArguments, actualArguments);
	}

	@Test
	public void testResolveUnnecessaryDeserializationFunctionArguments() {

		functionArgumentResolver = new PdxFunctionArgumentResolver() {

			@Override
			public Method getFunctionAnnotatedMethod() {
				return getMethod(FunctionExecutions.class, "unnecessaryDeserializationMethod", Boolean.class,
					Object.class, String.class, PdxInstanceEnum.class);
			}
		};

		Person sandyHandy = createPerson("Sandy", "Handy", Gender.FEMALE);

		Object[] expectedArguments = { Boolean.TRUE, toPdxInstance(sandyHandy), "test", toPdxInstance(Gender.FEMALE) };

		FunctionContext mockFunctionContext = Mockito.mock(FunctionContext.class, "testResolveUnnecessaryDeserializationFunctionArguments");

		Mockito.when(mockFunctionContext.getArguments()).thenReturn(expectedArguments);

		Object[] actualArguments = functionArgumentResolver.resolveFunctionArguments(mockFunctionContext);

		assertArguments(expectedArguments, actualArguments);
	}

	@Test
	public void testResolveUnresolvableApplicationDomainTypeFunctionArguments() {

		functionArgumentResolver = new PdxFunctionArgumentResolver() {

			@Override
			public Method getFunctionAnnotatedMethod() {
				return getMethod(FunctionExecutions.class, "unresolvableMethod", String.class, Object.class);
			}
		};

		Map<String, Object> addressData = new HashMap<>(5);

		addressData.put("@type", "org.example.Address");
		addressData.put("street", "100 Main St.");
		addressData.put("city", "Portland");
		addressData.put("state", "OR");
		addressData.put("zip", "12345");

		Object[] expectedArguments = { "test", toPdxInstance(addressData) };

		FunctionContext mockFunctionContext = Mockito.mock(FunctionContext.class, "testResolveUnresolvableApplicationDomainTypeFunctionArguments");

		Mockito.when(mockFunctionContext.getArguments()).thenReturn(expectedArguments);

		Object[] actualArguments = functionArgumentResolver.resolveFunctionArguments(mockFunctionContext);

		assertArguments(expectedArguments, actualArguments);
	}

	@SuppressWarnings("unused")
	public interface FunctionExecutions {

		void simpleMethod(Boolean value1, Character value2, Integer value3, Double value4, String value5);

		void nonSerializedMethod(Boolean value1, Person person1, Character value2, Integer value3, Person person2, Double value4, Gender gender, String value5);

		void serializedMethod(Boolean value1, Person person, String value2, Gender gender);

		void unnecessaryDeserializationMethod(Boolean value1, Object person, String value2, PdxInstanceEnum gender);

		void unresolvableMethod(String value, Object pdxInstance);

	}

	public enum Gender {
		FEMALE,
		MALE
	}

	public static class Person {

		private final Gender gender;

		private final String firstName;
		private final String lastName;

		public Person(String firstName, String lastName, Gender gender) {

			Assert.hasText(firstName, "The person's first name must be specified");
			Assert.hasText(lastName, "The person's last name must be specified");
			Assert.notNull(gender, "The person's gender must be specified");

			this.firstName = firstName;
			this.lastName = lastName;
			this.gender = gender;
		}

		public String getFirstName() {
			return this.firstName;
		}

		public String getLastName() {
			return this.lastName;
		}

		public Gender getGender() {
			return this.gender;
		}

		@Override
		public boolean equals(Object obj) {

			if (this ==  obj) {
				return true;
			}

			if (!(obj instanceof Person)) {
				return false;
			}

			Person that = (Person) obj;

			return ObjectUtils.nullSafeEquals(this.getFirstName(), that.getFirstName())
				&& ObjectUtils.nullSafeEquals(this.getLastName(), that.getLastName())
				&& ObjectUtils.nullSafeEquals(this.getGender(), that.getGender());
		}

		@Override
		public int hashCode() {

			int hashValue = 17;

			hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getFirstName());
			hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getLastName());
			hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getGender());

			return hashValue;
		}

		@Override
		public String toString() {
			return String.format("%1$s %2$s is a %3$s", getFirstName(), getLastName(), getGender());
		}
	}

	public static class PersonPdxSerializer implements PdxSerializer {

		@Override
		public boolean toData(final Object obj, final PdxWriter out) {

			if (obj instanceof Person) {

				Person person = (Person) obj;

				out.writeString("firstName", person.getFirstName());
				out.writeString("lastName", person.getLastName());
				out.writeObject("gender", person.getGender());

				return true;
			}

			return false;
		}

		@Override
		public Object fromData(final Class<?> type, final PdxReader in) {

			if (Person.class.isAssignableFrom(type)) {
				return new Person(in.readString("firstName"), in.readString("lastName"),
					(Gender) in.readObject("gender"));
			}

			return null;
		}
	}
}
