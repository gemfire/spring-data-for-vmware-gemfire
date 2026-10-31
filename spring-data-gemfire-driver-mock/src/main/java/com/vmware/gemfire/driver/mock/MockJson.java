// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.mock;

import com.vmware.gemfire.driver.spi.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/** Local JSON text handling for the mock. Parsing yields a tree of {@link LinkedHashMap}, {@link ArrayList}, String, Boolean, null and numbers.
 * Integral numbers narrow to the smallest of byte, short, int, long and BigInteger; numbers with a fraction or exponent become double.
 * Parse failures carry a position but never the document text, so private content is not leaked through diagnostics.
 */
final class MockJson {
	static final String PARSE_FAILURE = "JSON conversion failed";

	private final String text;
	private int pos;

	private MockJson(String text) { this.text = text; }

	/** Parses a JSON object; any other top-level value is rejected. */
	static Map<String, Object> parseObject(String text) {
		if (text == null) throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "JSON is required", null);
		var parser = new MockJson(text);
		try {
			parser.skipWhitespace();
			if (parser.peek() != '{') throw parser.error("top-level value must be an object");
			@SuppressWarnings("unchecked") Map<String, Object> result = (Map<String, Object>) parser.value();
			parser.skipWhitespace();
			if (parser.pos < text.length()) throw parser.error("unexpected trailing content");
			return result;
		}
		catch (IllegalArgumentException ex) { throw new DriverJsonParseException(PARSE_FAILURE, ex); }
	}

	private IllegalArgumentException error(String reason) { return new IllegalArgumentException(reason + " at position " + pos); }
	private char peek() { return pos < text.length() ? text.charAt(pos) : '\0'; }
	private void skipWhitespace() { while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) pos++; }
	private void expect(char c) { if (peek() != c || pos >= text.length()) throw error("expected '" + c + "'"); pos++; }

	private Object value() {
		skipWhitespace();
		if (pos >= text.length()) throw error("unexpected end of input");
		char c = text.charAt(pos);
		if (c == '{') return object();
		if (c == '[') return array();
		if (c == '"') return string();
		if (text.startsWith("true", pos)) { pos += 4; return Boolean.TRUE; }
		if (text.startsWith("false", pos)) { pos += 5; return Boolean.FALSE; }
		if (text.startsWith("null", pos)) { pos += 4; return null; }
		if (c == '-' || (c >= '0' && c <= '9')) return number();
		throw error("unexpected character");
	}

	private Map<String, Object> object() {
		var result = new LinkedHashMap<String, Object>();
		expect('{');
		skipWhitespace();
		if (peek() == '}') { pos++; return result; }
		while (true) {
			skipWhitespace();
			if (peek() != '"') throw error("expected a field name");
			String name = string();
			skipWhitespace();
			expect(':');
			result.put(name, value());
			skipWhitespace();
			if (peek() == ',' && pos < text.length()) { pos++; continue; }
			expect('}');
			return result;
		}
	}

	private List<Object> array() {
		var result = new ArrayList<Object>();
		expect('[');
		skipWhitespace();
		if (peek() == ']') { pos++; return result; }
		while (true) {
			result.add(value());
			skipWhitespace();
			if (peek() == ',' && pos < text.length()) { pos++; continue; }
			expect(']');
			return result;
		}
	}

	private String string() {
		var out = new StringBuilder();
		expect('"');
		while (true) {
			if (pos >= text.length()) throw error("unterminated string");
			char c = text.charAt(pos++);
			if (c == '"') return out.toString();
			if (c < 0x20) throw error("control character in string");
			if (c != '\\') { out.append(c); continue; }
			if (pos >= text.length()) throw error("unterminated escape");
			char escaped = text.charAt(pos++);
			switch (escaped) {
				case '"', '\\', '/' -> out.append(escaped);
				case 'b' -> out.append('\b');
				case 'f' -> out.append('\f');
				case 'n' -> out.append('\n');
				case 'r' -> out.append('\r');
				case 't' -> out.append('\t');
				case 'u' -> {
					if (pos + 4 > text.length()) throw error("incomplete unicode escape");
					try { out.append((char) Integer.parseInt(text.substring(pos, pos + 4), 16)); }
					catch (NumberFormatException ex) { throw error("invalid unicode escape"); }
					pos += 4;
				}
				default -> throw error("invalid escape");
			}
		}
	}

	private Number number() {
		int start = pos;
		if (peek() == '-') pos++;
		boolean integral = true;
		while (pos < text.length()) {
			char c = text.charAt(pos);
			if (c >= '0' && c <= '9') pos++;
			else if (c == '.' || c == 'e' || c == 'E' || c == '+' || c == '-') { integral = false; pos++; }
			else break;
		}
		String token = text.substring(start, pos);
		try {
			if (!integral) {
				double parsed = Double.parseDouble(token);
				if (Double.isNaN(parsed) || Double.isInfinite(parsed)) throw error("number out of range");
				return parsed;
			}
			BigInteger big = new BigInteger(token);
			if (big.bitLength() < 8) return big.byteValue();
			if (big.bitLength() < 16) return big.shortValue();
			if (big.bitLength() < 32) return big.intValue();
			if (big.bitLength() < 64) return big.longValue();
			return big;
		}
		catch (NumberFormatException ex) { throw error("invalid number"); }
	}

	/** Converts a PDX instance (nested instances and lists) to the generic tree used for serialization. */
	static Object toTree(Object value) {
		if (value instanceof DriverPdxInstance pdx) {
			var result = new LinkedHashMap<String, Object>();
			for (String name : pdx.getFieldNames()) result.put(name, toTree(pdx.getField(name)));
			return result;
		}
		if (value instanceof DriverDocument document && !(value instanceof DriverPdxInstance)) {
			var result = new LinkedHashMap<String, Object>();
			for (String name : document.getFieldNames()) result.put(name, toTree(document.getField(name)));
			return result;
		}
		if (value instanceof Collection<?> items) { var result = new ArrayList<Object>(); for (Object item : items) result.add(toTree(item)); return result; }
		return value;
	}

	static String write(Object tree) {
		var out = new StringBuilder();
		write(tree, out);
		return out.toString();
	}

	private static void write(Object value, StringBuilder out) {
		if (value == null) out.append("null");
		else if (value instanceof String s) quote(s, out);
		else if (value instanceof Boolean || value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long || value instanceof BigInteger || value instanceof BigDecimal) out.append(value);
		else if (value instanceof Double d) number(d, out);
		else if (value instanceof Float f) number(f.doubleValue(), out);
		else if (value instanceof Map<?, ?> map) {
			out.append('{');
			boolean first = true;
			for (var entry : map.entrySet()) {
				if (!first) out.append(',');
				first = false;
				quote(String.valueOf(entry.getKey()), out);
				out.append(':');
				write(entry.getValue(), out);
			}
			out.append('}');
		}
		else if (value instanceof List<?> list) {
			out.append('[');
			for (int i = 0; i < list.size(); i++) { if (i > 0) out.append(','); write(list.get(i), out); }
			out.append(']');
		}
		else throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, PARSE_FAILURE, new IllegalArgumentException("Unsupported JSON value type " + value.getClass().getName()));
	}

	private static void number(double value, StringBuilder out) {
		if (Double.isNaN(value) || Double.isInfinite(value))
			throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, PARSE_FAILURE, new IllegalArgumentException("Non-finite number"));
		out.append(value);
	}

	private static void quote(String value, StringBuilder out) {
		out.append('"');
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			switch (c) {
				case '"' -> out.append("\\\"");
				case '\\' -> out.append("\\\\");
				case '\b' -> out.append("\\b");
				case '\f' -> out.append("\\f");
				case '\n' -> out.append("\\n");
				case '\r' -> out.append("\\r");
				case '\t' -> out.append("\\t");
				default -> { if (c < 0x20) out.append(String.format("\\u%04x", (int) c)); else out.append(c); }
			}
		}
		out.append('"');
	}
}
