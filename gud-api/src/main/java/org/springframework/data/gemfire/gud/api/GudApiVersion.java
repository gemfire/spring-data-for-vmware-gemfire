/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-14: Created GudApiVersion class for version compatibility checking
 * 2026-03-17: Removed V1_1 (10.4) placeholder
 * 2026-04-02: Moved from gud-core to gud-api — version contract belongs with the API
 * 2026-06-06: Updated V1_0_0 Javadoc to reflect removal of the 10.0 driver (minimum is now 10.1)
 */

package org.springframework.data.gemfire.gud.api;

/**
 * Represents a GUD API version for compatibility checking.
 * Used to determine if a driver is compatible with a given API version.
 *
 * <p>Parse a version string with {@link #parse(String)} and compare instances
 * with {@link #compareTo(GudApiVersion)} or {@link #isCompatibleWith(GudApiVersion)}.
 */
public final class GudApiVersion implements Comparable<GudApiVersion> {

    /** GUD API version 1.0 — initial release supporting GemFire 10.1 through 10.3. */
    public static final GudApiVersion V1_0_0 = new GudApiVersion(1, 0, 0);

    private final int major;
    private final int minor;
    private final int patch;

    /**
     * Creates a new GudApiVersion.
     *
     * @param major the major version number
     * @param minor the minor version number
     * @param patch the patch version number
     */
    public GudApiVersion(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    /**
     * Parses a version string into a GudApiVersion.
     * Supports {@code "1.0"}, {@code "1.0.0"}, or any number of dot-separated integer segments.
     *
     * @param version the version string
     * @return the parsed GudApiVersion
     * @throws NumberFormatException if any segment is not a valid integer
     */
    public static GudApiVersion parse(String version) {
        String[] parts = version.split("\\.");
        return new GudApiVersion(
            Integer.parseInt(parts[0]),
            parts.length > 1 ? Integer.parseInt(parts[1]) : 0,
            parts.length > 2 ? Integer.parseInt(parts[2]) : 0
        );
    }

    /**
     * Gets the major version number.
     *
     * @return the major version
     */
    public int getMajor() {
        return major;
    }

    /**
     * Gets the minor version number.
     *
     * @return the minor version
     */
    public int getMinor() {
        return minor;
    }

    /**
     * Gets the patch version number.
     *
     * @return the patch version
     */
    public int getPatch() {
        return patch;
    }

    /**
     * Checks if this version is compatible with another version.
     * Compatibility means the same major version and this version's minor &gt;= other's minor.
     *
     * @param other the other version to check compatibility with
     * @return {@code true} if compatible
     */
    public boolean isCompatibleWith(GudApiVersion other) {
        return this.major == other.major && this.minor >= other.minor;
    }

    @Override
    public int compareTo(GudApiVersion other) {
        int result = Integer.compare(this.major, other.major);
        if (result == 0) {
            result = Integer.compare(this.minor, other.minor);
        }
        if (result == 0) {
            result = Integer.compare(this.patch, other.patch);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GudApiVersion that = (GudApiVersion) o;
        return major == that.major && minor == that.minor && patch == that.patch;
    }

    @Override
    public int hashCode() {
        int result = major;
        result = 31 * result + minor;
        result = 31 * result + patch;
        return result;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}
