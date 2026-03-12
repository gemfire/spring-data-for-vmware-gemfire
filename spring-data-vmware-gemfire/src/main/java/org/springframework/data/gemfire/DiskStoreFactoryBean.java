/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-12: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire;

import static java.util.stream.StreamSupport.stream;
import static org.springframework.data.gemfire.util.ArrayUtils.nullSafeArray;
import static org.springframework.data.gemfire.util.CollectionUtils.nullSafeCollection;
import static org.springframework.data.gemfire.util.CollectionUtils.nullSafeIterable;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalStateException;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.gemfire.config.annotation.DiskStoreConfigurer;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudDiskStore;
import org.springframework.data.gemfire.gud.api.GudDiskStoreFactory;
import org.springframework.data.gemfire.support.AbstractFactoryBeanSupport;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Spring {@link FactoryBean} used to create a {@link GudDiskStore}.
 *
 * @author David Turanski
 * @author John Blum
 * @see File
 * @see GudDiskStore
 * @see GudDiskStoreFactory
 * @see GudClientCache
 * @see FactoryBean
 * @see InitializingBean
 * @see DiskStoreConfigurer
 * @see AbstractFactoryBeanSupport
 */
@SuppressWarnings("unused")
public abstract class DiskStoreFactoryBean extends AbstractFactoryBeanSupport<GudDiskStore> implements InitializingBean {

	private Boolean allowForceCompaction;
	private Boolean autoCompact;

	private GudDiskStore diskStore;

	private GudClientCache cache;

	private Integer compactionThreshold;
	private Integer queueSize;
	private Integer writeBufferSize;
	private Integer segments;

	private Float diskUsageCriticalPercentage;
	private Float diskUsageWarningPercentage;

	private Long maxOplogSize;
	private Long timeInterval;

	private List<DiskStoreConfigurer> diskStoreConfigurers = Collections.emptyList();

	private DiskStoreConfigurer compositeDiskStoreConfigurer = (beanName, bean) ->
		nullSafeCollection(diskStoreConfigurers).forEach(diskStoreConfigurer ->
			diskStoreConfigurer.configure(beanName, bean));

	private List<DiskDir> diskDirs;

	@Override
	public void afterPropertiesSet() throws Exception {

		String diskStoreName = resolveDiskStoreName();

		applyDiskStoreConfigurers(diskStoreName);

		GudClientCache cache = resolveCache(diskStoreName);

		GudDiskStoreFactory diskStoreFactory = postProcess(configure(createDiskStoreFactory(cache)));

		this.diskStore = postProcess(newDiskStore(diskStoreFactory, diskStoreName));
	}

	/* (non-Javadoc) */
	private void applyDiskStoreConfigurers(String diskStoreName) {
		applyDiskStoreConfigurers(diskStoreName, getCompositeDiskStoreConfigurer());
	}

	/**
	 * Null-safe operation to apply the given array of {@link DiskStoreConfigurer DiskStoreConfigurers}
	 * to this {@link DiskStoreFactoryBean}.
	 *
	 * @param diskStoreName {@link String} containing the name of the {@link DiskStore}.
	 * @param diskStoreConfigurers array of {@link DiskStoreConfigurer DiskStoreConfigurers} applied
	 * to this {@link DiskStoreFactoryBean}.
	 * @see DiskStoreConfigurer
	 * @see #applyDiskStoreConfigurers(String, Iterable)
	 */
	protected void applyDiskStoreConfigurers(String diskStoreName, DiskStoreConfigurer... diskStoreConfigurers) {
		applyDiskStoreConfigurers(diskStoreName,
			Arrays.asList(nullSafeArray(diskStoreConfigurers, DiskStoreConfigurer.class)));
	}

	/**
	 * Null-safe operation to apply the given {@link Iterable} of {@link DiskStoreConfigurer DiskStoreConfigurers}
	 * to this {@link DiskStoreFactoryBean}.
	 *
	 * @param diskStoreName {@link String} containing the name of the {@link DiskStore}.
	 * @param diskStoreConfigurers {@link Iterable} of {@link DiskStoreConfigurer DiskStoreConfigurers} applied
	 * to this {@link DiskStoreFactoryBean}.
	 * @see DiskStoreConfigurer
	 */
	protected void applyDiskStoreConfigurers(String diskStoreName, Iterable<DiskStoreConfigurer> diskStoreConfigurers) {
		stream(nullSafeIterable(diskStoreConfigurers).spliterator(), false)
			.forEach(diskStoreConfigurer -> diskStoreConfigurer.configure(diskStoreName, this));
	}

	/* (non-Javadoc) */
	private GudClientCache resolveCache(String diskStoreName) {
		return Optional.ofNullable(this.cache)
			.orElseThrow(() -> newIllegalStateException("Cache is required to create DiskStore [%s]", diskStoreName));
	}

