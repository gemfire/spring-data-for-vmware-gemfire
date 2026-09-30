package com.vmware.gemfire.driver.spi.config;

// SPI-owned mirror of the native ClientRegionShortcut (spec §5.5); each Driver Impl maps to/from its native equivalent.
public enum ClientRegionShortcut {

	PROXY, CACHING_PROXY, CACHING_PROXY_HEAP_LRU, CACHING_PROXY_OVERFLOW,
	LOCAL, LOCAL_PERSISTENT, LOCAL_HEAP_LRU, LOCAL_OVERFLOW, LOCAL_PERSISTENT_OVERFLOW

}
