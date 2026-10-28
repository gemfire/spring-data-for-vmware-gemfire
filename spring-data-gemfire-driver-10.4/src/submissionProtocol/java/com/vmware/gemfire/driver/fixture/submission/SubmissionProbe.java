/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.fixture.submission;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/** JDK-only experimental application body, not a proposed public SPI or complete permission model. */
public final class SubmissionProbe implements Serializable {

	private static final long serialVersionUID = 1L;
	private final long clientProcessId = ProcessHandle.current().pid();
	private String prefix = "first:";
	private int permissionCalls;
	private int bodyCalls;
	private String permissionRegion;
	private Object permissionArguments;

	public String id() { return SubmissionProbe.class.getName(); }
	public boolean hasResult() { return true; }
	public boolean highAvailability() { return false; }
	public boolean optimizeForWrite() { return false; }
	public int permissionCalls() { return permissionCalls; }
	public int bodyCalls() { return bodyCalls; }
	public void prefix(String prefix) { this.prefix = prefix; }

	public List<Permission> permissions(String region, Object arguments) {
		requireServerProcess();
		permissionCalls++;
		permissionRegion = region;
		permissionArguments = arguments;
		return List.of(new Permission("DATA", "READ", region, "*"));
	}

	public void execute(Context context, Sender sender) {
		requireServerProcess();
		bodyCalls++;
		sender.sendResult(prefix + context.arguments());
		sender.lastResult(List.of("permissions=" + permissionCalls, "bodies=" + bodyCalls,
			"permissionRegion=" + permissionRegion, "permissionArguments=" + permissionArguments,
			"function=" + context.functionId(), "region=" + context.regionName(),
			"filter=" + context.filter(), "duplicate=" + context.possibleDuplicate()));
	}

	private void requireServerProcess() {
		if (ProcessHandle.current().pid() == clientProcessId) {
			throw new IllegalStateException("Submission callbacks must execute in the server process");
		}
	}

	public record Permission(String resource, String operation, String target, String key) {}
	public record Context(Object arguments, String functionId, String regionName,
			Set<?> filter, boolean possibleDuplicate) {}
	public interface Sender {
		void sendResult(Object result);
		void lastResult(Object result);
	}
}
