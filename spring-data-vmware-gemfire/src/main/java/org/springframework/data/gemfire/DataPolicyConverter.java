/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Migrated to GUD API types
 */

package org.springframework.data.gemfire;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.gemfire.gud.api.GudDataPolicy;

/**
 * The DataPolicyConverter class converts String values into GemFire DataPolicy enumerated values.
 *
 * @author David Turanski
 * @author John Blum
 * @see Converter
 * @see GudDataPolicy
 */
public class DataPolicyConverter implements Converter<String, GudDataPolicy> {

	enum Policy {
		DEFAULT, EMPTY, NORMAL, PRELOADED;

		private static String toUpperCase(String value) {
			return (value == null ? null : value.toUpperCase());
		}

		public static Policy getValue(String value) {
			try {
				return valueOf(toUpperCase(value));
			}
			catch (Exception e) {
				return null;
			}
		}

		public GudDataPolicy toDataPolicy() {
			switch (this) {
				case EMPTY:
					return GudDataPolicy.EMPTY;
				case NORMAL:
					return GudDataPolicy.NORMAL;
				case PRELOADED:
					return GudDataPolicy.PRELOADED;
				case DEFAULT:
				default:
					return GudDataPolicy.DEFAULT;
			}
		}
	}

	@Override
	public GudDataPolicy convert(String policyValue) {
		Policy policy = Policy.getValue(policyValue);
		return (policy == null ? null : policy.toDataPolicy());
	}

}
