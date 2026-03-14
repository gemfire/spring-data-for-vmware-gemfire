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
 * 2026-03-11: Created GemFire 10.3 driver implementation
 * 2026-03-13: Added factory creation methods for cache creation
 * 2026-03-14: Added capability detection and exception translation for API evolution
 */

package org.springframework.data.gemfire.gud.driver.gemfire103;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.cache.CacheLoaderException;
import org.apache.geode.cache.CacheWriterException;
import org.apache.geode.cache.CommitConflictException;
import org.apache.geode.cache.DiskAccessException;
import org.apache.geode.cache.EntryDestroyedException;
import org.apache.geode.cache.EntryExistsException;
import org.apache.geode.cache.EntryNotFoundException;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionDestroyedException;
import org.apache.geode.cache.RegionExistsException;
import org.apache.geode.cache.TimeoutException;
import org.apache.geode.cache.TransactionException;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.query.QueryException;
import org.apache.geode.security.AuthenticationFailedException;

import org.springframework.data.gemfire.gud.api.GudAuthenticationFailedException;
import org.springframework.data.gemfire.gud.api.GudCache;
import org.springframework.data.gemfire.gud.api.GudCacheClosedException;
import org.springframework.data.gemfire.gud.api.GudCacheFactory;
import org.springframework.data.gemfire.gud.api.GudCacheLoaderException;
import org.springframework.data.gemfire.gud.api.GudCacheWriterException;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientCacheFactory;
import org.springframework.data.gemfire.gud.api.GudCommitConflictException;
import org.springframework.data.gemfire.gud.api.GudDiskAccessException;
import org.springframework.data.gemfire.gud.api.GudEntryDestroyedException;
import org.springframework.data.gemfire.gud.api.GudEntryExistsException;
import org.springframework.data.gemfire.gud.api.GudEntryNotFoundException;
import org.springframework.data.gemfire.gud.api.GudGemFireException;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudQueryException;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudRegionDestroyedException;
import org.springframework.data.gemfire.gud.api.GudRegionExistsException;
import org.springframework.data.gemfire.gud.api.GudTimeoutException;
import org.springframework.data.gemfire.gud.api.GudTransactionException;
import org.springframework.data.gemfire.gud.core.GudApiVersion;
import org.springframework.data.gemfire.gud.core.GudCapability;
import org.springframework.data.gemfire.gud.core.GudDriver;

/**
 * GUD driver implementation for GemFire 10.3.x.
 * Wraps native GemFire 10.3 types with GUD API interfaces.
 */
public class GemFire103Driver implements GudDriver {

    public static final String DRIVER_NAME = "gemfire-10.3";
    public static final String SUPPORTED_VERSION = "10.3";

    private static final Set<GudCapability> SUPPORTED_CAPABILITIES = Collections.unmodifiableSet(
        EnumSet.of(
            GudCapability.BASIC_CACHE_OPERATIONS,
            GudCapability.REGIONS,
            GudCapability.QUERIES,
            GudCapability.CONTINUOUS_QUERY,
            GudCapability.TRANSACTIONS,
            GudCapability.PDX_SERIALIZATION,
            GudCapability.FUNCTIONS,
            GudCapability.SECURITY_MANAGER,
            GudCapability.PER_SERVER_CONNECTION_LIMITS
        )
    );

    @Override
    public String getName() {
        return DRIVER_NAME;
    }

    @Override
    public String getSupportedVersion() {
        return SUPPORTED_VERSION;
    }

    @Override
    public GudApiVersion getMinimumApiVersion() {
        return GudApiVersion.V1_0;
    }

    @Override
    public GudApiVersion getMaximumApiVersion() {
        return GudApiVersion.V1_0;
    }

    @Override
    public boolean supportsCapability(GudCapability capability) {
        return SUPPORTED_CAPABILITIES.contains(capability);
    }

    @Override
    public Set<GudCapability> getCapabilities() {
        return SUPPORTED_CAPABILITIES;
    }

    @Override
    public GudClientCacheFactory createClientCacheFactory() {
        return new GemFire103ClientCacheFactory();
    }

