// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.Serializable;
import java.util.List;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.query.QueryService;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.DriverIndexType;
import com.vmware.gemfire.driver.spi.DriverMultiIndexCreationException;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.DriverSelectResults;
import com.vmware.gemfire.driver.spi.DriverStruct;
import com.vmware.gemfire.driver.spi.DriverUndefined;
import com.vmware.gemfire.driver.spi.GemFireDriverException;
import com.vmware.gemfire.driver.spi.PoolConfiguration;
import com.vmware.gemfire.driver.spi.PoolConfiguration.Endpoint;

/** Query, struct, select-result and index mapping against the native engine using LOCAL client regions. */
@Timeout(180)
class NativeQueryTests {

	public static class Person implements Serializable {
		private final int id;
		private final String name;
		public Person(int id, String name) { this.id = id; this.name = name; }
		public int getId() { return id; }
		public String getName() { return name; }
	}

	private static ClientCache newCache() { return new ClientCacheFactory().set("log-level", "error").create(); }

	/** The mapper wraps argument failures as INVALID_ARGUMENT operation failures rather than rethrowing them. */
	private static void assertInvalidArgument(ThrowingCallable call) {
		assertFailureKind(call, DriverFailureKind.INVALID_ARGUMENT);
	}

	private static void assertFailureKind(ThrowingCallable call, DriverFailureKind kind) {
		assertThatThrownBy(call).isInstanceOfSatisfying(DriverOperationException.class,
			failure -> assertThat(failure.kind()).isEqualTo(kind));
	}

	private static GemFireConnection connectionWithPeople(ClientCache cache) {
		cache.createClientRegionFactory(ClientRegionShortcut.LOCAL).create("people");
		var connection = new GemFireConnection(cache);
		var people = connection.<Integer, Person>getRegion("people");
		people.put(1, new Person(1, "ada"));
		people.put(2, new Person(2, "grace"));
		people.put(3, new Person(3, "edsger"));
		return connection;
	}

	@Test
	void queryReturnsAnUnmodifiableSnapshotOfSelectResults() {
		try (ClientCache cache = newCache()) {
			var service = connectionWithPeople(cache).getLocalQueryService();
			Object result = service.newQuery("select p.id from /people p where p.id > 1").execute();
			assertThat(result).isInstanceOf(DriverSelectResults.class);
			@SuppressWarnings("unchecked") var results = (DriverSelectResults<Object>) result;
			assertThat(results).containsExactlyInAnyOrder(2, 3);
			assertThat(results.isModifiable()).isFalse();
			assertThatThrownBy(() -> results.add(9)).isInstanceOf(UnsupportedOperationException.class);
			assertThat(results.asSet()).containsExactlyInAnyOrder(2, 3);
		}
	}

	@Test
	void structQueriesExposeFieldNamesAndValues() {
		try (ClientCache cache = newCache()) {
			var service = connectionWithPeople(cache).getLocalQueryService();
			@SuppressWarnings("unchecked")
			var results = (DriverSelectResults<Object>) service
				.newQuery("select p.id as id, p.name as name from /people p where p.id = 2").execute();
			assertThat(results).hasSize(1);
			var struct = (DriverStruct) results.iterator().next();
			assertThat(struct.get("id")).isEqualTo(2);
			assertThat(struct.get("name")).isEqualTo("grace");
			assertThat(struct.fieldValues()).containsExactly(2, "grace");
			assertThat(struct.structType()).isNotNull();
		}
	}

	@Test
	void parameterisedQueriesBindArgumentsAndRejectNulls() {
		try (ClientCache cache = newCache()) {
			var query = connectionWithPeople(cache).getLocalQueryService().newQuery("select p.name from /people p where p.id = $1");
			@SuppressWarnings("unchecked") var results = (DriverSelectResults<Object>) query.execute(3);
			assertThat(results).containsExactly("edsger");
			assertInvalidArgument(() -> query.execute((Object[]) null));
			assertInvalidArgument(() -> query.execute(new Object[] { null }));
		}
	}

	@Test
	void nativeUndefinedIsMappedToTheDriverSentinel() {
		assertThat(GemFireQueryValues.convert(QueryService.UNDEFINED)).isSameAs(DriverUndefined.INSTANCE);
		assertThat(GemFireQueryValues.convert("plain")).isEqualTo("plain");
	}

	@Test
	void regionQueryHelpersMapToTheNativeShortcuts() {
		try (ClientCache cache = newCache()) {
			var people = connectionWithPeople(cache).<Integer, Person>getRegion("people");
			assertThat(people.query("id >= 2")).hasSize(2);
			assertThat(people.existsValue("id = 1")).isTrue();
			assertThat(people.existsValue("id = 99")).isFalse();
			assertThat(((Person) people.selectValue("id = 3")).getName()).isEqualTo("edsger");
			assertThat(people.selectValue("id = 99")).isNull();
			assertInvalidArgument(() -> people.query(null));
		}
	}

