// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire;

import org.apache.geode.cache.FixedPartitionAttributes;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Spring {@link FactoryBean} to create a instance of the {@link FixedPartitionAttributes}.
 *
 * @author David Turanski
 * @author John Blum
 * @see FactoryBean
 * @see InitializingBean
 * @see FixedPartitionAttributes
 */
@SuppressWarnings("unused")
public class FixedPartitionAttributesFactoryBean implements FactoryBean<FixedPartitionAttributes>, InitializingBean {

	private Boolean primary;

	private FixedPartitionAttributes fixedPartitionAttributes;

	private Integer numBuckets;

	private String partitionName;

	/**
	 * @inheritDoc
	 */
	@Override
	@SuppressWarnings("all")
	public void afterPropertiesSet() throws Exception {
		Assert.hasText(this.partitionName, "partitionName must be specified");

		if (this.primary == null && this.numBuckets == null){
			this.fixedPartitionAttributes = FixedPartitionAttributes.createFixedPartition(this.partitionName);
		}
		else if (this.primary == null && this.numBuckets != null){
			this.fixedPartitionAttributes = FixedPartitionAttributes.createFixedPartition(
				this.partitionName, this.numBuckets);
		}
		else if (this.primary != null && this.numBuckets == null) {
			this.fixedPartitionAttributes = FixedPartitionAttributes.createFixedPartition(
				this.partitionName, this.primary);
		}
		else {
			this.fixedPartitionAttributes = FixedPartitionAttributes.createFixedPartition(
				this.partitionName, this.primary, this.numBuckets);
		}

	}

	/**
	 * @inheritDoc
	 */
	@Override
	public FixedPartitionAttributes getObject() throws Exception {
		return this.fixedPartitionAttributes;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Class<?> getObjectType() {
		return (this.fixedPartitionAttributes != null ? this.fixedPartitionAttributes.getClass()
			: FixedPartitionAttributes.class);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean isSingleton() {
		return true;
	}

	/**
	 * Set the number of buckets in the Partition Region.
	 *
	 * @param numBuckets integer value indicating the number of buckets in the Partition Region.
	 */
	public void setNumBuckets(Integer numBuckets) {
		this.numBuckets = numBuckets;
	}

	/**
	 * Set the name of the partition in the Partition Region.
	 *
	 * @param partitionName name of the partition.
	 */
	public void setPartitionName(String partitionName) {
		this.partitionName = partitionName;
	}

	/**
	 * Sets whether this particular PARTITION Region is the primary (i.e. not secondary).
	 *
	 * @param primary a boolean value to indicate whether this PARTITION Region is the primary.
	 */
	public void setPrimary(boolean primary) {
		this.primary = primary;
	}
}
