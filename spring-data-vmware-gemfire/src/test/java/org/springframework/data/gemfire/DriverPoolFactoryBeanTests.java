/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.support.GenericApplicationContext;

import com.vmware.gemfire.driver.spi.PoolConfiguration;
import com.vmware.gemfire.driver.spi.DriverPool;
import com.vmware.gemfire.driver.spi.DriverConnection;

class DriverPoolFactoryBeanTests {

	private final PoolConfiguration configuration = new PoolConfiguration(
		List.of(new PoolConfiguration.Endpoint("host", 40404)), List.of(), 0);
	private final RecordingPool pool = new RecordingPool();
	private final RecordingConnection connection = new RecordingConnection();

	private DriverPoolFactoryBean owner() {
		return new DriverPoolFactoryBean(connection, "pool", configuration);
	}

	@Test
	void validatesConstructorArgumentsWithoutCreating() {
		assertThatThrownBy(() -> new DriverPoolFactoryBean((DriverPool) null))
			.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> new DriverPoolFactoryBean(null, "pool", configuration))
			.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> new DriverPoolFactoryBean(connection, "pool", null))
			.isInstanceOf(IllegalArgumentException.class);
		for (String name : new String[] { null, "", " " }) {
			assertThatThrownBy(() -> new DriverPoolFactoryBean(connection, name, configuration))
				.isInstanceOf(IllegalArgumentException.class);
		}
		owner();
		assertThat(connection.creates).isZero();
	}

	@Test
	void exposesOneProductAndPassesTheExactConfiguration() {
		var factory = owner();
		assertThat(factory.getObjectType()).isEqualTo(DriverPool.class);
		assertThat(factory.isSingleton()).isTrue();
		assertThatThrownBy(factory::getObject).isInstanceOf(IllegalStateException.class).hasMessageContaining("NEW");
		factory.afterPropertiesSet();
		factory.afterPropertiesSet();
		assertThat(factory.getObject()).isSameAs(pool);
		assertThat(factory.getObject()).isSameAs(pool);
		assertThat(connection.received).isSameAs(configuration);
		assertThat(connection.creates).isEqualTo(1);
		factory.destroy();
		factory.destroy();
		assertThat(pool.destroys).isEqualTo(1);
		assertThatThrownBy(factory::getObject).hasMessageContaining("DESTROYED");
		assertThatThrownBy(factory::afterPropertiesSet).hasMessageContaining("DESTROYED");
		assertThat(connection.creates).isEqualTo(1);
	}

	@Test
	void unopenedDestructionDoesNotCreate() {
		var factory = owner();
		factory.destroy();
		factory.destroy();
		assertThatThrownBy(factory::afterPropertiesSet).hasMessageContaining("DESTROYED");
		assertThat(connection.creates).isZero();
		assertThat(pool.destroys).isZero();
	}

	@Test
	void borrowingDoesNotEvenInspectTheHandle() {
		pool.inspectFailure = new IllegalStateException("must not inspect borrowed pool");
		var factory = new DriverPoolFactoryBean(pool);
		assertThatThrownBy(factory::getObject).hasMessageContaining("NEW");
		factory.afterPropertiesSet();
		factory.afterPropertiesSet();
		assertThat(factory.getObject()).isSameAs(pool);
		factory.destroy();
		factory.destroy();
		assertThat(pool.destroys).isZero();
		assertThatThrownBy(factory::getObject).hasMessageContaining("DESTROYED");
	}

	@Test
	void creationFailureIsOriginalAndTerminal() {
		var failure = new IllegalArgumentException("pool creation rejected");
		connection.failure = failure;
		var factory = owner();
		assertThatThrownBy(factory::afterPropertiesSet).isSameAs(failure);
		assertThatThrownBy(factory::afterPropertiesSet).hasCause(failure).hasMessageContaining("FAILED");
		assertThatThrownBy(factory::getObject).hasCause(failure);
		factory.destroy();
		assertThat(connection.creates).isEqualTo(1);
		assertThat(pool.destroys).isZero();
	}

	@Test
	void nullProductFailsWithoutRetry() {
		connection.result = null;
		var factory = owner();
		assertThatThrownBy(factory::afterPropertiesSet).hasMessage("Driver returned no pool");
		assertThatThrownBy(factory::afterPropertiesSet).hasMessageContaining("FAILED");
		factory.destroy();
		assertThat(connection.creates).isEqualTo(1);
	}

	@Test
	void destroyedOwnedProductIsRejectedAndCleanedUpOnce() {
		pool.destroyed = true;
		var factory = owner();
		assertThatThrownBy(factory::afterPropertiesSet).hasMessage("Driver returned a destroyed pool");
		factory.destroy();
		assertThat(pool.destroys).isEqualTo(1);
	}

	@Test
	void initializationCleanupPreservesOriginalFailureAndSuppressesDestroyFailure() {
		var failure = new IllegalStateException("inspection failed");
		var cleanup = new IllegalStateException("destroy failed");
		pool.inspectFailure = failure;
		pool.destroyFailure = cleanup;
		var factory = owner();
		assertThatThrownBy(factory::afterPropertiesSet).isSameAs(failure).hasSuppressedException(cleanup);
		assertThatThrownBy(factory::getObject).hasCause(failure);
		factory.destroy();
		assertThat(pool.destroys).isEqualTo(1);
	}

	@Test
	void errorDuringInitializationAlsoCleansUpWithoutReplacingFailure() {
		var failure = new AssertionError("inspection error");
		pool.inspectError = failure;
		var factory = owner();
		assertThatThrownBy(factory::afterPropertiesSet).isSameAs(failure);
		factory.destroy();
		assertThat(pool.destroys).isEqualTo(1);
	}

	@Test
	void cleanupDoesNotSuppressFailureOnItself() {
		var failure = new IllegalStateException("shared failure");
		pool.inspectFailure = failure;
		pool.destroyFailure = failure;
		var factory = owner();
		assertThatThrownBy(factory::afterPropertiesSet).isSameAs(failure);
		assertThat(failure.getSuppressed()).isEmpty();
	}

	@Test
	void destroyFailureIsSurfacedWithoutRetryOrRecreation() {
		var failure = new IllegalStateException("destroy failed");
		pool.destroyFailure = failure;
		var factory = owner();
		factory.afterPropertiesSet();
		assertThatThrownBy(factory::destroy).isSameAs(failure);
		factory.destroy();
		assertThatThrownBy(factory::getObject).hasMessageContaining("DESTROYED");
		assertThatThrownBy(factory::afterPropertiesSet).hasMessageContaining("DESTROYED");
		assertThat(pool.destroys).isEqualTo(1);
		assertThat(connection.creates).isEqualTo(1);
	}

	@Test
	void springDestroysFactoryOnceAndDoesNotDestroyItsProduct() {
		for (boolean owned : new boolean[] { true, false }) {
			pool.destroys = 0;
			pool.destroyed = false;
			try (var context = new GenericApplicationContext()) {
				context.registerBean("pool", DriverPoolFactoryBean.class,
					() -> owned ? owner() : new DriverPoolFactoryBean(pool));
				context.refresh();
				assertThat(context.getBean("pool")).isSameAs(pool);
				context.close();
				assertThat(pool.destroys).isEqualTo(owned ? 1 : 0);
			}
		}
	}

	@Test
	void springCleansUpRegisteredOwnerWhenProductPostProcessingFails() {
		var failure = new IllegalStateException("consumer initialization failed");
		try (var context = new GenericApplicationContext()) {
			context.getBeanFactory().addBeanPostProcessor(new BeanPostProcessor() {
				@Override public Object postProcessAfterInitialization(Object bean, String name) {
					if (bean == pool) throw failure;
					return bean;
				}
			});
			context.registerBean("pool", DriverPoolFactoryBean.class, this::owner);
			context.registerBean("consumer", Object.class, () -> context.getBean("pool"));
			assertThatThrownBy(context::refresh).hasRootCause(failure);
			assertThat(pool.destroys).isEqualTo(1);
		}
		assertThat(pool.destroys).isEqualTo(1);
	}

	private class RecordingConnection implements DriverConnection {
		private int creates;
		private PoolConfiguration received;
		private DriverPool result = pool;
		private RuntimeException failure;
		@Override public DriverPool createPool(String name, PoolConfiguration configuration) {
			creates++;
			assertThat(name).isEqualTo("pool");
			received = configuration;
			if (failure != null) throw failure;
			return result;
		}
		@Override public void close() { throw new AssertionError("Pool owner must not close connection"); }
		@Override public DriverPool getPool(String name) { throw new AssertionError("Must never find and adopt"); }
	}

	private static class RecordingPool implements DriverPool {
		private int destroys;
		private boolean destroyed;
		private RuntimeException inspectFailure;
		private Error inspectError;
		private RuntimeException destroyFailure;
		@Override public boolean isDestroyed() {
			if (inspectFailure != null) throw inspectFailure;
			if (inspectError != null) throw inspectError;
			return destroyed;
		}
		@Override public void destroy() {
			destroys++;
			if (destroyFailure != null) throw destroyFailure;
			destroyed = true;
		}
	}
}
