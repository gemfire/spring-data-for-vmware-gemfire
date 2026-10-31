// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi.config;

import java.io.File;
import java.io.UncheckedIOException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * Immutable state of the legacy region disk settings: disk directories and sizes, write settings, the synchronous flag
 * and the disk store name. Each {@code with…} method reproduces the matching legacy {@code AttributesFactory} setter,
 * including the rule that a disk store name excludes the deprecated directory and write settings and the reverse,
 * and returns a new instance. A failed call changes nothing, whereas the legacy factory could keep a directory change
 * made before a later check failed. Directory creation is separate, see {@link #ensureDirectoriesExist(File[])}.
 */
public final class LegacyDiskConfiguration {

	/** Legacy default concurrency level. */
	public static final int DEFAULT_CONCURRENCY_LEVEL = 16;
	/** Legacy default for synchronous disk writes. */
	public static final boolean DEFAULT_DISK_SYNCHRONOUS = true;
	/** Legacy default directory size in megabytes. */
	public static final int DEFAULT_DISK_DIR_SIZE = Integer.MAX_VALUE;

	private final String diskStoreName;
	private final File[] diskDirs;
	private final int[] diskSizes;
	private final DiskWriteSettings writeSettings;
	private final boolean diskSynchronous;
	private final boolean hasDiskDirs;
	private final boolean hasWriteSettings;
	private final boolean hasDiskSynchronous;

	private LegacyDiskConfiguration(String diskStoreName, File[] diskDirs, int[] diskSizes, DiskWriteSettings writeSettings,
			boolean diskSynchronous, boolean hasDiskDirs, boolean hasWriteSettings, boolean hasDiskSynchronous) {
		this.diskStoreName = diskStoreName;
		this.diskDirs = diskDirs;
		this.diskSizes = diskSizes;
		this.writeSettings = writeSettings;
		this.diskSynchronous = diskSynchronous;
		this.hasDiskDirs = hasDiskDirs;
		this.hasWriteSettings = hasWriteSettings;
		this.hasDiskSynchronous = hasDiskSynchronous;
	}

	/** The state of a fresh legacy factory: the current directory, synchronous writes and no disk store. */
	public LegacyDiskConfiguration() {
		this(null, new File[] {new File(".")}, new int[] {DEFAULT_DISK_DIR_SIZE}, DiskWriteSettings.defaultSync(),
				DEFAULT_DISK_SYNCHRONOUS, false, false, false);
	}

	public LegacyDiskConfiguration withDiskWriteSettings(DiskWriteSettings settings) {
		Objects.requireNonNull(settings, "settings");
		rejectDeprecatedApiWithDiskStore("setDiskWriteAttributes");
		return new LegacyDiskConfiguration(null, diskDirs, diskSizes, settings, settings.isSynchronous(), hasDiskDirs, true, hasDiskSynchronous);
	}

	public LegacyDiskConfiguration withDiskDirs(File[] dirs) {
		Objects.requireNonNull(dirs, "dirs");
		rejectDeprecatedApiWithDiskStore("setDiskDirs");
		int[] sizes = new int[dirs.length];
		Arrays.fill(sizes, DEFAULT_DISK_DIR_SIZE);
		return withDirsAndSizes(dirs, sizes);
	}

	public LegacyDiskConfiguration withDiskDirsAndSizes(File[] dirs, int[] sizes) {
		Objects.requireNonNull(dirs, "dirs");
		Objects.requireNonNull(sizes, "sizes");
		rejectDeprecatedApiWithDiskStore("setDiskDirsAndSizes");
		if (sizes.length != dirs.length) {
			throw new IllegalArgumentException(String.format("Number of diskSizes is %s which is not equal to number of disk Dirs which is %s", sizes.length, dirs.length));
		}
		for (int size : sizes) {
			if (size < 0) throw new IllegalArgumentException(String.format("Dir size cannot be negative : %s", size));
		}
		return withDirsAndSizes(dirs, sizes);
	}

	private LegacyDiskConfiguration withDirsAndSizes(File[] dirs, int[] sizes) {
		boolean legacyDefault = !hasWriteSettings && !hasDiskSynchronous;
		return new LegacyDiskConfiguration(null, dirs.clone(), sizes.clone(), legacyDefault ? DiskWriteSettings.defaultAsync() : writeSettings,
				legacyDefault ? false : diskSynchronous, true, hasWriteSettings, hasDiskSynchronous);
	}

	public LegacyDiskConfiguration withDiskStoreName(String name) {
		if (hasDiskDirs || hasWriteSettings) {
			throw new IllegalStateException(String.format("Deprecated API %s cannot be used with DiskStore %s", "setDiskDirs or setDiskWriteAttributes", name));
		}
		return new LegacyDiskConfiguration(name, diskDirs, diskSizes, writeSettings, diskSynchronous, false, false, hasDiskSynchronous);
	}

	public LegacyDiskConfiguration withDiskSynchronous(boolean synchronous) {
		DiskWriteSettings settings = hasWriteSettings ? writeSettings.toBuilder().synchronous(synchronous).build()
				: (synchronous ? DiskWriteSettings.defaultSync() : DiskWriteSettings.defaultAsync());
		return new LegacyDiskConfiguration(diskStoreName, diskDirs, diskSizes, settings, synchronous, hasDiskDirs, hasWriteSettings, true);
	}

	private void rejectDeprecatedApiWithDiskStore(String api) {
		if (diskStoreName != null) throw new IllegalStateException(String.format("Deprecated API %s cannot be used with DiskStore %s", api, diskStoreName));
	}

	/** The disk store name, or {@code null} when the deprecated settings are in use. */
	public String getDiskStoreName() { return diskStoreName; }
	public File[] getDiskDirs() { return diskDirs.clone(); }
	public int[] getDiskDirSizes() { return diskSizes.clone(); }
	public DiskWriteSettings getDiskWriteSettings() { return writeSettings; }
	public boolean isDiskSynchronous() { return diskSynchronous; }

	/**
	 * Creates each directory that is missing, as the legacy factory did while setting directories. Unlike the legacy
	 * {@code GemFireIOException} this fails with an {@link UncheckedIOException}.
	 */
	public static void ensureDirectoriesExist(File[] dirs) {
		for (File dir : Objects.requireNonNull(dirs, "dirs")) {
			if (!dir.isDirectory() && !dir.mkdirs()) {
				throw new UncheckedIOException(new IOException(String.format("Unable to create directory : %s", dir)));
			}
		}
	}
}
