/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.contract;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.DoubleSupplier;

import org.junit.jupiter.api.Test;

import com.vmware.gemfire.driver.contract.DefaultMethodContract.Probe;
import com.vmware.gemfire.driver.spi.DriverClientRuntime;
import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverStatistics;
import com.vmware.gemfire.driver.spi.DriverStatisticDescriptor;
import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.DriverStatisticsFactory;
import com.vmware.gemfire.driver.spi.DriverStatisticsType;
import com.vmware.gemfire.driver.spi.DriverStatisticsTypes;
import com.vmware.gemfire.driver.spi.StatisticDescriptorConfiguration;
import com.vmware.gemfire.driver.spi.StatisticDescriptorConfiguration.ValueType;

/** Local instance and supplier behavior shared by the mock and native driver. */
public abstract class StatisticsContract extends TransactionContract {

	/** Test-only sampling of the registry containing the supplied instance. */
	protected abstract int sampleStatistics(DriverConnection connection, DriverStatistics statistics);

	protected Map<Method, Probe> statisticsProbes() throws Exception {
		Map<Method, Probe> probes = new LinkedHashMap<>();
		probes.put(DriverStatisticsTypes.class.getMethod("createTypesFromXml", Reader.class),
			new Probe("StatisticsTypes.createTypesFromXml", () -> {
				try (var c = connect(); var reader = new StringReader("<statistics/>")) {
					assertThat(c.getRuntime().getStatisticsTypes().createTypesFromXml(reader)).isEmpty();
				}
			}));
		probes.put(DriverClientRuntime.class.getMethod("getStatisticsFactory"),
			new Probe("ClientRuntime.getStatisticsFactory", () -> {
				try (var c = connect()) { assertThat(c.getRuntime().getStatisticsFactory()).isNotNull(); }
			}));
		probes.put(DriverStatisticsFactory.class.getMethod("create", DriverStatisticsType.class, String.class, long.class, boolean.class),
			new Probe("StatisticsFactory.create", () -> {
				try (var c = connect(); var s = c.getRuntime().getStatisticsFactory().create(type(c.getRuntime()), "probe", 123, false)) {
					assertThat(s.getNumericId()).isEqualTo(123);
				}
			}));
		probes.put(DriverStatisticsFactory.class.getMethod("findByType", DriverStatisticsType.class),
			new Probe("StatisticsFactory.findByType", () -> {
				try (var c = connect()) { assertThat(c.getRuntime().getStatisticsFactory().findByType(type(c.getRuntime()))).isEmpty(); }
			}));
		probes.put(DriverStatisticsFactory.class.getMethod("findByTextId", String.class),
			new Probe("StatisticsFactory.findByTextId", () -> {
				try (var c = connect()) { assertThat(c.getRuntime().getStatisticsFactory().findByTextId(UUID.randomUUID().toString())).isEmpty(); }
			}));
		probes.put(DriverStatisticsFactory.class.getMethod("findByNumericId", long.class),
			new Probe("StatisticsFactory.findByNumericId", () -> {
				try (var c = connect()) { assertThat(c.getRuntime().getStatisticsFactory().findByNumericId(Long.MIN_VALUE)).isEmpty(); }
			}));
		probes.put(DriverStatistics.class.getMethod("setIntSupplier", int.class, IntSupplier.class),
			new Probe("Statistics.setIntSupplier", () -> {
				try (var c = connect(); var s = c.getRuntime().getStatisticsFactory().create(type(c.getRuntime()), "supplier", 1, false)) {
					assertThat(s.setIntSupplier(s.nameToId("count"), () -> 7)).isNull();
					assertThat(s.getLong("count")).isEqualTo(7);
				}
			}));
		probes.put(DriverStatistics.class.getMethod("setIntSupplier", String.class, IntSupplier.class),
			new Probe("Statistics.setIntSupplier", () -> {
				try (var c = connect(); var s = c.getRuntime().getStatisticsFactory().create(type(c.getRuntime()), "supplier", 1, false)) {
					assertThat(s.setIntSupplier("count", () -> 7)).isNull();
					assertThat(s.getLong("count")).isEqualTo(7);
				}
			}));
		probes.put(DriverStatistics.class.getMethod("setIntSupplier", DriverStatisticDescriptor.class, IntSupplier.class),
			new Probe("Statistics.setIntSupplier", () -> {
				try (var c = connect(); var s = c.getRuntime().getStatisticsFactory().create(type(c.getRuntime()), "supplier", 1, false)) {
					assertThat(s.setIntSupplier(s.nameToDescriptor("count"), () -> 7)).isNull();
					assertThat(s.getLong("count")).isEqualTo(7);
				}
			}));
		probes.put(DriverStatistics.class.getMethod("setLongSupplier", int.class, LongSupplier.class),
			new Probe("Statistics.setLongSupplier", () -> {
				try (var c = connect(); var s = c.getRuntime().getStatisticsFactory().create(type(c.getRuntime()), "supplier", 1, false)) {
					assertThat(s.setLongSupplier(s.nameToId("count"), () -> 7)).isNull();
					assertThat(s.getLong("count")).isEqualTo(7);
				}
			}));
		probes.put(DriverStatistics.class.getMethod("setLongSupplier", String.class, LongSupplier.class),
			new Probe("Statistics.setLongSupplier", () -> {
				try (var c = connect(); var s = c.getRuntime().getStatisticsFactory().create(type(c.getRuntime()), "supplier", 1, false)) {
					assertThat(s.setLongSupplier("count", () -> 7)).isNull();
					assertThat(s.getLong("count")).isEqualTo(7);
				}
			}));
		probes.put(DriverStatistics.class.getMethod("setLongSupplier", DriverStatisticDescriptor.class, LongSupplier.class),
			new Probe("Statistics.setLongSupplier", () -> {
				try (var c = connect(); var s = c.getRuntime().getStatisticsFactory().create(type(c.getRuntime()), "supplier", 1, false)) {
					assertThat(s.setLongSupplier(s.nameToDescriptor("count"), () -> 7)).isNull();
					assertThat(s.getLong("count")).isEqualTo(7);
				}
			}));
		probes.put(DriverStatistics.class.getMethod("setDoubleSupplier", int.class, DoubleSupplier.class),
			new Probe("Statistics.setDoubleSupplier", () -> {
				try (var c = connect(); var s = c.getRuntime().getStatisticsFactory().create(type(c.getRuntime()), "supplier", 1, false)) {
					assertThat(s.setDoubleSupplier(s.nameToId("ratio"), () -> 7)).isNull();
					assertThat(s.getDouble("ratio")).isEqualTo(7.0);
				}
			}));
		probes.put(DriverStatistics.class.getMethod("setDoubleSupplier", String.class, DoubleSupplier.class),
			new Probe("Statistics.setDoubleSupplier", () -> {
				try (var c = connect(); var s = c.getRuntime().getStatisticsFactory().create(type(c.getRuntime()), "supplier", 1, false)) {
					assertThat(s.setDoubleSupplier("ratio", () -> 7)).isNull();
					assertThat(s.getDouble("ratio")).isEqualTo(7.0);
				}
			}));
		probes.put(DriverStatistics.class.getMethod("setDoubleSupplier", DriverStatisticDescriptor.class, DoubleSupplier.class),
			new Probe("Statistics.setDoubleSupplier", () -> {
				try (var c = connect(); var s = c.getRuntime().getStatisticsFactory().create(type(c.getRuntime()), "supplier", 1, false)) {
					assertThat(s.setDoubleSupplier(s.nameToDescriptor("ratio"), () -> 7)).isNull();
					assertThat(s.getDouble("ratio")).isEqualTo(7.0);
				}
			}));
		return probes;
	}

