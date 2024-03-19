/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.mapping.model;

import static org.assertj.core.api.Assertions.assertThat;

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

import org.junit.Test;

import org.springframework.data.gemfire.test.model.Person;

/**
 * Unit tests for {@link GemfireSimpleTypeHolder} class.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.data.gemfire.mapping.model.GemfireSimpleTypeHolder
 * @see org.springframework.data.mapping.model.SimpleTypeHolder
 * @since 1.6.3
 */
public class GemfireSimpleTypeHolderUnitTests {

	private final GemfireSimpleTypeHolder holder = new GemfireSimpleTypeHolder();

	@Test
	public void javaTimeTypesAreSimpleTypes() {

		assertThat(this.holder.isSimpleType(ChronoLocalDate.class)).isTrue();
		assertThat(this.holder.isSimpleType(ChronoLocalDateTime.class)).isTrue();
		assertThat(this.holder.isSimpleType(Chronology.class)).isTrue();
		assertThat(this.holder.isSimpleType(ChronoPeriod.class)).isTrue();
		assertThat(this.holder.isSimpleType(ChronoPeriod.class)).isTrue();
		assertThat(this.holder.isSimpleType(ChronoZonedDateTime.class)).isTrue();
		assertThat(this.holder.isSimpleType(Duration.class)).isTrue();
		assertThat(this.holder.isSimpleType(Era.class)).isTrue();
		assertThat(this.holder.isSimpleType(Instant.class)).isTrue();
		assertThat(this.holder.isSimpleType(LocalDate.class)).isTrue();
		assertThat(this.holder.isSimpleType(LocalDateTime.class)).isTrue();
		assertThat(this.holder.isSimpleType(LocalTime.class)).isTrue();
		assertThat(this.holder.isSimpleType(MonthDay.class)).isTrue();
		assertThat(this.holder.isSimpleType(OffsetDateTime.class)).isTrue();
		assertThat(this.holder.isSimpleType(OffsetTime.class)).isTrue();
		assertThat(this.holder.isSimpleType(Period.class)).isTrue();
		assertThat(this.holder.isSimpleType(Year.class)).isTrue();
		assertThat(this.holder.isSimpleType(YearMonth.class)).isTrue();
		assertThat(this.holder.isSimpleType(ZonedDateTime.class)).isTrue();
		assertThat(this.holder.isSimpleType(ZoneOffset.class)).isTrue();
	}

	@Test
	public void bigDecimalAndBigIntegerAreSimpleTypes() {

		assertThat(this.holder.isSimpleType(BigDecimal.class)).isTrue();
		assertThat(this.holder.isSimpleType(BigInteger.class)).isTrue();
	}

	@Test
	public void personIsNotASimpleType() {
		assertThat(this.holder.isSimpleType(Person.class)).isFalse();
	}
}
