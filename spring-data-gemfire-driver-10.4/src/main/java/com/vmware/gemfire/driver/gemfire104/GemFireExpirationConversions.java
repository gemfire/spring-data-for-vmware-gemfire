// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import com.vmware.gemfire.driver.spi.DriverCustomExpiry;
import com.vmware.gemfire.driver.spi.UnsupportedFeatureException;
import com.vmware.gemfire.driver.spi.config.Compressor;
import com.vmware.gemfire.driver.spi.config.EvictionAction;
import com.vmware.gemfire.driver.spi.config.EvictionAlgorithm;
import com.vmware.gemfire.driver.spi.config.EvictionAttributes;
import com.vmware.gemfire.driver.spi.config.ExpirationAction;
import com.vmware.gemfire.driver.spi.config.ExpirationAttributes;
import com.vmware.gemfire.driver.spi.config.ObjectSizer;
import com.vmware.gemfire.driver.spi.config.SnappyCompressor;
import com.vmware.gemfire.driver.spi.config.TimeToLiveResetPolicy;
import org.apache.geode.cache.CustomExpiry;

/** Maps expiration, eviction, sizing and compression values between the SPI and native types. Native types never leave this package. */
@SuppressWarnings("deprecation")
final class GemFireExpirationConversions {
	org.apache.geode.cache.ExpirationAttributes toNative(ExpirationAttributes attributes) {
		return new org.apache.geode.cache.ExpirationAttributes(attributes.getTimeout(), toNative(attributes.getAction()));
	}
	ExpirationAttributes fromNative(org.apache.geode.cache.ExpirationAttributes attributes) {
		return new ExpirationAttributes(attributes.getTimeout(), ExpirationAction.valueOf(attributes.getAction().toString().replace('-', '_').toUpperCase(java.util.Locale.ROOT)));
	}
	org.apache.geode.cache.ExpirationAction toNative(ExpirationAction action) {
		return switch (action) {
			case INVALIDATE -> org.apache.geode.cache.ExpirationAction.INVALIDATE;
			case LOCAL_INVALIDATE -> org.apache.geode.cache.ExpirationAction.LOCAL_INVALIDATE;
			case DESTROY -> org.apache.geode.cache.ExpirationAction.DESTROY;
			case LOCAL_DESTROY -> org.apache.geode.cache.ExpirationAction.LOCAL_DESTROY;
		};
	}
	org.apache.geode.cache.TimeToLiveResetPolicy toNative(TimeToLiveResetPolicy policy) {
		return org.apache.geode.cache.TimeToLiveResetPolicy.valueOf(policy.name());
	}
	TimeToLiveResetPolicy fromNative(org.apache.geode.cache.TimeToLiveResetPolicy policy) { return TimeToLiveResetPolicy.valueOf(policy.name()); }