	@Test
	void statisticsRegistryPreservesDuplicateLabelsAndDetachedMembershipSnapshots() {
		try (var c = connect()) {
			var runtime = c.getRuntime();
			var type = type(runtime);
			var factory = runtime.getStatisticsFactory();
			String text = UUID.randomUUID().toString();
			try (var first = factory.create(type, text, -123456789L, false);
				 var second = factory.create(type, text, -123456789L, true);
				 var defaults = factory.create(type, null, 0, false);
				 var emptyText = factory.create(type, "", 0, true)) {
				assertThat(first.getType()).isEqualTo(type);
				assertThat(first.getTextId()).isEqualTo(text);
				assertThat(first.getNumericId()).isEqualTo(-123456789L);
				assertThat(first.getUniqueId()).isNotEqualTo(second.getUniqueId());
				assertThat(first.isAtomic()).isFalse();
				assertThat(second.isAtomic()).isTrue();
				assertThat(defaults.getTextId()).isEqualTo(runtime.getName());
				assertThat(emptyText.getTextId()).isEqualTo(runtime.getName());
				assertThat(defaults.getNumericId()).isEqualTo(ProcessHandle.current().pid());
				assertThat(emptyText.getNumericId()).isEqualTo(defaults.getNumericId());
				assertThat(factory.findByType(type)).containsExactlyInAnyOrder(first, second, defaults, emptyText);
				assertThat(factory.findByNumericId(-123456789L)).contains(first, second);
				var snapshot = factory.findByTextId(text);
				assertThat(snapshot).containsExactlyInAnyOrder(first, second);
				assertThatThrownBy(snapshot::clear).isInstanceOf(UnsupportedOperationException.class);
				first.setLong("count", 42);
				assertThat(snapshot.stream().filter(s -> s.equals(first)).findFirst().orElseThrow().getLong("count")).isEqualTo(42);
				first.close();
				first.close();
				assertThat(first.isClosed()).isTrue();
				assertThat(snapshot).hasSize(2);
				assertThat(factory.findByTextId(text)).containsExactly(second);
				assertThat(factory.findByType(type)).doesNotContain(first);
				assertThat(factory.findByNumericId(-123456789L)).doesNotContain(first);
			}
			assertThat(factory.findByType(type)).isEmpty();
		}
	}

