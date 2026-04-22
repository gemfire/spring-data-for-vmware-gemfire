/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import jakarta.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientRegionShortcut;
import org.springframework.data.gemfire.gud.api.GudRegion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.data.gemfire.util.RegionUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration tests for an Apache Geode {@link ClientCache} application using mock objects.
 *
 * @author John Blum
 * @see Test
 * @see ClientCache
 * @see org.apache.geode.cahce.Region
 * @see ClientCache
 * @see org.springframework.data.gemfire.client.ClientCacheFactoryBean
 * @see ClientRegionFactoryBean
 * @see ClientCacheApplication
 * @see EnableGemFireMockObjects
 * @see ContextConfiguration
 * @see SpringRunner
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("all")
public class MockClientCacheApplicationIntegrationTests {

  @Autowired
  private GudClientCache clientCache;

  @Resource(name = "Example")
  private GudRegion<Object, Object> example;

  @Test
  public void clientCacheIsMocked() {

    assertThat(this.clientCache).isNotNull();
    assertThat(this.clientCache).isInstanceOf(GudClientCache.class);
    assertThat(this.clientCache.isClosed()).isFalse();

    Set<GudRegion<?, ?>> rootRegions = this.clientCache.rootRegions();

    assertThat(rootRegions).isNotNull();
    assertThat(rootRegions).hasSize(1);
    assertThat(rootRegions).containsExactly(this.example);
  }

  @Test
  public void exampleRegionIsMocked() {

    assertThat(this.example).isNotNull();
    assertThat(this.example.getFullPath()).isEqualTo(RegionUtils.toRegionPath("Example"));
    assertThat(this.example.getName()).isEqualTo("Example");
    assertThat(this.example.put(1, "test")).isNull();
    assertThat(this.example.get(1)).isEqualTo("test");
    assertThat(this.example.containsKey(1)).isTrue();

    this.example.invalidate(1);

    assertThat(this.example.containsKey(1)).isTrue();
    assertThat(this.example.get(1)).isNull();
    assertThat(this.example.remove(1)).isNull();
    assertThat(this.example.containsKey(1)).isFalse();
  }

  @ClientCacheApplication
  @EnableGemFireMockObjects
  static class TestConfiguration {

    @Bean("Example")
    public ClientRegionFactoryBean<Object, Object> exampleRegion(GudClientCache gemfireCache) {

      ClientRegionFactoryBean<Object, Object> exampleRegion = new ClientRegionFactoryBean<>();

      exampleRegion.setCache(gemfireCache);
      exampleRegion.setClose(false);
      exampleRegion.setShortcut(GudClientRegionShortcut.LOCAL);

      return exampleRegion;
    }
  }
}
