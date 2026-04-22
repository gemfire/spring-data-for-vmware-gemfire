/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.repository.cdi;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientRegionFactory;
import org.springframework.data.gemfire.gud.api.GudClientRegionShortcut;
import org.springframework.data.gemfire.gud.core.GudDriverManager;
import org.springframework.data.gemfire.repository.sample.Person;
import org.springframework.util.Assert;

/**
 * The GemfireCacheRegionProducer class is an application scoped CDI context bean that is responsible
 * for creating the GemFire Cache "People" GudRegion used to store {@link Person} instances.
 *
 * @author John Blum
 * @see jakarta.enterprise.context.ApplicationScoped
 * @see jakarta.enterprise.inject.Produces
 * @since 1.8.0
 */
@SuppressWarnings("unused")
public class GemfireCacheRegionProducer {

	@Produces
	@ApplicationScoped
	public GudRegion<Long, Person> createPeopleRegion() {

		GudClientCache gemfireCache = GudDriverManager.getDefaultDriver().createClientCacheFactory()
			.set("name", "SpringDataGemFireCdiTest")
			.set("log-level", "error")
			.create();

		GudClientRegionFactory<Long, Person> peopleRegionFactory = gemfireCache.createClientRegionFactory(GudClientRegionShortcut.LOCAL);

		peopleRegionFactory.setKeyConstraint(Long.class);
		peopleRegionFactory.setValueConstraint(Person.class);

		GudRegion<Long, Person> peopleRegion = peopleRegionFactory.create("People");

		Assert.notNull(peopleRegion,"PeopleRegion is null");

		return peopleRegion;
	}

}