    @Override
    public GudCacheFactory createCacheFactory() {
        return new GemFire103CacheFactory();
    }

    @Override
    public GudCache wrapCache(Object nativeCache) {
        if (nativeCache instanceof Cache) {
            return new GemFire103Cache((Cache) nativeCache);
        }
        throw new IllegalArgumentException("Expected Cache instance but got: " + 
            (nativeCache != null ? nativeCache.getClass().getName() : "null"));
    }

    @Override
    public GudClientCache wrapClientCache(Object nativeClientCache) {
        if (nativeClientCache instanceof ClientCache) {
            return new GemFire103ClientCache((ClientCache) nativeClientCache);
        }
        throw new IllegalArgumentException("Expected ClientCache instance but got: " + 
            (nativeClientCache != null ? nativeClientCache.getClass().getName() : "null"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> GudRegion<K, V> wrapRegion(Object nativeRegion) {
        if (nativeRegion instanceof Region) {
            return new GemFire103Region<>((Region<K, V>) nativeRegion);
        }
        throw new IllegalArgumentException("Expected Region instance but got: " + 
            (nativeRegion != null ? nativeRegion.getClass().getName() : "null"));
    }

    @Override
    public GudPool wrapPool(Object nativePool) {
        if (nativePool instanceof Pool) {
            return new GemFire103Pool((Pool) nativePool);
        }
        throw new IllegalArgumentException("Expected Pool instance but got: " + 
            (nativePool != null ? nativePool.getClass().getName() : "null"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Object gudObject) {
        if (gudObject instanceof NativeWrapper) {
            return ((NativeWrapper<T>) gudObject).getNative();
        }
        throw new IllegalArgumentException("Object is not a GUD wrapper: " + 
            (gudObject != null ? gudObject.getClass().getName() : "null"));
    }

    @Override
    public GudGemFireException translateException(Throwable nativeException) {
        if (nativeException instanceof CacheClosedException) {
            return new GudCacheClosedException(nativeException.getMessage(), nativeException);
        }
        if (nativeException instanceof RegionExistsException) {
            return new GudRegionExistsException(nativeException.getMessage(), nativeException);
        }
        if (nativeException instanceof RegionDestroyedException) {
            RegionDestroyedException rde = (RegionDestroyedException) nativeException;
            return new GudRegionDestroyedException(rde.getRegionFullPath(), rde.getMessage(), nativeException);
        }
        if (nativeException instanceof EntryNotFoundException) {
            return new GudEntryNotFoundException(nativeException.getMessage(), nativeException);
        }
        if (nativeException instanceof EntryExistsException) {
            return new GudEntryExistsException(nativeException.getMessage(), nativeException);
        }
        if (nativeException instanceof EntryDestroyedException) {
            return new GudEntryDestroyedException(nativeException.getMessage(), nativeException);
        }
        if (nativeException instanceof CacheLoaderException) {
            return new GudCacheLoaderException(nativeException.getMessage(), nativeException);
        }
        if (nativeException instanceof CacheWriterException) {
            return new GudCacheWriterException(nativeException.getMessage(), nativeException);
        }
        if (nativeException instanceof TimeoutException) {
            return new GudTimeoutException(nativeException.getMessage(), nativeException);
        }
        if (nativeException instanceof CommitConflictException) {
            return new GudCommitConflictException(nativeException.getMessage(), nativeException);
        }
        if (nativeException instanceof TransactionException) {
            return new GudTransactionException(nativeException.getMessage(), nativeException);
        }
        if (nativeException instanceof QueryException) {
            return new GudQueryException(nativeException.getMessage(), nativeException);
        }
        if (nativeException instanceof DiskAccessException) {
            return new GudDiskAccessException(nativeException.getMessage(), nativeException);
        }
        if (nativeException instanceof AuthenticationFailedException) {
            return new GudAuthenticationFailedException(nativeException.getMessage(), nativeException);
        }
        return new GudGemFireException(nativeException.getMessage(), nativeException);
    }
}
