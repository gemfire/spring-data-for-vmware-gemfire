/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.ArrayList;
import java.util.List;

import org.apache.geode.cache.query.types.CollectionType;
import org.apache.geode.cache.query.types.ObjectType;
import org.apache.geode.cache.query.types.StructType;

import com.vmware.gemfire.driver.spi.DriverCollectionType;
import com.vmware.gemfire.driver.spi.DriverObjectType;
import com.vmware.gemfire.driver.spi.DriverStructType;

/** Detached snapshots of native result type descriptors. */
final class GemFireObjectTypes {

	private GemFireObjectTypes() {
	}

	static DriverObjectType adapt(ObjectType type) {
		if (type == null) return null;
		if (type instanceof StructType struct) return struct(struct);
		if (type instanceof CollectionType collection) return collection(collection);
		return new Simple(type.getSimpleClassName(), type.isCollectionType(), type.isMapType(), type.isStructType());
	}

	static DriverCollectionType collection(CollectionType type) {
		return new Collection(type.getSimpleClassName(), type.isMapType(), adapt(type.getElementType()),
			type.allowsDuplicates(), type.isOrdered());
	}

	static DriverStructType struct(StructType type) {
		List<String> names = List.of(type.getFieldNames());
		List<DriverObjectType> types = new ArrayList<>();
		for (ObjectType field : type.getFieldTypes()) types.add(adapt(field));
		return new Struct(type.getSimpleClassName(), names, List.copyOf(types));
	}

	private record Simple(String simpleClassName, boolean isCollectionType, boolean isMapType, boolean isStructType)
			implements DriverObjectType {
	}

	private record Collection(String simpleClassName, boolean isMapType, DriverObjectType elementType,
			boolean allowsDuplicates, boolean isOrdered) implements DriverCollectionType {
		@Override public boolean isCollectionType() { return true; }
		@Override public boolean isStructType() { return false; }
	}

	private record Struct(String simpleClassName, List<String> fieldNames, List<DriverObjectType> fieldTypes)
			implements DriverStructType {
		@Override public boolean isCollectionType() { return false; }
		@Override public boolean isMapType() { return false; }
		@Override public boolean isStructType() { return true; }
	}
}
