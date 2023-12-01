/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vmware.gemfire.testcontainers.GemFireCluster;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.pdx.PdxReader;
import org.apache.geode.pdx.PdxSerializer;
import org.apache.geode.pdx.PdxWriter;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.PdxInstanceFactory;
import org.apache.geode.pdx.internal.PdxInstanceEnum;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.function.sample.ApplicationDomainFunctionExecutions;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.testcontainers.utility.MountableFile;

/**
 * Integration Tests for SDG's Function annotation support and interaction between an Apache Geode client
 * and server cache when PDX is configured and read-serialized is set to {@literal true}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.pdx.PdxInstance
 * @see org.apache.geode.pdx.PdxInstanceFactory
 * @see org.apache.geode.pdx.PdxReader
 * @see org.apache.geode.pdx.PdxSerializer
 * @see org.apache.geode.pdx.PdxWriter
 * @see org.apache.geode.pdx.internal.PdxInstanceEnum
 * @see org.springframework.data.gemfire.function.annotation.GemfireFunction
 * @see org.springframework.data.gemfire.function.sample.ApplicationDomainFunctionExecutions
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.5.2
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class ClientCacheFunctionExecutionWithPdxIntegrationTest {

	private static GemFireCluster gemFireCluster;

	@Autowired
	private ClientCache gemfireClientCache;

	@Autowired
	private ApplicationDomainFunctionExecutions functionExecutions;

	@BeforeClass
	public static void startGemFireServer() throws Exception {
		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 1)
				.withCacheXml(GemFireCluster.ALL_GLOB, "/client-cache-function-execution-with-pdx-cache.xml")
				.withClasspath(GemFireCluster.ALL_GLOB, System.getProperty("TEST_JAR_PATH"))
				.withPdx("org\\.springframework\\.data\\.gemfire\\.function\\..*", true)
				.withPreStart(GemFireCluster.ALL_GLOB, container -> container.copyFileToContainer(MountableFile.forHostPath(System.getProperty("TEST_JAR_PATH")), "/testJar.jar"))
				.withGfsh(false, "deploy --jar=/testJar.jar");

		gemFireCluster.acceptLicense().start();

		System.setProperty("spring.data.gemfire.cache.server.port", String.valueOf(gemFireCluster.getServerPorts().get(0)));
	}

	private PdxInstance toPdxInstance(Map<String, Object> pdxData) {

		PdxInstanceFactory pdxInstanceFactory =
			this.gemfireClientCache.createPdxInstanceFactory(pdxData.get("@type").toString());

		for (Map.Entry<String, Object> entry : pdxData.entrySet()) {
			pdxInstanceFactory.writeObject(entry.getKey(), entry.getValue());
		}

		return pdxInstanceFactory.create();
	}

	@Test
	public void unconvertedFunctionArgumentTypes() {

		Class<?>[] argumentTypes = this.functionExecutions
			.captureUnconvertedArgumentTypes("test", 2, Boolean.FALSE, new Person("Jane", "Doe"),
				Gender.FEMALE);

		assertThat(argumentTypes).isNotNull();
		assertThat(argumentTypes.length).isEqualTo(5);
		assertThat(argumentTypes[0]).isEqualTo(String.class);
		assertThat(argumentTypes[1]).isEqualTo(Integer.class);
		assertThat(argumentTypes[2]).isEqualTo(Boolean.class);
		assertThat(PdxInstance.class).isAssignableFrom(argumentTypes[3]);
		assertThat(argumentTypes[4]).isEqualTo(PdxInstanceEnum.class);
	}

	@Test
	public void getAddressFieldValue() {

		Address address = new Address("100 Main St.", "Portland", "OR", "97205");

		assertThat(this.functionExecutions.getAddressField(address, "city")).isEqualTo("Portland");
	}

	@Test
	public void pdxDataFieldValue() {

		Map<String, Object> pdxData = new HashMap<>(3);

		pdxData.put("@type", "x.y.z.domain.MyApplicationDomainType");
		pdxData.put("booleanField", Boolean.TRUE);
		pdxData.put("integerField", 123);
		pdxData.put("stringField", "test");

		Integer value = this.functionExecutions.getDataField(toPdxInstance(pdxData), "integerField");

		assertThat(value).isEqualTo(pdxData.get("integerField"));
	}

	private static Class<?>[] getArgumentTypes(Object... arguments) {

		List<Class<?>> argumentTypes = new ArrayList<>();

		for (Object argument : arguments) {
			argumentTypes.add(argument.getClass());
		}

		return argumentTypes.toArray(new Class[0]);
	}

	public static class CaptureUnconvertedArgumentTypesFunction implements Function<Class<?>[]> {

		@Override
		public void execute(FunctionContext<Class<?>[]> context) {
			Object[] args = context.getArguments();

			String stringValue = (String) args[0];
			Integer integerValue = (Integer) args[1];
			Boolean booleanValue = (Boolean) args[2];
			Object domainObject = args[3];
			Object enumValue = args[4];


			context.getResultSender().lastResult(getArgumentTypes(stringValue, integerValue, booleanValue, domainObject, enumValue));
		}

		@Override
		public String getId() {
			return "captureUnconvertedArgumentTypes";
		}
	}


	public static class DataFieldFunction implements Function<Object> {

		@Override
		public String getId() {
			return "getDataField";
		}

		@Override
		public void execute(FunctionContext<Object> context) {
			Object[] args = (Object[]) context.getArguments();

			PdxInstance data = (PdxInstance) args[0];
			String fieldName = (String) args[1];

			context.getResultSender().lastResult(data.getField(fieldName));
		}
	}

	public static class AddressFieldFunction implements Function<Object> {

		@Override
		public String getId() {
			return "getAddressField";
		}

		@Override
		public void execute(FunctionContext<Object> context) {
			Object[] args = (Object[]) context.getArguments();

			PdxInstance address = (PdxInstance) args[0];
			String fieldName = (String) args[1];

			Assert.isTrue(Address.class.getName().equals(address.getClassName()),
					"Address is not the correct type");

			context.getResultSender().lastResult(String.valueOf(address.getField(fieldName)));
		}
	}

	public static class Address {

		private final String street;
		private final String city;
		private final String state; // Refactor; use Enum!
		private final String zipCode;

		public Address(String street, String city, String state, String zipCode) {

			Assert.hasText("Street is required", street);
			Assert.hasText("City is required", city);
			Assert.hasText("State is required", state);
			Assert.hasText("ZipCode is required", zipCode);

			this.street = street;
			this.city = city;
			this.state = state;
			this.zipCode = zipCode;
		}

		public String getStreet() {
			return street;
		}

		public String getCity() {
			return city;
		}

		public String getState() {
			return state;
		}

		public String getZipCode() {
			return zipCode;
		}

		@Override
		public boolean equals(final Object obj) {

			if (obj == this) {
				return true;
			}

			if (!(obj instanceof Address)) {
				return false;
			}

			Address that = (Address) obj;

			return ObjectUtils.nullSafeEquals(this.getStreet(), that.getStreet())
				&& ObjectUtils.nullSafeEquals(this.getCity(), that.getCity())
				&& ObjectUtils.nullSafeEquals(this.getState(), that.getState())
				&& ObjectUtils.nullSafeEquals(this.getZipCode(), that.getZipCode());
		}

		@Override
		public int hashCode() {

			int hashValue = 17;

			hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getStreet());
			hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getCity());
			hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getState());
			hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getZipCode());

			return hashValue;
		}

		@Override
		public String toString() {
			return String.format("%1$s %2$s, %3$s %4$s", getStreet(), getCity(), getState(), getZipCode());
		}
	}

	public enum Gender {
		FEMALE,
		MALE
	}

	public static class Person {

		private final String firstName;
		private final String lastName;

		public Person(String firstName, String lastName) {

			Assert.hasText(firstName, "First name is required");
			Assert.hasText(lastName, "Last name is required");

			this.firstName = firstName;
			this.lastName = lastName;
		}

		public String getFirstName() {
			return firstName;
		}

		public String getLastName() {
			return lastName;
		}

		@Override
		public boolean equals(final Object obj) {

			if (obj == this) {
				return true;
			}

			if (!(obj instanceof Person)) {
				return false;
			}

			Person that = (Person) obj;

			return ObjectUtils.nullSafeEquals(this.getFirstName(), that.getFirstName())
				&& ObjectUtils.nullSafeEquals(this.getLastName(), that.getLastName());
		}

		@Override
		public int hashCode() {

			int hashValue = 17;

			hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getFirstName());
			hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getLastName());

			return hashValue;
		}

		@Override
		public String toString() {
			return String.format("%1$s %2$s", getFirstName(), getLastName());
		}
	}

	public static class ComposablePdxSerializer implements PdxSerializer {

		private final PdxSerializer[] pdxSerializers;

		private ComposablePdxSerializer(PdxSerializer[] pdxSerializers) {
			this.pdxSerializers = pdxSerializers;
		}

		public static PdxSerializer compose(PdxSerializer... pdxSerializers) {

			return pdxSerializers == null
				? null
				: pdxSerializers.length == 1
				? pdxSerializers[0]
				: new ComposablePdxSerializer(pdxSerializers);
		}

		@Override
		public boolean toData(Object obj, PdxWriter out) {

			for (PdxSerializer pdxSerializer : this.pdxSerializers) {
				if (pdxSerializer.toData(obj, out)) {
					return true;
				}
			}

			return false;
		}

		@Override
		public Object fromData(Class<?> type, final PdxReader in) {

			for (PdxSerializer pdxSerializer : this.pdxSerializers) {

				Object obj = pdxSerializer.fromData(type, in);

				if (obj != null) {
					return obj;
				}
			}

			return null;
		}
	}

	public static class ComposablePdxSerializerFactoryBean implements FactoryBean<PdxSerializer>, InitializingBean {

		private List<PdxSerializer> pdxSerializers = Collections.emptyList();

		private PdxSerializer pdxSerializer;

		public void setPdxSerializers(List<PdxSerializer> pdxSerializers) {
			this.pdxSerializers = pdxSerializers;
		}

		@Override
		public void afterPropertiesSet() {
			this.pdxSerializer = ComposablePdxSerializer.compose(this.pdxSerializers.toArray(new PdxSerializer[0]));
		}

		@Override
		public PdxSerializer getObject() {
			return this.pdxSerializer;
		}

		@Override
		public Class<?> getObjectType() {
			return this.pdxSerializer != null ? this.pdxSerializer.getClass() : PdxSerializer.class;
		}

		@Override
		public boolean isSingleton() {
			return true;
		}
	}

	public static class AddressPdxSerializer implements PdxSerializer {

		@Override
		public boolean toData(Object obj, PdxWriter out) {

			if (obj instanceof Address) {

				Address address = (Address) obj;

				out.writeString("street", address.getStreet());
				out.writeString("city", address.getCity());
				out.writeString("state", address.getState());
				out.writeString("zipCode", address.getZipCode());

				return true;
			}

			return false;
		}

		@Override
		public Object fromData(Class<?> type, PdxReader in) {

			if (Address.class.isAssignableFrom(type)) {
				return new Address(in.readString("street"), in.readString("city"), in.readString("state"),
					in.readString("zipCode"));
			}

			return null;
		}
	}

	public static class PersonPdxSerializer implements PdxSerializer, Declarable {

		@Override
		public boolean toData(Object obj, PdxWriter out) {

			if (obj instanceof Person) {

				Person person = (Person) obj;

				out.writeString("firstName", person.getFirstName());
				out.writeString("lastName", person.getLastName());

				return true;
			}

			return false;
		}

		@Override
		public Object fromData(Class<?> type, PdxReader in) {

			if (Person.class.isAssignableFrom(type)) {
				return new Person(in.readString("firstName"), in.readString("lastName"));
			}

			return null;
		}
	}
}
