/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.integration;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.junit.AfterClass;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.client.ClientCache;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.gemfire.config.annotation.CacheServerApplication;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnablePdx;
import org.springframework.data.gemfire.tests.integration.config.ClientServerIntegrationTestsConfiguration;
import org.springframework.data.gemfire.tests.process.JavaProcessRunner;
import org.springframework.data.gemfire.tests.process.ProcessWrapper;
import org.springframework.data.gemfire.tests.util.SpringUtils;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Abstract base class used to bootstrap Apache Geode {@link Cache} and/or {@link ClientCache} applications
 * as independent (forked), {@link Process child processes}.
 *
 * @author John Blum
 * @see File
 * @see InetAddress
 * @see Executors
 * @see ThreadFactory
 * @see Cache
 * @see ClientCache
 * @see ApplicationContext
 * @see Bean
 * @see Configuration
 * @see ContextRefreshedEvent
 * @see EventListener
 * @see CacheServerApplication
 * @see ClientCacheApplication
 * @see ClientServerIntegrationTestsSupport
 * @see ClientServerIntegrationTestsConfiguration
 * @see JavaProcessRunner
 * @see ProcessWrapper
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class ForkingClientServerIntegrationTestsSupport extends ClientServerIntegrationTestsSupport {

	protected static final String REMOVE_TEST_DIRECTORY_PROPERTY = "spring.data.gemfire.test.directory.remove";

	private static ProcessWrapper gemfireServer;

	public static @NonNull ProcessWrapper startGemFireServer(@NonNull Class<?> gemfireServerMainClass,
			@NonNull String... arguments) throws IOException {

		return startGemFireServer(new JavaProcessRunner() {

			@Override
			public Class<?> getMainClass() {
				return gemfireServerMainClass;
			}

			@Override
			public ProcessWrapper run(String... arguments) throws IOException {
				return ForkingClientServerIntegrationTestsSupport.run(getMainClass(), arguments);
			}
		}, arguments);
	}

	public static @NonNull ProcessWrapper startGemFireServer(@NonNull File workingDirectory,
			@NonNull Class<?> gemfireServerMainClass, @NonNull String... arguments) throws IOException {

		return startGemFireServer(new JavaProcessRunner() {

			@Override
			public Class<?> getMainClass() {
				return gemfireServerMainClass;
			}

			@Override
			public File getWorkingDirectory() {
				return workingDirectory;
			}

			@Override
			public ProcessWrapper run(String... arguments) throws IOException {
				return ForkingClientServerIntegrationTestsSupport.run(getWorkingDirectory(), getMainClass(), arguments);
			}
		}, arguments);
	}

	public static @NonNull ProcessWrapper startGemFireServer(@NonNull String classpath,
			@NonNull Class<?> gemfireServerMainClass, @NonNull String... arguments) throws IOException {

		return startGemFireServer(new JavaProcessRunner() {

			@Override
			public String getClassPath() {
				return classpath;
			}

			@Override
			public Class<?> getMainClass() {
				return gemfireServerMainClass;
			}

			@Override
			public ProcessWrapper run(String... arguments) throws IOException {
				return ForkingClientServerIntegrationTestsSupport.run(getClassPath(), getMainClass(), arguments);
			}
		}, arguments);
	}

	public static @NonNull ProcessWrapper startGemFireServer(@NonNull File workingDirectory, @NonNull String classpath,
			@NonNull Class<?> gemfireServerMainClass, @NonNull String... arguments) throws IOException {

		return startGemFireServer(new JavaProcessRunner() {

			@Override
			public String getClassPath() {
				return classpath;
			}

			@Override
			public Class<?> getMainClass() {
				return gemfireServerMainClass;
			}

			@Override
			public File getWorkingDirectory() {
				return workingDirectory;
			}

			@Override
			public ProcessWrapper run(String... arguments) throws IOException {
				return ForkingClientServerIntegrationTestsSupport.run(getWorkingDirectory(),
					getClassPath(), getMainClass(), arguments);
			}
		}, arguments);
	}

	private static @NonNull ProcessWrapper startGemFireServer(@NonNull JavaProcessRunner processRunner,
			@NonNull String... arguments) throws IOException {

		int availablePort = setAndGetPoolPortProperty(setAndGetCacheServerPortProperty(findAndReserveAvailablePort()));

		List<String> argumentList = new ArrayList<>(Arrays.asList(ArrayUtils.nullSafeArray(arguments, String.class)));

		argumentList.add(String.format("-D%s=%d", GEMFIRE_CACHE_SERVER_PORT_PROPERTY, availablePort));

		ProcessWrapper gemfireServerProcessWrapper = processRunner.run(argumentList);

		gemfireServerProcessWrapper = gemfireServerProcessWrapper
			.runningOn(InetAddress.getLocalHost().getHostAddress())
			.listeningOn(availablePort);

		setGemFireServerProcess(gemfireServerProcessWrapper);
		waitForServerToStart("localhost", availablePort);

		return gemfireServerProcessWrapper;
	}

	public static ProcessWrapper startGeodeServer(@NonNull Class<?> geodeServerMainClass, String... arguments)
			throws IOException {

		return startGemFireServer(geodeServerMainClass, arguments);
	}

	public static ProcessWrapper startGeodeServer(@NonNull File workingDirectory,
			@NonNull Class<?> geodeServerMainClass, String... arguments) throws IOException {

		return startGemFireServer(workingDirectory, geodeServerMainClass, arguments);
	}

	public static ProcessWrapper startGeodeServer(@NonNull String classpath,
			@NonNull Class<?> geodeServerMainClass, String... arguments) throws IOException {

		return startGemFireServer(classpath, geodeServerMainClass, arguments);
	}

	public static ProcessWrapper startGeodeServer(@NonNull File workingDirectory, @NonNull String classpath,
			@NonNull Class<?> geodeServerMainClass, String... arguments) throws IOException {

		return startGemFireServer(workingDirectory, classpath, geodeServerMainClass, arguments);
	}

	protected static int setAndGetCacheServerPortProperty(int port) {

		System.setProperty(GEMFIRE_CACHE_SERVER_PORT_PROPERTY, String.valueOf(port));

		return port;
	}

	protected static int setAndGetPoolPortProperty(int port) {

		System.setProperty(GEMFIRE_POOL_SERVERS_PROPERTY, String.format(GEMFIRE_LOCALHOST_PORT, port));

		return port;
	}

	@AfterClass
	public static void stopGemFireServer() {

		getGemFireServerProcess().ifPresent(ForkingClientServerIntegrationTestsSupport::stop);

		if (isTestDirectoryRemovalEnabled()) {
			getGemFireServerProcess()
				.map(ProcessWrapper::getWorkingDirectory)
				.ifPresent(IntegrationTestsSupport::removeRecursiveDirectory);
		}

		setGemFireServerProcess(null);
	}

	private static boolean isTestDirectoryRemovalEnabled() {
		return !System.getProperties().containsKey(REMOVE_TEST_DIRECTORY_PROPERTY)
			|| Boolean.getBoolean(REMOVE_TEST_DIRECTORY_PROPERTY);
	}

	@AfterClass
	public static void clearCacheServerPortAndPoolPortProperties() {
		System.clearProperty(GEMFIRE_CACHE_SERVER_PORT_PROPERTY);
		System.clearProperty(GEMFIRE_POOL_SERVERS_PROPERTY);
	}

	protected static synchronized void setGemFireServerProcess(@Nullable ProcessWrapper gemfireServerProcess) {
		gemfireServer = gemfireServerProcess;
	}

	protected static synchronized Optional<ProcessWrapper> getGemFireServerProcess() {
		return Optional.ofNullable(gemfireServer);
	}

	@EnablePdx
	@ClientCacheApplication
	public static class BaseGemFireClientConfiguration extends ClientServerIntegrationTestsConfiguration { }

	@EnablePdx
	@CacheServerApplication(name = "ForkingClientServerIntegrationTestsSupport", logLevel = GEMFIRE_LOG_LEVEL)
	public static class BaseGemFireServerConfiguration extends ClientServerIntegrationTestsConfiguration {

		public static void main(String[] args) {

			AnnotationConfigApplicationContext applicationContext =
				new AnnotationConfigApplicationContext(BaseGemFireServerConfiguration.class);

			applicationContext.registerShutdownHook();
		}
	}

	@Configuration
	public static class SpringApplicationTerminatorConfiguration {

		@Bean
		public SpringApplicationTerminatorConfigurer springApplicationTerminatorConfigurer() {
			return SpringApplicationTerminatorConfigurer.EMPTY;
		}

		@EventListener(classes = ContextRefreshedEvent.class)
		public void configureTerminator(@NonNull ContextRefreshedEvent event) {

			Function<ApplicationContext, SpringApplicationTerminatorConfigurer> safeSpringApplicationTerminatorConfigurerResolver =
				applicationContext -> SpringUtils.safeGetValue(() ->
						applicationContext.getBean(SpringApplicationTerminatorConfigurer.class),
					(SpringApplicationTerminatorConfigurer) null);

			Runnable springApplicationTerminatorRunnable = () -> System.exit(-1);

			ThreadFactory springApplicationTerminatorThreadFactory = runnable -> {

				Thread springApplicationTerminatorThread =
					new Thread(runnable, "Spring Application Terminator Thread");

				springApplicationTerminatorThread.setDaemon(true);
				springApplicationTerminatorThread.setPriority(Thread.NORM_PRIORITY);

				return springApplicationTerminatorThread;
			};

			Optional.ofNullable(event)
				.map(ContextRefreshedEvent::getApplicationContext)
				.map(safeSpringApplicationTerminatorConfigurerResolver)
				.filter(SpringApplicationTerminatorConfigurer::isNotEmpty)
				.filter(this::delayIsGreaterThanZero)
				.ifPresent(configurer ->
					Executors.newScheduledThreadPool(1, springApplicationTerminatorThreadFactory)
						.schedule(springApplicationTerminatorRunnable, configurer.delay().toMillis(),
							TimeUnit.MILLISECONDS)
				);
		}

		private boolean delayIsGreaterThanZero(@NonNull SpringApplicationTerminatorConfigurer configurer) {

			return Optional.ofNullable(configurer)
				.map(SpringApplicationTerminatorConfigurer::delay)
				.map(delay -> Duration.ZERO.compareTo(delay) > 0)
				.orElse(false);
		}
	}

	public interface SpringApplicationTerminatorConfigurer {

		SpringApplicationTerminatorConfigurer EMPTY = new SpringApplicationTerminatorConfigurer() {
		};

		default boolean isEmpty() {
			return EMPTY.equals(this);
		}

		default boolean isNotEmpty() {
			return !isEmpty();
		}

		default Duration delay() {
			return Duration.ZERO;
		}
	}
}
