/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.gemfire.util.ArrayUtils.asArray;
import java.lang.reflect.Proxy;
import java.util.concurrent.Executor;
import lombok.Data;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudRegionAttributes;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudCqAttributes;
import org.springframework.data.gemfire.gud.api.GudCqEvent;
import org.springframework.data.gemfire.gud.api.GudCqQuery;
import org.springframework.data.gemfire.gud.api.GudQueryService;
import org.junit.After;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.RegionAttributesFactoryBean;
import org.springframework.data.gemfire.listener.ContinuousQueryListenerContainer;
import org.springframework.data.gemfire.listener.annotation.ContinuousQuery;
import org.springframework.data.gemfire.mapping.GemfireMappingContext;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.data.gemfire.repository.support.GemfireRepositoryFactoryBean;
import org.springframework.data.gemfire.test.model.Person;
import org.springframework.data.gemfire.test.repo.PersonRepository;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.GemFireMockObjectsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

/**
 * Unit Tests for {@link EnableContinuousQueries}, {@link ContinuousQueryConfiguration}, {@link ContinuousQuery}
 * and {@link ContinuousQueryListenerContainer}.
 *
 * @author John Blum
 * @see java.lang.reflect.Proxy
 * @see java.util.concurrent.Executor
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.GudClientCache
 * @see org.apache.geode.cache.GudRegion
 * @see org.apache.geode.cache.client.GudClientCache
 * @see org.apache.geode.cache.query.GudCqEvent
 * @see org.apache.geode.cache.query.GudCqQuery
 * @see org.apache.geode.cache.query.GudQueryService
 * @see org.springframework.aop.framework.ProxyFactory
 * @see org.springframework.data.gemfire.config.annotation.ContinuousQueryConfiguration
 * @see org.springframework.data.gemfire.config.annotation.EnableContinuousQueries
 * @see org.springframework.data.gemfire.listener.annotation.ContinuousQuery
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.GemFireMockObjectsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @since 2.0.1
 */
public class EnableContinuousQueriesConfigurationUnitTests extends SpringApplicationContextIntegrationTestsSupport {

	@After
	public void tearDown() {
		destroyAllGemFireMockObjects();
	}

	@Test
	public void continuousQueryListenerContainerConfigurationIsCorrect() {

		newApplicationContext(TestContinuousQueryListenerContainerConfiguration.class);

		ErrorHandler mockErrorHandler = getBean("mockErrorHandler", ErrorHandler.class);

		Executor mockTaskExecutor = getBean("mockTaskExecutor", Executor.class);

		GudPool mockPool = getBean("mockPool", GudPool.class);

		GudQueryService mockQueryService = getBean("mockQueryService", GudQueryService.class);

		assertThat(containsBean("continuousQueryListenerContainer")).isTrue();

		ContinuousQueryListenerContainer container =
			getBean("continuousQueryListenerContainer", ContinuousQueryListenerContainer.class);

		assertThat(container).isNotNull();
		assertThat(container.getErrorHandler().orElse(null)).isEqualTo(mockErrorHandler);
		assertThat(container.getPhase()).isEqualTo(1);
		assertThat(container.getPoolName()).isEqualTo(mockPool.getName());
		assertThat(container.getQueryService()).isEqualTo(mockQueryService);
		assertThat(container.getTaskExecutor()).isEqualTo(mockTaskExecutor);
	}

	private void testRegisterAndExecuteContinuousQuery(Class<?>... annotatedClasses) throws Exception {

		ConfigurableApplicationContext applicationContext = newApplicationContext(annotatedClasses);

		try {

			assertThat(applicationContext).isNotNull();
			assertThat(applicationContext.containsBean("DEFAULT")).isTrue();

			GudClientCache gemfireCache = applicationContext.getBean(GudClientCache.class);

			assertThat(gemfireCache).isNotNull();

			GudQueryService mockQueryService = gemfireCache.getQueryService();

			assertThat(mockQueryService).isNotNull();
			assertThat(mockQueryService.getCqs()).hasSize(1);

			GudCqQuery mockCqQuery = mockQueryService.getCqs()[0];

			assertThat(mockCqQuery).isNotNull();
			assertThat(mockCqQuery.getName()).isEqualTo("TestQuery");
			assertThat(mockCqQuery.getQueryString()).isEqualTo("SELECT * FROM /Example");
			assertThat(mockCqQuery.isRunning()).isTrue();

			verify(mockQueryService, times(1)).newCq(eq("TestQuery"),
				eq("SELECT * FROM /Example"), any(GudCqAttributes.class), eq(false));

			verify(mockCqQuery, times(1)).execute();
		}
		finally {
			closeApplicationContext(applicationContext);
			destroyAllGemFireMockObjects();
		}
	}

	@Test
	public void registersAndExecutesContinuousQueries() throws Exception {
		testRegisterAndExecuteContinuousQuery(TestContinuousQueryRegistrationAndExecutionConfiguration.class);
	}

