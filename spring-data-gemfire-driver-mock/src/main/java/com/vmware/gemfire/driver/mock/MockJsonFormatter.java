// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.mock;

import com.vmware.gemfire.driver.spi.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.BooleanSupplier;

/** Local JSON/PDX conversion bound to the service that issued it. The mock encodes and decodes UTF-8 in both directions, a deliberate
 * normalization of the native {@code fromPdxInstance} default-charset decoding. The sort-field-names property is not honored: fields keep document order.
 */
final class MockJsonFormatter implements DriverJsonFormatter {
	private final DriverRegionService service;
	private final BooleanSupplier closed;

	MockJsonFormatter(DriverRegionService service, BooleanSupplier closed) { this.service = service; this.closed = closed; }

	@Override public DriverPdxInstance fromJSON(String json) { return toPdxInstance(json); }
	@Override public DriverPdxInstance fromJSON(String json, String... identityFields) { return toPdxInstance(json, identityFields); }
	@Override public DriverPdxInstance fromJSON(byte[] json) { return toPdxInstance(json); }
	@Override public DriverPdxInstance fromJSON(byte[] json, String... identityFields) { return toPdxInstance(json, identityFields); }

	@Override public DriverPdxInstance toPdxInstance(Object json, String... identityFields) {
		checkOpen();
		String text;
		if (json instanceof String s) text = s;
		else if (json instanceof byte[] bytes) text = new String(bytes, StandardCharsets.UTF_8);
		else throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "JSON must be a String or byte[]", null);
		var pdx = pdx(MockJson.parseObject(text));
		if (identityFields == null || identityFields.length == 0) return pdx;
		// Identity marks are applied after the field writes, matching the native factory order.
		var factory = new MockPdxInstanceFactory(JSON_CLASSNAME);
		for (String name : pdx.getFieldNames()) factory.writeObject(name, pdx.getField(name));
		for (String name : identityFields) factory.markIdentityField(name);
		return factory.create();
	}

	private DriverPdxInstance pdx(Map<String, Object> tree) {
		var factory = new MockPdxInstanceFactory(JSON_CLASSNAME);
		tree.forEach((name, value) -> factory.writeObject(name, convert(value)));
		return factory.create();
	}

	@SuppressWarnings("unchecked")
	private Object convert(Object value) {
		if (value instanceof Map<?, ?> map) return pdx((Map<String, Object>) map);
		if (value instanceof java.util.List<?> list) { var result = new java.util.ArrayList<Object>(); for (Object item : list) result.add(convert(item)); return result; }
		return value;
	}

	@Override public String fromPdxInstance(DriverPdxInstance instance) { return new String(toJsonByteArrayFromPdxInstance(instance), StandardCharsets.UTF_8); }
	@Override public String toJSON(DriverPdxInstance instance) { return text(instance); }
	@Override public byte[] toJSONByteArray(DriverPdxInstance instance) { return text(instance).getBytes(StandardCharsets.UTF_8); }
	@Override public byte[] toJsonByteArrayFromPdxInstance(DriverPdxInstance instance) { return text(instance).getBytes(StandardCharsets.UTF_8); }
	@Override public DriverRegionService getRegionService() { return service; }

	private String text(DriverPdxInstance instance) {
		checkOpen();
		if (instance == null) throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "PDX instance is required", null);
		return MockJson.write(MockJson.toTree(instance));
	}

	private void checkOpen() {
		if (closed.getAsBoolean()) throw new DriverOperationException(DriverFailureKind.INVALID_STATE, "Cache is closed", null);
	}
}
