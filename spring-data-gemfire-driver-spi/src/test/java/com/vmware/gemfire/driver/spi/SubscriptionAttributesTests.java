// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.vmware.gemfire.driver.spi.config.InterestPolicy;
import com.vmware.gemfire.driver.spi.config.SubscriptionAttributes;

class SubscriptionAttributesTests {
	@Test void defaultPolicyIsCacheContentAndPredicatesUseIdentity() {
		assertThat(InterestPolicy.DEFAULT).isSameAs(InterestPolicy.CACHE_CONTENT);
		assertThat(InterestPolicy.DEFAULT.isDefault()).isTrue();
		assertThat(InterestPolicy.ALL.isAll()).isTrue();
		assertThat(InterestPolicy.ALL.isCacheContent()).isFalse();
		assertThat(InterestPolicy.ALL.isDefault()).isFalse();
		assertThat(InterestPolicy.valueOf("ALL")).isSameAs(InterestPolicy.ALL);
		assertThatThrownBy(() -> InterestPolicy.valueOf("UNKNOWN")).isInstanceOf(IllegalArgumentException.class);
	}
	@Test void noArgumentConstructorUsesDefaultWhileExplicitPolicyIsRetained() {
		assertThat(new SubscriptionAttributes().getInterestPolicy()).isSameAs(InterestPolicy.CACHE_CONTENT);
		assertThat(new SubscriptionAttributes(InterestPolicy.ALL).getInterestPolicy()).isSameAs(InterestPolicy.ALL);
	}
	@Test void equalityHashAndStringFollowThePolicy() {
		var all = new SubscriptionAttributes(InterestPolicy.ALL);
		assertThat(all).isEqualTo(new SubscriptionAttributes(InterestPolicy.ALL)).isNotEqualTo(new SubscriptionAttributes())
			.isNotEqualTo(null).isNotEqualTo("ALL");
		assertThat(all.hashCode()).isEqualTo(new SubscriptionAttributes(InterestPolicy.ALL).hashCode());
		assertThat(all.toString()).contains("ALL");
	}
	@Test void nullPolicyIsStoredButHashAndStringDereferenceIt() {
		var empty = new SubscriptionAttributes(null);
		assertThat(empty.getInterestPolicy()).isNull();
		assertThat(empty).isEqualTo(new SubscriptionAttributes(null)).isNotEqualTo(new SubscriptionAttributes());
		assertThatThrownBy(empty::hashCode).isInstanceOf(NullPointerException.class);
	}
}
