/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.wan;

import static java.util.stream.StreamSupport.stream;
import static org.springframework.data.gemfire.util.CollectionUtils.nullSafeIterable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.wan.GatewayEventFilter;
import org.apache.geode.cache.wan.GatewayEventSubstitutionFilter;
import org.apache.geode.cache.wan.GatewaySender;
import org.apache.geode.cache.wan.GatewaySenderFactory;
import org.apache.geode.cache.wan.GatewayTransportFilter;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.gemfire.config.annotation.GatewaySenderConfigurer;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * Spring {@link FactoryBean} used to construct, configure and initialize parallel and serial
 * {@link GatewaySender GatewaySenders}.
 *
 * @author David Turanski
 * @author John Blum
 * @author Udo Kohlmeyer
 * @see org.apache.geode.cache.Cache
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.wan.GatewayEventFilter
 * @see org.apache.geode.cache.wan.GatewayEventSubstitutionFilter
 * @see org.apache.geode.cache.wan.GatewaySender
 * @see org.apache.geode.cache.wan.GatewaySenderFactory
 * @see org.apache.geode.cache.wan.GatewayTransportFilter
 * @see org.springframework.beans.factory.FactoryBean
 * @see org.springframework.data.gemfire.config.annotation.GatewaySenderConfigurer
 * @see org.springframework.data.gemfire.wan.AbstractWANComponentFactoryBean
 * @since 1.2.2
 */
@SuppressWarnings("unused")
public class GatewaySenderFactoryBean extends AbstractWANComponentFactoryBean<GatewaySender> {

	private boolean manualStart = false;

	private int remoteDistributedSystemId;

	private GatewaySender gatewaySender;

	private List<GatewayEventFilter> eventFilters;

	private List<GatewayTransportFilter> transportFilters;

	private Boolean diskSynchronous;
	private Boolean enforceThreadsConnectToSameReceiver;
	private Boolean batchConflationEnabled;
	private Boolean groupTransactionEvents;
	private Boolean parallel;
	private Boolean persistent;

	private GatewaySender.OrderPolicy orderPolicy;

	@SuppressWarnings("rawtypes")
	private GatewayEventSubstitutionFilter eventSubstitutionFilter;

	private Integer alertThreshold;
	private Integer batchSize;
	private Integer batchTimeInterval;
	private Integer dispatcherThreads;
	private Integer maximumQueueMemory;
	private Integer socketBufferSize;
	private Integer socketReadTimeout;

	private List<GatewaySenderConfigurer> gatewaySenderConfigurers = Collections.emptyList();

	// TODO: Come up with better association and remove.
	private List<String> regions = new ArrayList<>();

	private String diskStoreReference;

	public GatewaySenderFactoryBean() { }

