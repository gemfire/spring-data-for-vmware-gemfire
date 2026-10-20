/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.gemfire.DriverConnectionFactoryBean;
import org.springframework.data.gemfire.DriverPoolFactoryBean;
import com.vmware.gemfire.driver.core.GemFireDriverManager;
import com.vmware.gemfire.driver.spi.CacheConfiguration;
import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverPool;
import com.vmware.gemfire.driver.spi.PoolConfiguration;
import com.vmware.gemfire.driver.spi.PoolConfiguration.Endpoint;

/** Identical Java/XML lifecycle scenarios run with the mock and native providers. */
class InjectedPoolIntegrationTests {
	@Test
	void javaOwnersDestroyPoolBeforeConnection() {
		try (var context = new AnnotationConfigApplicationContext()) {
			ShutdownObserver observer = observe(context);
			context.register(Owners.class);
			context.refresh();
			verifyOwner(context, observer);
		}
	}

	@Test
	void xmlOwnersDestroyPoolBeforeConnection() {
		try (var context = new GenericApplicationContext()) {
			ShutdownObserver observer = observe(context);
			new XmlBeanDefinitionReader(context).loadBeanDefinitions(
				"classpath:org/springframework/data/gemfire/support/pool-context.xml");
			context.refresh();
			verifyOwner(context, observer);
		}
	}

	@Test
	void javaBorrowersLeaveExternalPoolAndConnectionUsable() {
		try (DriverConnection external = new GemFireDriverManager().driver().connect(cacheConfiguration());
			var context = new AnnotationConfigApplicationContext()) {
			DriverPool pool = external.createPool(UUID.randomUUID().toString(), poolConfiguration());
			try {
				context.getBeanFactory().registerSingleton("externalConnection", external);
				context.getBeanFactory().registerSingleton("externalPool", pool);
				context.register(Borrowers.class);
				context.refresh();
				verifyBorrower(context, external, pool);
			}
			finally { pool.destroy(); }
		}
	}

	@Test
	void xmlBorrowersLeaveExternalPoolAndConnectionUsable() {
		try (DriverConnection external = new GemFireDriverManager().driver().connect(cacheConfiguration());
			var context = new GenericApplicationContext()) {
			DriverPool pool = external.createPool(UUID.randomUUID().toString(), poolConfiguration());
			try {
				context.getBeanFactory().registerSingleton("externalConnection", external);
				context.getBeanFactory().registerSingleton("externalPool", pool);
				new XmlBeanDefinitionReader(context).loadBeanDefinitions(
					"classpath:org/springframework/data/gemfire/support/borrowed-pool-context.xml");
				context.refresh();
				verifyBorrower(context, external, pool);
			}
			finally { pool.destroy(); }
		}
	}

	@Test
	void downstreamRefreshFailureCleansUpPoolBeforeConnection() {
		try (var context = new AnnotationConfigApplicationContext()) {
			ShutdownObserver observer = observe(context);
			context.register(Owners.class);
			var failure = new IllegalStateException("downstream failed");
			context.registerBean("failure", Object.class, () -> {
				context.getBean("pool", DriverPool.class);
				throw failure;
			});
			assertThatThrownBy(context::refresh).hasRootCause(failure);
			assertThat(observer.events).containsExactly("pool:connection-open=true", "connection:pool-destroyed=true");
			assertThat(observer.pool.isDestroyed()).isTrue();
			assertThat(observer.connection.isClosed()).isTrue();
		}
	}

	private ShutdownObserver observe(GenericApplicationContext context) {
		var observer = new ShutdownObserver();
		context.getBeanFactory().addBeanPostProcessor(observer);
		return observer;
	}

