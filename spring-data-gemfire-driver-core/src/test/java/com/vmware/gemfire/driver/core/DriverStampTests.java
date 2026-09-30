package com.vmware.gemfire.driver.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import com.vmware.gemfire.driver.spi.DriverBindException;
import com.vmware.gemfire.driver.spi.SpiVersion;

class DriverStampTests {

	private static Properties properties(String... keyValues) {
		Properties properties = new Properties();
		for (int i = 0; i < keyValues.length; i += 2) {
			properties.setProperty(keyValues[i], keyValues[i + 1]);
		}
		return properties;
	}

	@Test
	void parsesBothVersions() {
		DriverStamp stamp = DriverStamp.parse(properties("spi.version", "1.4", "gemfire.version", "10.3.2"), "p.Driver");
		assertThat(stamp.builtAgainstSpiVersion()).isEqualTo(SpiVersion.of(1, 4));
		assertThat(stamp.gemFireVersion()).isEqualTo("10.3.2");
	}

	@Test
	void ignoresSpiPatchSegment() {
		DriverStamp stamp = DriverStamp.parse(properties("spi.version", "1.4.7", "gemfire.version", "10.3.2"), "p.Driver");
		assertThat(stamp.builtAgainstSpiVersion()).isEqualTo(SpiVersion.of(1, 4));
	}

	@Test
	void missingSpiVersionIsABindFailure() {
		assertThatThrownBy(() -> DriverStamp.parse(properties("gemfire.version", "10.3.2"), "p.Driver"))
			.isInstanceOf(DriverBindException.class).hasMessageContaining("spi.version").hasMessageContaining("p.Driver");
	}

	@Test
	void missingGemFireVersionIsABindFailure() {
		assertThatThrownBy(() -> DriverStamp.parse(properties("spi.version", "1.4"), "p.Driver"))
			.isInstanceOf(DriverBindException.class).hasMessageContaining("gemfire.version");
	}

	@Test
	void blankValueIsABindFailure() {
		assertThatThrownBy(() -> DriverStamp.parse(properties("spi.version", " ", "gemfire.version", "10.3.2"), "p.Driver"))
			.isInstanceOf(DriverBindException.class);
	}

	@Test
	void malformedSpiVersionIsABindFailure() {
		assertThatThrownBy(() -> DriverStamp.parse(properties("spi.version", "one", "gemfire.version", "10.3.2"), "p.Driver"))
			.isInstanceOf(DriverBindException.class).hasMessageContaining("malformed");
	}

}
