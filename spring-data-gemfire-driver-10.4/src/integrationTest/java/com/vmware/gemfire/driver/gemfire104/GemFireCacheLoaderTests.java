/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.geode.cache.LoaderHelper;
import org.apache.geode.cache.Region;
import org.junit.jupiter.api.Test;

import com.vmware.gemfire.driver.spi.DriverCacheLoader;
import com.vmware.gemfire.driver.spi.DriverLoaderHelper;
import com.vmware.gemfire.driver.spi.DriverOperationException;

class GemFireCacheLoaderTests {

	@Test
	void networkSearchPassesTheFlagAndResultToNativeHelper() {
		for (boolean doNetLoad : new boolean[] { false, true }) {
			GemFireCacheLoader<String, String> adapter = adapter(doNetLoad);
			assertThat(adapter.load(new NativeHelper() {
				@Override public String netSearch(boolean flag) {
					assertThat(flag).isEqualTo(doNetLoad);
					return "native-result";
				}
			})).isEqualTo("native-result");
		}
	}

	@Test
	void networkSearchTranslatesNativeFailureAndKeepsCause() {
		RuntimeException failure = new IllegalStateException("native-secret");
		assertThatThrownBy(() -> adapter(false).load(new NativeHelper() {
			@Override public String netSearch(boolean flag) { throw failure; }
		})).isInstanceOf(DriverOperationException.class).hasCause(failure)
			.hasMessageNotContaining("native-secret");
	}

	private GemFireCacheLoader<String, String> adapter(boolean doNetLoad) {
		return new GemFireCacheLoader<>(new DriverCacheLoader<>() {
			@Override public String load(DriverLoaderHelper<String, String> helper) { return helper.netSearch(doNetLoad); }
			@Override public void close() { }
		});
	}

	private abstract static class NativeHelper implements LoaderHelper<String, String> {
		@Override public String getKey() { return "key"; }
		@Override public Region<String, String> getRegion() { throw new AssertionError("Not needed for network search"); }
		@Override public Object getArgument() { return null; }
	}
}
