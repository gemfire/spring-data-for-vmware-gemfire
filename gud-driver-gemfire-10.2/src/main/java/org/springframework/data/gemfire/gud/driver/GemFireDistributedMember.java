/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GemFire 10.2 DistributedMember adapter
 */

package org.springframework.data.gemfire.gud.driver;

import java.net.InetAddress;
import java.util.List;
import java.util.Set;

import org.apache.geode.distributed.DistributedMember;

import org.springframework.data.gemfire.gud.api.GudDistributedMember;

/**
 * GUD API adapter for GemFire 10.2 DistributedMember.
 */
public class GemFireDistributedMember implements GudDistributedMember, NativeWrapper<DistributedMember> {

    private final DistributedMember nativeMember;

    public GemFireDistributedMember(DistributedMember nativeMember) {
        this.nativeMember = nativeMember;
    }

    @Override
    public DistributedMember getNative() {
        return nativeMember;
    }

    @Override
    public String getName() {
        return nativeMember.getName();
    }

    @Override
    public String getId() {
        return nativeMember.getId();
    }

    @Override
    public String getHost() {
        return nativeMember.getHost();
    }

    @Override
    public InetAddress getInetAddress() {
        // getInetAddress not available in 10.2 - return null
        return null;
    }

    @Override
    public int getProcessId() {
        return nativeMember.getProcessId();
    }

    @Override
    @SuppressWarnings("deprecation")
    public Set<String> getRoles() {
        // Roles are deprecated but still available
        return nativeMember.getRoles().stream()
            .map(Object::toString)
            .collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public List<String> getGroups() {
        return nativeMember.getGroups();
    }

    @Override
    public int getVmViewId() {
        // getVmViewId not available in 10.2 - return -1
        return -1;
    }

    @Override
    public long getUniqueId() {
        // GemFire 10.2 may not have getUniqueId - return hash
        return nativeMember.hashCode();
    }

    @Override
    public String getDurableClientId() {
        // getDurableClientId not available directly in 10.2 - return empty
        return "";
    }

    @Override
    public int getDurableClientTimeout() {
        // getDurableClientTimeout not available directly in 10.2 - return 0
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof GemFireDistributedMember) {
            return nativeMember.equals(((GemFireDistributedMember) obj).nativeMember);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return nativeMember.hashCode();
    }

    @Override
    public String toString() {
        return nativeMember.toString();
    }
}
