// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

import static org.assertj.core.api.Assertions.*;
import java.util.Map;
import org.junit.jupiter.api.Test;
import com.vmware.gemfire.driver.spi.config.*;
import com.vmware.gemfire.driver.spi.config.LegacyEvictionSettings.Branch;

class LegacyEvictionSettingsTests {

	@Test void allNullArgumentsProduceNothing() {
		assertThat(LegacyEvictionSettingsFactory.generate(null, null, null, null, null)).isNull();
	}

	@Test void noMaximumSelectsTheHeapBranch() {
		var settings = LegacyEvictionSettingsFactory.generate("overflow-to-disk", null, null, null, null);
		assertThat(settings.getBranch()).isEqualTo(Branch.LRU_HEAP_PERCENTAGE);
		assertThat(settings.getAction()).isEqualTo(EvictionAction.OVERFLOW_TO_DISK);
		assertThat(settings.getMaximum()).isNull();
		assertThat(settings.getSizerClassName()).isNull();
	}

	@Test void aMemoryMaximumWinsOverAnEntryMaximum() {
		var settings = LegacyEvictionSettingsFactory.generate(null, 50, 7, "com.example.Sizer", Map.of("b", "2", "a", "1"));
		assertThat(settings.getBranch()).isEqualTo(Branch.LRU_MEMORY_SIZE);
		assertThat(settings.getAction()).isEqualTo(EvictionAction.LOCAL_DESTROY);
		assertThat(settings.getMaximum()).isEqualTo("50");
		assertThat(settings.getSizerClassName()).isEqualTo("com.example.Sizer");
		assertThat(settings.getSizerParameters()).containsExactly(Map.entry("a", "1"), Map.entry("b", "2"));
	}

	@Test void anEntryMaximumDropsTheSizer() {
		var settings = LegacyEvictionSettingsFactory.generate("local-destroy", null, 7, "com.example.Sizer", Map.of("a", "1"));
		assertThat(settings.getBranch()).isEqualTo(Branch.LRU_ENTRY_COUNT);
		assertThat(settings.getMaximum()).isEqualTo("7");
		assertThat(settings.getSizerClassName()).isNull();
		assertThat(settings.getSizerParameters()).isEmpty();
	}

	@Test void aSizerAloneSelectsTheHeapBranchWithTheSizer() {
		var settings = LegacyEvictionSettingsFactory.generate(null, null, null, "com.example.Sizer", null);
		assertThat(settings.getBranch()).isEqualTo(Branch.LRU_HEAP_PERCENTAGE);
		assertThat(settings.getSizerClassName()).isEqualTo("com.example.Sizer");
		assertThat(settings.getSizerParameters()).isEmpty();
	}

	@Test void aBlankSizerNameMeansAnEmptyNameWithoutParameters() {
		var settings = LegacyEvictionSettingsFactory.generate(null, 1, null, " ", Map.of("a", "1"));
		assertThat(settings.getSizerClassName()).isEmpty();
		assertThat(settings.getSizerParameters()).isEmpty();
	}

	@Test void actionParsingFollowsTheLegacyRules() {
		assertThat(LegacyEvictionSettings.parseAction("")).isEqualTo(EvictionAction.LOCAL_DESTROY);
		assertThat(LegacyEvictionSettings.parseAction("  ")).isEqualTo(EvictionAction.LOCAL_DESTROY);
		assertThat(LegacyEvictionSettings.parseAction("local-destroy")).isEqualTo(EvictionAction.LOCAL_DESTROY);
		assertThatIllegalArgumentException().isThrownBy(() -> LegacyEvictionSettings.parseAction("none")).withMessage("invalid eviction action: none");
		assertThatIllegalArgumentException().isThrownBy(() -> LegacyEvictionSettings.parseAction("LOCAL-DESTROY")).withMessage("invalid eviction action: LOCAL-DESTROY");
	}

	@Test void anInvalidSizerClassNameIsRejectedEvenForTheEntryBranch() {
		assertThatIllegalArgumentException().isThrownBy(() -> LegacyEvictionSettingsFactory.generate(null, null, 5, "not a class", null)).withMessage("Invalid className");
	}

	@Test void equalSettingsAreEqual() {
		var a = LegacyEvictionSettingsFactory.generate(null, 5, null, "a.B", Map.of("k", "v"));
		var b = LegacyEvictionSettingsFactory.generate("", 5, null, "a.B", Map.of("k", "v"));
		assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
		assertThat(a).isNotEqualTo(LegacyEvictionSettingsFactory.generate(null, 6, null, "a.B", Map.of("k", "v")));
	}
}
