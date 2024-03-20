/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.extensions.spring.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.AliasFor;

/**
 * The {@link DependencyOf} annotation is the inverse of Spring's {@link DependsOn} annotation allowing a bean
 * to declare itself as a dependency of another bean in the Spring container.
 *
 * For example, with Spring's {@link DependsOn} annotation, a bean A can say that it {@literal depends on} bean B.
 * However, with the {@link DependencyOf} annotation, a bean B can say it is a required dependency for bean A,
 * or rather that bean A depends on bean B.
 *
 * Therefore, the following bean definitions for A & B are equivalent:
 *
 * <pre>
 * <code>
 * {@literal Configuration}
 * public class ConfigurationOne {
 *
 *   {@literal Bean}
 *   {@literal DependsOn DependsOn("b")}
 *   public A a() {
 *     return new A();
 *   }
 *
 *   {@literal Bean}
 *   public B b() {
 *     return new B();
 *   }
 * }
 * </code>
 * </pre>
 *
 * And...
 *
 * <pre>
 * <code>
 * {@literal Configuration}
 * public class ConfigurationTwo {
 *
 *   {@literal Bean}
 *   public A a() {
 *     return new A();
 *   }
 *
 *   {@literal Bean}
 *   {@literal DependencyOf DependencyOf("a")}
 *   public B b() {
 *     return new B();
 *   }
 * }
 * </code>
 * </pre>
 *
 * One advantage of this approach is that bean A does not need to know all the beans it is possibly dependent on,
 * especially at runtime when additional collaborators or dependencies maybe added dynamically to the classpath,
 * Therefore, additional dependencies of A can be added to the configuration automatically, over time without
 * having to go back and modify the bean definition for A.
 *
 * This feature is experimental.
 *
 * @author John Blum
 * @see Documented
 * @see Inherited
 * @see Retention
 * @see Target
 * @see DependsOn
 * @since 0.0.23
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE })
@SuppressWarnings("unused")
public @interface DependencyOf {

	@AliasFor("value")
	String[] beanNames() default {};

	@AliasFor("beanNames")
	String[] value() default {};

}
