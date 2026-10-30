/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.apache.geode.StatisticDescriptor;
import org.apache.geode.StatisticsType;
import org.apache.geode.StatisticsTypeFactory;

import com.vmware.gemfire.driver.spi.DriverStatisticDescriptor;
import com.vmware.gemfire.driver.spi.DriverStatisticsType;
import com.vmware.gemfire.driver.spi.DriverStatisticsTypes;
import com.vmware.gemfire.driver.spi.StatisticDescriptorConfiguration;
import com.vmware.gemfire.driver.spi.StatisticDescriptorConfiguration.ValueType;

/** Delegates to the injected runtime; the native registry never crosses the SPI. */
final class GemFireStatisticsTypes implements DriverStatisticsTypes {
	private final StatisticsTypeFactory factory;
	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();

	GemFireStatisticsTypes(StatisticsTypeFactory factory) { this.factory = factory; }

	@Override public DriverStatisticDescriptor createDescriptor(StatisticDescriptorConfiguration config) {
		return invoke("StatisticsTypes.createDescriptor", () -> {
			StatisticDescriptor descriptor;
			if (config.type() == ValueType.LONG) {
				descriptor = config.counter()
					? factory.createLongCounter(config.name(), config.description(), config.unit(), config.largerBetter())
					: factory.createLongGauge(config.name(), config.description(), config.unit(), config.largerBetter());
			}
			else {
				descriptor = config.counter()
					? factory.createDoubleCounter(config.name(), config.description(), config.unit(), config.largerBetter())
					: factory.createDoubleGauge(config.name(), config.description(), config.unit(), config.largerBetter());
			}
			return new Descriptor(descriptor);
		});
	}

	@Override public DriverStatisticsType createType(String name, String description, List<DriverStatisticDescriptor> descriptors) {
		return invoke("StatisticsTypes.createType", () -> {
			var nativeDescriptors = descriptors.stream().map(descriptor -> {
				if (!(descriptor instanceof Descriptor wrapped)) throw new IllegalArgumentException("Foreign descriptor");
				return wrapped.delegate;
			}).toArray(StatisticDescriptor[]::new);
			return new Type(factory.createType(name, description, nativeDescriptors));
		});
	}

	@Override public DriverStatisticsType findType(String name) {
		return invoke("StatisticsTypes.findType", () -> {
			var type = factory.findType(name);
			return type == null ? null : new Type(type);
		});
	}

	@Override public List<DriverStatisticsType> createTypesFromXml(Reader reader) {
		try { return Arrays.stream(factory.createTypesFromXml(reader)).<DriverStatisticsType>map(Type::new).toList(); }
		catch (IOException | RuntimeException failure) { throw exceptions.translate("StatisticsTypes.createTypesFromXml", failure); }
	}

	StatisticsType unwrap(DriverStatisticsType type) {
		if (!(type instanceof Type wrapped)) throw new IllegalArgumentException("Foreign statistics type");
		return wrapped.delegate;
	}
	StatisticDescriptor unwrap(DriverStatisticDescriptor descriptor) {
		if (!(descriptor instanceof Descriptor wrapped)) throw new IllegalArgumentException("Foreign descriptor");
		return wrapped.delegate;
	}
	DriverStatisticsType wrap(StatisticsType type) { return new Type(type); }
	DriverStatisticDescriptor wrap(StatisticDescriptor descriptor) { return new Descriptor(descriptor); }

	private <T> T invoke(String operation, Supplier<T> action) {
		try { return action.get(); }
		catch (RuntimeException failure) { throw exceptions.translate(operation, failure); }
	}

	private final class Descriptor implements DriverStatisticDescriptor {
		private final StatisticDescriptor delegate;
		Descriptor(StatisticDescriptor delegate) { this.delegate = delegate; }
		@Override public String getName() { return delegate.getName(); }
		@Override public String getDescription() { return delegate.getDescription(); }
		@Override public String getUnit() { return delegate.getUnit(); }
		@Override public ValueType getType() { return delegate.getType() == long.class ? ValueType.LONG : ValueType.DOUBLE; }
		@Override public boolean isCounter() { return delegate.isCounter(); }
		@Override public boolean isLargerBetter() { return delegate.isLargerBetter(); }
		@Override public int getId() { return delegate.getId(); }
		@Override public int compareTo(DriverStatisticDescriptor other) { return getName().compareTo(other.getName()); }
		@Override public boolean equals(Object other) { return other instanceof Descriptor that && delegate.equals(that.delegate); }
		@Override public int hashCode() { return delegate.hashCode(); }
	}

	private final class Type implements DriverStatisticsType {
		private final StatisticsType delegate;
		Type(StatisticsType delegate) { this.delegate = delegate; }
		@Override public String getName() { return delegate.getName(); }
		@Override public String getDescription() { return delegate.getDescription(); }
		@Override public List<DriverStatisticDescriptor> getStatistics() {
			return Arrays.stream(delegate.getStatistics()).<DriverStatisticDescriptor>map(Descriptor::new).toList();
		}
		@Override public DriverStatisticDescriptor nameToDescriptor(String name) {
			return invoke("StatisticsType.nameToDescriptor", () -> new Descriptor(delegate.nameToDescriptor(name)));
		}
		@Override public int nameToId(String name) { return invoke("StatisticsType.nameToId", () -> delegate.nameToId(name)); }
		@Override public boolean equals(Object other) { return other instanceof Type that && delegate.equals(that.delegate); }
		@Override public int hashCode() { return delegate.hashCode(); }
	}
}
