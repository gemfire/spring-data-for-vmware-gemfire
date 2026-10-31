// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.*;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import org.apache.geode.cache.configuration.RegionAttributesType;
import org.apache.geode.cache.configuration.RegionAttributesType.EvictionAttributes;
import org.apache.geode.management.configuration.ClassName;
import org.junit.jupiter.api.Test;
import com.vmware.gemfire.driver.spi.config.LegacyEvictionSettings;
import com.vmware.gemfire.driver.spi.config.LegacyEvictionSettingsFactory;
import com.vmware.gemfire.driver.spi.config.LegacyEvictionSettings.Branch;

/** Pins the native-free eviction settings to the native configuration-model generator they replace. No cache or server is needed. */
class NativeLegacyEvictionSettingsTests {

	private final String[] ACTIONS = {null, "", " ", "local-destroy", "overflow-to-disk", "none", "LOCAL-DESTROY", "bogus"};
	private final Integer[] MAXIMUMS = {null, 0, 25};
	private final String[] SIZERS = {null, "", "com.example.Sizer"};

	private static Map<String, String> asMap(Properties properties) {
		var map = new TreeMap<String, String>();
		properties.stringPropertyNames().forEach(k -> map.put(k, properties.getProperty(k)));
		return map;
	}

	private static Map<String, String> asMap(java.util.List<org.apache.geode.cache.configuration.ParameterType> parameters) {
		var map = new TreeMap<String, String>();
		parameters.forEach(p -> map.put(p.getName(), p.getString()));
		return map;
	}

	@Test void generateMatchesTheNativeGeneratorAcrossTheInputMatrix() {
		var parameters = Map.of("a", "1", "b", "2");
		var properties = new Properties();
		parameters.forEach(properties::setProperty);
		for (String action : ACTIONS) {
			for (Integer memory : MAXIMUMS) {
				for (Integer entries : MAXIMUMS) {
					for (String sizer : SIZERS) {
						String label = action + "/" + memory + "/" + entries + "/" + sizer;
						Throwable nativeFailure = catchThrowable(() -> EvictionAttributes.generate(action, memory, entries, sizer == null ? null : new ClassName(sizer, properties)));
						Throwable spiFailure = catchThrowable(() -> LegacyEvictionSettingsFactory.generate(action, memory, entries, sizer, parameters));
						if (nativeFailure != null) {
							assertThat(spiFailure).as(label).isNotNull().isInstanceOf(nativeFailure.getClass()).hasMessage(nativeFailure.getMessage());
							continue;
						}
						assertThat(spiFailure).as(label).isNull();
						var nat = EvictionAttributes.generate(action, memory, entries, sizer == null ? null : new ClassName(sizer, properties));
						var spi = LegacyEvictionSettingsFactory.generate(action, memory, entries, sizer, parameters);
						if (nat == null) {
							assertThat(spi).as(label).isNull();
							continue;
						}
						assertThat(spi).as(label).isNotNull();
						if (nat.getLruHeapPercentage() != null) {
							var heap = nat.getLruHeapPercentage();
							assertThat(spi.getBranch()).as(label).isEqualTo(Branch.LRU_HEAP_PERCENTAGE);
							assertThat(spi.getAction().toString()).as(label).isEqualTo(heap.getAction().value());
							assertThat(spi.getMaximum()).as(label).isNull();
							assertThat(spi.getSizerClassName()).as(label).isEqualTo(heap.getClassName());
							assertThat(spi.getSizerParameters()).as(label).isEqualTo(asMap(heap.getParameters()));
						} else if (nat.getLruMemorySize() != null) {
							var memorySize = nat.getLruMemorySize();
							assertThat(spi.getBranch()).as(label).isEqualTo(Branch.LRU_MEMORY_SIZE);
							assertThat(spi.getAction().toString()).as(label).isEqualTo(memorySize.getAction().value());
							assertThat(spi.getMaximum()).as(label).isEqualTo(memorySize.getMaximum());
							assertThat(spi.getSizerClassName()).as(label).isEqualTo(memorySize.getClassName());
							assertThat(spi.getSizerParameters()).as(label).isEqualTo(asMap(memorySize.getParameters()));
						} else {
							var entryCount = nat.getLruEntryCount();
							assertThat(spi.getBranch()).as(label).isEqualTo(Branch.LRU_ENTRY_COUNT);
							assertThat(spi.getAction().toString()).as(label).isEqualTo(entryCount.getAction().value());
							assertThat(spi.getMaximum()).as(label).isEqualTo(entryCount.getMaximum());
							assertThat(spi.getSizerClassName()).as(label).isNull();
							assertThat(spi.getSizerParameters()).as(label).isEmpty();
						}
					}
				}
			}
		}
	}

	@Test void theNativeStringOverloadAndInvalidClassNamesFailLikeTheSpi() {
		Throwable nativeFailure = catchThrowable(() -> EvictionAttributes.generate(null, null, 5, "not a class"));
		Throwable spiFailure = catchThrowable(() -> LegacyEvictionSettingsFactory.generate(null, null, 5, "not a class", null));
		assertThat(nativeFailure).isInstanceOf(IllegalArgumentException.class);
		assertThat(spiFailure).isInstanceOf(IllegalArgumentException.class).hasMessage(nativeFailure.getMessage());
	}

	@Test void parseActionMatchesTheNativeEnumConversion() {
		for (String action : ACTIONS) {
			Throwable nativeFailure = catchThrowable(() -> org.apache.geode.cache.configuration.EnumActionDestroyOverflow.fromValue(action));
			if (nativeFailure != null) {
				assertThatThrownBy(() -> LegacyEvictionSettings.parseAction(action)).isInstanceOf(nativeFailure.getClass()).hasMessage(nativeFailure.getMessage());
			} else {
				assertThat(LegacyEvictionSettings.parseAction(action).toString())
						.isEqualTo(org.apache.geode.cache.configuration.EnumActionDestroyOverflow.fromValue(action).value());
			}
		}
	}
}
