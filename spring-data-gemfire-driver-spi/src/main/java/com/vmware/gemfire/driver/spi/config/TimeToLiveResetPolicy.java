// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi.config;

import java.util.Locale;

/** Whether an entry time-to-live restarts on modification. */
public enum TimeToLiveResetPolicy {
	LAST_MODIFIED("last-modified"), NEVER("never");
	private final String xmlString;
	TimeToLiveResetPolicy(String xmlString) { this.xmlString = xmlString; }
	public String toXmlString() { return xmlString; }
	/** Null maps to {@link #LAST_MODIFIED}; the match is case-insensitive and anything else fails. */
	public static TimeToLiveResetPolicy fromXmlString(String xmlValue) {
		if (xmlValue == null) return LAST_MODIFIED;
		return switch (xmlValue.toLowerCase(Locale.ROOT)) {
			case "last-modified" -> LAST_MODIFIED;
			case "never" -> NEVER;
			default -> throw new IllegalArgumentException("invalid reset policy: " + xmlValue);
		};
	}
}
