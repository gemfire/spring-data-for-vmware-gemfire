// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire;

import org.apache.geode.cache.DataPolicy;

import org.springframework.core.convert.converter.Converter;

/**
 * The DataPolicyConverter class converts String values into GemFire DataPolicy enumerated values.
 *
 * @author David Turanski
 * @author John Blum
 * @see Converter
 * @see DataPolicy
 */
public class DataPolicyConverter implements Converter<String, DataPolicy> {

	static enum Policy {
		DEFAULT, EMPTY, NORMAL, PRELOADED, PARTITION, PERSISTENT_PARTITION, REPLICATE, PERSISTENT_REPLICATE;

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

		public DataPolicy toDataPolicy() {
			switch (this) {
				case EMPTY:
					return DataPolicy.EMPTY;
				case NORMAL:
					return DataPolicy.NORMAL;
				case PRELOADED:
					return DataPolicy.PRELOADED;
				case PARTITION :
					return DataPolicy.PARTITION;
				case PERSISTENT_PARTITION:
					return DataPolicy.PERSISTENT_PARTITION;
				case REPLICATE:
					return DataPolicy.REPLICATE;
				case PERSISTENT_REPLICATE:
					return DataPolicy.PERSISTENT_REPLICATE;
				case DEFAULT:
				default:
					return DataPolicy.DEFAULT;
			}
		}
	}

	@Override
	public DataPolicy convert(String policyValue) {
		Policy policy = Policy.getValue(policyValue);
		return (policy == null ? null : policy.toDataPolicy());
	}

}
