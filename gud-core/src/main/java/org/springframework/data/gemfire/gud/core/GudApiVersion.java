/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-14: Created GudApiVersion class for version compatibility checking
 */

package org.springframework.data.gemfire.gud.core;

/**
 * Represents a GUD API version for compatibility checking.
 * Used to determine if a driver is compatible with a given API version.
 */
public final class GudApiVersion implements Comparable<GudApiVersion> {

    /** GUD API version 1.0 - initial release supporting GemFire 10.3 */
    public static final GudApiVersion V1_0 = new GudApiVersion(1, 0, 0);

    /** GUD API version 1.1 - adds GemFire 10.4 support */
    public static final GudApiVersion V1_1 = new GudApiVersion(1, 1, 0);

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
     *
     * @param version the version string (e.g., "1.0.0" or "1.1")
     * @return the parsed GudApiVersion
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
     * Compatibility means same major version and this version's minor >= other's minor.
     *
     * @param other the other version to check compatibility with
     * @return true if compatible
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
