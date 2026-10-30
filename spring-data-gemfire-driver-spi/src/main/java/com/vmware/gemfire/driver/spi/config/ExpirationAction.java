// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi.config;

/** Action taken when an entry or region expires. Constants are declared in the native ordinal order, so
 * {@link #ordinal()} and {@link #fromOrdinal(int)} agree with the native values.
 */
public enum ExpirationAction {
	INVALIDATE("invalidate"), LOCAL_INVALIDATE("local-invalidate"), DESTROY("destroy"), LOCAL_DESTROY("local-destroy");
	private final String xmlString;
	ExpirationAction(String xmlString) { this.xmlString = xmlString; }
	public String toXmlString() { return xmlString; }
	/** Fails with {@link IllegalArgumentException} for an unknown or null string. */
	public static ExpirationAction fromXmlString(String xmlValue) {
		for (ExpirationAction action : values()) if (action.xmlString.equals(xmlValue)) return action;
		throw new IllegalArgumentException("invalid expiration action: " + xmlValue);
	}
	/** Fails with {@link ArrayIndexOutOfBoundsException} for an out-of-range ordinal, as the native table lookup does. */
	public static ExpirationAction fromOrdinal(int ordinal) { return values()[ordinal]; }
	public boolean isInvalidate() { return this == INVALIDATE; }
	public boolean isLocalInvalidate() { return this == LOCAL_INVALIDATE; }
	public boolean isDestroy() { return this == DESTROY; }
	public boolean isLocalDestroy() { return this == LOCAL_DESTROY; }
	public boolean isLocal() { return this == LOCAL_INVALIDATE || this == LOCAL_DESTROY; }
	public boolean isDistributed() { return !isLocal(); }
}