	org.apache.geode.cache.EvictionAction toNative(EvictionAction action) {
		return switch (action) {
			case NONE -> org.apache.geode.cache.EvictionAction.NONE;
			case LOCAL_DESTROY -> org.apache.geode.cache.EvictionAction.LOCAL_DESTROY;
			case OVERFLOW_TO_DISK -> org.apache.geode.cache.EvictionAction.OVERFLOW_TO_DISK;
		};
	}
	org.apache.geode.cache.EvictionAttributes toNative(EvictionAttributes attributes) {
		var action = toNative(attributes.getAction());
		var sizer = toNative(attributes.getObjectSizer());
		return switch (attributes.getAlgorithm()) {
			case LRU_ENTRY -> org.apache.geode.cache.EvictionAttributes.createLRUEntryAttributes(attributes.getMaximum(), action);
			case LRU_HEAP -> org.apache.geode.cache.EvictionAttributes.createLRUHeapAttributes(sizer, action);
			case LRU_MEMORY -> org.apache.geode.cache.EvictionAttributes.createLRUMemoryAttributes(attributes.getMaximum(), sizer, action);
			case LIFO_ENTRY -> org.apache.geode.cache.EvictionAttributes.createLIFOEntryAttributes(attributes.getMaximum(), action);
			case LIFO_MEMORY -> org.apache.geode.cache.EvictionAttributes.createLIFOMemoryAttributes(attributes.getMaximum(), action);
			case NONE -> throw UnsupportedFeatureException.notIntroduced("EvictionAttributes.algorithmNone");
		};
	}
	EvictionAttributes fromNative(org.apache.geode.cache.EvictionAttributes attributes) {
		var algorithm = EvictionAlgorithm.parseAction(attributes.getAlgorithm().toString());
		var action = EvictionAction.parseAction(attributes.getAction().toString());
		return EvictionAttributes.of(algorithm, action, algorithm.isLRUHeap() ? 0 : attributes.getMaximum(), fromNative(attributes.getObjectSizer()));
	}
	org.apache.geode.cache.util.ObjectSizer toNative(ObjectSizer sizer) {
		if (sizer == null) return null;
		if (sizer == ObjectSizer.SIZE_CLASS_ONCE) return org.apache.geode.cache.util.ObjectSizer.SIZE_CLASS_ONCE;
		if (sizer == ObjectSizer.REFLECTION_SIZE) return org.apache.geode.cache.util.ObjectSizer.REFLECTION_SIZE;
		return new GemFireObjectSizer(sizer);
	}
	ObjectSizer fromNative(org.apache.geode.cache.util.ObjectSizer sizer) {
		if (sizer == null) return null;
		if (sizer == org.apache.geode.cache.util.ObjectSizer.SIZE_CLASS_ONCE) return ObjectSizer.SIZE_CLASS_ONCE;
		if (sizer == org.apache.geode.cache.util.ObjectSizer.REFLECTION_SIZE) return ObjectSizer.REFLECTION_SIZE;
		if (sizer instanceof GemFireObjectSizer adapter) return adapter.delegate;
		throw UnsupportedFeatureException.notIntroduced("EvictionAttributes.nativeObjectSizer");
	}

	org.apache.geode.compression.Compressor toNative(Compressor compressor) {
		if (compressor == null) return null;
		if (compressor instanceof SnappyCompressor) return org.apache.geode.compression.SnappyCompressor.getDefaultInstance();
		return new GemFireCompressor(compressor);
	}
	Compressor fromNative(org.apache.geode.compression.Compressor compressor) {
		if (compressor == null) return null;
		if (compressor instanceof GemFireCompressor adapter) return adapter.delegate;
		if (compressor instanceof org.apache.geode.compression.SnappyCompressor) return new SnappyCompressor();
		throw UnsupportedFeatureException.notIntroduced("Region.nativeCompressor");
	}

	<K, V> CustomExpiry<K, V> toNative(DriverCustomExpiry<K, V> custom) { return custom == null ? null : new GemFireCustomExpiry<>(custom); }
	<K, V> DriverCustomExpiry<K, V> fromNative(CustomExpiry<K, V> custom) {
		if (custom == null) return null;
		if (custom instanceof GemFireCustomExpiry<K, V> adapter) return adapter.delegate();
		throw UnsupportedFeatureException.notIntroduced("Region.nativeCustomExpiry");
	}

	private static final class GemFireObjectSizer implements org.apache.geode.cache.util.ObjectSizer {
		private final ObjectSizer delegate;
		GemFireObjectSizer(ObjectSizer delegate) { this.delegate = delegate; }
		@Override public int sizeof(Object value) { return delegate.sizeof(value); }
	}

	private static final class GemFireCompressor implements org.apache.geode.compression.Compressor {
		private final Compressor delegate;
		GemFireCompressor(Compressor delegate) { this.delegate = delegate; }
		@Override public byte[] compress(byte[] input) { return delegate.compress(input); }
		@Override public byte[] decompress(byte[] input) { return delegate.decompress(input); }
	}
}
