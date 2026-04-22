/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-17: In-memory mock GudJndiBinding
 */

package org.springframework.data.gemfire.gud.driver.mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.gemfire.gud.api.GudConfigProperty;
import org.springframework.data.gemfire.gud.api.GudJndiBinding;

/**
 * In-memory mock {@link GudJndiBinding}.  Remembers mapped datasources so tests can
 * verify that {@code mapDatasource(...)} was invoked with the expected attributes.
 */
public class MockGudJndiBinding implements GudJndiBinding {

    private final ConcurrentHashMap<String, Map<String, String>> bindings = new ConcurrentHashMap<>();

    @Override
    public void mapDatasource(Map<String, String> attributes, List<GudConfigProperty> props) {
        String jndiName = attributes != null ? attributes.get("jndi-name") : null;
        if (jndiName != null) {
            bindings.put(jndiName, new HashMap<>(attributes));
        }
    }

    @Override
    public void unmapDatasource(String jndiName) {
        bindings.remove(jndiName);
    }

    /**
     * Returns the attribute map recorded for the given JNDI name, or {@code null} if no
     * datasource has been mapped with that name.
     *
     * @param jndiName the JNDI name
     * @return the attribute map, or {@code null}
     */
    public Map<String, String> getBinding(String jndiName) {
        Map<String, String> attrs = bindings.get(jndiName);
        return attrs == null ? null : new HashMap<>(attrs);
    }
}
