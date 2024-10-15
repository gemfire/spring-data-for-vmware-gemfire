/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Scanner;
import java.util.logging.Logger;

import org.springframework.data.gemfire.tests.util.FileSystemUtils;
import org.springframework.data.gemfire.tests.util.IOUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * The {@link ProcessUtils} class is a utility class for working with Operating System (OS) {@link Process processes}.
 *
 * @author John Blum
 * @see File
 * @see Process
 * @since 0.0.1
 */
@SuppressWarnings("unused")
public abstract class ProcessUtils {

	protected static final Logger log = Logger.getLogger(ProcessUtils.class.getName());

	protected static final String TERM_TOKEN = "<TERM/>";

	/* (non-Javadoc) */
	public static int currentPid() {

		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

		String runtimeMXBeanName = runtimeMXBean.getName();

		Exception cause = null;

		if (StringUtils.hasText(runtimeMXBeanName)) {

			int atSignIndex = runtimeMXBeanName.indexOf('@');

			if (atSignIndex > 0) {
				try {
					return Integer.parseInt(runtimeMXBeanName.substring(0, atSignIndex));
				}
				catch (NumberFormatException e) {
					cause = e;
				}
			}
		}

		throw new PidNotFoundException(String.format("Process ID (PID) not available [%s]",
			runtimeMXBeanName), cause);
	}

	public static boolean isAlive(Process process) {
		return process != null && process.isAlive();
	}

	public static boolean isRunning(int processId) {
		throw new UnsupportedOperationException("Operation not supported");
	}

	public static boolean isRunning(Process process) {

		try {
			process.exitValue();
			return false;
		}
		catch (IllegalThreadStateException ignore) {
			return true;
		}
	}

	public static int findAndReadPid(File workingDirectory) {

		File pidFile = findPidFile(workingDirectory);

		if (pidFile == null) {
			throw new PidNotFoundException(
				String.format("No PID file was found in working directory [%s] or any of it's sub-directories",
					workingDirectory));
		}

		return readPid(pidFile);
	}

	protected static File findPidFile(File workingDirectory) {

        if (!FileSystemUtils.isDirectory(workingDirectory)) {
            return null;
        }

		for (File file : workingDirectory.listFiles(DirectoryPidFileFilter.INSTANCE)) {
			if (file.isDirectory()) {
				file = findPidFile(file);
			}

			if (PidFileFilter.INSTANCE.accept(file)) {
				return file;
			}
		}

		return null;
	}

	public static int readPid(File pidFile) {

		Assert.isTrue(pidFile != null && pidFile.isFile(),
			String.format("File [%s] is not a valid file", pidFile));

		BufferedReader fileReader = null;

		String pidValue = null;

		try {

			fileReader = new BufferedReader(new FileReader(pidFile));
			pidValue = String.valueOf(fileReader.readLine()).trim();

			return Integer.parseInt(pidValue);
		}
		catch (FileNotFoundException cause) {
			throw new PidNotFoundException(String.format("PID file [%s] not found", pidFile), cause);
		}
		catch (IOException cause) {
			throw new PidNotFoundException(String.format("Failed to read PID from file [%s]", pidFile), cause);
		}
		catch (NumberFormatException cause) {
			throw new PidNotFoundException(String.format("Value [%1$s] from PID file [%2$s] was not a valid numerical PID",
				pidValue, pidFile), cause);
		}
		finally {
			IOUtils.close(fileReader);
		}
	}

	public static void writePid(File pidFile, int pid) throws IOException {

		Assert.isTrue(pidFile != null && (pidFile.isFile() || pidFile.createNewFile()),
			String.format("File [%s] is not a valid file", pidFile));

		Assert.isTrue(pid > 0, String.format("PID [%d] must greater than 0", pid));

		PrintWriter fileWriter = new PrintWriter(new BufferedWriter(
			new FileWriter(pidFile, false), 16), true);

		try {
			fileWriter.println(pid);
		}
		finally {
			pidFile.deleteOnExit();
			FileSystemUtils.close(fileWriter);
		}
	}

	public static void signalStop(Process process) throws IOException {

		if (isRunning(process)) {
			OutputStream processOutputStream = process.getOutputStream();
			processOutputStream.write(TERM_TOKEN.concat("\n").getBytes());
			processOutputStream.flush();
		}
	}

	@SuppressWarnings("all")
	public static void waitForStopSignal() {

		Scanner in = new Scanner(System.in);

		while (!TERM_TOKEN.equals(in.next()));
	}

	protected static class DirectoryPidFileFilter extends PidFileFilter {

		protected static final DirectoryPidFileFilter INSTANCE = new DirectoryPidFileFilter();

		@Override
		public boolean accept(File path) {
			return (path != null && (path.isDirectory() || super.accept(path)));
		}
	}

	protected static class PidFileFilter implements FileFilter {

		protected static final PidFileFilter INSTANCE = new PidFileFilter();

		@Override
		public boolean accept(File path) {
			return (path != null && path.isFile() && path.getName().toLowerCase().endsWith(".pid"));
		}
	}
}
