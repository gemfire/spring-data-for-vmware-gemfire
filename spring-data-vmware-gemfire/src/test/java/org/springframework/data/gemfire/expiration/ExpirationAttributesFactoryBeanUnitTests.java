/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.expiration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.springframework.data.gemfire.gud.api.GudExpirationAction;
import org.springframework.data.gemfire.gud.api.GudExpirationAttributes;

/**
 * Unit Tests for {@link ExpirationAttributesFactoryBean}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.GudExpirationAttributes
 * @see org.springframework.data.gemfire.expiration.ExpirationAttributesFactoryBean
 * @since 1.6.0
 */
public class ExpirationAttributesFactoryBeanUnitTests {

	@Test
	public void testIsSingleton() {
		assertThat(new ExpirationAttributesFactoryBean().isSingleton()).isTrue();
	}

	@Test
	public void testSetAndGetAction() {

		ExpirationAttributesFactoryBean expirationAttributesFactoryBean = new ExpirationAttributesFactoryBean();

		assertThat(expirationAttributesFactoryBean.getAction())
			.isEqualTo(ExpirationAttributesFactoryBean.DEFAULT_EXPIRATION_ACTION);

		expirationAttributesFactoryBean.setAction(GudExpirationAction.LOCAL_DESTROY);

		assertThat(expirationAttributesFactoryBean.getAction()).isEqualTo(GudExpirationAction.LOCAL_DESTROY);

		expirationAttributesFactoryBean.setAction(null);

		assertThat(expirationAttributesFactoryBean.getAction())
			.isEqualTo(ExpirationAttributesFactoryBean.DEFAULT_EXPIRATION_ACTION);
	}

	@Test
	public void testSetAndGetTimeout() {

		ExpirationAttributesFactoryBean expirationAttributesFactoryBean = new ExpirationAttributesFactoryBean();

		assertThat(expirationAttributesFactoryBean.getTimeout()).isEqualTo(0);

		expirationAttributesFactoryBean.setTimeout(60000);

		assertThat(expirationAttributesFactoryBean.getTimeout()).isEqualTo(60000);

		expirationAttributesFactoryBean.setTimeout(null);

		assertThat(expirationAttributesFactoryBean.getTimeout()).isEqualTo(0);
	}

	@Test
	public void testAfterPropertiesSet() throws Exception {

		ExpirationAttributesFactoryBean expirationAttributesFactoryBean = new ExpirationAttributesFactoryBean();

		assertThat(expirationAttributesFactoryBean.getObject()).isNull();
		assertThat(expirationAttributesFactoryBean.getObjectType()).isEqualTo(GudExpirationAttributes.class);

		expirationAttributesFactoryBean.setAction(GudExpirationAction.DESTROY);
		expirationAttributesFactoryBean.setTimeout(8192);
		expirationAttributesFactoryBean.afterPropertiesSet();

		GudExpirationAttributes expirationAttributes = expirationAttributesFactoryBean.getObject();

		assertThat(expirationAttributes).isNotNull();
		assertThat(expirationAttributes.getAction()).isEqualTo(GudExpirationAction.DESTROY);
		assertThat(expirationAttributes.getTimeout()).isEqualTo(8192);
		assertThat(expirationAttributesFactoryBean.getObjectType()).isEqualTo(expirationAttributes.getClass());
	}
}
