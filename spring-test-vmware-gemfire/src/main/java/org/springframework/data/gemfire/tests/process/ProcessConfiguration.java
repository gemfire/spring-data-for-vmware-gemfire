/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.tests.process;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.gemfire.tests.util.FileSystemUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * The {@link ProcessConfiguration} class is a container encapsulating configuration and context meta-data
 * for a running process.
 *
 * @author John Blum
 * @see Process
 * @see ProcessBuilder
 * @see ProcessExecutor
 * @since 0.0.1
 */
@SuppressWarnings("unused")
public class ProcessConfiguration {

	private final boolean redirectingErrorStream;

	private final File workingDirectory;

	private final List<String> command;

	private final Map<String, String> environment;

	public static ProcessConfiguration create(ProcessBuilder processBuilder) {

		Assert.notNull(processBuilder,
			"The ProcessBuilder used to configure and start the Process must not be null");

		return new ProcessConfiguration(processBuilder.command(), processBuilder.directory(),
			processBuilder.environment(), processBuilder.redirectErrorStream());
	}

	public ProcessConfiguration(List<String> command, File workingDirectory, Map<String, String> environment,
			boolean redirectErrorStream) {

		Assert.notEmpty(command, "Process command is required");

		Assert.isTrue(FileSystemUtils.isDirectory(workingDirectory),
			String.format("Process working directory [%s] is not valid", workingDirectory));

		this.command = Collections.unmodifiableList(new ArrayList<>(command));
		this.workingDirectory = workingDirectory;
		this.redirectingErrorStream = redirectErrorStream;

		this.environment = environment != null
			? Collections.unmodifiableMap(new HashMap<>(environment))
			: Collections.emptyMap();
	}

	public List<String> getCommand() {
		return this.command;
	}

	public String getCommandString() {
		return StringUtils.arrayToDelimitedString(getCommand().toArray(), " ");
	}

	public Map<String, String> getEnvironment() {
		return this.environment;
	}

	public boolean isRedirectingErrorStream() {
		return this.redirectingErrorStream;
	}

	public File getWorkingDirectory() {
		return this.workingDirectory;
	}

	@Override
	public String toString() {

		return "{ command = ".concat(getCommandString())
			.concat(", workingDirectory = ".concat(getWorkingDirectory().getAbsolutePath()))
			.concat(", environment = ".concat(String.valueOf(getEnvironment())))
			.concat(", redirectingErrorStream = ".concat(String.valueOf(isRedirectingErrorStream())))
			.concat(" }");
	}
}
