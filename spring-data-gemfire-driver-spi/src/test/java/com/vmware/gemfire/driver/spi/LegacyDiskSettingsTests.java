// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

import static org.assertj.core.api.Assertions.*;
import java.io.File;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import com.vmware.gemfire.driver.spi.config.*;

class LegacyDiskSettingsTests {
	private final File[] DIRS = {new File("a"), new File("b")};

	@Test void defaultInstancesCarryTheLegacyDefaults() {
		var sync = DiskWriteSettings.defaultSync();
		assertThat(sync.isSynchronous()).isTrue();
		assertThat(sync.isRollOplogs()).isTrue();
		assertThat(sync.getTimeInterval()).isZero();
		assertThat(sync.getBytesThreshold()).isZero();
		assertThat(sync.getMaxOplogSize()).isEqualTo(1024);
		var async = DiskWriteSettings.defaultAsync();
		assertThat(async.isSynchronous()).isFalse();
		assertThat(async.getTimeInterval()).isEqualTo(1000);
	}

	@Test void unsetTimeIntervalIsZeroWhenABytesThresholdIsSet() {
		assertThat(DiskWriteSettings.builder().bytesThreshold(10).build().getTimeInterval()).isZero();
		assertThat(DiskWriteSettings.builder().synchronous(false).bytesThreshold(10).timeInterval(5).build().getTimeInterval()).isEqualTo(5);
	}

	@Test void builderRejectsNegativeValuesWithTheLegacyMessages() {
		assertThatIllegalArgumentException().isThrownBy(() -> DiskWriteSettings.builder().timeInterval(-1)).withMessage("Time Interval specified has to be a non-negative number and the value given -1 is not acceptable");
		assertThatIllegalArgumentException().isThrownBy(() -> DiskWriteSettings.builder().bytesThreshold(-2)).withMessage("Queue size specified has to be a non-negative number and the value given -2 is not acceptable");
		assertThatIllegalArgumentException().isThrownBy(() -> DiskWriteSettings.builder().maxOplogSize(-3)).withMessageContaining("the value given");
	}

	@Test void infiniteOplogSizeConflictsWithRolling() {
		assertThatIllegalStateException().isThrownBy(() -> DiskWriteSettings.builder().maxOplogSize(0).build()).withMessageStartingWith("Compaction cannot be set to true");
		assertThatIllegalArgumentException().isThrownBy(() -> DiskWriteSettings.builder().maxOplogSize(Integer.MAX_VALUE).build()).withMessage("Cannot set maxOplogs size to infinity (0) if compaction is set to true");
		var infinite = DiskWriteSettings.builder().rollOplogs(false).maxOplogSize(0).build();
		assertThat(infinite.getMaxOplogSize()).isEqualTo(Integer.MAX_VALUE);
	}

	@Test void equalityIgnoresAsyncOnlyFieldsForSynchronousSettings() {
		var a = DiskWriteSettings.builder().synchronous(true).timeInterval(1).bytesThreshold(2).build();
		var b = DiskWriteSettings.builder().synchronous(true).timeInterval(7).bytesThreshold(9).build();
		assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
		assertThat(DiskWriteSettings.defaultAsync()).isNotEqualTo(DiskWriteSettings.builder().synchronous(false).timeInterval(5).build());
	}

	@Test void toStringDescribesTheSettings() {
		assertThat(DiskWriteSettings.defaultSync().toString()).startsWith("Synchronous writes to disk. MaxOplog size is        : 1073741824");
		assertThat(DiskWriteSettings.defaultAsync().toString()).startsWith("Asynchronous writes to disk after a threshold of 1000ms or 0 bytes");
	}

	@Test void toBuilderKeepsTheSettings() {
		var original = DiskWriteSettings.builder().synchronous(false).timeInterval(5).bytesThreshold(6).maxOplogSize(7).rollOplogs(false).build();
		var copy = original.toBuilder().build();
		assertThat(copy).isEqualTo(original);
		assertThat(copy.getTimeInterval()).isEqualTo(5);
		assertThat(original.toBuilder().synchronous(true).build().isSynchronous()).isTrue();
	}

	@Test void freshConfigurationHoldsTheLegacyDefaults() {
		var fresh = new LegacyDiskConfiguration();
		assertThat(fresh.getDiskStoreName()).isNull();
		assertThat(fresh.getDiskDirs()).containsExactly(new File("."));
		assertThat(fresh.getDiskDirSizes()).containsExactly(Integer.MAX_VALUE);
		assertThat(fresh.getDiskWriteSettings()).isEqualTo(DiskWriteSettings.defaultSync());
		assertThat(fresh.isDiskSynchronous()).isTrue();
	}