	/* (non-Javadoc) */
	final String resolveDiskStoreName() {
		return Optional.ofNullable(getBeanName()).filter(StringUtils::hasText)
			.orElse(getDefaultDiskStoreName());
	}

	/**
	 * Returns the default disk store name.
	 *
	 * @return the default disk store name.
	 */
	protected abstract String getDefaultDiskStoreName();

	/**
	 * Creates an instance of {@link GudDiskStoreFactory} using the given {@link GudClientCache} in order to
	 * construct, configure and initialize a new {@link GudDiskStore}.
	 *
	 * @param cache reference to the {@link GudClientCache} used to create the {@link GudDiskStoreFactory}.
	 * @return a new instance of {@link GudDiskStoreFactory}.
	 * @see GudClientCache#createDiskStoreFactory()
	 * @see GudDiskStoreFactory
	 */
	protected abstract GudDiskStoreFactory createDiskStoreFactory(GudClientCache cache);

	/**
	 * Configures the given {@link GudDiskStoreFactory} with the configuration settings present
	 * on this {@link DiskStoreFactoryBean}
	 *
	 * @param diskStoreFactory {@link GudDiskStoreFactory} to configure.
	 * @return the given {@link GudDiskStoreFactory}
	 * @see GudDiskStoreFactory
	 */
	protected GudDiskStoreFactory configure(GudDiskStoreFactory diskStoreFactory) {

		Optional.ofNullable(this.allowForceCompaction).ifPresent(diskStoreFactory::setAllowForceCompaction);
		Optional.ofNullable(this.autoCompact).ifPresent(diskStoreFactory::setAutoCompact);
		Optional.ofNullable(this.compactionThreshold).ifPresent(diskStoreFactory::setCompactionThreshold);
		Optional.ofNullable(this.diskUsageCriticalPercentage).ifPresent(diskStoreFactory::setDiskUsageCriticalPercentage);
		Optional.ofNullable(this.diskUsageWarningPercentage).ifPresent(diskStoreFactory::setDiskUsageWarningPercentage);
		Optional.ofNullable(this.maxOplogSize).ifPresent(diskStoreFactory::setMaxOplogSize);
		Optional.ofNullable(this.queueSize).ifPresent(diskStoreFactory::setQueueSize);
		Optional.ofNullable(this.timeInterval).ifPresent(diskStoreFactory::setTimeInterval);
		Optional.ofNullable(this.writeBufferSize).ifPresent(diskStoreFactory::setWriteBufferSize);
		Optional.ofNullable(this.segments).ifPresent(diskStoreFactory::setSegments);

		Optional.ofNullable(this.diskDirs).filter(diskDirs -> !CollectionUtils.isEmpty(diskDirs))
			.ifPresent(diskDirs -> {

				File[] diskDirFiles = new File[diskDirs.size()];
				int[] diskDirSizes = new int[diskDirs.size()];

				for (int index = 0; index < diskDirs.size(); index++) {
					DiskDir diskDir = diskDirs.get(index);
					diskDirFiles[index] = new File(diskDir.location);
					diskDirSizes[index] = Optional.ofNullable(diskDir.maxSize)
						.orElse(getDefaultDiskDirSize());
				}

				diskStoreFactory.setDiskDirsAndSizes(diskDirFiles, diskDirSizes);
			});

		return diskStoreFactory;
	}

	/**
	 * Returns the default disk directory size.
	 *
	 * @return the default disk directory size.
	 */
	protected abstract int getDefaultDiskDirSize();

	/**
	 * Constructs a new instance of {@link GudDiskStore} with the given {@link String name}
	 * using the provided {@link GudDiskStoreFactory}
	 *
	 * @param diskStoreFactory {@link GudDiskStoreFactory} used to create the {@link GudDiskStore}.
	 * @param diskStoreName {@link String} containing the name of the new {@link GudDiskStore}.
	 * @return a new instance of {@link GudDiskStore} with the given {@link String name}.
	 * @see GudDiskStoreFactory
	 * @see GudDiskStore
	 */
	protected GudDiskStore newDiskStore(GudDiskStoreFactory diskStoreFactory, String diskStoreName) {
		return diskStoreFactory.create(diskStoreName);
	}

	/**
	 * Post-process the {@link GudDiskStoreFactory} with any custom {@link GudDiskStoreFactory} or {@link GudDiskStore}
	 * configuration settings as required by the application.
	 *
	 * @param diskStoreFactory {@link GudDiskStoreFactory} to process.
	 * @return the given {@link GudDiskStoreFactory}.
	 * @see GudDiskStoreFactory
	 */
	protected GudDiskStoreFactory postProcess(GudDiskStoreFactory diskStoreFactory) {
		return diskStoreFactory;
	}