	@Test
	void statisticValuesShareStorageAcrossNameDescriptorAndIdAccess() {
		try (var c = connect()) {
			var type = type(c.getRuntime());
			var count = type.nameToDescriptor("count");
			var ratio = type.nameToDescriptor("ratio");
			for (boolean atomic : List.of(false, true)) {
				try (var s = c.getRuntime().getStatisticsFactory().create(type, "values", 11, atomic)) {
					assertThat(s.nameToId("count")).isEqualTo(count.getId());
					assertThat(s.nameToDescriptor("ratio")).isEqualTo(ratio);
					assertThat(s.getLong(count)).isZero();
					assertThat(s.getDouble(ratio)).isZero();
					s.setLong("count", 10);
					s.incLong(count, 2);
					s.incLong(count.getId(), 3);
					assertThat(s.getLong("count")).isEqualTo(15);
					s.setLong(count, Long.MAX_VALUE);
					s.incLong("count", 1);
					assertThat(s.getLong(count.getId())).isEqualTo(Long.MIN_VALUE);
					s.setLong(count.getId(), 27);
					assertThat(s.get(count)).isEqualTo(Long.valueOf(27));
					assertThat(s.get("count")).isEqualTo(Long.valueOf(27));
					assertThat(s.getRawBits(count)).isEqualTo(27);
					assertThat(s.getRawBits("count")).isEqualTo(27);
					s.setDouble("ratio", 1.25);
					s.incDouble(ratio, 0.5);
					s.incDouble(ratio.getId(), 0.25);
					assertThat(s.getDouble("ratio")).isEqualTo(2.0);
					s.setDouble(ratio, 4.5);
					s.incDouble("ratio", -0.5);
					assertThat(s.getDouble(ratio.getId())).isEqualTo(4.0);
					s.setDouble(ratio.getId(), 8.5);
					assertThat(s.get(ratio)).isEqualTo(Double.valueOf(8.5));
					assertThat(s.get("ratio")).isEqualTo(Double.valueOf(8.5));
					assertThat(s.getRawBits(ratio)).isEqualTo(Double.doubleToRawLongBits(8.5));
					assertThat(s.getRawBits("ratio")).isEqualTo(s.getRawBits(ratio));
				}
			}
		}
	}

