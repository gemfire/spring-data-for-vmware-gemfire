/*
 * Copyright (c) VMware, Inc. 2023-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Abstract utility class used to process IO operations.
 *
 * @author John Blum
 * @see Closeable
 * @see Serializable
 * @since 0.0.1
 */
@SuppressWarnings("unused")
public abstract class IOUtils {

	protected static final Logger log = Logger.getLogger(IOUtils.class.getName());

	public static boolean close(@Nullable Closeable closeable) {

		if (closeable != null) {
			try {
				closeable.close();
				return true;
			}
			catch (IOException cause) {

				if (log.isLoggable(Level.FINE)) {
					log.fine(String.format("Failed to close the Closeable object (%1$s) due to an I/O error:%n%2$s",
						closeable, ThrowableUtils.toString(cause)));
				}
			}
		}

		return false;
	}

	/**
	 * Executes the given {@link IoExceptionThrowingOperation}, handling any {@link IOException IOExceptions} thrown
	 * during normal IO processing.
	 *
	 * @param operation {@link IoExceptionThrowingOperation} to execute.
	 * @return a boolean indicating whether the IO operation was successful, or {@literal false} if the IO operation
	 * threw an {@link IOException}.
	 * @see IOException
	 */
	public static boolean doSafeIo(@NonNull IoExceptionThrowingOperation operation) {

		try {
			operation.doIo();
			return true;
		}
		catch (IOException cause) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T deserializeObject(byte[] objectBytes) throws IOException, ClassNotFoundException {

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(objectBytes);

		ObjectInputStream objectInputStream = null;

		try {
			objectInputStream = new ObjectInputStream(byteArrayInputStream);
			return (T) objectInputStream.readObject();
		}
		finally {
			IOUtils.close(objectInputStream);
		}
	}

	public static byte[] serializeObject(Serializable obj) throws IOException {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		ObjectOutputStream objectOutputStream = null;

		try {
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(obj);
			objectOutputStream.flush();
			return byteArrayOutputStream.toByteArray();
		}
		finally {
			IOUtils.close(objectOutputStream);
		}
	}

	public interface IoExceptionThrowingOperation {
		void doIo() throws IOException;
	}
}
