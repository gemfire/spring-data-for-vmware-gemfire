/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.schema;

import static java.util.Arrays.stream;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.DiskStore;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.asyncqueue.AsyncEventQueue;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.lucene.LuceneIndex;
import org.apache.geode.cache.query.Index;
import org.apache.geode.cache.wan.GatewayReceiver;
import org.apache.geode.cache.wan.GatewaySender;

/**
 * {@link SchemaObjectType} defines an enumeration of all the types of Apache Geode or Pivotal GemFire schema objects
 * (e.g. {@link Region}) that may possibly be handled by Spring Data Geode / Spring Data GemFire
 * and that can be created remotely, from a client application.
 *
 * @author John Blum
 * @see AsyncEventQueue
 * @see Cache
 * @see DiskStore
 * @see Region
 * @see ClientCache
 * @see Pool
 * @see Function
 * @see LuceneIndex
 * @see Index
 * @see GatewayReceiver
 * @see GatewaySender
 * @see SchemaObjectDefinition
 * @since 2.0.0
 */
public enum SchemaObjectType {

	ASYNC_EVENT_QUEUE(AsyncEventQueue.class),
	CACHE(Cache.class),
	CLIENT_CACHE(ClientCache.class),
	DISK_STORE(DiskStore.class),
	FUNCTION(Function.class),
	GATEWAY_RECEIVER(GatewayReceiver.class),
	GATEWAY_SENDER(GatewaySender.class),
	INDEX(Index.class),
	LUCENE_INDEX(LuceneIndex.class),
	POOL(Pool.class),
	REGION(Region.class),
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
	 * For example, given an instance of {@link Region}, this factory method will return
	 * {@link SchemaObjectType#REGION}.
	 *
	 * @param obj actual instance of a GemFire schema object, e.g. reference to a {@link Region}.
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
	 * For example, given the {@link Region} {@link Class interface} or any {@link Class sub-type} of {@link Region},
	 * this factory method will return {@link SchemaObjectType#REGION}.
	 *
	 * @param type {@link Class type} of the GemFire schema object, e.g. the {@link Region} {@link Class interface}.
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
