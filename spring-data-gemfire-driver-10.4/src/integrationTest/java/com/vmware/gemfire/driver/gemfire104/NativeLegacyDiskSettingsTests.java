// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.*;
import java.io.File;
import java.nio.file.Path;
import org.apache.geode.cache.AttributesFactory;
import org.apache.geode.cache.DiskWriteAttributes;
import org.apache.geode.cache.DiskWriteAttributesFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import com.vmware.gemfire.driver.spi.config.*;

/** Pins the native-free legacy disk settings to the native 10.4 factories they replace. No cache or server is needed. */
@SuppressWarnings({"deprecation", "unchecked"})
class NativeLegacyDiskSettingsTests {

	private static void assertSame(DiskWriteSettings spi, DiskWriteAttributes nat) {
		assertThat(spi.isSynchronous()).isEqualTo(nat.isSynchronous());
		assertThat(spi.isRollOplogs()).isEqualTo(nat.isRollOplogs());
		assertThat(spi.getTimeInterval()).isEqualTo(nat.getTimeInterval());
		assertThat(spi.getBytesThreshold()).isEqualTo(nat.getBytesThreshold());
		assertThat(spi.getMaxOplogSize()).isEqualTo(nat.getMaxOplogSize());
		assertThat(spi.toString()).isEqualTo(nat.toString());
		assertThat(spi.hashCode()).isEqualTo(nat.hashCode());
	}

	private static void assertSame(LegacyDiskConfiguration spi, AttributesFactory<Object, Object> factory) {
		var nat = factory.create();
		assertThat(spi.getDiskStoreName()).isEqualTo(nat.getDiskStoreName());
		assertThat(spi.getDiskDirs()).containsExactly(nat.getDiskDirs());
		assertThat(spi.getDiskDirSizes()).containsExactly(nat.getDiskDirSizes());
		assertThat(spi.isDiskSynchronous()).isEqualTo(nat.isDiskSynchronous());
		assertSame(spi.getDiskWriteSettings(), nat.getDiskWriteAttributes());
	}

	@Test void constantsMatchTheNativeDefaults() {
		assertThat(LegacyDiskConfiguration.DEFAULT_CONCURRENCY_LEVEL).isEqualTo(AttributesFactory.DEFAULT_CONCURRENCY_LEVEL);
		assertThat(LegacyDiskConfiguration.DEFAULT_DISK_SYNCHRONOUS).isEqualTo(AttributesFactory.DEFAULT_DISK_SYNCHRONOUS);
		assertThat(LegacyDiskConfiguration.DEFAULT_DISK_DIR_SIZE).isEqualTo(org.apache.geode.cache.DiskStoreFactory.DEFAULT_DISK_DIR_SIZE);
	}

	@Test void writeSettingsMatchTheNativeFactoryForCombinationsOfInputs() {
		for (boolean sync : new boolean[] {true, false}) {
			for (boolean roll : new boolean[] {true, false}) {
				for (long threshold : new long[] {0, 10}) {
					var spi = DiskWriteSettings.builder().synchronous(sync).rollOplogs(roll).bytesThreshold(threshold).build();
					var nat = new DiskWriteAttributesFactory();
					nat.setSynchronous(sync);
					nat.setRollOplogs(roll);
					nat.setBytesThreshold(threshold);
					assertSame(spi, nat.create());
					assertThat(spi.equals(DiskWriteSettings.builder().synchronous(sync).rollOplogs(roll).bytesThreshold(threshold).build())).isTrue();
					assertSame(spi.toBuilder().build(), new DiskWriteAttributesFactory(nat.create()).create());
				}
			}
		}
		var spi = DiskWriteSettings.builder().synchronous(false).timeInterval(5).maxOplogSize(7).rollOplogs(false).build();
		var nat = new DiskWriteAttributesFactory();
		nat.setSynchronous(false);
		nat.setTimeInterval(5);
		nat.setMaxOplogSize(7);
		nat.setRollOplogs(false);
		assertSame(spi, nat.create());
	}

	@Test void defaultInstancesMatchTheNativeFactoryDefaults() {
		assertSame(DiskWriteSettings.defaultAsync(), new DiskWriteAttributesFactory().create());
		var sync = new DiskWriteAttributesFactory();
		sync.setSynchronous(true);
		assertSame(DiskWriteSettings.defaultSync(), sync.create());
	}

