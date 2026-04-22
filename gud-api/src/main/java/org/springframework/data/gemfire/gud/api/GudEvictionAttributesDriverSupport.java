/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-17: Reflectively delegates GudEvictionAttributes static factories to GudDriver (gud-api cannot depend on gud-core)
 */

package org.springframework.data.gemfire.gud.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Invokes eviction-attribute factory methods on the default {@code GudDriver}.
 * <p>
 * {@code gud-api} cannot depend on {@code gud-core} at compile time; this type uses the same
 * reflective bootstrap pattern as {@link GudCacheProvider#ensureInitialized()}.
 */
final class GudEvictionAttributesDriverSupport {

    private static final String DRIVER_MANAGER = "org.springframework.data.gemfire.gud.core.GudDriverManager";

    private GudEvictionAttributesDriverSupport() {
    }

    static GudEvictionAttributes invoke(String methodName, Class<?>[] parameterTypes, Object[] arguments) {
        try {
            Class<?> driverManagerClass = Class.forName(DRIVER_MANAGER);
            driverManagerClass.getMethod("loadDrivers").invoke(null);
            Object driver = driverManagerClass.getMethod("getDefaultDriver").invoke(null);
            Method method = driver.getClass().getMethod(methodName, parameterTypes);
            return (GudEvictionAttributes) method.invoke(driver, arguments);
        } catch (InvocationTargetException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new UnsupportedOperationException("Must be implemented by driver", cause);
        } catch (ReflectiveOperationException | LinkageError ex) {
            throw new UnsupportedOperationException("Must be implemented by driver", ex);
        }
    }
}
