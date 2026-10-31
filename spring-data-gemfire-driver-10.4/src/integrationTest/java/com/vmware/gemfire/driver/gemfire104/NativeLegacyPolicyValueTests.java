// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.vmware.gemfire.driver.spi.config.*;

/** Pins the native-free policy values to the native 10.4 classes they replace. No cache or server is needed. */
class NativeLegacyPolicyValueTests {
	@Test void dataPolicyMatchesNativeNamesOrdinalsAndPredicates() {
		for (var spi : DataPolicy.values()) {
			var nat = org.apache.geode.cache.DataPolicy.valueOf(spi.name());
			assertThat(spi.legacyOrdinal()).isEqualTo(nat.ordinal());
			assertThat(spi.toString()).isEqualTo(nat.toString());
			assertThat(spi.withStorage()).isEqualTo(nat.withStorage());
			assertThat(spi.withReplication()).isEqualTo(nat.withReplication());
			assertThat(spi.withPersistence()).isEqualTo(nat.withPersistence());
			assertThat(spi.withPartitioning()).isEqualTo(nat.withPartitioning());
			assertThat(spi.withPreloaded()).isEqualTo(nat.withPreloaded());
			assertThat(spi.isEmpty()).isEqualTo(nat.isEmpty());
			assertThat(spi.isNormal()).isEqualTo(nat.isNormal());
			assertThat(spi.isPreloaded()).isEqualTo(nat.isPreloaded());
			assertThat(spi.isDefault()).isEqualTo(nat.isDefault());
			assertThat(spi.isReplicate()).isEqualTo(nat.isReplicate());
			assertThat(spi.isPersistentReplicate()).isEqualTo(nat.isPersistentReplicate());
			assertThat(spi.isPartition()).isEqualTo(nat.isPartition());
			assertThat(DataPolicy.fromLegacyOrdinal(nat.ordinal)).isSameAs(spi);
			assertThat(org.apache.geode.cache.DataPolicy.fromOrdinal((byte) spi.legacyOrdinal())).isSameAs(nat);
		}
		assertThat(DataPolicy.DEFAULT.name()).isEqualTo(org.apache.geode.cache.DataPolicy.DEFAULT.toString());
	}

	@Test void clientRegionShortcutMatchesTheNativeConstantsInOrder() {
		var natives = org.apache.geode.cache.client.ClientRegionShortcut.values();
		var spi = ClientRegionShortcut.values();
		assertThat(spi).hasSameSizeAs(natives);
		for (int i = 0; i < natives.length; i++) {
			assertThat(spi[i].name()).isEqualTo(natives[i].name());
			assertThat(ClientRegionShortcut.valueOf(natives[i].name())).isSameAs(spi[i]);
			assertThat(org.apache.geode.cache.client.ClientRegionShortcut.valueOf(spi[i].name())).isSameAs(natives[i]);
		}
	}

	@Test void scopeMatchesNativeNamesOrdinalsAndPredicates() {
		for (var spi : Scope.values()) {
			if (spi == Scope.DISTRIBUTED_NO_ACK) continue;
			var nat = org.apache.geode.cache.Scope.fromString(spi.name());
			assertThat(spi.legacyOrdinal()).isEqualTo(nat.ordinal);
			assertThat(spi.toString()).isEqualTo(nat.toString());
			assertThat(spi.toConfigTypeString()).isEqualTo(nat.toConfigTypeString());
			assertThat(spi.isLocal()).isEqualTo(nat.isLocal());
			assertThat(spi.isDistributed()).isEqualTo(nat.isDistributed());
			assertThat(spi.isDistributedAck()).isEqualTo(nat.isDistributedAck());
			assertThat(spi.isGlobal()).isEqualTo(nat.isGlobal());
			assertThat(spi.isAck()).isEqualTo(nat.isAck());
		}
		for (int ordinal = 0; ordinal <= 3; ordinal++) {
			assertThat(Scope.fromLegacyOrdinal(ordinal).name()).isEqualTo(org.apache.geode.cache.Scope.fromOrdinal(ordinal).toString());
		}
		assertThatThrownBy(() -> org.apache.geode.cache.Scope.fromString("DISTRIBUTED_NO_ACK")).hasMessage(catchThrowable(() -> Scope.fromString("DISTRIBUTED_NO_ACK")).getMessage());
		assertThatThrownBy(() -> org.apache.geode.cache.Scope.fromString("nope")).hasMessageContaining("nope is not a valid string representation of");
	}

	@Test @SuppressWarnings("deprecation") void mirrorTypeMatchesNativeOrdinalsPolicyAndPredicates() {
		for (var spi : MirrorType.values()) {
			var nat = org.apache.geode.cache.MirrorType.fromOrdinal(spi.legacyOrdinal());
			assertThat(spi.toString()).isEqualTo(nat.toString());
			assertThat(spi.getDataPolicy().name()).isEqualTo(nat.getDataPolicy().toString());
			assertThat(spi.isNone()).isEqualTo(nat.isNone());
			assertThat(spi.isKeys()).isEqualTo(nat.isKeys());
			assertThat(spi.isKeysValues()).isEqualTo(nat.isKeysValues());
			assertThat(spi.isMirrored()).isEqualTo(nat.isMirrored());
		}
	}

	/** Drives the native factory with every starting policy and compares each conversion with the SPI one. */
	@Test @SuppressWarnings({"deprecation", "unchecked"}) void mirrorAndPersistConversionsMatchTheNativeFactory() {
		for (var start : DataPolicy.values()) {
			if (start.withPartitioning()) continue;
			var natStart = org.apache.geode.cache.DataPolicy.valueOf(start.name());
			for (var mirror : MirrorType.values()) {
				var factory = new org.apache.geode.cache.AttributesFactory<Object, Object>();
				factory.setDataPolicy(natStart);
				factory.setMirrorType(org.apache.geode.cache.MirrorType.fromOrdinal(mirror.legacyOrdinal()));
				assertThat(LegacyPolicyConversions.applyMirrorType(start, mirror).name()).as("%s + %s", start, mirror).isEqualTo(factory.create().getDataPolicy().toString());
			}
			for (boolean persist : new boolean[] {true, false}) {
				var factory = new org.apache.geode.cache.AttributesFactory<Object, Object>();
				factory.setDataPolicy(natStart);
				factory.setPersistBackup(persist);
				assertThat(LegacyPolicyConversions.applyPersistBackup(start, persist).name()).as("%s persist=%s", start, persist).isEqualTo(factory.create().getDataPolicy().toString());
			}
			var attributes = new org.apache.geode.cache.AttributesFactory<Object, Object>();
			attributes.setDataPolicy(natStart);
			var created = attributes.create();
			assertThat(LegacyPolicyConversions.mirrorTypeOf(start).toString()).isEqualTo(created.getMirrorType().toString());
			assertThat(LegacyPolicyConversions.persistBackupOf(start)).isEqualTo(created.getPersistBackup());
		}
		assertThatThrownBy(() -> new org.apache.geode.cache.AttributesFactory<Object, Object>().setMirrorType(null)).isInstanceOf(IllegalArgumentException.class).hasMessage("mirrorType must not be null");
	}
}
