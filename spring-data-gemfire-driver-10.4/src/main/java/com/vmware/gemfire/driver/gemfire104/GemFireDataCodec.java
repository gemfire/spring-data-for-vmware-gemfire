// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import com.vmware.gemfire.driver.spi.DriverDataCodec;
import java.io.*;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.geode.DataSerializer;

/** Delegates every helper to the native {@link DataSerializer}, so the bytes are the native wire format. Region references resolve against the bound cache. */
final class GemFireDataCodec implements DriverDataCodec {
	private final org.apache.geode.cache.GemFireCache cache;
	private final GemFireExceptionMapper exceptions;
	private final GemFireExpirationConversions conversions = new GemFireExpirationConversions();

	GemFireDataCodec(org.apache.geode.cache.GemFireCache cache, GemFireExceptionMapper exceptions) {
		this.cache = cache;
		this.exceptions = exceptions;
	}

	@Override public void writeRegion(com.vmware.gemfire.driver.spi.DriverRegion<?, ?> region, DataOutput out) throws IOException {
		if (region != null && !(region instanceof GemFireRegion<?, ?>))
			throw new com.vmware.gemfire.driver.spi.DriverOperationException(com.vmware.gemfire.driver.spi.DriverFailureKind.INVALID_ARGUMENT, "Region does not belong to this provider", null);
		DataSerializer.writeRegion(region == null ? null : ((GemFireRegion<?, ?>) region).region, out);
	}

	@Override @SuppressWarnings("unchecked")
	public <K, V> com.vmware.gemfire.driver.spi.DriverRegion<K, V> readRegion(DataInput in) throws IOException, ClassNotFoundException {
		String path = DataSerializer.readString(in);
		if (path == null) return null;
		try {
			org.apache.geode.cache.Region<K, V> region = cache.getRegion(path);
			if (region == null) throw new org.apache.geode.cache.RegionDestroyedException("Region not found", path);
			return new GemFireRegion<>(region);
		}
		catch (RuntimeException failure) { throw exceptions.translate("readRegion", failure); }
	}

	@Override public void writeExpirationAttributes(com.vmware.gemfire.driver.spi.config.ExpirationAttributes value, DataOutput out) throws IOException {
		conversions.toNative(value).toData(out);
	}

	@Override public com.vmware.gemfire.driver.spi.config.ExpirationAttributes readExpirationAttributes(DataInput in) throws IOException, ClassNotFoundException {
		return conversions.fromNative(org.apache.geode.cache.ExpirationAttributes.createFromData(in));
	}

