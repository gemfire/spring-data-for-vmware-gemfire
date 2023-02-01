/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.vmware.gemfire.cache;

import org.apache.geode.cache.Cache;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.data.gemfire.config.xml.GemfireConstants;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class CacheProductNameIntegrationTest {
  protected Cache getCache(String configLocation) {

    String baseConfigLocation =
        File.separator.concat(getClass().getPackage().getName().replace('.', File.separatorChar));

    String resolvedConfigLocation = baseConfigLocation.concat(File.separator).concat(configLocation);

    ClassPathXmlApplicationContext applicationContext =
        new ClassPathXmlApplicationContext(resolvedConfigLocation);

    return applicationContext.getBean(GemfireConstants.DEFAULT_GEMFIRE_CACHE_NAME, Cache.class);
  }

  @Test
  public void testCacheCanRetrieveProductName() {
    System.setProperty("logback.log.level", "info");
    Cache cache = getCache(
        "cacheCanResolveProductNameIntegrationTests.xml");
    assertThat(GemfireUtils.apacheGeodeProductName()).isEqualTo("VMware GemFire");
  }
}
