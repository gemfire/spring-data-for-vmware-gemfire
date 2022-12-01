/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * Unit Tests for {@link JndiDataSourceType} enum.
 *
 * @author John Blum
 * @see Test
 * @see JndiDataSourceType
 * @since 1.7.0
 */
public class JndiDataSourceTypeUnitTests {

	@Test
	public void testNames() {

		assertThat(JndiDataSourceType.MANAGED.getName()).isEqualTo("ManagedDataSource");
		assertThat(JndiDataSourceType.POOLED.getName()).isEqualTo("PooledDataSource");
		assertThat(JndiDataSourceType.SIMPLE.getName()).isEqualTo("SimpleDataSource");
		assertThat(JndiDataSourceType.XA.getName()).isEqualTo("XAPooledDataSource");
	}

	@Test
	public void testValueOfIgnoreCase() {

		assertThat(JndiDataSourceType.valueOfIgnoreCase("managedDataSource  ")).isEqualTo(JndiDataSourceType.MANAGED);
		assertThat(JndiDataSourceType.valueOfIgnoreCase("   ManAGEd")).isEqualTo(JndiDataSourceType.MANAGED);
		assertThat(JndiDataSourceType.valueOfIgnoreCase("POOLedDataSource")).isEqualTo(JndiDataSourceType.POOLED);
		assertThat(JndiDataSourceType.valueOfIgnoreCase("PoolED ")).isEqualTo(JndiDataSourceType.POOLED);
		assertThat(JndiDataSourceType.valueOfIgnoreCase(" SIMPLEDATASOURCE")).isEqualTo(JndiDataSourceType.SIMPLE);
		assertThat(JndiDataSourceType.valueOfIgnoreCase(" SIMPLE ")).isEqualTo(JndiDataSourceType.SIMPLE);
		assertThat(JndiDataSourceType.valueOfIgnoreCase(" xapooleddatasource  ")).isEqualTo(JndiDataSourceType.XA);
		assertThat(JndiDataSourceType.valueOfIgnoreCase("  xa  ")).isEqualTo(JndiDataSourceType.XA);
	}

	@Test
	public void testValueOfIgnoreCaseWithInvalidNames() {

		assertThat(JndiDataSourceType.valueOfIgnoreCase("ManageDataSource")).isNull();
		assertThat(JndiDataSourceType.valueOfIgnoreCase("ManagedDataSink")).isNull();
		assertThat(JndiDataSourceType.valueOfIgnoreCase("ManedDataSrc")).isNull();
		assertThat(JndiDataSourceType.valueOfIgnoreCase("PoolingDataSource")).isNull();
		assertThat(JndiDataSourceType.valueOfIgnoreCase("ComplexDataSource")).isNull();
		assertThat(JndiDataSourceType.valueOfIgnoreCase("SimplifiedDataSource")).isNull();
		assertThat(JndiDataSourceType.valueOfIgnoreCase("XA Pooled DataSource")).isNull();
		assertThat(JndiDataSourceType.valueOfIgnoreCase("X A")).isNull();
		assertThat(JndiDataSourceType.valueOfIgnoreCase("XADATASOURCE")).isNull();
		assertThat(JndiDataSourceType.valueOfIgnoreCase("XAPOOLED")).isNull();
		assertThat(JndiDataSourceType.valueOfIgnoreCase("XA POOLED")).isNull();
		assertThat(JndiDataSourceType.valueOfIgnoreCase("  ")).isNull();
		assertThat(JndiDataSourceType.valueOfIgnoreCase("")).isNull();
		assertThat(JndiDataSourceType.valueOfIgnoreCase(null)).isNull();
	}
}
