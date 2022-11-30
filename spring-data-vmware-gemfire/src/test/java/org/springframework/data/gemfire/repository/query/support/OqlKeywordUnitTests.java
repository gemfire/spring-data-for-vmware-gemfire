// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.repository.query.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.Test;

/**
 * Unit Tests for {@link OqlKeyword}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.data.gemfire.repository.query.support.OqlKeyword
 * @since 1.0.0
 */
public class OqlKeywordUnitTests {

	@Test
	public void valueOfIgnoreCaseWithEnumeratedValuesIsSuccessful() {

		for (OqlKeyword oqlKeyword : OqlKeyword.values()) {
			assertThat(OqlKeyword.valueOfIgnoreCase(oqlKeyword.getKeyword())).isEqualTo(oqlKeyword);
		}
	}

	@Test
	public void valueOfIgnoreCaseWithUnconventionalEnumeratedValuesIsSuccessful() {

		assertThat(OqlKeyword.valueOfIgnoreCase("and")).isEqualTo(OqlKeyword.AND);
		assertThat(OqlKeyword.valueOfIgnoreCase("As")).isEqualTo(OqlKeyword.AS);
		assertThat(OqlKeyword.valueOfIgnoreCase("CoUnT")).isEqualTo(OqlKeyword.COUNT);
		assertThat(OqlKeyword.valueOfIgnoreCase(" DISTINCT  ")).isEqualTo(OqlKeyword.DISTINCT);
		assertThat(OqlKeyword.valueOfIgnoreCase("  Order BY ")).isEqualTo(OqlKeyword.ORDER_BY);
	}

	@Test
	public void valueOfIgnoreCaseWithIllegalEnumeratedValuesThrowsIllegalArgumentException() {

		assertIllegalOqlKeyword("AN");
		assertIllegalOqlKeyword("ASS");
		assertIllegalOqlKeyword("CNT");
		assertIllegalOqlKeyword("EXTINCT");
		assertIllegalOqlKeyword("TO");
		assertIllegalOqlKeyword("EXPORT");
		assertIllegalOqlKeyword("OUT");
		assertIllegalOqlKeyword("IS DEFINED");
		assertIllegalOqlKeyword("IS-UNDEFINED");
		assertIllegalOqlKeyword("UNLIKE");
		assertIllegalOqlKeyword("NIL");
		assertIllegalOqlKeyword("NULL VALUE");
		assertIllegalOqlKeyword("XOR");
		assertIllegalOqlKeyword("ORDER_BY");
		assertIllegalOqlKeyword("INSERT");
		assertIllegalOqlKeyword("UPDATE");
		assertIllegalOqlKeyword("LIST");
		assertIllegalOqlKeyword("CLASS");
		assertIllegalOqlKeyword("WHAT");
		assertIllegalOqlKeyword("WHEN");
	}

	private void assertIllegalOqlKeyword(String keyword) {

		try {
			OqlKeyword.valueOfIgnoreCase(keyword);
			fail("Keyword [%s] is not valid", keyword);
		}
		catch (IllegalArgumentException expected) {
			assertThat(expected).hasMessage("[%s] is not a valid GemFire OQL Keyword", keyword);
			assertThat(expected).hasNoCause();
		}
	}

	@Test
	public void getKeywordEqualsNameExceptForOrderBy() {

		for (OqlKeyword oqlKeyword : OqlKeyword.values()) {
			if (!OqlKeyword.ORDER_BY.equals(oqlKeyword)) {
				assertThat(oqlKeyword.getKeyword()).isEqualTo(oqlKeyword.name());
			}
		}
	}
}
