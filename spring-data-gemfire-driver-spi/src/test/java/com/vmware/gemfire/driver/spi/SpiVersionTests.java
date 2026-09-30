package com.vmware.gemfire.driver.spi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class SpiVersionTests {

	@Test
	void acceptsSameVersion() {
		assertThat(SpiVersion.of(1, 4).accepts(SpiVersion.of(1, 4))).isTrue();
	}

	@Test
	void acceptsDriverBuiltAgainstOlderMinor() {
		assertThat(SpiVersion.of(1, 4).accepts(SpiVersion.of(1, 0))).isTrue();
	}

	@Test
	void rejectsDriverBuiltAgainstNewerMinor() {
		assertThat(SpiVersion.of(1, 2).accepts(SpiVersion.of(1, 4))).isFalse();
	}

	@Test
	void rejectsDifferentMajorInEitherDirection() {
		assertThat(SpiVersion.of(2, 0).accepts(SpiVersion.of(1, 9))).isFalse();
		assertThat(SpiVersion.of(1, 9).accepts(SpiVersion.of(2, 0))).isFalse();
	}

	@Test
	void parsesMajorMinorAndIgnoresFurtherSegments() {
		assertThat(SpiVersion.parse("1.4")).isEqualTo(SpiVersion.of(1, 4));
		assertThat(SpiVersion.parse(" 2.10.3-SNAPSHOT ")).isEqualTo(SpiVersion.of(2, 10));
	}

	@Test
	void rejectsMalformedVersions() {
		assertThatThrownBy(() -> SpiVersion.parse("1")).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> SpiVersion.parse("a.b")).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> SpiVersion.parse(null)).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> SpiVersion.of(-1, 0)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void ordersByMajorThenMinor() {
		assertThat(SpiVersion.of(1, 9)).isLessThan(SpiVersion.of(2, 0));
		assertThat(SpiVersion.of(1, 2)).isLessThan(SpiVersion.of(1, 3));
	}

	@Test
	void runtimeVersionIsReadFromTheSpiJarResource() {
		SpiVersion runtime = SpiVersion.runtime();
		assertThat(runtime.major()).isGreaterThanOrEqualTo(1);
		assertThat(runtime.accepts(runtime)).isTrue();
	}

}
