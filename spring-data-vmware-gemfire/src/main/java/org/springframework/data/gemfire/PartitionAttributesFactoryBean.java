/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire;

import static org.springframework.data.gemfire.util.CollectionUtils.nullSafeList;

import java.util.List;

import org.apache.geode.cache.FixedPartitionAttributes;
import org.apache.geode.cache.PartitionAttributes;
import org.apache.geode.cache.PartitionAttributesFactory;
import org.apache.geode.cache.PartitionResolver;
import org.apache.geode.cache.partition.PartitionListener;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.gemfire.support.AbstractFactoryBeanSupport;

/**
 * Spring {@link FactoryBean} for creating {@link PartitionAttributes}.
 *
 * Eliminates the need to use a XML 'factory-method' tag and allows the attributes properties to be set directly.
 *
 * @author Costin Leau
 * @author David Turanski
 * @author John Blum
 * @see FixedPartitionAttributes
 * @see PartitionAttributes
 * @see PartitionAttributesFactory
 * @see PartitionResolver
 * @see PartitionListener
 * @see FactoryBean
 * @see InitializingBean
 * @see AbstractFactoryBeanSupport
 */
@SuppressWarnings("unused")
public class PartitionAttributesFactoryBean<K, V> extends AbstractFactoryBeanSupport<PartitionAttributes<K, V>>
		implements InitializingBean {

	private List<PartitionListener> partitionListeners;

	private PartitionAttributes<K, V> partitionAttributes;

	private final PartitionAttributesFactory<K, V> partitionAttributesFactory = new PartitionAttributesFactory<>();

	/**
	 * @inheritDoc
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		nullSafeList(partitionListeners).forEach(partitionAttributesFactory::addPartitionListener);
		this.partitionAttributes = partitionAttributesFactory.create();
	}

	@Override
	public PartitionAttributes<K, V> getObject() throws Exception {
		return this.partitionAttributes;
	}

	@Override
	public Class<?> getObjectType() {
		return (this.partitionAttributes != null ? this.partitionAttributes.getClass() : PartitionAttributes.class);
	}

	public void setColocatedWith(String collocatedWith) {
		this.partitionAttributesFactory.setColocatedWith(collocatedWith);
	}

	public void setFixedPartitionAttributes(List<FixedPartitionAttributes> fixedPartitionAttributes) {
		nullSafeList(fixedPartitionAttributes).forEach(this.partitionAttributesFactory::addFixedPartitionAttributes);
	}

	public void setLocalMaxMemory(int mb) {
		this.partitionAttributesFactory.setLocalMaxMemory(mb);
	}

	public void setPartitionListeners(List<PartitionListener> partitionListeners) {
		this.partitionListeners = partitionListeners;
	}

	public void setPartitionResolver(PartitionResolver<K, V> resolver) {
		this.partitionAttributesFactory.setPartitionResolver(resolver);
	}

	public void setRecoveryDelay(long recoveryDelay) {
		this.partitionAttributesFactory.setRecoveryDelay(recoveryDelay);
	}

	public void setRedundantCopies(int redundantCopies) {
		this.partitionAttributesFactory.setRedundantCopies(redundantCopies);
	}

	public void setStartupRecoveryDelay(long startupRecoveryDelay) {
		this.partitionAttributesFactory.setStartupRecoveryDelay(startupRecoveryDelay);
	}

	public void setTotalMaxMemory(long megabytes) {
		this.partitionAttributesFactory.setTotalMaxMemory(megabytes);
	}

	public void setTotalNumBuckets(int numBuckets) {
		this.partitionAttributesFactory.setTotalNumBuckets(numBuckets);
	}
}