	@Test
	public void registersAndExecutesContinuousQueriesOnProxiedBean() throws Exception {
		testRegisterAndExecuteContinuousQuery(TestContinuousQueryRegistrationAndExecutionOnProxiedBeanConfiguration.class);
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@EnableContinuousQueries(errorHandlerBeanName = "mockErrorHandler", phase = 1, poolName = "mockPool",
		queryServiceBeanName = "mockQueryService", taskExecutorBeanName = "mockTaskExecutor")
	@SuppressWarnings("unused")
	static class TestContinuousQueryListenerContainerConfiguration {

		@Bean
		ErrorHandler mockErrorHandler() {
			return mock(ErrorHandler.class);
		}

		@Bean
		GudPool mockPool() {

			GudPool mockPool = mock(GudPool.class);

			when(mockPool.getName()).thenReturn("mockPool");

			return mockPool;
		}

		@Bean
		GudQueryService mockQueryService() {
			return mock(GudQueryService.class);
		}

		@Bean
		Executor mockTaskExecutor() {
			return mock(Executor.class);
		}
	}

	@ClientCacheApplication
	@EnableContinuousQueries
	@EnableGemFireMockObjects
	@SuppressWarnings("unused")
	static class TestContinuousQueryRegistrationAndExecutionConfiguration {

		@Bean
		BeanFactoryPostProcessor dependsOnBeanFactoryPostProcessor() {

			return configurableListableBeanFactory ->
				configurableListableBeanFactory.getBeanDefinition("continuousQueryListenerContainer")
					.setDependsOn("continuousQueryComponent");
		}

		@Bean("DEFAULT")
		GudPool mockPool() {
			return mock(GudPool.class);
		}

		@Bean
		@Order(Ordered.HIGHEST_PRECEDENCE)
		TestContinuousQueryComponent continuousQueryComponent() {
			return new TestContinuousQueryComponent();
		}
	}

	@Configuration
	@EnableGemfireRepositories(basePackageClasses = PersonRepository.class)
	@Import(TestContinuousQueryRegistrationAndExecutionConfiguration.class)
	@SuppressWarnings("unused")
	static class TestContinuousQueryRegistrationAndExecutionOnProxiedBeanConfiguration {

		@Bean
		BeanPostProcessor proxyTestContinuousQueryComponentPostProcessor() {

			return new BeanPostProcessor() {

				@Override
				public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

					if ("continuousQueryComponent".equals(beanName)) {
						return new ProxyFactory(bean).getProxy(bean.getClass().getClassLoader());
					}

					return bean;
				}
			};
		}

		@Bean("People")
		GudRegion<Long, Person> mockPeopleRegion(GudClientCache gemfireCache,
				@Qualifier("peopleRegionAttributes") GudRegionAttributes<Long, Person> peopleRegionAttributes) {

			return GemFireMockObjectsSupport.mockRegion(gemfireCache, "People", peopleRegionAttributes);
		}

		@Bean
		@SuppressWarnings({ "unchecked", "rawtypes" })
		RegionAttributesFactoryBean peopleRegionAttributes() {

			RegionAttributesFactoryBean peopleRegionAttributes = new RegionAttributesFactoryBean();

			peopleRegionAttributes.setKeyConstraint(Long.class);
			peopleRegionAttributes.setValueConstraint(Person.class);

			return peopleRegionAttributes;
		}

		@Bean("Examples")
		GudRegion<Long, Example> mockExamplesRegion(GudClientCache gemfireCache,
				@Qualifier("examplesRegionAttributes") GudRegionAttributes<Long, Example> mockExamplesRegionAttributes) {

			return GemFireMockObjectsSupport.mockRegion(gemfireCache, "Examples",
				mockExamplesRegionAttributes);
		}

		@Bean
		@SuppressWarnings({ "unchecked", "rawtypes" })
		RegionAttributesFactoryBean examplesRegionAttributes() {

			RegionAttributesFactoryBean examplesRegionAttributes = new RegionAttributesFactoryBean();

			examplesRegionAttributes.setKeyConstraint(Long.class);
			examplesRegionAttributes.setValueConstraint(Example.class);

			return examplesRegionAttributes;
		}

		@Bean
		@SuppressWarnings("all")
		ExampleDataAccessObject exampleDao() {

			return (ExampleDataAccessObject) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
				asArray(ExampleDataAccessObject.class), (proxy, method, args) -> null);
		}

		@Bean
		@SuppressWarnings("rawtypes")
		GemfireRepositoryFactoryBean exampleRepository() {

			GemfireRepositoryFactoryBean<ExampleRepository, Example, Long> exampleRepositoryFactory =
				new GemfireRepositoryFactoryBean<>(ExampleRepository.class);

			exampleRepositoryFactory.setGemfireMappingContext(new GemfireMappingContext());
			exampleRepositoryFactory.setLazyInit(false);

			return exampleRepositoryFactory;
		}

		@Bean
		Object exampleService(ExampleRepository exampleRepository, ExampleDataAccessObject exampleDao) {
			assertThat(exampleRepository).isNotNull();
			assertThat(exampleDao).isNotNull();
			return null;
		}

		@Bean
		Object personService(PersonRepository personRepository) {
			assertThat(personRepository).isNotNull();
			return null;
		}
	}

	@Component
	@SuppressWarnings("unused")
	static class TestContinuousQueryComponent {

		@ContinuousQuery(name = "TestQuery", query = "SELECT * FROM /Example")
		public void handle(GudCqEvent event) { }

	}

	@Data
	@org.springframework.data.gemfire.mapping.annotation.GudRegion("Examples")
	static class Example {
		@Id Long id;
	}

	interface ExampleDataAccessObject { }

	interface ExampleRepository extends CrudRepository<Example, Long> { }

}
