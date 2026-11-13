/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.LinkedHashMap;
import java.util.List;
import org.junit.jupiter.api.Test;

class QueryServiceContractTests {
	@Test
	void queryEntryPointsDefaultToTypedUnsupportedOnOlderImplementations() {
		DriverConnection connection = new DriverConnection() {};
		DriverPool pool = new DriverPool() {};
		DriverRegion<String, String> region = new DriverRegion<>() {};
		List<org.assertj.core.api.ThrowableAssert.ThrowingCallable> calls = List.of(
			connection::getQueryService, connection::getLocalQueryService, pool::getQueryService,
			() -> region.query("true"), () -> region.existsValue("true"), () -> region.selectValue("true"));
		List<String> features = List.of("RegionService.getQueryService", "Connection.getLocalQueryService", "Pool.getQueryService",
			"Region.query", "Region.existsValue", "Region.selectValue");
		for (int i = 0; i < calls.size(); i++) {
			String feature = features.get(i);
			assertThatThrownBy(calls.get(i)).isInstanceOfSatisfying(UnsupportedFeatureException.class, failure -> {
				assertThat(failure.feature()).isEqualTo(feature);
				assertThat(failure.reason()).isEqualTo(UnsupportedFeatureException.Reason.NOT_INTRODUCED);
				assertThat(failure.gemFireVersion()).isEmpty();
			});
		}
	}

	@Test
	void indexTypeSynonymsResolveBothNamesAndRejectUnknownOrNull() {
		assertThat(DriverIndexType.FUNCTIONAL.synonym()).isEqualTo("RANGE");
		assertThat(DriverIndexType.HASH.synonym()).isEqualTo("HASH");
		assertThat(DriverIndexType.PRIMARY_KEY.synonym()).isEqualTo("KEY");
		for (DriverIndexType type : DriverIndexType.values()) {
			assertThat(DriverIndexType.valueOfSynonym(type.name())).isSameAs(type);
			assertThat(DriverIndexType.valueOfSynonym(type.synonym())).isSameAs(type);
		}
		assertThatThrownBy(() -> DriverIndexType.valueOfSynonym("BTREE")).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> DriverIndexType.valueOfSynonym(null)).isInstanceOf(NullPointerException.class);
	}

	@Test
	void multiIndexFailuresAreCopiedKeepOrderAndAreImmutable() {
		var failures = new LinkedHashMap<String, DriverOperationException>();
		failures.put("second", new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "bad", null));
		failures.put("first", new DriverOperationException(DriverFailureKind.INVALID_STATE, "worse", null));
		var cause = new IllegalStateException("cause");
		var failure = new DriverMultiIndexCreationException("GemFire operation failed: createDefinedIndexes", failures, cause);
		failures.clear();
		assertThat(failure.failedIndexNames()).containsExactly("second", "first");
		assertThat(failure.failures().get("first").kind()).isEqualTo(DriverFailureKind.INVALID_STATE);
		assertThat(failure.getCause()).isSameAs(cause);
		assertThatThrownBy(() -> failure.failures().clear()).isInstanceOf(UnsupportedOperationException.class);
		assertThatThrownBy(() -> new DriverMultiIndexCreationException("m", null, cause)).isInstanceOf(NullPointerException.class);
	}

	@Test
	void undefinedIsASingletonSentinel() {
		assertThat(DriverUndefined.values()).containsExactly(DriverUndefined.INSTANCE);
	}
}
