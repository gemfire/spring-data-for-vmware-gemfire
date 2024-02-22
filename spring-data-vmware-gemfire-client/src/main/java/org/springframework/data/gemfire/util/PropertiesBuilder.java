/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;
import java.util.function.Function;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Builder for {@link Properties}.
 *
 * @author John Blum
 * @see Properties
 * @see FactoryBean
 * @since 1.9.0
 */
@SuppressWarnings("unused")
public class PropertiesBuilder implements FactoryBean<Properties> {

	private static final Function<Object, String> TO_STRING_FUNCTION = target ->
		target instanceof Class ? ((Class<?>) target).getName()
		: target != null ? target.toString()
		: String.valueOf(target);

	/**
	 * Factory method used to create a default {@link PropertiesBuilder} instance.
	 *
	 * @return an instance of the {@link PropertiesBuilder} class with not {@link Properties}.
	 * @see #PropertiesBuilder()
	 */
	public static @NonNull PropertiesBuilder create() {
		return new PropertiesBuilder();
	}

	/**
	 * Factory method used to create an instance of {@link PropertiesBuilder} initialized with
	 * the given {@link Properties}.
	 *
	 * @param properties {@link Properties} used as the default properties of the constructed {@link PropertiesBuilder}.
	 * @return an instance of {@link PropertiesBuilder} initialized with the given {@link Properties}.
	 * @see Properties
	 * @see #PropertiesBuilder(Properties)
	 */
	public static @NonNull PropertiesBuilder from(@NonNull Properties properties) {
		return new PropertiesBuilder(properties);
	}

	/**
	 * Constructs a new instance of {@link PropertiesBuilder} initialized with all properties
	 * from the given {@link InputStream}.
	 *
	 * @param in {@link InputStream} source containing properties to use as the defaults for the constructed builder.
	 * @return a {@link PropertiesBuilder} initialized with properties from the given {@link InputStream}.
	 * @throws IllegalArgumentException if the {@link InputStream} cannot be read.
	 * @see Properties#load(InputStream)
	 * @see InputStream
	 */
	public static @NonNull PropertiesBuilder from(@NonNull InputStream in) {

		try {
			Properties defaults = new Properties();
			defaults.load(in);
			return new PropertiesBuilder(defaults);
		}
		catch (IOException cause) {
			throw new IllegalArgumentException("Failed to read properties from InputStream", cause);
		}
	}

	/**
	 * Constructs a new isntance of {@link PropertiesBuilder} initialized with all properties
	 * from the given {@link Reader}.
	 *
	 * @param reader {@link Reader} source containing properties to use as the defaults for the constructed builder.
	 * @return a {@link PropertiesBuilder} initialized with properties from the given {@link Reader}.
	 * @throws IllegalArgumentException if the {@link Reader} cannot be read.
	 * @see Properties#load(Reader)
	 * @see Reader
	 */
	public static @NonNull PropertiesBuilder from(@NonNull Reader reader) {

		try {
			Properties defaults = new Properties();
			defaults.load(reader);
			return new PropertiesBuilder(defaults);
		}
		catch (IOException cause) {
			throw new IllegalArgumentException("Failed to read properties from Reader", cause);
		}
	}

	/**
	 * Constructs a new instance of {@link PropertiesBuilder} initialized with all properties
	 * from the given {@link InputStream} in XML format.
	 *
	 * @param xml {@link InputStream} source containing properties in XML format to use as defaults
	 * for the constructed builder.
	 * @return a {@link PropertiesBuilder} initialized with properties from the given XML {@link InputStream}.
	 * @throws IllegalArgumentException if the XML {@link InputStream} cannot be read.
	 * @see Properties#loadFromXML(InputStream)
	 * @see InputStream
	 */
	public static @NonNull PropertiesBuilder fromXml(@NonNull InputStream xml) {

		try {
			Properties defaults = new Properties();
			defaults.loadFromXML(xml);
			return new PropertiesBuilder(defaults);
		}
		catch (IOException cause) {
			throw new IllegalArgumentException("Failed to read properties from XML", cause);
		}
	}

	private final Properties properties;

	/**
	 * Constructs a new instance of {@link PropertiesBuilder}.
	 */
	public PropertiesBuilder() {
		this.properties = new Properties();
	}

	/**
	 * Constructs a new instance of {@link PropertiesBuilder} initialized with the default {@link Properties}.
	 *
	 * @param defaults {@link Properties} used as the defaults.
	 * @throws NullPointerException if the {@link Properties} reference is {@literal null}.
	 * @see Properties
	 */
	public PropertiesBuilder(@NonNull Properties defaults) {
		this.properties = new Properties();
		this.properties.putAll(defaults);
	}

	/**
	 * Constructs a new instance of {@link PropertiesBuilder} initialized with the given {@link PropertiesBuilder}
	 * providing the default {@link Properties} for {@literal this} builder.
	 *
	 * @param builder {@link PropertiesBuilder} providing the default {@link Properties} for this builder.
	 * @throws NullPointerException if the {@link PropertiesBuilder} reference is {@literal null}.
	 * @see #PropertiesBuilder(Properties)
	 */
	@SuppressWarnings("all")
	public PropertiesBuilder(@NonNull PropertiesBuilder builder) {
		this(builder.build());
	}

