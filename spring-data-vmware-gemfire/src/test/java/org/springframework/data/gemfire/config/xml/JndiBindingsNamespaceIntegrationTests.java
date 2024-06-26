/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.internal.datasource.GemFireBasicDataSource;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.util.FileSystemUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for Apache Geode JNDI context bindings.
 *
 * This test requires a real cache
 *
 * @author David Turanski
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Cache
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class JndiBindingsNamespaceIntegrationTests extends IntegrationTestsSupport {

	@AfterClass
	public static void cleanupAfterTests() {
		FileSystemUtils.deleteRecursive(new File(FileSystemUtils.WORKING_DIRECTORY, "newDB"));
		FileSystemUtils.newFile(FileSystemUtils.WORKING_DIRECTORY, "derby.log").delete();
	}

	@Autowired
	private ClientCache cache;

	@Test
	public void testJndiBindings() throws Exception {

		Object dataSourceObject = cache.getJNDIContext().lookup("java:/SimpleDataSource");

		assertThat(dataSourceObject).isInstanceOf(GemFireBasicDataSource.class);

		GemFireBasicDataSource dataSource = (GemFireBasicDataSource) dataSourceObject;

		assertThat(dataSource.getJDBCDriver()).isEqualTo("org.apache.derby.jdbc.EmbeddedDriver");
		assertThat(dataSource.getLoginTimeout()).isEqualTo(60);
	}
}
