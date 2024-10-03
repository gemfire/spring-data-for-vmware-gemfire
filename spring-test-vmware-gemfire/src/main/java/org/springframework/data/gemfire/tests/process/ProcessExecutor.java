/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.process;

import static org.springframework.data.gemfire.util.ArrayUtils.nullSafeArray;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.gemfire.tests.util.FileSystemUtils;
import org.springframework.data.gemfire.util.JavaVersion;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * The {@link ProcessExecutor} class is a utility class for launching and running Java processes.
 *
 * @author John Blum
 * @see File
 * @see Process
 * @see ProcessBuilder
 * @see System
 * @see ProcessConfiguration
 * @see ProcessWrapper
 * @since 0.0.1
 */
@SuppressWarnings("unused")
public abstract class ProcessExecutor {

	public static final File JAVA_EXE = new File(new File(FileSystemUtils.JAVA_HOME, "bin"), "java");

	public static final String JAVA_CLASSPATH = System.getProperty("java.class.path");

	protected static final String SPRING_DATA_GEMFIRE_SYSTEM_PROPERTY_PREFIX = "spring.data.gemfire.";
	protected static final String SPRING_GEMFIRE_SYSTEM_PROPERTY_PREFIX = "spring.gemfire.";

	/**
	 * Launch (run/execute) the given Java {@link Class} (program) passing in the given array of {@link String arguments}
	 * to the program at runtime.
	 *
	 * @param type Java {@link Class} (program) to launch in a forked JVM process.
	 * @param arguments array of {@link String} arguments to the pass to the Java program.
	 * @return a {@link ProcessWrapper} wrapping the Java {@link Process} representing the forked JVM process.
	 * @throws IOException if the Java program could not be launched (run).
	 * @see ProcessWrapper
	 * @see #launch(File, Class, String...)
	 * @see Class
	 */
	public static ProcessWrapper launch(Class<?> type, String... arguments) throws IOException {
		return launch(FileSystemUtils.WORKING_DIRECTORY, type, arguments);
	}

	/**
	 * Launch (run/execute) the given Java {@link Class} (program) in the specified {@link File working directory}
	 * and pass in the given array of {@link String arguments} to the program at runtime.
	 *
	 * @param type Java {@link Class} (program) to launch in a forked JVM process.
	 * @param workingDirectory {@link File} referring to the working directory of the forked JVM process.
	 * @param arguments array of {@link String} arguments to the pass to the Java program.
	 * @return a {@link ProcessWrapper} wrapping the Java {@link Process} representing the forked JVM process.
	 * @throws IOException if the Java program could not be launched (run).
	 * @see ProcessWrapper
	 * @see #launch(File, String, Class, String...)
	 * @see Class
	 * @see File
	 */
	public static ProcessWrapper launch(File workingDirectory, Class<?> type, String... arguments) throws IOException {
		return launch(workingDirectory, JAVA_CLASSPATH, type, arguments);
	}

	/**
	 * Launch (run/execute) the given Java {@link Class} (program) with the specified {@link String JVM classpath}
	 * in the {@link File current working directory} and pass in the given array of {@link Object arguments}
	 * to the program at runtime.
	 *
	 * @param type Java {@link Class} (program) to launch in a forked JVM process.
	 * @param classpath {@link String} containing the classpath elements to pass to the JVM to run the Java program.
	 * @param arguments array of {@link String} arguments to the pass to the Java program.
	 * @return a {@link ProcessWrapper} wrapping the Java {@link Process} representing the forked JVM process.
	 * @throws IOException if the Java program could not be launched (run).
	 * @see ProcessWrapper
	 * @see #launch(File, String, Class, String...)
	 * @see Class
	 * @see File
	 */
	public static ProcessWrapper launch(String classpath, Class<?> type, String... arguments) throws IOException {
		return launch(FileSystemUtils.WORKING_DIRECTORY, classpath, type, arguments);
	}

