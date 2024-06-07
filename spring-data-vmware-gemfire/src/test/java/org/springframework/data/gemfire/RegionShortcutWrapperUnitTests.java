/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.apache.geode.cache.RegionShortcut;

/**
 * Unit Tests for {@link RegionShortcutWrapper} enum.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.RegionShortcut
 * @see org.springframework.data.gemfire.RegionShortcutWrapper
 * @since 1.4.0
 */
public class RegionShortcutWrapperUnitTests {

	@Test
	public void unspecifiedRegionShortcut() {
		assertThat(RegionShortcutWrapper.valueOf((RegionShortcut) null)).isEqualTo(RegionShortcutWrapper.UNSPECIFIED);
	}

	@Test
	public void isHeapLru() {

		assertThat(RegionShortcutWrapper.LOCAL.isHeapLru()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_HEAP_LRU.isHeapLru()).isTrue();
		assertThat(RegionShortcutWrapper.LOCAL_OVERFLOW.isHeapLru()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT.isHeapLru()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT_OVERFLOW.isHeapLru()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE.isHeapLru()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_HEAP_LRU.isHeapLru()).isTrue();
		assertThat(RegionShortcutWrapper.REPLICATE_OVERFLOW.isHeapLru()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_PERSISTENT.isHeapLru()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_PERSISTENT_OVERFLOW.isHeapLru()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_PROXY.isHeapLru()).isFalse();
		assertThat(RegionShortcutWrapper.UNSPECIFIED.isHeapLru()).isFalse();
	}

