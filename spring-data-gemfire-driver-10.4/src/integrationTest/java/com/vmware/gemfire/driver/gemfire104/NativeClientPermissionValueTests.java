/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.geode.security.ResourcePermission;
import org.apache.geode.security.ResourcePermission.Operation;
import org.apache.geode.security.ResourcePermission.Resource;
import org.apache.geode.security.ResourcePermission.Target;
import org.junit.jupiter.api.Test;

/** Native value compatibility evidence; these tests do not grant or deny server execution. */
class NativeClientPermissionValueTests {

	@Test
	@SuppressWarnings("deprecation")
	void componentConstructionPreservesDefaultsAliasesAndTargetCase() {
		var empty = new ResourcePermission();
		assertThat(empty.getResource()).isEqualTo(Resource.NULL);
		assertThat(empty.getOperation()).isEqualTo(Operation.NULL);
		assertThat(empty.getTarget()).isEqualTo("*");
		assertThat(empty.getKey()).isEqualTo("*");
		assertThat(empty.toString()).isEqualTo("NULL:NULL");
		assertThat(new ResourcePermission((Resource) null, null, (String) null, null)).isEqualTo(empty);
		assertThat(new ResourcePermission((String) null, null, null, null)).isEqualTo(empty);

		var permission = new ResourcePermission("data", "read", "///Orders", "KeyA");
		assertThat(permission).isEqualTo(new ResourcePermission(Resource.DATA, Operation.READ, "Orders", "KeyA"));
		assertThat(permission.getResourceString()).isEqualTo("DATA");
		assertThat(permission.getOperationString()).isEqualTo("READ");
		assertThat(permission.getRegionName()).isEqualTo(permission.getTarget()).isEqualTo("Orders");
		assertThat(permission.getKey()).isEqualTo("KeyA");
		assertThat(permission.toString()).isEqualTo("DATA:READ:Orders:KeyA");
		assertThat(new ResourcePermission("ALL", "ALL")).isEqualTo(new ResourcePermission("*", "*"));
		assertThat(ResourcePermission.ALL_REGIONS).isEqualTo(ResourcePermission.ALL);
		assertThat(ResourcePermission.ALL_KEYS).isEqualTo(ResourcePermission.ALL);
		assertThat(new ResourcePermission(Resource.DATA, Operation.READ, "ALL").getTarget()).isEqualTo("ALL");
		assertThat(new ResourcePermission(Resource.CLUSTER, Operation.MANAGE, Target.ALL).getTarget()).isEqualTo("*");
	}

	@Test
	void wildcardConstructionCannotBeRebuiltFromComponentGettersOrToString() {
		var wildcard = new InspectablePermission("DATA:READ:Orders:KeyA");
		var requested = new ResourcePermission("DATA", "READ", "Orders", "KeyA");
		assertThat(wildcard.parts()).isEqualTo(List.of(Set.of("DATA"), Set.of("READ"), Set.of("Orders"), Set.of("KeyA")));
		assertThat(wildcard.getResource()).isEqualTo(Resource.NULL);
		assertThat(wildcard.getOperation()).isEqualTo(Operation.NULL);
		assertThat(wildcard.getTarget()).isEqualTo("*");
		assertThat(wildcard.getKey()).isEqualTo("*");
		assertThat(wildcard.toString()).isEqualTo("NULL:NULL");
		assertThat(wildcard.implies(requested)).isTrue();
		assertThat(wildcard.implies(new ResourcePermission("data:read:orders:keya"))).isFalse();
		assertThat(wildcard).isNotEqualTo(new ResourcePermission(wildcard.getResource(), wildcard.getOperation(),
			wildcard.getTarget(), wildcard.getKey()));
		assertThat(wildcard).isNotEqualTo(new ResourcePermission(wildcard.toString()));
	}

	@Test
	void componentPermissionsKeepSubpartSetsAndCaseSensitiveImplication() {
		var allowed = new ResourcePermission(Resource.DATA, Operation.READ, "Orders,Customers", "KeyA,KeyB");
		var reordered = new ResourcePermission(Resource.DATA, Operation.READ, "Customers,Orders", "KeyB,KeyA");
		assertThat(allowed).isEqualTo(reordered).hasSameHashCodeAs(reordered);
		assertThat(allowed.implies(new ResourcePermission(Resource.DATA, Operation.READ, "Orders", "KeyA"))).isTrue();
		assertThat(allowed.implies(new ResourcePermission(Resource.DATA, Operation.READ, "orders", "KeyA"))).isFalse();
		assertThat(allowed.implies(new ResourcePermission(Resource.DATA, Operation.READ, "Orders", "Other"))).isFalse();
		assertThat(allowed.implies(new ResourcePermission(Resource.DATA, Operation.WRITE, "Orders", "KeyA"))).isFalse();
		assertThat(new ResourcePermission(Resource.DATA, Operation.READ).implies(allowed)).isTrue();
		assertThat(allowed.implies(new ResourcePermission(Resource.DATA, Operation.READ))).isFalse();
	}

