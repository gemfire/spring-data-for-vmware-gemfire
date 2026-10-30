// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi.config;

import java.util.Objects;

/** Region subscription policy. The no-argument form uses {@link InterestPolicy#DEFAULT}, whereas client regions
 * install {@link InterestPolicy#ALL}; read the effective value from the region. A null policy is stored as given,
 * so {@code hashCode} and {@code toString} fail for it. Native serialization is a private adapter concern.
 */
public final class SubscriptionAttributes {
	private final InterestPolicy interestPolicy;
	public SubscriptionAttributes() { this(InterestPolicy.DEFAULT); }
	public SubscriptionAttributes(InterestPolicy interestPolicy) { this.interestPolicy = interestPolicy; }
	public InterestPolicy getInterestPolicy() { return interestPolicy; }
	@Override public boolean equals(Object other) {
		return this == other || other instanceof SubscriptionAttributes that && Objects.equals(interestPolicy, that.interestPolicy);
	}
	@Override public int hashCode() { return 17 * 37 + interestPolicy.hashCode(); }
	@Override public String toString() { return "SubscriptionAttributes policy=" + interestPolicy; }
}
