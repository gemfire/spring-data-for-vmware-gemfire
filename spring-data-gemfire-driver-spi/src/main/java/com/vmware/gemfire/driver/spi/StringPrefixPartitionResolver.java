// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

/** Routes a String key by the text before its first {@code |}; an empty prefix is valid. */
public class StringPrefixPartitionResolver implements DriverPartitionResolver<String, Object> {
	public static final String DEFAULT_DELIMITER = "|";

	public StringPrefixPartitionResolver() { }

	@Override public Object getRoutingObject(DriverEntryOperation<String, Object> operation) {
		String key = operation.getKey();
		String delimiter = getDelimiter();
		int index = key.indexOf(delimiter);
		if (index == -1) {
			throw new IllegalArgumentException("The key \"" + key + "\" does not contain the \"" + delimiter + "\" delimiter.");
		}
		return key.substring(0, index);
	}

	@Override public String getName() { return getClass().getName(); }

	protected String getDelimiter() { return DEFAULT_DELIMITER; }

	@Override public int hashCode() { return getName().hashCode(); }

	@Override public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof StringPrefixPartitionResolver that)) return false;
		return getName().equals(that.getName()) && getDelimiter().equals(that.getDelimiter());
	}

	@Override public void close() { }
}