	@Test
	void closedStatisticsReturnZeroButStillResolveNamesAndValidateDescriptorTypes() {
		try (var c = connect()) {
			var type = type(c.getRuntime());
			var count = type.nameToDescriptor("count");
			var ratio = type.nameToDescriptor("ratio");
			try (var s = c.getRuntime().getStatisticsFactory().create(type, "closed", 22, false)) {
				for (Runnable invalid : List.<Runnable>of(() -> s.getLong(ratio), () -> s.setLong(ratio, 1),
						() -> s.incDouble(count, 1), () -> s.getLong(ratio.getId()))) {
					assertFailure(invalid, DriverFailureKind.INVALID_ARGUMENT);
				}
				assertFailure(() -> s.getLong(-1), DriverFailureKind.UNKNOWN);
				assertFailure(() -> s.getDouble(count.getId()), DriverFailureKind.UNKNOWN);
				s.setLong(count, 123);
				s.setDouble(ratio, 2.5);
				long uniqueId = s.getUniqueId();
				s.close();
				s.setLong(count, 456);
				s.incDouble(ratio, 4.5);
				s.setLong(-1, 8);
				s.incDouble(-1, 8);
				assertThat(s.getLong(-1)).isZero();
				assertThat(s.getDouble(-1)).isZero();
				assertThat(s.getLong(count)).isZero();
				assertThat(s.getDouble(ratio)).isZero();
				assertThat(s.get("ratio")).isEqualTo(Integer.valueOf(0));
				assertThat(s.get(count)).isEqualTo(Integer.valueOf(0));
				assertThat(s.getRawBits(ratio)).isZero();
				assertThat(s.getRawBits("count")).isZero();
				assertThat(s.getType()).isEqualTo(type);
				assertThat(s.getUniqueId()).isEqualTo(uniqueId);
				assertThat(s.getTextId()).isEqualTo("closed");
				assertThat(s.getNumericId()).isEqualTo(22);
				assertThat(s.nameToId("count")).isEqualTo(count.getId());
				for (Runnable invalid : List.<Runnable>of(() -> s.getLong(ratio), () -> s.setLong(ratio, 1),
						() -> s.incDouble(count, 1), () -> s.get("private-missing"), () -> s.getRawBits("private-missing"),
						() -> s.setLong("private-missing", 1))) {
					assertFailure(invalid, DriverFailureKind.INVALID_ARGUMENT);
				}
			}
		}
	}

	@Test
	void ordinaryStatisticsPreserveNegativeZeroAndNanPayloadBits() {
		try (var c = connect(); var s = c.getRuntime().getStatisticsFactory().create(type(c.getRuntime()), "bits", 1, false)) {
			for (long bits : List.of(0x8000000000000000L, 0x7ff8000000001234L)) {
				s.setDouble("ratio", Double.longBitsToDouble(bits));
				assertThat(s.getRawBits("ratio")).isEqualTo(bits);
			}
		}
	}

	@Test
	void reusedDescriptorIdsAffectExistingInstanceAccessWithoutResizingStorage() {
		try (var c = connect()) {
			var factory = c.getRuntime().getStatisticsTypes();
			var descriptor = factory.createDescriptor(new StatisticDescriptorConfiguration("shared", "", "", ValueType.DOUBLE, false));
			var first = factory.createType("original-" + UUID.randomUUID(), "", List.of(descriptor));
			try (var s = c.getRuntime().getStatisticsFactory().create(first, "live", 1, false)) {
				s.setDouble(0, 9.5);
				var count = factory.createDescriptor(new StatisticDescriptorConfiguration("count", "", "", ValueType.LONG, false));
				factory.createType("reused-" + UUID.randomUUID(), "", List.of(descriptor, count));
				assertThat(s.nameToId("shared")).isEqualTo(1);
				assertThat(s.getDouble(0)).isEqualTo(9.5);
				assertFailure(() -> s.getDouble(descriptor), DriverFailureKind.UNKNOWN);
				assertFailure(() -> s.get("shared"), DriverFailureKind.UNKNOWN);
			}
		}
	}

	@Test
	void absentStoragePreservesNativeFailureKindsAndClosedShortCircuit() {
		try (var c = connect()) {
			var type = c.getRuntime().getStatisticsTypes().createType("empty-" + UUID.randomUUID(), "", List.of());
			for (boolean atomic : List.of(false, true)) {
				try (var s = c.getRuntime().getStatisticsFactory().create(type, "empty", 1, atomic)) {
					var storageFailure = atomic ? DriverFailureKind.UNKNOWN : DriverFailureKind.INVALID_ARGUMENT;
					assertThat(s.isAtomic()).isEqualTo(atomic);
					assertFailure(() -> s.getLong(0), DriverFailureKind.INVALID_ARGUMENT);
					assertFailure(() -> s.getLong(-1), storageFailure);
					assertFailure(() -> s.setLong(0, 1), storageFailure);
					assertFailure(() -> s.incLong(0, 1), storageFailure);
					assertFailure(() -> s.getDouble(0), storageFailure);
					assertFailure(() -> s.setDouble(0, 1), storageFailure);
					assertFailure(() -> s.incDouble(0, 1), storageFailure);
					s.close();
					s.setLong(0, 1);
					s.incDouble(0, 1);
					assertThat(s.getLong(0)).isZero();
					assertThat(s.getDouble(0)).isZero();
				}
			}
		}
	}

