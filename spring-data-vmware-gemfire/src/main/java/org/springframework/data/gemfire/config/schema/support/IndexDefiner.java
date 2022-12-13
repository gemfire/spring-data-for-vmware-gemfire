/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.schema.support;

import static org.springframework.data.gemfire.util.CollectionUtils.asSet;

import java.util.Optional;
import java.util.Set;

import org.apache.geode.cache.query.Index;

import org.springframework.data.gemfire.config.schema.SchemaObjectDefiner;
import org.springframework.data.gemfire.config.schema.SchemaObjectType;
import org.springframework.data.gemfire.config.schema.definitions.IndexDefinition;

/**
 * The {@link {{@link IndexDefiner }} class is responsible for defining an {@link Index} given a reference to
 * an {@link Index} instance.
 *
 * @author John Blum
 * @see org.apache.geode.cache.query.Index
 * @see org.springframework.data.gemfire.config.schema.SchemaObjectDefiner
 * @see org.springframework.data.gemfire.config.schema.SchemaObjectType
 * @see org.springframework.data.gemfire.config.schema.definitions.IndexDefinition
 * @since 2.0.0
 */
public class IndexDefiner implements SchemaObjectDefiner {

	@Override
	public Set<SchemaObjectType> getSchemaObjectTypes() {
		return asSet(SchemaObjectType.INDEX);
	}

	@Override
	public Optional<IndexDefinition> define(Object schemaObject) {
		return Optional.ofNullable(schemaObject).filter(this::canDefine).map(it -> IndexDefinition.from((Index) it));
	}
}
