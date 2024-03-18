/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.process;

/**
 * A {@link FunctionalInterface} and extension of the {@link ProcessRunner} interface used to encapsulate
 * the runtime parameters for running (executing) a Java/JVM {@link Process}.
 *
 * @author John Blum
 * @see ProcessRunner
 * @since 1.0.0
 */
@FunctionalInterface
@SuppressWarnings("unused")
public interface JavaProcessRunner extends ProcessRunner {

	/**
	 * Gets the {@link String classpath} used to run the Java/JVM {@link Process}.
	 *
	 * Defaults to the parent JVM {@link Process} classpath as defined by {@code System.getProperty("java.class.path}).
	 *
	 * @return the {@link String classpath} used to run the Java/JVM {@link Process}.
	 */
	default String getClassPath() {
		return System.getProperty("java.class.path");
	}

	/**
	 * Gets the Java {@link Class} with the main method to run.
	 *
	 * @return the Java {@link Class} with the main method to run.
	 * @see Class
	 */
	default Class<?> getMainClass() {
		return null;
	}
}
