/*
 * Copyright (c) VMware, Inc. 2023-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.unit.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.runner.RunWith;

import org.springframework.data.gemfire.tests.mock.context.GemFireMockObjectsApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * The {@link GemFireUnitTest} annotation marks a test class as an Apache Geode Unit Test
 * with GemFire Mock Objects enabled.
 *
 * Additionally, this annotation enables Spring's {@link SpringRunner} JUnit Runner implementation
 * using JUnit's {@link RunWith} annotation.
 *
 * @author John Blum
 * @see Documented
 * @see Inherited
 * @see Retention
 * @see Target
 * @see RunWith
 * @see GemFireMockObjectsApplicationContextInitializer
 * @see ContextConfiguration
 * @see SpringRunner
 * @since 0.0.1
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = GemFireMockObjectsApplicationContextInitializer.class)
@SuppressWarnings("unused")
public @interface GemFireUnitTest {

}
