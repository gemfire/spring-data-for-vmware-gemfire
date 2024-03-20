/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.mapping.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.ChronoZonedDateTime;
import java.time.chrono.Chronology;
import java.time.chrono.Era;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.mapping.model.SimpleTypeHolder;

/**
 * The GemfireSimpleTypeHolder class is a Spring Data Commons SimpleTypeHolder implementation adding additional
 * simple types to the collection.
 *
 * @author John Blum
 * @see org.springframework.data.mapping.model.SimpleTypeHolder
 * @since 1.6.3
 */
@SuppressWarnings("unused")
public class GemfireSimpleTypeHolder extends SimpleTypeHolder {

	private static final boolean REGISTER_DEFAULTS = true;

	protected static final Set<Class<?>> CUSTOM_SIMPLE_TYPES = new HashSet<>(2);

	static {
		CUSTOM_SIMPLE_TYPES.add(BigDecimal.class);
		CUSTOM_SIMPLE_TYPES.add(BigInteger.class);
		CUSTOM_SIMPLE_TYPES.add(ChronoLocalDate.class);
		CUSTOM_SIMPLE_TYPES.add(ChronoLocalDateTime.class);
		CUSTOM_SIMPLE_TYPES.add(Chronology.class);
		CUSTOM_SIMPLE_TYPES.add(ChronoPeriod.class);
		CUSTOM_SIMPLE_TYPES.add(ChronoZonedDateTime.class);
		CUSTOM_SIMPLE_TYPES.add(Duration.class);
		CUSTOM_SIMPLE_TYPES.add(Era.class);
		CUSTOM_SIMPLE_TYPES.add(Instant.class);
		CUSTOM_SIMPLE_TYPES.add(LocalDate.class);
		CUSTOM_SIMPLE_TYPES.add(LocalDateTime.class);
		CUSTOM_SIMPLE_TYPES.add(LocalTime.class);
		CUSTOM_SIMPLE_TYPES.add(MonthDay.class);
		CUSTOM_SIMPLE_TYPES.add(OffsetDateTime.class);
		CUSTOM_SIMPLE_TYPES.add(OffsetTime.class);
		CUSTOM_SIMPLE_TYPES.add(Period.class);
		CUSTOM_SIMPLE_TYPES.add(Year.class);
		CUSTOM_SIMPLE_TYPES.add(YearMonth.class);
		CUSTOM_SIMPLE_TYPES.add(ZonedDateTime.class);
		CUSTOM_SIMPLE_TYPES.add(ZoneOffset.class);
	}

	/**
	 * Constructs an instance of GemfireSimpleTypeHolder initialized with additional, custom simple types
	 * handled by GemFire along with register the default simple types.
	 *
	 * @see org.springframework.data.mapping.model.SimpleTypeHolder#SimpleTypeHolder(Set, boolean)
	 */
	public GemfireSimpleTypeHolder() {
		super(CUSTOM_SIMPLE_TYPES, REGISTER_DEFAULTS);
	}

	/**
	 * Constructs an instance of the GemfireSimpleTypeHolder initialized with a source {@link SimpleTypeHolder}.
	 *
	 * @param source the SimpleTypeHolder used as the source for GemFire's {@link SimpleTypeHolder} implementation.
	 * source must not be {@literal null}.
	 * @throws NullPointerException if source is null.
	 */
	public GemfireSimpleTypeHolder(SimpleTypeHolder source) {
		super(CUSTOM_SIMPLE_TYPES, source);
	}
}
