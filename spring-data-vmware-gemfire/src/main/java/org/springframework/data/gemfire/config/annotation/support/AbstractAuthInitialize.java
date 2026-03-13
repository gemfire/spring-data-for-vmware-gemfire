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

package org.springframework.data.gemfire.config.annotation.support;

import java.util.Optional;
import java.util.Properties;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.data.gemfire.gud.api.GudAuthInitialize;
import org.springframework.data.gemfire.gud.api.GudAuthenticationFailedException;
import org.springframework.data.gemfire.gud.api.GudDistributedMember;
import org.springframework.data.gemfire.gud.api.GudLogWriter;
import org.springframework.data.gemfire.support.WiringDeclarableSupport;
import org.springframework.lang.Nullable;

/**
 * Abstract class and basic implementation of the {@link GudAuthInitialize} interface used to authenticate a client
 * or peer with a secure Apache Geode cluster.
 *
 * @author John Blum
 * @see Properties
 * @see GudDistributedMember
 * @see GudAuthInitialize
 * @see EnvironmentAware
 * @see Environment
 * @see WiringDeclarableSupport
 * @since 2.0.0
 */
public abstract class AbstractAuthInitialize extends WiringDeclarableSupport
		implements GudAuthInitialize, EnvironmentAware {

	@Nullable
	private Environment environment;

	/**
	 * Sets a reference to the configured Spring {@link Environment}.
	 *
	 * @param environment reference to the configured Spring {@link Environment}.
	 * @see Environment
	 */
	@Override
	@SuppressWarnings("all")
	public void setEnvironment(@Nullable Environment environment) {
		this.environment = environment;
	}

	/**
	 * Get an {@link Optional} reference to the configured Spring {@link Environment}.
	 *
	 * @return an {@link Optional} reference to the configured Spring {@link Environment}.
	 * @see Environment
	 * @see Optional
	 */
	protected Optional<Environment> getEnvironment() {
		return Optional.ofNullable(this.environment);
	}

	/**
	 * Initializes this Apache Geode component by auto-wiring (configuring) any dependencies managed by
	 * the Spring container required during authentication.
	 *
	 * @param systemLogWriter {@link GudLogWriter} for system output.
	 * @param securityLogWriter {@link GudLogWriter} for security output.
	 * @throws GudAuthenticationFailedException if this Apache Geode node could not be authenticated with
	 * the Apache Geode distributed system (cluster).
	 * @see #doInit()
	 */
	@Override
	public final void init(GudLogWriter systemLogWriter, GudLogWriter securityLogWriter) {
		doInit();
	}

	protected void doInit() { }

	/**
	 * Gets the security credentials used to authenticate this Apache Geode node
	 * with the Apache Geode distributed system (cluster).
	 *
	 * @param properties Apache Geode {@link Properties} to configure with authentication credentials
	 * used during the authentication request.
	 * @param distributedMember {@link GudDistributedMember} representing this Apache Geode node.
	 * @param isPeer boolean value indicating whether this Apache Geode node is joining the cluster
	 * as a peer or a client.
	 * @return the given Apache Geode {@link Properties}.
	 * @throws GudAuthenticationFailedException if this Apache Geode node could not be authenticated with
	 * the Apache Geode distributed system (cluster).
	 * @see GudDistributedMember
	 * @see #doGetCredentials(Properties)
	 * @see Properties
	 */
	@Override
	public final Properties getCredentials(Properties properties, GudDistributedMember distributedMember, boolean isPeer) {
		return doGetCredentials(properties);
	}

	protected abstract Properties doGetCredentials(Properties properties);

}