	/**
	 * Launch (run/execute) the given Java {@link Class} (program) in the specified {@link File working directory},
	 * using the given {@link String Java CLASSPATH} and pass in the given array of {@link String arguments}
	 * to the program at runtime.
	 *
	 * @param type Java {@link Class} (program) to launch in a forked JVM process.
	 * @param workingDirectory {@link File} referring to the {@literal working directory} of the forked JVM process.
	 * @param classpath {@link String} containing the classpath elements to pass to the JVM to run the Java program.
	 * @param arguments array of {@link String} arguments to the pass to the Java program.
	 * @return a {@link ProcessWrapper} wrapping the Java {@link Process} representing the forked JVM process.
	 * @throws IOException if the Java program could not be launched (run).
	 * @see ProcessWrapper
	 * @see #buildCommand(String, Class, String...)
	 * @see ProcessBuilder
	 * @see Class
	 * @see File
	 */
	public static ProcessWrapper launch(File workingDirectory, String classpath, Class<?> type, String... arguments)
			throws IOException {

		ProcessBuilder processBuilder = new ProcessBuilder()
			.command(buildCommand(classpath, type, arguments))
			.directory(validateDirectory(workingDirectory))
			.redirectErrorStream(true);

		Process process = processBuilder.start();

		ProcessWrapper processWrapper = new ProcessWrapper(process, ProcessConfiguration.create(processBuilder));

		processWrapper.register(input -> System.err.printf("[FORK] - %s%n", input));
		processWrapper.registerShutdownHook();

		return processWrapper;
	}

	protected static String[] buildCommand(String classpath, Class<?> type, String... args) {

		Assert.notNull(type, "The main Java class to launch must not be null");

		List<String> command = new ArrayList<>();
		List<String> programArguments = new ArrayList<>(args.length);

		command.add(JAVA_EXE.getAbsolutePath());
		command.add("-server");
		command.add("-ea");
		addJavaRuntimeEnvironmentSpecificJvmOptions(command);
		command.add("-classpath");
		command.add(StringUtils.hasText(classpath) ? classpath : JAVA_CLASSPATH);
		command.addAll(getSpringGemFireSystemProperties());

		for (String arg : nullSafeArray(args, String.class)) {
			if (isJvmOption(arg)) {
				command.add(arg);
			}
			else if (isValidArgument(arg)) {
				programArguments.add(arg);
			}
		}

		command.add(type.getName());
		command.addAll(programArguments);

		return command.toArray(new String[0]);
	}

	private static List<String> addJavaRuntimeEnvironmentSpecificJvmOptions(List<String> command) {

		if (JavaVersion.current().isNewerThanOrEqualTo(JavaVersion.SIXTEEN)) {
			command.add("--add-opens");
			command.add("java.base/java.lang=ALL-UNNAMED");
			command.add("--add-opens");
			command.add("java.base/java.nio=ALL-UNNAMED");
			command.add("--add-opens");
			command.add("java.base/java.util=ALL-UNNAMED");
		}

		return command;
	}

	protected static Collection<? extends String> getSpringGemFireSystemProperties() {

		return System.getProperties().stringPropertyNames().stream()
			.filter(property -> property.startsWith(SPRING_DATA_GEMFIRE_SYSTEM_PROPERTY_PREFIX)
				|| property.startsWith(SPRING_GEMFIRE_SYSTEM_PROPERTY_PREFIX))
			.map(property -> String.format("-D%1$s=%2$s", property, System.getProperty(property)))
			.collect(Collectors.toList());
	}

	protected static boolean isJvmOption(String option) {
		return (StringUtils.hasText(option) && (option.startsWith("-D") || option.startsWith("-X")));
	}

	protected static boolean isValidArgument(String argument) {
		return StringUtils.hasText(argument);
	}

	protected static File validateDirectory(File workingDirectory) {

		Assert.isTrue(workingDirectory != null && (workingDirectory.isDirectory() || workingDirectory.mkdirs()),
			String.format("Failed to create working directory [%s]", workingDirectory));

		return workingDirectory;
	}
}
