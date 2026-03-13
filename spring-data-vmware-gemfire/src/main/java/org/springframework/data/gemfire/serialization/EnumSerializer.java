/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.serialization;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.data.gemfire.gud.api.GudDataSerializer;
import org.springframework.data.gemfire.gud.api.GudInternalDataSerializer;

/**
 * Generic serializer for all Java Enums. The class needs to be registered only once.  Custom enums will then
 * be understood by the converter by calling {@link #addEnum(Class)}.
 *
 * @author Costin Leau
 * @author John Blum
 * @see Serializable
 * @see GudDataSerializer
 */
public class EnumSerializer extends GudDataSerializer implements Serializable {

	private static final long serialVersionUID = -7069461993489626976L;

	private static final ConcurrentMap<Class<?>, Enum[]> supportedEnumTypes = new ConcurrentHashMap<>();

	private int id = 1024;

	@Override
	public boolean toData(Object obj, DataOutput out) throws IOException {
		return (obj instanceof Enum && serialize((Enum<?>) obj, out));
	}

	/* (non-Javadoc) */
	private boolean serialize(Enum<?> enumeratedValue, DataOutput out) throws IOException {
		GudDataSerializer.writeClass(registerEnumType(enumeratedValue), out);
		out.writeInt(enumeratedValue.ordinal());
		return true;
	}

	/* (non-Javadoc) */
	private Class<?> registerEnumType(Enum<?> enumeratedValue) {
		return addEnum(enumeratedValue.getDeclaringClass());
	}

	@Override
	public Object fromData(DataInput in) throws IOException, ClassNotFoundException {
		Class<?> type = GudDataSerializer.readClass(in);

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
		if (GudInternalDataSerializer.getSerializer(getId()) != null) {
			GudInternalDataSerializer.unregister(getId());
			GudDataSerializer.register(getClass());
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
