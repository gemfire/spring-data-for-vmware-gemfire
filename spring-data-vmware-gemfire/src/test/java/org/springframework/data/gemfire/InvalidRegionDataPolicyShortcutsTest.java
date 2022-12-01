/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Integration Tests testing and setting up some invalid, or illegal uses of the Region data-policy and shortcut
 * XML namespace attributes.
 *
 * @author John Blum
 * @see Test
 * @see ClassPathXmlApplicationContext
 * @since 1.4.0
 */
public class InvalidRegionDataPolicyShortcutsTest {

	@Test(expected = BeanCreationException.class)
	public void testInvalidRegionShortcutWithPersistentAttribute() {

		try {
			new ClassPathXmlApplicationContext(
				"/org/springframework/data/gemfire/invalid-region-shortcut-with-persistent-attribute.xml");
		}
		catch (BeanCreationException expected) {

			assertThat(expected).hasMessageContaining("Error creating bean with name 'InvalidReplicate'");

			throw expected;
		}
	}

	@Test(expected = BeanDefinitionParsingException.class)
	public void testInvalidUseOfRegionDataPolicyAndShortcut() {

		try {
			new ClassPathXmlApplicationContext(
				"/org/springframework/data/gemfire/invalid-use-of-region-datapolicy-and-shortcut.xml");
		}
		catch (BeanDefinitionParsingException expected) {

			assertThat(expected).hasMessageContaining(
				"Only one of [data-policy, shortcut] may be specified with element [gfe:partitioned-region]");

			throw expected;
		}
	}

}
