/*
 * Copyright (c) VMware, Inc. 2023-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.data.gemfire.tests.util.FileSystemUtils;
import org.springframework.data.gemfire.tests.util.FileUtils;
import org.springframework.data.gemfire.tests.util.IOUtils;
import org.springframework.data.gemfire.tests.util.ThreadUtils;
import org.springframework.data.gemfire.tests.util.ThrowableUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * The {@link ProcessWrapper} class is a wrapper for a {@link Process} object representing an Operating System (OS)
 * process and the {@link ProcessBuilder} used to construct and start the {@link Process}.
 *
 * @author John Blum
 * @see File
 * @see InputStream
 * @see OutputStream
 * @see Process
 * @see ProcessBuilder
 * @see java.util.concurrent.Executor
 * @see Executors
 * @see ExecutorService
 * @see Future
 * @see ProcessConfiguration
 * @see ProcessInputStreamListener
 * @see ProcessUtils
 * @since 0.0.1
 */
@SuppressWarnings("unused")
public class ProcessWrapper {

	protected static final boolean DEFAULT_DAEMON_THREAD = true;

	protected static final int DEFAULT_PORT = -1;

	protected static final long DEFAULT_WAIT_TIME_MILLISECONDS = TimeUnit.SECONDS.toMillis(15);

	protected static final String DEFAULT_HOST = "localhost";

	protected static final String PROCESS_DESTROY_FORCIBLY_PROPERTY = "spring.data.gemfire.test.process.destroy-forcibly";

	private final List<ProcessInputStreamListener> listeners = new CopyOnWriteArrayList<>();

	protected final Logger log = Logger.getLogger(getClass().getName());

	private int port = DEFAULT_PORT;

	private final Process process;

	private final ProcessConfiguration processConfiguration;

	private String host = DEFAULT_HOST;

	public ProcessWrapper(@NonNull Process process, @NonNull ProcessConfiguration processConfiguration) {

		Assert.notNull(process, "Process is required");

		Assert.notNull(processConfiguration, "The context and configuration metadata providing details"
			+ " about the environment in which the process is running and how the process was configured and executed"
			+ " is required");

		this.process = process;
		this.processConfiguration = processConfiguration;

		init();
	}

	private void init() {

		newThread("Process OUT Stream Reader Thread",
			newProcessInputStreamReaderRunnable(process.getInputStream())).start();

		if (!isRedirectingErrorStream()) {
			newThread("Process ERR Stream Reader Thread",
				newProcessInputStreamReaderRunnable(process.getErrorStream())).start();
		}
	}

	private Runnable newProcessInputStreamReaderRunnable(InputStream in) {

		return () -> {

			if (isRunning()) {

				BufferedReader inputReader = new BufferedReader(new InputStreamReader(in));

				try {
					for (String input = inputReader.readLine(); input != null; input = inputReader.readLine()) {
						for (ProcessInputStreamListener listener : this.listeners) {
							listener.onInput(input);
						}
					}
				}
				catch (IOException ignore) {
					// Ignore IO error and just stop reading from the process input stream
					// An IO error occurred most likely because the process was terminated
				}
				finally {
					IOUtils.close(inputReader);
				}
			}
		};
	}

	private @NonNull Thread newThread(@NonNull String name, @NonNull Runnable task) {

		Assert.hasText(name, "Thread name is required");
		Assert.notNull(task, "Thread task is required");

		Thread thread = new Thread(task, name);

		thread.setDaemon(DEFAULT_DAEMON_THREAD);
		thread.setPriority(Thread.NORM_PRIORITY);

		return thread;
	}

	public boolean isAlive() {
		return ProcessUtils.isAlive(process);
	}

	public boolean isNotAlive() {
		return !isAlive();
	}

	public List<String> getCommand() {
		return this.processConfiguration.getCommand();
	}

	public String getCommandString() {
		return this.processConfiguration.getCommandString();
	}

	public Map<String, String> getEnvironment() {
		return this.processConfiguration.getEnvironment();
	}

	public String getHost() {
		return this.host;
	}

	public int getPid() {
		return ProcessUtils.findAndReadPid(getWorkingDirectory());
	}

	public int safeGetPid() {

		try {
			return getPid();
		}
		catch (Throwable ignore) {
			return -1;
		}
	}

	public int getPort() {
		return this.port;
	}

	public boolean isRedirectingErrorStream() {
		return this.processConfiguration.isRedirectingErrorStream();
	}

	public boolean isNotRunning() {
		return !isRunning();
	}

	public boolean isRunning() {
		return ProcessUtils.isRunning(this.process);
	}

