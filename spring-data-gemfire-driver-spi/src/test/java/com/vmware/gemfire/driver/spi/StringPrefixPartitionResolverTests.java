// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

class StringPrefixPartitionResolverTests {
	private final StringPrefixPartitionResolver resolver = new StringPrefixPartitionResolver();

	private static DriverEntryOperation<String, Object> keyed(String key) {
		return new DriverEntryOperation<>() {
			@Override public DriverRegion<String, Object> getRegion() { return null; }
			@Override public DriverOperation getOperation() { return null; }
			@Override public String getKey() { return key; }
			@Override public Object getCallbackArgument() { return null; }
			@Override public boolean isCallbackArgumentAvailable() { return false; }
			@Override public Object getNewValue() { return null; }
		};
	}

	@Test void routesByTextBeforeFirstDelimiter() {
		assertThat(resolver.getRoutingObject(keyed("order|42|extra"))).isEqualTo("order");
		assertThat(resolver.getRoutingObject(keyed("|tail"))).isEqualTo("");
	}
	@Test void missingDelimiterAndNullKeyFail() {
		assertThatThrownBy(() -> resolver.getRoutingObject(keyed("plain"))).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> resolver.getRoutingObject(keyed(null))).isInstanceOf(NullPointerException.class);
	}
	@Test void nameEqualityAndLifecycleDefaults() {
		assertThat(StringPrefixPartitionResolver.DEFAULT_DELIMITER).isEqualTo("|");
		assertThat(resolver.getName()).isEqualTo(StringPrefixPartitionResolver.class.getName());
		assertThat(resolver).isEqualTo(new StringPrefixPartitionResolver()).hasSameHashCodeAs(new StringPrefixPartitionResolver())
			.isNotEqualTo("x");
		assertThatCode(() -> { resolver.init(new java.util.Properties()); resolver.close(); }).doesNotThrowAnyException();
	}
}