	@Test
	void atomicStatisticsRetainEveryConcurrentIncrement() throws Exception {
		try (var c = connect(); var s = c.getRuntime().getStatisticsFactory().create(type(c.getRuntime()), "atomic", 1, true)) {
			var executor = Executors.newFixedThreadPool(4);
			try {
				var jobs = new ArrayList<Future<?>>();
				for (int worker = 0; worker < 4; worker++) jobs.add(executor.submit(() -> {
					for (int i = 0; i < 1000; i++) { s.incLong("count", 1); s.incDouble("ratio", 0.5); }
				}));
				for (var job : jobs) job.get(30, TimeUnit.SECONDS);
				assertThat(s.getLong("count")).isEqualTo(4000);
				assertThat(s.getDouble("ratio")).isEqualTo(2000.0);
			}
			finally {
				executor.shutdownNow();
				assertThat(executor.awaitTermination(30, TimeUnit.SECONDS)).isTrue();
			}
		}
	}

	@Test
	void suppliersEvaluateImmediatelyAndReplacementPreservesIdentityAcrossAllOverloads() {
		try (var c = connect()) {
			for (boolean atomic : List.of(false, true)) {
				try (var s = c.getRuntime().getStatisticsFactory().create(type(c.getRuntime()), "suppliers", 1, atomic)) {
					var count = s.nameToDescriptor("count");
					var ratio = s.nameToDescriptor("ratio");
					var calls = new AtomicInteger();
					IntSupplier firstInt = () -> { calls.incrementAndGet(); return -3; };
					IntSupplier secondInt = () -> 4;
					LongSupplier firstLong = () -> 0x1_0000_0001L;
					LongSupplier secondLong = () -> 23;
					DoubleSupplier firstDouble = () -> 1.5;
					DoubleSupplier secondDouble = () -> 2.5;
					assertThat(s.setIntSupplier(count.getId(), firstInt)).isNull();
					assertThat(s.getLong("count")).isEqualTo(-3);
					assertThat(s.getLong("count")).isEqualTo(-3);
					assertThat(calls.get()).isEqualTo(1);
					assertThat(s.setIntSupplier("count", secondInt)).isSameAs(firstInt);
					assertThat(s.setIntSupplier(count, firstInt)).isSameAs(secondInt);
					assertThat(s.setLongSupplier(count.getId(), firstLong)).isNull();
					assertThat(s.getLong("count")).isEqualTo(0x1_0000_0001L);
					assertThat(s.setLongSupplier("count", secondLong)).isSameAs(firstLong);
					assertThat(s.setLongSupplier(count, firstLong)).isSameAs(secondLong);
					assertThat(s.setDoubleSupplier(ratio.getId(), firstDouble)).isNull();
					assertThat(s.setDoubleSupplier("ratio", secondDouble)).isSameAs(firstDouble);
					assertThat(s.setDoubleSupplier(ratio, firstDouble)).isSameAs(secondDouble);
					s.setLong("count", 99);
					s.setDouble("ratio", 99);
					assertThat(sampleStatistics(c, s)).isZero();
					assertThat(calls.get()).isEqualTo(3);
					// Int and long registrations coexist; the long callback is visited last.
					assertThat(s.getLong("count")).isEqualTo(0x1_0000_0001L);
					assertThat(s.getDouble("ratio")).isEqualTo(1.5);
				}
			}
		}
	}

	@Test
	void supplierExceptionsNullAndErrorsDifferBetweenRegistrationAndSampling() {
		try (var c = connect(); var s = c.getRuntime().getStatisticsFactory().create(type(c.getRuntime()), "failures", 1, false)) {
			LongSupplier good = () -> 17;
			LongSupplier failing = () -> { throw new IllegalStateException("supplier failure"); };
			var error = new AssertionError("supplier error");
			LongSupplier fatal = () -> { throw error; };
			s.setLongSupplier("count", good);
			assertThat(s.setLongSupplier("count", failing)).isSameAs(good);
			assertThat(s.getLong("count")).isZero();
			s.setLong("count", 23);
			assertThat(sampleStatistics(c, s)).isEqualTo(1);
			assertThat(s.getLong("count")).isEqualTo(23);
			assertThatThrownBy(() -> s.setLongSupplier("count", fatal)).isSameAs(error);
			assertThat(s.getLong("count")).isEqualTo(23);
			assertThat(s.setLongSupplier("count", null)).isSameAs(failing);
			assertThat(s.getLong("count")).isZero();
			s.setLong("count", 29);
			assertThat(sampleStatistics(c, s)).isEqualTo(1);
			assertThat(s.getLong("count")).isEqualTo(29);
			assertThat(s.setLongSupplier("count", good)).isNull();
			var calls = new AtomicInteger();
			s.setLongSupplier("count", () -> {
				if (calls.getAndIncrement() == 0) return 31;
				throw error;
			});
			var doubleValue = new AtomicLong(1);
			s.setDoubleSupplier("ratio", () -> doubleValue.get());
			doubleValue.set(2);
			assertThat(sampleStatistics(c, s)).isEqualTo(1);
			assertThat(s.getLong("count")).isEqualTo(31);
			assertThat(s.getDouble("ratio")).isEqualTo(2.0);
			// Null registrations remain present for every kind, including int and double.
			s.setIntSupplier("count", null);
			s.setDoubleSupplier("ratio", null);
			assertThat(sampleStatistics(c, s)).isEqualTo(3);
		}
	}

