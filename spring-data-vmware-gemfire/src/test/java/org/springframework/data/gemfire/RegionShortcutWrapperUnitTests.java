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
		assertThat(RegionShortcutWrapper.UNSPECIFIED.isHeapLru()).isFalse();
	}

	@Test
	public void isLocal() {

		assertThat(RegionShortcutWrapper.LOCAL.isLocal()).isTrue();
		assertThat(RegionShortcutWrapper.LOCAL_HEAP_LRU.isLocal()).isTrue();
		assertThat(RegionShortcutWrapper.LOCAL_OVERFLOW.isLocal()).isTrue();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT.isLocal()).isTrue();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT_OVERFLOW.isLocal()).isTrue();
		assertThat(RegionShortcutWrapper.UNSPECIFIED.isLocal()).isFalse();
	}

	@Test
	public void isOverflow() {

		assertThat(RegionShortcutWrapper.LOCAL.isOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_HEAP_LRU.isOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_OVERFLOW.isOverflow()).isTrue();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT.isOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT_OVERFLOW.isOverflow()).isTrue();
		assertThat(RegionShortcutWrapper.UNSPECIFIED.isOverflow()).isFalse();
	}

	@Test
	public void isPersistent() {

		assertThat(RegionShortcutWrapper.LOCAL.isPersistent()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_HEAP_LRU.isPersistent()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_OVERFLOW.isPersistent()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT.isPersistent()).isTrue();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT_OVERFLOW.isPersistent()).isTrue();
		assertThat(RegionShortcutWrapper.UNSPECIFIED.isPersistent()).isFalse();
	}

	@Test
	public void isPersistentOverflow() {

		assertThat(RegionShortcutWrapper.LOCAL.isPersistentOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_HEAP_LRU.isPersistentOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_OVERFLOW.isPersistentOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT.isPersistentOverflow()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT_OVERFLOW.isPersistentOverflow()).isTrue();
		assertThat(RegionShortcutWrapper.UNSPECIFIED.isPersistentOverflow()).isFalse();
	}
	@Test
	public void isProxy() {

		assertThat(RegionShortcutWrapper.LOCAL.isProxy()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_HEAP_LRU.isProxy()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_OVERFLOW.isProxy()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT.isProxy()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT_OVERFLOW.isProxy()).isFalse();
		assertThat(RegionShortcutWrapper.UNSPECIFIED.isProxy()).isFalse();
	}
	@Test
	public void isRedundant() {

		assertThat(RegionShortcutWrapper.LOCAL.isRedundant()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_HEAP_LRU.isRedundant()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_OVERFLOW.isRedundant()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT.isRedundant()).isFalse();
		assertThat(RegionShortcutWrapper.LOCAL_PERSISTENT_OVERFLOW.isRedundant()).isFalse();
		assertThat(RegionShortcutWrapper.UNSPECIFIED.isRedundant()).isFalse();
	}
}