	/**
	 * Post-process the provided {@link GudDiskStore} constructed, configured and initialized
	 * by this {@link DiskStoreFactoryBean}.
	 *
	 * @param diskStore {@link GudDiskStore} to process.
	 * @return the given {@link GudDiskStore}.
	 * @see GudDiskStore
	 */
	protected GudDiskStore postProcess(GudDiskStore diskStore) {
		return diskStore;
	}

	/**
	 * Returns a reference to the Composite {@link DiskStoreConfigurer} used to apply additional configuration
	 * to this {@link DiskStoreFactoryBean} on Spring container initialization.
	 *
	 * @return the Composite {@link DiskStoreConfigurer}.
	 * @see DiskStoreConfigurer
	 */
	protected DiskStoreConfigurer getCompositeDiskStoreConfigurer() {
		return this.compositeDiskStoreConfigurer;
	}

	@Override
	public GudDiskStore getObject() throws Exception {
		return this.diskStore;
	}

	@Override
	public Class<?> getObjectType() {
		return this.diskStore != null ? this.diskStore.getClass() : GudDiskStore.class;
	}

	public void setCache(GudClientCache cache) {
		this.cache = cache;
	}

	public void setAllowForceCompaction(Boolean allowForceCompaction) {
		this.allowForceCompaction = allowForceCompaction;
	}

	public void setAutoCompact(Boolean autoCompact) {
		this.autoCompact = autoCompact;
	}

	public void setCompactionThreshold(Integer compactionThreshold) {
		validateCompactionThreshold(compactionThreshold);
		this.compactionThreshold = compactionThreshold;
	}

	protected void validateCompactionThreshold(Integer compactionThreshold) {
		Assert.isTrue(compactionThreshold == null || (compactionThreshold >= 0 && compactionThreshold <= 100),
			String.format("The DiskStore's (%1$s) compaction threshold (%2$d) must be an integer value between 0 and 100 inclusive.",
				resolveDiskStoreName(), compactionThreshold));
	}

	public void setDiskDirs(List<DiskDir> diskDirs) {
		this.diskDirs = diskDirs;
	}

	/**
	 * Null-safe operation to set an array of {@link DiskStoreConfigurer DiskStoreConfigurers} used to
	 * apply additional configuration to this {@link DiskStoreFactoryBean} when using Annotation-based configuration.
	 *
	 * @param diskStoreConfigurers array of {@link DiskStoreConfigurer DiskStoreConfigurers} used to apply
	 * additional configuration to this {@link DiskStoreFactoryBean}.
	 * @see DiskStoreConfigurer
	 * @see #setDiskStoreConfigurers(List)
	 */
	public void setDiskStoreConfigurers(DiskStoreConfigurer... diskStoreConfigurers) {
		setDiskStoreConfigurers(Arrays.asList(nullSafeArray(diskStoreConfigurers, DiskStoreConfigurer.class)));
	}

	/**
	 * Null-safe operation to set an {@link Iterable} of {@link DiskStoreConfigurer DiskStoreConfigurers}
	 * used to apply additional configuration to this {@link DiskStoreFactoryBean}
	 * when using Annotation-based configuration.
	 *
	 * @param diskStoreConfigurers {@link Iterable } of {@link DiskStoreConfigurer DiskStoreConfigurers} used to
	 * apply additional configuration to this {@link DiskStoreFactoryBean}.
	 * @see DiskStoreConfigurer
	 */
	public void setDiskStoreConfigurers(List<DiskStoreConfigurer> diskStoreConfigurers) {
		this.diskStoreConfigurers = Optional.ofNullable(diskStoreConfigurers).orElseGet(Collections::emptyList);
	}

	public void setDiskUsageCriticalPercentage(Float diskUsageCriticalPercentage) {
		this.diskUsageCriticalPercentage = diskUsageCriticalPercentage;
	}

	public void setDiskUsageWarningPercentage(Float diskUsageWarningPercentage) {
		this.diskUsageWarningPercentage = diskUsageWarningPercentage;
	}

	public void setMaxOplogSize(Long maxOplogSize) {
		this.maxOplogSize = maxOplogSize;
	}

	public void setQueueSize(Integer queueSize) {
		this.queueSize = queueSize;
	}

	public void setTimeInterval(Long timeInterval) {
		this.timeInterval = timeInterval;
	}

	public void setWriteBufferSize(Integer writeBufferSize) {
		this.writeBufferSize = writeBufferSize;
	}

	public void setSegments(Integer segments) {
		this.segments = segments;
	}

	public static class DiskDir {

		final Integer maxSize;
		final String location;

		public DiskDir(String location) {
			this.location = location;
			this.maxSize = null;
		}

		public DiskDir(String location, int maxSize) {
			this.location = location;
			this.maxSize = maxSize;
		}
	}
}