	public File getWorkingDirectory() {
		return this.processConfiguration.getWorkingDirectory();
	}

	public int exitValue() {
		return this.process.exitValue();
	}

	public int safeExitValue() {

		try {
			return exitValue();
		}
		catch (IllegalThreadStateException ignore) {
			return -1;
		}
	}

	public @NonNull ProcessWrapper listeningOn(int port) {
		this.port = Math.max(port, DEFAULT_PORT);
		return this;
	}

	public @NonNull String readLogFile() throws IOException {

		File[] logFiles = FileSystemUtils.listFiles(getWorkingDirectory(),
			path -> (path != null && (path.isDirectory() || path.getAbsolutePath().endsWith(".log"))));

		if (logFiles.length > 0) {
			return readLogFile(logFiles[0]);
		}
		else {
			throw new FileNotFoundException(String.format("No log files found in process's [%d] working directory [%s]",
				safeGetPid(), getWorkingDirectory()));
		}
	}

	public @NonNull String readLogFile(@NonNull File log) throws IOException {
		return FileUtils.read(log);
	}

	public boolean register(@Nullable ProcessInputStreamListener listener) {
		return listener != null && listeners.add(listener);
	}

	public @NonNull ProcessWrapper registerShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
		return this;
	}

	public @NonNull ProcessWrapper runningOn(@Nullable String host) {
		this.host = StringUtils.hasText(host) ? host : DEFAULT_HOST;
		return this;
	}

	public void signal() {
		signal("\n");
	}

	public void signal(Object value) {

		try {

			OutputStream outputStream = this.process.getOutputStream();

			outputStream.write(String.valueOf(value).getBytes());
			outputStream.flush();
		}
		catch (IOException cause) {

			this.log.warning("Failed to signal process");

			if (this.log.isLoggable(Level.FINE)) {
				this.log.fine(ThrowableUtils.toString(cause));
			}
		}
	}

	@Deprecated
	public void signalStop() {

		try {
			ProcessUtils.signalStop(this.process);
		}
		catch (IOException cause) {

			this.log.warning("Failed to signal the process to stop");

			if (this.log.isLoggable(Level.FINE)) {
				this.log.fine(ThrowableUtils.toString(cause));
			}
		}
	}

	public int stop() {
		return stop(DEFAULT_WAIT_TIME_MILLISECONDS);
	}

	public int stop(long milliseconds) {

		if (isRunning()) {

			boolean interrupted = false;
			int exitValue = -1;
			int pid = safeGetPid();
			long timeout = System.currentTimeMillis() + milliseconds;

			AtomicBoolean exited = new AtomicBoolean(false);

			ExecutorService executorService = Executors.newSingleThreadExecutor();

			try {

				Future<Integer> futureExitValue = executorService.submit(() -> {
					this.process.destroy();
					int localExitValue = this.process.waitFor();
					exited.set(true);
					return localExitValue;
				});

				while (!exited.get() && System.currentTimeMillis() < timeout) {
					try {
						exitValue = futureExitValue.get(milliseconds, TimeUnit.MILLISECONDS);
						this.log.info(String.format("Process [%s] has stopped%n", pid));
					}
					catch (InterruptedException ignore) {
						interrupted = true;
					}
				}

			}
			catch (TimeoutException cause) {
				exitValue = -1;
				this.log.warning(String.format("Process [%1$d] did not stop within the allotted timeout of %2$d seconds%n",
					pid, TimeUnit.MILLISECONDS.toSeconds(milliseconds)));
			}
			catch (Exception ignore) {
				// handles CancellationException, ExecutionException
			}
			finally {
				executorService.shutdownNow();
				if (interrupted) {
					Thread.currentThread().interrupt();
				}
			}

			return exitValue;
		}
		else {
			return exitValue();
		}
	}

	public int shutdown() {

		int pid = safeGetPid();
		this.log.info(String.format("Stopping process [%d]...%n", pid));

		if (isRunning()) {
			stop();
			if (isRunning() && isShutdownForciblyEnabled()) {
				this.process.destroyForcibly();
			}
		}

		return safeExitValue();
	}

	private boolean isShutdownForciblyEnabled() {
		return Boolean.getBoolean(PROCESS_DESTROY_FORCIBLY_PROPERTY);
	}

	public boolean unregister(ProcessInputStreamListener listener) {
		return this.listeners.remove(listener);
	}

	public void waitFor() {
		waitFor(DEFAULT_WAIT_TIME_MILLISECONDS);
	}

	public void waitFor(long milliseconds) {
		ThreadUtils.timedWait(milliseconds, 500, this::isRunning);
	}
}