	private void verifyOwner(GenericApplicationContext context, ShutdownObserver observer) {
		DriverPool pool = context.getBean("pool", DriverPool.class);
		DriverConnection connection = context.getBean("connection", DriverConnection.class);
		assertThat(context.getType("pool")).isEqualTo(DriverPool.class);
		assertThat(context.getBean("pool")).isSameAs(pool);
		assertThat(context.getBean("&pool", DriverPoolFactoryBean.class).getObject()).isSameAs(pool);
		assertThat(connection.getPool(pool.name()).name()).isEqualTo(pool.name());
		assertThat(pool.minimumConnections()).isZero();
		context.close();
		assertThat(observer.events).containsExactly("pool:connection-open=true", "connection:pool-destroyed=true");
		assertThat(pool.isDestroyed()).isTrue();
		assertThat(connection.isClosed()).isTrue();
	}

	private void verifyBorrower(GenericApplicationContext context, DriverConnection external, DriverPool pool) {
		assertThat(context.getBean("connection")).isSameAs(external);
		assertThat(context.getBean("pool")).isSameAs(pool);
		assertThat(context.getBean("&pool", DriverPoolFactoryBean.class).getObject()).isSameAs(pool);
		context.close();
		assertThat(external.isClosed()).isFalse();
		assertThat(pool.isDestroyed()).isFalse();
		assertThat(external.getPool(pool.name()).minimumConnections()).isZero();
		pool.destroy();
		assertThat(external.getPool(pool.name())).isNull();
	}

	private CacheConfiguration cacheConfiguration() {
		return new CacheConfiguration(CacheConfiguration.Mode.CLIENT,
			Map.of("mcast-port", "0", "locators", "", "log-level", "error"));
	}
	private PoolConfiguration poolConfiguration() {
		return new PoolConfiguration(List.of(new Endpoint("127.0.0.1", 40404)), List.of(), 0);
	}

	@Configuration(proxyBeanMethods = false)
	public static class Owners {
		@Bean public DriverConnectionFactoryBean connection() {
			return new DriverConnectionFactoryBean(new GemFireDriverManager(),
				new CacheConfiguration(CacheConfiguration.Mode.CLIENT,
					Map.of("mcast-port", "0", "locators", "", "log-level", "error")));
		}
		@Bean(destroyMethod = "") public DriverConnection otherConnection() { return new UnusedConnection(); }
		@Bean public DriverPoolFactoryBean pool(@Qualifier("connection") DriverConnection connection) {
			return new DriverPoolFactoryBean(connection, UUID.randomUUID().toString(),
				new PoolConfiguration(List.of(new Endpoint("127.0.0.1", 40404)), List.of(), 0));
		}
	}

	@Configuration(proxyBeanMethods = false)
	public static class Borrowers {
		@Bean public DriverConnectionFactoryBean connection(@Qualifier("externalConnection") DriverConnection external) {
			return new DriverConnectionFactoryBean(external);
		}
		@Bean public DriverPoolFactoryBean pool(@Qualifier("externalPool") DriverPool external) {
			return new DriverPoolFactoryBean(external);
		}
	}

	public static class UnusedConnection implements DriverConnection { }

	private static class ShutdownObserver implements DestructionAwareBeanPostProcessor {
		private final List<String> events = new ArrayList<>();
		private DriverPool pool;
		private DriverConnection connection;
		@Override public Object postProcessAfterInitialization(Object bean, String name) {
			if (bean instanceof DriverConnectionFactoryBean factory && name.equals("connection")) connection = factory.getObject();
			if (bean instanceof DriverPoolFactoryBean factory && name.equals("pool")) pool = factory.getObject();
			return bean;
		}
		@Override public void postProcessBeforeDestruction(Object bean, String name) {
			if (name.equals("pool")) events.add("pool:connection-open=" + !connection.isClosed());
			if (name.equals("connection")) events.add("connection:pool-destroyed=" + pool.isDestroyed());
		}
		@Override public boolean requiresDestruction(Object bean) {
			return bean instanceof DriverConnectionFactoryBean || bean instanceof DriverPoolFactoryBean;
		}
	}
}
