/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.support.GenericApplicationContext;

import com.vmware.gemfire.driver.core.GemFireDriverManager;
import com.vmware.gemfire.driver.spi.CacheConfiguration;
import com.vmware.gemfire.driver.spi.DriverConfiguration;
import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverMetadata;
import com.vmware.gemfire.driver.spi.GemFireDriver;

class DriverConnectionFactoryBeanTests {

	private final CacheConfiguration configuration = new CacheConfiguration(CacheConfiguration.Mode.CLIENT, Map.of());
	private final RecordingConnection connection = new RecordingConnection();
	private final RecordingDriver driver = new RecordingDriver();

	private DriverConnectionFactoryBean owner() {
		GemFireDriverManager manager = mock(GemFireDriverManager.class);
		when(manager.driver()).thenReturn(driver);
		return new DriverConnectionFactoryBean(manager, configuration);
	}

	@Test
	void validatesConstructorArgumentsWithoutConnecting() {
		assertThatThrownBy(() -> new DriverConnectionFactoryBean((DriverConnection) null))
			.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> new DriverConnectionFactoryBean(null, configuration))
			.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> new DriverConnectionFactoryBean(mock(GemFireDriverManager.class), null))
			.isInstanceOf(IllegalArgumentException.class);
		owner();
		assertThat(driver.opens).isZero();
	}

	@Test
	void exposesOneProductAndPassesTheExactConfiguration() {
		var factory = owner();
		assertThat(factory.getObjectType()).isEqualTo(DriverConnection.class);
		assertThat(factory.isSingleton()).isTrue();
		assertThatThrownBy(factory::getObject).isInstanceOf(IllegalStateException.class).hasMessageContaining("NEW");
		factory.afterPropertiesSet();
		factory.afterPropertiesSet();
		assertThat(factory.getObject()).isSameAs(connection);
		assertThat(factory.getObject()).isSameAs(connection);
		assertThat(driver.received).isSameAs(configuration);
		assertThat(driver.opens).isEqualTo(1);
		factory.destroy();
		factory.destroy();
		assertThat(connection.closes).isEqualTo(1);
		assertThatThrownBy(factory::getObject).hasMessageContaining("DESTROYED");
		assertThatThrownBy(factory::afterPropertiesSet).hasMessageContaining("DESTROYED");
		assertThat(driver.opens).isEqualTo(1);
	}

	@Test
	void unopenedDestructionDoesNotConnect() {
		var factory = owner();
		factory.destroy();
		factory.destroy();
		assertThatThrownBy(factory::afterPropertiesSet).hasMessageContaining("DESTROYED");
		assertThat(driver.opens).isZero();
		assertThat(connection.closes).isZero();
	}

	@Test
	void borrowingDoesNotEvenInspectTheHandle() {
		connection.inspectFailure = new IllegalStateException("must not inspect borrowed connection");
		var factory = new DriverConnectionFactoryBean(connection);
		assertThatThrownBy(factory::getObject).hasMessageContaining("NEW");
		factory.afterPropertiesSet();
		factory.afterPropertiesSet();
		assertThat(factory.getObject()).isSameAs(connection);
		factory.destroy();
		factory.destroy();
		assertThat(connection.closes).isZero();
		assertThatThrownBy(factory::getObject).hasMessageContaining("DESTROYED");
	}

	@Test
	void creationFailureIsOriginalAndTerminal() {
		var failure = new IllegalArgumentException("connection rejected");
		driver.failure = failure;
		var factory = owner();
		assertThatThrownBy(factory::afterPropertiesSet).isSameAs(failure);
		assertThatThrownBy(factory::afterPropertiesSet).hasCause(failure).hasMessageContaining("FAILED");
		assertThatThrownBy(factory::getObject).hasCause(failure);
		factory.destroy();
		assertThat(driver.opens).isEqualTo(1);
		assertThat(connection.closes).isZero();
	}

	@Test
	void nullProductFailsWithoutRetry() {
		driver.result = null;
		var factory = owner();
		assertThatThrownBy(factory::afterPropertiesSet).hasMessage("Driver returned no connection");
		assertThatThrownBy(factory::afterPropertiesSet).hasMessageContaining("FAILED");
		factory.destroy();
		assertThat(driver.opens).isEqualTo(1);
	}

	@Test
	void closedOwnedProductIsRejectedAndCleanedUpOnce() {
		connection.closed = true;
		var factory = owner();
		assertThatThrownBy(factory::afterPropertiesSet).hasMessage("Driver returned a closed connection");
		factory.destroy();
		assertThat(connection.closes).isEqualTo(1);
	}

	@Test
	void initializationCleanupPreservesOriginalFailureAndSuppressesCloseFailure() {
		var failure = new IllegalStateException("inspection failed");
		var cleanup = new IllegalStateException("close failed");
		connection.inspectFailure = failure;
		connection.closeFailure = cleanup;
		var factory = owner();
		assertThatThrownBy(factory::afterPropertiesSet).isSameAs(failure).hasSuppressedException(cleanup);
		assertThatThrownBy(factory::getObject).hasCause(failure);
		factory.destroy();
		assertThat(connection.closes).isEqualTo(1);
	}

	@Test
	void errorDuringInitializationAlsoCleansUpWithoutReplacingFailure() {
		var failure = new AssertionError("inspection error");
		connection.inspectError = failure;
		var factory = owner();
		assertThatThrownBy(factory::afterPropertiesSet).isSameAs(failure);
		factory.destroy();
		assertThat(connection.closes).isEqualTo(1);
	}

	@Test
	void cleanupDoesNotSuppressFailureOnItself() {
		var failure = new IllegalStateException("shared failure");
		connection.inspectFailure = failure;
		connection.closeFailure = failure;
		var factory = owner();
		assertThatThrownBy(factory::afterPropertiesSet).isSameAs(failure);
		assertThat(failure.getSuppressed()).isEmpty();
	}

	@Test
	void closeFailureIsSurfacedWithoutRetryOrReconnection() {
		var failure = new IllegalStateException("close failed");
		connection.closeFailure = failure;
		var factory = owner();
		factory.afterPropertiesSet();
		assertThatThrownBy(factory::destroy).isSameAs(failure);
		factory.destroy();
		assertThatThrownBy(factory::getObject).hasMessageContaining("DESTROYED");
		assertThatThrownBy(factory::afterPropertiesSet).hasMessageContaining("DESTROYED");
		assertThat(connection.closes).isEqualTo(1);
		assertThat(driver.opens).isEqualTo(1);
	}

	@Test
	void springDestroysFactoryOnceAndDoesNotDestroyItsAutoCloseableProduct() {
		for (boolean owned : new boolean[] { true, false }) {
			connection.closes = 0;
			connection.closed = false;
			try (var context = new GenericApplicationContext()) {
				context.registerBean("connection", DriverConnectionFactoryBean.class,
					() -> owned ? owner() : new DriverConnectionFactoryBean(connection));
				context.refresh();
				assertThat(context.getBean("connection")).isSameAs(connection);
				context.close();
				assertThat(connection.closes).isEqualTo(owned ? 1 : 0);
			}
		}
	}

	@Test
	void springCleansUpRegisteredOwnerWhenProductPostProcessingFails() {
		var failure = new IllegalStateException("consumer initialization failed");
		try (var context = new GenericApplicationContext()) {
			context.getBeanFactory().addBeanPostProcessor(new BeanPostProcessor() {
				@Override public Object postProcessAfterInitialization(Object bean, String name) {
					if (bean == connection) throw failure;
					return bean;
				}
			});
			context.registerBean("connection", DriverConnectionFactoryBean.class, this::owner);
			context.registerBean("consumer", Object.class, () -> context.getBean("connection"));
			assertThatThrownBy(context::refresh).hasRootCause(failure);
			assertThat(connection.closes).isEqualTo(1);
		}
		assertThat(connection.closes).isEqualTo(1);
	}

	private class RecordingDriver implements GemFireDriver {
		private int opens;
		private DriverConfiguration received;
		private DriverConnection result = connection;
		private RuntimeException failure;
		@Override public DriverMetadata metadata() { return () -> "recording"; }
		@Override public String runtimeGemFireVersion() { return "recording"; }
		@Override public DriverConnection connect(DriverConfiguration configuration) {
			opens++;
			received = configuration;
			if (failure != null) throw failure;
			return result;
		}
	}

	private static class RecordingConnection implements DriverConnection {
		private int closes;
		private boolean closed;
		private RuntimeException inspectFailure;
		private Error inspectError;
		private RuntimeException closeFailure;
		@Override public boolean isClosed() {
			if (inspectFailure != null) throw inspectFailure;
			if (inspectError != null) throw inspectError;
			return closed;
		}
		@Override public void close() {
			closes++;
			if (closeFailure != null) throw closeFailure;
			closed = true;
		}
	}
}
