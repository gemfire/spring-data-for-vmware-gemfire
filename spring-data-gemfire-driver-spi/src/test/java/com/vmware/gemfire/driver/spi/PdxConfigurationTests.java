// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.vmware.gemfire.driver.spi.config.PdxConfiguration;

class PdxConfigurationTests {
	private static DriverPdxSerializer serializer() {
		return new DriverPdxSerializer() {
			public boolean toData(Object o, DriverPdxWriter w) { return false; }
			public Object fromData(Class<?> c, DriverPdxReader r) { return null; }
		};
	}

	@Test void defaultsMatchNativeDefaults() {
		var defaults = PdxConfiguration.defaults();
		assertThat(defaults.readSerialized()).isFalse();
		assertThat(defaults.ignoreUnreadFields()).isFalse();
		assertThat(defaults.persistent()).isFalse();
		assertThat(defaults.diskStoreName()).isEmpty();
		assertThat(defaults.serializer()).isEmpty();
	}

	@Test void withersReturnNewValuesAndNullClears() {
		var base = PdxConfiguration.defaults();
		var serializer = serializer();
		var configured = base.withReadSerialized(true).withDiskStoreName("ds").withSerializer(serializer);
		assertThat(base.readSerialized()).isFalse();
		assertThat(configured.readSerialized()).isTrue();
		assertThat(configured.diskStoreName()).contains("ds");
		assertThat(configured.withDiskStoreName(null).diskStoreName()).isEmpty();
		assertThat(configured.withSerializer(null).serializer()).isEmpty();
		assertThat(configured).isNotEqualTo(base).isEqualTo(base.withReadSerialized(true).withDiskStoreName("ds").withSerializer(serializer));
	}

	@Test void cacheConfigurationCarriesOptionalPdx() {
		var pdx = PdxConfiguration.defaults().withIgnoreUnreadFields(true);
		assertThat(new CacheConfiguration(java.util.Map.<String, String>of(), null, pdx).pdx()).contains(pdx);
		assertThat(new CacheConfiguration(java.util.Map.<String, String>of(), null, null).pdx()).isEmpty();
	}
}
