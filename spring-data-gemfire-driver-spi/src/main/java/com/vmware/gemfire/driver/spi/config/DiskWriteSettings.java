// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi.config;

import java.util.Objects;

/**
 * Immutable legacy disk write settings, replacing the legacy {@code DiskWriteAttributes}. Defaults, validation messages,
 * equality and {@link #toString()} follow the legacy implementation. The legacy default maximum oplog size can be changed
 * with the {@code gemfire.DEFAULT_MAX_OPLOG_SIZE} system property; that property is not honored here, the default is
 * {@value #DEFAULT_MAX_OPLOG_SIZE_MB} MB.
 */
public final class DiskWriteSettings {

	/** The default maximum oplog size in megabytes. */
	public static final long DEFAULT_MAX_OPLOG_SIZE_MB = 1024L;

	private static final long MB = 1024L * 1024L;
	private static final long MAX_OPLOG_SIZE_LIMIT = (long) Integer.MAX_VALUE * MB;
	private static final long DEFAULT_MAX_OPLOG_SIZE = DEFAULT_MAX_OPLOG_SIZE_MB * MB;
	private static final long DEFAULT_ASYNC_TIME_INTERVAL = 1000L;
	private static final DiskWriteSettings DEFAULT_SYNC = builder().synchronous(true).build();
	private static final DiskWriteSettings DEFAULT_ASYNC = builder().synchronous(false).build();

	private final boolean synchronous;
	private final boolean rollOplogs;
	private final long timeInterval;
	private final long bytesThreshold;
	private final long maxOplogSizeBytes;

	private DiskWriteSettings(boolean synchronous, boolean rollOplogs, long timeInterval, long bytesThreshold, long maxOplogSizeBytes) {
		this.synchronous = synchronous;
		this.rollOplogs = rollOplogs;
		this.timeInterval = timeInterval;
		this.bytesThreshold = bytesThreshold;
		this.maxOplogSizeBytes = maxOplogSizeBytes;
	}

	/** Synchronous writes with every other setting at its default. */
	public static DiskWriteSettings defaultSync() { return DEFAULT_SYNC; }

	/** Asynchronous writes every second with every other setting at its default. */
	public static DiskWriteSettings defaultAsync() { return DEFAULT_ASYNC; }

	public static Builder builder() { return new Builder(); }

	/** A builder holding these settings, so one attribute can be changed. The oplog size is carried over in whole megabytes, as the legacy copy did. */
	public Builder toBuilder() {
		return new Builder().synchronous(synchronous).rollOplogs(rollOplogs).timeInterval(timeInterval)
				.bytesThreshold(bytesThreshold).maxOplogSizeInBytes(getMaxOplogSize() * MB);
	}

	public boolean isSynchronous() { return synchronous; }
	public boolean isRollOplogs() { return rollOplogs; }
	public long getTimeInterval() { return timeInterval; }
	public long getBytesThreshold() { return bytesThreshold; }

	/** The maximum oplog size in whole megabytes. */
	public int getMaxOplogSize() { return (int) (maxOplogSizeBytes / MB); }

	/** The maximum oplog size in bytes. */
	public long getMaxOplogSizeInBytes() { return maxOplogSizeBytes; }

	@Override public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof DiskWriteSettings that)) return false;
		if (synchronous != that.synchronous) return false;
		if (rollOplogs != that.rollOplogs || getMaxOplogSize() != that.getMaxOplogSize()) return false;
		return synchronous || (timeInterval == that.timeInterval && bytesThreshold == that.bytesThreshold);
	}

	@Override public int hashCode() {
		int result = 0;
		if (synchronous) {
			if (rollOplogs) result += 2;
		} else {
			result += 1 + (int) timeInterval + (int) bytesThreshold;
		}
		return result + getMaxOplogSize();
	}

	@Override public String toString() {
		String writes = synchronous ? "Synchronous writes to disk"
				: "Asynchronous writes to disk after a threshold of " + timeInterval + "ms or " + bytesThreshold + " bytes";
		return writes + ". MaxOplog size is        : " + maxOplogSizeBytes + ". RollOplogs is          : " + rollOplogs;
	}

	/** Collects settings the way the legacy factory did: unset values take their defaults when {@link #build()} resolves them. */
	public static final class Builder {
		private Boolean synchronous;
		private Boolean rollOplogs;
		private Long timeInterval;
		private Long bytesThreshold;
		private Long maxOplogSizeBytes;

		private Builder() { }

		public Builder synchronous(boolean synchronous) { this.synchronous = synchronous; return this; }
		public Builder rollOplogs(boolean rollOplogs) { this.rollOplogs = rollOplogs; return this; }

		public Builder timeInterval(long timeInterval) {
			if (timeInterval < 0) throw new IllegalArgumentException(String.format("Time Interval specified has to be a non-negative number and the value given %s is not acceptable", timeInterval));
			this.timeInterval = timeInterval;
			return this;
		}

		public Builder bytesThreshold(long bytesThreshold) {
			if (bytesThreshold < 0) throw new IllegalArgumentException(String.format("Queue size specified has to be a non-negative number and the value given %s is not acceptable", bytesThreshold));
			this.bytesThreshold = bytesThreshold;
			return this;
		}

		/** Sets the maximum oplog size in megabytes. */
		public Builder maxOplogSize(int megabytes) {
			if (megabytes < 0) throw new IllegalArgumentException(String.format("Maximum Oplog size specified has to be a non-negative number and the value given %s is not acceptable", megabytes));
			return maxOplogSizeInBytes(megabytes * MB);
		}

		public Builder maxOplogSizeInBytes(long bytes) {
			if (bytes < 0) throw new IllegalArgumentException(String.format("Maximum Oplog size specified has to be a non-negative number and the value given %s is not acceptable", bytes));
			this.maxOplogSizeBytes = bytes;
			return this;
		}

		/** Resolves defaults and rejects an infinite oplog size combined with oplog rolling, as the legacy implementation did. */
		public DiskWriteSettings build() {
			boolean sync = synchronous != null && synchronous;
			boolean roll = rollOplogs == null || rollOplogs;
			long threshold = bytesThreshold == null ? 0L : bytesThreshold;
			long interval = timeInterval != null ? timeInterval : (!sync && threshold == 0L ? DEFAULT_ASYNC_TIME_INTERVAL : 0L);
			long oplog = DEFAULT_MAX_OPLOG_SIZE;
			if (maxOplogSizeBytes != null) {
				long requested = maxOplogSizeBytes;
				if (requested == 0L && roll) throw new IllegalStateException("Compaction cannot be set to true if max-oplog-size is set to infinite (infinite is represented by size zero : 0)");
				if (requested == 0L || requested == MAX_OPLOG_SIZE_LIMIT) {
					if (roll) throw new IllegalArgumentException("Cannot set maxOplogs size to infinity (0) if compaction is set to true");
					oplog = MAX_OPLOG_SIZE_LIMIT;
				} else {
					oplog = requested;
				}
			}
			return new DiskWriteSettings(sync, roll, interval, threshold, oplog);
		}
	}
}
