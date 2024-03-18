/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.serialization;

import java.awt.*;
import java.beans.Beans;
import java.io.DataInput;
import java.io.DataOutput;
import java.util.List;

import org.apache.geode.DataSerializable;
import org.apache.geode.Instantiator;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link WiringInstantiator}.
 *
 * @author Costin Leau
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.Instantiator
 * @see org.springframework.data.gemfire.serialization.WiringInstantiator
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class WiringInstantiatorIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private WiringInstantiator instantiator;

	public static class AnnotatedBean implements DataSerializable {

		@Autowired
		Point point;
		Shape shape;

		@Autowired
		void initShape(Shape shape) {
			this.shape = shape;
		}

		public void fromData(DataInput in) { }

		public void toData(DataOutput out) { }

	}

	public static class TemplateWiringBean implements DataSerializable {

		Beans beans;
		Point point;

		public void setBeans(Beans bs) {
			this.beans = bs;
		}

		public void fromData(DataInput in) { }

		public void toData(DataOutput out) { }

	}

	public static class TypeA implements DataSerializable {

		public void fromData(DataInput arg0) { }

		public void toData(DataOutput arg0) { }

	}

	public static class TypeB implements DataSerializable {

		public void fromData(DataInput arg0) { }

		public void toData(DataOutput arg0) { }

	}

	@Test
	public void testAutowiredBean() {

		Object instance = instantiator.newInstance();

		Assertions.assertThat(instance).isNotNull();
		Assertions.assertThat(instance instanceof AnnotatedBean).isTrue();

		AnnotatedBean bean = (AnnotatedBean) instance;

		Assertions.assertThat(bean.point).isNotNull();
		Assertions.assertThat(bean.shape).isNotNull();

		Assertions.assertThat(requireApplicationContext().getBean("point")).isSameAs(bean.point);
		Assertions.assertThat(requireApplicationContext().getBean("area")).isSameAs(bean.shape);
	}

	@Test
	public void testTemplateBean() {

		WiringInstantiator instantiator2 =
			new WiringInstantiator(new AsmInstantiatorGenerator().getInstantiator(TemplateWiringBean.class, 99));

		instantiator2.setBeanFactory(requireApplicationContext().getAutowireCapableBeanFactory());
		instantiator2.afterPropertiesSet();

		Object instance = instantiator2.newInstance();

		Assertions.assertThat(instance instanceof TemplateWiringBean).isTrue();
		TemplateWiringBean bean = (TemplateWiringBean) instance;

		Assertions.assertThat(bean.point).isNull();
		Assertions.assertThat(bean.beans).isNotNull();

		Assertions.assertThat(requireApplicationContext().getBean("beans")).isSameAs(bean.beans);
	}

	@SuppressWarnings("unchecked")
	public void testInstantiatorFactoryBean() {

		List<Instantiator> list =
			(List<Instantiator>) requireApplicationContext().getBean("instantiator-factory", List.class);

		Assertions.assertThat(list).isNotNull();
		Assertions.assertThat(list.size()).isEqualTo(2);
	}
}
