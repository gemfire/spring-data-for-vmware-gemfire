/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import com.vmware.gemfire.driver.spi.*;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.WritablePdxInstance;
import java.lang.reflect.Array;
import java.util.*;

/** Per-operation conversion of PDX handles and their standard container carriers. No registry. */
final class GemFirePdxValues {
	Object fromNative(Object value) { return convert(value, false, new IdentityHashMap<>()); }
	Object toNative(Object value) { return convert(value, true, new IdentityHashMap<>()); }

	private boolean needsConversion(Object value, boolean toNative, IdentityHashMap<Object, Boolean> seen) {
		if (value == null || seen.put(value, Boolean.TRUE) != null) return false;
		if (toNative && value instanceof DriverPdxInstance) return true;
		if (!toNative && (value instanceof PdxInstance || value.getClass().getName().startsWith("org.apache.geode."))) return true;
		if (value instanceof Object[] array) {
			if (componentType(value.getClass().getComponentType(), toNative) != value.getClass().getComponentType()) return true;
			for (Object item : array) if (needsConversion(item, toNative, seen)) return true;
		}
		if (value instanceof Collection<?> items) for (Object item : items) if (needsConversion(item, toNative, seen)) return true;
		if (value instanceof Map<?, ?> map) for (var entry : map.entrySet())
			if (needsConversion(entry.getKey(), toNative, seen) || needsConversion(entry.getValue(), toNative, seen)) return true;
		return false;
	}

	private Class<?> componentType(Class<?> type, boolean toNative) {
		if (type.isArray()) return Array.newInstance(componentType(type.getComponentType(), toNative), 0).getClass();
		if (!toNative && PdxInstance.class.isAssignableFrom(type)) return DriverPdxInstance.class;
		if (toNative && DriverPdxInstance.class.isAssignableFrom(type)) return PdxInstance.class;
		return type;
	}
	@SuppressWarnings("unchecked")
	private Object convert(Object value, boolean toNative, IdentityHashMap<Object, Object> seen) {
		if (value == null) return null;
		if (seen.containsKey(value)) return seen.get(value);
		if (toNative && value instanceof GemFirePdxInstance pdx) return pdx.value;
		if (toNative && value instanceof DriverPdxInstance) throw UnsupportedFeatureException.notIntroduced("Pdx.foreignProviderValue");
		if (!toNative && value instanceof PdxInstance pdx) {
			Object result = pdx instanceof WritablePdxInstance writable ? new GemFireWritablePdxInstance(writable) : new GemFirePdxInstance(pdx);
			seen.put(value, result);
			return result;
		}
		if (!toNative && value.getClass().getName().startsWith("org.apache.geode.")) {
			throw UnsupportedFeatureException.notIntroduced("Pdx.nativeObjectValue");
		}
		if (!needsConversion(value, toNative, new IdentityHashMap<>())) return value;
		if ((value instanceof Collection<?> || value instanceof Map<?, ?>) && !value.getClass().getName().startsWith("java.util."))
			throw UnsupportedFeatureException.notIntroduced("Pdx.customContainerConversion");
		if (value instanceof Object[] array) {
			Class<?> component = componentType(value.getClass().getComponentType(), toNative);
			Object[] result = (Object[]) Array.newInstance(component, array.length);
			seen.put(value, result);
			for (int i = 0; i < array.length; i++) result[i] = convert(array[i], toNative, seen);
			return result;
		}
		if (value instanceof List<?> list) {
			List<Object> result = value instanceof LinkedList<?> ? new LinkedList<>() : new ArrayList<>(list.size());
			seen.put(value, result);
			for (Object element : list) result.add(convert(element, toNative, seen));
			return result;
		}
		if (value instanceof Map<?, ?> map) {
			Map<Object, Object> result;
			if (map instanceof SortedMap<?, ?> sorted) result = new TreeMap<>((Comparator<Object>) sorted.comparator());
			else if (map instanceof IdentityHashMap<?, ?>) result = new IdentityHashMap<>();
			else if (value.getClass() == HashMap.class) result = new HashMap<>();
			else if (value.getClass() == Hashtable.class) result = new Hashtable<>();
			else if (value.getClass() == java.util.concurrent.ConcurrentHashMap.class) result = new java.util.concurrent.ConcurrentHashMap<>();
			else result = new LinkedHashMap<>();
			seen.put(value, result);
			map.forEach((key, element) -> result.put(convert(key, toNative, seen), convert(element, toNative, seen)));
			return result;
		}
		if (value instanceof Set<?> set) {
			Set<Object> result = set instanceof SortedSet<?> sorted ? new TreeSet<>((Comparator<Object>) sorted.comparator()) : value.getClass() == HashSet.class ? new HashSet<>() : new LinkedHashSet<>();
			seen.put(value, result);
			for (Object element : set) result.add(convert(element, toNative, seen));
			return result;
		}
		return value;
	}
}
