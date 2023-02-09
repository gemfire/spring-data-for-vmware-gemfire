/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.repository.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionFactory;
import org.apache.geode.cache.RegionShortcut;

import org.springframework.data.gemfire.repository.sample.Person;
import org.springframework.util.Assert;

/**
 * The GemfireCacheRegionProducer class is an application scoped CDI context bean that is responsible
 * for creating the GemFire Cache "People" Region used to store {@link Person} instances.
 *
 * @author John Blum
 * @see javax.enterprise.context.ApplicationScoped
 * @see javax.enterprise.inject.Produces
 * @see org.apache.geode.cache.Cache
 * @see org.apache.geode.cache.CacheFactory
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.RegionFactory
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
