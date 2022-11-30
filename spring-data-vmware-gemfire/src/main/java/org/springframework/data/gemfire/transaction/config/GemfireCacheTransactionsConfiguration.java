// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.transaction.config;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.TransactionListener;
import org.apache.geode.cache.TransactionWriter;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.CacheFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer;
import org.springframework.data.gemfire.config.annotation.PeerCacheConfigurer;
import org.springframework.data.gemfire.config.annotation.support.AbstractAnnotationConfigSupport;
import org.springframework.data.gemfire.transaction.GemfireTransactionManager;
import org.springframework.data.gemfire.transaction.event.ComposableTransactionWriter;
import org.springframework.data.gemfire.transaction.event.TransactionListenerAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * The {@link GemfireCacheTransactionsConfiguration} class is a Spring {@link Configuration @Configuration} class
 * used to enable Spring's Transaction Management infrastructure along with SDG's {@link GemfireTransactionManager}
 * to manage local, cache transactions for either Pivotal GemFire or Apache Geode.
 *
 * @author John Blum
 * @see GemFireCache
 * @see TransactionListener
 * @see TransactionWriter
 * @see ApplicationEventPublisher
 * @see Bean
 * @see Configuration
 * @see ImportAware
 * @see AnnotationAttributes
 * @see org.springframework.core.type.AnnotatedTypeMetadata
 * @see CacheFactoryBean
 * @see ClientCacheConfigurer
 * @see PeerCacheConfigurer
 * @see AbstractAnnotationConfigSupport
 * @see GemfireTransactionManager
 * @see ComposableTransactionWriter
 * @see TransactionListenerAdapter
 * @see EnableTransactionManagement
 * @since 2.0.0
 */
@Configuration
@EnableTransactionManagement
@SuppressWarnings("unused")
public class GemfireCacheTransactionsConfiguration extends AbstractAnnotationConfigSupport implements ImportAware {

	private volatile boolean enableAutoTransactionEventPublishing;

	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return EnableGemfireCacheTransactions.class;
	}

	@Override
	public void setImportMetadata(AnnotationMetadata annotationMetadata) {

		if (isAnnotationPresent(annotationMetadata)) {

			AnnotationAttributes enableGemfireCacheTransactionsAttributes = getAnnotationAttributes(annotationMetadata);

			this.enableAutoTransactionEventPublishing =
				enableGemfireCacheTransactionsAttributes.getBoolean("enableAutoTransactionEventPublishing");
		}
	}

	/**
	 * Declares and registers SDG's {@link GemfireTransactionManager} as the {@literal transactionManager}
	 * in Spring's Transaction Management infrastructure to manage local, GemFire/Geode cache transactions.
	 *
	 * @param gemfireCache reference to the {@link GemFireCache}.
	 * @return a new instance of {@link GemfireTransactionManager} initialized with the given {@link GemFireCache}.
	 * @see GemfireTransactionManager
	 * @see GemFireCache
	 */
	@Bean
	public GemfireTransactionManager transactionManager(GemFireCache gemfireCache) {
		return new GemfireTransactionManager(gemfireCache);
	}

	@Bean
	@Order(Ordered.LOWEST_PRECEDENCE)
	public ClientCacheConfigurer registerTransactionListenerAdapterClientCacheConfigurer(
		ApplicationEventPublisher applicationEventPublisher) {

		return (beanName, bean) -> {

			TransactionListenerAdapter transactionListener = newTransactionListenerAdapter(applicationEventPublisher);

			registerGemFireCacheTransactionEventHandlers(bean, transactionListener);
		};
	}

	@Bean
	@Order(Ordered.LOWEST_PRECEDENCE)
	public PeerCacheConfigurer registerTransactionListenerAdapterPeerCacheConfigurer(
		ApplicationEventPublisher applicationEventPublisher) {

		return (beanName, bean) -> {

			TransactionListenerAdapter transactionListener = newTransactionListenerAdapter(applicationEventPublisher);

			registerGemFireCacheTransactionEventHandlers(bean, transactionListener);
		};
	}

	private TransactionListenerAdapter newTransactionListenerAdapter(
			ApplicationEventPublisher applicationEventPublisher) {

		return new TransactionListenerAdapter(applicationEventPublisher);
	}

	protected void registerGemFireCacheTransactionEventHandlers(CacheFactoryBean cacheFactoryBean,
			TransactionListenerAdapter transactionListener) {

		if (this.enableAutoTransactionEventPublishing) {
			registerGemFireCacheTransactionListener(cacheFactoryBean, transactionListener);
			registerGemFireCacheTransactionWriter(cacheFactoryBean, transactionListener);
		}
	}

	private void registerGemFireCacheTransactionListener(CacheFactoryBean bean,
			TransactionListener transactionListener) {

		List<TransactionListener> transactionListeners = new ArrayList<>(bean.getTransactionListeners());

		transactionListeners.add(transactionListener);

		bean.setTransactionListeners(transactionListeners);
	}

	private void registerGemFireCacheTransactionWriter(CacheFactoryBean bean,
			TransactionWriter transactionWriter) {

		TransactionWriter existingTransactionWriter = bean.getTransactionWriter();

		TransactionWriter compositeTransactionWriter =
			ComposableTransactionWriter.compose(existingTransactionWriter, transactionWriter);

		bean.setTransactionWriter(compositeTransactionWriter);
	}

}
