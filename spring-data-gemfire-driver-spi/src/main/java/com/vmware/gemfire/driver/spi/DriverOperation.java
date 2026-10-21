/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Stable operation vocabulary. Numeric codes match the required 10.4 native contract;
 * future drivers must translate explicitly if native codes change. Flags describe the operation,
 * not a promise that the region distributes it (a local region can deliver CREATE).
 */
public enum DriverOperation {
	MARKER(false, false, 32, 0),
	CREATE(false, false, 1, 0),
	PUTALL_CREATE(false, false, 1, 32),
	GET(false, false, 3, 0),
	GET_ENTRY(false, false, 5, 0),
	CONTAINS_KEY(false, false, 6, 0),
	CONTAINS_VALUE(false, false, 7, 0),
	CONTAINS_VALUE_FOR_KEY(false, false, 9, 0),
	FUNCTION_EXECUTION(false, false, 10, 0),
	SEARCH_CREATE(false, false, 1, 1),
	LOCAL_LOAD_CREATE(false, false, 1, 2),
	NET_LOAD_CREATE(false, false, 1, 4),
	UPDATE(false, false, 2, 0),
	PUTALL_UPDATE(false, false, 2, 32),
	SEARCH_UPDATE(false, false, 2, 1),
	LOCAL_LOAD_UPDATE(false, false, 2, 2),
	NET_LOAD_UPDATE(false, false, 2, 4),
	INVALIDATE(false, false, 4, 0),
	LOCAL_INVALIDATE(true, false, 4, 0),
	DESTROY(false, false, 8, 0),
	LOCAL_DESTROY(true, false, 8, 0),
	EVICT_DESTROY(true, false, 8, 16),
	REGION_LOAD_SNAPSHOT(false, true, 8, 0),
	REGION_LOCAL_DESTROY(true, true, 8, 0),
	REGION_CREATE(true, true, 1, 0),
	REGION_CLOSE(true, true, 8, 0),
	REGION_DESTROY(false, true, 8, 0),
	EXPIRE_DESTROY(false, false, 8, 8),
	EXPIRE_LOCAL_DESTROY(true, false, 8, 8),
	EXPIRE_INVALIDATE(false, false, 4, 8),
	EXPIRE_LOCAL_INVALIDATE(true, false, 4, 8),
	REGION_EXPIRE_DESTROY(false, true, 8, 8),
	REGION_EXPIRE_LOCAL_DESTROY(true, true, 8, 8),
	REGION_EXPIRE_INVALIDATE(false, true, 4, 8),
	REGION_EXPIRE_LOCAL_INVALIDATE(true, true, 4, 8),
	REGION_LOCAL_INVALIDATE(true, true, 4, 0),
	REGION_INVALIDATE(false, true, 4, 0),
	REGION_CLEAR(false, true, 16, 0),
	REGION_LOCAL_CLEAR(true, true, 16, 0),
	CACHE_CREATE(true, true, 1, 0),
	CACHE_CLOSE(true, true, 8, 0),
	FORCED_DISCONNECT(true, true, 8, 0),
	REGION_REINITIALIZE(true, true, 8, 0),
	CACHE_RECONNECT(true, true, 8, 0),
	PUT_IF_ABSENT(false, false, 1, 64),
	REPLACE(false, false, 2, 64),
	REMOVE(false, false, 8, 0),
	UPDATE_VERSION_STAMP(false, false, 11, 0),
	REMOVEALL_DESTROY(false, false, 8, 128),
	GET_FOR_REGISTER_INTEREST(false, false, 12, 0);

	private final boolean local;
	private final boolean region;
	private final int type;
	private final int details;
	DriverOperation(boolean local, boolean region, int type, int details) {
		this.local = local; this.region = region; this.type = type; this.details = details;
	}
	public byte nativeOrdinal() { return (byte) ordinal(); }
	/** The 10.4 wire table reserves codes 50 through 54 as null entries. */
	public static DriverOperation fromOrdinal(byte ordinal) {
		return ordinal >= 50 && ordinal < 55 ? null : values()[ordinal];
	}
	public boolean isLocal() { return local; }
	public boolean isRegion() { return region; }
	public boolean isDistributed() { return !local; }
	public boolean isEntry() { return !region; }
	public boolean isCreate() { return type == 1 && !region; }
	public boolean isUpdate() { return type == 2 && !region; }
	public boolean isInvalidate() { return type == 4 && !region; }
	public boolean isDestroy() { return type == 8 && !region; }
	public boolean isGet() { return type == 3; }
	public boolean isContainsKey() { return type == 6; }
	public boolean isContainsValue() { return type == 7; }
	public boolean isContainsValueForKey() { return type == 9; }
	public boolean isFunctionExecution() { return type == 10; }
	public boolean isGetEntry() { return type == 5; }
	public boolean isGetForRegisterInterest() { return type == 12; }
	public boolean isClear() { return type == 16; }
	public boolean isRegionInvalidate() { return type == 4 && region; }
	public boolean isRegionDestroy() { return type == 8 && region; }
	public boolean isPutAll() { return (details & 32) != 0; }
	public boolean isRemoveAll() { return (details & 128) != 0; }
	public boolean isExpiration() { return (details & 8) != 0; }
	public boolean isEviction() { return (details & 16) != 0; }
	public boolean isLocalLoad() { return (details & 2) != 0; }
	public boolean isNetLoad() { return (details & 4) != 0; }
	public boolean isLoad() { return (details & 6) != 0; }
	public boolean isNetSearch() { return (details & 1) != 0; }
	public boolean isSearchOrLoad() { return (details & 7) != 0; }
	public boolean guaranteesOldValue() { return (details & 64) != 0; }
	public boolean isClose() {
		return this == REGION_CLOSE || this == CACHE_CLOSE || this == CACHE_RECONNECT || this == FORCED_DISCONNECT;
	}
	public DriverOperation getCorrespondingUpdateOp() {
		if (!isCreate()) return this;
		return switch (details) {
			case 1 -> SEARCH_UPDATE; case 2 -> LOCAL_LOAD_UPDATE; case 4 -> NET_LOAD_UPDATE;
			case 32 -> PUTALL_UPDATE; default -> UPDATE;
		};
	}
	public DriverOperation getCorrespondingCreateOp() {
		if (!isUpdate()) return this;
		return switch (details) {
			case 1 -> SEARCH_CREATE; case 2 -> LOCAL_LOAD_CREATE; case 4 -> NET_LOAD_CREATE;
			case 32 -> PUTALL_CREATE; default -> CREATE;
		};
	}
	@Override public String toString() { return this == UPDATE_VERSION_STAMP ? "UPDATE_VERSION" : name(); }
}
