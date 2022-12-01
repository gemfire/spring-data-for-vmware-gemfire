/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.expiration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.apache.geode.cache.ExpirationAction;
import org.apache.geode.cache.ExpirationAttributes;

/**
 * Unit Tests for {@link ExpirationAttributesFactoryBean}.
 *
 * @author John Blum
 * @see Test
 * @see ExpirationAttributes
 * @see ExpirationAttributesFactoryBean
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

		expirationAttributesFactoryBean.setAction(ExpirationAction.LOCAL_DESTROY);

		assertThat(expirationAttributesFactoryBean.getAction()).isEqualTo(ExpirationAction.LOCAL_DESTROY);

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
		assertThat(expirationAttributesFactoryBean.getObjectType()).isEqualTo(ExpirationAttributes.class);

		expirationAttributesFactoryBean.setAction(ExpirationAction.DESTROY);
		expirationAttributesFactoryBean.setTimeout(8192);
		expirationAttributesFactoryBean.afterPropertiesSet();

		ExpirationAttributes expirationAttributes = expirationAttributesFactoryBean.getObject();

		assertThat(expirationAttributes).isNotNull();
		assertThat(expirationAttributes.getAction()).isEqualTo(ExpirationAction.DESTROY);
		assertThat(expirationAttributes.getTimeout()).isEqualTo(8192);
		assertThat(expirationAttributesFactoryBean.getObjectType()).isEqualTo(expirationAttributes.getClass());
	}
}
