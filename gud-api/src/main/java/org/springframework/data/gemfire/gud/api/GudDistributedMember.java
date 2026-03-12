/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudDistributedMember interface as 1:1 mapping of GemFire DistributedMember
 */

package org.springframework.data.gemfire.gud.api;

import java.net.InetAddress;
import java.util.List;
import java.util.Set;

/**
 * GUD API abstraction for GemFire DistributedMember interface.
 * Represents a member of a distributed system.
 */
public interface GudDistributedMember {

    String getName();

    String getId();

    String getHost();

    InetAddress getInetAddress();

    int getProcessId();

    Set<String> getRoles();

    List<String> getGroups();

    int getVmViewId();

    long getUniqueId();

    String getDurableClientId();

    int getDurableClientTimeout();
}