	/**
	 * Constructs an instance of the {@link GatewaySenderFactoryBean} class initialized with a reference to
	 * the GemFire {@link Cache} used to configured and initialized a GemFire {@link GatewaySender}.
	 *
	 * @param cache reference to the GemFire {@link Cache} used to create the GemFire {@link GatewaySender}.
	 * @see org.apache.geode.cache.Cache
	 */
	public GatewaySenderFactoryBean(GemFireCache cache) {
		super(cache);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doInit() {

		GatewaySenderFactory gatewaySenderFactory = resolveGatewaySenderFactory();

		stream(nullSafeIterable(this.gatewaySenderConfigurers).spliterator(), false)
			.forEach(it -> it.configure(getName(), this));

		Optional.ofNullable(getAlertThreshold()).ifPresent(gatewaySenderFactory::setAlertThreshold);
		Optional.ofNullable(getBatchConflationEnabled()).ifPresent(gatewaySenderFactory::setBatchConflationEnabled);
		Optional.ofNullable(getBatchSize()).ifPresent(gatewaySenderFactory::setBatchSize);
		Optional.ofNullable(getBatchTimeInterval()).ifPresent(gatewaySenderFactory::setBatchTimeInterval);

		Optional.ofNullable(getDiskStoreReference())
			.filter(StringUtils::hasText)
			.ifPresent(gatewaySenderFactory::setDiskStoreName);

		Optional.ofNullable(getDiskSynchronous()).ifPresent(gatewaySenderFactory::setDiskSynchronous);
		Optional.ofNullable(getDispatcherThreads()).ifPresent(gatewaySenderFactory::setDispatcherThreads);
		Optional.ofNullable(getEnforceThreadsConnectToSameReceiver())
			.ifPresent(gatewaySenderFactory::setEnforceThreadsConnectSameReceiver);

		CollectionUtils.nullSafeList(getEventFilters()).forEach(gatewaySenderFactory::addGatewayEventFilter);

		Optional.ofNullable(getEventSubstitutionFilter())
			.ifPresent(gatewaySenderFactory::setGatewayEventSubstitutionFilter);

		Optional.ofNullable(getGroupTransactionEvents()).ifPresent(gatewaySenderFactory::setGroupTransactionEvents);

		gatewaySenderFactory.setManualStart(isManualStart());

		Optional.ofNullable(getMaximumQueueMemory()).ifPresent(gatewaySenderFactory::setMaximumQueueMemory);
		Optional.ofNullable(getOrderPolicy()).ifPresent(gatewaySenderFactory::setOrderPolicy);

		gatewaySenderFactory.setParallel(isParallelGatewaySender());
		gatewaySenderFactory.setPersistenceEnabled(isPersistent());

		Optional.ofNullable(getSocketBufferSize()).ifPresent(gatewaySenderFactory::setSocketBufferSize);
		Optional.ofNullable(getSocketReadTimeout()).ifPresent(gatewaySenderFactory::setSocketReadTimeout);

		CollectionUtils.nullSafeList(getTransportFilters()).forEach(gatewaySenderFactory::addGatewayTransportFilter);

		GatewaySenderWrapper wrapper =
			new GatewaySenderWrapper(gatewaySenderFactory.create(getName(), getRemoteDistributedSystemId()));

        wrapper.setManualStart(isManualStart());
        this.gatewaySender = wrapper;
	}

	private GatewaySenderFactory resolveGatewaySenderFactory() {

		return this.factory != null
			? (GatewaySenderFactory) this.factory
			: this.cache.createGatewaySenderFactory();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GatewaySender getObject() throws Exception {
		return this.gatewaySender;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?> getObjectType() {

		return this.gatewaySender != null
			? this.gatewaySender.getClass()
			: GatewaySender.class;
	}

	public void setGatewaySender(@Nullable GatewaySender gatewaySender) {
		this.gatewaySender = gatewaySender;
	}

	public @Nullable GatewaySender getGatewaySender() {
		return this.gatewaySender;
	}

	public void setGatewaySenderConfigurers(@NonNull List<GatewaySenderConfigurer> gatewaySenderConfigurers) {
		this.gatewaySenderConfigurers = gatewaySenderConfigurers;
	}

	public void setAlertThreshold(Integer alertThreshold) {
		this.alertThreshold = alertThreshold;
	}

	public Integer getAlertThreshold() {
		return this.alertThreshold;
	}

	public void setBatchConflationEnabled(Boolean batchConflationEnabled) {
		this.batchConflationEnabled = batchConflationEnabled;
	}

	public Boolean getBatchConflationEnabled() {
		return this.batchConflationEnabled;
	}

	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}

	public Integer getBatchSize() {
		return this.batchSize;
	}

	public void setBatchTimeInterval(Integer batchTimeInterval) {
		this.batchTimeInterval = batchTimeInterval;
	}

	public Integer getBatchTimeInterval() {
		return this.batchTimeInterval;
	}

	public void setDiskStoreRef(String diskStoreRef) {
		setDiskStoreReference(diskStoreRef);
	}

	public void setDiskStoreReference(String diskStoreReference) {
		this.diskStoreReference = diskStoreReference;
	}

	public String getDiskStoreReference() {
		return this.diskStoreReference;
	}

	public void setDiskSynchronous(Boolean diskSynchronous) {
		this.diskSynchronous = diskSynchronous;
	}

	public Boolean getDiskSynchronous() {
		return this.diskSynchronous;
	}

	public void setDispatcherThreads(Integer dispatcherThreads) {
		this.dispatcherThreads = dispatcherThreads;
	}

	public Integer getDispatcherThreads() {
		return this.dispatcherThreads;
	}

	public void setEnforceThreadsConnectToSameReceiver(Boolean enforceThreadsConnectToSameReceiver) {
		this.enforceThreadsConnectToSameReceiver = enforceThreadsConnectToSameReceiver;
	}

	public Boolean getEnforceThreadsConnectToSameReceiver() {
		return this.enforceThreadsConnectToSameReceiver;
	}

	public void setEventFilters(List<GatewayEventFilter> eventFilters) {
		this.eventFilters = eventFilters;
	}

	public List<GatewayEventFilter> getEventFilters() {
		return this.eventFilters;
	}

	@SuppressWarnings("rawtypes")
	public void setEventSubstitutionFilter(GatewayEventSubstitutionFilter eventSubstitutionFilter) {
		this.eventSubstitutionFilter = eventSubstitutionFilter;
	}

	@SuppressWarnings("rawtypes")
	public GatewayEventSubstitutionFilter getEventSubstitutionFilter() {
		return this.eventSubstitutionFilter;
	}

	public void setGroupTransactionEvents(Boolean groupTransactionEvents) {
		this.groupTransactionEvents = groupTransactionEvents;
	}

	public Boolean getGroupTransactionEvents() {
		return this.groupTransactionEvents;
	}

	@Deprecated
	public void setManualStart(boolean manualStart) {
		this.manualStart = manualStart;
	}

	@Deprecated
	public void setManualStart(Boolean manualStart) {
		setManualStart(Boolean.TRUE.equals(manualStart));
	}

	public boolean isManualStart() {
		return this.manualStart;
	}

	public void setMaximumQueueMemory(Integer maximumQueueMemory) {
		this.maximumQueueMemory = maximumQueueMemory;
	}

	public Integer getMaximumQueueMemory() {
		return this.maximumQueueMemory;
	}

	public void setOrderPolicy(GatewaySender.OrderPolicy orderPolicy) {
		this.orderPolicy = orderPolicy;
	}

	public void setOrderPolicy(OrderPolicyType orderPolicy) {
		setOrderPolicy(orderPolicy != null ? orderPolicy.getOrderPolicy() : null);
	}

	public GatewaySender.OrderPolicy getOrderPolicy() {
		return this.orderPolicy;
	}

	public void setParallel(Boolean parallel) {
		this.parallel = parallel;
	}

	public boolean isParallelGatewaySender() {
		return Boolean.TRUE.equals(this.parallel);
	}

	public boolean isSerialGatewaySender() {
		return !isParallelGatewaySender();
	}

	public void setPersistent(Boolean persistent) {
		this.persistent = persistent;
	}

	public boolean isPersistent() {
		return Boolean.TRUE.equals(this.persistent);
	}

	public boolean isNotPersistent() {
		return !isPersistent();
	}

	public void setRemoteDistributedSystemId(int remoteDistributedSystemId) {
		this.remoteDistributedSystemId = remoteDistributedSystemId;
	}

	public void setRegions(String[] regions) {
		setRegions(Arrays.asList(ArrayUtils.nullSafeArray(regions, String.class)));
	}

	public void setRegions(List<String> regions) {
		this.regions.addAll(CollectionUtils.nullSafeList(regions));
	}

	public List<String> getRegions() {
		return Collections.unmodifiableList(this.regions);
	}

	public int getRemoteDistributedSystemId() {
		return this.remoteDistributedSystemId;
	}

	public void setSocketBufferSize(Integer socketBufferSize) {
		this.socketBufferSize = socketBufferSize;
	}

	public Integer getSocketBufferSize() {
		return this.socketBufferSize;
	}

	public void setSocketReadTimeout(Integer socketReadTimeout) {
		this.socketReadTimeout = socketReadTimeout;
	}

	public Integer getSocketReadTimeout() {
		return this.socketReadTimeout;
	}

	public void setTransportFilters(List<GatewayTransportFilter> transportFilters) {
		this.transportFilters = transportFilters;
	}

	public List<GatewayTransportFilter> getTransportFilters() {
		return this.transportFilters;
	}
}
