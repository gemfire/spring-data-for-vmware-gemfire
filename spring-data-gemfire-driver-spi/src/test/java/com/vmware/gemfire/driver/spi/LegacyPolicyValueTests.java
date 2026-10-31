// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.vmware.gemfire.driver.spi.config.*;

class LegacyPolicyValueTests {
	@Test void dataPolicyLegacyOrdinalsFollowTheLegacyOrder() {
		assertThat(DataPolicy.values()).extracting(DataPolicy::legacyOrdinal).containsExactly(0, 1, 5, 4, 6, 2, 3);
		for (var policy : DataPolicy.values()) assertThat(DataPolicy.fromLegacyOrdinal(policy.legacyOrdinal())).isSameAs(policy);
		assertThat(DataPolicy.DEFAULT).isSameAs(DataPolicy.NORMAL);
	}

	@Test void dataPolicyRejectsUnassignedOrdinalsAndNames() {
		assertThatIllegalArgumentException().isThrownBy(() -> DataPolicy.fromLegacyOrdinal(7));
		assertThatIllegalArgumentException().isThrownBy(() -> DataPolicy.fromLegacyOrdinal(-1));
		assertThatIllegalArgumentException().isThrownBy(() -> DataPolicy.fromString("normal")).withMessage("normal");
		assertThatNullPointerException().isThrownBy(() -> DataPolicy.fromString(null));
		assertThat(DataPolicy.fromString("PERSISTENT_REPLICATE")).isSameAs(DataPolicy.PERSISTENT_REPLICATE);
	}

	@Test void dataPolicyPredicatesMatchTheLegacyDefinitions() {
		for (var p : DataPolicy.values()) {
			assertThat(p.withStorage()).isEqualTo(p != DataPolicy.EMPTY);
			assertThat(p.withReplication()).isEqualTo(p == DataPolicy.REPLICATE || p == DataPolicy.PERSISTENT_REPLICATE);
			assertThat(p.withPersistence()).isEqualTo(p == DataPolicy.PERSISTENT_PARTITION || p == DataPolicy.PERSISTENT_REPLICATE);
			assertThat(p.withPartitioning()).isEqualTo(p == DataPolicy.PARTITION || p == DataPolicy.PERSISTENT_PARTITION);
			assertThat(p.withPreloaded()).isEqualTo(p == DataPolicy.PRELOADED);
			assertThat(p.isEmpty()).isEqualTo(p == DataPolicy.EMPTY);
			assertThat(p.isNormal()).isEqualTo(p == DataPolicy.NORMAL);
			assertThat(p.isPreloaded()).isEqualTo(p == DataPolicy.PRELOADED);
			assertThat(p.isDefault()).isEqualTo(p == DataPolicy.NORMAL);
			assertThat(p.isReplicate()).isEqualTo(p == DataPolicy.REPLICATE);
			assertThat(p.isPersistentReplicate()).isEqualTo(p == DataPolicy.PERSISTENT_REPLICATE);
			assertThat(p.isPartition()).isEqualTo(p == DataPolicy.PARTITION);
		}
	}

	@Test void scopeLegacyOrdinalsSkipTheRemovedNoAckSlot() {
		assertThat(Scope.LOCAL.legacyOrdinal()).isZero();
		assertThat(Scope.DISTRIBUTED_ACK.legacyOrdinal()).isEqualTo(2);
		assertThat(Scope.GLOBAL.legacyOrdinal()).isEqualTo(3);
		assertThatIllegalStateException().isThrownBy(Scope.DISTRIBUTED_NO_ACK::legacyOrdinal);
		assertThat(Scope.fromLegacyOrdinal(0)).isSameAs(Scope.LOCAL);
		assertThat(Scope.fromLegacyOrdinal(1)).isSameAs(Scope.DISTRIBUTED_ACK);
		assertThat(Scope.fromLegacyOrdinal(2)).isSameAs(Scope.DISTRIBUTED_ACK);
		assertThat(Scope.fromLegacyOrdinal(3)).isSameAs(Scope.GLOBAL);
		assertThatIllegalArgumentException().isThrownBy(() -> Scope.fromLegacyOrdinal(4));
		assertThatIllegalArgumentException().isThrownBy(() -> Scope.fromLegacyOrdinal(-1));
	}