	@Test
	public void isLocal() {

		assertThat(RegionShortcutWrapper.LOCAL.isLocal()).isTrue();
		assertThat(RegionShortcutWrapper.LOCAL_HEAP_LRU.isLocal()).isTrue();
		assertThat(RegionShortcutWrapper.LOCAL_OVERFLOW.isLocal()).isTrue();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT.isLocal()).isTrue();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT_OVERFLOW.isLocal()).isTrue();
		assertThat(RegionShortcutWrapper.REPLICATE.isLocal()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_HEAP_LRU.isLocal()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_OVERFLOW.isLocal()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_PERSISTENT.isLocal()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_PERSISTENT_OVERFLOW.isLocal()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE.isLocal()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_HEAP_LRU.isLocal()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_OVERFLOW.isLocal()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_PERSISTENT.isLocal()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_PERSISTENT_OVERFLOW.isLocal()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_PROXY.isLocal()).isFalse();
		assertThat(RegionShortcutWrapper.UNSPECIFIED.isLocal()).isFalse();
	}

	@Test
	public void isOverflow() {

		assertThat(RegionShortcutWrapper.LOCAL.isOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_HEAP_LRU.isOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_OVERFLOW.isOverflow()).isTrue();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT.isOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT_OVERFLOW.isOverflow()).isTrue();
		assertThat(RegionShortcutWrapper.REPLICATE.isOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_HEAP_LRU.isOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_OVERFLOW.isOverflow()).isTrue();
		assertThat(RegionShortcutWrapper.REPLICATE_PERSISTENT.isOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_PERSISTENT_OVERFLOW.isOverflow()).isTrue();
		assertThat(RegionShortcutWrapper.REPLICATE.isOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_HEAP_LRU.isOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_OVERFLOW.isOverflow()).isTrue();
		assertThat(RegionShortcutWrapper.REPLICATE_PERSISTENT.isOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_PERSISTENT_OVERFLOW.isOverflow()).isTrue();
		assertThat(RegionShortcutWrapper.REPLICATE_PROXY.isOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.UNSPECIFIED.isOverflow()).isFalse();
	}

	@Test
	public void isPersistent() {

		assertThat(RegionShortcutWrapper.LOCAL.isPersistent()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_HEAP_LRU.isPersistent()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_OVERFLOW.isPersistent()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT.isPersistent()).isTrue();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT_OVERFLOW.isPersistent()).isTrue();
		assertThat(RegionShortcutWrapper.REPLICATE.isPersistent()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_HEAP_LRU.isPersistent()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_OVERFLOW.isPersistent()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_PERSISTENT.isPersistent()).isTrue();
		assertThat(RegionShortcutWrapper.REPLICATE_PERSISTENT_OVERFLOW.isPersistent()).isTrue();
		assertThat(RegionShortcutWrapper.REPLICATE.isPersistent()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_HEAP_LRU.isPersistent()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_OVERFLOW.isPersistent()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_PERSISTENT.isPersistent()).isTrue();
		assertThat(RegionShortcutWrapper.REPLICATE_PERSISTENT_OVERFLOW.isPersistent()).isTrue();
		assertThat(RegionShortcutWrapper.REPLICATE_PROXY.isPersistent()).isFalse();
		assertThat(RegionShortcutWrapper.UNSPECIFIED.isPersistent()).isFalse();
	}

	@Test
	public void isPersistentOverflow() {

		assertThat(RegionShortcutWrapper.LOCAL.isPersistentOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_HEAP_LRU.isPersistentOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_OVERFLOW.isPersistentOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT.isPersistentOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT_OVERFLOW.isPersistentOverflow()).isTrue();
		assertThat(RegionShortcutWrapper.REPLICATE.isPersistentOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_HEAP_LRU.isPersistentOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_OVERFLOW.isPersistentOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_PERSISTENT.isPersistentOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_PERSISTENT_OVERFLOW.isPersistentOverflow()).isTrue();
		assertThat(RegionShortcutWrapper.REPLICATE.isPersistentOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_HEAP_LRU.isPersistentOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_OVERFLOW.isPersistentOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_PERSISTENT.isPersistentOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_PERSISTENT_OVERFLOW.isPersistentOverflow()).isTrue();
		assertThat(RegionShortcutWrapper.REPLICATE_PROXY.isPersistentOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.UNSPECIFIED.isPersistentOverflow()).isFalse();
	}
	@Test
	public void isProxy() {

		assertThat(RegionShortcutWrapper.LOCAL.isProxy()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_HEAP_LRU.isProxy()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_OVERFLOW.isProxy()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT.isProxy()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT_OVERFLOW.isProxy()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE.isProxy()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_HEAP_LRU.isProxy()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_OVERFLOW.isProxy()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_PERSISTENT.isProxy()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_PERSISTENT_OVERFLOW.isProxy()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE.isProxy()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_HEAP_LRU.isProxy()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_OVERFLOW.isProxy()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_PERSISTENT.isProxy()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_PERSISTENT_OVERFLOW.isProxy()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_PROXY.isProxy()).isTrue();
		assertThat(RegionShortcutWrapper.UNSPECIFIED.isProxy()).isFalse();
	}
	@Test
	public void isRedundant() {

		assertThat(RegionShortcutWrapper.LOCAL.isRedundant()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_HEAP_LRU.isRedundant()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_OVERFLOW.isRedundant()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT.isRedundant()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT_OVERFLOW.isRedundant()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE.isRedundant()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_HEAP_LRU.isRedundant()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_OVERFLOW.isRedundant()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_PERSISTENT.isRedundant()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_PERSISTENT_OVERFLOW.isRedundant()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE_PROXY.isRedundant()).isFalse();
		assertThat(RegionShortcutWrapper.UNSPECIFIED.isRedundant()).isFalse();
	}

	@Test
	public void isReplicate() {

		assertThat(RegionShortcutWrapper.LOCAL.isReplicate()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_HEAP_LRU.isReplicate()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_OVERFLOW.isReplicate()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT.isReplicate()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT_OVERFLOW.isReplicate()).isFalse();
		assertThat(RegionShortcutWrapper.REPLICATE.isReplicate()).isTrue();
		assertThat(RegionShortcutWrapper.REPLICATE_HEAP_LRU.isReplicate()).isTrue();
		assertThat(RegionShortcutWrapper.REPLICATE_OVERFLOW.isReplicate()).isTrue();
		assertThat(RegionShortcutWrapper.REPLICATE_PERSISTENT.isReplicate()).isTrue();
		assertThat(RegionShortcutWrapper.REPLICATE_PERSISTENT_OVERFLOW.isReplicate()).isTrue();
		assertThat(RegionShortcutWrapper.REPLICATE_PROXY.isReplicate()).isTrue();
		assertThat(RegionShortcutWrapper.UNSPECIFIED.isReplicate()).isFalse();
	}
}
