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
 * 2026-03-13: Created GemFire 10.0 JndiBinding implementation
 */

package org.springframework.data.gemfire.gud.driver.gemfire100;

import org.apache.geode.internal.jndi.JNDIInvoker;

import org.springframework.data.gemfire.gud.api.GudConfigProperty;
import org.springframework.data.gemfire.gud.api.GudJndiBinding;

import java.util.List;
import java.util.Map;

/**
 * GemFire 10.0 implementation of GudJndiBinding.
 * Delegates to GemFire's JNDIInvoker for JNDI data source management.
 */
public class GemFire100JndiBinding implements GudJndiBinding {

    @Override
    public void mapDatasource(Map<String, String> attributes, List<GudConfigProperty> props) {
        try {
            // Convert GudConfigProperty to the format expected by JNDIInvoker
            List<org.apache.geode.internal.datasource.ConfigProperty> nativeProps = 
                props.stream()
                    .map(p -> new org.apache.geode.internal.datasource.ConfigProperty(
                        p.getName(), p.getValue(), p.getType()))
                    .collect(java.util.stream.Collectors.toList());
            
            JNDIInvoker.mapDatasource(attributes, nativeProps);
        } catch (Exception e) {
            throw new RuntimeException("Failed to map datasource", e);
        }
    }

    @Override
    public void unmapDatasource(String jndiName) {
        try {
            JNDIInvoker.unMapDatasource(jndiName);
        } catch (javax.naming.NamingException e) {
            throw new RuntimeException("Failed to unmap datasource: " + jndiName, e);
        }
    }
}
