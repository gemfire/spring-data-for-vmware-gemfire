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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.gemfire.DriverConnectionFactoryBean;
import org.springframework.data.gemfire.cache.GemfireCacheManager;

import com.vmware.gemfire.driver.core.GemFireDriverManager;
import com.vmware.gemfire.driver.spi.CacheConfiguration;
import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.DriverRegion;
import com.vmware.gemfire.driver.spi.DriverRegionLifecycleListener;
import com.vmware.gemfire.driver.spi.config.ClientRegionShortcut;

/** Same explicit bean wiring is exercised on both the mock and native driver classpaths. */
class InjectedResolverIntegrationTests {

	@Test
	void javaConfigurationResolvesQualifiedBorrowedHandles() {
		try (var context = new AnnotationConfigApplicationContext(ResolverConfiguration.class)) {
			verifyContextAndOwnership(context, true);
		}
	}

	@Test
	void ordinaryBeanXmlResolvesQualifiedBorrowedHandles() {
		try (var context = new GenericApplicationContext()) {
			context.getBeanFactory().registerSingleton("resolverBeanFactory", context.getBeanFactory());
			new XmlBeanDefinitionReader(context).loadBeanDefinitions(
				"classpath:org/springframework/data/gemfire/support/resolver-context.xml");
			context.refresh();
			verifyContextAndOwnership(context, true);
		}
	}

	@Test
	void javaConfigurationBorrowsAnExternallyOwnedConnection() {
		try (DriverConnection external = new GemFireDriverManager().driver().connect(configuration());
			var context = new AnnotationConfigApplicationContext()) {
			context.registerBean("connection", DriverConnectionFactoryBean.class,
				() -> new DriverConnectionFactoryBean(external));
			context.register(ResolverConsumers.class);
			context.refresh();
			assertThat(context.getBean("connection")).isSameAs(external);
			verifyContextAndOwnership(context, false);
		}
	}

	@Test
	void ordinaryBeanXmlBorrowsAnExternallyOwnedConnection() {
		try (DriverConnection external = new GemFireDriverManager().driver().connect(configuration());
			var context = new GenericApplicationContext()) {
			context.getBeanFactory().registerSingleton("externalConnection", external);
			context.getBeanFactory().registerSingleton("resolverBeanFactory", context.getBeanFactory());
			new XmlBeanDefinitionReader(context).loadBeanDefinitions(
				"classpath:org/springframework/data/gemfire/support/borrowed-resolver-context.xml");
			context.refresh();
			assertThat(context.getBean("connection")).isSameAs(external);
			verifyContextAndOwnership(context, false);
		}
	}

	private CacheConfiguration configuration() {
		return new CacheConfiguration(Map.of("mcast-port", "0", "locators", "", "log-level", "error"));
	}

	private void verifyContextAndOwnership(GenericApplicationContext context, boolean owned) {
		DriverConnection connection = context.getBean("connection", DriverConnection.class);
		DriverRegion<?, ?> region = context.getBean("entries", DriverRegion.class);
		assertThat(context.getBean("connection")).isSameAs(connection);
		assertThat(context.getBean("&connection", DriverConnectionFactoryBean.class).getObject()).isSameAs(connection);
		GemfireCacheManager cacheManager = context.getBean(GemfireCacheManager.class);
		cacheManager.getCache("entries").put("key", "value");
		assertThat(connection.getRegion("entries").get("key")).isEqualTo("value");
		BeanFactoryCacheResolver cacheResolver = context.getBean(BeanFactoryCacheResolver.class);
		BeanFactoryRegionResolver beanResolver = context.getBean(BeanFactoryRegionResolver.class);
		RegionServiceRegionResolver<?> serviceResolver = context.getBean(RegionServiceRegionResolver.class);
		assertThat(cacheResolver.resolve()).isSameAs(connection);
		assertThat(SingleCacheCacheResolver.from(connection).get()).isSameAs(connection);
		assertThat(beanResolver.resolve("entries")).isSameAs(region);
		assertThat(beanResolver.resolve("absent")).isNull();
		assertThat(serviceResolver.resolve("absent")).isNull();
		assertThat(serviceResolver.require("entries").name()).isEqualTo("entries");
		SingleRegionRegionResolver single = new SingleRegionRegionResolver(region);
		assertThat(single.resolve("entries")).isSameAs(region);
		assertThat(single.resolve("/entries")).isNull();
		List<String> notifications = new ArrayList<>();
		region.addLifecycleListener(new DriverRegionLifecycleListener() {
			@Override public void afterRegionDestroy(DriverRegion<?, ?> destroyed) {
				notifications.add("destroy:" + destroyed.name());
			}
			@Override public void close() { notifications.add("close"); }
		});
		// Resolver listener cleanup has no ownership of the connection or cached handles.
		beanResolver.close();
		serviceResolver.close();
		assertThat(connection.isClosed()).isFalse();
		assertThat(beanResolver.resolve("entries")).isSameAs(region);
		context.close();
		assertThat(connection.isClosed()).isEqualTo(owned);
		if (owned) assertThat(notifications).containsExactly("destroy:entries", "close");
		else {
			assertThat(notifications).isEmpty();
			cacheManager.getCache("entries").put("afterClose", "still usable");
			assertThat(connection.getRegion("entries").get("afterClose")).isEqualTo("still usable");
		}
		assertThat(cacheResolver.resolve()).isSameAs(connection);
		assertThat(single.resolve("entries")).isSameAs(region);
		// Destruction removed the service resolver's name key: it now reaches the closed connection.
		if (owned) {
			assertThatThrownBy(() -> serviceResolver.resolve("entries")).isInstanceOf(DriverOperationException.class);
		}
		else assertThat(serviceResolver.require("entries").name()).isEqualTo("entries");
	}

	@Configuration(proxyBeanMethods = false)
	public static class ResolverConfiguration extends ResolverConsumers {
		@Bean
		public GemFireDriverManager driverManager() { return new GemFireDriverManager(); }
		@Bean
		public CacheConfiguration cacheConfiguration() {
			return new CacheConfiguration(Map.of("mcast-port", "0", "locators", "", "log-level", "error"));
		}
		@Bean
		public DriverConnectionFactoryBean connection(GemFireDriverManager manager, CacheConfiguration configuration) {
			return new DriverConnectionFactoryBean(manager, configuration);
		}
	}

	@Configuration(proxyBeanMethods = false)
	public static class ResolverConsumers {
		@Bean(destroyMethod = "")
		public DriverConnection otherConnection() { return new UnusedConnection(); }
		@Bean
		public DriverRegion<Object, Object> entries(@Qualifier("connection") DriverConnection connection) {
			return connection.createClientRegion("entries", ClientRegionShortcut.LOCAL);
		}
		@Bean
		public GemfireCacheManager cacheManager(@Qualifier("connection") DriverConnection connection) {
			return new GemfireCacheManager(connection);
		}
		@Bean
		public BeanFactoryCacheResolver cacheResolver(BeanFactory factory) {
			BeanFactoryCacheResolver resolver = new BeanFactoryCacheResolver(factory);
			resolver.setCacheBeanName("connection");
			return resolver;
		}
		@Bean
		public BeanFactoryRegionResolver beanResolver(BeanFactory factory) { return new BeanFactoryRegionResolver(factory); }
		@Bean
		public RegionServiceRegionResolver<DriverConnection> serviceResolver(
			@Qualifier("connection") DriverConnection connection) { return RegionServiceRegionResolver.from(connection); }
	}

	/** Deliberately unusable: qualified resolution must not use this second bean. */
	public static class UnusedConnection implements DriverConnection { }
}
