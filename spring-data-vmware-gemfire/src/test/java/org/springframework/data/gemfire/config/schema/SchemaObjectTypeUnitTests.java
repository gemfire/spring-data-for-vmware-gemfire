/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.schema;

import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.springframework.data.gemfire.util.CollectionUtils.asSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.gemfire.gud.api.GudDiskStore;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.data.gemfire.gud.api.GudIndex;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit tests for {@link SchemaObjectType}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.springframework.data.gemfire.config.schema.SchemaObjectType
 * @since 1.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class SchemaObjectTypeUnitTests {

	@Test
	public void objectTypesAreSetAndCorrect() {

		Set<Class<?>> expectedSchemaObjectTypes = asSet(GudClientCache.class,
			GudDiskStore.class, GudFunction.class, GudIndex.class,
			GudPool.class, GudRegion.class, Void.class);

		Set<Class<?>> actualSchemaObjectTypes = stream(SchemaObjectType.values())
			.map(SchemaObjectType::getObjectType)
			.collect(Collectors.toSet());

		assertThat(actualSchemaObjectTypes).hasSameSizeAs(expectedSchemaObjectTypes);
		assertThat(actualSchemaObjectTypes).containsAll(expectedSchemaObjectTypes);
	}

	@Test
	public void fromClass() {
		stream(SchemaObjectType.values()).forEach(it ->
			assertThat(SchemaObjectType.from(it.getObjectType())).isSameAs(it));
	}

	@Test
	public void fromNullIsUnknown() {
		assertThat(SchemaObjectType.from(null)).isSameAs(SchemaObjectType.UNKNOWN);
		assertThat(SchemaObjectType.from((Object) null)).isSameAs(SchemaObjectType.UNKNOWN);
	}

	@Test
	public void fromObject() {
		stream(SchemaObjectType.values()).filter(it -> !SchemaObjectType.UNKNOWN.equals(it)).forEach(it ->
			assertThat(SchemaObjectType.from(mock(it.getObjectType()))).isSameAs(it));
	}

	@Test
	public void fromUntypedObjectIsUnknown() {
		assertThat(SchemaObjectType.from(new Object())).isSameAs(SchemaObjectType.UNKNOWN);
	}
}
