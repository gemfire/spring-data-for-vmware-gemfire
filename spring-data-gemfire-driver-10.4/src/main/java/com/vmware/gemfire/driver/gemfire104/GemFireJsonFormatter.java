// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import java.util.function.Supplier;
import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.JSONFormatterException;
import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.DriverJsonFormatter;
import com.vmware.gemfire.driver.spi.DriverJsonParseException;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.DriverPdxInstance;
import com.vmware.gemfire.driver.spi.DriverRegionService;

/** Delegates every conversion to the native formatter bound to the injected service, so an authenticated service converts in its own context. */
@SuppressWarnings("deprecation")
final class GemFireJsonFormatter implements DriverJsonFormatter {
	static final String CONVERSION_FAILURE = "JSON conversion failed";
	private final JSONFormatter formatter;
	private final DriverRegionService service;
	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();
	GemFireJsonFormatter(JSONFormatter formatter, DriverRegionService service) { this.formatter = formatter; this.service = service; }

	@Override public DriverPdxInstance fromJSON(String json) { return toPdxInstance(json); }
	@Override public DriverPdxInstance fromJSON(String json, String... identityFields) { return toPdxInstance(json, identityFields); }
	@Override public DriverPdxInstance fromJSON(byte[] json) { return toPdxInstance(json); }
	@Override public DriverPdxInstance fromJSON(byte[] json, String... identityFields) { return toPdxInstance(json, identityFields); }
	@Override public DriverPdxInstance toPdxInstance(Object json, String... identityFields) {
		if (!(json instanceof String) && !(json instanceof byte[])) {
			throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "JSON must be a String or byte[]", null);
		}
		return convert(() -> new GemFirePdxInstance(formatter.toPdxInstance(json, identityFields)));
	}
	@Override public String fromPdxInstance(DriverPdxInstance instance) { return convert(() -> formatter.fromPdxInstance(nativeOf(instance))); }
	@Override public String toJSON(DriverPdxInstance instance) { return convert(() -> JSONFormatter.toJSON(nativeOf(instance))); }
	@Override public byte[] toJSONByteArray(DriverPdxInstance instance) { return convert(() -> JSONFormatter.toJSONByteArray(nativeOf(instance))); }
	@Override public byte[] toJsonByteArrayFromPdxInstance(DriverPdxInstance instance) {
		return convert(() -> formatter.toJsonByteArrayFromPdxInstance(nativeOf(instance)));
	}
	@Override public DriverRegionService getRegionService() { return service; }

	private static org.apache.geode.pdx.PdxInstance nativeOf(DriverPdxInstance instance) {
		return (org.apache.geode.pdx.PdxInstance) new GemFirePdxValues().toNative(instance);
	}
	/** The native parse failure becomes a safe diagnostic; its cause stays attached and its message is never copied. */
	private <T> T convert(Supplier<T> action) {
		try { return action.get(); }
		catch (JSONFormatterException ex) { throw new DriverJsonParseException(CONVERSION_FAILURE, ex); }
		catch (RuntimeException ex) { throw exceptions.translate("jsonFormatter", ex); }
	}
}
