/*
 * Copyright (c) VMware, Inc. 2023-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.process;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.data.gemfire.tests.util.FileSystemUtils;

/**
 * A {@link FunctionalInterface} encapsulating the contract, logic and strategy for running (executing)
 * an Operating System (OS) [JVM] {@link Process}.
 *
 * @author John Blum
 * @see File
 * @see Process
 * @see ProcessExecutor
 * @see ProcessWrapper
 * @since 1.0.0
 */
@FunctionalInterface
@SuppressWarnings("unused")
public interface ProcessRunner {

	/**
	 * Gets the {@link File working directory} in which the {@link Process} will run.
	 *
	 * Default to the user directory as defined by {@code System.getProperty("user.dir")}.
	 *
	 * @return {@link File} representing the {@literal working directory} in which the {@link Process} will run.
	 * @see File
	 */
	default File getWorkingDirectory() {
		return FileSystemUtils.WORKING_DIRECTORY;
	}

	/**
	 * Runs a [JVM] {@link Process} defined by the this {@literal run} method.
	 *
	 * @param arguments array of {@link String} arguments passed to the program at runtime.
	 * @return the {@link ProcessWrapper} representing the [JVM] {@link Process}.
	 * @throws IOException if the {@link Process} could not be run.
	 * @see ProcessWrapper
	 */
	ProcessWrapper run(String... arguments) throws IOException;

	/**
	 * Runs a [JVM] {@link Process} defined by the this {@literal run} method.
	 *
	 * @param arguments {@link List} of {@link String} arguments passed to the program at runtime.
	 * @return the {@link ProcessWrapper} representing the [JVM] {@link Process}.
	 * @throws IOException if the {@link Process} could not be run.
	 * @see ProcessWrapper
	 * @see #run(String...)
	 */
	default ProcessWrapper run(List<String> arguments) throws IOException {
		return run(arguments.toArray(new String[0]));
	}
}