	@Test void scopeFromStringIsCaseSensitiveAndRejectsTheRemovedScope() {
		assertThat(Scope.fromString("GLOBAL")).isSameAs(Scope.GLOBAL);
		assertThatIllegalArgumentException().isThrownBy(() -> Scope.fromString("DISTRIBUTED_NO_ACK")).withMessage("The distributed-no-ack scope has been removed. Use distributed-ack.");
		assertThatIllegalArgumentException().isThrownBy(() -> Scope.fromString("global")).withMessage("global is not a valid string representation of " + Scope.class.getName() + ".");
		assertThatIllegalArgumentException().isThrownBy(() -> Scope.fromString(null));
		assertThat(Scope.DISTRIBUTED_ACK.toConfigTypeString()).isEqualTo("distributed-ack");
		assertThat(Scope.GLOBAL.toString()).isEqualTo("GLOBAL");
	}

	@Test void mirrorTypeSelectsTheDataPolicyAndPredicates() {
		assertThat(MirrorType.NONE.getDataPolicy()).isSameAs(DataPolicy.NORMAL);
		assertThat(MirrorType.KEYS.getDataPolicy()).isSameAs(DataPolicy.REPLICATE);
		assertThat(MirrorType.KEYS_VALUES.getDataPolicy()).isSameAs(DataPolicy.REPLICATE);
		assertThat(MirrorType.values()).extracting(MirrorType::legacyOrdinal).containsExactly(0, 1, 2);
		for (var t : MirrorType.values()) {
			assertThat(MirrorType.fromLegacyOrdinal(t.legacyOrdinal())).isSameAs(t);
			assertThat(t.isMirrored()).isEqualTo(t != MirrorType.NONE);
			assertThat(t.isNone()).isEqualTo(t == MirrorType.NONE);
			assertThat(t.isKeys()).isEqualTo(t == MirrorType.KEYS);
			assertThat(t.isKeysValues()).isEqualTo(t == MirrorType.KEYS_VALUES);
		}
		assertThat(MirrorType.KEYS_VALUES.toString()).isEqualTo("KEYS_VALUES");
		assertThatIllegalArgumentException().isThrownBy(() -> MirrorType.fromLegacyOrdinal(3));
		assertThatIllegalArgumentException().isThrownBy(() -> MirrorType.fromLegacyOrdinal(-1));
	}

	@Test void mirrorTypeKeepsOrRevertsReplicationOnTheCurrentPolicy() {
		assertThat(LegacyPolicyConversions.applyMirrorType(DataPolicy.NORMAL, MirrorType.KEYS_VALUES)).isSameAs(DataPolicy.REPLICATE);
		assertThat(LegacyPolicyConversions.applyMirrorType(DataPolicy.PERSISTENT_REPLICATE, MirrorType.KEYS)).isSameAs(DataPolicy.PERSISTENT_REPLICATE);
		assertThat(LegacyPolicyConversions.applyMirrorType(DataPolicy.PERSISTENT_REPLICATE, MirrorType.NONE)).isSameAs(DataPolicy.NORMAL);
		assertThat(LegacyPolicyConversions.applyMirrorType(DataPolicy.PRELOADED, MirrorType.NONE)).isSameAs(DataPolicy.PRELOADED);
		assertThatIllegalArgumentException().isThrownBy(() -> LegacyPolicyConversions.applyMirrorType(DataPolicy.NORMAL, null)).withMessage("mirrorType must not be null");
	}

	@Test void persistBackupMovesBetweenPersistentAndPlainPolicies() {
		assertThat(LegacyPolicyConversions.applyPersistBackup(DataPolicy.NORMAL, true)).isSameAs(DataPolicy.PERSISTENT_REPLICATE);
		assertThat(LegacyPolicyConversions.applyPersistBackup(DataPolicy.PARTITION, true)).isSameAs(DataPolicy.PERSISTENT_PARTITION);
		assertThat(LegacyPolicyConversions.applyPersistBackup(DataPolicy.PERSISTENT_REPLICATE, true)).isSameAs(DataPolicy.PERSISTENT_REPLICATE);
		assertThat(LegacyPolicyConversions.applyPersistBackup(DataPolicy.PERSISTENT_REPLICATE, false)).isSameAs(DataPolicy.REPLICATE);
		assertThat(LegacyPolicyConversions.applyPersistBackup(DataPolicy.PERSISTENT_PARTITION, false)).isSameAs(DataPolicy.PARTITION);
		assertThat(LegacyPolicyConversions.applyPersistBackup(DataPolicy.NORMAL, false)).isSameAs(DataPolicy.NORMAL);
	}

	@Test void policiesObserveAsMirrorTypeAndPersistBackup() {
		for (var p : DataPolicy.values()) {
			assertThat(LegacyPolicyConversions.mirrorTypeOf(p)).isSameAs(p.withReplication() ? MirrorType.KEYS_VALUES : MirrorType.NONE);
			assertThat(LegacyPolicyConversions.persistBackupOf(p)).isEqualTo(p.withPersistence());
		}
	}
}
