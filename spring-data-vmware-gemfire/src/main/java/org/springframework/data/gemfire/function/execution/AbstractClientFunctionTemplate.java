/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.function.execution;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalStateException;

import java.util.Optional;

import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.data.gemfire.client.PoolResolver;
import org.springframework.data.gemfire.client.support.PoolManagerPoolResolver;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudExecution;
import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudRegionService;
import org.springframework.data.gemfire.util.CacheUtils;
import org.springframework.util.StringUtils;

/**
 * Abstract base class for Apache Geode client-side {@link GudFunction} {@link GudExecution}.
 *
 * @author John Blum
 * @see GudRegionService
 * @see GudClientCache
 * @see GudPool
 * @see GudExecution
 * @see GudFunction
 * @since 2.3.0
 */
@SuppressWarnings("unused")
public abstract class AbstractClientFunctionTemplate extends AbstractFunctionTemplate {

	protected static final PoolResolver DEFAULT_POOL_RESOLVER = new PoolManagerPoolResolver();

	private GudPool pool;

	private PoolResolver poolResolver = DEFAULT_POOL_RESOLVER;

	private GudRegionService regionService;

	private String poolName;

	public AbstractClientFunctionTemplate(GudRegionService regionService) {
		this.regionService = regionService;
	}

	public AbstractClientFunctionTemplate(GudPool pool) {
		this.pool = pool;
	}

	public AbstractClientFunctionTemplate(String poolName) {
		this.poolName = poolName;
	}

	public void setPool(GudPool pool) {
		this.pool = pool;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	public void setPoolResolver(PoolResolver poolResolver) {
		this.poolResolver = poolResolver;
	}

	protected PoolResolver getPoolResolver() {

		PoolResolver poolResolver = this.poolResolver;

		return poolResolver != null ? poolResolver : DEFAULT_POOL_RESOLVER;
	}

	protected Object resolveRequiredGemFireObject() {
		return Optional.<Object>ofNullable(resolvePool()).orElseGet(this::resolveClientCache);
	}

	/**
	 * @deprecated as of 2.3.0; Use {@link #resolveRegionService()}.
	 */
	@Deprecated
	protected GudClientCache resolveClientCache() {
		return (GudClientCache) resolveRegionService();
	}

	protected GudPool resolvePool() {

		if (this.pool == null) {
			this.pool = resolveNamedPool();
		}

		return this.pool;
	}

	protected GudPool resolveDefaultPool() {

		return Optional.ofNullable(getPoolResolver().resolve(GemfireUtils.DEFAULT_POOL_NAME))
			.orElseThrow(() -> newIllegalStateException("DEFAULT Pool is not present"));
	}

	protected GudPool resolveNamedPool() {

		if (StringUtils.hasText(this.poolName)) {
			this.pool = Optional.ofNullable(getPoolResolver().resolve(this.poolName))
				.orElseThrow(() -> newIllegalStateException("Pool with name [%s] is not present",
					this.poolName));
		}

		return this.pool;
	}

	protected GudRegionService resolveRegionService() {

		GudRegionService resolvedRegionService = this.regionService != null
			? this.regionService
			: CacheUtils.getClientCache();

		return Optional.ofNullable(resolvedRegionService)
			.orElseThrow(() -> newIllegalStateException("ClientCache is not present"));
	}

	@Override
	protected AbstractFunctionExecution getFunctionExecution() {

		Object gemfireObject = resolveRequiredGemFireObject();

		return gemfireObject instanceof GudPool
			? newFunctionExecutionUsingPool((GudPool) gemfireObject)
			: newFunctionExecutionUsingRegionService((GudRegionService) gemfireObject);
	}

	protected abstract AbstractFunctionExecution newFunctionExecutionUsingPool(GudPool pool);

	protected abstract AbstractFunctionExecution newFunctionExecutionUsingRegionService(GudRegionService regionService);

}
