/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;

import org.apache.shiro.util.Assert;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.config.annotation.support.AbstractAnnotationConfigSupport;
import org.springframework.data.gemfire.config.annotation.support.Authentication;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * Spring {@link Configuration} class used to configure and register an {@link Authentication} object
 * based on security (auth) configuration metadata supplied in the {@link EnableSecurity} annotation.
 *
 * @author John Blum
 * @see Annotation
 * @see BeanDefinition
 * @see BeanDefinitionBuilder
 * @see BeanDefinitionRegistry
 * @see BeanNameGenerator
 * @see Configuration
 * @see ImportAware
 * @see AbstractAnnotationConfigSupport
 * @see Authentication
 * @since 1.0.0
 */
@Configuration
public class AuthenticationBeanConfiguration extends AbstractAnnotationConfigSupport implements ImportAware {

	private static final char[] EMPTY_CHAR_ARRAY = {};

	private String username;
	private String password;

	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return EnableSecurity.class;
	}

	protected void setUsername(@Nullable String username) {
		this.username = username;
	}

	private @Nullable String getUsername() {
		return this.username;
	}

	protected void setPassword(@Nullable String password) {
		this.password = password;
	}

	private @Nullable String getPassword() {
		return this.password;
	}

	@Override
	public void setImportMetadata(@NonNull AnnotationMetadata importMetadata) {

		if (isAnnotationPresent(importMetadata)) {

			AnnotationAttributes enableSecurityAttributes = getAnnotationAttributes(importMetadata);

			setUsername(resolveProperty(securityProperty("username"), String.class,
				enableSecurityAttributes.getString("securityUsername")));

			setPassword(resolveProperty(securityProperty("password"), String.class,
				enableSecurityAttributes.getString("securityPassword")));
		}
	}

	@Bean
	@SuppressWarnings("unused")
	public @NonNull Authentication<String, String> springDataGeodeAuthentication() {
		return SpringDataGeodeAuthentication.from(this::getUsername, this::getPassword);
	}

	private void registerAuthenticationBean(@NonNull BeanDefinitionRegistry registry,
			@NonNull BeanNameGenerator beanNameGenerator) {

		if (isAuthenticationCredentialsSet(getUsername(), nullSafeToCharArray(getPassword()))) {

			BeanDefinition authenticationBean =
				BeanDefinitionBuilder.rootBeanDefinition(SpringDataGeodeAuthentication.class)
					.addConstructorArgValue(getUsername())
					.addConstructorArgValue(getPassword())
					.setRole(BeanDefinition.ROLE_INFRASTRUCTURE)
					.getBeanDefinition();

			String beanName = beanNameGenerator.generateBeanName(authenticationBean, registry);

			registry.registerBeanDefinition(beanName, authenticationBean);
		}
	}

	protected boolean isAuthenticationCredentialsSet(String username, char[] password) {
		return StringUtils.hasText(username) && nullSafeCharArray(password).length > 0;
	}

	private @NonNull char[] nullSafeCharArray(@Nullable char[] array) {
		return array != null ? array : EMPTY_CHAR_ARRAY;
	}

	private @NonNull char[] nullSafeToCharArray(@Nullable String value) {
		return value != null ? value.trim().toCharArray() : EMPTY_CHAR_ARRAY;
	}

	protected static class SpringDataGeodeAuthentication implements Authentication<String, String> {

		public static @NonNull SpringDataGeodeAuthentication from(@NonNull Supplier<String> username,
				@NonNull Supplier<String> password) {

			return new SpringDataGeodeAuthentication(username, password);
		}

		private final Supplier<String> username;
		private final Supplier<String> password;

		protected SpringDataGeodeAuthentication(@NonNull Supplier<String> username, @NonNull Supplier<String> password) {
			this.username = require(username, "Username [%s] is required", username);
			this.password = require(password, "Password [%s] is required", password);
		}

		private <T> T require(T target, String message, Object... arguments) {
			Assert.notNull(target, String.format(message, arguments));
			return target;
		}

		@Override
		public boolean isRequested() {
			return StringUtils.hasText(getPrincipal()) && StringUtils.hasText(getCredentials());
		}

		@Override
		public @NonNull String getPrincipal() {
			return this.username.get();
		}

		@Override
		public @NonNull String getCredentials() {
			return this.password.get();
		}

		@Override
		public String toString() {
			return getPrincipal();
		}
	}
}
