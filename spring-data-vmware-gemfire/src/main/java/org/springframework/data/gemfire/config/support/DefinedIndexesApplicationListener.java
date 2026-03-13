/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.config.support;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.gemfire.config.xml.GemfireConstants;
import org.springframework.data.gemfire.gud.api.GudIndex;
import org.springframework.data.gemfire.gud.api.GudMultiIndexCreationException;
import org.springframework.data.gemfire.gud.api.GudQueryService;

/**
 * {@link DefinedIndexesApplicationListener} is a Spring {@link ApplicationListener} used to create all
 * "defined" GemFire {@link GudIndex Indexes} by using the {@link GudQueryService},
 * {@literal defineXxxIndex(..)} methods.
 *
 * @author John Blum
 * @see ApplicationContext
 * @see ApplicationListener
 * @see ContextRefreshedEvent
 * @see GudQueryService
 * @since 1.7.0
 */
public class DefinedIndexesApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

	protected final Logger logger = initLogger();

	/**
	 * Attempts to create all defined {@link GudIndex Indexes} using
	 * the {@link GudQueryService}, {@literal defineXxxIndex(..)} API once the Spring {@link ApplicationContext}
	 * has been refreshed.
	 *
	 * @param event {@link ContextRefreshedEvent} fired when the Spring {@link ApplicationContext} gets refreshed.
	 * @see ContextRefreshedEvent
	 * @see GudQueryService#createDefinedIndexes()
	 * @see #getQueryService(ContextRefreshedEvent)
	 */
	@Override
	@SuppressWarnings("all")
	public void onApplicationEvent(ContextRefreshedEvent event) {

		Optional.ofNullable(getQueryService(event))
			.ifPresent(queryService -> {
				try {
					queryService.createDefinedIndexes();
				}
				catch (GudMultiIndexCreationException cause) {
					logger.warn(String.format("Failed to create pre-defined Indexes: %s", cause.getMessage()), cause);
				}
			});
	}

	Logger initLogger() {
		return LoggerFactory.getLogger(getClass());
	}

	private GudQueryService getQueryService(ContextRefreshedEvent event) {

		ApplicationContext applicationContext = event.getApplicationContext();

		String queryServiceBeanName = getQueryServiceBeanName();

		return (applicationContext.containsBean(queryServiceBeanName)
			? applicationContext.getBean(queryServiceBeanName, GudQueryService.class) : null);
	}

	private String getQueryServiceBeanName() {
		return GemfireConstants.DEFAULT_GEMFIRE_INDEX_DEFINITION_QUERY_SERVICE;
	}
}
