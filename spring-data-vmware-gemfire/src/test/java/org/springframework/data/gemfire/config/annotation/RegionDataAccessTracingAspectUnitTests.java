/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientRegionShortcut;
import org.junit.After;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.RegionDataAccessTracingAspect;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.logging.slf4j.logback.TestAppender;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ClassUtils;

/**
 * Unit Tests for {@link RegionDataAccessTracingAspect}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.GudClientCache
 * @see org.apache.geode.cache.GudRegion
 * @see org.springframework.data.gemfire.client.ClientRegionFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.support.RegionDataAccessTracingAspect
 * @see org.springframework.data.gemfire.tests.logging.slf4j.logback.TestAppender
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.0.2
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class RegionDataAccessTracingAspectUnitTests extends IntegrationTestsSupport {

	private static final String LOGBACK_LOGGER_CLASS_NAME = "ch.qos.logback.classic.Logger";

	@BeforeClass
	public static void setupBeforeTestSuite() {
		assumeLogbackIsPresent();
	}

	private static void assumeLogbackIsPresent() {
		Assume.assumeTrue(String.format("Ignoring test class [%s]; Logback is not on the classpath",
			RegionDataAccessTracingAspectUnitTests.class.getName()), ClassUtils.isPresent(LOGBACK_LOGGER_CLASS_NAME,
				Thread.currentThread().getContextClassLoader()));
	}

	@After
	public void tearDown() {
		TestAppender.getInstance().clear();
	}

	@Autowired
	@Qualifier("ClientRegion")
	private GudRegion<Object, Object> region;

	private Runnable regionCallbackArgument(AtomicBoolean called) {
		return () -> called.set(true);
	}

	@Test
	public void logsRegionCreate() {

		this.region.create("testKey", "testValue");

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.create\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionCreate",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionCreateWithCallbackArgument() {

		this.region.create("testKey", "testValue",
			regionCallbackArgument(new AtomicBoolean(false)));

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.create\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionCreateWithCallbackArgument",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionGet() {

		this.region.get("testKey");

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.get\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionGet",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionGetAll() {

		this.region.getAll(Arrays.asList("testKeyOne", "testKeyTwo", "testKeyThree"));

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.getAll\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionGetAll",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionGetAllWithCallbackArgument() {

		this.region.getAll(Arrays.asList("testKeyOne", "testKeyTwo", "testKeyThree"),
			regionCallbackArgument(new AtomicBoolean(false)));

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.getAll\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionGetAllWithCallbackArgument",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionGetEntry() {

		this.region.getEntry("testKey");

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.getEntry\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionGetEntry",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionInvalidate() {

		this.region.put("testKey", "testValue");
		this.region.invalidate("testKey");

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.invalidate\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionInvalidate",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionInvalidateWithCallbackArgument() {

		this.region.put("testKey", "testValue");
		this.region.invalidate("testKey", regionCallbackArgument(new AtomicBoolean(false)));

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.invalidate\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionInvalidateWithCallbackArgument",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionKeySet() {

		this.region.keySet();

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.keySet\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionKeySet",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionKeySetOnServer() {

		this.region.keySetOnServer();

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.keySetOnServer\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionKeySetOnServer",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionLocalClear() {

		this.region.localClear();

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.localClear\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionLocalClear",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionLocalDestroy() {

		this.region.localDestroy("testKey");

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.localDestroy\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionLocalDestroy",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionLocalDestroyWithCallbackArgument() {

		this.region.localDestroy("testKey", regionCallbackArgument(new AtomicBoolean(false)));

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.localDestroy\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionLocalDestroyWithCallbackArgument",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionLocalInvalidate() {

		this.region.put("testKey", "testValue");
		this.region.localInvalidate("testKey");

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.localInvalidate\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionLocalInvalidate",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionLocalInvalidateWithCallbackArgument() {

		this.region.put("testKey", "testValue");
		this.region.localInvalidate("testKey", regionCallbackArgument(new AtomicBoolean(false)));

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.localInvalidate\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionLocalInvalidateWithCallbackArgument",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionPut() {

		this.region.put("testKey", "testValue");

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.put\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionPut",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionPutWithCallbackArgument() {

		this.region.put("testKey", "testValue", regionCallbackArgument(new AtomicBoolean(false)));

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.put\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionPutWithCallbackArgument",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionPutAll() {

		this.region.putAll(Collections.emptyMap());

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.putAll\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionPutAll",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionPutAllWithCallbackArgument() {

		this.region.putAll(Collections.emptyMap(), regionCallbackArgument(new AtomicBoolean(false)));

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.putAll\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionPutAllWithCallbackArgument",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionPutIfAbsent() {

		this.region.putIfAbsent("testKey", "testValue");

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.putIfAbsent\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionPutIfAbsent",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionQuery() throws Exception {

		this.region.query("SELECT * FROM /GudRegion WHERE id = ?");

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.query\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionQuery",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionRemoveWithKey() {

		this.region.remove("testKey");

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.remove\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionRemoveWithKey",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionRemoveWithKeyAndValue() {

		this.region.remove("testKey", "testValue");

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.remove\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionRemoveWithKeyAndValue",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionRemoveAll() {

		this.region.removeAll(Arrays.asList("testKeyOne", "testKeyTwo", "testKeyThree"));

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.removeAll\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionRemoveAll",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionRemoveAllWithCallbackArgument() {

		this.region.removeAll(Arrays.asList("testKeyOne", "testKeyTwo", "testKeyThree"),
			regionCallbackArgument(new AtomicBoolean(false)));

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.removeAll\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionRemoveAllWithCallbackArgument",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionReplace() {

		this.region.replace("testKey", "testValue");

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.replace\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionReplace",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionReplaceWithKeyOldValueNewValue() {

		this.region.replace("testKey", "testOldValue", "testNewValue");

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.replace\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionReplaceWithKeyOldValueNewValue",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionSelectValue() throws Exception {

		this.region.selectValue("id = ?");

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.selectValue\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionSelectValue",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionSize() {

		this.region.size();

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.size\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionSize",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionSizeOnServer() {

		this.region.sizeOnServer();

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.sizeOnServer\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionSizeOnServer",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void logsRegionValues() {

		this.region.values();

		String logMessage = TestAppender.getInstance().lastLogMessage();

		assertThat(logMessage)
			.containsPattern("GudRegion data access call \\[org.apache.geode.cache.GudRegion.*.values\\(..\\)\\] with stack trace");

		assertThat(logMessage).contains(String.format("%s.logsRegionValues",
			RegionDataAccessTracingAspectUnitTests.class.getName()));
	}

	@Test
	public void doesNotLogRegionGetName() {

		this.region.getName();

		assertThat(TestAppender.getInstance().lastLogMessage()).isNull();
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects()
	@EnableRegionDataAccessTracing
	static class TestConfiguration {

		@Bean("ClientRegion")
		@SuppressWarnings("unused")
		public ClientRegionFactoryBean<Object, Object> clientRegion(GudClientCache gemfireCache) {

			ClientRegionFactoryBean<Object, Object> clientRegion = new ClientRegionFactoryBean<>();

			clientRegion.setCache(gemfireCache);
			clientRegion.setClose(false);
			clientRegion.setShortcut(GudClientRegionShortcut.LOCAL);

			return clientRegion;
		}
	}
}