	@Test
	void supplierValidationPrecedesEvaluationExceptForNegativeLongStorageAccess() {
		try (var c = connect(); var s = c.getRuntime().getStatisticsFactory().create(type(c.getRuntime()), "validation", 1, false)) {
			var calls = new AtomicInteger();
			LongSupplier callback = () -> calls.incrementAndGet();
			assertFailure(() -> s.setLongSupplier("private-missing", callback), DriverFailureKind.INVALID_ARGUMENT);
			assertFailure(() -> s.setLongSupplier(s.nameToDescriptor("ratio"), callback), DriverFailureKind.INVALID_ARGUMENT);
			assertFailure(() -> s.setLongSupplier(s.nameToId("ratio"), callback), DriverFailureKind.INVALID_ARGUMENT);
			assertFailure(() -> s.setDoubleSupplier(-1, () -> calls.incrementAndGet()), DriverFailureKind.INVALID_ARGUMENT);
			assertThat(calls.get()).isZero();
			assertFailure(() -> s.setLongSupplier(-1, callback), DriverFailureKind.UNKNOWN);
			assertThat(calls.get()).isEqualTo(1);
			assertThat(sampleStatistics(c, s)).isZero();
			assertThat(calls.get()).isEqualTo(1);
		}
	}

	@Test
	void closedInstancesStillEvaluateRegistrationButAreAbsentFromSubsequentRegistrySamples() {
		try (var c = connect(); var s = c.getRuntime().getStatisticsFactory().create(type(c.getRuntime()), "closed-supplier", 1, true)) {
			var calls = new AtomicInteger();
			LongSupplier callback = () -> calls.incrementAndGet();
			s.setLongSupplier("count", callback);
			s.close();
			assertThat(s.setLongSupplier("count", callback)).isSameAs(callback);
			assertThat(calls.get()).isEqualTo(2);
			assertThat(s.getLong("count")).isZero();
			assertThat(sampleStatistics(c, s)).isZero();
			assertThat(calls.get()).isEqualTo(2);
			assertFailure(() -> s.setLongSupplier("ratio", callback), DriverFailureKind.INVALID_ARGUMENT);
			assertThat(calls.get()).isEqualTo(2);
		}
	}

