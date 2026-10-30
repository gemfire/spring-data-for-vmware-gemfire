// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi.config;

/** Expiration timeout in seconds plus action. Negative timeouts clamp to zero and a null action becomes INVALIDATE.
 * A zero timeout means no expiration. Binary encoding is owned by the codec work, not this value.
 */
public final class ExpirationAttributes {
	public static final ExpirationAttributes DEFAULT = new ExpirationAttributes();
	private final int timeout;
	private final ExpirationAction action;
	public ExpirationAttributes() { this(0, ExpirationAction.INVALIDATE); }
	public ExpirationAttributes(int expirationTime) { this(expirationTime, ExpirationAction.INVALIDATE); }
	public ExpirationAttributes(int expirationTime, ExpirationAction expirationAction) {
		this.timeout = Math.max(expirationTime, 0);
		this.action = expirationAction == null ? ExpirationAction.INVALIDATE : expirationAction;
	}
	public int getTimeout() { return timeout; }
	public ExpirationAction getAction() { return action; }
	public boolean isDefault() { return action == ExpirationAction.INVALIDATE && timeout == 0; }
	@Override public boolean equals(Object other) {
		return other instanceof ExpirationAttributes that && timeout == that.timeout && action == that.action;
	}
	@Override public int hashCode() { return timeout ^ action.hashCode(); }
	@Override public String toString() { return timeout == 0 ? "NO EXPIRATION" : "timeout: " + timeout + ";action: " + action; }
}
