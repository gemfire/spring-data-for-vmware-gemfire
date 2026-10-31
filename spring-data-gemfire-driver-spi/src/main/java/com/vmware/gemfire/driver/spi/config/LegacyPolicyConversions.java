// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi.config;

import java.util.Objects;

/**
 * Conversions between the legacy mirror-type / persist-backup settings and a {@link DataPolicy}. They reproduce the
 * legacy {@code AttributesFactory.setMirrorType}/{@code setPersistBackup} and {@code RegionAttributes.getMirrorType}/{@code getPersistBackup}
 * rules, including the partition branches, so values read from legacy configuration behave as before. They describe
 * client-local storage and persistence; they do not request distributed replication.
 */
public final class LegacyPolicyConversions {

	private LegacyPolicyConversions() { }

	/** The policy after applying a mirror type to {@code current}. An already replicating policy is kept when mirroring, and none reverts replication to normal. */
	public static DataPolicy applyMirrorType(DataPolicy current, MirrorType mirrorType) {
		Objects.requireNonNull(current, "current");
		if (mirrorType == null) throw new IllegalArgumentException("mirrorType must not be null");
		DataPolicy selected = mirrorType.getDataPolicy();
		if (selected.withReplication()) return current.withReplication() ? current : selected;
		return current.withReplication() ? selected : current;
	}

	/** The policy after applying the persist-backup flag to {@code current}. */
	public static DataPolicy applyPersistBackup(DataPolicy current, boolean persistBackup) {
		Objects.requireNonNull(current, "current");
		if (persistBackup) {
			if (current.withPersistence()) return current;
			return current.withPartitioning() ? DataPolicy.PERSISTENT_PARTITION : DataPolicy.PERSISTENT_REPLICATE;
		}
		if (current.withReplication()) return DataPolicy.REPLICATE;
		return current.withPartitioning() ? DataPolicy.PARTITION : current;
	}

	/** The mirror type a policy corresponds to: none for non-replicating policies, keys-values for replicating ones. */
	public static MirrorType mirrorTypeOf(DataPolicy policy) {
		Objects.requireNonNull(policy, "policy");
		if (policy.isNormal() || policy.isPreloaded() || policy.isEmpty() || policy.withPartitioning()) return MirrorType.NONE;
		if (policy.withReplication()) return MirrorType.KEYS_VALUES;
		throw new IllegalStateException(String.format("No mirror type corresponds to data policy %s", policy));
	}

	/** Whether the policy persists its data. */
	public static boolean persistBackupOf(DataPolicy policy) {
		return Objects.requireNonNull(policy, "policy").withPersistence();
	}
}
