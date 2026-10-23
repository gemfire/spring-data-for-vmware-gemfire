/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.*;

/** Private native server process for client integration tests; never exposed through the SPI. */
final class NativeServerFixture implements AutoCloseable {
	private final Process process;
	private final ExecutorService reader = Executors.newSingleThreadExecutor();
	private final BlockingQueue<String> messages = new LinkedBlockingQueue<>();
	private final BufferedWriter input;
	final int port;
	NativeServerFixture(Path directory) throws Exception {
		process = new ProcessBuilder(Path.of(System.getProperty("java.home"), "bin", "java").toString(),
			"--add-exports=java.base/sun.nio.ch=ALL-UNNAMED", "--add-opens=java.base/java.lang=ALL-UNNAMED",
			"--add-opens=java.base/java.nio=ALL-UNNAMED", "-cp", System.getProperty("nativeFixtureClasspath"),
			NativeServerProcess.class.getName()).directory(java.nio.file.Files.createTempDirectory(directory, "server-").toFile()).redirectErrorStream(true).start();
		input = process.outputWriter();
		reader.submit(() -> {
			try (var output = process.inputReader()) {
				for (String line; (line = output.readLine()) != null;) if (line.startsWith("CALLBACK_")) messages.add(line);
			} catch (IOException failure) { messages.add("CALLBACK_FAILURE:" + failure); }
		});
		String ready = messages.poll(30, TimeUnit.SECONDS);
		if (ready == null || !ready.startsWith("CALLBACK_READY:")) {
			close(); throw new AssertionError("Server did not become ready: " + ready);
		}
		port = Integer.parseInt(ready.substring("CALLBACK_READY:".length()));
	}
	void command(String command) throws Exception {
		input.write(command); input.newLine(); input.flush();
		assertThat(messages.poll(30, TimeUnit.SECONDS)).isEqualTo("CALLBACK_DONE:" + command);
	}
	void crash() throws Exception { process.destroyForcibly(); assertThat(process.waitFor(10, TimeUnit.SECONDS)).isTrue(); }
	@Override public void close() throws Exception {
		try {
			if (process.isAlive()) {
				input.write("STOP"); input.newLine(); input.flush();
				if (!process.waitFor(10, TimeUnit.SECONDS)) crash();
			}
		} finally { process.destroyForcibly(); reader.shutdownNow(); }
	}
}
