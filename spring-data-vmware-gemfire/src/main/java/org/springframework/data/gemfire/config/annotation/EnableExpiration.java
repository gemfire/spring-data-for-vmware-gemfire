/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.springframework.data.gemfire.config.annotation.EnableExpiration.ExpirationType.IDLE_TIMEOUT;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.geode.cache.Region;

import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.expiration.Expiration;
import org.springframework.data.gemfire.expiration.ExpirationActionType;
import org.springframework.data.gemfire.expiration.IdleTimeoutExpiration;
import org.springframework.data.gemfire.expiration.TimeToLiveExpiration;

/**
 * The {@link EnableExpiration} annotation marks a Spring {@link org.springframework.context.annotation.Configuration @Configuration}
 * annotated class to enable {@link Region} entry expiration for individual entries.  Note, this annotation does not
 * cover {@link Region} expiration; {@link Region} expiration must be configure on the {@link Region} definition itself.
 *
 * @author John Blum
 * @see org.apache.geode.cache.Region
 * @see org.springframework.context.annotation.Import
 * @see org.springframework.data.gemfire.config.annotation.ExpirationConfiguration
 * @see org.springframework.data.gemfire.expiration.Expiration
 * @see org.springframework.data.gemfire.expiration.ExpirationActionType
 * @see org.springframework.data.gemfire.expiration.IdleTimeoutExpiration
 * @see org.springframework.data.gemfire.expiration.TimeToLiveExpiration
 * @see <a href="https://docs.spring.io/spring-data-gemfire/docs/current/reference/html/#bootstrap:region:expiration:annotation">Annotation-based Data Expiration</a>
 * @see <a href="https://geode.apache.org/docs/guide/113/developing/expiration/chapter_overview.html">Geode Expiration</a>
 * @since 1.9.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(ExpirationConfiguration.class)
@SuppressWarnings({ "unused" })
public @interface EnableExpiration {

	/**
	 * Defines individual {@link Region} Expiration policies or customizes the default Expiration policy
	 * for all {@link Region Regions}.
	 *
	 * Defaults to empty.
	 */
	ExpirationPolicy[] policies() default {};

	/**
	 * Definition for a specific Expiration policy that can be applied to 1 or more {@link Region Regions}.
	 *
	 * An Expiration policy defines the expiration timeout and expiration action to take when
	 * an {@link Region} entry times out.
	 *
	 * Additionally, the Expiration policy defines the algorithm to use (e.g. Idle Timeout (TTI) or Time-To-Live (TTL),
	 * or both) to determine if and when an {@link Region} entry has timed out.
	 */
	@interface ExpirationPolicy {

		/**
		 * Specifies the timeout used to determine when a {@link Region} entry expires.
		 *
		 * This value of this attribute determines the "default" timeout used if no specific timeout was specified.
		 * A specific timeout is determined by {@link Expiration#timeout()}, {@link IdleTimeoutExpiration#timeout()}
		 * or {@link TimeToLiveExpiration#timeout()} attribute on the application domain object.
		 *
		 * See the SDG Reference Guide for more details...
		 *
		 * @see <a href="https://docs.spring.io/spring-data-gemfire/docs/current/reference/html/#bootstrap:region:expiration:annotation">Annotation-based Data Expiration</a>
		 */
		int timeout();

		/**
		 * Specifies the action taken when a {@link Region} entry expires.
		 *
		 * This value of this attribute determines the "default" action taken if no specific action was specified.
		 * The specific action is determined by {@link Expiration#action()}, {@link IdleTimeoutExpiration#action()}
		 * or {@link TimeToLiveExpiration#action()} attribute on the application domain object.
		 *
		 * See the SDG Reference Guide for more details...
		 *
		 * @see <a href="https://docs.spring.io/spring-data-gemfire/docs/current/reference/html/#bootstrap:region:expiration:annotation">Annotation-based Data Expiration</a>
		 */
		ExpirationActionType action();

		/**
		 * Names of specific {@link Region Regions} on which this Expiration policy is applied.
		 *
		 * If no {@link Region} names are specified then this Expiration policy will apply to
		 * all {@link Region Regions} declared in the Spring context.
		 *
		 * Defaults to all {@link Region Regions}.
		 */
		String[] regionNames() default {};

		/**
		 * Types of Expiration algorithms (Idle Timeout (TTI) or Time to Live (TTL)) configured and used by
		 * {@link Region Region(s)} to expire entries.
		 *
		 * Defaults to Idle Timeout (TTI).
		 *
		 * @see org.springframework.data.gemfire.config.annotation.EnableExpiration.ExpirationType
		 */
		ExpirationType[] types() default { IDLE_TIMEOUT };

	}

	/**
	 * {@link ExpirationType} defines different types of GemFire/Geode Expiration policies such as
	 * (Entry) Idle Timeout (TTI) and (Entry) Time to Live (TTL).
	 *
	 * @see <a href="https://geode.apache.org/docs/guide/113/developing/expiration/chapter_overview.html">Geode Expiration</a>
	 */
	enum ExpirationType {

		IDLE_TIMEOUT("TTI"),
		TIME_TO_LIVE("TTL");

		private final String abbreviation;

		/**
		 * Factory method to lookup an appropriate {@link ExpirationType} based on an abbreviation.
		 *
		 * @param abbreviation abbreviation used to lookup the appropriate {@link ExpirationType}.
		 * @return an {@link ExpirationType} matching the abbreviation or {@literal null} if the abbreviation
		 * does not match an {@link ExpirationType}.
		 * @see org.springframework.data.gemfire.config.annotation.EnableExpiration.ExpirationType
		 * @see #values()
		 * @see #abbreviation()
		 */
		static ExpirationType valueOfAbbreviation(String abbreviation) {

			for (ExpirationType expirationType : values()) {
				if (expirationType.abbreviation().equalsIgnoreCase(abbreviation)) {
					return expirationType;
				}
			}

			return null;
		}

		/**
		 * Constructs a new instance of {@link ExpirationType} initialized with the given abbreviation.
		 *
		 * @param abbreviation {@link String} indicating the {@link ExpirationType} abbreviation.
		 */
		ExpirationType(String abbreviation) {
			this.abbreviation = abbreviation;
		}

		/**
		 * Returns the abbreviation for this {@link ExpirationType}.
		 *
		 * @return a {@link String} with the {@link ExpirationType} abbreviation.
		 */
		protected String abbreviation() {
			return this.abbreviation;
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public String toString() {
			return String.format("%1$s (%2$s)", name(), abbreviation());
		}
	}
}