	@Override
	public @NonNull Properties getObject() throws Exception {
		return build();
	}

	@Override
	public @NonNull Class<?> getObjectType() {
		return this.properties != null ? this.properties.getClass() : Properties.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	/**
	 * Null-safe method to add all the {@link Properties} to this builder.  This operation effectively overwrites
	 * any properties already set with the same name from the source.
	 *
	 * @param properties {@link Properties} to add to this builder.
	 * @return a reference to this {@link PropertiesBuilder}.
	 * @see Properties
	 */
	public @NonNull PropertiesBuilder add(@Nullable Properties properties) {

		if (!CollectionUtils.isEmpty(properties)) {
			this.properties.putAll(properties);
		}

		return this;
	}

	/**
	 * Null-safe method to add all the {@link Properties} from the provided {@link PropertiesBuilder} to this builder.
	 * This operation effectively overwrites any properties already set with the same name from the source.
	 *
	 * @param builder source of the {@link Properties} to add to this builder.
	 * @return a reference to this {@link PropertiesBuilder}.
	 * @see PropertiesBuilder
	 */
	public @NonNull PropertiesBuilder add(@Nullable PropertiesBuilder builder) {
		return builder != null ? add(builder.build()) : this;
	}

	/**
	 * Sets a property with given name to the specified value.  The property is only set if the value is not null.
	 *
	 * @param name the name of the property to set.
	 * @param value the value to set the property to.
	 * @return a reference to this {@link PropertiesBuilder}.
	 * @see #setProperty(String, String)
	 */
	public @NonNull PropertiesBuilder setProperty(@NonNull String name, @NonNull Object value) {
		return value != null ? setProperty(name, TO_STRING_FUNCTION.apply(value)) : this;
	}

	/**
	 * Sets the named property to the given array of object values.  The property is only set
	 * if the array of object value is not null or empty.
	 *
	 * @param name name of the property to set.
	 * @param values array of object values used as the property's value.
	 * @return a reference to this {@link PropertiesBuilder}
	 * @see StringUtils#arrayToCommaDelimitedString(Object[])
	 * @see #setProperty(String, String)
	 */
	public @NonNull PropertiesBuilder setProperty(@NonNull String name, @Nullable Object[] values) {
		return !ObjectUtils.isEmpty(values) ? setProperty(name, StringUtils.arrayToCommaDelimitedString(values)) : this;
	}

	/**
	 * Sets a property with the given name to the specified {@link String} value.  The property is only set
	 * if the value is not {@literal null}, an empty {@link String} or not equal to the {@link String} literal
	 * {@literal null}, ignoring case.
	 *
	 * @param name the name of the property to set.
	 * @param value the value to set the property to.
	 * @return a reference to this {@link PropertiesBuilder}.
	 * @throws IllegalArgumentException if the property name is not specified.
	 * @see Properties#setProperty(String, String)
	 */
	public @NonNull PropertiesBuilder setProperty(@NonNull String name, @NonNull String value) {

		Assert.hasText(name, String.format("Name [%s] must be specified", name));

		if (isValuable(value)) {
			this.properties.setProperty(name, value);
		}

		return this;
	}

	/**
	 * Sets the named property to the given {@literal value} if the {@literal defaultValue} is not null
	 * and {@literal value} is not equal to {@literal defaultValue}.
	 *
	 * @param <T> Class type of the property value.
	 * @param name name of the property to set.
	 * @param value value to set for the property.
	 * @param defaultValue default value of the property to compare with the given value
	 * when determining whether to set the property.
	 * @return a reference to this {@link PropertiesBuilder}.
	 * @see #setProperty(String, Object)
	 */
	public @NonNull <T> PropertiesBuilder setPropertyIfNotDefault(@NonNull String name, Object value, T defaultValue) {
		return defaultValue == null || !defaultValue.equals(value) ? setProperty(name, value) : this;
	}

	/**
	 * Un-sets the named property.  This method sets the given named property to an empty {@link String}.
	 *
	 * @param name name of the property to unset.
	 * @return a reference to this {@link PropertiesBuilder}.
	 * @throws IllegalArgumentException if the property name is not specified.
	 */
	public @NonNull PropertiesBuilder unsetProperty(@NonNull String name) {

		Assert.hasText(name, String.format("Name [%s] mut be specified", name));

		this.properties.setProperty(name, "");

		return this;
	}

	/**
	 * Determine whether the given {@link String} value is a valid {@link Properties} value.  A property value is
	 * considered valid if it is not null, not empty and not equal to (case-insensitive) {@link String} literal
	 * {@literal null}.
	 *
	 * @param value {@link String} value for the property being set.
	 * @return a boolean value indicating whether the given {@link String} value is a valid {@link Properties} value.
	 */
	protected boolean isValuable(@Nullable String value) {
		return StringUtils.hasText(value) && !"null".equalsIgnoreCase(value.trim());
	}

	/**
	 * Builds the {@link Properties} object from this builder.
	 *
	 * @return the {@link Properties} object built by this builder.
	 * @see Properties
	 */
	public @NonNull Properties build() {
		return this.properties;
	}
}
