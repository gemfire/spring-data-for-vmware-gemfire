/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.logging.slf4j.logback;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit Tests for {@link TestAppender}.
 *
 * @author John Blum
 * @see Test
 * @see Logger
 * @see LoggerFactory
 * @see TestAppender
 * @since 0.0.5.RELEASE
 */
public class TestAppenderUnitTests {

	private static final Logger logger = LoggerFactory.getLogger(TestAppenderUnitTests.class);

	@Test
	public void logEventsAppendedCorrectly() {

		TestAppender testAppender = TestAppender.getInstance();

		assertThat(testAppender).isNotNull();
		assertThat(testAppender.lastLogMessage()).isNull();

		LoggableObject object = new LoggableObject();

		object.logAnError();
		object.logAnInfoMessage();
		object.logAWarning();

		assertThat(testAppender.lastLogMessage()).isEqualTo("WARN TEST");
		assertThat(testAppender.lastLogMessage()).isEqualTo("ERROR TEST");
		assertThat(testAppender.lastLogMessage()).isNull();
	}

	static class LoggableObject {

		public void logAnError() {
			logger.error("ERROR TEST");
		}

		public void logAnInfoMessage() {
			logger.info("INFO TEST");
		}

		public void logAWarning() {
			logger.warn("WARN TEST");
		}
	}
}
