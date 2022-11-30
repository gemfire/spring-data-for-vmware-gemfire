// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.util.ErrorHandler;

/**
 * Spring {@link ErrorHandler} for testing.
 *
 * @author Costin Leau
 * @author John Blum
 * @see org.springframework.util.ErrorHandler
 */
public class StubErrorHandler implements ErrorHandler, Iterable<Throwable> {

	public final List<Throwable> throwables = new ArrayList<>();

	@Override
	public void handleError(Throwable throwable) {
		this.throwables.add(throwable);
	}

	@Override
	public Iterator<Throwable> iterator() {
		return Collections.unmodifiableList(this.throwables).iterator();
	}
}