	@Test void writeSettingsRejectTheSameInputsWithTheSameMessages() {
		assertThatThrownBy(() -> new DiskWriteAttributesFactory().setTimeInterval(-1)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage(catchThrowable(() -> DiskWriteSettings.builder().timeInterval(-1)).getMessage());
		assertThatThrownBy(() -> new DiskWriteAttributesFactory().setBytesThreshold(-1)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage(catchThrowable(() -> DiskWriteSettings.builder().bytesThreshold(-1)).getMessage());
		assertThatThrownBy(() -> new DiskWriteAttributesFactory().setMaxOplogSize(-1)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage(catchThrowable(() -> DiskWriteSettings.builder().maxOplogSize(-1)).getMessage());
		var infinite = new DiskWriteAttributesFactory();
		infinite.setMaxOplogSize(0);
		assertThatThrownBy(infinite::create).isInstanceOf(IllegalStateException.class)
				.hasMessage(catchThrowable(() -> DiskWriteSettings.builder().maxOplogSize(0).build()).getMessage());
		var limit = new DiskWriteAttributesFactory();
		limit.setMaxOplogSize(Integer.MAX_VALUE);
		assertThatThrownBy(limit::create).isInstanceOf(IllegalArgumentException.class)
				.hasMessage(catchThrowable(() -> DiskWriteSettings.builder().maxOplogSize(Integer.MAX_VALUE).build()).getMessage());
	}

	@Test void freshConfigurationMatchesAFreshNativeFactory() {
		assertSame(new LegacyDiskConfiguration(), new AttributesFactory<>());
	}

	@Test void directoriesAndSizesMatchTheNativeFactory(@TempDir Path temp) {
		File[] dirs = {temp.resolve("a").toFile(), temp.resolve("b").toFile()};
		var nat = new AttributesFactory<Object, Object>();
		nat.setDiskDirs(dirs);
		assertSame(new LegacyDiskConfiguration().withDiskDirs(dirs), nat);

		var sized = new AttributesFactory<Object, Object>();
		sized.setDiskDirsAndSizes(dirs, new int[] {3, 4});
		assertSame(new LegacyDiskConfiguration().withDiskDirsAndSizes(dirs, new int[] {3, 4}), sized);

		var explicit = new AttributesFactory<Object, Object>();
		explicit.setDiskSynchronous(true);
		explicit.setDiskDirs(dirs);
		assertSame(new LegacyDiskConfiguration().withDiskSynchronous(true).withDiskDirs(dirs), explicit);
	}

	@Test void synchronousAndWriteSettingsInteractLikeTheNativeFactory() {
		var toggled = new AttributesFactory<Object, Object>();
		toggled.setDiskSynchronous(false);
		assertSame(new LegacyDiskConfiguration().withDiskSynchronous(false), toggled);

		var write = new AttributesFactory<Object, Object>();
		write.setDiskWriteAttributes(new DiskWriteAttributesFactory().create());
		var configured = new LegacyDiskConfiguration().withDiskWriteSettings(DiskWriteSettings.defaultAsync());
		assertSame(configured, write);

		write.setDiskSynchronous(true);
		assertSame(configured.withDiskSynchronous(true), write);
	}

	@Test void mutualExclusionAndLengthChecksMatchTheNativeFactory(@TempDir Path temp) {
		File[] dirs = {temp.resolve("a").toFile(), temp.resolve("b").toFile()};
		var store = new AttributesFactory<Object, Object>();
		store.setDiskStoreName("store");
		var spiStore = new LegacyDiskConfiguration().withDiskStoreName("store");
		assertThatThrownBy(() -> store.setDiskDirs(dirs)).isInstanceOf(IllegalStateException.class).hasMessage(catchThrowable(() -> spiStore.withDiskDirs(dirs)).getMessage());
		assertThatThrownBy(() -> store.setDiskDirsAndSizes(dirs, new int[] {1, 2})).isInstanceOf(IllegalStateException.class)
				.hasMessage(catchThrowable(() -> spiStore.withDiskDirsAndSizes(dirs, new int[] {1, 2})).getMessage());
		var write = DiskWriteSettings.defaultSync();
		assertThatThrownBy(() -> store.setDiskWriteAttributes(new DiskWriteAttributesFactory().create())).isInstanceOf(IllegalStateException.class)
				.hasMessage(catchThrowable(() -> spiStore.withDiskWriteSettings(write)).getMessage());

		var dirsFirst = new AttributesFactory<Object, Object>();
		dirsFirst.setDiskDirs(dirs);
		assertThatThrownBy(() -> dirsFirst.setDiskStoreName("s")).isInstanceOf(IllegalStateException.class)
				.hasMessage(catchThrowable(() -> new LegacyDiskConfiguration().withDiskDirs(dirs).withDiskStoreName("s")).getMessage());

		var plain = new AttributesFactory<Object, Object>();
		var spiPlain = new LegacyDiskConfiguration();
		assertThatThrownBy(() -> plain.setDiskDirsAndSizes(dirs, new int[] {1})).isInstanceOf(IllegalArgumentException.class)
				.hasMessage(catchThrowable(() -> spiPlain.withDiskDirsAndSizes(dirs, new int[] {1})).getMessage());
		assertThatThrownBy(() -> new AttributesFactory<Object, Object>().setDiskDirsAndSizes(dirs, new int[] {1, -1})).isInstanceOf(IllegalArgumentException.class)
				.hasMessage(catchThrowable(() -> spiPlain.withDiskDirsAndSizes(dirs, new int[] {1, -1})).getMessage());
	}

	@Test void missingDirectoriesAreCreatedLikeTheNativeFactory(@TempDir Path temp) {
		var viaNative = temp.resolve("native/deep").toFile();
		var viaSpi = temp.resolve("spi/deep").toFile();
		new AttributesFactory<Object, Object>().setDiskDirs(new File[] {viaNative});
		LegacyDiskConfiguration.ensureDirectoriesExist(new File[] {viaSpi});
		assertThat(viaNative).isDirectory();
		assertThat(viaSpi).isDirectory();
	}
}
