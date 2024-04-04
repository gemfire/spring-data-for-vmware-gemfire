/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Integration Tests testing and setting up some invalid, or illegal uses of the Region data-policy and shortcut
 * XML namespace attributes.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.context.support.ClassPathXmlApplicationContext
 * @since 1.4.0
 */
public class InvalidRegionDataPolicyShortcutsTest {

	@Test(expected = BeanDefinitionParsingException.class)
	public void testInvalidUseOfRegionDataPolicyAndShortcut() {

		try {
			new ClassPathXmlApplicationContext(
				"/org/springframework/data/gemfire/invalid-use-of-region-datapolicy-and-shortcut.xml");
		}
		catch (BeanDefinitionParsingException expected) {

			assertThat(expected).hasMessageContaining(
				"Only one of [data-policy, shortcut] may be specified with element [gfe:client-region]");

			throw expected;
		}
	}

}
