/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.client;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.gemfire.gud.api.GudInterestResultPolicy;
import org.springframework.util.Assert;

/**
 * The Interest class holds details for registering a client interest.
 *
 * @author Costin Leau
 * @author John Blum
 * @see Pattern
 * @see GudInterestResultPolicy
 * @see InitializingBean
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class Interest<K> implements InitializingBean {

	public static final String ALL_KEYS = "ALL_KEYS";

	protected static final boolean DEFAULT_DURABLE = false;
	protected static final boolean DEFAULT_RECEIVE_VALUES = true;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private boolean durable = false;
	private boolean receiveValues = true;

	private GudInterestResultPolicy policy = GudInterestResultPolicy.DEFAULT;

	private K key;

	private Type type;

	/**
	 * Factory method to construct a new instance of {@link Interest} initialized with the given key.
	 *
	 * @param <K> {@link Class} type of the key.
	 * @param key key of interest.
	 * @return a new instance of {@link Interest} initialized with the given key.
	 * @see #Interest(Object)
	 */
	public static <K> Interest newInterest(K key) {
		return new Interest<>(key);
	}

	/**
	 * Constructs an instance of non-durable {@link Interest} initialized with the given key to register interest in,
	 * using the {@link GudInterestResultPolicy#DEFAULT} to initialize the client cache and receiving values by default.
	 *
	 * @param key key(s) of interest.
	 * @see #Interest(Object, GudInterestResultPolicy, boolean, boolean)
	 */
	public Interest(K key) {
		this(key, GudInterestResultPolicy.DEFAULT, DEFAULT_DURABLE, DEFAULT_RECEIVE_VALUES);
	}

	/**
	 * Constructs an instance of non-durable {@link Interest} initialized with the given key to register interest in,
	 * the given {@link GudInterestResultPolicy} used to initialize the client cache, receiving values by default.
	 *
	 * @param key key(s) of interest.
	 * @param policy initial {@link GudInterestResultPolicy} used to initialize the client cache.
	 * @see #Interest(Object, GudInterestResultPolicy, boolean, boolean)
	 */
	public Interest(K key, GudInterestResultPolicy policy) {
		this(key, policy, DEFAULT_DURABLE, DEFAULT_RECEIVE_VALUES);
	}

	/**
	 * Constructs an instance of {@link Interest} initialized with the given key to register interest in,
	 * the given {@link GudInterestResultPolicy} used to initialize the client cache, the given boolean value
	 * to indicate whether interest registration should be durable, receiving values by default.
	 *
	 * @param key key(s) of interest.
	 * @param policy initial {@link GudInterestResultPolicy} used to initialize the client cache.
	 * @param durable boolean value to indicate whether the interest registration should be durable.
	 * @see #Interest(Object, GudInterestResultPolicy, boolean, boolean)
	 */
	public Interest(K key, GudInterestResultPolicy policy, boolean durable) {
		this(key, policy, durable, DEFAULT_RECEIVE_VALUES);
	}

	/**
	 * Constructs an instance of {@link Interest} initialized with the given key to register interest in,
	 * the given {@link GudInterestResultPolicy} used to initialize the client cache and the given boolean values
	 * indicating whether interest registration should be durable and whether to receive values during notifications.
	 *
	 * @param key key(s) of interest.
	 * @param policy initial {@link GudInterestResultPolicy} used to initialize the client cache.
	 * @param durable boolean value to indicate whether the interest registration should be durable.
	 * @param receiveValues boolean value to indicate whether to receive value in notifications.
	 * @see #Interest(Object, GudInterestResultPolicy, boolean, boolean)
	 * @see #afterPropertiesSet()
	 */
	public Interest(K key, GudInterestResultPolicy policy, boolean durable, boolean receiveValues) {

		this.key = key;
		this.policy = policy;
		this.durable = durable;
		this.receiveValues = receiveValues;

		afterPropertiesSet();
	}

	/**
	 * {@inheritDoc}
	 */
	public void afterPropertiesSet() {

		Assert.notNull(this.key, "Key is required");

		setType(resolveType(getType()));
	}

	/**
	 * Attempts to resolve the {@link Type} based on the configured {@link #getKey()}.
	 *
	 * @param type provided {@link Type} used if {@literal non-null}.
	 * @return the resolved {@link Type}.
	 * @see #isRegularExpression(Object)
	 */
	protected Type resolveType(Type type) {
		return (type != null ? type : (isRegularExpression(getKey()) ? Type.REGEX : Type.KEY));
	}

	/**
	 * Determines whether the given {@code key} is a Regular Expression (Regex).
	 *
	 * If the given {@code key} is {@literal "ALL_KEYS"}, a {@link List} or only contains letters, numbers and spaces,
	 * then the {@code key} is not considered a Regular Expression by GemFire, and can be handled with normal
	 * interest registration using {@link org.springframework.data.gemfire.gud.api.GudRegion#registerInterest(Object)}.
	 *
	 * @param key {@link Object} to evaluate.
	 * @return a boolean value indicating whether the given {@link Object} {@code key} is a Regular Expression.
	 * @see #isRegularExpression(String)
	 */
	protected boolean isRegularExpression(Object key) {
		return (!(ALL_KEYS.equals(key) || key instanceof List) && isRegularExpression(String.valueOf(key)));
	}

	/**
	 * Determines whether the given {@code key} is a Regular Expression (Regex).
	 *
	 * If the given {@code value} contains at least 1 special character (e.g. *) and can be compiled
	 * using {@link Pattern#compile(String)}, then the {@code key} is considered a Regular Expression
	 * and interest will be registered using {@link org.springframework.data.gemfire.gud.api.GudRegion#registerInterestRegex(String)}.
	 *
	 * @param value {@link String} to evaluate.
	 * @return a boolean value indicating whether the given {@link String} {@code value} is a Regular Expression.
	 * @see #containsNonAlphaNumericWhitespace(String)
	 * @see Pattern#compile(String)
	 */
	@SuppressWarnings("all")
	protected boolean isRegularExpression(String value) {
		try {
			return (containsNonAlphaNumericWhitespace(value) && Pattern.compile(value) != null);
		}
		catch (PatternSyntaxException ignore) {
			return false;
		}
	}

	/**
	 * Determines whether the given {@link String} value contains at least 1 special character.
	 *
	 * @param value {@link String} to evaluate.
	 * @return a boolean value indicating whether the given {@link String} contains at least 1 special character,
	 * a non-alphanumeric, non-whitespace character.
	 * @see #isAlphaNumericWhitespace(char)
	 * @see #isNotAlphaNumericWhitespace(char)
	 */
	protected boolean containsNonAlphaNumericWhitespace(String value) {
		for (char character : String.valueOf(value).toCharArray()) {
			if (isNotAlphaNumericWhitespace(character)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Determines whether the given {@code character} is a special character (non-alphanumeric, non-whitespace).
	 *
	 * @param character {@link Character} to evaluate.
	 * @return a boolean value indicating whether the given {@code character} is a special character.
	 * @see #isAlphaNumericWhitespace(char)
	 */
	protected boolean isNotAlphaNumericWhitespace(char character) {
		return !isAlphaNumericWhitespace(character);
	}

	/**
	 * Determines whether the given {@code character} is an alphanumeric or whitespace character.
	 *
	 * @param character {@link Character} to evaluate.
	 * @return a boolean value indicating whether the given {@code character} is an alphanumeric
	 * or whitespace character.
	 * @see Character#isDigit(char)
	 * @see Character#isLetter(char)
	 * @see Character#isWhitespace(char)
	 */
	protected boolean isAlphaNumericWhitespace(char character) {
		return (Character.isDigit(character) || Character.isLetter(character)  || Character.isWhitespace(character));
	}

	/**
	 * Determines whether the interest registration is durable and persists between cache client sessions.
	 *
	 * @return a boolean value indicating whether this interest registration is durable.
	 */
	public boolean isDurable() {
		return this.durable;
	}

	/**
	 * Sets whether interest registration is durable and persists between cache client sessions.
	 *
	 * @param durable boolean value to indicate whether this interest registration is durable.
	 */
	public void setDurable(boolean durable) {
		this.durable = durable;
	}

	/**
	 * Returns the key on which interest is registered.
	 *
	 * @return the key of interest.
	 */
	public K getKey() {
		return this.key;
	}

	/**
	 * Sets the key on which interest is registered.
	 *
	 * @param key the key of interest.
	 */
	public void setKey(K key) {
		this.key = key;
	}

	/**
	 * Returns the {@link GudInterestResultPolicy} used when interest is registered and determines whether KEYS,
	 * KEYS_VALUES or nothing (NONE) is initially fetched on initial registration.
	 *
	 * @return the policy
	 */
	public GudInterestResultPolicy getPolicy() {
		return this.policy;
	}

	/**
	 * Sets the initial {@link GudInterestResultPolicy} used when interest is first registered and determines whether KEYS,
	 * KEYS_VALUE or nothing (NONE) is initially fetched.
	 *
	 * The argument is set as an {@link Object} to be able to accept both {@link GudInterestResultPolicy}
	 * and {@link String Strings}, used in XML configuration meta-data.
	 *
	 * @param policy initial {@link GudInterestResultPolicy} to set.
	 * @throws IllegalArgumentException if the given {@code policy} is not a valid type.
	 * @see GudInterestResultPolicy
	 */
	public void setPolicy(Object policy) {

		if (policy instanceof GudInterestResultPolicy) {
			this.policy = (GudInterestResultPolicy) policy;
		}
		else if (policy instanceof String) {
			this.policy = GudInterestResultPolicy.valueOf(String.valueOf(policy).toUpperCase());
		}
		else {
			throw new IllegalArgumentException(String.format("Unknown argument type [%s] for property policy", policy));
		}
	}

	/**
	 * Returns the type of values received by the listener.
	 *
	 * @return the receiveValues
	 */
	public boolean isReceiveValues() {
		return this.receiveValues;
	}

	/**
	 * Switches between the different entities received by the listener.
	 *
	 * @param receiveValues the receiveValues to set
	 */
	public void setReceiveValues(boolean receiveValues) {
		this.receiveValues = receiveValues;
	}

	/**
	 * Returns the type of interest registration (e.g. based on KEY or Regex).
	 *
	 * @return a {@link Type} determining the type of interest.
	 * @see Type
	 */
	public Type getType() {
		return this.type;
	}

	/**
	 * Set the type of interest registration (e.g. based on KEY or Regex).
	 *
	 * @param type {@link Type} qualifying the type of interest.
	 * @see Type
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * Determines whether this {@link Interest} is a KEY interest registration.
	 *
	 * @return a boolean value indicating whether this is KEY interest.
	 * @see Type#KEY
	 * @see #getType()
	 */
	public boolean isKeyType() {
		return Type.KEY.equals(getType());
	}

	/**
	 * Determines whether this {@link Interest} is a REGEX interest registration.
	 *
	 * @return a boolean value indicating whether this is REGEX interest.
	 * @see Type#REGEX
	 * @see #getType()
	 */
	public boolean isRegexType() {
		return Type.REGEX.equals(getType());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("{ @type = %1$s, key = %2$s, durable = %3$s, policy = %4$s, receiveValues = %5$s, type = %6$s }",
			getClass().getName(), getKey(), isDurable(), getPolicy(), isReceiveValues(), getType());
	}

	/**
	 * Builder method to specify the type of interest registration.
	 *
	 * @param type {@link Type} of interest registration.
	 * @return this {@link Interest}.
	 * @see Type
	 * @see #resolveType(Type)
	 * @see #setType(Type)
	 */
	public Interest asType(Type type) {
		setType(resolveType(type));
		return this;
	}

	/**
	 * Builder method to mark this {@link Interest} as durable.
	 *
	 * @return this {@link Interest}.
	 * @see #setDurable(boolean)
	 */
	public Interest makeDurable() {
		setDurable(true);
		return this;
	}

	/**
	 * Builder method to set whether the interest event notifications will receive values along with keys.
	 *
	 * @param receiveValues boolean to indicate that value should be sent along with keys
	 * on interest event notifications.
	 * @return this {@link Interest}.
	 * @see #setReceiveValues(boolean)
	 */
	public Interest receivesValues(boolean receiveValues) {
		setReceiveValues(receiveValues);
		return this;
	}

	/**
	 * Builder method to set the {@link GudInterestResultPolicy} used to initialize the cache.
	 *
	 * @param policy {@link GudInterestResultPolicy}.
	 * @return this {@link Interest}.
	 * @see GudInterestResultPolicy
	 * @see #setPolicy(Object)
	 */
	public Interest usingPolicy(GudInterestResultPolicy policy) {
		setPolicy(policy);
		return this;
	}

	/**
	 * Builder method to express the key of interest.
	 *
	 * @param key key of interests.
	 * @return this {@link Interest}.
	 * @see #setKey(Object)
	 * @see #getType()
	 * @see #resolveType(Type)
	 * @see #setType(Type)
	 */
	public Interest withKey(K key) {
		setKey(key);
		setType(resolveType(getType()));
		return this;
	}

	/**
	 * Type of interest registration.
	 */
	public enum Type {
		KEY, REGEX
	}
}
