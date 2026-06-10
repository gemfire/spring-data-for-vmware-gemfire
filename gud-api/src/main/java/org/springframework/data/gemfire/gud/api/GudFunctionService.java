// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudFunctionService interface as 1:1 mapping of GemFire FunctionService
 * 2026-03-13: Changed to abstract class with static methods
 * 2026-06-06: Refactored to delegate pattern — static methods now dispatch through a
 *             registered GudFunctionService instance; cleanup methods (getRegisteredFunctions,
 *             registerFunction, unregisterFunction, isRegistered) are safe no-ops when no
 *             delegate is registered; on*() and getFunction() still require a delegate.
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * GUD API abstraction for GemFire FunctionService.
 * Entry point for function execution APIs.
 *
 * <p>Static methods dispatch through a registered {@link GudFunctionService} delegate.
 * Call {@link #register(GudFunctionService)} once per JVM (typically done automatically
 * by {@code GudDriverManager} when a driver is loaded via ServiceLoader).
 *
 * <p>Cleanup methods ({@link #getRegisteredFunctions()}, {@link #registerFunction},
 * {@link #unregisterFunction}, {@link #isRegistered}) return safe defaults when no delegate
 * is registered so that test-teardown code never throws.  Execution methods
 * ({@link #onRegion}, {@link #onServer}, etc.) require a registered delegate and throw
 * {@link UnsupportedOperationException} when none is present.
 */
public abstract class GudFunctionService {

    private static volatile GudFunctionService delegate;

    /**
     * Registers the driver-provided {@link GudFunctionService} implementation.
     * Pass {@code null} to clear the delegate (e.g. during test-framework teardown).
     *
     * @param implementation the concrete implementation to register, or {@code null} to clear
     */
    public static void register(GudFunctionService implementation) {
        delegate = implementation;
    }

    // ===== Execution methods — require a registered delegate =====

    public static GudExecution<?, ?, ?> onRegion(GudRegion<?, ?> region) {
        requireDelegate("onRegion");
        return delegate.doOnRegion(region);
    }

    public static GudExecution<?, ?, ?> onServer(GudPool pool) {
        requireDelegate("onServer(GudPool)");
        return delegate.doOnServerWithPool(pool);
    }

    public static GudExecution<?, ?, ?> onServer(GudRegionService regionService) {
        requireDelegate("onServer(GudRegionService)");
        return delegate.doOnServerWithRegionService(regionService);
    }

    public static GudExecution<?, ?, ?> onServers(GudPool pool) {
        requireDelegate("onServers(GudPool)");
        return delegate.doOnServersWithPool(pool);
    }

    public static GudExecution<?, ?, ?> onServers(GudRegionService regionService) {
        requireDelegate("onServers(GudRegionService)");
        return delegate.doOnServersWithRegionService(regionService);
    }

    public static GudExecution<?, ?, ?> onMember(GudDistributedMember member) {
        requireDelegate("onMember(GudDistributedMember)");
        return delegate.doOnMemberWithDistributedMember(member);
    }

    public static GudExecution<?, ?, ?> onMembers(Set<GudDistributedMember> members) {
        requireDelegate("onMembers(Set)");
        return delegate.doOnMembersWithSet(members);
    }

    public static GudExecution<?, ?, ?> onMembers(String... groups) {
        requireDelegate("onMembers(String...)");
        return delegate.doOnMembersWithGroups(groups);
    }

    public static GudExecution<?, ?, ?> onMember(String... groups) {
        requireDelegate("onMember(String...)");
        return delegate.doOnMemberWithGroups(groups);
    }

    public static GudFunction getFunction(String functionId) {
        requireDelegate("getFunction");
        return delegate.doGetFunction(functionId);
    }

    // ===== Cleanup methods — safe no-ops / defaults when no delegate =====

    public static void registerFunction(GudFunction function) {
        if (delegate != null) {
            delegate.doRegisterFunction(function);
        }
    }

    public static void unregisterFunction(String functionId) {
        if (delegate != null) {
            delegate.doUnregisterFunction(functionId);
        }
    }

    public static boolean isRegistered(String functionId) {
        return delegate != null && delegate.doIsRegistered(functionId);
    }

    public static Map<String, GudFunction> getRegisteredFunctions() {
        return delegate != null ? delegate.doGetRegisteredFunctions() : Collections.emptyMap();
    }

    // ===== Abstract instance methods — implemented by each driver's concrete subclass =====

    protected abstract GudExecution<?, ?, ?> doOnRegion(GudRegion<?, ?> region);

    protected abstract GudExecution<?, ?, ?> doOnServerWithPool(GudPool pool);

    protected abstract GudExecution<?, ?, ?> doOnServerWithRegionService(GudRegionService regionService);

    protected abstract GudExecution<?, ?, ?> doOnServersWithPool(GudPool pool);

    protected abstract GudExecution<?, ?, ?> doOnServersWithRegionService(GudRegionService regionService);

    protected abstract GudExecution<?, ?, ?> doOnMemberWithDistributedMember(GudDistributedMember member);

    protected abstract GudExecution<?, ?, ?> doOnMembersWithSet(Set<GudDistributedMember> members);

    protected abstract GudExecution<?, ?, ?> doOnMembersWithGroups(String... groups);

    protected abstract GudExecution<?, ?, ?> doOnMemberWithGroups(String... groups);

    protected abstract GudFunction doGetFunction(String functionId);

    protected abstract void doRegisterFunction(GudFunction function);

    protected abstract void doUnregisterFunction(String functionId);

    protected abstract boolean doIsRegistered(String functionId);

    protected abstract Map<String, GudFunction> doGetRegisteredFunctions();

    // ===== Internal helpers =====

    private static void requireDelegate(String method) {
        if (delegate == null) {
            throw new UnsupportedOperationException(
                "GudFunctionService." + method + " requires a GUD driver to be registered. "
                    + "Ensure a driver module is on the classpath and has been loaded.");
        }
    }
}
