/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.DriverStatisticDescriptor;
import com.vmware.gemfire.driver.spi.DriverStatisticsType;
import com.vmware.gemfire.driver.spi.DriverStatisticsTypes;
import com.vmware.gemfire.driver.spi.StatisticDescriptorConfiguration;
import com.vmware.gemfire.driver.spi.StatisticDescriptorConfiguration.ValueType;

/** Per-connection schema registry; no sampling, global registrations or statistic instances. */
final class MockStatisticsTypes implements DriverStatisticsTypes {
	private final Map<String, Type> types = new HashMap<>();

	@Override public DriverStatisticDescriptor createDescriptor(StatisticDescriptorConfiguration config) {
		return invoke("StatisticsTypes.createDescriptor", () -> new Descriptor(config));
	}

	@Override public synchronized DriverStatisticsType createType(String name, String description, List<DriverStatisticDescriptor> descriptors) {
		return invoke("StatisticsTypes.createType", () -> {
			if (name == null || descriptors == null || descriptors.size() > 254) throw new IllegalArgumentException("Invalid type definition");
			var candidate = new Type(name, description, descriptors.stream().map(descriptor -> {
				if (!(descriptor instanceof Descriptor local)) throw new IllegalArgumentException("Foreign descriptor");
				return local;
			}).toList());
			var previous = types.get(name);
			if (previous != null) {
				if (!candidate.equals(previous)) throw new IllegalArgumentException("Conflicting type definition");
				return previous;
			}
			types.put(name, candidate);
			return candidate;
		});
	}

	@Override public synchronized DriverStatisticsType findType(String name) { return types.get(name); }

	@Override public List<DriverStatisticsType> createTypesFromXml(Reader reader) {
		return invoke("StatisticsTypes.createTypesFromXml", () -> new MockStatisticsXml(this).read(reader));
	}

	DriverStatisticsType requireType(DriverStatisticsType type) {
		if (!(type instanceof Type)) throw new IllegalArgumentException("Foreign statistics type");
		return type;
	}
	DriverStatisticDescriptor requireDescriptor(DriverStatisticDescriptor descriptor) {
		if (!(descriptor instanceof Descriptor)) throw new IllegalArgumentException("Foreign descriptor");
		return descriptor;
	}

	private <T> T invoke(String operation, Supplier<T> action) {
		try { return action.get(); }
		catch (IllegalArgumentException | NullPointerException failure) {
			throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, operation + " failed", failure);
		}
	}

	private final class Descriptor implements DriverStatisticDescriptor {
		private final StatisticDescriptorConfiguration config;
		private volatile int id = -1;
		Descriptor(StatisticDescriptorConfiguration config) {
			this.config = new StatisticDescriptorConfiguration(config.name(),
				config.description() == null ? "" : config.description(), config.unit() == null ? "" : config.unit(),
				config.type(), config.counter(), config.largerBetter());
		}
		@Override public String getName() { return config.name(); }
		@Override public String getDescription() { return config.description(); }
		@Override public String getUnit() { return config.unit(); }
		@Override public ValueType getType() { return config.type(); }
		@Override public boolean isCounter() { return config.counter(); }
		@Override public boolean isLargerBetter() { return config.largerBetter(); }
		@Override public int getId() { return id; }
		@Override public int compareTo(DriverStatisticDescriptor other) { return getName().compareTo(other.getName()); }
		@Override public boolean equals(Object other) { return other instanceof Descriptor that && id == that.id && config.equals(that.config); }
		@Override public int hashCode() { return getName().hashCode(); }
	}

	private final class Type implements DriverStatisticsType {
		private final String name;
		private final String description;
		private final List<DriverStatisticDescriptor> descriptors;
		private final Map<String, DriverStatisticDescriptor> byName = new HashMap<>();
		Type(String name, String description, List<Descriptor> descriptors) {
			this.name = name;
			this.description = description;
			this.descriptors = List.copyOf(descriptors);
			int nextId = 0;
			for (var type : List.of(ValueType.LONG, ValueType.DOUBLE)) {
				for (var descriptor : descriptors) {
					if (descriptor.getType() == type) {
						descriptor.id = nextId++;
						if (byName.put(descriptor.getName(), descriptor) != null) throw new IllegalArgumentException("Duplicate descriptor");
					}
				}
			}
		}
		@Override public String getName() { return name; }
		@Override public String getDescription() { return description; }
		@Override public List<DriverStatisticDescriptor> getStatistics() { return descriptors; }
		@Override public DriverStatisticDescriptor nameToDescriptor(String name) {
			return invoke("StatisticsType.nameToDescriptor", () -> lookup(name));
		}
		@Override public int nameToId(String name) { return invoke("StatisticsType.nameToId", () -> lookup(name).getId()); }
		private DriverStatisticDescriptor lookup(String name) {
			var descriptor = byName.get(name);
			if (descriptor == null) throw new IllegalArgumentException("Unknown statistic");
			return descriptor;
		}
		@Override public boolean equals(Object other) {
			return other instanceof Type that && name.equals(that.name) && description.equals(that.description) && descriptors.equals(that.descriptors);
		}
		@Override public int hashCode() { return name.hashCode(); }
	}
}
