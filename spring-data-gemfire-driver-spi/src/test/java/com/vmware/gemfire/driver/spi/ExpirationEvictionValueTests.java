// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.vmware.gemfire.driver.spi.config.*;

class ExpirationEvictionValueTests {
	@Test void expirationAttributesClampAndDefault() {
		assertThat(new ExpirationAttributes(-5).getTimeout()).isZero();
		assertThat(new ExpirationAttributes(5, null).getAction()).isEqualTo(ExpirationAction.INVALIDATE);
		assertThat(ExpirationAttributes.DEFAULT.toString()).isEqualTo("NO EXPIRATION");
		assertThat(new ExpirationAttributes(5, ExpirationAction.DESTROY)).isEqualTo(new ExpirationAttributes(5, ExpirationAction.DESTROY));
	}

	@Test void expirationActionAndResetPolicyParsing() {
		assertThat(ExpirationAction.fromXmlString("local-destroy")).isEqualTo(ExpirationAction.LOCAL_DESTROY);
		assertThat(ExpirationAction.DESTROY.isDistributed()).isTrue();
		assertThat(TimeToLiveResetPolicy.fromXmlString(null)).isEqualTo(TimeToLiveResetPolicy.LAST_MODIFIED);
		assertThatThrownBy(() -> TimeToLiveResetPolicy.fromXmlString("bogus")).isInstanceOf(IllegalArgumentException.class);
	}

	@Test void evictionDefaultsAndParsing() {
		assertThat(EvictionAttributes.createLRUEntryAttributes().getMaximum()).isEqualTo(900);
		assertThat(EvictionAttributes.createLRUMemoryAttributes().getMaximum()).isEqualTo(10);
		assertThat(EvictionAction.parseAction("overflow-to-disk")).isEqualTo(EvictionAction.OVERFLOW_TO_DISK);
		assertThat(EvictionAlgorithm.parseAction("lru-entry-count")).isEqualTo(EvictionAlgorithm.LRU_ENTRY);
	}
}