	@Test
	void wildcardImplicationDoesNotMeanValueEquality() {
		var shortForm = new ResourcePermission("data:read");
		var explicitWildcards = new ResourcePermission("data:read:*:*");
		var specific = new ResourcePermission("data:read:orders:keya");
		assertThat(shortForm.implies(explicitWildcards)).isTrue();
		assertThat(explicitWildcards.implies(shortForm)).isTrue();
		assertThat(shortForm).isNotEqualTo(explicitWildcards);
		assertThat(shortForm.implies(specific)).isTrue();
		assertThat(specific.implies(shortForm)).isFalse();
		assertThat(new ResourcePermission("data:read,write:orders:*").implies(specific)).isTrue();
		assertThat(new ResourcePermission("*:read:orders:*").implies(specific)).isTrue();
	}

	@Test
	void inheritedPartsCanDifferFromFieldsAndRetainMutableAliases() {
		var permission = new InspectablePermission("*");
		permission.replace("DATA:READ:Orders:KeyA", true);
		var request = new ResourcePermission(Resource.DATA, Operation.READ, "Orders", "KeyA");
		assertThat(permission.implies(request)).isTrue();
		assertThat(permission.getResource()).isEqualTo(Resource.NULL);
		permission.replace("DATA:READ:Orders:KeyA");
		assertThat(permission.implies(request)).isTrue();
		permission.replace("DATA:READ:Orders:KeyA", false);
		assertThat(permission.parts()).isEqualTo(List.of(Set.of("data"), Set.of("read"), Set.of("orders"), Set.of("keya")));
		assertThat(permission.implies(request)).isFalse();
		assertThat(permission.implies(new ResourcePermission("data:read:orders:keya"))).isTrue();

		var keys = new HashSet<>(Set.of("KeyA"));
		var parts = new ArrayList<>(List.of(Set.of("DATA"), Set.of("READ"), Set.of("Orders"), keys));
		permission.replace(parts);
		assertThat(permission.parts()).isSameAs(parts);
		assertThat(permission.implies(request)).isTrue();
		keys.clear();
		keys.add("KeyB");
		assertThat(permission.implies(request)).isFalse();
		assertThat(permission.implies(new ResourcePermission(Resource.DATA, Operation.READ, "Orders", "KeyB"))).isTrue();
	}

	@Test
	void serializationPreservesBothRepresentations() throws Exception {
		for (var original : List.of(new ResourcePermission("DATA:READ:Orders:KeyA"),
				new ResourcePermission(Resource.DATA, Operation.READ, "Orders", "KeyA"))) {
			var restored = roundTrip(original);
			assertThat(restored).isNotSameAs(original).isEqualTo(original).hasSameHashCodeAs(original);
			assertThat(restored.getResource()).isEqualTo(original.getResource());
			assertThat(restored.getOperation()).isEqualTo(original.getOperation());
			assertThat(restored.getTarget()).isEqualTo(original.getTarget());
			assertThat(restored.getKey()).isEqualTo(original.getKey());
			assertThat(restored.toString()).isEqualTo(original.toString());
			assertThat(restored.implies(original)).isTrue();
			assertThat(original.implies(restored)).isTrue();
		}
	}

	@Test
	void malformedDeclarationsFailInsteadOfBecomingUnrestrictedPermissions() {
		assertThatThrownBy(() -> new ResourcePermission((String) null)).isInstanceOf(IllegalArgumentException.class);
		for (String malformed : List.of("", " ", ":", ",")) {
			assertThatThrownBy(() -> new ResourcePermission(malformed)).isInstanceOf(IllegalArgumentException.class);
		}
		assertThatThrownBy(() -> new ResourcePermission("unknown", "READ")).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> new ResourcePermission("DATA", "unknown")).isInstanceOf(IllegalArgumentException.class);
	}

	private static ResourcePermission roundTrip(ResourcePermission original) throws Exception {
		var bytes = new ByteArrayOutputStream();
		try (var output = new ObjectOutputStream(bytes)) {
			output.writeObject(original);
		}
		try (var input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
			return (ResourcePermission) input.readObject();
		}
	}

	private static final class InspectablePermission extends ResourcePermission {
		private static final long serialVersionUID = 1L;

		private InspectablePermission(String wildcard) { super(wildcard); }
		private List<Set<String>> parts() { return getParts(); }
		private void replace(String wildcard) { setParts(wildcard); }
		private void replace(String wildcard, boolean caseSensitive) { setParts(wildcard, caseSensitive); }
		private void replace(List<Set<String>> parts) { setParts(parts); }
	}
}
