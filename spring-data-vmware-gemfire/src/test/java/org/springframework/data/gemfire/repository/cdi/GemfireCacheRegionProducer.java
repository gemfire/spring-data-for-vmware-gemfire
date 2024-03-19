/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository.cdi;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionFactory;
import org.apache.geode.cache.RegionShortcut;

import org.springframework.data.gemfire.repository.sample.Person;
import org.springframework.util.Assert;

/**
 * The {@link GemfireCacheRegionProducer} class is an application scoped CDI context bean that is responsible
 * for creating the {@link GemFireCache} {@literal People} {@link Region} used to store {@link Person} instances.
 *
 * @author John Blum
 * @see jakarta.enterprise.context.ApplicationScoped
 * @see jakarta.enterprise.inject.Produces
 * @see Cache
 * @see CacheFactory
 * @see GemFireCache
 * @see Region
 * @see RegionFactory
 * @since 1.8.0
 */
@SuppressWarnings("unused")
public class GemfireCacheRegionProducer {

	@Produces
	@ApplicationScoped
	public Region<Long, Person> createPeopleRegion() {

		Cache gemfireCache = new CacheFactory()
			.set("name", "SpringDataGemFireCdiTest")
			.set("log-level", "error")
			.create();

		RegionFactory<Long, Person> peopleRegionFactory = gemfireCache.createRegionFactory(RegionShortcut.REPLICATE);

		peopleRegionFactory.setKeyConstraint(Long.class);
		peopleRegionFactory.setValueConstraint(Person.class);

		Region<Long, Person> peopleRegion = peopleRegionFactory.create("People");

		Assert.notNull(peopleRegion);

		return peopleRegion;
	}

}
