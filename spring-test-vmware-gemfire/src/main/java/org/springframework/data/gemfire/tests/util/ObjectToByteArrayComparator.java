/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.util;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalArgumentException;

import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;

import org.springframework.util.Assert;

/**
 * The ObjectToByteArrayComparator class...
 *
 * @author John Blum
 * @since 1.0.0
 */
public class ObjectToByteArrayComparator implements Comparator<Object> {

	public static final ObjectToByteArrayComparator INSTANCE = new ObjectToByteArrayComparator();

	@Override
	public int compare(Object objectOne, Object objectTwo) {

		return ByteArrayComparator.INSTANCE
			.compare(fromSerializableToByteArray(objectOne), fromSerializableToByteArray(objectTwo));
	}

	private Serializable assertSerializable(Object target) {

		Assert.isInstanceOf(Serializable.class, target);

		return (Serializable) target;
	}

	private byte[] fromSerializableToByteArray(Object target) {
		return toByteArray(assertSerializable(target));
	}

	private byte[] toByteArray(Object target) {

		try {
			return IOUtils.serializeObject(assertSerializable(target));
		}
		catch (IOException cause) {
			throw newIllegalArgumentException(cause, "Object [%s] could not be serialized", target);
		}
	}
}
