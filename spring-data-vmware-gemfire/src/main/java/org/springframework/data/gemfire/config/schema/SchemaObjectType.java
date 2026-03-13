/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.config.schema;

import static java.util.Arrays.stream;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudDiskStore;
import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.data.gemfire.gud.api.GudIndex;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudRegion;

/**
 * {@link SchemaObjectType} defines an enumeration of all the types of Apache Geode or Pivotal GemFire schema objects
 * (e.g. {@link GudRegion}) that may possibly be handled by Spring Data Geode / Spring Data GemFire
 * and that can be created remotely, from a client application.
 *
 * @author John Blum
 * @see GudDiskStore
 * @see GudRegion
 * @see GudClientCache
 * @see GudPool
 * @see GudFunction
 * @see GudIndex
 * @see SchemaObjectDefinition
 * @since 2.0.0
 */
public enum SchemaObjectType {

	CLIENT_CACHE(GudClientCache.class),
	DISK_STORE(GudDiskStore.class),
	FUNCTION(GudFunction.class),
	INDEX(GudIndex.class),
	POOL(GudPool.class),
	REGION(GudRegion.class),
	UNKNOWN(Void.class);

	private final Class<?> objectType;

	/**
	 * Constructs an instance of an {@link SchemaObjectType} enumerated value initialized with
	 * the actual GemFire {@link Class schema object instance type}.
	 *
	 * @param objectType actual {@link Class interface type} of the GemFire schema object instance.
	 * @see Class
	 */
	SchemaObjectType(Class<?> objectType) {
		this.objectType = objectType;
	}

	/**
	 * Null-safe factory method used to look up and resolve the corresponding {@link SchemaObjectType}
	 * given an instance of a GemFire schema object.
	 *
	 * For example, given an instance of {@link GudRegion}, this factory method will return
	 * {@link SchemaObjectType#REGION}.
	 *
	 * @param obj actual instance of a GemFire schema object, e.g. reference to a {@link GudRegion}.
	 * @return a corresponding {@link SchemaObjectType} for a given instance of a GemFire schema object.
	 * If the type cannot be determined, then {@link SchemaObjectType#UNKNOWN} is returned.
	 * @see #from(Class)
	 */
	public static SchemaObjectType from(Object obj) {
		return stream(SchemaObjectType.values())
			.filter(it -> it.getObjectType().isInstance(obj))
			.findFirst().orElse(UNKNOWN);
	}

	/**
	 * Null-safe factory method used to look up and resolve the corresponding {@link SchemaObjectType}
	 * given the type of GemFire schema object.
	 *
	 * For example, given the {@link GudRegion} {@link Class interface} or any {@link Class sub-type} of {@link GudRegion},
	 * this factory method will return {@link SchemaObjectType#REGION}.
	 *
	 * @param type {@link Class type} of the GemFire schema object, e.g. the {@link GudRegion} {@link Class interface}.
	 * @return a corresponding {@link SchemaObjectType} for a given {@link Class type }of a GemFire schema object.
	 * If the type cannot be determined, then {@link SchemaObjectType#UNKNOWN} is returned.
	 * @see #from(Object)
	 */
	public static SchemaObjectType from(Class<?> type) {
		return stream(SchemaObjectType.values())
			.filter(it -> type != null && it.getObjectType().isAssignableFrom(type))
			.findFirst().orElse(UNKNOWN);
	}

	/**
	 * Returns the {@link Class class type} of the GemFire schema object represented by this enumerated value.
	 *
	 * @return the {@link Class class type} of the GemFire schema object represented by this enumerated value.
	 * @see Class
	 */
	public Class<?> getObjectType() {
		return this.objectType;
	}
}