	@Test
	void statisticsXmlPreservesMetadataDefaultsOrderingAndUsableTypes() {
		try (var c = connect()) {
			var factory = c.getRuntime().getStatisticsTypes();
			for (String doctype : List.of("", """
				<!DOCTYPE statistics PUBLIC "-//GemStone Systems, Inc.//GemFire StatisticsType//EN"
				"http://www.gemstone.com/dtd/statisticsType.dtd">
				""")) {
				String name = "xml-" + UUID.randomUUID();
				String xml = doctype + """
					<statistics><type name="%s"><description> type &amp; metadata </description>
					  <stat name="ratio" storage="DOUBLE" counter="FALSE" largerBetter="TRUE">
					    <description><![CDATA[ ratio ]]></description><unit>%%</unit>
					  </stat>
					  <stat name="count"/>
					  <stat name="gauge" storage="int" counter="false"><unit/></stat>
					  <stat name="total" storage="long" largerBetter="false"/>
					</type><type name="%s-empty"/></statistics>
					""".formatted(name, name);
				var loaded = factory.createTypesFromXml(new StringReader(xml));
				assertThat(loaded).extracting(DriverStatisticsType::getName).containsExactly(name, name + "-empty");
				assertThatThrownBy(loaded::clear).isInstanceOf(UnsupportedOperationException.class);
				var type = loaded.get(0);
				assertThat(factory.findType(name)).isEqualTo(type);
				assertThat(type.getDescription()).isEqualTo(" type & metadata ");
				assertThat(type.getStatistics()).extracting(DriverStatisticDescriptor::getName)
					.containsExactly("ratio", "count", "gauge", "total");
				assertThat(type.getStatistics()).extracting(DriverStatisticDescriptor::getId).containsExactly(3, 0, 1, 2);
				assertThat(type.getStatistics()).extracting(DriverStatisticDescriptor::getType)
					.containsExactly(ValueType.DOUBLE, ValueType.LONG, ValueType.LONG, ValueType.LONG);
				assertThat(type.getStatistics()).extracting(DriverStatisticDescriptor::isCounter).containsExactly(false, true, false, true);
				assertThat(type.getStatistics()).extracting(DriverStatisticDescriptor::isLargerBetter).containsExactly(true, true, false, false);
				assertThat(type.nameToDescriptor("ratio").getDescription()).isEqualTo(" ratio ");
				assertThat(type.nameToDescriptor("ratio").getUnit()).isEqualTo("%");
				assertThat(type.nameToDescriptor("count").getDescription()).isEmpty();
				assertThat(type.nameToDescriptor("gauge").getUnit()).isEmpty();
				assertThat(loaded.get(1).getDescription()).isEmpty();
				assertThat(loaded.get(1).getStatistics()).isEmpty();
				assertThat(factory.createTypesFromXml(new StringReader(xml))).isEqualTo(loaded);
				try (var values = c.getRuntime().getStatisticsFactory().create(type, "xml", 1, true)) {
					values.setLong("count", 0x1_0000_0001L);
					values.setDouble("ratio", 1.25);
					assertThat(values.getLong("count")).isEqualTo(0x1_0000_0001L);
					assertThat(values.getDouble("ratio")).isEqualTo(1.25);
				}
			}
		}
	}

	@Test
	void statisticsXmlRegistrationKeepsEarlierTypesWhenLaterDefinitionConflicts() {
		try (var c = connect()) {
			var factory = c.getRuntime().getStatisticsTypes();
			String name = "xml-partial-" + UUID.randomUUID();
			String xml = """
				<statistics><type name="%s"><stat name="count"><description>stat only</description></stat></type>
				<type name="%s"><stat name="different"/></type><type name="%s-unreached"/></statistics>
				""".formatted(name, name, name);
			assertFailure(() -> factory.createTypesFromXml(new StringReader(xml)), DriverFailureKind.INVALID_ARGUMENT);
			assertThat(factory.findType(name).getStatistics()).extracting(DriverStatisticDescriptor::getName).containsExactly("count");
			assertThat(factory.findType(name).getDescription()).isEmpty();
			assertThat(factory.findType(name + "-unreached")).isNull();
			assertFailure(() -> factory.createTypesFromXml(new StringReader(
				"<statistics><type name=\"" + name + "-duplicate\"><stat name=\"count\"/><stat name=\"count\"/></type></statistics>")),
				DriverFailureKind.INVALID_ARGUMENT);
			assertThat(factory.findType(name + "-duplicate")).isNull();
		}
	}

	@Test
	void statisticsXmlSyntaxAndReadFailuresAreClassifiedBeforeRegistration() {
		try (var c = connect()) {
			var factory = c.getRuntime().getStatisticsTypes();
			String name = "xml-syntax-" + UUID.randomUUID();
			assertFailure(() -> factory.createTypesFromXml(new StringReader(
				"<statistics><type name=\"" + name + "\"/></private-missing>")), DriverFailureKind.SYSTEM_FAILURE);
			assertThat(factory.findType(name)).isNull();
			try (var reader = new Reader() {
				@Override public int read(char[] buffer, int offset, int length) throws IOException { throw new IOException("private-missing"); }
				@Override public void close() { }
			}) {
				assertFailure(() -> factory.createTypesFromXml(reader), DriverFailureKind.SYSTEM_FAILURE);
			}
		}
	}