	@Override public void writeClass(Class<?> type, DataOutput out) throws IOException { DataSerializer.writeClass(type, out); }
	@Override public void writeNonPrimitiveClassName(String value, DataOutput out) throws IOException { DataSerializer.writeNonPrimitiveClassName(value, out); }
	@Override public Class<?> readClass(DataInput in) throws IOException, ClassNotFoundException { return DataSerializer.readClass(in); }
	@Override public String readNonPrimitiveClassName(DataInput in) throws IOException { return DataSerializer.readNonPrimitiveClassName(in); }
	@Override public void writeDate(Date value, DataOutput out) throws IOException { DataSerializer.writeDate(value, out); }
	@Override public Date readDate(DataInput in) throws IOException { return DataSerializer.readDate(in); }
	@Override public void writeFile(File value, DataOutput out) throws IOException { DataSerializer.writeFile(value, out); }
	@Override public File readFile(DataInput in) throws IOException { return DataSerializer.readFile(in); }
	@Override public void writeInetAddress(InetAddress value, DataOutput out) throws IOException { DataSerializer.writeInetAddress(value, out); }
	@Override public InetAddress readInetAddress(DataInput in) throws IOException { return DataSerializer.readInetAddress(in); }
	@Override public void writeString(String value, DataOutput out) throws IOException { DataSerializer.writeString(value, out); }
	@Override public String readString(DataInput in) throws IOException { return DataSerializer.readString(in); }
	@Override public void writeBoolean(Boolean value, DataOutput out) throws IOException { DataSerializer.writeBoolean(value, out); }
	@Override public Boolean readBoolean(DataInput in) throws IOException { return DataSerializer.readBoolean(in); }
	@Override public void writeCharacter(Character value, DataOutput out) throws IOException { DataSerializer.writeCharacter(value, out); }
	@Override public Character readCharacter(DataInput in) throws IOException { return DataSerializer.readCharacter(in); }
	@Override public void writeByte(Byte value, DataOutput out) throws IOException { DataSerializer.writeByte(value, out); }
	@Override public Byte readByte(DataInput in) throws IOException { return DataSerializer.readByte(in); }
	@Override public void writeShort(Short value, DataOutput out) throws IOException { DataSerializer.writeShort(value, out); }
	@Override public Short readShort(DataInput in) throws IOException { return DataSerializer.readShort(in); }
	@Override public void writeInteger(Integer value, DataOutput out) throws IOException { DataSerializer.writeInteger(value, out); }
	@Override public Integer readInteger(DataInput in) throws IOException { return DataSerializer.readInteger(in); }
	@Override public void writeLong(Long value, DataOutput out) throws IOException { DataSerializer.writeLong(value, out); }
	@Override public Long readLong(DataInput in) throws IOException { return DataSerializer.readLong(in); }
	@Override public void writeFloat(Float value, DataOutput out) throws IOException { DataSerializer.writeFloat(value, out); }
	@Override public Float readFloat(DataInput in) throws IOException { return DataSerializer.readFloat(in); }
	@Override public void writeDouble(Double value, DataOutput out) throws IOException { DataSerializer.writeDouble(value, out); }
	@Override public Double readDouble(DataInput in) throws IOException { return DataSerializer.readDouble(in); }
	@Override public void writePrimitiveBoolean(boolean value, DataOutput out) throws IOException { DataSerializer.writePrimitiveBoolean(value, out); }
	@Override public boolean readPrimitiveBoolean(DataInput in) throws IOException { return DataSerializer.readPrimitiveBoolean(in); }
	@Override public void writePrimitiveByte(byte value, DataOutput out) throws IOException { DataSerializer.writePrimitiveByte(value, out); }
	@Override public byte readPrimitiveByte(DataInput in) throws IOException { return DataSerializer.readPrimitiveByte(in); }
	@Override public void writePrimitiveChar(char value, DataOutput out) throws IOException { DataSerializer.writePrimitiveChar(value, out); }
	@Override public char readPrimitiveChar(DataInput in) throws IOException { return DataSerializer.readPrimitiveChar(in); }
	@Override public void writePrimitiveShort(short value, DataOutput out) throws IOException { DataSerializer.writePrimitiveShort(value, out); }
	@Override public short readPrimitiveShort(DataInput in) throws IOException { return DataSerializer.readPrimitiveShort(in); }
	@Override public void writeUnsignedByte(int value, DataOutput out) throws IOException { DataSerializer.writeUnsignedByte(value, out); }
	@Override public int readUnsignedByte(DataInput in) throws IOException { return DataSerializer.readUnsignedByte(in); }
	@Override public void writeUnsignedShort(int value, DataOutput out) throws IOException { DataSerializer.writeUnsignedShort(value, out); }
	@Override public int readUnsignedShort(DataInput in) throws IOException { return DataSerializer.readUnsignedShort(in); }
	@Override public void writePrimitiveInt(int value, DataOutput out) throws IOException { DataSerializer.writePrimitiveInt(value, out); }
	@Override public int readPrimitiveInt(DataInput in) throws IOException { return DataSerializer.readPrimitiveInt(in); }
	@Override public void writePrimitiveLong(long value, DataOutput out) throws IOException { DataSerializer.writePrimitiveLong(value, out); }
	@Override public long readPrimitiveLong(DataInput in) throws IOException { return DataSerializer.readPrimitiveLong(in); }
	@Override public void writePrimitiveFloat(float value, DataOutput out) throws IOException { DataSerializer.writePrimitiveFloat(value, out); }
	@Override public float readPrimitiveFloat(DataInput in) throws IOException { return DataSerializer.readPrimitiveFloat(in); }
	@Override public void writePrimitiveDouble(double value, DataOutput out) throws IOException { DataSerializer.writePrimitiveDouble(value, out); }
	@Override public double readPrimitiveDouble(DataInput in) throws IOException { return DataSerializer.readPrimitiveDouble(in); }
	@Override public void writeByteArray(byte[] value, DataOutput out) throws IOException { DataSerializer.writeByteArray(value, out); }
	@Override public void writeByteArray(byte[] value, int length, DataOutput out) throws IOException { DataSerializer.writeByteArray(value, length, out); }
	@Override public void writeObjectAsByteArray(Object value, DataOutput out) throws IOException { DataSerializer.writeObjectAsByteArray(value, out); }
	@Override public byte[] readByteArray(DataInput in) throws IOException { return DataSerializer.readByteArray(in); }
	@Override public void writeStringArray(String[] value, DataOutput out) throws IOException { DataSerializer.writeStringArray(value, out); }
	@Override public String[] readStringArray(DataInput in) throws IOException { return DataSerializer.readStringArray(in); }
	@Override public void writeShortArray(short[] value, DataOutput out) throws IOException { DataSerializer.writeShortArray(value, out); }
	@Override public short[] readShortArray(DataInput in) throws IOException { return DataSerializer.readShortArray(in); }
	@Override public void writeCharArray(char[] value, DataOutput out) throws IOException { DataSerializer.writeCharArray(value, out); }
	@Override public char[] readCharArray(DataInput in) throws IOException { return DataSerializer.readCharArray(in); }
	@Override public void writeBooleanArray(boolean[] value, DataOutput out) throws IOException { DataSerializer.writeBooleanArray(value, out); }
	@Override public boolean[] readBooleanArray(DataInput in) throws IOException { return DataSerializer.readBooleanArray(in); }
	@Override public void writeIntArray(int[] value, DataOutput out) throws IOException { DataSerializer.writeIntArray(value, out); }
	@Override public int[] readIntArray(DataInput in) throws IOException { return DataSerializer.readIntArray(in); }
	@Override public void writeLongArray(long[] value, DataOutput out) throws IOException { DataSerializer.writeLongArray(value, out); }
	@Override public long[] readLongArray(DataInput in) throws IOException { return DataSerializer.readLongArray(in); }
	@Override public void writeFloatArray(float[] value, DataOutput out) throws IOException { DataSerializer.writeFloatArray(value, out); }
	@Override public float[] readFloatArray(DataInput in) throws IOException { return DataSerializer.readFloatArray(in); }
	@Override public void writeDoubleArray(double[] value, DataOutput out) throws IOException { DataSerializer.writeDoubleArray(value, out); }
	@Override public double[] readDoubleArray(DataInput in) throws IOException { return DataSerializer.readDoubleArray(in); }
	@Override public void writeObjectArray(Object[] value, DataOutput out) throws IOException { DataSerializer.writeObjectArray(value, out); }
	@Override public Object[] readObjectArray(DataInput in) throws IOException, ClassNotFoundException { return DataSerializer.readObjectArray(in); }
	@Override public void writeArrayOfByteArrays(byte[][] value, DataOutput out) throws IOException { DataSerializer.writeArrayOfByteArrays(value, out); }
	@Override public byte[][] readArrayOfByteArrays(DataInput in) throws IOException { return DataSerializer.readArrayOfByteArrays(in); }
	@Override public void writeArrayList(ArrayList<?> value, DataOutput out) throws IOException { DataSerializer.writeArrayList(value, out); }
	@Override public <E> ArrayList<E> readArrayList(DataInput in) throws IOException, ClassNotFoundException { return DataSerializer.readArrayList(in); }
	@Override public void writeVector(Vector<?> value, DataOutput out) throws IOException { DataSerializer.writeVector(value, out); }
	@Override public <E> Vector<E> readVector(DataInput in) throws IOException, ClassNotFoundException { return DataSerializer.readVector(in); }
	@Override public void writeStack(Stack<?> value, DataOutput out) throws IOException { DataSerializer.writeStack(value, out); }
	@Override public <E> Stack<E> readStack(DataInput in) throws IOException, ClassNotFoundException { return DataSerializer.readStack(in); }
	@Override public void writeLinkedList(LinkedList<?> value, DataOutput out) throws IOException { DataSerializer.writeLinkedList(value, out); }
	@Override public <E> LinkedList<E> readLinkedList(DataInput in) throws IOException, ClassNotFoundException { return DataSerializer.readLinkedList(in); }
	@Override public void writeHashSet(HashSet<?> value, DataOutput out) throws IOException { DataSerializer.writeHashSet(value, out); }
	@Override public <E> HashSet<E> readHashSet(DataInput in) throws IOException, ClassNotFoundException { return DataSerializer.readHashSet(in); }
	@Override public void writeLinkedHashSet(LinkedHashSet<?> value, DataOutput out) throws IOException { DataSerializer.writeLinkedHashSet(value, out); }
	@Override public <E> LinkedHashSet<E> readLinkedHashSet(DataInput in) throws IOException, ClassNotFoundException { return DataSerializer.readLinkedHashSet(in); }
	@Override public void writeHashMap(Map<?, ?> value, DataOutput out) throws IOException { DataSerializer.writeHashMap(value, out); }
	@Override public <K, V> HashMap<K, V> readHashMap(DataInput in) throws IOException, ClassNotFoundException { return DataSerializer.readHashMap(in); }
	@Override public void writeIdentityHashMap(IdentityHashMap<?, ?> value, DataOutput out) throws IOException { DataSerializer.writeIdentityHashMap(value, out); }
	@Override public <K, V> IdentityHashMap<K, V> readIdentityHashMap(DataInput in) throws IOException, ClassNotFoundException { return DataSerializer.readIdentityHashMap(in); }
	@Override public void writeConcurrentHashMap(ConcurrentHashMap<?, ?> value, DataOutput out) throws IOException { DataSerializer.writeConcurrentHashMap(value, out); }
	@Override public <K, V> ConcurrentHashMap<K, V> readConcurrentHashMap(DataInput in) throws IOException, ClassNotFoundException { return DataSerializer.readConcurrentHashMap(in); }
	@Override public void writeHashtable(Hashtable<?, ?> value, DataOutput out) throws IOException { DataSerializer.writeHashtable(value, out); }
	@Override public <K, V> Hashtable<K, V> readHashtable(DataInput in) throws IOException, ClassNotFoundException { return DataSerializer.readHashtable(in); }
	@Override public void writeTreeMap(TreeMap<?, ?> value, DataOutput out) throws IOException { DataSerializer.writeTreeMap(value, out); }
	@Override public <K, V> TreeMap<K, V> readTreeMap(DataInput in) throws IOException, ClassNotFoundException { return DataSerializer.readTreeMap(in); }
	@Override public void writeLinkedHashMap(Map<?, ?> value, DataOutput out) throws IOException { DataSerializer.writeLinkedHashMap(value, out); }
	@Override public <K, V> LinkedHashMap<K, V> readLinkedHashMap(DataInput in) throws IOException, ClassNotFoundException { return DataSerializer.readLinkedHashMap(in); }
	@Override public void writeTreeSet(TreeSet<?> value, DataOutput out) throws IOException { DataSerializer.writeTreeSet(value, out); }
	@Override public <E> TreeSet<E> readTreeSet(DataInput in) throws IOException, ClassNotFoundException { return DataSerializer.readTreeSet(in); }
	@Override public void writeProperties(Properties value, DataOutput out) throws IOException { DataSerializer.writeProperties(value, out); }
	@Override public Properties readProperties(DataInput in) throws IOException, ClassNotFoundException { return DataSerializer.readProperties(in); }
	@Override public void writeObject(Object value, DataOutput out, boolean allowJavaSerialization) throws IOException { DataSerializer.writeObject(value, out, allowJavaSerialization); }
	@Override public void writeObject(Object value, DataOutput out) throws IOException { DataSerializer.writeObject(value, out); }
	@Override public <T> T readObject(DataInput in) throws IOException, ClassNotFoundException { return DataSerializer.readObject(in); }
	@Override public <T> T readObject(DataInput in, ObjectInputFilter filter) throws IOException, ClassNotFoundException { return DataSerializer.readObject(in, filter); }
	@Override public void writeEnum(Enum<?> value, DataOutput out) throws IOException { DataSerializer.writeEnum(value, out); }
	@Override public <E extends Enum<E>> E readEnum(Class<E> type, DataInput in) throws IOException { return DataSerializer.readEnum(type, in); }
	@Override public <T> void writeOptional(Optional<T> value, DataOutput out) throws IOException { DataSerializer.writeOptional(value, out); }
	@Override public <T> Optional<T> readOptional(DataInput in) throws IOException, ClassNotFoundException { return DataSerializer.readOptional(in); }
	@Override public void writeOptionalInt(OptionalInt value, DataOutput out) throws IOException { DataSerializer.writeOptionalInt(value, out); }
	@Override public OptionalInt readOptionalInt(DataInput in) throws IOException { return DataSerializer.readOptionalInt(in); }
	@Override public void writeOptionalLong(OptionalLong value, DataOutput out) throws IOException { DataSerializer.writeOptionalLong(value, out); }
	@Override public OptionalLong readOptionalLong(DataInput in) throws IOException { return DataSerializer.readOptionalLong(in); }
	@Override public void writeOptionalDouble(OptionalDouble value, DataOutput out) throws IOException { DataSerializer.writeOptionalDouble(value, out); }
	@Override public OptionalDouble readOptionalDouble(DataInput in) throws IOException { return DataSerializer.readOptionalDouble(in); }
}