	@Test
	void indexLifecycleCreatesListsAndRemovesIndexes() {
		try (ClientCache cache = newCache()) {
			var service = connectionWithPeople(cache).getLocalQueryService();
			var index = service.createIndex("byId", DriverIndexType.FUNCTIONAL, "id", "/people", null);
			assertThat(index.name()).isEqualTo("byId");
			assertThat(index.type()).isEqualTo(DriverIndexType.FUNCTIONAL);
			assertThat(index.regionPath()).isEqualTo("/people");
			assertThat(index.indexedExpression()).isEqualTo("id");
			assertThat(index.isValid()).isTrue();
			assertThat(index.statistics().numberOfKeys()).isEqualTo(3);
			assertThat(service.getIndex("/people", "byId").name()).isEqualTo("byId");
			assertThat(service.getIndexes("/people")).extracting(i -> i.name()).containsExactly("byId");
			assertThat(service.getIndexes("/people", DriverIndexType.FUNCTIONAL)).hasSize(1);
			assertThat(service.getIndexes()).hasSize(1);
			service.removeIndex(index);
			assertThat(service.getIndexes()).isEmpty();
			service.createIndex("again", DriverIndexType.FUNCTIONAL, "id", "/people", null);
			service.removeIndexes("/people");
			assertThat(service.getIndexes("/people")).isEmpty();
		}
	}

	@Test
	void primaryKeyIndexesRejectImportsAndUnknownRegionsAreMapped() {
		try (ClientCache cache = newCache()) {
			var service = connectionWithPeople(cache).getLocalQueryService();
			assertInvalidArgument(() -> service.createIndex("pk", DriverIndexType.PRIMARY_KEY, "id", "/people", "import x.Y;"));
			// The engine resolves the region from the from-clause and reports a missing one as a query failure.
			assertFailureKind(() -> service.createIndex("none", DriverIndexType.FUNCTIONAL, "id", "/absent", null),
				DriverFailureKind.QUERY_FAILURE);
			// Region-path lookups done by the adapter itself reject unknown regions as invalid arguments.
			assertInvalidArgument(() -> service.getIndexes("/absent"));
		}
	}

	@Test
	void definedIndexesAreCreatedTogetherAndFailuresAreMappedPerIndexName() {
		try (ClientCache cache = newCache()) {
			var service = connectionWithPeople(cache).getLocalQueryService();
			service.defineIndex("good", DriverIndexType.FUNCTIONAL, "id", "/people", null);
			assertThat(service.createDefinedIndexes()).extracting(i -> i.name()).containsExactly("good");
			assertThat(service.clearDefinedIndexes()).isTrue();
			// Clearing drops only pending definitions; remove the created index so "taken" below is not a duplicate definition.
			service.removeIndexes("/people");
			// An existing index name with a different definition is a deterministic per-name native failure.
			service.createIndex("taken", DriverIndexType.FUNCTIONAL, "id", "/people", null);
			service.defineIndex("taken", DriverIndexType.FUNCTIONAL, "name", "/people", null);
			assertThatThrownBy(service::createDefinedIndexes).isInstanceOfSatisfying(DriverMultiIndexCreationException.class, failure -> {
				assertThat(failure.failedIndexNames()).containsExactly("taken");
				assertThat(failure.failures().get("taken").kind()).isEqualTo(DriverFailureKind.INDEX_FAILURE);
				assertThat(failure.getMessage()).isEqualTo("GemFire operation failed: queryService.createDefinedIndexes");
			});
			service.defineIndex("discarded", DriverIndexType.FUNCTIONAL, "id", "/people", null);
			assertThat(service.clearDefinedIndexes()).isTrue();
		}
	}

	@Test
	void poolQueryServicesRejectIndexOperationsAndLocalServiceIsAvailable() {
		try (ClientCache cache = newCache()) {
			var connection = connectionWithPeople(cache);
			var pool = connection.createPool("queryPool", PoolConfiguration.builder()
				.servers(List.of(new Endpoint("localhost", 40404))).minimumConnections(0).build());
			var poolService = pool.getQueryService();
			assertThat(poolService.newQuery("select * from /people")).isNotNull();
			assertThatThrownBy(() -> poolService.createIndex("x", DriverIndexType.FUNCTIONAL, "id", "/people", null))
				.isInstanceOf(GemFireDriverException.class);
			var local = connection.getLocalQueryService();
			@SuppressWarnings("unchecked") var results = (DriverSelectResults<Object>) local.newQuery("select p.id from /people p").execute();
			assertThat(results).hasSize(3);
		}
	}
}
