/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.schema;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * The {@link SchemaObjectDefiner} interface defines a contract for implementing objects
 * that can reverse engineer a schema object instance back into a definition of the schema object.
 *
 * @author John Blum
 * @see SchemaObjectDefinition
 * @see SchemaObjectType
 * @since 2.0.0
 */
public interface SchemaObjectDefiner {

	/**
	 * Returns a {@link Set} of {@link SchemaObjectType schema object types} definable by this definer.
	 *
	 * @return a {@link Set} of {@link SchemaObjectType schema object types} definable by this definer.
	 * @see SchemaObjectType
	 * @see Set
	 */
	default Set<SchemaObjectType> getSchemaObjectTypes() {
		return Collections.emptySet();
	}

	/**
	 * Determines whether this definer is able to define the given {@link Object schema object} instance.
	 *
	 * @param schemaObject {@link Object} to evaluate.
	 * @return a boolean value indicating whether this definer is able to define
	 * the given {@link Object schema object} instance.
	 * @see Object#getClass()
	 * @see #canDefine(Class)
	 */
	default boolean canDefine(Object schemaObject) {
		return Optional.ofNullable(schemaObject).map(Object::getClass).filter(this::canDefine).isPresent();
	}

	/**
	 * Determines whether this definer is able to define schema objects of the given {@link Class type}.
	 *
	 * @param schemaObjectType {@link Class type} of the {@link Object schema object} instance to evaluate.
	 * @return a boolean value indicating whether this definer is able to define {@link Object schema objects}
	 * of the given {@link Class type}.
	 * @see SchemaObjectType#from(Class)
	 * @see #canDefine(SchemaObjectType)
	 */
	default boolean canDefine(Class<?> schemaObjectType) {
		return canDefine(SchemaObjectType.from(schemaObjectType));
	}

	/**
	 * Determines whether this definer is able to define schema objects of the given
	 * {@link SchemaObjectType enumerated schema object type}.
	 *
	 * @param schemaObjectType {@link SchemaObjectType} to evaluate.
	 * @return a boolean value indicating whether this handler is able to handle schema objects
	 * of the given {@link SchemaObjectType enumerated schema object type}.
	 * @see SchemaObjectType
	 */
	default boolean canDefine(SchemaObjectType schemaObjectType) {
		return getSchemaObjectTypes().contains(schemaObjectType);
	}

	/**
	 * Returns an {@link Optional} {@link SchemaObjectDefinition definition} for the given
	 * {@link Object schema object} instance.
	 *
	 * @param schemaObject {@link Object schema object} to define.
	 * @return an {@link Optional} {@link SchemaObjectDefinition definition} for the given
	 * {@link Object schema object} instance.
	 * @see SchemaObjectDefinition
	 */
	Optional<? extends SchemaObjectDefinition> define(Object schemaObject);

}