	@Test void settingDirectoriesSwitchesToTheOldAsynchronousDefault() {
		var configured = new LegacyDiskConfiguration().withDiskDirs(DIRS);
		assertThat(configured.getDiskDirs()).containsExactly(DIRS);
		assertThat(configured.getDiskDirSizes()).containsExactly(Integer.MAX_VALUE, Integer.MAX_VALUE);
		assertThat(configured.isDiskSynchronous()).isFalse();
		assertThat(configured.getDiskWriteSettings()).isEqualTo(DiskWriteSettings.defaultAsync());
	}

	@Test void directoriesKeepAnExplicitSynchronousChoice() {
		var configured = new LegacyDiskConfiguration().withDiskSynchronous(true).withDiskDirs(DIRS);
		assertThat(configured.isDiskSynchronous()).isTrue();
		assertThat(configured.getDiskWriteSettings()).isEqualTo(DiskWriteSettings.defaultSync());
		var withWrite = new LegacyDiskConfiguration().withDiskWriteSettings(DiskWriteSettings.defaultSync()).withDiskDirsAndSizes(DIRS, new int[] {1, 2});
		assertThat(withWrite.getDiskWriteSettings()).isEqualTo(DiskWriteSettings.defaultSync());
		assertThat(withWrite.getDiskDirSizes()).containsExactly(1, 2);
	}

	@Test void sizesMustMatchTheDirectoriesAndBeNonNegative() {
		var fresh = new LegacyDiskConfiguration();
		assertThatIllegalArgumentException().isThrownBy(() -> fresh.withDiskDirsAndSizes(DIRS, new int[] {1})).withMessage("Number of diskSizes is 1 which is not equal to number of disk Dirs which is 2");
		assertThatIllegalArgumentException().isThrownBy(() -> fresh.withDiskDirsAndSizes(DIRS, new int[] {1, -1})).withMessage("Dir size cannot be negative : -1");
	}

	@Test void writeSettingsDriveTheSynchronousFlag() {
		var configured = new LegacyDiskConfiguration().withDiskWriteSettings(DiskWriteSettings.defaultAsync());
		assertThat(configured.isDiskSynchronous()).isFalse();
		var toggled = configured.withDiskSynchronous(true);
		assertThat(toggled.getDiskWriteSettings().isSynchronous()).isTrue();
		assertThat(toggled.getDiskWriteSettings().getTimeInterval()).isEqualTo(1000);
		assertThat(new LegacyDiskConfiguration().withDiskSynchronous(false).getDiskWriteSettings()).isEqualTo(DiskWriteSettings.defaultAsync());
	}

	@Test void aDiskStoreExcludesTheDeprecatedSettingsInBothDirections() {
		var store = new LegacyDiskConfiguration().withDiskStoreName("store");
		assertThat(store.getDiskStoreName()).isEqualTo("store");
		assertThatIllegalStateException().isThrownBy(() -> store.withDiskDirs(DIRS)).withMessage("Deprecated API setDiskDirs cannot be used with DiskStore store");
		assertThatIllegalStateException().isThrownBy(() -> store.withDiskDirsAndSizes(DIRS, new int[] {1, 2})).withMessage("Deprecated API setDiskDirsAndSizes cannot be used with DiskStore store");
		assertThatIllegalStateException().isThrownBy(() -> store.withDiskWriteSettings(DiskWriteSettings.defaultSync())).withMessage("Deprecated API setDiskWriteAttributes cannot be used with DiskStore store");
		assertThatIllegalStateException().isThrownBy(() -> new LegacyDiskConfiguration().withDiskDirs(DIRS).withDiskStoreName("s")).withMessage("Deprecated API setDiskDirs or setDiskWriteAttributes cannot be used with DiskStore s");
		assertThatIllegalStateException().isThrownBy(() -> new LegacyDiskConfiguration().withDiskWriteSettings(DiskWriteSettings.defaultSync()).withDiskStoreName("s"));
		assertThat(store.withDiskSynchronous(false).getDiskStoreName()).isEqualTo("store");
	}

	@Test void accessorsReturnCopies() {
		var configured = new LegacyDiskConfiguration().withDiskDirs(DIRS);
		configured.getDiskDirs()[0] = new File("changed");
		assertThat(configured.getDiskDirs()).containsExactly(DIRS);
	}

	@Test void directoriesAreCreatedWhenMissing(@TempDir Path temp) {
		var missing = temp.resolve("x/y").toFile();
		LegacyDiskConfiguration.ensureDirectoriesExist(new File[] {missing});
		assertThat(missing).isDirectory();
	}

	@Test void directoryCreationFailureNamesTheDirectory(@TempDir Path temp) throws Exception {
		var file = temp.resolve("plain").toFile();
		assertThat(file.createNewFile()).isTrue();
		var impossible = new File(file, "child");
		assertThatExceptionOfType(UncheckedIOException.class).isThrownBy(() -> LegacyDiskConfiguration.ensureDirectoriesExist(new File[] {impossible}))
				.withMessageContaining("Unable to create directory : " + impossible);
	}
}
