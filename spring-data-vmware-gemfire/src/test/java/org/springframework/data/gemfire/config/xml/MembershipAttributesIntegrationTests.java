/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.LossAction;
import org.apache.geode.cache.MembershipAttributes;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.ResumptionAction;
import org.apache.geode.distributed.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link MembershipAttributes}.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.MembershipAttributes
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings({ "deprecation", "unused" })
public class MembershipAttributesIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("secure")
	private Region<?, ?> secure;

	@Autowired
	@Qualifier("simple")
	private Region<?, ?> simple;

	@Test
	public void secureRegionMembershipAttributesConfigurationIsCorrect() {

		MembershipAttributes membershipAttributes = secure.getAttributes().getMembershipAttributes();

		assertThat(membershipAttributes).isNotNull();
		assertThat(membershipAttributes.getLossAction()).isEqualTo(LossAction.LIMITED_ACCESS);
		assertThat(membershipAttributes.hasRequiredRoles()).isTrue();
		assertThat(membershipAttributes.getRequiredRoles().stream().map(Role::getName))
			.containsExactlyInAnyOrder("ROLE1", "ROLE2");
		assertThat(membershipAttributes.getResumptionAction()).isEqualTo(ResumptionAction.REINITIALIZE);
	}

	@Test
	public void simpleRegionMembershipAttributesConfigurationIsCorrect() {

		MembershipAttributes membershipAttributes = simple.getAttributes().getMembershipAttributes();

		assertThat(membershipAttributes).isNotNull();
		assertThat(membershipAttributes.getLossAction()).isEqualTo(LossAction.FULL_ACCESS);
		assertThat(membershipAttributes.hasRequiredRoles()).isFalse();
		assertThat(membershipAttributes.getRequiredRoles()).isEmpty();
		assertThat(membershipAttributes.getResumptionAction()).isEqualTo(ResumptionAction.NONE);
	}
}
