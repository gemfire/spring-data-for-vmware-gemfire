/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.repository.cdi;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.springframework.data.gemfire.repository.sample.Person;
import org.springframework.util.Assert;

/**
 * The GemfireCacheRegionProducer class is an application scoped CDI context bean that is responsible
 * for creating the GemFire Cache "People" Region used to store {@link Person} instances.
 *
 * @author John Blum
 * @see jakarta.enterprise.context.ApplicationScoped
 * @see jakarta.enterprise.inject.Produces
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

		ClientCache gemfireCache = new ClientCacheFactory()
			.set("name", "SpringDataGemFireCdiTest")
			.set("log-level", "error")
			.create();

		ClientRegionFactory<Long, Person> peopleRegionFactory = gemfireCache.createClientRegionFactory(ClientRegionShortcut.LOCAL);

		peopleRegionFactory.setKeyConstraint(Long.class);
		peopleRegionFactory.setValueConstraint(Person.class);

		Region<Long, Person> peopleRegion = peopleRegionFactory.create("People");

		Assert.notNull(peopleRegion,"PeopleRegion is null");

		return peopleRegion;
	}

}
