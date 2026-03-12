/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudDistributedSystem interface as 1:1 mapping of GemFire DistributedSystem
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Properties;
import java.util.Set;

/**
 * GUD API abstraction for GemFire DistributedSystem interface.
 * Represents the distributed system connection.
 */
public interface GudDistributedSystem {

    static GudDistributedSystem getAnyInstance() {
        throw new UnsupportedOperationException("Static method should be implemented by driver");
    }

    String getName();

    GudDistributedMember getDistributedMember();

    Set<GudDistributedMember> getAllOtherMembers();

    Set<GudDistributedMember> getGroupMembers(String group);

    GudDistributedMember findDistributedMember(String name);

    GudDistributedMember findDistributedMemberByName(String name);

    Properties getProperties();

    String getSystemId();

    boolean isConnected();

    boolean isReconnecting();

    void disconnect();

    void waitUntilReconnected(long time, java.util.concurrent.TimeUnit unit) throws InterruptedException;

    void stopReconnecting();

    GudDistributedSystem getReconnectedSystem();
}
