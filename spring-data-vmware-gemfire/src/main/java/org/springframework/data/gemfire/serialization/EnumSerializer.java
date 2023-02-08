/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.serialization;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.geode.DataSerializer;
import org.apache.geode.internal.InternalDataSerializer;

/**
 * Generic serializer for all Java Enums. The class needs to be registered only once.  Custom enums will then
 * be understood by the converter by calling {@link #addEnum(Class)}.
 *
 * @author Costin Leau
 * @author John Blum
 * @see java.io.Serializable
 * @see org.apache.geode.DataSerializer
 */
public class EnumSerializer extends DataSerializer implements Serializable {

	private static final long serialVersionUID = -7069461993489626976L;

	private static final ConcurrentMap<Class<?>, Enum[]> supportedEnumTypes = new ConcurrentHashMap<>();

	private int id = 1024;

	@Override
	public boolean toData(Object obj, DataOutput out) throws IOException {
		return (obj instanceof Enum && serialize((Enum<?>) obj, out));
	}

	/* (non-Javadoc) */
	private boolean serialize(Enum<?> enumeratedValue, DataOutput out) throws IOException {
		DataSerializer.writeClass(registerEnumType(enumeratedValue), out);
		out.writeInt(enumeratedValue.ordinal());
		return true;
	}

	/* (non-Javadoc) */
	private Class<?> registerEnumType(Enum<?> enumeratedValue) {
		return addEnum(enumeratedValue.getDeclaringClass());
	}

	@Override
	public Object fromData(DataInput in) throws IOException, ClassNotFoundException {
		Class<?> type = DataSerializer.readClass(in);

		return Optional.ofNullable(type).filter(Class::isEnum).map(enumType -> {
			int ordinal = safeReadInt(in);
			return supportedEnumTypes.get(addEnum(enumType))[ordinal];
		}).orElseThrow(() -> new IOException(String.format("Non-enum type [%s] read from the stream", type)));
	}

	/* (non-Javadoc) */
	private int safeReadInt(DataInput in) {
		try {
			return in.readInt();
		}
		catch (IOException e) {
			throw new IllegalStateException("Failed to read int from DataInput", e);
		}
	}

	public Class<?> addEnum(Class<?> enumType) {
		synchronized (supportedEnumTypes) {
			if (!supportedEnumTypes.containsKey(enumType)) {
				supportedEnumTypes.put(enumType, (Enum[]) enumType.getEnumConstants());
				potentiallyReRegisterThisSerializer();
			}
		}

		return enumType;
	}

	// TODO refactor the use of the Apache Geode internal class
	// if registered then re-register this serializer to propagate and distribute the changes
	private void potentiallyReRegisterThisSerializer() {
		if (InternalDataSerializer.getSerializer(getId()) != null) {
			InternalDataSerializer.unregister(getId());
			DataSerializer.register(getClass());
		}
	}

	/**
	 * Sets the id of this serializer.  Default is 1024.
	 *
	 * @param id identifier to set on this serializer.
	 */
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public Class<?>[] getSupportedClasses() {
		return supportedEnumTypes.keySet().toArray(new Class<?>[supportedEnumTypes.size()]);
	}
}
