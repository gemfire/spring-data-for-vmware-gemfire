// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.*;

import org.apache.geode.cache.EntryOperation;
import org.apache.geode.cache.Operation;
import org.apache.geode.cache.Region;
import org.junit.jupiter.api.Test;

import com.vmware.gemfire.driver.spi.DriverOperation;
import com.vmware.gemfire.driver.spi.StringPrefixPartitionResolver;

/** Native characterization of resolver routing, compared with the native-free SPI implementation. */
class NativePartitionResolverTests {
	private static EntryOperation<String, Object> keyed(String key, Operation operation) {
		return new EntryOperation<>() {
			@Override public Region<String, Object> getRegion() { return null; }
			@Override public Operation getOperation() { return operation; }
			@Override public String getKey() { return key; }
			@Override public Object getCallbackArgument() { return "arg"; }
			@Override public boolean isCallbackArgumentAvailable() { return true; }
			@Override public Object getNewValue() { return "value"; }
		};
	}

	private final org.apache.geode.cache.util.StringPrefixPartitionResolver nativeResolver = new org.apache.geode.cache.util.StringPrefixPartitionResolver();
	private final GemFirePartitionResolver<String, Object> bridge = new GemFirePartitionResolver<>(new StringPrefixPartitionResolver());

	@Test void routingMatchesNativeResolver() {
		for (String key : new String[] {"order|42", "|tail", "a|b|c"}) {
			assertThat(bridge.getRoutingObject(keyed(key, Operation.GET))).isEqualTo(nativeResolver.getRoutingObject(keyed(key, Operation.GET)));
		}
		// getName is the implementing class name, so each side reports its own class.
		assertThat(nativeResolver.getName()).isEqualTo(org.apache.geode.cache.util.StringPrefixPartitionResolver.class.getName());
		assertThat(bridge.getName()).isEqualTo(StringPrefixPartitionResolver.class.getName());
		assertThat(org.apache.geode.cache.util.StringPrefixPartitionResolver.DEFAULT_DELIMITER).isEqualTo(StringPrefixPartitionResolver.DEFAULT_DELIMITER);
	}
	@Test void missingDelimiterFailsLikeNative() {
		assertThatThrownBy(() -> nativeResolver.getRoutingObject(keyed("plain", Operation.GET))).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> bridge.getRoutingObject(keyed("plain", Operation.GET))).isInstanceOf(IllegalArgumentException.class);
	}
	@Test void operationViewIsNativeFreeAndNullTolerant() {
		var view = GemFirePartitionResolver.view(keyed("k|1", Operation.UPDATE));
		assertThat(view.getKey()).isEqualTo("k|1");
		assertThat(view.getOperation()).isSameAs(DriverOperation.UPDATE);
		assertThat(view.getCallbackArgument()).isEqualTo("arg");
		assertThat(view.isCallbackArgumentAvailable()).isTrue();
		assertThat(view.getNewValue()).isEqualTo("value");
		assertThat(view.getRegion()).isNull();
		assertThat(GemFirePartitionResolver.view(keyed("k|1", null)).getOperation()).isNull();
	}
	@Test void closeAndInitHooksAreInvokedWithoutNativeTypes() {
		assertThatCode(() -> { bridge.init(new java.util.Properties()); bridge.close(); }).doesNotThrowAnyException();
		assertThat(bridge.delegate()).isInstanceOf(StringPrefixPartitionResolver.class);
	}
}
