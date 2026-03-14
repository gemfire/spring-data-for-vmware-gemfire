/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-14: Created GudCapability enum for driver capability detection
 */

package org.springframework.data.gemfire.gud.core;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Enumeration of capabilities that may vary between GemFire versions.
 * Drivers report which capabilities they support, allowing spring-data-vmware-gemfire
 * to adapt behavior based on the available features.
 */
public enum GudCapability {

    // Core capabilities (all versions)
    BASIC_CACHE_OPERATIONS("Basic cache get/put/remove", "10.0"),
    REGIONS("Region creation and management", "10.0"),
    QUERIES("OQL query execution", "10.0"),
    CONTINUOUS_QUERY("Continuous query support", "10.0"),
    TRANSACTIONS("Transaction support", "10.0"),
    PDX_SERIALIZATION("PDX serialization", "10.0"),
    FUNCTIONS("Function execution", "10.0"),

    // 10.3+ capabilities
    SECURITY_MANAGER("Integrated security manager", "10.3"),
    PER_SERVER_CONNECTION_LIMITS("Per-server min/max connection limits", "10.3"),

    // 10.4+ capabilities (placeholders for future versions)
    PARTITION_STATISTICS("Partition statistics API", "10.4"),
    ENHANCED_SECURITY("Enhanced security features", "10.4"),
    NEW_INDEX_TYPES("New index type support", "10.4"),
    ASYNC_EVENT_QUEUE_V2("Async event queue v2", "10.4");

    private final String description;
    private final String minimumVersion;

    GudCapability(String description, String minimumVersion) {
        this.description = description;
        this.minimumVersion = minimumVersion;
    }

    /**
     * Gets the human-readable description of this capability.
     *
     * @return the capability description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the minimum GemFire version required for this capability.
     *
     * @return the minimum version string
     */
    public String getMinimumVersion() {
        return minimumVersion;
    }

    /**
     * Returns capabilities available at or below the given version.
     *
     * @param version the GemFire version to check against
     * @return set of capabilities available for that version
     */
    public static Set<GudCapability> forVersion(String version) {
        return Arrays.stream(values())
            .filter(c -> compareVersions(c.minimumVersion, version) <= 0)
            .collect(Collectors.toSet());
    }

    private static int compareVersions(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");
        for (int i = 0; i < Math.max(parts1.length, parts2.length); i++) {
            int p1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int p2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
            if (p1 != p2) {
                return Integer.compare(p1, p2);
            }
        }
        return 0;
    }
}
