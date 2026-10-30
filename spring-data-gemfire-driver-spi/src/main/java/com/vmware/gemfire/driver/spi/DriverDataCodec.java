// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

import java.io.*;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import com.vmware.gemfire.driver.spi.config.ExpirationAttributes;

/** Typed read and write helpers for application binary serializers, mirroring the native helper contract over JDK streams.
 * Collection kind and order are preserved. Null handling follows the provider's native helper for each type; for example the native
 * {@code writeInteger} does not accept null. A provider writes its own wire format, so a provider that cannot promise
 * one rejects the whole codec with {@link UnsupportedFeatureException}. {@link #readObject(DataInput, ObjectInputFilter)} applies the filter
 * only to values that fall back to Java serialization. Region references and expiration attributes are written as the provider's native bytes; a region is read back only against the connection that issued the codec.
 */
public interface DriverDataCodec {
	void writeClass(Class<?> type, DataOutput out) throws IOException;
	void writeNonPrimitiveClassName(String value, DataOutput out) throws IOException;
	Class<?> readClass(DataInput in) throws IOException, ClassNotFoundException;
	String readNonPrimitiveClassName(DataInput in) throws IOException;
	void writeDate(Date value, DataOutput out) throws IOException;
	Date readDate(DataInput in) throws IOException;
	void writeFile(File value, DataOutput out) throws IOException;
	File readFile(DataInput in) throws IOException;
	void writeInetAddress(InetAddress value, DataOutput out) throws IOException;
	InetAddress readInetAddress(DataInput in) throws IOException;
	void writeString(String value, DataOutput out) throws IOException;
	String readString(DataInput in) throws IOException;
	void writeBoolean(Boolean value, DataOutput out) throws IOException;
	Boolean readBoolean(DataInput in) throws IOException;
	void writeCharacter(Character value, DataOutput out) throws IOException;
	Character readCharacter(DataInput in) throws IOException;
	void writeByte(Byte value, DataOutput out) throws IOException;
	Byte readByte(DataInput in) throws IOException;
	void writeShort(Short value, DataOutput out) throws IOException;
	Short readShort(DataInput in) throws IOException;
	void writeInteger(Integer value, DataOutput out) throws IOException;
	Integer readInteger(DataInput in) throws IOException;
	void writeLong(Long value, DataOutput out) throws IOException;
	Long readLong(DataInput in) throws IOException;
	void writeFloat(Float value, DataOutput out) throws IOException;
	Float readFloat(DataInput in) throws IOException;
	void writeDouble(Double value, DataOutput out) throws IOException;
	Double readDouble(DataInput in) throws IOException;
	void writePrimitiveBoolean(boolean value, DataOutput out) throws IOException;
	boolean readPrimitiveBoolean(DataInput in) throws IOException;
	void writePrimitiveByte(byte value, DataOutput out) throws IOException;
	byte readPrimitiveByte(DataInput in) throws IOException;
	void writePrimitiveChar(char value, DataOutput out) throws IOException;
	char readPrimitiveChar(DataInput in) throws IOException;
	void writePrimitiveShort(short value, DataOutput out) throws IOException;
	short readPrimitiveShort(DataInput in) throws IOException;
	void writeUnsignedByte(int value, DataOutput out) throws IOException;
	int readUnsignedByte(DataInput in) throws IOException;
	void writeUnsignedShort(int value, DataOutput out) throws IOException;
	int readUnsignedShort(DataInput in) throws IOException;
	void writePrimitiveInt(int value, DataOutput out) throws IOException;
	int readPrimitiveInt(DataInput in) throws IOException;
	void writePrimitiveLong(long value, DataOutput out) throws IOException;
	long readPrimitiveLong(DataInput in) throws IOException;
	void writePrimitiveFloat(float value, DataOutput out) throws IOException;
	float readPrimitiveFloat(DataInput in) throws IOException;
	void writePrimitiveDouble(double value, DataOutput out) throws IOException;
	double readPrimitiveDouble(DataInput in) throws IOException;
	void writeByteArray(byte[] value, DataOutput out) throws IOException;
	void writeByteArray(byte[] value, int length, DataOutput out) throws IOException;
	void writeObjectAsByteArray(Object value, DataOutput out) throws IOException;
	byte[] readByteArray(DataInput in) throws IOException;
	void writeStringArray(String[] value, DataOutput out) throws IOException;
	String[] readStringArray(DataInput in) throws IOException;
	void writeShortArray(short[] value, DataOutput out) throws IOException;
	short[] readShortArray(DataInput in) throws IOException;
	void writeCharArray(char[] value, DataOutput out) throws IOException;
	char[] readCharArray(DataInput in) throws IOException;
	void writeBooleanArray(boolean[] value, DataOutput out) throws IOException;
	boolean[] readBooleanArray(DataInput in) throws IOException;
	void writeIntArray(int[] value, DataOutput out) throws IOException;
	int[] readIntArray(DataInput in) throws IOException;
	void writeLongArray(long[] value, DataOutput out) throws IOException;
	long[] readLongArray(DataInput in) throws IOException;
	void writeFloatArray(float[] value, DataOutput out) throws IOException;
	float[] readFloatArray(DataInput in) throws IOException;
	void writeDoubleArray(double[] value, DataOutput out) throws IOException;
	double[] readDoubleArray(DataInput in) throws IOException;
	void writeObjectArray(Object[] value, DataOutput out) throws IOException;
	Object[] readObjectArray(DataInput in) throws IOException, ClassNotFoundException;
	void writeArrayOfByteArrays(byte[][] value, DataOutput out) throws IOException;
	byte[][] readArrayOfByteArrays(DataInput in) throws IOException;
	void writeArrayList(ArrayList<?> value, DataOutput out) throws IOException;
	<E> ArrayList<E> readArrayList(DataInput in) throws IOException, ClassNotFoundException;
	void writeVector(Vector<?> value, DataOutput out) throws IOException;
	<E> Vector<E> readVector(DataInput in) throws IOException, ClassNotFoundException;
	void writeStack(Stack<?> value, DataOutput out) throws IOException;
	<E> Stack<E> readStack(DataInput in) throws IOException, ClassNotFoundException;
	void writeLinkedList(LinkedList<?> value, DataOutput out) throws IOException;
	<E> LinkedList<E> readLinkedList(DataInput in) throws IOException, ClassNotFoundException;
	void writeHashSet(HashSet<?> value, DataOutput out) throws IOException;
	<E> HashSet<E> readHashSet(DataInput in) throws IOException, ClassNotFoundException;
	void writeLinkedHashSet(LinkedHashSet<?> value, DataOutput out) throws IOException;
	<E> LinkedHashSet<E> readLinkedHashSet(DataInput in) throws IOException, ClassNotFoundException;
	void writeHashMap(Map<?, ?> value, DataOutput out) throws IOException;
	<K, V> HashMap<K, V> readHashMap(DataInput in) throws IOException, ClassNotFoundException;
	void writeIdentityHashMap(IdentityHashMap<?, ?> value, DataOutput out) throws IOException;
	<K, V> IdentityHashMap<K, V> readIdentityHashMap(DataInput in) throws IOException, ClassNotFoundException;
	void writeConcurrentHashMap(ConcurrentHashMap<?, ?> value, DataOutput out) throws IOException;
	<K, V> ConcurrentHashMap<K, V> readConcurrentHashMap(DataInput in) throws IOException, ClassNotFoundException;
	void writeHashtable(Hashtable<?, ?> value, DataOutput out) throws IOException;
	<K, V> Hashtable<K, V> readHashtable(DataInput in) throws IOException, ClassNotFoundException;
	void writeTreeMap(TreeMap<?, ?> value, DataOutput out) throws IOException;
	<K, V> TreeMap<K, V> readTreeMap(DataInput in) throws IOException, ClassNotFoundException;
	void writeLinkedHashMap(Map<?, ?> value, DataOutput out) throws IOException;
	<K, V> LinkedHashMap<K, V> readLinkedHashMap(DataInput in) throws IOException, ClassNotFoundException;
	void writeTreeSet(TreeSet<?> value, DataOutput out) throws IOException;
	<E> TreeSet<E> readTreeSet(DataInput in) throws IOException, ClassNotFoundException;
	void writeProperties(Properties value, DataOutput out) throws IOException;
	Properties readProperties(DataInput in) throws IOException, ClassNotFoundException;
	void writeObject(Object value, DataOutput out, boolean allowJavaSerialization) throws IOException;
	void writeObject(Object value, DataOutput out) throws IOException;
	<T> T readObject(DataInput in) throws IOException, ClassNotFoundException;
	<T> T readObject(DataInput in, ObjectInputFilter filter) throws IOException, ClassNotFoundException;
	void writeEnum(Enum<?> value, DataOutput out) throws IOException;
	<E extends Enum<E>> E readEnum(Class<E> type, DataInput in) throws IOException;
	<T> void writeOptional(Optional<T> value, DataOutput out) throws IOException;
	<T> Optional<T> readOptional(DataInput in) throws IOException, ClassNotFoundException;
	void writeOptionalInt(OptionalInt value, DataOutput out) throws IOException;
	OptionalInt readOptionalInt(DataInput in) throws IOException;
	void writeOptionalLong(OptionalLong value, DataOutput out) throws IOException;
	OptionalLong readOptionalLong(DataInput in) throws IOException;
	void writeOptionalDouble(OptionalDouble value, DataOutput out) throws IOException;
	OptionalDouble readOptionalDouble(DataInput in) throws IOException;
	/** Writes a null marker, or the full path of a region of this connection's cache. A region from another provider is rejected. */
	void writeRegion(DriverRegion<?, ?> region, DataOutput out) throws IOException;
	/** Resolves a written region path against this connection's open cache. Returns null for the null marker; an unknown path fails as region-unavailable. */
	<K, V> DriverRegion<K, V> readRegion(DataInput in) throws IOException, ClassNotFoundException;
	/** Writes the timeout and action of the value, matching the native expiration attributes encoding. */
	void writeExpirationAttributes(ExpirationAttributes value, DataOutput out) throws IOException;
	/** Reads a timeout and action into a new value. Out-of-range timeouts are normalized by the value type. */
	ExpirationAttributes readExpirationAttributes(DataInput in) throws IOException, ClassNotFoundException;
}