	@Test
	void statisticsXmlSchemaAssertionsRemainErrors() {
		try (var c = connect()) {
			var factory = c.getRuntime().getStatisticsTypes();
			for (String xml : List.of("<wrong/>", "<statistics><type/></statistics>",
				"<statistics><type name=\"invalid\"><stat/></type></statistics>",
				"<statistics><type name=\"invalid\"><stat name=\"s\" storage=\"float\"/></type></statistics>",
				"<statistics><type name=\"invalid\"><stat name=\"s\" counter=\"yes\"/></type></statistics>")) {
				assertThatThrownBy(() -> factory.createTypesFromXml(new StringReader(xml))).isInstanceOf(Error.class);
			}
		}
	}

	@Test
	void statisticsAndCapturedFactoryRemainUsableAfterConnectionClosure() {
		for (boolean atomic : List.of(false, true)) {
			try (var connection = connect()) {
				var runtime = connection.getRuntime();
				var type = type(runtime);
				var factory = runtime.getStatisticsFactory();
				String text = "shutdown-" + UUID.randomUUID();
				try (var instance = factory.create(type, text, 123, atomic)) {
					instance.setLong("count", 41);
					instance.setDouble("ratio", 1.5);
					var snapshot = factory.findByType(type);
					connection.close();
					assertThat(runtime.isConnected()).isFalse();
					assertThat(instance.isClosed()).isFalse();
					assertThat(instance.getLong("count")).isEqualTo(41);
					assertThat(instance.getDouble("ratio")).isEqualTo(1.5);
					instance.incLong("count", 1);
					instance.incDouble("ratio", 0.5);
					assertThat(instance.getLong("count")).isEqualTo(42);
					assertThat(instance.getDouble("ratio")).isEqualTo(2.0);
					assertThat(factory.findByTextId(text)).containsExactly(instance);
					assertThat(factory.findByNumericId(123)).contains(instance);
					try (var later = factory.create(type, text, 123, atomic)) {
						assertThat(later.isClosed()).isFalse();
						assertThat(later.getUniqueId()).isNotEqualTo(instance.getUniqueId());
						assertThat(factory.findByType(type)).containsExactlyInAnyOrder(instance, later);
						instance.close();
						assertThat(snapshot).containsExactly(instance);
						assertThat(snapshot.get(0).isClosed()).isTrue();
						assertThat(instance.getLong("count")).isZero();
						assertThat(factory.findByTextId(text)).containsExactly(later);
					}
					assertThat(factory.findByType(type)).isEmpty();
				}
			}
		}
	}

	@Test
	void reconnectCreatesIndependentInstanceRegistryWhileOldHandlesRemainUsable() {
		try (var oldConnection = connect()) {
			var oldFactory = oldConnection.getRuntime().getStatisticsFactory();
			var oldType = type(oldConnection.getRuntime());
			String text = "reconnect-" + UUID.randomUUID();
			try (var oldInstance = oldFactory.create(oldType, text, 123, true)) {
				oldInstance.setLong("count", 7);
				oldConnection.close();
				try (var newConnection = connect()) {
					var newFactory = newConnection.getRuntime().getStatisticsFactory();
					assertThat(newFactory.findByTextId(text)).isEmpty();
					try (var newInstance = newFactory.create(type(newConnection.getRuntime()), text, 123, true)) {
						newInstance.setLong("count", 8);
						assertThat(oldFactory.findByTextId(text)).containsExactly(oldInstance);
						assertThat(newFactory.findByTextId(text)).containsExactly(newInstance);
						assertThat(oldInstance.getLong("count")).isEqualTo(7);
						oldInstance.close();
						assertThat(oldFactory.findByTextId(text)).isEmpty();
						assertThat(newInstance.isClosed()).isFalse();
						assertThat(newInstance.getLong("count")).isEqualTo(8);
						assertThat(newFactory.findByTextId(text)).containsExactly(newInstance);
					}
				}
			}
		}
	}

	private DriverStatisticsType type(DriverClientRuntime runtime) {
		var types = runtime.getStatisticsTypes();
		return types.createType("statistics-" + UUID.randomUUID(), "", List.of(
			types.createDescriptor(new StatisticDescriptorConfiguration("ratio", "", "%", ValueType.DOUBLE, false)),
			types.createDescriptor(new StatisticDescriptorConfiguration("count", "", "operations", ValueType.LONG, true))));
	}

	private void assertFailure(Runnable action, DriverFailureKind kind) {
		assertThatThrownBy(action::run).isInstanceOfSatisfying(DriverOperationException.class, failure -> {
			assertThat(failure.kind()).isEqualTo(kind);
			assertThat(failure.getMessage()).doesNotContain("private-missing");
		});
	}
}
