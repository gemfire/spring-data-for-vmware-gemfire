/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GemFire 10.3 DistributedSystem adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire103;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.DistributedSystem;

import org.springframework.data.gemfire.gud.api.GudDistributedMember;
import org.springframework.data.gemfire.gud.api.GudDistributedSystem;

/**
 * GUD API adapter for GemFire 10.3 DistributedSystem.
 */
public class GemFire103DistributedSystem implements GudDistributedSystem, NativeWrapper<DistributedSystem> {

    private final DistributedSystem nativeSystem;

    public GemFire103DistributedSystem(DistributedSystem nativeSystem) {
        this.nativeSystem = nativeSystem;
    }

    @Override
    public DistributedSystem getNative() {
        return nativeSystem;
    }

    @Override
    public String getName() {
        return nativeSystem.getName();
    }

    @Override
    public GudDistributedMember getDistributedMember() {
        return new GemFire103DistributedMember(nativeSystem.getDistributedMember());
    }

    @Override
    public Set<GudDistributedMember> getAllOtherMembers() {
        return nativeSystem.getAllOtherMembers().stream()
            .map(GemFire103DistributedMember::new)
            .collect(Collectors.toSet());
    }

    @Override
    public Set<GudDistributedMember> getGroupMembers(String group) {
        return nativeSystem.getGroupMembers(group).stream()
            .map(GemFire103DistributedMember::new)
            .collect(Collectors.toSet());
    }

    @Override
    public GudDistributedMember findDistributedMember(String name) {
        DistributedMember member = nativeSystem.findDistributedMember(name);
        return member != null ? new GemFire103DistributedMember(member) : null;
    }

    @Override
    public GudDistributedMember findDistributedMemberByName(String name) {
        // In GemFire 10.3, use findDistributedMember
        return findDistributedMember(name);
    }

    @Override
    public Properties getProperties() {
        return nativeSystem.getProperties();
    }

    @Override
    public String getSystemId() {
        // GemFire 10.3 may not have getSystemId - return name
        return getName();
    }

    @Override
    public boolean isConnected() {
        return nativeSystem.isConnected();
    }

    @Override
    public boolean isReconnecting() {
        return nativeSystem.isReconnecting();
    }

    @Override
    public void disconnect() {
        nativeSystem.disconnect();
    }

    @Override
    public void waitUntilReconnected(long time, TimeUnit unit) throws InterruptedException {
        nativeSystem.waitUntilReconnected(time, unit);
    }

    @Override
    public void stopReconnecting() {
        nativeSystem.stopReconnecting();
    }

    @Override
    public GudDistributedSystem getReconnectedSystem() {
        DistributedSystem reconnected = nativeSystem.getReconnectedSystem();
        return reconnected != null ? new GemFire103DistributedSystem(reconnected) : null;
    }
}
